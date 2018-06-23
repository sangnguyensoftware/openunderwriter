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

import static com.ail.pageflow.PageFlowContext.getOperationParameters;
import static com.ail.pageflow.PageFlowContext.getSubmitEnabled;
import static com.ail.pageflow.PageFlowContext.getSubmitMode;
import static com.ail.pageflow.PageFlowContext.setSubmitEnabled;
import static com.ail.pageflow.portlet.SubmitMode.PAGE;

import java.io.IOException;

import com.ail.core.BaseException;
import com.ail.core.Type;

/**
 * A PageSection renders as a section on the page containing a number of columns (1 by default). The section may
 * contain any number of sub-elements. A section may optionally define a title to be rendered above the
 * section itself.
 */
public class PageSection extends PageContainer {
	private static final long serialVersionUID = 6794522768423045427L;

    /** Number of columns to be rendered */
    private int columns=1;

    /**
     * The number of columns to be rendered in this section. This defaults to 1.
     * @return number of columns
     */
    public int getColumns() {
        return columns;
    }

    /**
     * @see #getColumns()
     * @param columns Number of columns to render.
     */
    public void setColumns(int columns) {
        this.columns = columns;
    }

    @Override
    public Type applyRequestValues(Type model) {
        if (getSubmitMode() == PAGE) {
            return super.applyRequestValues(model);
        }
        else {
            boolean isTargetOfSubmit = isTargetOfSubmit();
            Type ret = model;

            if (isTargetOfSubmit) {
                setSubmitEnabled(true);
            }

            if (getSubmitEnabled()) {
                ret = super.applyRequestValues(model);
            }
            else {
                Type localModel = (Type) fetchBoundObject(model, model);

                for (PageElement e : super.getPageElement()) {
                    if (e instanceof PageSection) {
                        localModel = e.applyRequestValues(localModel);
                    }
                }
            }

            if (isTargetOfSubmit) {
                setSubmitEnabled(false);
            }

            return ret;
        }
    }

    boolean isTargetOfSubmit() {
        return !getSubmitEnabled() && getId()!=null && getId().equals(getOperationParameters().getProperty("submitTarget"));
    }

    @Override
    public boolean processValidations(Type model) {
        if (getSubmitMode() == PAGE) {
            return super.processValidations(model);
        }
        else {
            boolean isTargetOfSubmit = isTargetOfSubmit();
            Boolean ret = false;

            if (isTargetOfSubmit) {
                setSubmitEnabled(true);
            }

            if (getSubmitEnabled()) {
                ret = super.processValidations(model);
            }
            else {
                Type localModel = (Type)fetchBoundObject(model, model);

                if (conditionIsMet(localModel)) {
                    for (PageElement e : getPageElement()) {
                        if (e instanceof PageSection) {
                            ret |= e.processValidations(localModel);
                        }
                    }
                }
            }

            if (isTargetOfSubmit) {
                setSubmitEnabled(false);
            }

            return ret;
        }
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        if (getSubmitMode() == PAGE) {
            return super.processActions(model);
        }
        else {
            boolean isTargetOfSubmit = isTargetOfSubmit();
            Type ret = model;

            if (isTargetOfSubmit) {
                setSubmitEnabled(true);
            }

            if (getSubmitEnabled()) {
                model = super.processActions(model);
            }
            else {
                Type localModel = (Type)fetchBoundObject(model, model);

                if (conditionIsMet(localModel)) {

                    for (PageElement e : getPageElement()) {
                        if (e instanceof PageSection) {
                            model=e.processActions(localModel);
                        }
                    }

                    if (PageFlowContext.getPageFlow().isAdvancingPage()) {
                        for(Action a: getAction()) {
                            localModel=a.executeAction(localModel, ActionType.ON_PAGE_EXIT);
                        }
                    }
                }
            }

            if (isTargetOfSubmit) {
                setSubmitEnabled(false);
            }

            return ret;
        }
    }

    @Override
	public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("PageSection", model);
    }

    @Override
    public Type renderPageHeader(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("PageSectionHeader", model);
    }
}
