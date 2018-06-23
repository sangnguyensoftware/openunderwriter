/* Copyright Applied Industrial Logic Limited 2018. All rights Reserved */
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

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.HasLabels;
import com.ail.core.HasNotes;
import com.ail.core.Note;
import com.ail.core.Type;
import com.ail.core.context.RequestWrapper;
import com.ail.core.label.LabelsForSubjectService.LabelsForSubjectCommand;

public class NoteRecorder extends PageElement {
    private static final long serialVersionUID = -4810599045554021748L;
    private static final String SCRATCH_NOTE_ATTRIBUTE_ID = "moneyNote";

    private String typeDiscriminator = null;

    private transient LabelDetails labelDetails;

    public NoteRecorder() {
        super();
    }

    public String titleId(Type model) {
        return encodeId(getId() + "-title");
    }

    public String bodyId(Type model) {
        return encodeId(getId() + "-body");
    }

    public String typeId(Type model) {
        return encodeId(getId() + "-type");
    }

    public String noteId() {
        return encodeId(getId() + "-id");
    }

    public String getTypeDiscriminator() {
        return typeDiscriminator;
    }

    public void setTypeDiscriminator(String typeDiscriminator) {
        this.typeDiscriminator = typeDiscriminator;
    }

    public LabelDetails getLabelDetails() {
        if (labelDetails == null) {
            labelDetails = new LabelDetails();
            labelDetails.applyElementId(getId()+"-ld");
        }
        return labelDetails;
    }

    public Note getNote(Type model) {
        return getScratchNote(model);
    }

    public boolean canHaveNotes(Object o) {
        return o instanceof HasNotes;
    }

    public List<String> getNoteTypeOptions(Type model) throws BaseException {
        return noteTypeOptions(model).stream().
                 sorted().
                 collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public Set<String> noteTypeOptions(Type model) throws BaseException {
        Type target = (Type)fetchBoundObject(model, model);

        LabelsForSubjectCommand lfsc = getCoreProxy().newCommand(LabelsForSubjectCommand.class);
        lfsc.setSubjectArg((Class<? extends HasLabels>) target.getClass());
        lfsc.setDiscriminatorArg("(note_types" + ((typeDiscriminator!=null) ? "|"+typeDiscriminator+")" : ")"));
        lfsc.setRootModelArg(getPolicy());
        lfsc.invoke();

        return lfsc.getLabelsRet();
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("NoteRecorder", model);
    }

    @Override
    public Type applyRequestValues(Type model) {
        Type target = (Type)fetchBoundObject(model, model);

        if (!conditionIsMet(target)) {
            return model;
        }

        handleNoteUpdates(target);

        return model;
    }

    @Override
    public boolean processValidations(Type model) {
        return super.processValidations(model);
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        if (!conditionIsMet(model)) {
            return model;
        }

         return model;
    }

     private Type handleNoteUpdates(Type model) {
        RequestWrapper request = PageFlowContext.getRequestWrapper();

        String noteId = request.getParameter(noteId());
        String title = request.getParameter(titleId(model));
        String body = request.getParameter(bodyId(model));
        String type = request.getParameter(typeId(model));

        if (title != null) {
            getScratchNote(model).setTitle(title);
        }

        if (body != null) {
            getScratchNote(model).setBody(body);
        }

        if (type != null) {
            getScratchNote(model).setType(type);
        }

        if (!isEmpty(title) || !isEmpty(body)) {
            if (noteId != null && Long.parseLong(noteId) == -1 ) {
                Note note = getScratchNote(model);
                note = CoreContext.getCoreProxy().create(note);
                ((HasNotes) model).getNote().add(note);
            }
        }

        getLabelDetails().applyRequestValues(getScratchNote(model));

        return model;
    }

    public Note getScratchNote(Type model) {
        RequestWrapper request = PageFlowContext.getRequestWrapper();

        Note scratchNote = (Note) request.getServletRequest().getAttribute(scratchNoteAttributeId());

        if (scratchNote == null) {
            String noteIdString = CoreContext.getRequestWrapper().getParameter(noteId());

            if (noteIdString == null || Long.parseLong(noteIdString) == -1) {
                scratchNote = CoreContext.getCoreProxy().newType(Note.class);
            }
            else {
                Long noteId=Long.parseLong(noteIdString);
                scratchNote = ((HasNotes)model).getNote().stream().filter(n -> n.getSystemId() == noteId).findFirst().get();
            }

            request.getServletRequest().setAttribute(scratchNoteAttributeId(), scratchNote);
        }

        return scratchNote;
    }

    private String scratchNoteAttributeId() {
        return SCRATCH_NOTE_ATTRIBUTE_ID + getId();
    }
}
