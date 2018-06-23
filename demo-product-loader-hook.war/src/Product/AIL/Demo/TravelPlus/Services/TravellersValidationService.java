import java.util.Iterator;

import com.ail.core.Attribute;
import com.ail.core.Type;
import com.ail.insurance.policy.Asset;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.util.Functions;

/**
 * Validation and processing script for Travellers List
 */
public class TravellersValidationService {
    public static void invoke(ExecutePageActionArgument args) throws Exception {

        Type quote = args.getModelArgRet();

        // get field to report errors against, and check for errors already there
        Attribute fieldsAttribute = (Attribute) quote.xpathGet("/asset[assetTypeId='TravellerAsset'][1]/attribute[id='firstName']", Attribute.class);
        if (Functions.hasErrorMarkers(fieldsAttribute)) {
            return;
        }

        int adultCount = 0;
        int childCount = 0;

        // iterate through travellers
        Iterator<Asset> names = quote.xpathIterate("/asset[assetTypeId='TravellerAsset']", Asset.class);
        while (names.hasNext()) {
            Asset name = (Asset) names.next();
            Attribute age = (Attribute) name.xpathGet("/attribute[id='age']", Attribute.class);
            int years = ((Number) age.getObject()).intValue();
            if (years < 18) {
                childCount++;
            } else {
                adultCount++;
            }

        }
        int travellerCount = adultCount + childCount;

        // type of party
        Attribute partyTypeAttribute = (Attribute) quote.xpathGet("/asset[id='travel']/attribute[id='partyType']", Attribute.class);
        String partytype = partyTypeAttribute.getValue();
        if (partytype.equals("i18n_party_type_individual_option")) {
            if (childCount != 0 || adultCount != 1) {
                args.setValidationFailedRet(true);
                fieldsAttribute.addAttribute(new Attribute("error.CoverDetailPageValidation", "i18n_party_type_individual_error", "string"));
            }
        } else if (partytype.equals("i18n_party_type_couple_option")) {
            if (childCount != 0 || adultCount != 2) {
                args.setValidationFailedRet(true);
                fieldsAttribute.addAttribute(new Attribute("error.CoverDetailPageValidation", "i18n_party_type_couple_error", "string"));
            }
        } else if (partytype.equals("i18n_party_type_family_option")) {
            if (childCount < 1 || adultCount > 2 || travellerCount > 5) {
                args.setValidationFailedRet(true);
                fieldsAttribute.addAttribute(new Attribute("error.CoverDetailPageValidation", "i18n_party_type_family_error", "string"));
            }
        } else {
            if (childCount != 0 || adultCount < 2 || adultCount > 9) {
                args.setValidationFailedRet(true);
                fieldsAttribute.addAttribute(new Attribute("error.CoverDetailPageValidation", "i18n_party_type_group_error", "string"));
            }
        }

    }
}