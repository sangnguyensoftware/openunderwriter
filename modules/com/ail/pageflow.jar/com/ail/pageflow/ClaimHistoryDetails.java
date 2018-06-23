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
package com.ail.pageflow;

import java.io.IOException;

import com.ail.core.Type;

public class ClaimHistoryDetails extends PageContainer {
	private static final long serialVersionUID = -4810599045554021748L;

    private String excludeStatuses;

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("ClaimHistoryDetails", model);
    }

    /**
     * Comma separated list of statuses to exclude from the results.
     * @return list of statuses
     */
    public String getExcludeStatuses() {
        return excludeStatuses;
    }

    /**
     * Comma separated list of statuses to exclude from the results.
     * @param excludeStatuses to set
     */
    public void setExcludeStatuses(String excludeStatuses) {
        this.excludeStatuses = excludeStatuses;
    }

}
