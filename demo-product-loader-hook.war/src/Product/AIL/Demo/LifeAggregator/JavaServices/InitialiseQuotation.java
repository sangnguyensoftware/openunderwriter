/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
import java.util.Calendar;
import java.util.Date;

import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.insurance.policy.Policy;

/**
 * Initialize a quotation in preparation for a new business quotation. 
 * This service is called at the beginning of a product's quotation page flow. At this point, 
 * the quotation will already have been created and setup with respect to its type definition.
 * However, type definitions are static and therefore cannot fill in variable data like
 * today's date. This service provides a convenient place to carry out such initializations.
 */
public class InitialiseQuotation {
    /**
     * Service entry point.
     * @param args Contains the quotation object available for initialization. 
     */
    public static void invoke(ExecutePageActionArgument args) {
        Calendar date=null;
        Policy quote = (Policy) args.getModelArgRet();

        /* Set the quote and expiry dates to today */
        quote.setQuotationDate(new Date());
        quote.setInceptionDate(new Date());
        
        /* Set the policy's expiry date to yesterday + 1 year */
        date=Calendar.getInstance();
        date.add(Calendar.DATE, -1);
        date.add(Calendar.YEAR, 1);
        quote.setExpiryDate(date.getTime());

        /* Set the quote expiry date to today + 30 days */
        date=Calendar.getInstance();
        date.add(Calendar.DATE, 30);
        quote.setQuotationExpiryDate(date.getTime());
    }
}