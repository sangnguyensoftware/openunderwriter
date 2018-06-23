import com.ail.core.Attribute;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.Proposer;

/**
 * Populate the 1st drivers name and gender based on the proposer's details. The
 * assumption here is that the proposer is the main driver, so we can use the
 * details collected on the proposer screen to populate the 1st driver's name,
 * and gender.
 */
public class PopulateDriverNameService {
    /**
     * Service entry point.
     * 
     * @param args
     *            This contains the quote we need to modify
     */
    public static void invoke(ExecutePageActionArgument args) {
        Policy quote = (Policy) args.getModelArgRet();

        /* Fetch the name attribute from the 1st driver asset */
        Attribute name = (Attribute) quote.xpathGet("asset[assetTypeId='DriverAsset'][1]/attribute[@id='name']");

        /*
         * Only set the driver's name and sex if there isn't already a value in
         * the name field.
         */
        if (name.getValue().equals("")) {
            Proposer proposer = (Proposer) quote.getClient();

            name.setValue(proposer.getFirstName() + " " + proposer.getSurname());

            Attribute sex = (Attribute) quote.xpathGet("asset[assetTypeId='DriverAsset'][1]/attribute[@id='sex']");

            if ("Mr.".contains(proposer.getTitle().longName())) {
                sex.setValue("Male");
            } else if ("Mrs.Miss.Ms.".contains(proposer.getTitle().longName())) {
                sex.setValue("Female");
            }
        }
    }
}
