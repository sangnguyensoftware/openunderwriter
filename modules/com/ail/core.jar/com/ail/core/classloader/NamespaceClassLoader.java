package com.ail.core.classloader;
/* Copyright Applied Industrial Logic Limited 2007. All rights Reserved */
/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
import static com.liferay.portlet.documentlibrary.model.DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;
import static javax.tools.JavaFileObject.Kind.CLASS;
import static javax.tools.JavaFileObject.Kind.SOURCE;
import static javax.tools.StandardLocation.CLASS_OUTPUT;

import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import com.ail.core.CoreProxy;
import com.ail.core.PreconditionException;
import com.ail.core.command.JavaAccessor;
import com.ail.core.configure.Command;
import com.ail.core.configure.Group;
import com.ail.core.configure.Parameter;
import com.ail.core.configure.Service;
import com.ail.core.configure.Type;
import com.ail.core.product.ProductServiceCommand;
import com.liferay.portlet.documentlibrary.NoSuchFolderException;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

/**
 * Extends the standard class loader to compile from java source in memory into a memory class store.
 * Is intended to be used to only load the classes required for a product, as defined by namespace.
 * There can be a hierarchy of these classloaders as there can be a hierarchy of products.
 *
 * Will also dynamically create a map of configuration {@link Service} and {@link Command} Types
 * if the {@link ProductServiceCommand} annotation is present on the class.
 * The service entry name will be set to FQN and the command entry name will be set to
 * the value of commandName on the {@link ProductServiceCommand} annotation.
 */
public class NamespaceClassLoader extends ClassLoader {

    private static final String SERVICE = "Service";
    private static final String CACHING_CLASS_BUILDER = "CachingClassBuilder";
    private static final String EXECUTE_PAGE_ACTION_COMMAND_IMPL = "com.ail.pageflow.ExecutePageActionCommandImpl";

    private static final int REPOSITORY_ID = 10197;


    private final String namespace;
    private final CoreProxy coreProxy;
    private final Map<String, StringSourceFileObject> sources;
    private JavaCompiler compiler;
    private MemoryFileManager fileManager;
    private final Map<String, Type> types = new HashMap<>();

    /**
     * Creates and fully initialises a classloader for the required namespace. Searches through the Registry-configured sourcepaths for any viable
     * java sources and compiles and loads them.
     * @param parentClassLoader
     * @param namespace
     * @param compiler
     * @throws PreconditionException
     * @throws CompileException
     */
    public NamespaceClassLoader(ClassLoader parentClassLoader, String namespace, JavaCompiler compiler) throws PreconditionException, CompileException {
        super(parentClassLoader);
        this.namespace = namespace;
        this.coreProxy = new CoreProxy(namespace);
        this.sources = getSources();
        if (sources != null) {
            this.compiler = compiler;
            DiagnosticCollector<JavaFileObject> collector = new DiagnosticCollector<>();
            this.fileManager = new MemoryFileManager(this, getAppropriateFileManager(parentClassLoader, collector));
            this.compile(collector);
            this.addServiceCommandTypes();
        }
    }

    @Override
    public Class<?> loadClass(String name) throws ClassNotFoundException {
        try {
            return getParent().loadClass(name);
        } catch (ClassNotFoundException e) {
            return findClass(name);
        }
    }

    /**
     * Get a {@link Type} for a dynamically created Service or Command, either from here or from a parent classloader
     * @param name
     * @return
     */
    public Type getType(String name) {
        Type type = types.get(name);

        if (type == null && NamespaceClassLoader.class.isInstance(getParent())) {
            type = ((NamespaceClassLoader) getParent()).getType(name);
        }

        return type;
    }

    public String getNamespace() {
        return namespace;
    }

    private JavaFileManager getAppropriateFileManager(ClassLoader parentClassLoader, DiagnosticCollector<JavaFileObject> collector) {
        JavaFileManager parentFileManager = null;
        if (NamespaceClassLoader.class.isInstance(parentClassLoader)) {
            parentFileManager = ((NamespaceClassLoader) parentClassLoader).getFileManager();
        } else {
            parentFileManager = compiler.getStandardFileManager(collector, null, null);
        }
        return parentFileManager;
    }

    private void compile(DiagnosticCollector<JavaFileObject> collector) throws CompileException {
        if (fileManager == null) {
            throw new IllegalStateException("File manager not available");
        }
        if (sources == null) {
            throw new IllegalStateException("Sources not available");
        }

        if (!sources.isEmpty()) {
            String o = "-g";
            List<String> options = Arrays.asList(new String[]{o});

            // Run the compiler.
            long start = System.nanoTime();
            CompilationTask task = compiler.getTask(null, fileManager, collector, options, null, sources.values());
            boolean status = task.call();

            if (status) {
                coreProxy.logInfo("Compiled classes for " + namespace  + " in " + ((System.nanoTime() - start) / 1000000d) + "ms");
            } else {
                throw new CompileException(buildCompilationReport(collector));
            }
        }
    }

    public MemoryFileManager getFileManager() {
        return fileManager;
    }

    @Override
    protected Class<?> findClass(String className) throws ClassNotFoundException {
        assert className != null;

        String findClassName = className;

        Class<?> clazz = null;
        try {
            // Bytecode should be there already since compilation is done for all product sources in one go
            Type type = types.get(findClassName);
            if (type != null) {
                // Might be loaded under a different name, get that name
                findClassName = type.getName();
            }
            ClassFileObject classObject = (ClassFileObject) fileManager.getJavaFileForInput(CLASS_OUTPUT, findClassName, CLASS);
            if (classObject != null) {
                clazz = classObject.getClassInstance();
                if (clazz == null) {
                    byte[] ba = classObject.getFileBytes();
                    clazz = defineClass(findClassName, ba, 0, ba.length, null);
                    classObject.setClassInstance(clazz);
                }
            } else {
                // Is there a sourceFile?
                JavaFileObject sourceFileObject = sources.get(findClassName);
                if (sourceFileObject == null) {
                    throw new ClassNotFoundException("Source for '" + findClassName + "' not found");
                }

                throw new ClassNotFoundException("Bytecode for '" + findClassName + "' not found");
            }

        } catch (IOException ioe) {
            throw new ClassNotFoundException("CompilerFileObject for '" + findClassName + "' not found", ioe);
        }

        return clazz;
    }

    private String buildCompilationReport(DiagnosticCollector<JavaFileObject> collector) {
        StringBuilder report = new StringBuilder();

        List<Diagnostic<? extends JavaFileObject>> diagnostics = collector.getDiagnostics();

        report.append(diagnostics.size()).append(" compilation errors.%n%n");

        for (Diagnostic<?> diagnostic : diagnostics) {
            JavaFileObject source = (JavaFileObject) diagnostic.getSource();
            if (source != null) {
                report.append(source.getName()).append(" at line ").append(diagnostic.getLineNumber()).append(", column ")
                            .append(diagnostic.getColumnNumber()).append(": ").append(diagnostic.getMessage(null)).append("%n%n");
            } else {
                report.append(diagnostic.getMessage(null)).append("%n%n");
            }
        }

        return String.format(report.toString());
    }

    /**
     * Look through the namespace product location in the Liferay document store for java source files and add them to a collection.
     * @return
     * @throws PreconditionException
     */
    private Map<String, StringSourceFileObject> getSources() throws PreconditionException {
        Map<String, StringSourceFileObject> sources = new HashMap<>();

        try {
            long start = System.nanoTime();
            Parameter parameter = coreProxy.getParameter("SourceFolders.Java");
            if (parameter != null && parameter.getNamespace().equals(namespace)) {
                String[] sourcepaths = parameter.getValue().split(",");
                String productPath = "Product/" + namespace.replace("Registry", "").replace(".", "/");
                for (String sourcepath : sourcepaths) {
                    try {
                        String[] folderNames = (productPath + sourcepath).split("[\\/\\\\]");
                        DLFolder folder = null;
                        long parentFolderId = DEFAULT_PARENT_FOLDER_ID;
                        for (String folderName : folderNames) {
                            folder = DLFolderLocalServiceUtil.getFolder(REPOSITORY_ID, parentFolderId, folderName);
                            parentFolderId = folder.getFolderId();
                        }

                        addSources(sources, folder, null);
                    } catch (NoSuchFolderException e) {
                        coreProxy.logInfo("No " + sourcepath + " source code folder for " + namespace);
                    }
                }
                coreProxy.logInfo("Loaded " + sources.size() +" sources for " + namespace  + " in " + ((System.nanoTime() - start) / 1000000d) + "ms");
            }
        } catch (Exception e) {
            // Any problem retrieving anything will cause this to happen. We don't want to go down gracefully in this situation.
            throw new PreconditionException("Problem retrieving source files from liferay for " + namespace, e);
        }

        return sources;
    }

    private void addSources(Map<String, StringSourceFileObject> sources, DLFolder folder, String parentPackage) throws Exception {
        String packageName = StringUtils.isBlank(parentPackage) ? "" : parentPackage + ".";

        // Add all the source files in this folder
        List<DLFileEntry> files = DLFileEntryLocalServiceUtil.getFileEntries(REPOSITORY_ID, folder.getFolderId());
        for (DLFileEntry fileEntry : files) {
            if (SOURCE.extension.endsWith(fileEntry.getExtension())) {
                String name = packageName + fileEntry.getTitle().split("\\.")[0];
                InputStream is = DLFileEntryLocalServiceUtil.getFileAsStream(fileEntry.getFileEntryId(), fileEntry.getLatestFileVersion(true).getVersion());
                sources.put(name, new StringSourceFileObject(name, IOUtils.toString(is)));
                coreProxy.logDebug("Loaded source for " + name);
            } // else not a source file we want in the classloader
        }

        // Now find all sub folders and recurse
        List<DLFolder> subfolders = DLFolderLocalServiceUtil.getFolders(REPOSITORY_ID, folder.getFolderId());
        for (DLFolder subfolder : subfolders) {
            addSources(sources, subfolder, packageName + subfolder.getName());
        }
    }

    /**
     * Search all the classes in this classloader for possible {@link Type}s. These will be anything that has the {@link ProductServiceCommand} annotation.
     */
    private void addServiceCommandTypes() {
        if (sources != null) {
            for (String possibleServiceName : sources.keySet()) {
                try {
                    addServiceCommandType(findClass(possibleServiceName));
                } catch (ClassNotFoundException e) {
                    coreProxy.logWarning("No class found in this classloader for " + possibleServiceName + ": " + e.getMessage());
                }
            }
        }
    }

    /**
     * Add a Service configuration to a map of Types if none exists for this class here or in the main config.
     * The service name will be set to the FQN of the class unless serviceName has been set for the ProductServiceCommand annotation.
     * If the commandName has been set for the ProductServiceCommand annotation then that will be used as the command name
     * for a Command configuration also.
     * @param serviceName
     */
    private void addServiceCommandType(Class<?> possibleServiceCommand) {
        ProductServiceCommand productServiceCommand = possibleServiceCommand.getAnnotation(ProductServiceCommand.class);

        if (productServiceCommand == null) {
            // just do nothing, no need to throw an exception
            return;
        }

        String serviceName = productServiceCommand.serviceName();
        if (StringUtils.isBlank(serviceName)) {
            serviceName = possibleServiceCommand.getName();
        }

        if (!isExistingType(serviceName)) {
            Service service = new Service();
            // put into the types map keyed by the supplied serviceName, but set the Service.name to the FQN. Need that later to instantiate
            service.setName(possibleServiceCommand.getName());
            service.setBuilder(CACHING_CLASS_BUILDER);
            service.setKey(JavaAccessor.class.getName());
            types.put(serviceName, service);
            coreProxy.logInfo("Added dynamic service type for '" + serviceName + "' to namespace '" + namespace + "'");
        }

        String commandName = productServiceCommand.commandName();
        if (StringUtils.isNotBlank(productServiceCommand.commandName())
                && !isExistingType(commandName)) {
            Command command = new Command();
            command.setName(commandName);
            command.setKey(EXECUTE_PAGE_ACTION_COMMAND_IMPL);
            command.addParameter(new Parameter(SERVICE, serviceName));
            types.put(commandName, command);
            coreProxy.logInfo("Added dynamic command type for '" + commandName + "' to namespace '" + namespace + "'");
        }
    }

    private boolean isExistingType(String serviceName) {
        Group configType = coreProxy.getGroup("_Types." + serviceName);
        if (configType != null && !configType.getNamespace().equals(namespace)) {
            /// we want to be able to override services at a product level, so ignore anything defined by a parent
            configType = null;
        }

        return configType != null || types.containsKey(serviceName);
    }
}