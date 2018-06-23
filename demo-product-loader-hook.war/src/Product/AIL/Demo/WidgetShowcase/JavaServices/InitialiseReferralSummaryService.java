/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
import java.util.Calendar;

import com.ail.core.VersionEffectiveDate;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

public class InitialiseReferralSummaryService {
    /**
     * Service entry point.
     * 
     * @param args
     *            Contains the quotation object available for initialisation.
     */
    public static void invoke(ExecutePageActionArgument args) {
        Calendar calendar = Calendar.getInstance();

        Policy policy = (Policy) args.getModelArgRet();

        policy.setVersionEffectiveDate(new VersionEffectiveDate());
        
        /* Set the quote and expiry dates to today */
        calendar.set(2012,  8, 31);
        policy.setQuotationDate(calendar.getTime());

        /* Set the quote expiry date to today + 30 days */
        calendar.set(2012,  9, 30);
        policy.setQuotationExpiryDate(calendar.getTime());

        /* Set policy inception date */
        calendar.set(2012,  9, 31);
        policy.setInceptionDate(calendar.getTime());

        /* Set the policy's expiry */
        calendar.set(2013,  8, 30);
        policy.setExpiryDate(calendar.getTime());

    }
}