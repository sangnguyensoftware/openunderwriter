package com.ail.workflow;

import java.io.Serializable;
import java.util.Locale;
import java.util.Map;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.language.LanguageUtil;
import com.liferay.portal.kernel.workflow.BaseWorkflowHandler;

public class NewBusinessReferralWorkflowHandler extends BaseWorkflowHandler {

    
    @Override
    public String getClassName() {
        return NewBusinessReferral.class.getName();
    }

    @Override
    public String getType(Locale locale) {
        return LanguageUtil.get(locale, "model.resource." + getClassName());
    }

    @Override
    public Object updateStatus(final int status, Map<String, Serializable> context) throws PortalException, SystemException {
        
//        final long policySystemId = getLong(context.get(CONTEXT_ENTRY_CLASS_PK));
//        new CoreProxy().logInfo("NewBusinessReferral workflow status updated to: "+getStatusLabel(status)+", for policyId: "+policyID);
        
        return null;
    }
    
    

}
