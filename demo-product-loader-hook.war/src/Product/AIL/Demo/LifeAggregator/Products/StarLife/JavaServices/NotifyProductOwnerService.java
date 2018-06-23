/* Copyright Applied Industrial Logic Limited 2016. All rights reserved. */
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

public class NotifyProductOwnerService {
    public static void invoke(ExecutePageActionArgument args) throws BaseException {
    	String productName = CoreContext.getProductName();
    	CoreProxy coreProxy = CoreContext.getCoreProxy();
    	String recipient = coreProxy.getParameterValue("ReferralNotificationEmail");

    	coreProxy.logInfo("Stub email notification called for: "+productName+", email recepient: "+recipient);
    }
}