/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
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
package com.ail.ui.client.common.component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ail.ui.client.common.UIUtil;
import com.ail.ui.client.common.i18n.Messages;
import com.ail.ui.shared.validation.FieldVerifier;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.HasName;
import com.google.gwt.user.client.ui.ListBox;

public class AilSimpleListBox extends ListBox implements SessionState, Resetable, HasName {


    protected final Messages messages = GWT.create(Messages.class);

    private String stateId = "";

    private String defaultState = "";

    /**
     * Constructor
     * Use if list is dynamic
     */
    public AilSimpleListBox() {
        this("");
    }

    /**
     * Constructor - Use if session state is required
     * @param stateId
     */
    public AilSimpleListBox(String stateId) {
        this.stateId = stateId;
        setModel(UIUtil.toListArray(messages.loading()));
    }

    public AilSimpleListBox(String stateId, boolean multiSelect) {
        super(multiSelect);
        this.stateId = stateId;
    }

    /**
     * Constructor
     * Use if list is static
     * @param model
     */
    public AilSimpleListBox(List<String> model) {
        this(model, false);
    }

    /**
     * Constructor - Use if session state is required
     * @param stateId
     * @param model
     */
    public AilSimpleListBox(String stateId, List<String> model) {
        this(model, false);
        this.stateId = stateId;
    }

    /**
     * Internal constructor only - use AilSimpleMultiSelectListBox for multi-select
     * @param model
     * @param multiSelect
     */
    protected AilSimpleListBox(List<String> model, boolean multiSelect) {
        super(multiSelect);
        setModel(model);
    }

    /**
     * Internal constructor only - use AilSimpleMultiSelectListBox for multi-select
     * @param model
     * @param multiSelect
     */
    public AilSimpleListBox(String stateId, List<String> model, boolean multiSelect) {
        super(multiSelect);
        this.stateId = stateId;
        setModel(model);
    }

    /**
     * Internal constructor only - use AilSimpleMultiSelectListBox for multi-select
     * @param model
     */
    protected AilSimpleListBox(boolean multiSelect) {
        super(multiSelect);
        setModel(UIUtil.toListArray(messages.loading()));
    }

    public void setModel(List<String> newModel) {
        clear();

        if (!isMultipleSelect()) {
            if (!newModel.isEmpty()) {
                if (isSelectItem(newModel.get(0))) { // check if already has select message
                    addItem(messages.select());
                }
            }
        }

        for (String item : newModel) {
            addItem(item);
        }
    }

    public void setModel(Map<String,String> newModel) {
        clear();

        for (String i18nKey : newModel.keySet()) {
            addItem(newModel.get(i18nKey), i18nKey);
        }
    }

    /**
     * State to be set if none stored
     * @param defaultState
     */
    public void setDefaultState(String defaultState) {
        this.defaultState = defaultState;
    }

    /**
     * Return selected item or empty string if nothing selected
     * @return
     */
    public String getSelectedItem() {
        String selected =  getItemText(getSelectedIndex());
        if (isSelectItem(selected)) {
            return selected;
        }
        return "";
    }

    /**
     * Return selected items if multi-select or empty string if nothing selected
     * @return
     */
    public List<String> getSelectedItems() {
        List<String> selectedValues = new ArrayList<>();
        for (int i = 0, l = getItemCount(); i < l; i++) {
           if (isItemSelected(i)) {
              selectedValues.add(getValue(i));
           }
        }
        return selectedValues;
    }

    public void failedToLoad() {
        setModel(UIUtil.toListArray(messages.failedToLoad()));
    }

    @Override
    public String getState() {
        String state = "";
        for (int i = 0; i < getItemCount(); i ++) {
           if (isItemSelected(i)) {
              state += (i + ",");
           }
        }
        return state;
    }

    @Override
    public void setState(String data) {
        if (FieldVerifier.hasLength(data)) {
            doSetState(data);
        } else if (FieldVerifier.hasLength(defaultState)) {
            doSetState(defaultState);
        }
    }

    private void doSetState(String data) {
        String[] state = data.split(",");
        for (int i = 0 ; i < state.length; i ++) {
            setItemSelected(Integer.valueOf(state[i]), true);
        }
    }

    @Override
    public String getStateId() {
        return stateId;
    }

    @Override
    public boolean isStatefull() {
        return FieldVerifier.hasLength(stateId);
    }

    @Override
    public void reset() {
        if (FieldVerifier.hasLength(defaultState)) {
            setSelectedIndex(-1);
            setState(null);
        } else {
            setSelectedIndex(0);
        }
    }

    private boolean isSelectItem(String item) {
        return !item.matches("^\\[.*\\]$"); // e.g. [Select]
    }
}
