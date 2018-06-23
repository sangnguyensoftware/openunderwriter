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
package com.ail.ui.shared.viewer;

import java.io.Serializable;

public class ViewerCommand implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private static final String COMMAND_DELIM = ",";
    
    public enum ViewerId {
        QUICKSEARCH,
        POLICYSEARCH;
    }
    
    private long entityId;
    
    private ViewerId viewerId;
    
    
    public ViewerCommand() {
    }
    
    public ViewerCommand(String partsString) {
        this(partsString.split(COMMAND_DELIM));
    }
    
    public ViewerCommand(String[] parts) {
        this(
                Long.valueOf(parts[0]), 
                ViewerCommand.ViewerId.valueOf(parts[1]));
    }

    public ViewerCommand(Long entityId, ViewerId viewerId) {
        this.entityId = entityId;
        this.viewerId = viewerId;
    }

    public long getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public ViewerId getViewerId() {
        return viewerId;
    }

    public void setViewerId(ViewerId viewerId) {
        this.viewerId = viewerId;
    }

   public String toString() {
       return viewerId.name() + COMMAND_DELIM + getEntityId();
   }

  

    
    
}
