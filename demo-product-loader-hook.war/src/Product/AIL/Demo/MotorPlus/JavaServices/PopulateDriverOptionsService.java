import java.util.Iterator;

import com.ail.core.Attribute;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.insurance.policy.Policy;

public class PopulateDriverOptionsService {
    public static void invoke(ExecutePageActionArgument args) {
        Policy quote = (Policy) args.getModelArgRet();

        int i = 1;
        String choice = "choice,options=-1#?";

        /* Build a 'choice' format listing all the driver's names */
        for (Iterator<String> it = quote.xpathIterate("asset[assetTypeId='DriverAsset']/attribute[id='name']/value", String.class); it.hasNext(); i++) {
            choice = choice + "|" + i + "#" + it.next();
        }

        /* Add the choice to all accident history driver attributes */
        for (Iterator<Attribute> it = quote.xpathIterate("asset[assetTypeId='AccidentHistoryAsset']/attribute[id='driver']", Attribute.class); it.hasNext();) {
            ((Attribute)it.next()).setFormat(choice);
        }

        /* Add the choice to all conviction history driver attributes */
        for (Iterator<Attribute> it = quote.xpathIterate("asset[assetTypeId='ConvictionHistoryAsset']/attribute[id='driver']", Attribute.class); it.hasNext();) {
            ((Attribute)it.next()).setFormat(choice);
        }
    }
}
