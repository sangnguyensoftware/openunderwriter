import java.util.Calendar;
import java.util.Date;

import com.ail.core.Attribute;
import com.ail.core.Type;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.util.Functions;

/**
 * Validate that requested cover start date is not in the past, and not more than 30 days in the future
 */
public class CoverPageValidationService {
    public static void invoke(ExecutePageActionArgument args) throws Exception {
        Type quote = args.getModelArgRet();
        Attribute question = (Attribute)quote.xpathGet("/attribute[id='Policy']/attribute[id='PeriodFrom']", Attribute.class);
        
        // if errors already exist, skip futher validation
        if(!Functions.hasErrorMarkers(question)){
        
            // get the value of the attribute as a date
            Date answer=(Date)question.getObject();

            // Calculate today, test cover date not in past
            Calendar limit=Calendar.getInstance();
            limit.add(Calendar.DATE, -1);       
            if (answer.before(limit.getTime())) {
        	args.setValidationFailedRet(true);
                question.addAttribute(new Attribute("error.CoverPageValidation", "must not be in the past", "string"));
            }

            // Calculate today + 30 days, test cover date within 30 days
            limit.add(Calendar.DATE, 31);       
            if (answer.after(limit.getTime())) {
        	args.setValidationFailedRet(true);
                question.addAttribute(new Attribute("error.CoverPageValidation", "must be within 30 days after today", "string"));
            }
        }
    }
}
