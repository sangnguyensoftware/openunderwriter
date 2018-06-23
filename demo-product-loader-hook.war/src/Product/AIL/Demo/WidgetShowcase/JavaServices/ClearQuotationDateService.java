
/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

/**
 * The quotation date of quote is an essential value. The system uses it to
 * determine which configuration to use when processing the quote. If the system
 * cannot find a configuration to match the quotation date, it will not be able
 * to process the quotation. This includes all operations including saving the
 * quote to persistent store. In normal operation, this is a good thing but
 * during testing it creates the awkward situation where UI tests that want to
 * that verify the quotation date has been correctly rendered on the UI. For
 * that reason this service is provided which can be attached to the UI allowing
 * tests to rest the quotation date to a value which will allow the quotation to
 * be processed.
 */
public class ClearQuotationDateService {
    /**
     * Set the quotation date to null to allow the quotation to be processed.
     */
    public static void invoke(ExecutePageActionArgument args) {
        Policy policy = (Policy) args.getModelArgRet();
        policy.setQuotationDate(null);
    }
}