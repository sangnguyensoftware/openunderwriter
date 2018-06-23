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
package com.ail.core;

import java.util.Date;

import javax.persistence.Entity;

import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;

/**
 * Records the detail of a page visit during the processing of quotation.
 */
@TypeDefinition
@Audited
@Entity
public class PageVisit extends Type {
	private static final long serialVersionUID = -7207431665904766363L;
	private String pageFlowName;   // Name of the PageFlow which pageName relates to.
    private String pageName;       // Name of the page that was visited
	private Date visited;		   // Date the page was visited

	public PageVisit() {
	}

	public PageVisit(String pageFlowName, String pageName, Date visited) {
	    this.pageFlowName=pageFlowName;
	    this.pageName=pageName;
		this.visited=visited;
	}

    public String getPageFlowName() {
	    return pageFlowName;
	}

	public String getPageName() {
		return pageName;
	}

	public Date getVisited() {
		return visited;
	}

    public void setPageFlowName(String pageFlowName) {
        this.pageFlowName = pageFlowName;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public void setVisited(Date visited) {
        this.visited = visited;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + typeHashCode();
        result = prime * result + ((pageFlowName == null) ? 0 : pageFlowName.hashCode());
        result = prime * result + ((pageName == null) ? 0 : pageName.hashCode());
        result = prime * result + ((visited == null) ? 0 : visited.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        if (!typeEquals(obj))
            return false;
        PageVisit other = (PageVisit) obj;
        if (pageFlowName == null) {
            if (other.pageFlowName != null)
                return false;
        } else if (!pageFlowName.equals(other.pageFlowName))
            return false;
        if (pageName == null) {
            if (other.pageName != null)
                return false;
        } else if (!pageName.equals(other.pageName))
            return false;
        if (visited == null) {
            if (other.visited != null)
                return false;
        } else if (!visited.equals(other.visited))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "PageVisit [pageFlowName=" + pageFlowName + ", pageName=" + pageName + ", visited=" + visited + "]";
    }
}
