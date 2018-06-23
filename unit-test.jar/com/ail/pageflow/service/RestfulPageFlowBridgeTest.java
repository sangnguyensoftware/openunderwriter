package com.ail.pageflow.service;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import javax.ws.rs.core.Response;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.context.RequestWrapper;
import com.ail.core.context.ResponseWrapper;
import com.ail.core.logging.ServiceRequestRecord;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.PageFlowContext;
import com.ail.pageflow.portlet.PageFlowCommon;
import com.ail.pageflow.service.InitialisePageFlowContextService.InitialisePageFlowContextCommand;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PageFlowContext.class, CoreContext.class, RestfulPageFlowBridge.class })
public class RestfulPageFlowBridgeTest {

    private static final String PRODUCT_ID = "PRODUCT_ID";
    private static final String STRING_POLICY_ID = "1";
    private static final String PAGE_FLOW_NAME = "PAGE_FLOW_NAME";
    private static final Long POLICY_OWNING_USER_ID = 21L;

    private RestfulPageFlowBridge sut;

    @Mock
    private Policy policy;
    @Mock
    private CoreProxy coreProxyNoProduct;
    @Mock
    private ServiceRequestRecord serviceRequestRecord;
    @Mock
    private CoreProxy coreProxyForProduct;
    @Mock
    private InitialisePageFlowContextCommand initialisePageFlowContextCommand;
    @Mock
    private ResponseWrapper responseWrapper;
    @Mock
    private RequestWrapper requestWrapper;
    @Mock
    private PageFlowCommon pageFlowCommon;
    @Mock
    private Response response;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockStatic(PageFlowContext.class, CoreContext.class);

        whenNew(CoreProxy.class).withNoArguments().thenReturn(coreProxyNoProduct);
        whenNew(ServiceRequestRecord.class).withNoArguments().thenReturn(serviceRequestRecord);
        whenNew(PageFlowCommon.class).withNoArguments().thenReturn(pageFlowCommon);
        when(PageFlowContext.getCoreProxy()).thenReturn(coreProxyForProduct);
        when(PageFlowContext.getResponseWrapper()).thenReturn(responseWrapper);
        when(PageFlowContext.getRequestWrapper()).thenReturn(requestWrapper);
        when(CoreContext.getServiceRequestRecord()).thenReturn(serviceRequestRecord);

        doReturn(initialisePageFlowContextCommand).when(coreProxyNoProduct).newCommand(InitialisePageFlowContextCommand.class);

        sut = spy(new RestfulPageFlowBridge(PAGE_FLOW_NAME));

        doReturn(response).when(sut).buildResponse();
        doReturn(policy).when(coreProxyNoProduct).queryUnique(eq("get.policy.by.externalSystemId"), eq(STRING_POLICY_ID));
        doReturn(PRODUCT_ID).when(policy).getProductTypeId();
        doReturn(POLICY_OWNING_USER_ID).when(policy).getOwningUser();
        whenNew(CoreProxy.class).withArguments(PRODUCT_ID+".Registry").thenReturn(coreProxyForProduct);
    }

    @Test
    public void verifyDetermineByPolicyHolderAppliedWhenConfigured() throws Exception {

        doReturn("yes").when(coreProxyForProduct).getParameterValue(eq("DetermineRemoteUserByPolicyOwner"), eq((String)null));

        when(CoreContext.getRemoteUser()).thenReturn(null);

        sut.policyId(STRING_POLICY_ID).invoke();

        verifyStatic(times(1));
        CoreContext.setRemoteUser(POLICY_OWNING_USER_ID);
    }

    @Test
    public void verifyDetermineByPolicyHolderNotAppliedWhenNotConfigured() throws Exception {

        doReturn(null).when(coreProxyForProduct).getParameterValue(eq("DetermineRemoteUserByPolicyOwner"), eq((String)null));

        when(CoreContext.getRemoteUser()).thenReturn(null);

        sut.policyId(STRING_POLICY_ID).invoke();

        verifyStatic(never());
        CoreContext.setRemoteUser(POLICY_OWNING_USER_ID);
    }

//    @Test
//    public void verifyDetermineByPolicyHolderNotAppliedWhenRemoteUserIsDefined() throws Exception {
//
//        doReturn("yes").when(coreProxyForProduct).getParameterValue(eq("DetermineRemoteUserByPolicyOwner"), eq((String)null));
//
//        when(CoreContext.getRemoteUser()).thenReturn(REMOTE_USER);
//
//        sut.policyId(STRING_POLICY_ID).invoke();
//
//        verifyStatic(never());
//        CoreContext.setRemoteUser(POLICY_OWNING_USER_ID);
//    }
}
