/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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

package com.ail.insurance.policy;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Reference;
import com.ail.core.Type;

/**
 * Base class for all types of assessment lines. Assessment lines record the detail of the calculations
 * to be made in order to arrive at a premium. During the premium calculation process, any number of these
 * lines will be added to the {@link AssessmentSheet AssessmentSheets} of a policy to both define and record
 * how the premium is arrived at. This may well include factors like tax and brokerage, and also will may
 * list the reasons why rating isn't possible in the form or referral or decline indications.
 */
@TypeDefinition
public class AssessmentLine extends Type implements Comparable<AssessmentLine> {
    private static final long serialVersionUID = 1357488757251866318L;
    private String id=null;
    private String reason=null;
    private boolean disabled=false;
    private int priority=0;
    private int processedOrder=0;
    private transient AssessmentSheet assessmentSheet=null;
    private Reference relatesTo;

    /** Origin describes the actor (system or user) who created this AssessmenLine. */
    private String origin;

    /**
     * Default constructor
     */
    public AssessmentLine() {
    }

    /**
     * Constructor. A default priority of 0 is assumed.
     * @param id Value for Id property.
     * @param reason
     * @param relatesTo
     */
    public AssessmentLine(String id, String reason, Reference relatesTo) {
        this.id=id;
        this.reason=reason;
        this.relatesTo=relatesTo;
    }

    /**
     * Constructor
     * @param id Value for Id property.
     * @param reason The reason for creating this line.
     * @param relatesTo The id of another line that this one relates to.
     * @param priority The priority of this line wrt other lines in the same assessment sheet.
     */
    public AssessmentLine(String id, String reason, Reference relatesTo, int priority) {
        this.id=id;
        this.reason=reason;
        this.relatesTo=relatesTo;
        this.priority=priority;
    }

    /**
     * The lines Id. This is unique value within an assessment sheet.
     * @return
     */
    public String getId() {
        return id;
    }

    public String getIdForDisplay() {
        return id.startsWith("#") ? "" : id;
    }

    /**
     * @see #getId()
     * @param id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * An optional pointer to the object in the policy which this lines relates to.
     * For example, a motor policy may have 3 vehicles on it, one of which doesn't
     * have an alarm fitted and therefore caused a loading to be added. This property
     * would identify which vehicle.
     * This is for indication only, the property's value is not used by the system.
     * @return
     */
    public Reference getRelatesTo(){
      return relatesTo;
    }

    /**
     * @see #getRelatesTo()
     * @param relatesTo
     */
    public void setRelatesTo(Reference relatesTo){
      this.relatesTo = relatesTo;
    }

    /**
     * Human readable textual description of why this line exists. For example:
     * "Loading added because the driver is less than 20 years or age"
     * @return
     */
    public String getReason() {
        return reason;
    }

    /**
     * @see #getReason()
     * @param reason
     */
    public void setReason(String reason) {
        this.reason = reason;
    }

    /**
     * Getter returning the value of the origin property. Origin describes the actor (system or user) who
     * created this AssessmenLine.
     * @return Value of the origin property
     */
    public String getOrigin() {
        return origin;
    }

    /**
     * Setter to update the value of the origin property. Origin describes the actor (system or user) who
     * created this AssessmenLine.
     * @param origin New value for the origin property
     */
    public void setOrigin(String origin) {
        this.origin = origin;
    }

    /**
     * Determine if this line is disabled. By default all lines are enabled when created, and as such are
     * included in the calculations and assessments carried out by services, but individual lines may
     * be disabled. When a line is disabled it is simple ignored by all services that operate on
     * assessment sheets.
     * @return Returns the disabled.
     */
    public boolean isDisabled()
    {
      return disabled;
    }

    /**
     * @see #isDisabled()
     * @param disabled The disabled to set.
     */
    public void setDisabled(boolean disabled)
    {
      this.disabled = disabled;
    }

    /**
     * The priority of a line affects the order in which it is processed with respect to other lines.
     * Some services (for example {@link com.ail.insurance.quotation.refreshassessmentsheet.RefreshAssessmentSheetService RefreshAssessmentSheetService})
     * process all the lines in an assessment sheet. Such services use the priority of the lines to determine the order in
     * which the lines should be processed.<p>
     * The priority may be any integer value, higher values are processed before lower.
     * @return Returns the priority.
     */
    public int getPriority()
    {
      return priority;
    }

    /**
     * @see #getPriority()
     * @param priority The priority to set.
     */
    public void setPriority(int priority)
    {
      this.priority = priority;
    }

    /**
     * Order lines based on each line's priority.
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(AssessmentLine that)
    {
      return  that.priority - this.priority;
    }

    /**
     * Records the order in which lines were processed relative to other lines. When an assessment sheet has
     * been through some process (e.g. {@link com.ail.insurance.quotation.refreshassessmentsheet.RefreshAssessmentSheetService RefreshAssessmentSheetService})
     * the service will set the value of this property to indicate the order in which it processed the lines.
     * @return
     */
    public int getProcessedOrder() {
        return processedOrder;
    }

    /**
     * @see #getProcessedOrder()
     * @param processedOrder
     */
    public void setProcessedOrder(int processedOrder) {
        this.processedOrder = processedOrder;
    }

    /**
     * Set the assessment sheet which this line is part of. During the processing of
     * assessment sheets, lines may need the context of the sheet that they exist
     * within in order to complete their operations. For example, to be able to
     * check for {@link ControlLine ControlLines} which might affect them.
     * @param assessmentSheet
     */
    public void setAssessmentSheet(AssessmentSheet assessmentSheet) {
        this.assessmentSheet = assessmentSheet;
    }

    /**
     * @see #setAssessmentSheet(AssessmentSheet)
     */
    public AssessmentSheet getAssessmentSheet() {
        return assessmentSheet;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (disabled ? 1231 : 1237);
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((origin == null) ? 0 : origin.hashCode());
        result = prime * result + priority;
        result = prime * result + processedOrder;
        result = prime * result + ((reason == null) ? 0 : reason.hashCode());
        result = prime * result + ((relatesTo == null) ? 0 : relatesTo.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        AssessmentLine other = (AssessmentLine) obj;
        if (disabled != other.disabled)
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (origin == null) {
            if (other.origin != null)
                return false;
        } else if (!origin.equals(other.origin))
            return false;
        if (priority != other.priority)
            return false;
        if (processedOrder != other.processedOrder)
            return false;
        if (reason == null) {
            if (other.reason != null)
                return false;
        } else if (!reason.equals(other.reason))
            return false;
        if (relatesTo == null) {
            if (other.relatesTo != null)
                return false;
        } else if (!relatesTo.equals(other.relatesTo))
            return false;
        return true;
    }
}
