/* Copyright Applied Industrial Logic Limited 2008. All rights reserved. */
import java.text.SimpleDateFormat;
import java.util.Date;

import com.ail.core.Attribute;
import com.ail.core.BaseException;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;
import com.ail.pageflow.RenderingError;
import com.liferay.portal.model.User;

/**
 * This service is called immediately before the first page of the quotation pageflow
 * is displayed.
 */
public class QuoteSetupService {
    /**
     * Service entry point.
     * @param args Contains the quotation object to me modified
     */
    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        Policy quote=(Policy)args.getModelArgRet();

        /* Populate the application date with today's date */
        Attribute applicationDate=(Attribute)quote.xpathGet("/asset[id='company']/attribute[id='applicationFormDate']");
        if (applicationDate.getValue()==null || "".equals(applicationDate.getValue())) {
            SimpleDateFormat sdf=new SimpleDateFormat(applicationDate.getFormatOption("pattern"));
            applicationDate.setValue(sdf.format(new Date()).toString());
        }

        /* Populate the agent's name attribute based on the current user */
        try {
            User user=PageFlowContext.getRequestWrapper().getUser();
            String agentName=user.getFirstName()+" "+user.getLastName();
            Attribute agentAttr=(Attribute)quote.xpathGet("/asset[id='company']/attribute[id='agentName']");
            agentAttr.setValue(agentName);
        }
        catch(Throwable e) {
            throw new RenderingError("Failed to set the agent's name attribute", e);
        }

        args.setModelArgRet(quote);
    }
}