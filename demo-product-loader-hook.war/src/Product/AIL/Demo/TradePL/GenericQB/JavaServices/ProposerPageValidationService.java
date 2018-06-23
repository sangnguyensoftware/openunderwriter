import java.util.Calendar;
import java.util.Date;

import com.ail.core.Attribute;
import com.ail.core.Type;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.util.Functions;
import com.ail.util.DateOfBirth;

/**
 */
public class ProposerPageValidationService
{
	public static void invoke(ExecutePageActionArgument args) throws Exception
	{
		Type quote = args.getModelArgRet();
		Attribute question = (Attribute)quote.xpathGet("proposer/attribute[id='dateOfBirth']", Attribute.class);

		// if errors already exist, skip futher validation
		if (!Functions.hasErrorMarkers(question))
		{

			// get the value of the attribute as a date
			Date answer = (Date)question.getObject();

			// Calculate today - dob must not be in the future
			Calendar limit = Calendar.getInstance();
			if (answer.after(limit.getTime()))
			{
				args.setValidationFailedRet(true);
				question.addAttribute(new Attribute("error.ProposerPageValidation", "proposer must be 16 or older", "string"));
			}
			else
			{
				// get the value of the attribute as a date
				DateOfBirth dob = new DateOfBirth(answer);
				if (dob.currentAge() < 16)
				{
					args.setValidationFailedRet(true);
					question.addAttribute(new Attribute("error.ProposerPageValidation", "proposer must be 16 or older", "string"));
				}
			}
		}

		question = (Attribute)quote.xpathGet("proposer/attribute[id='title']", Attribute.class);

		if (!Functions.hasErrorMarkers(question))
		{
			String answer = (String)quote.xpathGet("proposer/attribute[id='title']/value");
			if (answer.equals("-"))
			{
				args.setValidationFailedRet(true);
				question.addAttribute(new Attribute("error.ProposerPageValidation", "Please select a valid title", "string"));
			}
		}
	}
}