import java.util.Iterator;

import com.ail.core.Attribute;
import com.ail.core.Type;
import com.ail.insurance.policy.Asset;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.util.Functions;


/**
 * Validation and processing script for Personnel Page
 * Actions performed:
 * 1. Convert drop down NbrManualPPDOptions value to integer NbrManualPPD in quote model
 * 2. Convert drop down NbrNonManualPPDOptions value to integer NbrNonManualPPD in quote model
 * 3. Convert drop down MaximumNbrManualEmployeesOptions value to integer MaximumNbrManualEmployees in quote model
 * 4. Convert drop down MaximumNbrNonManualEmployeesOptions value to integer MaximumNbrNonManualEmployees in quote model
 * 5. Convert drop down MaximumNbrLOSCOptions value to integer MaximumNbrLOSC in quote model
 * 6. Convert drop down PPDYearsInTradeOptions value to integer PPDYearsInTrade in quote model for all named partners
 * 7. Validate director counts and names match and that there is at least 1
 * 8. Validate employee count for manual trades (at least one manual employee (inc directors)
 */
public class PersonnelPageValidationService
{
	public static void invoke(ExecutePageActionArgument args) throws Exception
	{


		Type quote = args.getModelArgRet();

		
		// 1. convert drop down NbrManualPPDOptions value to integer NbrManualPPD in quote model
		Attribute questionDirectorsManual = (Attribute)quote.xpathGet("/asset[id='Company']/attribute[id='Personnel']/attribute[id='NbrManualPPDOptions']", Attribute.class);
		int manualDirectorCount = ((Number)questionDirectorsManual.getObject()).intValue();
		quote.xpathSet("/asset[id='Company']/attribute[id='Personnel']/attribute[id='NbrManualPPD']/value",manualDirectorCount);	

		// 2. convert drop down NbrNonManualPPDOptions value to integer NbrNonManualPPD in quote model
		Attribute questionDirectorsNonManual = (Attribute)quote.xpathGet("/asset[id='Company']/attribute[id='Personnel']/attribute[id='NbrNonManualPPDOptions']", Attribute.class);
		int nonManualDirectorCount = ((Number)questionDirectorsNonManual.getObject()).intValue();
		quote.xpathSet("/asset[id='Company']/attribute[id='Personnel']/attribute[id='NbrNonManualPPD']/value",nonManualDirectorCount);	
		
		// 3. convert drop down MaximumNbrManualEmployeesOptions value to integer MaximumNbrManualEmployees in quote model
		Attribute questionEmployeesManual = (Attribute)quote.xpathGet("/asset[id='Company']/attribute[id='Personnel']/attribute[id='MaximumNbrManualEmployeesOptions']", Attribute.class);
		int manualEmployeesCount = ((Number)questionEmployeesManual.getObject()).intValue();
		quote.xpathSet("/asset[id='Company']/attribute[id='Personnel']/attribute[id='MaximumNbrManualEmployees']/value",manualEmployeesCount);	

		// 4. convert drop down MaximumNbrNonManualEmployeesOptions value to integer MaximumNbrNonManualEmployees in quote model
		Attribute questionEmployeesNonManual = (Attribute)quote.xpathGet("/asset[id='Company']/attribute[id='Personnel']/attribute[id='MaximumNbrNonManualEmployeesOptions']", Attribute.class);
		int nonManualEmployeesCount = ((Number)questionEmployeesNonManual.getObject()).intValue();
		quote.xpathSet("/asset[id='Company']/attribute[id='Personnel']/attribute[id='MaximumNbrNonManualEmployees']/value",nonManualEmployeesCount);	

		// 5. convert drop down MaximumNbrLOSCOptions value to integer MaximumNbrLOSC in quote model
		Attribute questionLOSC = (Attribute)quote.xpathGet("/asset[id='Company']/attribute[id='Personnel']/attribute[id='MaximumNbrLOSCOptions']", Attribute.class);
		int loscCount = ((Number)questionLOSC.getObject()).intValue();
		quote.xpathSet("/asset[id='Company']/attribute[id='Personnel']/attribute[id='MaximumNbrLOSC']/value",loscCount);	
		
		// 6. convert drop down PPDYearsInTradeOptions value to integer PPDYearsInTrade in quote model for all named partners
		Iterator<Asset> names = quote.xpathIterate("/asset[assetTypeId='PartnerAsset']", Asset.class);  
		while(names.hasNext())
		{
			Asset name = (Asset)names.next();
			Attribute questionDirectorsExperience = (Attribute)name.xpathGet("/attribute[id='PPDYearsInTradeOptions']", Attribute.class);
			int years = ((Number)questionDirectorsExperience.getObject()).intValue();
			name.xpathSet("/attribute[id='PPDYearsInTrade']/value",years);				
			
		}


		// any director counts over 11 should be set to 11 for name count validation purposes
		if(manualDirectorCount>11){manualDirectorCount=11;}
		if(nonManualDirectorCount>11){nonManualDirectorCount=11;}
		
		
		// check manual or non-manual
		String includesTradeType1 = (String)quote.xpathGet("/asset[id='Company']/attribute[id='IncludesTradeType1']/value");

		// 7. check directors entered correctly
		// if errors already exist, skip futher validation
		if (!Functions.hasErrorMarkers(questionDirectorsManual) && !Functions.hasErrorMarkers(questionDirectorsNonManual))
		{

			// get counters
			int totalDirectorCount = 0;
			
			int maxDirectorCount = 10;
 
			// if non manual trades only, do not include manual trades director count
			if ("No".equals(includesTradeType1))
			{
				totalDirectorCount = nonManualDirectorCount;
			}
			else
			{
				totalDirectorCount = nonManualDirectorCount + manualDirectorCount;
				if(!(nonManualDirectorCount>=11 || manualDirectorCount>=11)){maxDirectorCount=20;}
			}

			// added, user must have at least one
			if (totalDirectorCount > 0)
			{

				// count numebr of names
				int numberOfNames = ((Number)quote.xpathGet("count(/asset[assetTypeId='PartnerAsset'])")).intValue();

				// cannot have less names that numbered directors
				if (numberOfNames < totalDirectorCount)
				{
					args.setValidationFailedRet(true);

					// if count does not exceed max director count ('11 or more' not selected), then count specific error message applied
					if(totalDirectorCount<=maxDirectorCount){
						questionDirectorsNonManual.addAttribute(new Attribute("error.PersonnelPageValidation", "All " + totalDirectorCount + " partners, principles and directors must be listed", "string"));
					}
					else{
						questionDirectorsNonManual.addAttribute(new Attribute("error.PersonnelPageValidation", "All partners, principles and directors must be listed", "string"));
					}
				}
			}
			else
			{
				// no names added error
                        	args.setValidationFailedRet(true);
				questionDirectorsNonManual.addAttribute(new Attribute("error.PersonnelPageValidation", "You must specify at least one partner, principle or director", "string"));
			}
			

		}


		// 8. check if manual trade, that at least one director or employee is manual
		// if errors already exist, skip futher validation
		if (!Functions.hasErrorMarkers(questionEmployeesManual) && "Yes".equals(includesTradeType1))
		{

			int totalCount = manualEmployeesCount + manualDirectorCount;

			// check if manual trade, that at least one director or employee is manual
			if (totalCount == 0)
			{
				args.setValidationFailedRet(true);
				questionEmployeesManual.addAttribute(new Attribute("error.PersonnelPageValidation", "You must specify at least one manual director or employee", "string"));
			}
			

		}


	}
}