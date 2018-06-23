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

import static com.ail.pageflow.ActionType.ON_PAGE_ENTRY;
import static com.ail.pageflow.ActionType.ON_PAGE_EXIT;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ail.core.BaseException;
import com.ail.core.Type;
import com.ail.pageflow.util.OrderedList;

/**
 * The PageFlow class is the root of the OpenUnderwriter's product UI concept. It
 * contains a collection of pages and implements life-cycle methods which simply
 * delegate to the "current" page.<br/>
 * In all cases the page flow assumes that the model it is processing is a
 * {@link com.ail.openquote.Policy Policy}. The name of the "current" page is
 * indicated by the Policy's {@link com.ail.openquote.Policy#getPages() page}
 * property.
 *
 * @see com.ail.insurance.Policy
 * @see com.ail.pageflow.portlet.PageFlowPortlet
 * @see com.ail.pageflow.portlet.SandpitPortlet
 */
public class PageFlow extends PageElement {
    private static final long serialVersionUID = -3699440857377974385L;

    public static final String QUOTATION_PAGE_FLOW_NAME = "QuotationPageFlow";

    /** List of pages which are part of this flow */
    private OrderedList<AbstractPage> pages;

    /** Optional definition of the page on which the quote process should start. */
    private String startPage = null;

    /** Cached stage list (derived from page.getStage) */
    transient private List<String> stages = null;

    /**
     * Set true when elements IDs have been applied so we can avoid applying
     * them twice.
     */
    private transient boolean appliedElementId = false;

    /**
     * When true pages are rendered with a progress bar if the current page defines
     * a {@link AbstractPage#getStage() stage}.
     */
    private boolean progressBar = false;

    /**
     * When true validation errors rollback the current transaction and so prevent error tags
     * from being persisted.
     */
    private boolean rollbackOnValidationFailure = false;

    /**
     * Default constructor
     */
    public PageFlow() {
        super();
        pages = new OrderedList<>();
    }

    /**
     * The list of pages associated with this PageFlow
     *
     * @return list of pages
     */
    public List<AbstractPage> getPages() {
        return pages;
    }

    /**
     * @see #getPages()
     * @param page
     *            List of pages
     */
    public void setPages(List<AbstractPage> page) {
        this.pages = new OrderedList<>(page);
    }

    /**
     * Get the name of the page on which the quote process should start. This
     * can also be defined in the Policy's XML. If defined in both places, the
     * setting here takes precedence.
     *
     * @return Name of the page to start the process on.
     */
    public String getStartPage() {
        return startPage;
    }

    /**
     * @see #getStartPage()
     * @param startPage
     */
    public void setStartPage(String startPage) {
        this.startPage = startPage;
    }

    @Override
    public void onStartProcessAction(Type model) {
        String targetPage = targetPage();

        for (AbstractPage p : pages) {
            if (p.getId().equals(targetPage)) {
                p.onStartProcessAction(model);
                break;
            }
        }
    }

    @Override
    public Type applyRequestValues(Type model) {
        String targetPage = targetPage();

        for (AbstractPage p : pages) {
            if (p.getId().equals(targetPage)) {
                model = p.applyRequestValues(model);
                break;
            }
        }
        return model;
    }

    @Override
    public boolean processValidations(Type model) {
        boolean errors = false;

        String targetPage = targetPage();

        for (AbstractPage p : pages) {
            if (p.getId().equals(targetPage)) {
                errors |= p.processValidations(model);
                break;
            }
        }

        PageFlowContext.getResponseWrapper().setValidationErrorsFound(errors);

        return errors;
    }

    @Override
    public Type processActions(Type model) throws BaseException {

        super.processActions(model);

        String targetPage = targetPage();

        for (AbstractPage p : pages) {
            if (p.getId().equals(targetPage)) {
                model = p.processActions(model);
                break;
            }
        }

        // Process request related actions
        for (Action action : getAction()) {
            model = action.processActions(model);
        }

        return model;
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        String targetPage;

        // If we're coming in for the first time, execute the ON_PAGE_FLOW_ENTRY
        // actions
        if (isEnteringPageFlow()) {
            for (Action action : getAction()) {
                model = action.executeAction(model, ActionType.ON_PAGE_FLOW_ENTRY);
            }

            PageFlowContext.setPageFlowInitliased(true);
        }

        // Execute the render response page flow actions
        for (Action action : getAction()) {
            model = action.renderResponse(model);
        }

        if (isAdvancingPage()) {
            executePageActions(ON_PAGE_EXIT, getCurrentPage(), model);
            executePageActions(ON_PAGE_ENTRY, getNextPage(), model);
            targetPage = getNextPage();
        } else {
            targetPage = getCurrentPage();
        }

        for (AbstractPage p : pages) {
            if (p.getId().equals(targetPage)) {
                model = p.renderResponse(model);
                break;
            }
        }

        if (isAdvancingPage()) {
            advancePage();
        }

        return model;
    }

    private void executePageActions(ActionType when, String targetPage, Type model) {
        for (AbstractPage p : pages) {
            if (p.getId().equals(targetPage)) {
                for(Action a: p.getAction()) {
                    try {
                        a.processActions(model);
                    } catch (BaseException e) {
                        throw new RenderingError(e);
                    }
                }
                break;
            }
        }
    }

    @Override
    public void applyElementId(String baseId) {
        if (!appliedElementId) {
            int idx = -0;
            for (AbstractPage p : pages) {
                p.applyElementId(baseId + ID_SEPARATOR + (idx++));
            }
            appliedElementId = true;
        }
    }

    private String targetPage() {
        return (getCurrentPage() != null) ? getCurrentPage() : getStartPage();
    }

    public String getCurrentPage() {
        return PageFlowContext.getCurrentPageName();
    }

    public void setCurrentPage(String pageName) {
        PageFlowContext.setCurrentPageName(pageName);
    }

    public String getNextPage() {
        return PageFlowContext.getNextPageName();
    }

    public void setNextPage(String pageName) {
        PageFlowContext.setNextPageName(pageName);
    }

    public boolean isProgressBar() {
        return progressBar;
    }

    public void setProgressBar(boolean progressBar) {
        this.progressBar = progressBar;
    }

    public boolean isRollbackOnValidationFailure() {
        return rollbackOnValidationFailure;
    }

    public void setRollbackOnValidationFailure(boolean rollbackOnValidationError) {
        this.rollbackOnValidationFailure = rollbackOnValidationError;
    }

    public boolean isEnteringPageFlow() {
        return !PageFlowContext.isPageFlowInitialised();
    }

    public boolean isAdvancingPage() {
        return getNextPage()!=null && !getNextPage().equals(getCurrentPage());
    }

    public void advancePage() {
        setCurrentPage(getNextPage());
        setNextPage(null);

        if (PageFlowContext.getPolicy()!=null) {
            String pageFlowName=PageFlowContext.getPageFlowName();
            PageFlowContext.getPolicy().setPage(pageFlowName, getCurrentPage());
        }
    }

    public List<String> getStages() {
        if (stages == null) {
            stages = new ArrayList<>();

            // The same stage name may appear in both i18n form and non-i18n form - take care that
            // no matter which forms it appears in it is only added to the stages list once.
            for (AbstractPage p : pages) {
                String stageName = p.getStage();
                if (stageName != null && stageName.length() != 0) {
                    if (!stages.contains(stageName) && !stages.contains(i18n(stageName))) {
                        stages.add(stageName);
                    }
                }
            }
        }

        return stages;
    }
}
