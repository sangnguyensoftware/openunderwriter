/* Copyright Applied Industrial Logic Limited 2017. All rights Reserved */
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
import static com.ail.core.Functions.isEmpty;
import static com.ail.pageflow.PageFlowContext.getPolicy;
import static com.ail.pageflow.PageFlowContext.getRequestedOperation;
import static com.ail.pageflow.PageFlowContext.getRequestedOperationId;
import static java.lang.Integer.parseInt;
import static java.lang.String.valueOf;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.ail.core.BaseException;
import com.ail.core.HasLabels;
import com.ail.core.HasNotes;
import com.ail.core.Note;
import com.ail.core.Type;
import com.ail.core.context.RequestWrapper;
import com.ail.core.label.LabelsForSubjectService.LabelsForSubjectCommand;


public class NoteDetails extends PageElement {
    private static final long serialVersionUID = -4810599045554021748L;

    /** Set to true if the user should be able to add and remove notes */
    private boolean addAndDeleteEnabled = true;
    private boolean readOnly = false;
    private String typeDiscriminator = null;
    private static final Pattern MATCH_ALL = Pattern.compile(".*");

    transient private List<LabelDetails> labelDetails;

    private String validNoteTypes = ".*";
    private String validLabels = ".*";
    transient private Pattern validNoteTypesPattern = MATCH_ALL;
    transient private Pattern validLabelsPattern = MATCH_ALL;

    public NoteDetails() {
        super();
    }

    public NoteDetails(String typeDiscriminator){
        this.typeDiscriminator = typeDiscriminator;
    }

    public String getValidNoteTypes() {
        return validNoteTypes;
    }

    public void setValidNoteTypes(String validNoteTypes) {
        this.validNoteTypes = validNoteTypes;
    }

    private Pattern validNoteTypesPattern() {
        if (validNoteTypesPattern == null) {
            validNoteTypesPattern = Pattern.compile(validNoteTypes);
        }
        return validNoteTypesPattern;
    }

    public String getValidLabels() {
        return validLabels;
    }

    public void setValidLabels(String validLabels) {
        this.validLabels = validLabels;
    }

    private Pattern validLabelsPattern() {
        if (validLabelsPattern == null) {
            validLabelsPattern = Pattern.compile(validLabels);
        }
        return validLabelsPattern;
    }

    public LabelDetails getLabelDetails(int i) {
        if (labelDetails == null) {
            labelDetails = new ArrayList<>();
        }

        if (i >= labelDetails.size()) {
            for(int x=labelDetails.size() ; x<=i ; x++) {
                LabelDetails ld = new LabelDetails();
                ld.applyElementId(getId()+"-ld-"+x);
                labelDetails.add(ld);
            }
        }
        return labelDetails.get(i);
    }

    public void setLabelDetails(int i, LabelDetails labelDetails) {
        this.labelDetails.set(i, labelDetails);
    }

    public List<String> getNoteTypeOptions(Type model) throws BaseException {
        return noteTypeOptions(model).stream().
                 filter(s -> validNoteTypesPattern().matcher(s).matches()).
                 sorted().
                 collect(Collectors.toList());
    }

    public Collection<Note> filteredNotes(HasNotes source) {
        return source.getNote().stream().filter(n -> isNoteTypeAndLabelsValid(n)).collect(Collectors.toList());
    }

    /**
     * Determine if the note is valid WRT {@link #validNoteTypesPattern} and {@link #validLabels}.
     * @param note
     * @return true, if the note passes both validation checks; false, otherwise.
     */
    public boolean isNoteTypeAndLabelsValid(Note note) {
        if (note.getType() == null || validNoteTypesPattern().matcher(note.getType()).matches()) {
            if (note.getLabel().size() == 0) {
                return true;
            }
            else {
                if (note.getLabel().stream().allMatch(l -> validLabelsPattern().matcher(l).matches())) {
                    return true;
                }
            }
        }

        return false;
    }

    public boolean canHaveNotes(Object o) {
        return o instanceof HasNotes;
    }

    @SuppressWarnings("unchecked")
    public Set<String> noteTypeOptions(Type model) throws BaseException {
        LabelsForSubjectCommand lfsc = getCoreProxy().newCommand(LabelsForSubjectCommand.class);
        lfsc.setSubjectArg((Class<? extends HasLabels>) model.getClass());
        lfsc.setDiscriminatorArg("(note_types" + ((typeDiscriminator!=null) ? "|"+typeDiscriminator+")" : ")"));
        lfsc.setRootModelArg(getPolicy());
        lfsc.invoke();

        return lfsc.getLabelsRet();
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("NoteDetails", model);
    }

    @Override
    public Type applyRequestValues(Type model) {
        if (!isAddAndDeleteEnabled() || isReadOnly()) {
            return model;
        }

        if (!conditionIsMet(model)) {
            return model;
        }

        handleNoteUpdates(model);

        return model;
    }

    @Override
    public boolean processValidations(Type model) {
        if (!isAddAndDeleteEnabled() || isReadOnly()) {
            return false;
        }
        return super.processValidations(model);
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        if (!isAddAndDeleteEnabled() || isReadOnly()) {
            return model;
        }

        if (!conditionIsMet(model)) {
            return model;
        }

        handleNoteAdd(model);

        handleNoteDelete(model);

        return model;
    }

    private void handleNoteDelete(Type model) {
        HasNotes target = getTarget(model);
        if (deleteButtonPressed(target.getSystemId())) {
            int row = Integer.parseInt((String)PageFlowContext.getOperationParameters().get("row"));

            target.getNote().remove(row);

            PageFlowContext.flagActionAsProcessed();
        }
    }

    private Type handleNoteUpdates(Type model) {
        RequestWrapper request = PageFlowContext.getRequestWrapper();

        HasNotes target = getTarget(model);

        // Uopdate existing notes
        for(int i=0 ; i<target.getNote().size() ; i++) {
            String title = request.getParameter(titleId(model, i));
            String body = request.getParameter(bodyId(model, i));
            String type = request.getParameter(typeId(model, i));

            if (title != null) {
                target.getNote().get(i).setTitle(title);
            }

            if (body != null) {
                target.getNote().get(i).setBody(body);
            }

            if (type != null) {
                target.getNote().get(i).setType(type);
            }

            getLabelDetails(i).applyRequestValues(target.getNote().get(i));
        }

        if (getRequestedOperationId() != null && getRequestedOperationId().equals(getId())) {
            // check for new notes
            String body = request.getParameter(encodeId("bodyId"));
            if (body != null) {
                Note note = (Note)getCoreProxy().newType("Note", com.ail.core.Note.class);

                note.setTitle(request.getParameter(encodeId("titleId")));
                note.setBody(body);
                note.setType(request.getParameter(encodeId("noteTypeId")));

                target.getNote().add(note);

                // Also check for labels
                LabelDetails newLabelDetails = getLabelDetails(target.getNote().size() - 1);
                // Get the request param, then re-ßadd with updated id
                String labelInput = request.getParameter(encodeId("labelId"));
                request.getParameterMap().put(newLabelDetails.labelInputId(), new String[] {labelInput});

                newLabelDetails.applyRequestValues(note);
            }
        }

        return model;
    }

    private void handleNoteAdd(Type model) {
        HasNotes target = getTarget(model);
        if (addButtonPressed(target.getSystemId())) {
            target.getNote().add(getCoreProxy().newType("Note", com.ail.core.Note.class));
            PageFlowContext.flagActionAsProcessed();
        }
    }

    private HasNotes getTarget(Type model) {
        return (binding!=null) ? model.xpathGet(binding, HasNotes.class) : (HasNotes)model;
    }

    private boolean addButtonPressed(long systemId) {
        return (this.getClass().getSimpleName() + "-add").equals(getRequestedOperation()) && parseInt(getRequestedOperationId()) == systemId;
    }

    private boolean deleteButtonPressed(long systemId) {
        return (this.getClass().getSimpleName() + "-delete").equals(getRequestedOperation()) && parseInt(getRequestedOperationId()) == systemId;
    }

    public String titleId(Type model, int i) {
        String bindingRoot = !isEmpty(binding) ? binding : valueOf(model.getSystemId());
        return encodeId(bindingRoot + "/note[" + (i + 1) + "]/title");
    }

    public String bodyId(Type model, int i) {
        String bindingRoot = !isEmpty(binding) ? binding : valueOf(model.getSystemId());
        return encodeId(bindingRoot + "/note[" + (i + 1) + "]/body");
    }

    public String typeId(Type model, int i) {
        String bindingRoot = !isEmpty(binding) ? binding : valueOf(model.getSystemId());
        return encodeId(bindingRoot + "/note[" + (i + 1) + "]/type");
    }

    public String deleteOp(Type model, int i) {
        return "op="+NoteDetails.class.getSimpleName()+"-delete:id="+model.getSystemId()+":row="+i+":immediate=true";
    }

    public String addOp(Type model) {
        return "op="+NoteDetails.class.getSimpleName()+"-add:id="+model.getSystemId()+":immediate=true";
    }

    public boolean isReadOnly() {
        return readOnly;
    }

    public void setReadOnly(boolean readOnly) {
        this.readOnly = readOnly;
    }

    public boolean isAddAndDeleteEnabled() {
        return addAndDeleteEnabled;
    }

    public void setAddAndDeleteEnabled(boolean addAndDeleteEnabled) {
        this.addAndDeleteEnabled = addAndDeleteEnabled;
    }

    public String getTypeDiscriminator() {
        return typeDiscriminator;
    }

    public void setTypeDiscriminator(String typeDiscriminator) {
        this.typeDiscriminator = typeDiscriminator;
    }
}
