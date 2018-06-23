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

package com.ail.core.key;

import java.util.List;

import org.hibernate.LockMode;
import org.hibernate.Query;
import org.hibernate.Session;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.CoreContext;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.key.GenerateUniqueKeyService.GenerateUniqueKeyArgument;
import com.ail.core.persistence.hibernate.HibernateSessionBuilder;

@ServiceImplementation
public class TableKeyGeneratorService extends Service<GenerateUniqueKeyArgument> {
    private KeyValueGenerator keyValueGenerator;

    public TableKeyGeneratorService() {
        keyValueGenerator = new KeyValueGenerator();
    }

    @Override
    public void invoke() throws PreconditionException, PostconditionException {
        String keyIdParamName = "KeyGenerators." + args.getKeyIdArg() + ".ID";

        String generatorId = CoreContext.getCoreProxy().getParameterValue(keyIdParamName, null);

        if (generatorId == null) {
            throw new PreconditionException(keyIdParamName + " not defined.");
        }

        UniqueKey key = fetchKey(generatorId);

        if (key == null) {
            key = new UniqueKey(generatorId, keyValueGenerator.fetchMinValue(args.getKeyIdArg()));
        } else {
            key.setValue(keyValueGenerator.fetchNextValue(args.getKeyIdArg(), key.getValue()));
        }

        HibernateSessionBuilder.getSessionFactory().getCurrentSession().saveOrUpdate(key);

        args.setKeyRet(key.getValue());
    }

    private UniqueKey fetchKey(String generatorId) {
        Session session = HibernateSessionBuilder.getSessionFactory().getCurrentSession();
        Query query = null;

        query = session.getNamedQuery("get.uniquekey.by.id");
        query.setParameter(0, generatorId);
        query.setLockMode("key", LockMode.PESSIMISTIC_WRITE);

        @SuppressWarnings("unchecked")
        List<UniqueKey> keys = query.list();
        if (keys.size() == 1) {
            return keys.get(0);
        } else {
            return null;
        }
    }
}