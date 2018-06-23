/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
import static com.ail.financial.FinancialFrequency.MONTHLY;
import static com.ail.financial.FinancialFrequency.ONE_TIME;
import static com.ail.financial.MoneyProvisionPurpose.PREMIUM;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.ail.core.CoreProxy;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.DirectDebit;
import com.ail.financial.MoneyProvision;
import com.ail.financial.PaymentCard;
import com.ail.financial.PaymentSchedule;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.util.Rate;

/**
 * Initialise the necessary fields to enable to PaymentOptions widget to render.
 */
public class InitialisePaymentOptionsService {
    /**
     * Service entry point.
     */
    public static void invoke(ExecutePageActionArgument args) {
        Policy policy = (Policy) args.getModelArgRet();

        if (policy.getPaymentOption()==null || policy.getPaymentOption().size()==0) {
            policy.setPaymentOption(new ArrayList<PaymentSchedule>());

            CurrencyAmount premium=new CurrencyAmount(1000.0, Currency.GBP);
            
            addOptionOne(premium, policy);        
            addOptionTwo(premium, policy);        
            addOptionThree(premium, policy);
        }
    }
        
    /**
     * First option: Whole premium in one payment by payment card.
     * @param premium Premium to base the schedule on
     * @param policy Schedule will be added to args.getOptionsRet() 
     */
    private static void addOptionOne(CurrencyAmount premium, Policy policy) {
        List<MoneyProvision> payments=new ArrayList<MoneyProvision>();
        payments.add(new MoneyProvision(1, premium, PREMIUM, ONE_TIME, new PaymentCard(), null, null, null));
        policy.getPaymentOption().add(new PaymentSchedule(payments, "A single payment by payment card of "+premium.toString()));
    }

    /**
     * Second option: Whole premium in one payment by direct debit. 
     * @param premium Premium to base the schedule on
     * @param policy Schedule will be added to args.getOptionsRet() 
     */
    private static void addOptionTwo(CurrencyAmount premium, Policy policy) {
        List<MoneyProvision> payments=new ArrayList<MoneyProvision>();
        payments.add(new MoneyProvision(1, premium, PREMIUM, ONE_TIME, new DirectDebit(), null, null, null));
        policy.getPaymentOption().add(new PaymentSchedule(payments, "A single payment by direct debit of "+premium.toString()));
    }
        
    /**
     * Third option: Deposit + 9 monthly payments. 
     * @param premium Premium to base the schedule on
     * @param policy Schedule will be added to args.getOptionsRet() 
     */ 
    private static void addOptionThree(CurrencyAmount premium, Policy policy) {
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
            payments.add(new MoneyProvision(1, fstPmnt, PREMIUM, ONE_TIME, new PaymentCard(), null, null, "A deposit of "+fstPmnt.toString()));
            payments.add(new MoneyProvision(9, pmnt, PREMIUM, MONTHLY, new DirectDebit(), null, null, "Followed by 9 monthly payments of "+pmnt.toString()));
    
            /* Turn the list into a schedule */
            PaymentSchedule sched=new PaymentSchedule(payments, null);
            sched.setDescription("A deposit by payment card followed by nine monthly installments by direct debit totalling "+sched.calculateTotal().toString()+" (includes an "+adminFee+" admin fee)");
    
            /* Add the schedule to the list of options */
            policy.getPaymentOption().add(sched);
        }
        catch(CloneNotSupportedException e) {
            /* A clone exception is a system error - nothing we can handle here. We'll log it and 
             * just leave this option out of the list.
             */
            new CoreProxy().logError("Payment option assessment failed with: "+e);
        }
    }
}