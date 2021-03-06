package com.ail.insurance.quotation;

import static com.ail.insurance.policy.PolicyStatus.CANCELLED;
import static com.ail.insurance.policy.PolicyStatus.LAPSED;
import static com.ail.insurance.policy.PolicyStatus.ON_RISK;
import static com.ail.insurance.policy.PolicyStatus.SUBMITTED;
import static java.util.Arrays.asList;

import java.util.HashSet;
import java.util.Set;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.Functions;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.key.GenerateUniqueKeyService.GenerateUniqueKeyCommand;
import com.ail.insurance.claim.Claim;
import com.ail.insurance.policy.PolicyStatus;
import com.ail.insurance.quotation.GenerateClaimNumberService.GenerateClaimNumberCommand;

@ServiceImplementation
public class AddClaimNumberService extends Service<AddClaimNumberService.AddClaimNumberArgument> {
    private static final Set<PolicyStatus> VALID_POLICY_STATUSES = new HashSet<>(asList(new PolicyStatus[]{CANCELLED, LAPSED, ON_RISK, SUBMITTED }));

    private String configurationNamespace;

    @ServiceArgument
    public interface AddClaimNumberArgument extends Argument {
        /**
         * Getter for the claimArgRet property. The claim to generate a claim number for
         * @return Value of claimArgRet, or null if it is unset
         */
       Claim getClaimArgRet();

       /**
        * Setter for the claimArgRet property. * @see #getClaimArgRet
        * @param claimArgRet new value for property.
        */
        void setClaimArgRet(Claim claim);
    }

    /**
     * Return the product type id of the policy we're assessing the risk for as the
     * configuration namespace. The has the effect of selecting the product's configuration.
     * @return product type id
     */
    @Override
    public String getConfigurationNamespace() {
        return configurationNamespace;
    }

    //An implementation will be auto generated by the com.ail.annotation.Processor
    @ServiceCommand(defaultServiceClass=AddClaimNumberService.class)
    public interface AddClaimNumberCommand extends Command, AddClaimNumberArgument {}

    @Override
    public void invoke() throws BaseException {
        Claim claim = args.getClaimArgRet();

        if (args.getClaimArgRet() == null) {
            throw new PreconditionException("args.getClaimArgRet() == null");
        }

        if (!Functions.isEmpty(args.getClaimArgRet().getClaimNumber())) {
            throw new PreconditionException("!Functions.isEmpty(args.getClaimArgRet().getClaimNumber())");
        }

        if (args.getClaimArgRet().getPolicy() == null) {
            throw new PreconditionException("args.getClaimArgRet().getPolicy() == null");
        }

        if (!VALID_POLICY_STATUSES.contains(args.getClaimArgRet().getPolicy().getStatus())) {
            throw new PreconditionException("!VALID_POLICY_STATUSES.contains(args.getClaimArgRet().getPolicy().getStatus())");
        }

        if (Functions.isEmpty(args.getClaimArgRet().getPolicy().getProductTypeId())) {
            throw new PreconditionException("Functions.isEmpty(args.getClaimArgRet().getPolicy().getProductTypeId())");
        }

        // switch over to the product's configuration
        configurationNamespace=Functions.productNameToConfigurationNamespace(args.getClaimArgRet().getPolicy().getProductTypeId());

        GenerateUniqueKeyCommand gukc = getCore().newCommand(GenerateUniqueKeyCommand.class);
        gukc.setKeyIdArg("ClaimNumber");
        gukc.invoke();

        GenerateClaimNumberCommand command = getCore().newCommand("GenerateClaimNumber", GenerateClaimNumberCommand.class);
        command.setClaimArg(claim);
        command.setUniqueNumberArg(gukc.getKeyRet());
        command.invoke();
        String claimNumber = command.getClaimNumberRet();
        core.logInfo("Claim number: " + claimNumber + " generated");
        claim.setClaimNumber(claimNumber);

        if (Functions.isEmpty(args.getClaimArgRet().getClaimNumber())) {
            throw new PostconditionException("Functions.isEmpty(args.getClaimArgRet().getClaimNumber())");
        }
    }
}
