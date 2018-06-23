/* Copyright Applied Industrial Logic Limited 2008. All rights reserved. */
import java.util.Calendar;
import java.util.Date;

import com.ail.core.Type;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.insurance.policy.Policy;

/**
 * A number of values within the quote can be calculated after the user has
 * submitted the first page of questions. This class deals with those
 * calculations.
 */
public class ConditionsSetupService {
    /**
     * Service entry point.
     * 
     * @param args
     *            Contains the quotation object available for initialisation.
     */
    public static void invoke(ExecutePageActionArgument args) {
        Policy quote = (Policy) args.getModelArgRet();

        setupInceptionAndExpiryDates(quote);
        setupExcess(quote);
        setupBusinessDescription(quote);
    }

    /**
     * The user specifies an inception date manually during the quote. This
     * method copies their answer from the attribute into the quote's inception
     * date field, and also calculates the expiry date.
     * 
     * @param quote
     */
    private static void setupInceptionAndExpiryDates(Policy quote) {
        Calendar date = null;

        Date incept = (Date) quote.xpathGet("/asset[id='company']/attribute[id='inceptionDate']/object", Date.class);

        /* Set the quote and expiry dates to today */
        quote.setInceptionDate(incept);

        /* Set the policy's expiry date to yesterday + 1 year */
        date = Calendar.getInstance();
        date.setTime(incept);
        date.add(Calendar.DATE, -1);
        date.add(Calendar.YEAR, 1);
        quote.setExpiryDate(date.getTime());
    }

    /**
     * The excess applicable to this quote depends on two factors: The
     * percentage of business which is related to mortgage work; and, whether
     * the proposer offers financial services. This method determines the
     * appropriate excess based on those factors.
     * 
     * @param quote
     */
    private static void setupExcess(Policy quote) {
        Number mortgageWork = (Number) quote.xpathGet("/asset[id='company']/attribute[id='mortgageWork']/object", Number.class);
        String financialServices = (String) quote.xpathGet("/asset[id='company']/attribute[id='financialServices']/value", String.class);

        if (mortgageWork.floatValue() > 15.0 || "Yes".equals(financialServices)) {
            quote.xpathSet("/asset[id='company']/attribute[id='excess']/value", "2500");
        } else {
            quote.xpathSet("/asset[id='company']/attribute[id='excess']/value", "1000");
        }
    }

    /**
     * The business description is a compound field. This method builds a single
     * string representing whatever combination of values the user may have
     * selected.
     * 
     * @param quote
     *            To modify.
     */
    private static void setupBusinessDescription(Policy quote) {
        StringBuffer compound = new StringBuffer();

        Type descriptions = (Type) quote.xpathGet("/asset[id='company']/attribute[id='business']");

        if ("Yes".equals(descriptions.xpathGet("attribute[id='generalInsuranceBroker']/value"))) {
            compound.append("General insurance broker");
        }

        if ("Yes".equals(descriptions.xpathGet("attribute[id='lifeAndPensions']/value"))) {
            if (compound.length() > 0)
                compound.append(", ");
            compound.append("Life and pensions");
        }

        if ("Yes".equals(descriptions.xpathGet("attribute[id='mortgageCompany']/value"))) {
            if (compound.length() > 0)
                compound.append(", ");
            compound.append("Mortgage company");
        }

        if ("Yes".equals(descriptions.xpathGet("attribute[id='financialAdvisor']/value"))) {
            if (compound.length() > 0)
                compound.append(", ");
            compound.append("Financial adviser");
        }

        if ("Yes".equals(descriptions.xpathGet("attribute[id='other']/value"))) {
            if (compound.length() > 0)
                compound.append(", ");
            compound.append(descriptions.xpathGet("attribute[id='otherDescription']/value"));
        }

        quote.xpathSet("/asset[id='company']/attribute[id='business']/value", compound.toString());
    }
}