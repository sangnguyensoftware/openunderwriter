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
import java.util.ArrayList;

import com.ail.core.Type;

/**
 * <p>An AnswerScroller displays a repeating pattern of answers. It is generally used on summary screens within an 
 * {@link AnswerSection} to output blocks of repeating answers.</p>
 * <p><img src="doc-files/AnswerScroller.png"/></p>
 * <p>In the above example the AnswerScroller is {@link #getBinding() bound} to assets containing vehicle information.
 * Two assets match the binding's criteria (an ALFA ROMEO and a BENTLEY). The AnswerScroller contains a list of 
 * {@link #getAnswer() answers} (in this example: Make, Model and Registration).</p>
 * The binding's of the Answers are evaluated relative to the binding of the AnswerScoller itself. For example, if the 
 * AnswerScroller's binding was: <code>/asset[assetTypeId='Vehicle']</code>, the Answers would be bound to <code>
 * attribute[id='Make']</code>, <code>attribute[id='Model']</code> and <code>attribute[id='Registration']</code>. So 
 * for each element returned by the AnswerScroller's binding, a section is rendered containing the Answers relative 
 * to it</p>
 * @see Answer
 * @see AnswerSection
 */
public class AnswerScroller extends Answer {
	private static final long serialVersionUID = -6043887157243002172L;

    /**
     * A list of answers to be rendered in this scroller. These are rendered once for each of the
     * elements returned by {@link #getBinding()} and are evaluated relative to it.
     */
    private ArrayList<Answer> answer;
    
    public AnswerScroller() {
        answer=new ArrayList<Answer>();
    }
    
    /**
    * A list of answers to be rendered in this scroller. These are rendered once for each of the
    * elements returned by {@link #getBinding()} and are evaluated relative to it.
    * @return The list of Answers associated with this scroller
    */
    public ArrayList<Answer> getAnswer() {
        return answer;
    }

    /**
     * @see #getAnswer()
     * @param answer
     */
    public void setAnswer(ArrayList<Answer> answer) {
        this.answer = answer;
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
   	    return executeTemplateCommand("AnswerScroller", model);
    }
    
    @Override
    public void applyElementId(String basedId) {
        int idx=0;
        for(PageElement e: answer) {
            e.applyElementId(basedId+ID_SEPARATOR+(idx++));
        }
        super.applyElementId(basedId);
    }
}

