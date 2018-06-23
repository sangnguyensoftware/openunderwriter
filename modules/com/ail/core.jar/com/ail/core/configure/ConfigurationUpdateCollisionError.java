/* Copyright Applied Industrial Logic Limited 2003. All rights Reserved */
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

import com.ail.core.BaseError;

/**
 * Thrown when a configuration update collision is detected. An instance of this exception
 * is thrown in the following situation:<ol>
 * <li>User A reads configuration.</li>
 * <li>User B reads configuration.</li>
 * <li>User A modifies their in memory configuration, and saves.</li>
 * <li>User B modifies their in memory configuration, and attempts to save. <b>*Error thrown here*</b></li>
 * </ol>
 */
public class ConfigurationUpdateCollisionError extends BaseError {
    public ConfigurationUpdateCollisionError(String namespace) {
        super("Update collision for namespace:"+namespace);
    }
}
