/* Copyright Applied Industrial Logic Limited 2014. All rights Reserved */
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

import com.ail.core.Functions;
import com.ail.core.TypeEnum;

public enum ActionType implements TypeEnum {

    /**
     * Occurs immediately the request comes in before anything else is done. These events are fired
     * regardless of any validation issues that might exist on the page. This is
     * invoked during the portlet's processAction cycle.
     */
    ON_START_PROCESS_ACTION("onStartProcessAction"),

    /**
     * Occurs immediately before request values are applied when a user submits a page. These events are fired
     * regardless of any validation issues that might exist on the page. This is
     * invoked during the portlet's processAction cycle.
     */
    ON_BEFORE_APPLY_REQUEST_VALUES("onBeforeApplyRequestValues"),

    /**
     * Occurs immediately after the user submits a page. These events are fired
     * regardless of any validation issues that might exist on the page. This is
     * invoked during the portlet's processAction cycle.
     */
    ON_APPLY_REQUEST_VALUES("onApplyRequestValues"),

    /**
     * This event occurs as part of the process validations stage after
     * onApplyRequestValues and before onProcessValidations. This is invoked
     * during the portlet's processAction cycle. A typical example of its use is
     * to support validation using Java service commands. This can be useful in
     * complex multi-field validation, or validation of a field's value using an
     * external system.
     */
    ON_PROCESS_VALIDATIONS("onProcessValidations"),

    /**
     * Fires after onApplyRequestValues and only if there are no validation
     * errors on the page, or the page has been submitted in "immediate" mode.
     * This is invoked during the portlet's processAction cycle.
     */
    ON_PROCESS_ACTIONS("onProcessActions"),

    /**
     * Invoked from the portlet's doView cycle. These actions are invoked during
     * the rendering of the page. These actions can be defined at both the page
     * and page flow levels. When defined at the page flow level they apply to
     * all pages.
     */
    ON_RENDER_RESPONSE("onRenderResponse"),

    /**
     * These actions are fired on entry to a page and may be defined either
     * within a page, or within the page flow. When defined at the page level
     * they are only fired for that page, when defined at the page flow level
     * they apply to all pages. Note that these events will not fire if a page
     * forwards to itself, the are only fired on page change. They are fired
     * from within the portlet's doView cycle.
     */
    ON_PAGE_ENTRY("onPageEntry"),

    /**
     * These actions are fired on exit from a page and may be defined either
     * within a page, or within the page flow. When defined at the page level
     * they are only fired for that page, when defined at the page flow level
     * they apply to all pages. Note that these events will not fire if a page
     * forwards to itself, the are only fired on page change. They are fired
     * from within the portlet's processAction cycle.
     */
    ON_PAGE_EXIT("onPageExit"),

    /**
     * These actions are defined at the page flow level and will be fired when
     * the pageflow first loads. They are fired from within the portlet's doView
     * cycle.
     */
    ON_PAGE_FLOW_ENTRY("onPageFlowEntry"),

    /**
     * onError actions defined at the page flow level will fire whenever the
     * page flow encounters a non-recoverable exception. These actions are
     * typically used to redirect the user to an error page.
     */
    ON_ERROR("onError");

    private final String longName;

    ActionType() {
        this.longName=name();
    }

    ActionType(String longName) {
        this.longName=longName;
    }

    public String valuesAsCsv() {
        return Functions.arrayAsCsv(values());
    }

    @Override
    public String longName() {
        return longName;
    }

    /**
     * This method is similar to the valueOf() method offered by Java's Enum type, but
     * in this case it will match either the Enum's name or the longName.
     * @param name The name to lookup
     * @return The matching Enum, or IllegalArgumentException if there isn't a match.
     */
    public static ActionType forName(String name) {
        return (ActionType)Functions.enumForName(name, values());
    }

    @Override
    public String getName() {
        return name();
    }

    @Override
    public String getLongName() {
        return longName;
    }

    @Override
    public int getOrdinal() {
        return ordinal();
    }

    @Override
    public String toString() {
        return longName;
    }
}
