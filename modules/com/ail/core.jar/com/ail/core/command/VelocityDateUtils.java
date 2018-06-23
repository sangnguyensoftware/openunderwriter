/* Copyright Applied Industrial Logic Limited 2006. All rights Reserved */
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

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class defines a collection of date related functions which are useful in
 * velocity templates. An instance of this class is automatically passed into
 * every velocity context created by the
 * {@link com.ail.core.command.VelocityAccessor VelocityAccessor} and is
 * accessible with templates using the <code>$date</code> velocity variable.
 */
public class VelocityDateUtils {
    public VelocityDateUtils() {
    }

    public String format(String format, Date date) {
        return new SimpleDateFormat(format).format(date);
    }

    public Date currentDate() {
        return new Date();
    }
}
