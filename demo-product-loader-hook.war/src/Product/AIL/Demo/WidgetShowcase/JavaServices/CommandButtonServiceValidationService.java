
/* Copyright Applied Industrial Logic Limited 2017. All rights reserved. */

import com.ail.core.Attribute;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.util.Functions;

public class CommandButtonServiceValidationService {
    public static void invoke(ExecutePageActionArgument args) {

        Attribute attr = (Attribute) args.getModelArgRet().xpathGet("/asset[id='commandActionButton']/attribute[id='string']");

        Functions.removeErrorMarkers(attr);

        if (!"Service Validation".equals(attr.getValue())) {
            Functions.addError("CommandButtonServiceValidationService", "Value != 'Service Validation'", attr);
            args.setValidationFailedRet(true);
        }
    }
}