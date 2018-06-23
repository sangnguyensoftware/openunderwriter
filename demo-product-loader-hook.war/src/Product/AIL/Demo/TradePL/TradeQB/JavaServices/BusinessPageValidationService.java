import com.ail.core.Attribute;
import com.ail.core.Type;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;


/**
 * Validation and processing script for Business Page
 * Actions performed:
 * 1. Convert drop down YearsBusinessEstablishedOptions value to integer YearsBusinessEstablished in quote model
 */
public class BusinessPageValidationService
{
	public static void invoke(ExecutePageActionArgument args) throws Exception
	{
		Type quote = args.getModelArgRet();

		// 1. convert drop down YearsBusinessEstablishedOptions value to integer YearsBusinessEstablished in quote model
		Attribute questionYearsBusinessEstablished = (Attribute)quote.xpathGet("/asset[id='Company']/attribute[id='Business']/attribute[id='YearsBusinessEstablishedOptions']", Attribute.class);
		int yearsBusinessEstablished = ((Number)questionYearsBusinessEstablished.getObject()).intValue();
		quote.xpathSet("/asset[id='Company']/attribute[id='Business']/attribute[id='YearsBusinessEstablished']/value",yearsBusinessEstablished);	
	}
}
