/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
import com.ail.core.Attribute;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.insurance.policy.PersonalProposer;
import com.ail.insurance.policy.Policy;
import com.ail.party.Address;
import com.ail.party.Title;

/**
 * Initialise the necessary fields to enable to ProposerDetails widget for a personal proposer.
 */
public class InitialiseProposerDetailsPersonalService {
    /**
     * Service entry point.
     */
    public static void invoke(ExecutePageActionArgument args) {
        Policy policy = (Policy) args.getModelArgRet();
        
        /*
         * Ensure that the personal proposer is only setup once using the
         * "personalSetupDone" attribute as a flag. As the service gets invoked every
         * time the page is visited, running it more than once will mess up validation
         * by removing all the errors.
         */
        if (policy.xpathGet("/proposer/attribute[id='personalSetupDone']", null, Attribute.class) == null) {
            PersonalProposer proposer=new PersonalProposer();

            proposer.addAttribute(new Attribute("personalSetupDone", "Yes", "yesorno"));

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
}