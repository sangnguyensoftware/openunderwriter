/* Copyright Applied Industrial Logic Limited 2017 All rights Reserved */
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

import static com.ail.core.CoreContext.getCoreProxy;
import static com.ail.core.CoreContext.getRequestWrapper;
import static com.ail.pageflow.PageFlowContext.flagActionAsProcessed;
import static com.ail.pageflow.PageFlowContext.getPolicy;
import static com.ail.pageflow.PageFlowContext.getRequestedOperation;
import static com.ail.pageflow.PageFlowContext.getRequestedOperationId;

import java.io.IOException;
import java.util.Map;
import java.util.TreeMap;

import com.ail.core.BaseException;
import com.ail.core.Functions;
import com.ail.core.HasLabels;
import com.ail.core.Type;
import com.ail.core.label.LabelsForSubjectService.LabelsForSubjectCommand;

public class LabelDetails extends PageElement {
    private static final long serialVersionUID = -4810599045554021748L;

    private String discriminator;
    private boolean addAndDeleteEnabled = true;
    private boolean readOnly;

    public LabelDetails() {
        super();
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public String getDiscriminator() {
        return discriminator;
    }

    public void setDiscriminator(String discriminator) {
        this.discriminator = discriminator;
    }

    /**
     * Fetch a map of all the labels that are active for (currently applied to) the
     * <code>model</code>. The labels are returned as Map with keys being the
     * I18N'ed translation for the label, and the value being the i18n code. This
     * has the effect of naturally ordering the labels alphabetically and also
     * providing the client with the i18n references it will need to use when making
     * changes.
     *
     * @param model
     * @return Map of applicable labels.
     * @throws BaseException
     */
    public Map<String, String> activeLabels(Type model, Map<String, String> availableLabels) {
        Map<String, String> results = new TreeMap<>();

        Object target = fetchBoundObject(model, model);

        if (target instanceof HasLabels) {
            ((HasLabels)model).getLabel().stream().forEach(l -> {
                if (availableLabels.containsKey(i18n(l))) {
                    results.put(i18n(l), l);
                }
            });
        }

        return results;
    }

    public String valueId(String id) {
        return encodeId(id);
    }

    /**
     * Fetch a map of all the labels that are valid for the <code>model</code>. The
     * labels are returned as Map with keys being the I18N'ed translation for the
     * label, and the value being the i18n code. This has the effect of naturally
     * ordering the labels alphabetically and also providing the client with the
     * i18n references it will need to use when making changes.
     * @param model
     * @return Map of applicable labels.
     * @throws BaseException
     */
    public Map<String,String> availableLabels(Type model) throws BaseException {
        Map<String, String> results = new TreeMap<>();

        Object target = fetchBoundObject(model, model);

        if (target instanceof HasLabels) {
            LabelsForSubjectCommand lfsc = getCoreProxy().newCommand(LabelsForSubjectCommand.class);
            lfsc.setDiscriminatorArg(discriminator);
            lfsc.setSubjectArg(((HasLabels)target).getClass());
            lfsc.setRootModelArg(getPolicy());
            lfsc.setLocalModelArg((Type)target);
            lfsc.invoke();
            lfsc.getLabelsRet().stream().forEach(l -> results.put(i18n(l), l));
        }

        return results;
    }

    public boolean isAddAndDeleteEnabled() {
        return addAndDeleteEnabled;
    }

    public void setAddAndDeleteEnabled(boolean addAndDeleteEnabled) {
        this.addAndDeleteEnabled = addAndDeleteEnabled;
    }

    public String addOp(Type model) {
        return "op="+LabelDetails.class.getSimpleName()+"-add:id="+getId()+":immediate=true";
    }

    public String deleteOp(Type model) {
        return "op="+LabelDetails.class.getSimpleName()+"-delete:id="+getId()+":immediate=true";
    }

    public String labelInputId() {
        return valueId(getId()+"-input");
    }

    @Override
    public Type applyRequestValues(Type model) {
        if (!isAddAndDeleteEnabled() || isReadOnly()) {
            return model;
        }

        String labelInput = getRequestWrapper().getParameter(labelInputId());

        Object target = fetchBoundObject(model, model);

        if (!Functions.isEmpty(labelInput)) {
            if (target instanceof HasLabels) {
                HasLabels hasLabels=(HasLabels)target;
                for(String label: labelInput.split(",")) {
                    if(!Functions.isEmpty(label)) {
                        switch(label.charAt(0)) {
                        case '+':
                            hasLabels.getLabel().add(label.substring(1));
                            break;
                        case '-': ;
                            hasLabels.getLabel().remove(label.substring(1));
                            break;
                        default:
                            throw new IllegalArgumentException("Label directive: "+label+" is not understood");
                        }
                    }
                }
            }
        }

        return model;
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        if (!isAddAndDeleteEnabled() || isReadOnly()) {
            return model;
        }

        Type target = (Type)fetchBoundObject(model, model);

        if (!conditionIsMet(target)) {
            return model;
        }

        handleAddAction(target);

        handleDeleteAction(target);

        return model;
    }

    private void handleDeleteAction(Type model) {
        if (deleteOperation(model)) {
            String labelId = getRequestWrapper().getParameter(valueId(getId()));
            ((HasLabels)model).getLabel().remove(labelId);
            flagActionAsProcessed();
        }
    }

    private void handleAddAction(Type model) {
        if (addOperation(model)) {
            String labelId = getRequestWrapper().getParameter(valueId(getId()));
            ((HasLabels)model).getLabel().add(labelId);
            flagActionAsProcessed();
        }
    }

    private boolean deleteOperation(Type model) {
        return (LabelDetails.class.getSimpleName()+"-delete").equals(getRequestedOperation()) && getId().equals(getRequestedOperationId());
    }

    private boolean addOperation(Type model) {
        return (LabelDetails.class.getSimpleName()+"-add").equals(getRequestedOperation()) && getId().equals(getRequestedOperationId());
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("LabelDetails", model);
    }
}
