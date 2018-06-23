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
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ail.core.Attribute;
import com.ail.core.Type;
import com.ail.financial.CurrencyAmount;
import com.ail.pageflow.render.RenderService.RenderArgument;

/**
 * <p>An Answer simply displays the answer given to a previous asked question. An {@link AnswerSection} is used to
 * group a number of Answers together. These elements are generally used as part of a summary screen.</p>
 * <p><img src="doc-files/Answer.png"/></p>
 * <p>The screenshot shows an Answer in the context of an {@link AnswerSection} which contains five Answers in all.</p>
 * <p>An Answer displays a title and answer. The title is defined statically, using the {@link #getTitle() title}
 * property; but may include dynamic references into the model in the form "${xpath}". The XPath expression(s) defined
 * are evaluated against the quotation object if they are absolute (i.e. begin with a /) or relative to the binding if
 * they begin "./". Evaluations are performed at page render time. The answer itself is the result of evaluating
 * the XPath expression defined by {@link PageElement#getBinding() binding} against the quotation object.</p>
 * @see AnswerScroller
 * @version 1.1
 */
public class Answer extends PageElement {
    private static final long serialVersionUID = -1048535311696230109L;

    public Answer() {
    }

    public String formattedAnswer(RenderArgument args) {
        Object answer = fetchBoundObject(args.getModelArgRet(), "");

        if (answer == null) {
            return "";
        }

        if (answer instanceof String) {
            return (String)answer;
        }
        else if (answer instanceof Date) {
            SimpleDateFormat dateFormat=new SimpleDateFormat("d MMMMM, yyyy");
            return dateFormat.format((Date)answer);
        }
        else if (answer instanceof com.ail.core.Attribute) {
        	Attribute a=(Attribute)answer;
        	if (a.isYesornoType() || a.isChoiceType()) {
       			return i18n(a.getValue());
        	}
        	else {
        	    String val = ((Attribute)answer).getFormattedValue();
        	    return val == null ? "" : val;
        	}
        }
        else if (answer instanceof CurrencyAmount) {
        	return ((CurrencyAmount)answer).toFormattedString();
        }
        else {
            return answer.toString();
        }
    }

    public String dataType(RenderArgument args) {
        Object answer = fetchBoundObject(args.getModelArgRet(), "");

        if (answer instanceof String) {
            return "string";
        }
        else if (answer instanceof Date) {
            return "date";
        }
        else if (answer instanceof com.ail.core.Attribute) {
            Attribute a=(Attribute)answer;
            return a.getFormatType();
        }
        else if (answer instanceof CurrencyAmount) {
            return "currency";
        }
        else {
            return "string";
        }
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("Answer", model);
    }
}
