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

package com.ail.insurance.search.hibernate;

import static com.ail.insurance.policy.Policy.CLIENT_PARTY_ROLE_TYPE;
import static com.ail.insurance.policy.PolicyStatus.DELETED;
import static java.lang.Long.valueOf;
import static org.apache.commons.lang.StringUtils.isNotBlank;
import static org.apache.commons.lang.math.NumberUtils.isNumber;
import static org.hibernate.criterion.Order.asc;
import static org.hibernate.criterion.Order.desc;
import static org.hibernate.criterion.Projections.property;
import static org.hibernate.criterion.Restrictions.disjunction;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.gt;
import static org.hibernate.criterion.Restrictions.in;
import static org.hibernate.criterion.Restrictions.isNotNull;
import static org.hibernate.criterion.Restrictions.isNull;
import static org.hibernate.criterion.Restrictions.le;
import static org.hibernate.criterion.Restrictions.like;
import static org.hibernate.sql.JoinType.LEFT_OUTER_JOIN;
import static org.hibernate.transform.Transformers.aliasToBean;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.Criteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Projections;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.Functions;
import com.ail.core.Service;
import com.ail.core.security.FilterListAccessibilityToUserService.FilterListAccessibilityToUserCommand;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.search.PolicySearchService.PolicySearchArgument;

@ServiceImplementation
public class HibernatePolicySearchService extends Service<PolicySearchArgument> {
    private static final long serialVersionUID = 3198893603833694389L;
    private static final String CONFIGURATION_NAMESPACE = Functions.productNameToConfigurationNamespace("AIL.Base");

    @SuppressWarnings("unchecked")
    @Override
    public void invoke() throws BaseException {

        Criteria criteria = createCriteria();
        criteria = buildWhere(criteria);
        criteria = buildOrder(criteria);
        criteria.setReadOnly(true);

        args.setPoliciesRet(filterSuperseded(
                               filterForAccessibility(
                                       criteria.list())));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<Policy> filterForAccessibility(List<Object> policies) throws BaseException {
        FilterListAccessibilityToUserCommand fla = core.newCommand("FilterListAccessibilityToUserCommand", FilterListAccessibilityToUserCommand.class);

        fla.setListArg(policies);
        fla.setUserIdArg(args.getUserIdArg());
        fla.invoke();
        return (List)fla.getListRet();
    }

    private Collection<Policy> filterSuperseded(List<Policy> policies) throws BaseException {

        if (!args.getIncludeSupersededArg()) {
            return getActivePolicies(policies);
        }
        return policies;
    }

    public static Collection<Policy> getActivePolicies(List<Policy> policies) {

        Map<String, Policy> filtered = new LinkedHashMap<>();
        for (Policy policy : policies) {

            if (policy.getStatus() != DELETED) {
                boolean put = true;

                String id = policy.getPolicyNumber();
                if (policy.getPolicyNumber() != null) {

                    Policy otherPolicy = filtered.get(id);
                    if (otherPolicy != null) {
                        if (
                                (otherPolicy.getRenewalIndex() == null ? 0 : otherPolicy.getRenewalIndex()) <= (policy.getRenewalIndex() == null ? 0 : policy.getRenewalIndex())
                                        && (otherPolicy.getMtaIndex() == null ? 0 : otherPolicy.getMtaIndex()) < (policy.getMtaIndex() == null ? 0 : policy.getMtaIndex())
                            ) {

                            filtered.remove(id);
                        } else {
                            put = false;
                        }
                    }
                }
                if (put) {
                    filtered.put(id == null || id.length() == 0 ? policy.getSystemId() + "" : id, policy);
                }
            }
        }
        return filtered.values();
    }


    Criteria createCriteria() {
        return new CoreProxy().criteria(Policy.class, "pol")
            .createAlias("pol.partyRole", "pro", LEFT_OUTER_JOIN)
            .createAlias("pro.party", "par", LEFT_OUTER_JOIN)
            .createAlias("par.contactSystem", "csy", LEFT_OUTER_JOIN)
            .add(
                disjunction()
                    .add(isNull("pro.role"))
                    .add(eq("pro.role", CLIENT_PARTY_ROLE_TYPE)))
            .setProjection(
                Projections.distinct(
                    Projections.projectionList()
                        .add(property("pol.systemId"), "systemId")
                        .add(property("pol.externalSystemId"), "externalSystemId")
                        .add(property("pol.owningUser"), "owningUser")
                        .add(property("pol.policyNumber"), "policyNumber")
                        .add(property("pol.quotationNumber"), "quotationNumber")
                        .add(property("pol.status"), "status")
                        .add(property("pol.productName"), "productName")
                        .add(property("pol.productTypeId"), "productTypeId")
                        .add(property("pol.createdDate"), "createdDate")
                        .add(property("pol.quotationDate"), "quotationDate")
                        .add(property("pol.inceptionDate"), "inceptionDate")
                        .add(property("pol.expiryDate"), "expiryDate")
                        .add(property("pol.renewalIndex"), "renewalIndex")
                        .add(property("pol.mtaIndex"), "mtaIndex")
                        .add(property("par.legalName"), "id")))
            .setResultTransformer(aliasToBean(Policy.class));
    }

    private Criteria buildWhere(Criteria criteria) {
        if (isNotBlank(args.getProductTypeIdArg())) {
            criteria.add(eq("productTypeId", args.getProductTypeIdArg()));
        }
        String policyId = args.getPolicyNumberArg();
        if (isNotBlank(policyId)) {
            Disjunction or = disjunction();

            or.add(like("policyNumber", "%" + policyId + "%"))
            .add(like("quotationNumber", "%" + policyId + "%"))
            .add(like("externalSystemId", "%" + policyId + "%"));

            if (isNumber(policyId)){
                or.add(eq("systemId", valueOf(policyId)));
            }
            criteria.add(or);
        }
        if (args.getPolicyStatusArg() != null) {
            criteria.add(in("status", args.getPolicyStatusArg()));
        } else {
            criteria.add(isNotNull("status"));
        }
        if (args.getUpdatedDateArg() != null) {
            criteria.add(gt("updatedDate", args.getUpdatedDateArg()));
        }
        if (args.getCreatedDateMinimumArg() != null) {
            criteria.add(gt("createdDate", args.getCreatedDateMinimumArg()));
        }
        if (args.getCreatedDateMaximumArg() != null) {
            criteria.add(le("createdDate", args.getCreatedDateMaximumArg()));
        }
        if (args.getExpiryDateMinimumArg() != null) {
            criteria.add(gt("expiryDate", args.getExpiryDateMinimumArg()));
        }
        if (args.getExpiryDateMaximumArg() != null) {
            criteria.add(le("expiryDate", args.getExpiryDateMaximumArg()));
        }
        if (args.getQuoteDateMinimumArg() != null) {
            criteria.add(gt("quotationDate", args.getQuoteDateMinimumArg()));
        }
        if (args.getQuoteDateMaximumArg() != null) {
            criteria.add(le("quotationDate", args.getQuoteDateMaximumArg()));
        }
        if (args.getInceptionDateMinimumArg() != null) {
            criteria.add(gt("inceptionDate", args.getInceptionDateMinimumArg()));
        }
        if (args.getInceptionDateMaximumArg() != null) {
            criteria.add(le("inceptionDate", args.getInceptionDateMaximumArg()));
        }
        if (!args.getIncludeTestArg()) {
            criteria.add(eq("testCase", false));
        }

        if (isNotBlank(args.getPartyNameArg())
                || isNotBlank(args.getPartyAddressArg())
                || isNotBlank(args.getPartyIdArg())
                || isNotBlank(args.getPartyEmailAddressArg())
                || args.getDateOfBirthArg() != null) {

            if (isNotBlank(args.getPartyIdArg())) {
                Disjunction or = disjunction();

                or.add(eq("par.partyId", args.getPartyIdArg()));

                if (isNumber(args.getPartyIdArg())) {
                    or.add(eq("par.systemId", valueOf(args.getPartyIdArg())));
                }

                criteria.add(or);
            }

            if (isNotBlank(args.getPartyNameArg())) {
                criteria.add(
                    like("par.legalName", args.getPartyNameArg())
                );
            }

            if(isNotBlank(args.getPartyAddressArg())) {
                criteria.add(
                    like("csy.fullAddress", args.getPartyAddressArg())
                );
            }

            if(isNotBlank(args.getPartyEmailAddressArg())) {
                criteria.add(
                    like("csy.fullAddress", args.getPartyEmailAddressArg())
                );
            }

            if (args.getDateOfBirthArg() != null) {
                criteria.add(
                    eq("par.dateOfBirth.date", args.getDateOfBirthArg())
                );
            }
        }

        return criteria;
    }

    private Criteria buildOrder(Criteria criteria) {
        if (args.getOrderByArg()!=null) {
            switch(args.getOrderByArg()) {
            case "Status ASC":
                criteria.addOrder(asc("status"));
                criteria.addOrder(asc("systemId"));
                break;
            case "Status DESC":
                criteria.addOrder(desc("status"));
                criteria.addOrder(desc("systemId"));
                break;
            case "Created Date ASC":
                criteria.addOrder(asc("createdDate"));
                criteria.addOrder(asc("systemId"));
                break;
            case "Created Date DESC":
                criteria.addOrder(desc("createdDate"));
                criteria.addOrder(desc("systemId"));
                break;
            case "Quote Date ASC":
                criteria.addOrder(asc("quotationDate"));
                criteria.addOrder(asc("systemId"));
                break;
            case "Quote Date DESC":
                criteria.addOrder(desc("quotationDate"));
                criteria.addOrder(desc("systemId"));
                break;
            case "Quote Expiry Date ASC":
                criteria.addOrder(asc("quotationExpiryDate"));
                criteria.addOrder(asc("systemId"));
                break;
            case "Quote Expiry Date DESC":
                criteria.addOrder(desc("quotationExpiryDate"));
                criteria.addOrder(desc("systemId"));
                break;
            case "Product ASC":
                criteria.addOrder(asc("productName"));
                criteria.addOrder(asc("systemId"));
                break;
            case "Product DESC":
                criteria.addOrder(desc("productName"));
                criteria.addOrder(desc("systemId"));
                break;
            case "Policy Number ASC":
                criteria.addOrder(asc("policyNumber"));
                criteria.addOrder(asc("systemId"));
                break;
            case "Policy Number DESC":
                criteria.addOrder(desc("policyNumber"));
                criteria.addOrder(desc("systemId"));
                break;
            default:
                criteria.addOrder(desc("createdDate"));
                criteria.addOrder(desc("systemId"));
                break;
            }
        }

        return criteria;
    }

    @Override
    public String getConfigurationNamespace() {
        return CONFIGURATION_NAMESPACE;
    }
}
