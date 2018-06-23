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
import com.ail.insurance.acceptance.AssessPaymentOptionsService.AssessPaymentOptionsArgument;
import com.ail.util.Rate;

/**
 * Assess the payment options for MotorPlus. This service is called once the proposer selects to 
 * 'confirm and pay'. It prepares the three payment schedules which MotorPlus supports and returns
 * them to the caller.
 */
public class AssessPaymentOptionsRule {
    /**
     * Class entry point. 
     * @param args Service arguments
     */
    public static void invoke(AssessPaymentOptionsArgument args) {
        args.setOptionsRet(new ArrayList<PaymentSchedule>());

        CurrencyAmount premium=args.getPolicyArg().getTotalPremium();
        
        addOptionOne(premium, args);        
        addOptionTwo(premium, args);        
        addOptionThree(premium, args);
    }
    
    /**
     * First option: Whole premium in one payment by payment card.
     * @param premium Premium to base the schedule on
     * @param args Schedule will be added to args.getOptionsRet() 
     */
    private static void addOptionOne(CurrencyAmount premium, AssessPaymentOptionsArgument args) {
        List<MoneyProvision> payments=new ArrayList<MoneyProvision>();
        payments.add(new MoneyProvision(1, premium, PREMIUM, ONE_TIME, new PaymentCard(), null, null, null));
        args.getOptionsRet().add(new PaymentSchedule(payments, "A single payment by payment card of "+premium.toString()));
    }

    /**
     * Second option: Whole premium in one payment by direct debit. 
     * @param premium Premium to base the schedule on
     * @param args Schedule will be added to args.getOptionsRet() 
     */
    private static void addOptionTwo(CurrencyAmount premium, AssessPaymentOptionsArgument args) {
        List<MoneyProvision> payments=new ArrayList<MoneyProvision>();
        payments.add(new MoneyProvision(1, premium, PREMIUM, ONE_TIME, new DirectDebit(), null, null, null));
        args.getOptionsRet().add(new PaymentSchedule(payments, "A single payment by direct debit of "+premium.toString()));
    }
        
    /**
     * Third option: Deposit + 9 monthly payments. 
     * @param premium Premium to base the schedule on
     * @param args Schedule will be added to args.getOptionsRet() 
     */ 
    private static void addOptionThree(CurrencyAmount premium, AssessPaymentOptionsArgument args) {
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
            args.getOptionsRet().add(sched);
        }
        catch(CloneNotSupportedException e) {
            /* A clone exception is a system error - nothing we can handle here. We'll log it and 
             * just leave this option out of the list.
             */
            new CoreProxy().logError("Payment option assessment failed with: "+e);
        }
    }
}