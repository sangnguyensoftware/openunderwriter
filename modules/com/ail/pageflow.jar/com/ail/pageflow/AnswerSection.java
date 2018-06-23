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
 * <p>An AnswerSection is generally used on summary pages and acts as a container for listing the answers
 * given to previously asked questions. It contains a list of {@link #getAnswer() answers} which may be
 * instances of {@link Answer} or {@link AnswerScroller}.</p>
 * <p><img src="doc-files/AnswerSection.png"/></p>
 * <p>The screenshot shows two answer sections. The first contains 5 {@link Answer Answers}; the second
 * contains a single {@link AnswerScroller} which itself contains 3 {@link Answer Answers}.</p>
 * <p>The {@link #getTitle() title} may be static (using {@link #setTitle(String)}, or dynamic (using {@link #setTitleBinding(String)}.
 * Dynamic titles are defined as XPath expressions which are evaluated at page render time against the quotation model. The
 * result of the evaluation is used as the title.
 * @see Answer
 * @see AnswerScroller
 * @version 1.1
 */
public class AnswerSection extends PageContainer {
	private static final long serialVersionUID = 6794522768423045427L;

    public AnswerSection() {
        super();
    }

    @Override
	public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("AnswerSection", model);
	}
}
