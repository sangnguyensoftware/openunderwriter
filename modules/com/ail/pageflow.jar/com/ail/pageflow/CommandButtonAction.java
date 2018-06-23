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

import com.ail.core.BaseException;
import com.ail.core.Type;
import com.ail.pageflow.render.RenderService.RenderArgument;

/**
 * <p>This page element renders itself as a button, typically within a {@link NavigationSection}. When clicked the
 * button forwards to another page in {@link PageFlow} and optionally runs any number of {@link Action Actions}
 * in the process allowing commands to executed between pages.</p>
 * <p><img src="doc-files/CommandButtonAction.png"/></p>
 * <p>The {@link #isImmediate() immediate} flag provides control over the effects of validation on page forwarding.
 * By default (if immediate is false), validation errors prevent the forward from being processed and so enforces
 * the rule that pages must be complete and valid before the user moves on. If set to true the page forward is
 * processed regardless of validation errors - validation is still performed but errors do not prevent forwarding.</p>
 * <p>The screenshot shows two CommandButtonAction elements in the context of a {@link AbstractPage}. In this case it is likely
 * that the 'Previous' button would have immediate set to true - so the user can return to the previous page without
 * completing this one.</p>
 * <p>Note: The Quit button is also a CommandButtonAction. It is an implicit part of {@link NavigationSection}. By default
 * it appears on all pages and when clicked closes the current session and forwards the user back to the product
 * home page. It can be disabled using {@link NavigationSection#getQuitButton() NavigationSection.quitButton}.</p>
 * @see NavigationSection
 * @see AbstractPage
 * @see PageFlow
 */
public class CommandButtonAction extends PageElement {
    private static final long serialVersionUID = 7575333161831400599L;
    /** Label to appear on the button */
    private String label;

    /** Id of another page in the same {@link PageFlow} to forward to. */
    private String destinationPageId;

    /** Name of a different pageflow to forward to. */
    private String destinationPageFlow;

    /** Forward regardless of page errors */
    private boolean immediate=false;

    /** Optional view template */
    protected String template;

    /**
     * Label to appear on the button.
     * @return Label text
     */
    public String getLabel() {
        return label;
    }

    /**
     * @see #getLabel()
     * @param label Label text
     */
    public void setLabel(String label) {
        this.label = label;
    }

    public String formattedLabel(RenderArgument args) {
        if (getLabel()!=null) {
            return i18n(expand(getLabel(), args.getPolicyArg(), args.getModelArgRet()));
        }
        else {
            return null;
        }
    }

    /**
     * Id of another page in the same {@link PageFlow} to forward to.
     * @return Page Id
     */
    public String getDestinationPageId() {
        return destinationPageId;
    }

    /**
     * @see #getDestinationPageId()
     * @param destinationPageId
     */
    public void setDestinationPageId(String destinationPageId) {
        this.destinationPageId = destinationPageId;
    }

    public String getDestinationPageFlow() {
        return destinationPageFlow;
    }

    public void setDestinationPageFlow(String destinationPageFlow) {
        this.destinationPageFlow = destinationPageFlow;
    }

    /**
     * Get view template if set
     * @return
     */
    public String getTemplate() {
        return template;
    }

    /**
     * Set a view template
     * @param template
     */
    public void setTemplate(String template) {
        this.template = template;
    }

    /**
     * If immediate is set to true the forward action will be forced even if validation
     * errors are found on the page. By default, any validation error on the page will
     * prevent the forward action.
     * @return true if forwarding is to be immediate, false otherwise.
     */
    public boolean isImmediate() {
        return immediate;
    }

    /**
     * @see #isImmediate()
     * @param immediate
     */
    public void setImmediate(boolean immediate) {
        this.immediate = immediate;
    }

    @Override
    public void onStartProcessAction(Type model) {
        if (buttonPressed()) {
            super.onStartProcessAction(model);
        }
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        if (buttonPressed()) {
            PageFlowContext.getPageFlow().setNextPage(destinationPageId);
            model = super.processActions(model);
            PageFlowContext.flagActionAsProcessed();
        }
        return model;
    }

    protected boolean buttonPressed() {
        String op = PageFlowContext.getRequestedOperation();
        return op != null && op.equals(label);
    }

    @Override
    public Type applyRequestValues(Type model) {
        // do nothing
    	return model;
    }

    @Override
    public boolean processValidations(Type model) {
        boolean error=false;

        if (buttonPressed()) {
        	for(Action a: getAction()) {
                error |= a.processValidations(model);
            }
        }
        return error;
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        super.renderResponse(model);
        return executeTemplateCommand(template == null ? "CommandButtonAction" : template, model);
    }
}
