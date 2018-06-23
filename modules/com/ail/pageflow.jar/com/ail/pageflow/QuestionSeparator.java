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
 * <p>The QuestionSeparator is used to break up long list of questions with either a title, or simple white space.</p>
 * <p><img src="doc-files/QuestionSeparator.png"/></p>
 * <p>In the example above, a page of conditions has been broken into three blocks to make it more readable. Three
 * QuestionSeparators are used, two with titles one without.</p>
 * @see com.ail.pageflow.Question
 */
public class QuestionSeparator extends Question {
    private static final long serialVersionUID = 7118438575837087257L;

    public QuestionSeparator() {
		super();
	}

    @Override
    public Type applyRequestValues(Type model) {
    	return model;
    }

    @Override
    public boolean processValidations(Type model) {
        return false;
    }

    @Override
    public Type renderPageHeader(Type model) throws IllegalStateException, IOException {
        return model;
    }

	@Override
    public Type renderResponse(Type model, String rowContext) throws IllegalStateException, IOException {
	    return executeTemplateCommand("QuestionSeparator", model);
	}
}
