package com.ail.demo.equineplus;
import com.ail.core.Attribute;
import com.ail.core.product.ProductServiceCommand;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

/**
 * The user selects from a drop down list which describes the horses age in
 * common equine terms, e.g. "yearling, foal, etc, etc". This service converts
 * the description into a number which rules can later use to assess risk.
 * 
 * This class uses the @ServiceCommand annotation to dynamically add this as a service to the configuration
 * without need for entries in the Registry, specifying the command name to be set to just "CalculateHorsesAge"
 * with no package name or anything.
 */
@ProductServiceCommand(commandName = "CalculateHorsesAge")
public class CalculateHorsesAge {
    /**
     * The age in years happens to match the choice list item IDs, so this is
     * simple.
     * 
     * @param args
     *            This contains the quote we need to modify
     */
    public static void invoke(ExecutePageActionArgument args) {
        Policy quote = (Policy) args.getModelArgRet();

        // Get the ageDescription attribute - which holds the user's selection
        Attribute ageDescription = (Attribute) quote.xpathGet("asset[assetTypeId='HorseAsset'][1]/attribute[@id='ageDescription']");

        // Get the age attribute - where we want to store the number of years
        Attribute age = (Attribute) quote.xpathGet("asset[assetTypeId='HorseAsset'][1]/attribute[@id='age']");

        // The choice list's ID _is_ the age, and we can get the ID using
        // getObject.
        age.setValue(ageDescription.getObject().toString());
    }
}
