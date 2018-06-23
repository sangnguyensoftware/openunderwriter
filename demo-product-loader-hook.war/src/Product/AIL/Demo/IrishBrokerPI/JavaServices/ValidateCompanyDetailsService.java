import java.util.Iterator;

import com.ail.core.Attribute;
import com.ail.core.Type;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.util.Functions;

/**
 * Perform validation on the 'type of business' question. The user must select
 * at least one of the options from listed. This service checks that at, and
 * adds an error message if necessary
 */
public class ValidateCompanyDetailsService {
    public static void invoke(ExecutePageActionArgument args) throws Exception {
        boolean error = true; // assume guilty until proven innocent
        Type quote = args.getModelArgRet();
        Type question = (Type) quote.xpathGet("/asset[id='company']/attribute[id='business']", Attribute.class);

        // remote any existing error markers for this question
        Functions.removeErrorMarkers(question);

        for (Iterator<String> it = question.xpathIterate("attribute/value", String.class); it.hasNext();) {
            if ("Yes".equals(it.next())) {
                error = false;
            }
        }

        if (error) {
            args.setValidationFailedRet(true);
            question.addAttribute(new Attribute("error.ValidateCompanyDetailService", "please set at least one of the following to 'Yes'", "string"));
        }
    }
}
