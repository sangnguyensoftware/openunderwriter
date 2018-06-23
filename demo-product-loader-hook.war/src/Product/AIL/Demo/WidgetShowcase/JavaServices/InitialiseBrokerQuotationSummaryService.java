/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
import static com.ail.financial.FinancialFrequency.MONTHLY;
import static com.ail.financial.FinancialFrequency.ONE_TIME;
import static com.ail.financial.MoneyProvisionPurpose.PREMIUM;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.ail.core.VersionEffectiveDate;
import com.ail.financial.CardIssuer;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.DirectDebit;
import com.ail.financial.MoneyProvision;
import com.ail.financial.PaymentCard;
import com.ail.financial.PaymentSchedule;
import com.ail.insurance.policy.PersonalProposer;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.party.Address;
import com.ail.party.Title;

/**
 * Initialise the necessary fields to enable to ProposerBrokerSummary widget for a personal proposer.
 */
public class InitialiseBrokerQuotationSummaryService {
    /**
     * Service entry point.
     */
    public static void invoke(ExecutePageActionArgument args) {
        Calendar policyExpiryDate = null;
        Calendar quoteExpiryDate = null;
        Calendar quoteDate = null;

        Policy policy = (Policy) args.getModelArgRet();

        policy.setVersionEffectiveDate(new VersionEffectiveDate());

        quoteDate=Calendar.getInstance();
        quoteDate.set(2012, 7, 7);

        /* Set the quote and expiry dates to today */
        policy.setQuotationDate(quoteDate.getTime());
        policy.setInceptionDate(quoteDate.getTime());

        /* Set the policy's expiry date to yesterday + 1 year */
        policyExpiryDate = (Calendar)quoteDate.clone();
        policyExpiryDate.add(Calendar.DATE, -1);
        policyExpiryDate.add(Calendar.YEAR, 1);
        policy.setExpiryDate(policyExpiryDate.getTime());

        /* Set the quote expiry date to today + 30 days */
        quoteExpiryDate = (Calendar)quoteDate.clone();
        quoteExpiryDate.add(Calendar.DATE, 30);
        policy.setQuotationExpiryDate(quoteExpiryDate.getTime());

        PersonalProposer proposer=new PersonalProposer();

    	proposer.setTitle(Title.MR);
        proposer.setOtherTitle(null);
        proposer.setFirstName("Tester");
        proposer.setSurname("Testerton");
        proposer.setAddress(new Address());
        proposer.getAddress().setLine1("Address Line 1");
        proposer.getAddress().setLine2("Address Line 2");
        proposer.getAddress().setLine3("Address Line 3");
        proposer.getAddress().setLine4("Address Line 4");
        proposer.getAddress().setLine5("Address Line 5");
        proposer.getAddress().setTown("Town");
        proposer.getAddress().setCounty("County");
        proposer.getAddress().setPostcode("POSTCODE");
        proposer.getAddress().setCountry("Country");
        proposer.setTelephoneNumber("01234 56789");
        proposer.setMobilephoneNumber("02345 67890");
        proposer.setEmailAddress("testing@testing.com");

        policy.setClient(proposer);

        /* Setup some payment detail to display */
        CurrencyAmount premium=policy.getTotalPremium();
        setupPaymentDetails(premium, policy);
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
        Calendar cardStartDate=Calendar.getInstance();
        Calendar cardExpiryDate=Calendar.getInstance();

        /* Calculate the installment prices, and the first payment */
        CurrencyAmount pmnt=new CurrencyAmount(premium.getAmount().divide(new BigDecimal(100)).intValue()*10, Currency.GBP);
        CurrencyAmount fstPmnt=new CurrencyAmount(pmnt.getAmount().add(premium.getAmount().remainder(new BigDecimal(100))), Currency.GBP);

        cardStartDate.set(2012, 7, 7);
        cardExpiryDate.set(2013, 7, 6);

        PaymentCard paymentCard=new PaymentCard();
        paymentCard.setCardHoldersName("Tester Testerton");
        paymentCard.setCardNumber("1111 2222 3333 4444");
        paymentCard.setStartDate(cardStartDate.getTime());
        paymentCard.setExpiryDate(cardExpiryDate.getTime());
        paymentCard.setIssuer(CardIssuer.MASTERCARD);

        DirectDebit directDebit=new DirectDebit();
        directDebit.setAccountNumber("01200123");
        directDebit.setSortCode("01-02-03");

        /* Put the payment details into a list */
        List<MoneyProvision> payments=new ArrayList<MoneyProvision>();
        payments.add(new MoneyProvision(1, fstPmnt, PREMIUM, ONE_TIME, paymentCard, null, null, "A deposit of "+fstPmnt.toString()));
        payments.add(new MoneyProvision(9, pmnt, PREMIUM, MONTHLY, directDebit, null, null, "Followed by 9 monthly payments of "+pmnt.toString()));

        /* Turn the list into a schedule */
        PaymentSchedule sched=new PaymentSchedule(payments, null);
        sched.setDescription("A deposit by payment card of "+fstPmnt.toString()+" followed by nine monthly installments "+pmnt.toString()+" by direct debit totalling "+sched.calculateTotal().toString()+".");

        /* Add the schedule to the list of options */
        policy.setPaymentDetails(sched);
    }
}
