/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

/**
 * Copy the iWinPay principal into the proposer's mobile phone number.
 * This is a temporary fix to allow payments to be made. It will be removed
 * once we have iWinPay payment requests working with mobile numbers.
 */
public class PopulateIWinPayUserService {
    public static void invoke(ExecutePageActionArgument args) {
        Policy policy = (Policy) args.getModelArgRet();
        String username = (String)policy.xpathGet("/attribute[id='iWinPayUsername']/value");
        policy.xpathSet("/proposer/mobilephoneNumber", username);
    }
}
