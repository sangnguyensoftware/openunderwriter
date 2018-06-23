/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.ail.core.CoreProxy;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.Currency;
import com.ail.financial.FinancialFrequency;
import com.ail.financial.DirectDebit;
import com.ail.financial.MoneyProvision;
import com.ail.financial.MoneyProvisionPurpose;
import com.ail.financial.PaymentCard;
import com.ail.financial.PaymentSchedule;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.insurance.policy.Policy;
import com.ail.util.Rate;

/**
 * Initialise the necessary fields to enable to PaymentDetails widget to render.
 */
public class InitialisePaymentDetailsService {
    /**
     * Service entry point.
     */
    public static void invoke(ExecutePageActionArgument args) {
        Policy policy = (Policy) args.getModelArgRet();

        if (policy.getPaymentDetails()==null) {
            Calendar policyExpiryDate = null;
            Calendar quoteExpiryDate = null;
	        
	        /* Set the quote and expiry dates to today */
	        policy.setQuotationDate(new Date());
	        policy.setInceptionDate(new Date());
	
	        /* Set the policy's expiry date to yesterday + 1 year */
	        policyExpiryDate = Calendar.getInstance();
	        policyExpiryDate.add(Calendar.DATE, -1);
	        policyExpiryDate.add(Calendar.YEAR, 1);
	        policy.setExpiryDate(policyExpiryDate.getTime());
	
	        /* Set the quote expiry date to today + 30 days */
	        quoteExpiryDate = Calendar.getInstance();
	        quoteExpiryDate.add(Calendar.DATE, 30);
	        policy.setQuotationExpiryDate(quoteExpiryDate.getTime());
	
	        /* Setup some payment detail to display */
	        CurrencyAmount premium=new CurrencyAmount("650.20", "GBP");
	        setupPaymentDetails(premium, policy);
        }
    }
        
    /**
	 * Setup payment details for a two part payment including a deposit (by
	 * credit card) + 9 monthly payments (by direct debit).
	 * 
	 * @param premium
	 *            Premium to base the schedule on
	 * @param policy
	 *            Schedule will be added to args.getOptionsRet()
	 */ 
    private static void setupPaymentDetails(CurrencyAmount premium, Policy policy) {
        try {
            String adminFee="8%";
            
            /* Calculate the total premium inclusive of the admin fee */
            CurrencyAmount premInc=(CurrencyAmount)premium.clone();
            premInc=premInc.apply(new Rate(adminFee));
            premInc=premInc.add(premium);
    
            /* Calculate the installment prices, and the first payment */
            CurrencyAmount pmnt=new CurrencyAmount(premInc.getAmount().divide(new BigDecimal(100)).intValue()*10, Currency.GBP);
            CurrencyAmount fstPmnt=new CurrencyAmount(pmnt.getAmount().add(premInc.getAmount().remainder(new BigDecimal(100))), Currency.GBP);
    
            /* Put the payment details into a list */
            List<MoneyProvision> payments=new ArrayList<MoneyProvision>();
            payments.add(new MoneyProvision(1, fstPmnt, MoneyProvisionPurpose.PREMIUM, FinancialFrequency.ONE_TIME, new PaymentCard(), null, null, "A deposit of "+fstPmnt.toString()));
            payments.add(new MoneyProvision(9, pmnt, MoneyProvisionPurpose.PREMIUM, FinancialFrequency.MONTHLY, new DirectDebit(), null, null, "Followed by 9 monthly payments of "+pmnt.toString()));
    
            /* Turn the list into a schedule */
            PaymentSchedule sched=new PaymentSchedule(payments, null);
            sched.setDescription("A deposit by payment card followed by nine monthly installments by direct debit totalling "+sched.calculateTotal().toString()+" (includes an "+adminFee+" admin fee)");
    
            /* Add the schedule to the list of options */
            policy.setPaymentDetails(sched);
        }
        catch(CloneNotSupportedException e) {
            /* A clone exception is a system error - nothing we can handle here. We'll log it and 
             * just leave this option out of the list.
             */
            new CoreProxy().logError("Payment option assessment failed with: "+e);
        }
    }
}