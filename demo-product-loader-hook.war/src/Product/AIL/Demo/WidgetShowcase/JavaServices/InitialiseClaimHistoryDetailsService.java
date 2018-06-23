import static com.ail.insurance.policy.PolicyStatus.ON_RISK;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.financial.CurrencyAmount;
import com.ail.insurance.claim.Claim;
import com.ail.insurance.claim.ClaimStatus;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.quotation.AddClaimNumberService.AddClaimNumberCommand;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

/* Copyright Applied Industrial Logic Limited 2007. All rights reserved. */

/**
 * Initialise the necessary fields to enable to ClaimHistory widget to render.
 */
public class InitialiseClaimHistoryDetailsService {
    /**
     * Service entry point.
     * @throws BaseException 
     */
    public static void invoke(ExecutePageActionArgument args) throws BaseException {
        Policy policy = (Policy) args.getModelArgRet();

        if (policy.getClaim().size() == 0) {
            addClaim(policy, ClaimStatus.OPEN, new CurrencyAmount("123.45GBP"));
            addClaim(policy, ClaimStatus.REJECTED, new CurrencyAmount("67.89GBP"));
        }
    }
        
    /**
	 * Add claim with the passed status.
	 * 
	 * @param policy Schedule will be added to args.getOptionsRet()
	 * @param status of claim
     * @throws BaseException 
	 */ 
    private static void addClaim(Policy policy, ClaimStatus status, CurrencyAmount outstandingTotalAmount) throws BaseException {
        Claim claim = (Claim) CoreContext.getCoreProxy().newType(Claim.class);
        policy.addClaim(claim);
        policy.setStatus(ON_RISK);

        AddClaimNumberCommand gcnc = (AddClaimNumberCommand)CoreContext.getCoreProxy().newCommand(AddClaimNumberCommand.class);
        gcnc.setClaimArgRet(claim);
        gcnc.invoke();
        
        LocalDate endDate = LocalDate.now();
        LocalDate startDate = endDate.minusDays(2);
        
        claim.setStartDate(Date.from(startDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        claim.setEndDate(Date.from(endDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        
        claim.setStatus(status);
        
        claim.setOutstandingTotal(outstandingTotalAmount);
    }
}