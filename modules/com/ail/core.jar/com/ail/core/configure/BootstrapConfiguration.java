/* Copyright Applied Industrial Logic Limited 2016. All rights reserved. */
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
package com.ail.core.configure;

import com.ail.core.command.ClassAccessor;
import com.ail.core.factory.ClassFactory;
import com.ail.core.xmlbinding.FromXMLService.FromXMLCommand;
import com.ail.core.xmlbinding.castor.CastorFromXMLService;

/**
 * The Bootstrap configuration defined by this class underlies all other
 * configuration namespaces (including com.ail.core). It is used to define
 * fundamental services/types which the system needs in order to successfully
 * boot up.
 */
public class BootstrapConfiguration {
    private static Configuration bootstrap;

    static {
        bootstrap = new Configuration();

        bootstrap.setName("Bootstrap configuration");
        bootstrap.setVersion("1.0");
        bootstrap.setNamespace(BootstrapConfiguration.class.getName());

        Builders blds=new Builders();
        bootstrap.setBuilders(blds);

        Builder b=new Builder();
        blds.addBuilder(b);
        b.setName("ClassBuilder");
        b.setFactory(ClassFactory.class.getName());

        Types ts=new Types();
        bootstrap.setTypes(ts);
        com.ail.core.configure.Type t=new Type();
        ts.addType(t);
        t.setName(CastorFromXMLService.class.getName());
        t.setBuilder("ClassBuilder");
        t.setKey(ClassAccessor.class.getName());
        Parameter p=new Parameter();
        p.setName("ServiceClass");
        p.setValue(CastorFromXMLService.class.getName());
        t.addParameter(p);

        t=new Type();
        ts.addType(t);
        p=new Parameter();
        t.setName(FromXMLCommand.class.getName().replace('$', '.'));
        t.setBuilder("ClassBuilder");
        t.setKey("com.ail.core.xmlbinding.FromXMLCommandImpl");
        p.setName("Accessor");
        p.setValue(CastorFromXMLService.class.getName());
        t.addParameter(p);
    }

    static Configuration getInstance() {
        return bootstrap;
    }
}
