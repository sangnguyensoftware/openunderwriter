package com.ail.demo.equineplus;
/* Copyright Applied Industrial Logic Limited 2014. All rights Reserved */
/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
import static com.ail.core.Functions.hideNull;
import static com.ail.financial.Currency.GBP;
import static com.ail.financial.ledger.AccountType.CASH;
import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_OK;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.RestfulServiceInvoker;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.product.ProductServiceCommand;
import com.ail.financial.service.CreateAccountService.CreateAccountCommand;
import com.ail.insurance.policy.PersonalProposer;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionCommand;
import com.ail.pageflow.PageFlowContext;
import com.ail.party.Address;
import com.ail.party.ContactPreference;
import com.ail.party.Title;
/**
 * Generates a new EquinePlus policy and takes it through to QUOTATION status
 *
 */
@ProductServiceCommand(commandName = "CreatePolicy")
public class CreatePolicyService extends RestfulServiceInvoker {

	public static void invoke(ExecutePageActionArgument args) throws BaseException {
		new CreatePolicyService().invoke(Argument.class);
	}

	public RestfulServiceReturn service(Argument argument) throws Exception {

		if (argument.isInvalid()) {
			return new ClientError(HTTP_BAD_REQUEST, argument.error());
		}

		createPolicy();

		enrichPolicy(argument);
		
		calculatePolicy();
		
		assessPaymentOptions();

		savePolicy();

		return new Return(HTTP_OK, PageFlowContext.getPolicy());
	}

	void createPolicy() throws BaseException {
		ExecutePageActionCommand command = (ExecutePageActionCommand) PageFlowContext.getCoreProxy()
				.newCommand("CreateNewBusinessQuotationCommand", ExecutePageActionCommand.class);
		command.invoke();

		ExecutePageActionCommand applicationCommand = (ExecutePageActionCommand) PageFlowContext.getCoreProxy()
				.newCommand("QuotationToApplication", ExecutePageActionCommand.class);
		applicationCommand.setModelArgRet(PageFlowContext.getPolicy());
		applicationCommand.invoke();
	}

	void enrichPolicy(Argument argument) throws BaseException {
		Policy policy = PageFlowContext.getPolicy();
		PersonalProposer personalProposer = (PersonalProposer) policy.getClient();
		createMember(argument, personalProposer);
		createAccount(personalProposer);
		createHorse(argument, policy);
		calculateHorsesAge(policy);
	}

	private void calculateHorsesAge(Policy policy) throws BaseException {
		ExecutePageActionCommand horsesAgeCommand = (ExecutePageActionCommand) PageFlowContext.getCoreProxy()
				.newCommand("CalculateHorsesAge", ExecutePageActionCommand.class);
		horsesAgeCommand.setModelArgRet(policy);
		horsesAgeCommand.invoke();
	}

	private void calculatePolicy() throws BaseException {
		ExecutePageActionCommand premiumCommand = (ExecutePageActionCommand) PageFlowContext.getCoreProxy()
				.newCommand("PremiumCalculation", ExecutePageActionCommand.class);
		premiumCommand.setModelArgRet(PageFlowContext.getPolicy());
		premiumCommand.invoke();
	}

	private void assessPaymentOptions() throws BaseException {
		ExecutePageActionCommand assessPaymentOptionsCommand = (ExecutePageActionCommand) PageFlowContext.getCoreProxy()
				.newCommand("AssessPaymentOptions", ExecutePageActionCommand.class);
		assessPaymentOptionsCommand.setModelArgRet(PageFlowContext.getPolicy());
		assessPaymentOptionsCommand.invoke();
	}

	private void createMember(Argument argument, PersonalProposer personalProposer) {
		if (argument.client != null) {
			try {
				Title title = Title.forName(argument.client.title);
				personalProposer.setOtherTitle("");
				personalProposer.setTitle(title);
			} catch (IllegalArgumentException e) {
				personalProposer.setTitle(Title.OTHER);
				personalProposer.setOtherTitle(argument.client.title);
			}

			personalProposer.setFirstName(argument.client.firstname);
			personalProposer.setSurname(argument.client.lastname);

			Address address = personalProposer.getAddress();
			address.setLine1(hideNull(argument.client.address_1));
			address.setLine2(hideNull(argument.client.address_2));
			address.setTown(hideNull(argument.client.town));
			address.setCounty(hideNull(argument.client.county));
			address.setPostcode(hideNull(argument.client.postcode));

			personalProposer.setEmailAddress(argument.client.email);
			personalProposer.setTelephoneNumber(argument.client.phone);
			personalProposer.setMobilephoneNumber(argument.client.mobile);
			personalProposer.setContactPreference(ContactPreference.EMAIL);
		}
	}

	void createAccount(PersonalProposer personalProposer) throws BaseException {
		CreateAccountCommand cac = (CreateAccountCommand) CoreContext.getCoreProxy()
				.newCommand(CreateAccountCommand.class);

		cac.setPartyArg(personalProposer);
		cac.setCurrencyArg(GBP);
		cac.setTypeArg(CASH);

		cac.invoke();
	}

	private void createHorse(Argument argument, Policy policy) {
		if (argument.horse != null) {
			policy.xpathSet("Horse/Name/value", hideNull(argument.horse.name));
			policy.xpathSet("Horse/Age/value", hideNull(argument.horse.ageDescription));
			policy.xpathSet("Horse/Gender/value", hideNull(argument.horse.gender));
			policy.xpathSet("Colour/value", hideNull(argument.horse.colour));
			policy.xpathSet("Height/value", argument.horse.height);
			policy.xpathSet("Freeze Brand/value", hideNull(argument.horse.freezeBrand));
			policy.xpathSet("Passport Number/value", hideNull(argument.horse.passport));
			policy.xpathSet("Ownership/value", hideNull(argument.horse.owned));
			policy.xpathSet("Purchase Date/value", hideNull(argument.horse.dateBought));
			policy.xpathSet("Value/value", argument.horse.currentValue);
			policy.xpathSet("Type of Use/value", hideNull(argument.horse.typeOfUse));
		}
	}

	void savePolicy() throws BaseException {
		PageFlowContext.savePolicy();
	}

	public static class Argument {
	    private String error;
		
		public Client client;
		
		public Horse horse;
		
		public static class Client {
			public String title;
			public String firstname;
			public String lastname;
	        public String address_1;
	        public String address_2;
	        public String town;
	        public String county;
	        public String postcode;
	        public String phone;
	        public String mobile;
	        public String email;
		}
		
		public static class Horse {
			public String name;
			public String ageDescription;
			public String gender;
	        public String colour;
	        public Double height;
	        public String freezeBrand;
	        public String passport;
	        public String owned;
	        public String dateBought;
	        public Double currentValue;
	        public String typeOfUse;
		}

		String error() {
		    return error;
		}
		
		boolean error(String error) {
		    this.error = error;
		    return true;
		}
		
		boolean isInvalid() throws BaseException {
			if (client == null) {
				return error("client not defined");
			}
			if (horse == null) {
				return error("horse not defined");
			}
			if (client.title == null) {
				return error("title not defined");
			}
			if (client.firstname == null) {
				return error("firstname not defined");
			}
			if (client.lastname == null) {
				return error("lastname not defined");
			}
			if (client.address_1 == null) {
				return error("address_1 not defined");
			}
			if (client.address_2 == null) {
				return error("address_2 not defined");
			}
			if (client.town == null) {
				return error("town not defined");
			}
			if (client.county == null) {
				return error("county not defined");
			}
			if (client.postcode == null) {
				return error("postcode not defined");
			}
			if (client.phone == null) {
				return error("phone not defined");
			}
			if (client.mobile == null) {
				return error("mobile not defined");
			}
			if (client.email == null) {
				return error("email not defined");
			}
			if (horse.name == null) {
				return error("name not defined");
			}
			if (horse.ageDescription == null) {
				return error("ageDescription not defined");
			}
			if (horse.gender == null) {
				return error("gender not defined");
			}
			if (horse.colour == null) {
				return error("colour not defined");
			}
			if (horse.height == null) {
				return error("height not defined");
			}
			if (horse.freezeBrand == null) {
				return error("freezeBrand not defined");
			}
			if (horse.passport == null) {
				return error("passport not defined");
			}
			if (horse.owned == null) {
				return error("owned not defined");
			}
			if (horse.dateBought == null) {
				return error("dateBought not defined");
			}
			if (horse.currentValue == null) {
				return error("currentValue not defined");
			}
			if (horse.typeOfUse == null) {
				return error("typeOfUse not defined");
			}

	        return false;
	    }
	}

	public static class Return extends RestfulServiceReturn {
		public String policyId;
		public Long policySystemId;

		public Return(int status, Policy policy) {
			super(status);
			this.policyId = policy.getExternalSystemId();
			this.policySystemId = policy.getSystemId();
		}
	}
}
