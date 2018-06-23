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

package com.ail.insurance.quotation;

import java.util.List;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.ExceptionRecord;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.document.Document;
import com.ail.insurance.HasClauses;
import com.ail.insurance.policy.Asset;
import com.ail.insurance.policy.Clause;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.policy.Section;
import com.ail.party.PartyRole;

@ServiceImplementation
public class PrepareRequoteService extends Service<PrepareRequoteService.PrepareRequoteArgument> {

    private static final long serialVersionUID = 7959054258764631251L;


    @ServiceArgument
    public interface PrepareRequoteArgument extends Argument {
        void setPolicyArg(Policy policyArg);

        Policy getPolicyArg();

        void setRequoteRet(Policy policyRet);

        Policy getRequoteRet();
    }

    @ServiceCommand(defaultServiceClass=PrepareRequoteService.class)
    public interface PrepareRequoteCommand extends Command, PrepareRequoteArgument {
    }

    @Override
    public void invoke() throws PreconditionException, BaseException {
        if (args.getPolicyArg() == null) {
            throw new PreconditionException("args.getPolicyArg() == null");
        }

        CoreProxy core = CoreContext.getCoreProxy();

        Policy donorPolicy = core.fromXML(Policy.class, core.toXML(args.getPolicyArg()));
        String productTypeId = args.getPolicyArg().getProductTypeId();
        Policy requote = core.newProductType(productTypeId, "Policy", Policy.class);

        requote.mergeWithDataFrom(donorPolicy, CoreContext.getCoreProxy().getCore());

        prepareBasicFields(requote);

        prepareAssessmentSheets(requote);

        prepareDocuments(requote);

        preparePaymentDetails(requote);

        prepareAssets(requote.getAsset());

        prepareParties(requote);

        prepareClauses(requote);

        args.setRequoteRet(requote);
    }

    private void prepareClauses(Policy requote) {
        prepareClauses(args.getPolicyArg(), requote, requote);
        for(int i=0 ; i<args.getPolicyArg().getSection().size() ; i++) {
            prepareClauses(args.getPolicyArg().getSection().get(i), requote.getSection().get(i), requote);
        }
    }

    private void prepareClauses(HasClauses src, HasClauses dst, Policy requote) {
        dst.getClause().clear();
        src.getClause().stream().forEach(c -> {
            try {
                Clause clone = (Clause) c.clone();
                clone.markAsNotPersisted();
                dst.getClause().add(clone);
            } catch (CloneNotSupportedException e) {
                requote.getException().add(new ExceptionRecord(e));
            }
        });
    }

    private void prepareParties(Policy requote) {
        requote.getPartyRole().clear();
        args.getPolicyArg().getPartyRole().forEach(
            pr -> requote.getPartyRole().add(new PartyRole(pr.getRole(), pr.getParty(), pr.getStartDate(), pr.getEndDate()))
        );
    }

    private void prepareAssets(List<Asset> requote) {
        for(Asset asset: requote) {
            prepareAssets(asset.getAsset());
            asset.markAsNotPersisted();
        }
    }

    private void preparePaymentDetails(Policy requote) {
        requote.setPaymentDetails(null);
        requote.getPaymentHistory().clear();
        requote.getPaymentOption().clear();
    }

    private void prepareDocuments(Policy requote) {
        requote.getArchivedDocument().clear();

        requote.getDocument().clear();

        for(Document document: args.getPolicyArg().getDocument()) {
            try {
                Document clone = (Document) document.clone();
                clone.markAsNotPersisted();
                requote.getDocument().add(clone);
            } catch (CloneNotSupportedException e) {
                requote.getException().add(new ExceptionRecord(e));
            }
        }
    }

    private void prepareAssessmentSheets(Policy requote) {
        if (requote.getAssessmentSheet() != null) {

            requote.getAssessmentSheet().markAsNotPersisted();

            if (requote.getAssessmentSheet().getAssessmentLine() != null) {
                requote.getAssessmentSheet().getAssessmentLine().clear();
            }
        }

        for (Section section : requote.getSection()) {
            section.markAsNotPersisted();

            if (section.getAssessmentSheet() != null) {

                section.getAssessmentSheet().markAsNotPersisted();

                if (section.getAssessmentSheet().getAssessmentLine() != null) {
                    section.getAssessmentSheet().getAssessmentLine().clear();
                }
            }
        }
    }

    private void prepareBasicFields(Policy requote) {
        requote.setStatus(PolicyStatus.APPLICATION);
        requote.setUserSaved(false);
        requote.markAsNotPersisted();
        requote.getPageVisit().clear();
        requote.setQuotationNumber(null);
        requote.setPolicyNumber(null);
    }
}


