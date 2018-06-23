/* Copyright Applied Industrial Logic Limited 2016. All rights Reserved */
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

import static com.liferay.portlet.documentlibrary.model.DLFolderConstants.DEFAULT_PARENT_FOLDER_ID;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.CoreProxy;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.RunAsProductHandler;
import com.ail.core.Service;
import com.ail.core.product.ScanForProductDefinitionsService.ScanForProductDefinitionsArgument;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLAppServiceUtil;

@ServiceImplementation
public class LiferayScanForProductDefinitionsService extends Service<ScanForProductDefinitionsArgument> {
    private Long repositoryId;
    private String root;

    LiferayScanForProductDefinitionsService(long repositoryId, String root) {
        super();
        this.repositoryId = repositoryId;
        this.root = root;
    }

    LiferayScanForProductDefinitionsService(CoreProxy coreProxy) {
        this(new Long(coreProxy.getParameterValue("ProductRepository.RepositoryID")), coreProxy.getParameterValue("ProductRepository.Root"));
    }

    public LiferayScanForProductDefinitionsService() {
        this(new CoreProxy());
    }

    @Override
    public void invoke() throws PostconditionException, PreconditionException {

        try {
            new RunAsProductHandler() {
                @Override
                protected void doRun() throws Exception {
                    List<String> details;

                    details = findAllProducts();

                    Collections.sort(details);;

                    args.setProductTypeIDsRet(details);
                }
            }.run();
        } catch (Exception e) {
            throw new PreconditionException("Failed to read product content store", e);
        }

        if (args.getProductTypeIDsRet() == null) {
            throw new PostconditionException("args.getProductTypeIDsRet()==null");
        }
    }

    private List<String> findAllProducts() throws PortalException, SystemException {
        return findProductsIn(findProductRootFolder(), new ArrayList<String>(), "");
    }

    private List<String> findProductsIn(Folder folder, List<String> details, String pwd) throws PortalException, SystemException {
        List<FileEntry> fileEntries = DLAppServiceUtil.getFileEntries(repositoryId, folder.getFolderId());
        for (FileEntry fileEntry : fileEntries) {
            if ("Registry.xml".equals(fileEntry.getTitle())) {
                details.add(pwd);
            }
        }

        String prefix = pwd.length()==0 ? "" : pwd+".";
        List<Folder> subFolders = DLAppServiceUtil.getFolders(repositoryId, folder.getFolderId());
        for (Folder subFolder : subFolders) {
            details = findProductsIn(subFolder, details, prefix + subFolder.getName());
        }

        return details;
    }

    private Folder findProductRootFolder() throws PortalException, SystemException {
        return DLAppLocalServiceUtil.getFolder(repositoryId, DEFAULT_PARENT_FOLDER_ID, root);
    }
}
