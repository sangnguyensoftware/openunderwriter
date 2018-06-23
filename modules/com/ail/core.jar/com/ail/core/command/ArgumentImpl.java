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

package com.ail.core.command;

import com.ail.core.CoreUser;
import com.ail.core.Type;

/**
 * Parent class of all command args. This provides access to the caller's
 * instance of Core. This is useful if the command user (i.e. a service) needs
 * to see the world as the client does - perhaps to use their logging settings,
 * or get their effective date, or security Subject.
 */
public class ArgumentImpl extends Type implements Argument {
    private CoreUser callersCore = null;

    public CoreUser getCallersCore() {
        return callersCore;
    }

    public void setCallersCore(CoreUser callersCore) {
        this.callersCore = callersCore;
    }
}
