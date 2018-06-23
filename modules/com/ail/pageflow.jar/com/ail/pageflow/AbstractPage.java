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
import java.util.List;

import com.ail.core.Type;

/**
 * An abstract UI element providing default handler methods common to its
 * concrete sub-classes.
 */
public abstract class AbstractPage extends PageContainer {
    private static final long serialVersionUID = 297215265083279666L;

    /**
     * Stage allows pages to be grouped. For exampled, all the pages related to
     * payment processing might be put into the "Payment" stage simply by giving
     * them all the value "Payment" in this property.
     */
    private String stage;

    public AbstractPage() {
        super();
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public List<String> getStages() {
        return PageFlowContext.getPageFlow().getStages();
    }

    public boolean isProgressBarEnabled() {
        return PageFlowContext.getPageFlow().isProgressBar();
    }

    @Override
    public void onStartProcessAction(Type model) {
        for (Action a : getAction()) {
            model = a.executeAction(model, ActionType.ON_START_PROCESS_ACTION);
        }
        super.onStartProcessAction(model);
    }

    @Override
    public boolean processValidations(Type model) {
        for (Action a : getAction()) {
            model = a.executeAction(model, ActionType.ON_PROCESS_VALIDATIONS);
        }
        return super.processValidations(model);
    }

    @Override
    public Type applyRequestValues(Type model) {
        for (Action a : getAction()) {
            model = a.executeAction(model, ActionType.ON_BEFORE_APPLY_REQUEST_VALUES);
        }
        model = super.applyRequestValues(model);
        for (Action a : getAction()) {
            model = a.executeAction(model, ActionType.ON_APPLY_REQUEST_VALUES);
        }
        return model;
    }

    @Override
    public Type renderPageFooter(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("PageFooter", model);
    }

    @Override
    public Type renderPageHeader(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("PageHeader", model);
    }
}
