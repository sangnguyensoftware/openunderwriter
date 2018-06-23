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

import com.ail.core.BaseException;
import com.ail.core.Type;

/**
 * <p>A QuestionSection renders itself as a section within a {@link AbstractPage Page} or other {@link PageContainer PageContainer}. 
 * The QuestionSection has a title, and itself contains any number of {@link Question Questions}.</p>
 * <p><img src="doc-files/QuestionSection.png"/></p>
 * There are two reason that you might consider using a QuestionSection: <ul>
 * <li>A page is too long and needs to be broken into parts - Breaking a long page of questions into sections,
 * and giving each of those sections a separate title helps usability.</li>
 * <li>Improved page layout - Each QuestionSection is rendered as it's own html table. Separating questions 
 * into separate sections therefore give the browser a better chance to optimise its use of space. This has
 * been used to good affect above. Had these two sections been rendered as one, the dropdowns would have all 
 * been aligned down the page making it look clumsy.</li></ul>
 * @see PageContainer
 * @see AbstractPage
 * @See Question
 */
public class QuestionSection extends PageElement {
	private static final long serialVersionUID = 6794522768423045427L;
    private ArrayList<? extends Question> question; 
    
    public QuestionSection() {
        super();
        question=new ArrayList<Question>();
    }

    /**
     * List of Questions which this section contains.
     * @return Question list of Questions
     */
    public ArrayList<? extends Question> getQuestion() {
        return question;
    }

    /**
     * @see #getQuestion()
     * @param question List of Questions
     */
    public void setQuestion(ArrayList<? extends Question> question) {
        this.question = question;
    }

    @Override
    public boolean processValidations(Type model) {
        boolean error=false;
        Type localModel = (getBinding()==null) ? model : model.xpathGet(getBinding(), Type.class);
        
        for (Question q : question) {
            error |= q.processValidations(localModel);
        }
        
        return error;
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        Type localModel = (getBinding()==null) ? model : model.xpathGet(getBinding(), Type.class);

        for (PageElement q : question) {
            q.processActions(localModel);
        }
        
        return model;
    }

    @Override
    public Type applyRequestValues(Type model) {
        Type localModel = (getBinding()==null) ? model : model.xpathGet(getBinding(), Type.class);
        
        for (Question q : question) {
            q.applyRequestValues(localModel);
        }
        
        return model;
    }

    @Override
	public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("QuestionSection", model);
	}

    @Override
    public Type renderPageHeader(Type model) throws IllegalStateException, IOException {
        Type localModel = (getBinding()==null) ? model : model.xpathGet(getBinding(), Type.class);

        for(Question q: question) {
            q.renderPageHeader(localModel);
        }
        
        return model;
    }

    @Override
    public void applyElementId(String basedId) {
    	int idx=0;
    	for(PageElement e: question) {
    		e.applyElementId(basedId+ID_SEPARATOR+(idx++));
    	}
    	super.applyElementId(basedId);
   	}
}
