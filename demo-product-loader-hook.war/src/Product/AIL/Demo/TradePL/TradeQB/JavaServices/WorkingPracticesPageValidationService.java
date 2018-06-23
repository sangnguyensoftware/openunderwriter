import com.ail.core.Attribute;
import com.ail.core.Type;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;


/**
 * Validation and processing script for Working Practices Page
 * Actions performed:
 * 1. Convert drop down NbrYearsInsuredOptions value to integer NbrYearsInsured in quote model
 */
public class WorkingPracticesPageValidationService
{
	public static void invoke(ExecutePageActionArgument args) throws Exception
	{


		Type quote = args.getModelArgRet();

		// convert drop down NbrYearsInsuredOptions value to integer NbrYearsInsured in quote model
		Attribute questionNbrYearsInsured = (Attribute)quote.xpathGet("/attribute[id='Policy']/attribute[id='NbrYearsInsuredOptions']", Attribute.class);
		int nbrYearsInsured = ((Number)questionNbrYearsInsured.getObject()).intValue();
		quote.xpathSet("/attribute[id='Policy']/attribute[id='NbrYearsInsured']/value",nbrYearsInsured);	


	}
}
