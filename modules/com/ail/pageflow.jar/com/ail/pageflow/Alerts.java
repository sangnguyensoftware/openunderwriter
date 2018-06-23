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
import static com.ail.pageflow.PageFlowContext.getPolicy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.ail.core.BaseException;
import com.ail.core.Note;
import com.ail.core.Type;
import com.ail.core.label.LabelsForSubjectService.LabelsForSubjectCommand;
import com.ail.insurance.policy.Clause;
import com.ail.insurance.policy.ClauseType;
import com.ail.insurance.policy.Policy;
import com.ail.party.Party;

/**
 * Alerts display either notes with the "alert" discriminator, or unresolved subjectivities.
 *
 */
public class Alerts extends PageElement {

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("Alerts", model);
    }


    public class Alert {
            public String level;
            public String message;

            Alert(String level, String message) {
                this.level = level;
                this.message = message;
            }

            public String getLevel() {
                return level;
            }

            public void setLevel(String level) {
                this.level = level;
            }

            public String getMessage() {
                return message;
            }

            public void setMessage(String message) {
                this.message = message;
            }
    }

    public List<Alert> getAlerts() {

        Policy policy = PageFlowContext.getPolicy();

        List<Alert> alerts = new ArrayList<>();

        // Get subjecttivies
        for (Clause clause :policy.getClause() ) {
            if (ClauseType.SUBJECTIVITY.equals(clause.getType())) {
                alerts.add(new Alerts.Alert("WARNING", clause.getText()));
            }
        }

        // Get labels for Party to check personal alerts
        try {
            LabelsForSubjectCommand lfsc = getCoreProxy().newCommand(LabelsForSubjectCommand.class);
            lfsc.setDiscriminatorArg("alerts");
            lfsc.setSubjectArg(Party.class);
            lfsc.setRootModelArg(getPolicy());
            lfsc.setLocalModelArg(policy);
            lfsc.invoke();

            for (Iterator<String> checkIt = lfsc.getLabelsRet().iterator(); checkIt.hasNext();) {
                // Check if label is valid
                String check = (String) checkIt.next();
                // Check notes for labels
                for (Note note : policy.getClient().getNote() ) {
                    for (Iterator<String> labelIt = note.getLabel().iterator(); labelIt.hasNext();) {
                        String label = (String) labelIt.next();
                        if (check.equals(label)) {
                            alerts.add(new Alerts.Alert("INFO", check));
                        }
                    }
                }
            }
        } catch (BaseException e) {
            // Add alert that could not get alers
        }
        return alerts;
    }
}
