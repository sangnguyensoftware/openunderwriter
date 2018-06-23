/* Copyright Applied Industrial Logic Limited 2003. All rights Reserved */
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

package com.ail.core.document;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.XMLString;
import com.ail.core.document.model.RenderContext;

@ServiceImplementation
public class JavaMergeDataService extends Service<MergeDataService.MergeDataArgument> {

    public void invoke() throws PreconditionException, PostconditionException {
        if (args.getModelArg()==null) {
            throw new PreconditionException("args.getModelArg()==null");
        }

        if (args.getDocumentDataArg()==null) {
            throw new PreconditionException("args.getDocumentDataArg()==null");
        }
        
        StringWriter sw=new StringWriter();
        RenderContext context=new RenderContext(new PrintWriter(sw), args.getModelArg());
        args.getDocumentDataArg().render(context);
        args.setMergedDataRet(new XMLString(sw.toString()));
        
        if (args.getMergedDataRet()==null) {
            throw new PostconditionException("args.getMergedDataRet()==null");
        }
    }
}


