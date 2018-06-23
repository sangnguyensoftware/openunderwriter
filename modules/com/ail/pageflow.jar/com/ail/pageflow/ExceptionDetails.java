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
package com.ail.pageflow;

import static ch.lambdaj.Lambda.DESCENDING;
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

import java.io.IOException;
import java.util.List;

import com.ail.core.ExceptionRecord;
import com.ail.core.Type;

/**
 * Page element to display the contents of a quotation's exception records.
 * Exceptions (Java exceptions) the are generated during the processing of a
 * policy are attached to it. This allows administrators and developers to
 * see both the exception and the data that caused it, making debugging
 * the issues much easier.
 */
public class ExceptionDetails extends PageElement {
	private static final long serialVersionUID = -4810599045554021748L;

	public ExceptionDetails() {
		super();
	}

    public List<ExceptionRecord> orderLines(List<ExceptionRecord> lines) {
        return sort(lines, on(ExceptionRecord.class).getDate(), DESCENDING);
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("ExceptionDetails", model);
    }
}
