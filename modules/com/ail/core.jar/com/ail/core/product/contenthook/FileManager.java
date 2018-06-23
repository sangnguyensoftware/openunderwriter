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

import java.io.InputStream;

import org.apache.commons.io.IOUtils;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;

class FileManager {
    private static final String DESCRIPTION = "Default OpenUnderwriter content.";

    private ServiceContext serviceContext;
    private Long repositoryId = 0L;
    private Long creatorId = 0L;
    private FolderManager folderManager;

    FileManager(FolderManager folderManager, Long repositoryId, Long creatorId) {
        this.repositoryId = repositoryId;
        this.creatorId = creatorId;
        this.folderManager = folderManager;
        this.serviceContext = new ServiceContext();
    }

    void createFile(String path) {
        Long folderId = folderManager.getFolders().get(folderName(path));
        String filename = fileName(path);
        String title = filename;
        String changeLog = "Bootstrap product content loaded by product-content.war";

        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path)) {

            if (in == null) {
                throw new ContentLoadError("Failed to open file as a class resource: " + path + "'");
            }

            byte[] bytes = IOUtils.toByteArray(in);

            DLAppLocalServiceUtil.addFileEntry(creatorId, repositoryId, folderId, filename, null, title, DESCRIPTION, changeLog, bytes, serviceContext);
        } catch (Throwable t) {
            throw new ContentLoadError("File add failed for: '" + path + "'", t);
        }
    }

    boolean isExistingFile(String path) {
        Long folderId = folderManager.getFolders().get(folderName(path));

        try {
            DLAppLocalServiceUtil.getFileEntry(repositoryId, folderId, fileName(path));
        } catch (PortalException e) {
            return false;
        } catch (SystemException e) {
            throw new ContentLoadError("File existance check failed for: '" + path + "'", e);
        }

        return true;
    }

    private String folderName(String path) {
        return path.substring(0, path.lastIndexOf('/') + 1);
    }

    private String fileName(String path) {
        return path.substring(path.lastIndexOf('/') + 1);
    }
}
