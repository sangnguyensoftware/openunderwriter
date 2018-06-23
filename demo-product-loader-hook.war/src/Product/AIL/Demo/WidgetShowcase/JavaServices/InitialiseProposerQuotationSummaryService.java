/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
import java.util.Calendar;

import com.ail.insurance.policy.PersonalProposer;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.party.Address;
import com.ail.party.Title;

/**
 * Initialise the necessary fields to enable to ProposerQuotationSummary widget for a personal proposer.
 */
public class InitialiseProposerQuotationSummaryService {
    /**
     * Service entry point.
     */
    public static void invoke(ExecutePageActionArgument args) {
        Calendar policyExpiryDate = null;
        Calendar quoteExpiryDate = null;
        Calendar quoteDate = null;

        Policy policy = (Policy) args.getModelArgRet();

        quoteDate=Calendar.getInstance();

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
   }
}