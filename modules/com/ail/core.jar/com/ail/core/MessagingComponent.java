/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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

import java.security.Principal;

import javax.ejb.MessageDrivenContext;

/**
 * This class is use as a superclass by all message bean components. 
 */
public abstract class MessagingComponent extends EJBComponent {
    private MessageDrivenContext ctx;
    
    public void setSessionContext(MessageDrivenContext context) {
        ctx = context;
    }

    public MessageDrivenContext getSessionContext() {
        return ctx;
    }
    
    public Principal getSecurityPrincipal() {
        return ctx==null ? null : ctx.getCallerPrincipal();
    }
}
