/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
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
package com.ail.ui.client.search;

import com.ail.ui.client.common.i18n.Messages;
import com.ail.ui.shared.viewer.ViewerCommand;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;

public abstract class BaseSearch implements EntryPoint {

    protected final Messages messages = GWT.create(Messages.class);

    protected ViewerCommand.ViewerId mode;

    protected final SearchServiceAsync searchService = GWT.create(SearchService.class);





    protected void doOnLoad(ViewerCommand.ViewerId mode) {

        this.mode = mode;

    }



}
