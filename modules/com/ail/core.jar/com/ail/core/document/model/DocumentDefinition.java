/* Copyright Applied Industrial Logic Limited 2006. All rights reserved. */
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
package com.ail.core.document.model;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;

/**
 * The document definition acts as a container for all the information that the document
 * production service needs in order to know how to generate a document. The production services
 * expects to find instances of this type in configuration.
 * The type defines two things: <ul>
 * <li><b>Lifecycle commands</b> - These are the commands (services) which the production
 * system should use at each point in the generation process.</li>
 * <li><b>Model mapping</b> - This defines how parts of the model on which the document
 * is based are mapped into the document itself. For example, a quotation document's <i>model</i>
 * might be an instance of the Policy.class, among other things the mapping defines where the 
 * premium can be found in that model.
 */
@TypeDefinition
public class DocumentDefinition extends Type {
    private String mergeCommand;
    private String styleCommand;
    private String renderCommand;
    private DocumentData documentData;
    
    public DocumentDefinition() {
    }
    
    public String getMergeCommand() {
        return mergeCommand;
    }

    public void setMergeCommand(String mergeCommand) {
        this.mergeCommand = mergeCommand;
    }
    
    public DocumentData getDocumentData() {
        return documentData;
    }
    
    public void setDocumentData(DocumentData documentData) {
        this.documentData = documentData;
    }
    
    public String getStyleCommand() {
        return styleCommand;
    }
    
    public void setStyleCommand(String styleCommand) {
        this.styleCommand = styleCommand;
    }
    
    public String getRenderCommand() {
        return renderCommand;
    }
    
    public void setRenderCommand(String renderCommand) {
        this.renderCommand = renderCommand;
    }
}
