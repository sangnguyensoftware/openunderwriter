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
package com.ail.workflow.identity;

import java.util.List;

import org.kie.api.task.UserGroupCallback;

import com.ail.workflow.LiferayUserGroupDelegate;

public class LiferayUserGroupCallback implements UserGroupCallback {

    LiferayUserGroupDelegate liferayUserGroupDelegate;

    public LiferayUserGroupCallback(LiferayUserGroupDelegate liferayUserGroupDelegate) {
        this.liferayUserGroupDelegate = liferayUserGroupDelegate;
    }

    @Override
    public boolean existsGroup(String group) {
        return liferayUserGroupDelegate.existsGroup(group);
    }

    @Override
    public boolean existsUser(String username) {
        return liferayUserGroupDelegate.existsUser(username);
    }

    @Override
    public List<String> getGroupsForUser(String username, List<String> groupIds, List<String> allExistingGroupIds) {
        return liferayUserGroupDelegate.getGroupsForUser(username, groupIds, allExistingGroupIds);
    }
}
