
/* Copyright Applied Industrial Logic Limited 2018. All rights reserved. */
import com.ail.core.CoreContext;
import com.ail.insurance.policy.Asset;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

public class InitialiseHtmlAssetService {
    public static void invoke(ExecutePageActionArgument args) {
        Policy policy = (Policy) args.getModelArgRet();

        Asset asset = (Asset) CoreContext.getCoreProxy().newType("HtmlTextAttributeAsset");

        policy.addAsset(asset);
    }
}