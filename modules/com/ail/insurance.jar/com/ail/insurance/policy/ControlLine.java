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

import com.ail.core.Reference;

/**
 * A control line is a type of line which may influence the processing of the assessment sheet. 
 * @since Insurance 2.4, OpenQuote 1.3
 */
public abstract class ControlLine extends AssessmentLine {
    private static final long serialVersionUID = -4519518497757725779L;
    private AssessmentStage assessmentStage;
    private ControlLineType controlLineType=ControlLineType.OUTSIDE;
    private boolean fired;

    public ControlLine() {
    }
    
    /**
     * Constructor
     * @param id The Id for this line.
     * @param reason Free text reason for the marker.
     * @param relatesTo A reference to the assessment line which this control line applies to.
     * @param stage The stage during rating when this line should be applied.
     */
    public ControlLine(String id, String reason, Reference relatesTo, AssessmentStage stage) {
        super(id, reason, relatesTo);
        this.assessmentStage=stage;
    }

    /**
     * Get the stage of the assessment sheet processing when this line should be executed.
     * @return
     */
    public AssessmentStage getAssessmentStage() {
        return assessmentStage;
    }
    
    /**
     * @see #getAssessmentStage()
     */
    public void setAssessmentStage(AssessmentStage stage) {
        this.assessmentStage = stage;
    }
    
    public String getAssessmentStageAsString() {
        return assessmentStage.getLongName();
    }

    public void setAssessmentStageAsString(String stageAsString) {
        this.assessmentStage = AssessmentStage.forName(stageAsString);
    }

    /**
     * Base implementation of control execution. This method is invoked during assessment sheet processing.
     * Implementors of this class should implement the appropriate checks/actions in this method.
     * @param sheet The sheet being processed at this time.
     * @param line The line on which the control is to be applied.
     */
    public void execute(AssessmentSheet sheet, AssessmentLine line) {
    }

    /**
     * Execute the control. This method is invoked during assessment sheet processing based on the
     * setting of {@link ControlLine#getAssessmentStage() stage}. Implementors of this class should implement
     * the appropriate checks/actions in this method.
     * @param sheet The sheet being processed at this time.
     * @param line The line on which the control is to be applied.
     */
    public void execute(AssessmentSheet sheet, CalculationLine line) {
    }

    /**
     * Set the control line's type. The type defines how the limits are to be applied.
     * @return Control line's type
     */
    public ControlLineType getControlLineType() {
        return controlLineType;
    }

    /**
     * Set the control line's type. The type defines how the limits are to be applied.
     * @return Control line's type
     */
    public String getControlLineTypeAsString() {
        return controlLineType.toString();
    }


    /**
     * @see #getControlLineType()
     * @param controlLineType
     */
    public void setControlLineType(ControlLineType controlLineType) {
        this.controlLineType = controlLineType;
    }

    /**
     * @see #getControlLineType()
     * @param controlLineType
     */
    public void setControlLineTypeAsString(String controlLineType) {
        this.controlLineType = ControlLineType.valueOf(controlLineType);
    }

    /**
     * A control line can only fire once. Once it has fired, this 
     * property is set to true to prevent it from firing again.
     * @return true if this line has fired, false otherwise
     */
    public boolean hasFired() {
        return fired;
    }

    /**
     * @see #hasFired()
     * @param fired
     */
    public void setFired(boolean fired) {
        this.fired = fired;
    }
}
