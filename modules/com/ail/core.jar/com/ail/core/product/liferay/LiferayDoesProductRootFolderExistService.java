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

import com.ail.annotation.ServiceImplementation;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.product.DoesProductRootFolderExistService.DoesProductRootFolderExistArgument;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;

@ServiceImplementation
public class LiferayDoesProductRootFolderExistService extends Service<DoesProductRootFolderExistArgument> {
    private Long repositoryId;
    private String root;

    public LiferayDoesProductRootFolderExistService() {
        String idAsString = getCore().getParameter("ProductRepository.RepositoryID").getValue();
        repositoryId = new Long(idAsString);

        root = getCore().getParameter("ProductRepository.Root").getValue();
    }

    @Override
    public void invoke() throws PreconditionException, PostconditionException {

        try {
            DLAppLocalServiceUtil.getFolder(repositoryId, DEFAULT_PARENT_FOLDER_ID, root);
            args.setProductRootFolderExistsRet(true);
        } catch (Exception e) {
            args.setProductRootFolderExistsRet(false);
        }
    }
}
