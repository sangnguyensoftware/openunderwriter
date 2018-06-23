
/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */
import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.ail.core.VersionEffectiveDate;
import com.ail.core.command.CommandInvocationError;
import com.ail.core.context.RequestWrapper;
import com.ail.financial.CurrencyAmount;
import com.ail.insurance.policy.FixedSum;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.pageflow.PageFlowContext;

public class SavedQuotationsUpdateAndSaveService {
    public static void invoke(ExecutePageActionArgument args) {
        try {
            DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            RequestWrapper requestWrapper = PageFlowContext.getRequestWrapper();

            String quotationNumber = requestWrapper.getParameter("quotation-number-override");
            String quotationDateString = requestWrapper.getParameter("quotation-date-override");
            String expiryDateString = requestWrapper.getParameter("expiry-date-override");
            String premium = requestWrapper.getParameter("premium-override");
            String productTypeId = PageFlowContext.getProductName();

            Policy policy = (Policy) PageFlowContext.getCoreProxy().newProductType(productTypeId, "Policy", Policy.class);

            policy.setQuotationNumber(quotationNumber);
            policy.setQuotationDate(dateFormat.parse(quotationDateString));
            policy.setQuotationExpiryDate(dateFormat.parse(expiryDateString));

            policy.setProductTypeId(productTypeId);
            policy.setUserSaved(true);
            policy.setOwningUser(PageFlowContext.getRemoteUser());
            policy.setVersionEffectiveDate(new VersionEffectiveDate());

            ((FixedSum) policy.getAssessmentSheet().findLineById("total premium")).setAmount(new CurrencyAmount(premium, "GBP"));

            PageFlowContext.getCoreProxy().update(policy);

            PageFlowContext.getCoreProxy().flush();
        } catch (Exception e) {
            throw new CommandInvocationError("Failed to create saved quotation for testing", e);
        }
    }
}