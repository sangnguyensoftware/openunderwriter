/* Copyright Applied Industrial Logic Limited 2005. All rights Reserved */
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

package com.ail.core.product.liferay;

import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.CoreProxy;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.ThreadLocale;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.configure.ConfigurationHandler;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.service.DLAppServiceUtil;

@ServiceImplementation
public class LiferayLocateFileEntryService extends Service<LiferayLocateFileEntryService.LiferayLocateFileEntryArgument> {
    private Long repositoryId;
    private String root;

    LiferayLocateFileEntryService(long repositoryId, String root) {
        super();
        this.repositoryId = repositoryId;
        this.root = root;
    }

    LiferayLocateFileEntryService(CoreProxy coreProxy) {
        this(new Long(coreProxy.getParameterValue("ProductRepository.RepositoryID")), coreProxy.getParameterValue("ProductRepository.Root"));
    }

    public LiferayLocateFileEntryService() {
        this(new CoreProxy());
    }

    @ServiceArgument
    public interface LiferayLocateFileEntryArgument extends Argument {
        void setProductUrlArg(URL productUrlArg);

        URL getProductUrlArg();

        void setFileEntryRet(FileEntry fileEntryRet);

        FileEntry getFileEntryRet();
    }

    @ServiceCommand(defaultServiceClass = LiferayLocateFileEntryService.class)
    public interface LiferayLocateFileEntryCommand extends Command, LiferayLocateFileEntryArgument {
    }

    @Override
    public void invoke() throws PreconditionException, PostconditionException {
        if (args.getProductUrlArg() == null) {
            throw new PreconditionException("args.getProductUrlArg() == null");
        }

        String urlPath = args.getProductUrlArg().getPath();

        try {
            if (urlPath.endsWith("/Registry.xml")) {
                args.setFileEntryRet(locateFileEntry(urlPath));
            } else {
                args.setFileEntryRet(locateFileEntryInNamespaceHierarchy(urlPath));
            }
        } catch (Exception e) {
            getCore().logWarning("Failed to locate content for: "+urlPath, e);
        }

        if (args.getFileEntryRet() == null) {
            throw new PostconditionException("args.getFileEntryRet() == null");
        }
    }

    /**
     * Locate a file entry by walking up the namespace ancestor tree.
     *
     * @param urlPath
     * @return Located file.
     * @throws PortalException
     *             The file was not found locally or in any ancestor namespace.
     * @throws SystemException
     *             A system error occurred during the search
     */
    FileEntry locateFileEntryInNamespaceHierarchy(String urlPath) throws PortalException, SystemException {
        String namespace = findNamespaceForUrlPath(urlPath);
        return locateFileEntryFromNamespace(urlPath, namespace);
    }

    String findNamespaceForUrlPath(String urlPath) throws PortalException {
        String bestMatch = null;

        for (String namespace : fetchNamespaces()) {
            String namespacePath = namespace.replace('.', '/').replace("/Registry", "/");
            if (namespace.endsWith(".Registry") && urlPath.contains(namespacePath)) {
                if (bestMatch == null || namespace.length() > bestMatch.length()) {
                    bestMatch = namespace;
                }
            }
        }

        if (bestMatch != null) {
            return bestMatch;
        }

        throw new PortalException("Product URL path (" + urlPath + ") is not within a product.");
    }

    FileEntry locateFileEntryFromNamespace(String urlPath, String namespace) throws PortalException {
        String namespacePath = namespace.replace('.', '/').replace("/Registry", "");

        CoreProxy cp = createCoreProxyForNamespace(namespace);

        for (String ancestorNamespace : cp.getConfigurationNamespaceParent()) {

            String ancestorNamespacePath = ancestorNamespace.replace('.', '/');

            String ancestorUrlPath = urlPath.replaceAll(namespacePath, ancestorNamespacePath).replace("/Registry/", "/");

            try {
                return locateFileEntry(ancestorUrlPath);
            } catch (Throwable th) {
                // Ignore this. continue to loop through the other ancestors
            }
        }

        throw new PortalException("File not found: " + urlPath);
    }

    /**
     * Locate a file based on its urlPath.
     *
     * @param urlPath
     * @return
     * @throws PortalException
     *             File could not be found.
     * @throws SystemException
     *             An error occurred during the search.
     */
    FileEntry locateFileEntry(String urlPath) throws PortalException, SystemException {
        String fullPath = root + "/" + urlPath;

        Iterator<String> pathElements = Arrays.asList(fullPath.split("/")).iterator();

        return findFileEntry(DLFolderConstants.DEFAULT_PARENT_FOLDER_ID, pathElements);
    }

    /**
     * Drill down in the document library to locate the file specified by
     * <code>pathElements</code>. A locale specific version of the file will be
     * returned if one exists.
     *
     * @param folder
     *            Folder to start search from.
     * @param pathElements
     *            Strings making up the folder names of the path (empty strings
     *            will be ignored).
     * @return File that was located.
     * @throws PortalException
     *             No such file entry exists.
     * @throws SystemException
     *             A system error prevented the file being returned.
     */
    FileEntry findFileEntry(long folder, Iterator<String> pathElements) throws PortalException, SystemException {
        String name = null;

        // get next path element, skipping all intervening elements that are
        // empty
        for (name = pathElements.next(); "".equals(name); name = pathElements.next())
            ;

        if (pathElements.hasNext()) {
            long folderId = getFolderId(folder, name);
            return findFileEntry(folderId, pathElements);
        } else {
            try {
                return getFileEntry(folder, name + "_" + getThreadLanguage());
            } catch (PortalException ex) {
                return getFileEntry(folder, name);
            }
        }
    }

    String getThreadLanguage() {
        return ThreadLocale.getThreadLocale().getLanguage();
    }

    FileEntry getFileEntry(long folder, String name) throws PortalException, SystemException {
        return DLAppServiceUtil.getFileEntry(repositoryId, folder, name);
    }

    Long getFolderId(long folder, String name) throws PortalException, SystemException {
        return DLAppServiceUtil.getFolder(repositoryId, folder, name).getFolderId();
    }

    Collection<String> fetchNamespaces() {
        return ConfigurationHandler.getInstance().getNamespaces();
    }

    CoreProxy createCoreProxyForNamespace(String namespace) {
        return new CoreProxy(namespace);
    }
}
