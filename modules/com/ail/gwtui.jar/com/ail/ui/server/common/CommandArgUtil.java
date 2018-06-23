package com.ail.ui.server.common;

import static com.ail.ui.client.search.SearchService.ALL_POLICY_STATUS_CODE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.StringUtils;

import com.ail.core.CoreProxy;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.search.PolicySearchService.PolicySearchCommand;
import com.ail.ui.shared.model.AdvancedSearchCriteria;

public final class CommandArgUtil {

    public static PolicySearchCommand getPolicySearchCommand(AdvancedSearchCriteria criteria, CoreProxy core) {
        PolicySearchCommand command = core.newCommand(PolicySearchCommand.class);

        command.setPolicyNumberArg(criteria.getPolicyId());

        command.setProductTypeIdArg(criteria.getProduct());

        if (criteriaIncludesStatuses(criteria)) {
            List<PolicyStatus> policyStatuses = new ArrayList<>();
            for (String status : criteria.getStatus()) {
                policyStatuses.add(PolicyStatus.forName(status));
            }
            command.setPolicyStatusArg(policyStatuses);
        }

        if (criteria.getCreatedDatePeriodStart().hasValue()) {
            command.setCreatedDateMinimumArg(criteria.getCreatedDatePeriodStart().getValue());
        }

        if (criteria.getCreatedDatePeriodEnd().hasValue()) {
            command.setCreatedDateMaximumArg(addDayToDate(criteria.getCreatedDatePeriodEnd().getValue()));
        }

        if (criteria.getQuoteDatePeriodStart().hasValue()) {
            command.setQuoteDateMinimumArg(criteria.getQuoteDatePeriodStart().getValue());
        }

        if (criteria.getQuoteDatePeriodEnd().hasValue()) {
            command.setQuoteDateMaximumArg(addDayToDate(criteria.getQuoteDatePeriodEnd().getValue()));
        }

        if (criteria.getExpiryDatePeriodStart().hasValue()) {
            command.setExpiryDateMinimumArg(criteria.getExpiryDatePeriodStart().getValue());
        }

        if (criteria.getExpiryDatePeriodEnd().hasValue()) {
            command.setExpiryDateMaximumArg(addDayToDate(criteria.getExpiryDatePeriodEnd().getValue()));
        }

        if (criteria.getInceptionDatePeriodStart().hasValue()) {
            command.setInceptionDateMinimumArg(criteria.getInceptionDatePeriodStart().getValue());
        }

        if (criteria.getInceptionDatePeriodEnd().hasValue()) {
            command.setInceptionDateMaximumArg(addDayToDate(criteria.getInceptionDatePeriodEnd().getValue()));
        }

        command.setPartyIdArg(criteria.getClientId());


        command.setPartyNameArg(
                getLikeCriteria(criteria.getClientName(), true));

        command.setPartyAddressArg(
                getLikeCriteria(criteria.getClientAddress(), false));

        command.setPartyEmailAddressArg(criteria.getClientEmailAddress());

        command.setUserIdArg(criteria.getUserId());

        command.setCompanyIdArg(criteria.getCompanyId());

        command.setIncludeTestArg(criteria.isIncludeTest());

        command.setIncludeSupersededArg(criteria.isIncludeSuperseded());

        command.setOrderByArg(criteria.getOrderBy());

        command.setOrderDirectionArg(criteria.getOrderDirection());
        return command;
    }

    private static boolean criteriaIncludesStatuses(AdvancedSearchCriteria criteria) {
        return !criteria.getStatus().isEmpty() && !criteria.getStatus().contains(ALL_POLICY_STATUS_CODE);
    }

    private static String getLikeCriteria(String criteria, boolean initials) {

        if (StringUtils.isEmpty(criteria)) {
            return "";
        }

        String[] words = criteria.split(" ");

        StringBuffer likeAny = new StringBuffer("%");

        for (int i = 0; i < words.length; i++) {
            likeAny.append(
                        ((initials && words[i].length() == 1) ? " ":"") // add extra space for initials
                        + words[i]
                        + "%");

        }
        return likeAny.toString();
    }

    private static Date addDayToDate(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 1);
        return c.getTime();
    }
}
