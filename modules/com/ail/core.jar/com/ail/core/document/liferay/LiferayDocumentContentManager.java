/* Copyright Applied Industrial Logic Limited 2017. All rights Reserved */
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
package com.ail.core.document.liferay;

import static com.ail.core.Functions.isEmpty;
import static com.liferay.portlet.documentlibrary.model.DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;

import com.ail.core.CoreProxy;
import com.ail.core.Functions;
import com.ail.core.PreconditionException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLAppServiceUtil;

/**
 * Provides wrapper methods to the Liferay document store API.
 * Expects either the following properties to be set in the product registry:
 * <pre>
 *   <group name="DocumentContentCms">
 *     <parameter name="CompanyID">10157</parameter>
 *     <parameter name="RepositoryID">10197</parameter>
 *     <parameter name="Root">Documents</parameter>
 *     <parameter name="Creator">documentowner</parameter>
 *   </group>
 * </pre>
 * Or for those properties to be passed in via constructor. Uses them to determine where the documents will be stored.
 */
public class LiferayDocumentContentManager {
    private Long companyId;
    private Long repositoryId;
    private String creatorName;
    private Long rootFolderId;
    private String rootFolderName;
    private ServiceContext serviceContext;
    private User documentOwner;

    /**
     * Constructor to use when supplying repository details that are not the default ones set in the DocumentContentCms
     * @param companyId     the ID of the portal instance (Control Panel > Configuration > Portal Instances)
     * @param repositoryId  the ID of the site, e.g. Global, OpenUnderwriter (Control Panel > Sites)
     * @param creatorName   the screen name of a user to set as the document creator
     * @throws PreconditionException
     */
    LiferayDocumentContentManager(Long companyId, Long repositoryId, String creatorName) throws PreconditionException {
        this.companyId = companyId;
        this.repositoryId = repositoryId;
        this.creatorName = creatorName;

        initialiseContext();
    }

    /**
     * Constructor to use the default settings from the registry
     * @param productTypeId
     * @throws PreconditionException
     */
    LiferayDocumentContentManager(String productTypeId) throws PreconditionException {
        CoreProxy core = new CoreProxy(Functions.productNameToConfigurationNamespace(productTypeId));

        companyId = getMandatoryParamValue(core, "DocumentContentCms.CompanyID", Long.class);
        repositoryId = getMandatoryParamValue(core, "DocumentContentCms.RepositoryID", Long.class);
        rootFolderName = getMandatoryParamValue(core, "DocumentContentCms.Root", String.class);
        creatorName = getMandatoryParamValue(core, "DocumentContentCms.Creator", String.class);

        initialiseContext();
        initialiseRootFolder();
    }

    /**
     * Initialise the liferay service and permission contexts
     * @throws PreconditionException
     */
    private void initialiseContext() throws PreconditionException {
        documentOwner = getDocumentOwnerFromScreenName(creatorName);

        serviceContext = new ServiceContext();

        try {
            PrincipalThreadLocal.setName(documentOwner.getUserId());
            PermissionThreadLocal.setPermissionChecker(PermissionCheckerFactoryUtil.create(documentOwner));
        } catch (Exception e) {
            throw new PreconditionException("Failed to create context for documentOwner user: '"+creatorName+"' (user id: "+documentOwner.getUserId()+")");
        }
    }

    /**
     * Initialise the root folder id from the default root folder name
     * @throws PreconditionException
     */
    private void initialiseRootFolder() throws PreconditionException {
        if (folderDoesNotExist(DEFAULT_PARENT_FOLDER_ID, rootFolderName)) {
            rootFolderId = createFolder(DEFAULT_PARENT_FOLDER_ID, rootFolderName);
        } else {
            rootFolderId = fetchFolderId(DEFAULT_PARENT_FOLDER_ID, rootFolderName);
        }
    }

    /**
     * Get a default setting from the registry
     * @param core
     * @param parameter
     * @param clazz
     * @return
     * @throws PreconditionException
     */
    @SuppressWarnings("unchecked")
    private <T> T getMandatoryParamValue(CoreProxy core, String parameter, Class<T> clazz) throws PreconditionException {
        String paramValue = core.getParameterValue(parameter);

        if (paramValue == null || paramValue.length() == 0) {
            throw new PreconditionException(parameter + " == null || " + parameter + ".length == 0");
        }

        if (clazz == String.class) {
            return (T) paramValue;
        } else if (clazz == Long.class) {
            return (T) new Long(paramValue);
        } else {
            throw new PreconditionException("Parameter of type: " + clazz.getName() + " is not supported.");
        }
    }

    /**
     * Add a document to the default document store. The title is set to be a random unique id.
     * @param documentContent
     * @return
     * @throws PreconditionException if the root folder has not been set
     */
    FileEntry createDocument(byte[] documentContent) throws PreconditionException {
        if (rootFolderId == null) {
            throw new PreconditionException("Root folder not set");
        }

        try {
            String filename = UUID.randomUUID().toString();
            return DLAppLocalServiceUtil.addFileEntry(documentOwner.getUserId(), repositoryId, rootFolderId, filename, null, filename, null, null, documentContent, serviceContext);
        } catch (Exception e) {
            throw new PreconditionException("Failed to create content: ", e);
        }
    }

    /**
     * Add a document with the given name at a specified folder path. Intermediate folders will be created if they do not exist.
     * If the file already exists a new version will be added.
     * @param documentContent
     * @param filename
     * @param path
     * @return
     * @throws PreconditionException
     */
    FileEntry createDocument(byte[] documentContent, String filename, String path) throws PreconditionException {
        try {
            String[] folders = path.split("[\\/\\\\]");
            long folderId = -1;
            for (int i = 0; i < folders.length; i++) {
                long parentFolderId = i == 0 ? DEFAULT_PARENT_FOLDER_ID : folderId;
                if (folderDoesNotExist(parentFolderId, folders[i])) {
                    folderId = createFolder(parentFolderId, folders[i]);
                } else {
                    folderId = fetchFolderId(parentFolderId, folders[i]);
                }
            }
            FileEntry existingFile = fetchEntry(folderId, filename);
            if (existingFile == null) {
                return DLAppLocalServiceUtil.addFileEntry(documentOwner.getUserId(), repositoryId, folderId, filename, null, filename, null, null, documentContent, serviceContext);
            } else {
                return DLAppLocalServiceUtil.updateFileEntry(documentOwner.getUserId(), existingFile.getFileEntryId(), filename, null, filename, null, null, false, documentContent, serviceContext);
            }
        } catch (Exception e) {
            throw new PreconditionException("Failed to create content: ", e);
        }
    }

    void deleteDocument(String filename, String path) throws PreconditionException {
        try {
            String[] folders = path.split("[\\/\\\\]");
            long folderId = -1;
            for (int i = 0; i < folders.length; i++) {
                long parentFolderId = i == 0 ? DEFAULT_PARENT_FOLDER_ID : folderId;
                if (!folderDoesNotExist(parentFolderId, folders[i])) {
                    folderId = fetchFolderId(parentFolderId, folders[i]);
                }
            }
            FileEntry existingFile = fetchEntry(folderId, filename);
            if (existingFile != null) {
                DLAppLocalServiceUtil.deleteFileEntry(existingFile.getFileEntryId());
            }
        } catch (Exception e) {
            throw new PreconditionException("Failed to delete content: ", e);
        }
    }

    /**
     * Retrieve documents from the path in the document store which match a given pattern.
     * @param path Path (folder) in the document library to search.
     * @param regex A pattern that file names must match (null == '.*').
     * @return A map of fileTitles to their content
     * @throws PreconditionException
     */
    Map<String, byte[]> retrieveDocuments(String path, String regex) throws PreconditionException {
        Map<String, byte[]> files = new HashMap<>();

        try {
            String[] folders = path.split("[\\/\\\\]");
            long folderId = -1;
            for (int i = 0; i < folders.length; i++) {
                long parentFolderId = folderId == -1 ? DEFAULT_PARENT_FOLDER_ID : folderId;

                if (!isEmpty(folders[i]) && !folderDoesNotExist(parentFolderId, folders[i])) {
                    folderId = fetchFolderId(parentFolderId, folders[i]);
                }
            }

            Pattern pattern = regex != null ? Pattern.compile(regex) : null;

            List<FileEntry> fileEntries = DLAppServiceUtil.getFileEntries(repositoryId, folderId);
            for (FileEntry fileEntry : fileEntries) {
                if (pattern == null || pattern.matcher(fileEntry.getTitle()).matches()) {
                    files.put(fileEntry.getTitle(), IOUtils.toByteArray(fileEntry.getContentStream()));
                }
            }

            return files;
        } catch (Exception e) {
            throw new PreconditionException("Failed to retrieve documents at: " + path, e);
        }
    }

    /**
     * Retrieve all the documents from a path in the document store.
     * @param path Path (folder) in the document library to search.
     * @return A map of fileTitles to their content
     * @throws PreconditionException
     */
    Map<String, byte[]> retrieveDocuments(String path) throws PreconditionException {
        return retrieveDocuments(path, null);
    }

    /**
     * Retrieve a document from the default document store by title.
     * @param title
     * @return
     * @throws PreconditionException
     */
    byte[] retrieveDocument(String title) throws PreconditionException {
        FileEntry fileEntry;

        try {
            fileEntry = DLAppLocalServiceUtil.getFileEntry(repositoryId, rootFolderId, title);
            return IOUtils.toByteArray(fileEntry.getContentStream());
        } catch (Exception e) {
            throw new PreconditionException("Failed to retrieve document: " + rootFolderName + "/" + title, e);
        }
    }

    private FileEntry fetchEntry(long parentFolderId, String title) throws PreconditionException {
        try {
            return DLAppLocalServiceUtil.getFileEntry(repositoryId, parentFolderId, title);
        } catch (Exception e) {
            return null;
        }
    }

    private Long fetchFolderId(long parentFolderId, String folderName) throws PreconditionException {
        try {
            Folder folder = DLAppLocalServiceUtil.getFolder(repositoryId, parentFolderId, folderName);
            return folder.getFolderId();
        } catch (Exception e) {
            throw new PreconditionException("Failed to create document content folderName folder: " + folderName, e);
        }
    }

    private Long createFolder(long parentFolderId, String folderName) throws PreconditionException {
        try {
            Folder folder = DLAppLocalServiceUtil.addFolder(documentOwner.getUserId(), repositoryId, parentFolderId, folderName, "Document store", serviceContext);
            return folder.getFolderId();
        } catch (Exception e) {
            throw new PreconditionException("Failed to create document content rootFolderName folder: " + rootFolderName, e);
        }
    }

    private boolean folderDoesNotExist(long parentFolderId, String folderName) {
        try {
            DLAppLocalServiceUtil.getFolder(repositoryId, parentFolderId, folderName);
            return false;
        } catch (Exception e) {
            return true;
        }
    }

    private User getDocumentOwnerFromScreenName(String creatorScreenName) {
        try {
            return UserLocalServiceUtil.getUserByScreenName(companyId, creatorScreenName);
        } catch (Throwable t) {
            return null;
        }
    }
}
