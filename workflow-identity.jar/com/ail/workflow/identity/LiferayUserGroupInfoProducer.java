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

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.inject.Alternative;
import javax.enterprise.inject.Produces;

import org.jbpm.kie.services.cdi.producer.UserGroupInfoProducer;
import org.jbpm.shared.services.cdi.Selectable;
import org.kie.api.task.UserGroupCallback;
import org.kie.internal.task.api.UserInfo;

import com.ail.workflow.LiferayUserGroupDelegate;

@ApplicationScoped
@Alternative
@Selectable
public class LiferayUserGroupInfoProducer implements UserGroupInfoProducer {
    private UserGroupCallback callback;
    private UserInfo userInfo;
    private LiferayUserGroupDelegate liferayUserGroupDelegate;

    public LiferayUserGroupInfoProducer() {
        liferayUserGroupDelegate = new LiferayUserGroupDelegate();
        callback = new LiferayUserGroupCallback(liferayUserGroupDelegate);
        userInfo = new LiferayUserInfo(liferayUserGroupDelegate);
    }

    @Override
    @Produces
    public UserGroupCallback produceCallback() {
        return callback;
    }

    @Override
    @Produces
    public UserInfo produceUserInfo() {
        return userInfo;
    }

}
