
/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;

import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.insurance.policy.Clause;
import com.ail.insurance.policy.Policy;

/**
 * Initialise a quotation in preparation for a new business quotation. This
 * service is called at the beginning of a product's quotation page flow. At
 * this point, the quotation will already have been created and setup with
 * respect to its type definition. However, type definitions are static and
 * therefore cannot fill in variable data like today's date. This service
 * provides a convenient place to carry out such initialisations.
 */
public class InitialiseQuotationService {
    /**
     * Service entry point.
     * 
     * @param args
     *            Contains the quotation object available for initialisation.
     */
    public static void invoke(ExecutePageActionArgument args) {
        Calendar policyExpiryDate = null;
        Calendar quoteExpiryDate = null;
        Policy quote = (Policy) args.getModelArgRet();

        /* Set the quote and expiry dates to today */
        quote.setQuotationDate(new Date());
        quote.setInceptionDate(new Date());

        /* Set the policy's expiry date to yesterday + 1 year */
        policyExpiryDate = Calendar.getInstance();
        policyExpiryDate.add(Calendar.DATE, -1);
        policyExpiryDate.add(Calendar.YEAR, 1);
        quote.setExpiryDate(policyExpiryDate.getTime());

        /* Set the quote expiry date to today + 30 days */
        quoteExpiryDate = Calendar.getInstance();
        quoteExpiryDate.add(Calendar.DATE, 30);
        quote.setQuotationExpiryDate(quoteExpiryDate.getTime());

        /* Setup the date field on Clauses */
        for (Iterator<Clause> it = quote.getClause().iterator(); it.hasNext();) {
            Clause c = (Clause) it.next();
            c.setStartDate(new Date());
            c.setEndDate(policyExpiryDate.getTime());
            c.setReminderDate(quoteExpiryDate.getTime());
        }
    }
}