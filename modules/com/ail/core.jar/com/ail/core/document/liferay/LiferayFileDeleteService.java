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

import org.apache.commons.lang.StringUtils;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.document.liferay.LiferayFileDeleteService.LiferayFileDeleteArgument;

/**
 * Provides a service to delete any file from the liferay document store. Does not return anything.
 */
@ServiceImplementation
public class LiferayFileDeleteService extends Service<LiferayFileDeleteArgument> {

    @Override
    public void invoke() throws BaseException {
        validate();

        LiferayDocumentContentManager manager = new LiferayDocumentContentManager(args.getCompanyIdArg(), args.getRepositoryIdArg(), args.getCreatorNameArg());

        manager.deleteDocument(args.getFileNameArg(), args.getFolderPathArg());
    }

    private void validate() throws PreconditionException {
        if (args.getCompanyIdArg() == null) {
            throw new PreconditionException("companyIdArg required!");
        }
        if (args.getRepositoryIdArg() == null) {
            throw new PreconditionException("repositoryIdArg required!");
        }
        if (StringUtils.isBlank(args.getFolderPathArg())) {
            throw new PreconditionException("folderPathArg required!");
        }
        if (StringUtils.isBlank(args.getCreatorNameArg())) {
            throw new PreconditionException("creatorNameArg required!");
        }
    }

    @ServiceCommand(defaultServiceClass = LiferayFileDeleteService.class)
    public interface LiferayFileDeleteCommand extends Command, LiferayFileDeleteArgument {
    }

    @ServiceArgument
    public interface LiferayFileDeleteArgument extends Argument {

        /**
         * The liferay company id
         */
        Long getCompanyIdArg();

        /**
         * The liferay company id
         */
        void setCompanyIdArg(Long companyIdArg);

        /**
         * The liferay repository id
         */
        Long getRepositoryIdArg();

        /**
         * The liferay repository id
         */
        void setRepositoryIdArg(Long repositoryIdArg);

        /**
         * The full folder path for where to put the file
         */
        String getFolderPathArg();

        /**
         * The full folder path for where to put the file
         */
        void setFolderPathArg(String folderPathArg);

        /**
         * The liferay creator name
         */
        String getCreatorNameArg();

        /**
         * The liferay creator name
         */
        void setCreatorNameArg(String creatorNameArg);

        /**
         * The file name for the file
         */
        String getFileNameArg();

        /**
         * The file name for the file
         */
        void setFileNameArg(String fileNameArg);
    }
}
