/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

/**
 * Example page script which simply sets the value of an attribute
 * to "Populated!" to demonstrate that the service has executed.
 */
public class CommandButtonActionScriptService {
    public static void invoke(ExecutePageActionArgument args) {
        args.getModelArgRet().xpathSet("/asset[id='commandActionButton']/attribute[id='string']/value", "Populated!");
    }
}