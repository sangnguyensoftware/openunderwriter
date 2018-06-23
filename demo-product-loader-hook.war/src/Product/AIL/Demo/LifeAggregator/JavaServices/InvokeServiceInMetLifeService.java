
/* Copyright Applied Industrial Logic Limited 2016. All rights reserved. */
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.pageflow.ExecutePageActionInSubProductService.ExecutePageActionInSubProductCommand;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

/*
 * Example service which demonstrates how to invoke a service in the context of a specific sub-product.
 */
public class InvokeServiceInMetLifeService {

    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        ExecutePageActionInSubProductCommand command = (ExecutePageActionInSubProductCommand)CoreContext.getCoreProxy().newCommand(ExecutePageActionInSubProductCommand.class);
        command.setProductTypeIdArg("AIL.Demo.LifeAggregator.Products.MetLife");
        command.setCommandNameArg("LabelPolicyCommand");
        command.setModelArgRet(args.getModelArgRet());
        command.invoke();
    }
}