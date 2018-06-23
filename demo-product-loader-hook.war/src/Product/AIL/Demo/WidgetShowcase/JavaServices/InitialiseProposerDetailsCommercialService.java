/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
import com.ail.core.Attribute;
import com.ail.insurance.policy.CommercialProposer;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.party.Address;
import com.ail.party.Person;
import com.ail.party.Title;

/**
 * Initialise the necessary fields to enable to Commercial ProposerDetails widget to render.
 */
public class InitialiseProposerDetailsCommercialService {
	/**
	 * Service entry point.
	 */
	public static void invoke(ExecutePageActionArgument args) {
		Policy policy = (Policy) args.getModelArgRet();

		/* Ensure that the commercial proposer is only setup once using the
		 * "commercialSetupDone" attribute as a flag. As the service gets
		 * invoked every time the page is visited, running it more than once
		 * will mess up validation by removing all the errors.
		 */
		if (policy.xpathGet("/proposer/attribute[id='commercialSetupDone']", "").getClass() == String.class) {
			CommercialProposer proposer = new CommercialProposer();

			proposer.addAttribute(new Attribute("commercialSetupDone", "Yes", "yesorno"));

			proposer.setCompanyName("Company Name Limited");

			proposer.setContact(new Person());
			proposer.getContact().setTitle(Title.MR);
			proposer.getContact().setFirstName("Tester");
			proposer.getContact().setSurname("Testerton");

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