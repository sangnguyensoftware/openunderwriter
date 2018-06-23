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
package com.ail.pageflow;

import java.io.IOException;

import com.ail.core.Type;

/**
 * An information page typically contains more information than question/answer elements (which are better housed in a
 * {@link QuestionPage}. There is no reason why an InformationPage should contain a &lt;form/&gt; (or many forms), but this
 * class doesn't create a &lt;form/&gt; itself, so any contained elements that need a form must render their own.
 * @deprecated Use {@link com.ail.pageflow.Page} instead.
 */
@Deprecated
public class InformationPage extends AbstractPage {
	private static final long serialVersionUID = -3356193978041821888L;

	public InformationPage() {
		super();
	}

	@Override
	public Type renderResponse(Type model) throws IllegalStateException, IOException {
        super.renderResponse(model);

        super.renderPageHeader(model);

        return executeTemplateCommand("InformationPage", model);
    }
}
