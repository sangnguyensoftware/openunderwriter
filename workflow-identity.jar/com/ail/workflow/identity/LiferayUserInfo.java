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

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.kie.api.task.model.Group;
import org.kie.api.task.model.OrganizationalEntity;
import org.kie.internal.task.api.TaskModelProvider;
import org.kie.internal.task.api.UserInfo;

import com.ail.workflow.LiferayUserGroupDelegate;

public class LiferayUserInfo implements UserInfo {

    LiferayUserGroupDelegate liferayUserGroupDelegate;

    public LiferayUserInfo(LiferayUserGroupDelegate liferayUserGroupDelegate) {
        this.liferayUserGroupDelegate = liferayUserGroupDelegate;
    }

    @Override
    public String getDisplayName(OrganizationalEntity org) {
        return liferayUserGroupDelegate.getDisplayName(org.getId());
    }

    @Override
    public String getEmailForEntity(OrganizationalEntity org) {
        return liferayUserGroupDelegate.getEmailForEntity(org.getId());
    }

    @Override
    public String getLanguageForEntity(OrganizationalEntity org) {
        return liferayUserGroupDelegate.getLanguageForEntity(org.getId());
    }

    @Override
    public Iterator<OrganizationalEntity> getMembersForGroup(Group org) {
        Set<OrganizationalEntity> members = new HashSet<>();

        for (String user : liferayUserGroupDelegate.getMembersForGroup(org.getId())) {
            OrganizationalEntity member = TaskModelProvider.getFactory().newUser(user);
            members.add(member);
        }

        return members.iterator();
    }

    @Override
    public boolean hasEmail(Group group) {
        return liferayUserGroupDelegate.hasEmail(group.getId());
    }
}
