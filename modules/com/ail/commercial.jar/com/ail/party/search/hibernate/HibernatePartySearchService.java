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

package com.ail.party.search.hibernate;

import static com.ail.party.PhoneNumber.MOBILE_PHONE_NUMBER;
import static org.hibernate.criterion.MatchMode.ANYWHERE;
import static org.hibernate.criterion.Order.asc;
import static org.hibernate.criterion.Order.desc;
import static org.hibernate.criterion.Restrictions.eq;
import static org.hibernate.criterion.Restrictions.gt;
import static org.hibernate.criterion.Restrictions.ilike;
import static org.hibernate.criterion.Restrictions.le;
import static org.hibernate.sql.JoinType.LEFT_OUTER_JOIN;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Restrictions;

import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.Functions;
import com.ail.core.Service;
import com.ail.core.persistence.hibernate.HibernateSessionBuilder;
import com.ail.core.security.FilterListAccessibilityToUserService.FilterListAccessibilityToUserCommand;
import com.ail.party.Address;
import com.ail.party.EmailAddress;
import com.ail.party.Party;
import com.ail.party.PhoneNumber;
import com.ail.party.search.PartySearchService.PartySearchArgument;

@ServiceImplementation
public class HibernatePartySearchService extends Service<PartySearchArgument> {
    private static final long serialVersionUID = 3198893603833694389L;
    private static final String CONFIGURATION_NAMESPACE = Functions.productNameToConfigurationNamespace("AIL.Base");

    @SuppressWarnings("unchecked")
    @Override
    public void invoke() throws BaseException {

        Criteria criteria = createCriteria();
        criteria = buildWhere(criteria);
        criteria = buildOrder(criteria);
        criteria.setReadOnly(true);

        args.setPartyRet(filterForAccessibility(criteria.list()));
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    private List<Party> filterForAccessibility(List<Object> policies) throws BaseException {
        FilterListAccessibilityToUserCommand fla = core.newCommand("FilterListAccessibilityToUserCommand", FilterListAccessibilityToUserCommand.class);

        fla.setListArg(policies);
        fla.setUserIdArg(args.getUserIdArg());
        fla.invoke();
        return (List)fla.getListRet();
    }

    Criteria createCriteria() {
        return HibernateSessionBuilder.
                getSessionFactory().
                getCurrentSession().
                createCriteria(Party.class, "par").
                createAlias("par.contactSystem", "csy", LEFT_OUTER_JOIN);
    }

    private Criteria buildWhere(Criteria criteria) {
        if (args.getUpdatedDateArg() != null) {
            criteria.add(gt("par.pdatedDate", args.getUpdatedDateArg()));
        }
        if (args.getCreatedDateMinimumArg() != null) {
            criteria.add(gt("par.createdDate", args.getCreatedDateMinimumArg()));
        }
        if (args.getCreatedDateMaximumArg() != null) {
            criteria.add(le("par.createdDate", args.getCreatedDateMaximumArg()));
        }
        if (args.getPartyIdArg() != null && args.getPartyIdArg().length() != 0) {
            criteria.add(ilike("par.partyId", args.getPartyIdArg(), MatchMode.ANYWHERE));
        }
        if (args.getLegalNameArg() != null && args.getLegalNameArg().length() != 0) {
            criteria.add(ilike("par.legalName", args.getLegalNameArg(), MatchMode.ANYWHERE));
        }
        if (args.getEmailAddressArg() != null && args.getEmailAddressArg().length() != 0) {
            criteria.add(eq("csy.class", EmailAddress.class));
            criteria.add(ilike("csy.fullAddress", args.getEmailAddressArg(), MatchMode.ANYWHERE));
        }
        if (args.getMobilephoneNumberArg() != null && args.getMobilephoneNumberArg().length() != 0) {
            criteria.add(eq("csy.class", PhoneNumber.class));
            criteria.add(eq("csy.type", MOBILE_PHONE_NUMBER));
            criteria.add(ilike("csy.fullAddress", args.getMobilephoneNumberArg(), MatchMode.ANYWHERE));
        }
        if (args.getTelephoneNumberArg() != null && args.getTelephoneNumberArg().length() != 0) {
            criteria.add(eq("csy.class", PhoneNumber.class));
            criteria.add(ilike("csy.fullAddress", args.getTelephoneNumberArg(), ANYWHERE));
        }
        if (args.getPostcodeArg() != null && args.getPostcodeArg().length() != 0) {
            criteria.add(eq("csy.class", Address.class));
            criteria.add(ilike("csy.postcode", args.getPostcodeArg(), ANYWHERE));
        }
        if (args.getAddressLineArg() != null && args.getAddressLineArg().length() != 0) {
            criteria.add(eq("csy.class", Address.class));
            criteria.add(Restrictions.disjunction()
                    .add(ilike("csy.line1", args.getAddressLineArg(), ANYWHERE))
                    .add(ilike("csy.line2", args.getAddressLineArg(), ANYWHERE))
                    .add(ilike("csy.line3", args.getAddressLineArg(), ANYWHERE))
                    .add(ilike("csy.line4", args.getAddressLineArg(), ANYWHERE))
                    .add(ilike("csy.line5", args.getAddressLineArg(), ANYWHERE))
                    .add(ilike("csy.town", args.getAddressLineArg(), ANYWHERE))
                    .add(ilike("csy.county", args.getAddressLineArg(), ANYWHERE))
                    .add(ilike("csy.country", args.getAddressLineArg(), ANYWHERE)));
        }
        if (args.getDateOfBirthArg() != null) {
            criteria.add(eq("class", "PersonalProposer"));
            criteria.add(eq("dateOfBirth.date", args.getDateOfBirthArg()));
        }

        return criteria;
    }

    private Criteria buildOrder(Criteria criteria) {
        if (args.getOrderByArg()!=null) {
            switch(args.getOrderByArg()) {
            case "Created Date ASC":
                criteria.addOrder(asc("createdDate"));
                break;
            case "Created Date DESC":
                criteria.addOrder(desc("createdDate"));
                break;
            default:
                criteria.addOrder(desc("createdDate"));
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
