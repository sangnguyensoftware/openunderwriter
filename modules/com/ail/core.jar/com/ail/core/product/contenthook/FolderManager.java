/* Copyright Applied Industrial Logic Limited 2014. All rights Reserved */
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
package com.ail.core.product.contenthook;

import static com.liferay.portlet.documentlibrary.model.DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;

class FolderManager {
    private static final String DESCRIPTION = "Default OpenUnderwriter content.";

    static final String PRODUCT_ROOT = "/Product/";

    private ServiceContext serviceContext;
    private Long repositoryId = 0L;
    private Long creatorId = 0L;
    private Map<String, Long> folders = new HashMap<>();

    FolderManager(Long repositoryId, Long creatorId) {
        this.repositoryId = repositoryId;
        this.creatorId = creatorId;
        this.serviceContext = new ServiceContext();
    }

    boolean isExistingFolder(String path) {
        String folderName = getFolderNameForPath(path);
        Long parentFolderId = getParentFolderIdForPath(path);

        try {
            Folder folder = DLAppLocalServiceUtil.getFolder(repositoryId, parentFolderId, folderName);
            folders.put(path, folder.getFolderId());
        } catch (PortalException e) {
            // PortalException is thrown when the folder does not exist.
            return false;
        } catch (SystemException e) {
            throw new ContentLoadError("Failed to test for existance of folder: '" + folderName + "'", e);
        }

        return true;
    }

    void createFolder(String path) {
        String folderName = getFolderNameForPath(path);
        Long parentFolderId = getParentFolderIdForPath(path);

        try {
            Folder folder = DLAppLocalServiceUtil.addFolder(creatorId, repositoryId, parentFolderId, folderName, DESCRIPTION, serviceContext);
            folders.put(path, folder.getFolderId());
        } catch (Exception e) {
            throw new ContentLoadError("Failed to create folder: '" + folderName + "'", e);
        }
    }

    Map<String, Long> getFolders() {
        return folders;
    }

    private String getFolderNameForPath(String path) {
        String[] elements = path.split("/");
        return elements[elements.length - 1];
    }

    private Long getParentFolderIdForPath(String path) {
        Long parentFolderId;
        if (PRODUCT_ROOT.equals(path)) {
            parentFolderId = DEFAULT_PARENT_FOLDER_ID;
        } else {
            String[] elements = path.split("/");
            String parentPath = StringUtils.join(elements, '/', 0, elements.length - 1);
            parentFolderId = folders.get(parentPath + "/");
        }
        return parentFolderId;
    }
}
