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
import static javax.tools.JavaFileObject.Kind.CLASS;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;

import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;

import org.jboss.vfs.VirtualFile;

import com.ail.core.CoreProxy;

/**
 * A JavaFileManager to read and write bytecode from memory rather than from disk.
 */
public class MemoryFileManager implements JavaFileManager {

    private final CoreProxy coreProxy = new CoreProxy();

    private NamespaceClassLoader classLoader;
    private JavaFileManager parentFileManager;
    private final Map<String, ClassFileObject> loadedClasses = new HashMap<>();
    private final Map<String, Set<JavaFileObject>> classpathClasses = new HashMap<>();

    public MemoryFileManager(NamespaceClassLoader classLoader, JavaFileManager parentFileManager) {
        this.classLoader = classLoader;
        this.parentFileManager = parentFileManager;
    }

    @Override
    public FileObject getFileForInput(JavaFileManager.Location location, String packageName, String relativeName) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public JavaFileObject getJavaFileForInput(Location location, String className, JavaFileObject.Kind kind) throws IOException {
        return loadedClasses.get(className);
    }

    @Override
    public JavaFileObject getJavaFileForOutput(Location location, String name, Kind kind, FileObject sibling) throws IOException {
        ClassFileObject fileObject = new ClassFileObject(name);

        loadedClasses.put(name, fileObject);

        return fileObject;
    }

    @Override
    public boolean isSameFile(FileObject a, FileObject b) {
        return false;
    }

    @Override
    public ClassLoader getClassLoader(Location location) {
        return classLoader;
    }

    @Override
    public Iterable<JavaFileObject> list(Location location, String packageName, Set<JavaFileObject.Kind> kinds, boolean recurse) throws IOException {
        // get the parent file manager files first
        Iterable<JavaFileObject> parentFiles = parentFileManager.list(location, packageName, kinds, recurse);

        // get all the ones from this manager too
        Set<JavaFileObject> thisFiles = classpathClasses.get(packageName);
        if (thisFiles == null) {
            thisFiles = find(packageName);
            if (thisFiles.iterator().hasNext()) {
                classpathClasses.put(packageName, (Set<JavaFileObject>) thisFiles);
            }
        }

        // Add them together
        Set<JavaFileObject> allFiles = new HashSet<>();
        allFiles.addAll(thisFiles);
        for (JavaFileObject parentFile : parentFiles) {
            allFiles.add(parentFile);
        }

        return allFiles;
    }

    @Override
    public int isSupportedOption(String option) {
        return 0;
    }

    @Override
    public String inferBinaryName(Location location, JavaFileObject file) {
        if (file instanceof ClassFileObject) {
            return ((ClassFileObject) file).binaryName();
        } else if (file instanceof ClasspathFileObject) {
            return ((ClasspathFileObject) file).binaryName();
        }

        return parentFileManager.inferBinaryName(location, file);
    }

    @Override
    public boolean handleOption(String current, Iterator<String> remaining) {
        return false;
    }

    @Override
    public boolean hasLocation(Location location) {
        return false;
    }

    @Override
    public FileObject getFileForOutput(Location location, String packageName, String relativeName, FileObject sibling) throws IOException {
        return null;
    }

    @Override
    public void flush() throws IOException {
    }

    @Override
    public void close() throws IOException {
    }

    private Set<JavaFileObject> find(String packageName) throws IOException {
        String javaPackageName = packageName.replaceAll("\\.", "/");

        Set<JavaFileObject> result = new HashSet<>();

        // Get everything the standard classloader getResources method returns
        Enumeration<URL> urlEnumeration = classLoader.getResources(javaPackageName);
        while (urlEnumeration.hasMoreElements()) { // one URL for each jar on the classpath that has the given package
            URL packageFolderURL = urlEnumeration.nextElement();
            if (!packageFolderURL.toExternalForm().contains("-sources.jar") && !packageFolderURL.toExternalForm().contains("-javadoc.jar")) {
                result.addAll(listUnder(packageName, packageFolderURL));
            }
        }

        // and get all the loaded memory classes from this file manager too
        for (ClassFileObject loadedClass : loadedClasses.values()) {
            String className = loadedClass.binaryName();
            if ("".equals(packageName) && !className.contains(".")) {
                result.add(loadedClass);
            } else if (className.startsWith(packageName) && className.substring(packageName.length(), className.length()).indexOf(".") < 1) {
                result.add(loadedClass);
            }
        }

        return result;
    }

    private Set<JavaFileObject> listUnder(String packageName, URL packageFolderURL) {
        File directory = new File(packageFolderURL.getFile());
        if (directory.isDirectory()) { // browse local .class files - useful for local execution
            return processDir(packageName, directory);
        } else if (directory.getAbsolutePath().matches(".*(\\.jar[!\\\\/]).*")) { // browse a jar file
            return processJar(packageFolderURL);
        } // maybe there can be something else for more involved class loaders

        return new HashSet<>();
    }

    private Set<JavaFileObject> processJar(URL packageFolderURL) {
        Set<JavaFileObject> result = new HashSet<>();
        try {
            URL jarURL = packageFolderURL;
            String jarUri = packageFolderURL.toExternalForm();
            if (jarUri.startsWith("vfs:")) { // e.g. vfs:/content/base-product-loader-hook.war/WEB-INF/lib/commons-lang-2.6.jar/org/apache/commons/lang/
                // Need to translate this to a real path on the filesystem
                try {
                    VirtualFile jbossVirtualFile = (VirtualFile) packageFolderURL.getContent();
                    File fileSystemFile = jbossVirtualFile.getPhysicalFile();
                    // e.g. /opt/openunderwriter-community-release.test/liferay-portal-6.2-ce-ga6/jboss-7.1.1/standalone/tmp/vfs/deployment6a7a02f0511dcda0/commons-lang-2.6.jar-6a2bf8fdd997d0da/contents/org/apache/commons/lang
                    String regex = "(.*)(\\/.*?\\.jar)(-.*)\\/contents";
                    jarUri = fileSystemFile.getPath().replace('\\', '/').replaceAll(regex, "jar:file:$1$2$3$2!");
                    // e.g. jar:file:/opt/openunderwriter-community-release.test/liferay-portal-6.2-ce-ga6/jboss-7.1.1/standalone/tmp/vfs/deployment6a7a02f0511dcda0/commons-lang-2.6.jar-6a2bf8fdd997d0da/commons-lang-2.6.jar!/org/apache/commons/lang
                } catch (IOException e) {
                    coreProxy.logError("Error reading VFS file for " + jarUri, e);
                }

                jarURL = new URL(jarUri);
            }

            JarURLConnection jarConn = (JarURLConnection) jarURL.openConnection();
            String rootEntryName = jarConn.getEntryName();
            int rootEnd = rootEntryName == null ? 0 : rootEntryName.length() + 1;

            try {
                Enumeration<JarEntry> entryEnum = jarConn.getJarFile().entries();
                while (entryEnum.hasMoreElements()) {
                    JarEntry jarEntry = entryEnum.nextElement();
                    String name = jarEntry.getName();
                    if (name.endsWith(CLASS.extension) &&
                            (rootEntryName == null || (name.startsWith(rootEntryName) && name.indexOf('/', rootEnd) == -1))) {
                        URI uri = URI.create(jarUri.split("!")[0] + "!/" + name);
                        String binaryName = name.replaceAll("/", ".");
                        binaryName = binaryName.replaceAll(CLASS.extension + "$", "");

                        result.add(new ClasspathFileObject(binaryName, uri));
                    }
                }
            } catch (Exception e) {
                coreProxy.logDebug("Wasn't able to open " + packageFolderURL + " as a jar file: " + e.getMessage());
            }

            if (!result.isEmpty()) {
                coreProxy.logDebug("Adding " + result.size() + " classes from: " + jarURL.toExternalForm());
            }
        } catch (Exception e) {
            throw new RuntimeException("Wasn't able to open " + packageFolderURL + " as a jar file", e);
        }

        return result;
    }

    private Set<JavaFileObject> processDir(String packageName, File directory) {
        Set<JavaFileObject> result = new HashSet<>();

        File[] childFiles = directory.listFiles();
        for (File childFile : childFiles) {
            if (childFile.isFile()) {
                // We only want the .class files.
                if (childFile.getName().endsWith(CLASS.extension)) {
                    String binaryName = packageName + "." + childFile.getName();
                    binaryName = binaryName.replaceAll(CLASS.extension + "$", "");

                    result.add(new ClasspathFileObject(binaryName, childFile.toURI()));
                }
            }
        }

        return result;
    }
}