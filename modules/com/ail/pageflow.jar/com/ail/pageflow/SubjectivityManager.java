/* Copyright Applied Industrial Logic Limited 2016. All rights Reserved */
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

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.lang.StringUtils;

import com.ail.core.Attribute;
import com.ail.core.BaseException;
import com.ail.core.Type;
import com.ail.core.context.RequestWrapper;
import com.ail.core.language.I18N;
import com.ail.insurance.HasClauses;
import com.ail.insurance.policy.Clause;
import com.ail.insurance.policy.ClauseType;
import com.ail.pageflow.util.Choice;

public class SubjectivityManager extends PageSection {

    public static final String CLAUSE_REFERENCE_DOCUMENT_TYPE_XPATH = "clause[reference='']/attribute[id='documentType']/value";

    private static final long serialVersionUID = 7575333161892813599L;

    public List<String> getSubjectivityTypeOptions() {
        String productName = PageFlowContext.getProductName();
        Choice choice = PageFlowContext.getCoreProxy().newProductType(productName, "DocumentTypes", Choice.class);
        List<String> values=extract(choice.getChoice(), on(Choice.class).getName());
        Collections.sort(values);
        return values;
    }

    @Override
    public boolean processValidations(Type model) {
        boolean errorsFound = false;
        return errorsFound;
    }

    @Override
    public Type applyRequestValues(Type model) {
        model = super.applyRequestValues(model);
        RequestWrapper request = PageFlowContext.getRequestWrapper();

        String documentType = request.getParameter(CLAUSE_REFERENCE_DOCUMENT_TYPE_XPATH);

        if (!"none".equals(documentType)) {

            String documentName = getDocumentName(request, documentType);
            String requiredByDate = getRequiredByDate(request);
            String requiresSigning = request.getParameter("clause[reference='']/attribute[id='requiresSigning']/value");
            String newRef = getReference(model, documentName, documentType);

            Clause subjectivity = new Clause(ClauseType.SUBJECTIVITY, newRef, documentName);

            subjectivity.addAttribute(
                    new Attribute("documentType", documentType, "string"));
            subjectivity.addAttribute(
                    new Attribute("documentName", documentName, "string"));
            subjectivity.addAttribute(
                    new Attribute("requiredByDate", requiredByDate, "date,pattern=MMddyyyy"));
            subjectivity.addAttribute(
                    new Attribute("requiresSigning", "Yes".equals(requiresSigning) ? "Yes" : "No", "yesorno"));

            ((HasClauses)model).getClause().add(subjectivity);

        }

        Map<String,String> references = new HashMap<>();

        for (String paramName : Collections.list(request.getParameterNames())) {
            if (paramName.startsWith("clause[") && !paramName.contains("[reference='']")) {
                String paramValue = request.getParameter(paramName);
                String currentValue = model.xpathGet(paramName, String.class);

                if (paramName.contains("[id='requiresSigning']")) {
                    if ("Yes".equals(paramValue)) {
                        paramValue = "Yes";
                    } else {
                        paramValue = "No";
                    }
                }
                if (paramValue!=null && !paramValue.equals(currentValue)) {
                    model.xpathSet(paramName, "".equals(paramValue) ? null : paramValue);
                    if (paramName.contains("[id='documentName']")) {
                        references.put(paramName + "/../../reference", paramValue);
                    }
                }
            }
        }

        for (Map.Entry<String,String> ref : references.entrySet()) {
            String newRef = getReference(model, ref.getValue(), "");
            if (StringUtils.isNotBlank(newRef)) {
                model.xpathSet(ref.getKey(), newRef);
            }
        }

        PageFlowContext.getCoreProxy().flush();

        return model;
    }

    private String getDocumentName(RequestWrapper request, String documentType) {
        String documentName = request.getParameter("clause[reference='']/attribute[id='documentName']/value");
        if (StringUtils.isBlank(documentName)) {
            documentName = I18N.i18n(documentType);
        }
        return documentName;
    }

    private String getRequiredByDate(RequestWrapper request) {
        String requiredByDate = request.getParameter("clause[reference='']/attribute[id='requiredByDate']/value");
        if (StringUtils.isBlank(requiredByDate)) {
            requiredByDate = new SimpleDateFormat("MMddyyyy").format(new Date());
        }
        return requiredByDate;
    }

    private String getReference(Type model, String documentName, String documentType) {
        String ref = StringUtils.isNotBlank(documentName) ? documentName : documentType;

        while( !model.xpathGet("count(clause[reference='" + ref + "'])").equals(Double.valueOf(0.0))) {
            ref = ref + "_";
        }
        return ref;
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        Properties opParams = PageFlowContext.getOperationParameters();
        String op = PageFlowContext.getRequestedOperation();

        if ("deleteClause".equals(op)) {
            String ref = opParams.getProperty("row");
            HasClauses hasClauses = (HasClauses) model;
            Iterator<Clause> clauses = hasClauses.getClause().iterator();
            while (clauses.hasNext()) {
                Clause clause = clauses.next();
                if (ref.equals(clause.getReference())) {
                    clauses.remove();
                }
            }
            PageFlowContext.flagActionAsProcessed();
        }

        return model;
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("SubjectivityManager", model);
    }
}
