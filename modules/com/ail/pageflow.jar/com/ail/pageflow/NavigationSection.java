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

/**
 * <p>A navigation section acts as a container for buttons like 'Next' and 'Previous' which are used to navigate through the
 * pageflow. By default navigation sections always contain a 'Quit' button - though this may be turned off
 * by setting the {@link #isQuitDisabled() quitDisabled} property to true.</p>
 * <p><img src="doc-files/NavigationSection.png"/></p>
 * <p>The screenshot shows a NavigationSection containing two {@link CommandButtonAction CommandButtonActions} (Previous and Next),
 * and in this case the quit button has been left enabled, which is the default.</p>
 * @see QuitButtonAction
 * @see CommandButtonAction
 */
public class NavigationSection extends PageContainer {
    private static final long serialVersionUID = 6794522768423045427L;
    boolean quitDisabled = false;
    private QuitButtonAction quitButton = new QuitButtonAction();

    /**
     * By default all NavigationSections include a Quit button. If this property is
     * set to true the quit button will not be included. Note: The quit button is an
     * instance of {@link QuitButtonAction}.
     *
     * @return True if the button is disabled, false otherwise.
     */
    public boolean isQuitDisabled() {
        return quitDisabled;
    }

    /**
     * @see #isQuitDisabled()
     * @param quitDisabled
     *            True if the button is to be disabled, false otherwise.
     */
    public void setQuitDisabled(boolean quitDisabled) {
        this.quitDisabled = quitDisabled;
    }

    /**
     * By default the Quit button is an instance of {@link QuitButtonAction} but
     * this may be overridden using this property.
     *
     * @return Quit action
     */
    public QuitButtonAction getQuitButton() {
        return quitButton;
    }

    /**
     * @see #getQuitButton()
     * @param quitButton
     *            Quit action
     */
    public void setQuitButton(QuitButtonAction quitButton) {
        this.quitButton = quitButton;
    }

    @Override
    public void onStartProcessAction(Type model) {
        for (PageElement element : getPageElement()) {
            element.onStartProcessAction(model);
        }
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        if (!isQuitDisabled()) {
            model = quitButton.processActions(model);
        }

        for (PageElement element : getPageElement()) {
            model = element.processActions(model);
        }

        return model;
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("NavigationSection", model);
    }

    @Override
    public Type applyRequestValues(Type model) {
        // Nothing to do here
        return model;
    }

    @Override
    public boolean processValidations(Type model) {
        // If our condition isn't met, validate nothing.
        if (!conditionIsMet(model)) {
            return false;
        }

        boolean error = false;

        for (PageElement element : getPageElement()) {
            error |= element.processValidations(model);
        }

        return error;
    }
}
