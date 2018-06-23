package com.ail.workflow;

import com.ail.insurance.policy.Policy;

public class NewBusinessReferral {
    Policy policy;

    public NewBusinessReferral(Policy policy) {
        this.policy = policy;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }
}
