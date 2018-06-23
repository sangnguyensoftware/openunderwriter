package com.ail.core.data;

import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;

/**
 * Implements a wrapper to the Core's data dictionary service to simplify
 * getting xpaths for definition names.
 */
public class XPath {

    /**
     * Return the data dictionary xpath matching a specific definitionId if one
     * exists, else return the definitionId (which will be xpath).
     *
     * @param definitionId  key identifying the xpath to be returned.
     * @return xpath string, or the value of <i>definitionId</i> if a match cannot be found.
     */
    public static String xpath(String definitionId) {
        if (definitionId != null) {
            try {
                String product = CoreContext.getProductName();

                if (product == null || product.length() == 0) {
                    product = "AIL.Base";
                }

                DataDictionary dictionary = getCoreProxy().newProductType(product, "DataDictionary", DataDictionary.class);

                return dictionary.translate(definitionId);
            } catch (Throwable ignorable) {
                // Obviously couldn't find a dictionary, so just return the input
            }
        }
        return definitionId;
    }

    private static CoreProxy getCoreProxy() {
        CoreProxy coreProxy = CoreContext.getCoreProxy();
        if (coreProxy == null) {
            coreProxy = new CoreProxy();
        }

        return coreProxy;
    }
}
