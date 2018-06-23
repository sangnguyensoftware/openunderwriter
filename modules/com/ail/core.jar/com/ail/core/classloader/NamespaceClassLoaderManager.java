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
import java.util.HashMap;
import java.util.Map;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;

import com.ail.core.Core;
import com.ail.core.CoreProxy;
import com.ail.core.PreconditionException;
import com.ail.core.ServerWarmChecker;
import com.ail.core.configure.Configuration;
import com.ail.core.configure.ConfigurationError;
import com.ail.core.configure.ConfigurationHandler;

public class NamespaceClassLoaderManager {

    private static JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
    private static final Map<String, NamespaceClassLoader> classLoaders = new HashMap<>();
    private static final ServerWarmChecker serverWarmChecker = new ServerWarmChecker();

    private NamespaceClassLoaderManager() {

    }

    public static synchronized NamespaceClassLoader getClassLoader(String namespace, Core core) throws PreconditionException, CompileException {
        if (compiler == null) {
            throw new IllegalStateException("JDK Java compiler not available - probably you're running a JRE, not a JDK");
        }

        NamespaceClassLoader classLoader = classLoaders.get(namespace);
        // Only load for products, which will have a namespace ending with 'Registry'
        if (classLoader == null && namespace.endsWith("Registry") && serverWarmChecker.isServerWarmedUp()) {
            classLoader = loadClassLoader(namespace, core);
        }

        return classLoader;
    }

    public static synchronized NamespaceClassLoader getClassLoaderOrNull(String namespace, Core core) {
        try {
            return getClassLoader(namespace, core);
        } catch (PreconditionException | CompileException e) {
            core.logError("Problem getting classloader for " + namespace, e);
            return null;
        }
    }

    public static synchronized void removeClassLoader(String namespace) {
        if (classLoaders.containsKey(namespace)) {
            classLoaders.remove(namespace);
            new CoreProxy(namespace).logInfo("Removed classloader for " + namespace);
        }
    }

    private static synchronized NamespaceClassLoader loadClassLoader(String namespace, Core core) throws PreconditionException, CompileException {
        Configuration configuration;
        try {
            configuration = ConfigurationHandler.getInstance().loadConfiguration(namespace, core, core);
        } catch (ConfigurationError ce) {
            // this generally happens when the server is being started up and we do not care about it. log a warning, nothing more.
            core.logWarning("Configuration problem for " + namespace, ce);
            return null;
        }

        NamespaceClassLoader classLoader = null;

        String parentNamespace = configuration.getParentNamespace();
        ClassLoader parentClassLoader = Thread.currentThread().getContextClassLoader(); // default
        if (parentNamespace != null) {
            // got a namespace parent so get its classloader
            parentClassLoader = getClassLoader(parentNamespace, core);
        }

        classLoader = new NamespaceClassLoader(parentClassLoader, namespace, compiler);

        classLoaders.put(namespace, classLoader);
        new CoreProxy(namespace).logInfo("Loaded classloader for " + namespace);

        return classLoader;
    }
}
