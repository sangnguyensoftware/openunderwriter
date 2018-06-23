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
package com.ail.core.command;

import com.ail.core.BaseError;

/**
 * This exception is thrown by the Velocity Accessor in response to exceptions
 * thrown by Velocity itself. It acts as a simple wrapper.
 */
public class VelocityServiceError extends BaseError {
    StringBuffer binding = new StringBuffer();

    public VelocityServiceError(String description) {
        super(description);
    }

    public VelocityServiceError(Throwable e) {
        super(e.toString(), e);
    }

    public VelocityServiceError(String description, Throwable t) {
        super(description, t);
    }

    public void addBinding(String binding) {
        if (binding!=null) {
            if (this.binding.length() == 0) {
                this.binding.append(binding);
            } else {
                this.binding.insert(0, binding+"/");
            }
        }
    }

    @Override
    public String getDescription() {
        if (binding.length() != 0) {
            return super.getDescription() + " binding; " + binding;
        } else {
            return super.getDescription();
        }
    }

}
