package com.ail.pageflow.service;

import static java.net.HttpURLConnection.HTTP_BAD_REQUEST;
import static java.net.HttpURLConnection.HTTP_NOT_FOUND;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import javax.servlet.ServletConfig;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.jboss.resteasy.plugins.providers.multipart.MultipartFormDataInput;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.Attribute;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.Functions;
import com.ail.core.JSONException;
import com.ail.core.RestfulServiceReturn;
import com.ail.core.logging.ServiceRequestRecord;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.PageFlowContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PageFlowContext.class, CoreContext.class, Functions.class, RestfulServiceBridge.class, Response.class })
public class RestfulServiceBridgeTest {
    private static final String USER_AGENT = "user-agent";
    private static final String PRODUCT_NAMESPACE = "PRODUCT_NAMESPACE";
    private static final String POLICY_ID = "1234";
    private static final String COMMAND = "COMMAND";
    private static final String PRODUCT = "PRODUCT";
    private static final String JSON_DATA = "JSON_DATA";
    private static final Long OWNER = 123L;

    private RestfulServiceBridge sut;

    @Mock
    private HttpServletRequest request;
    @Mock
    private HttpServletResponse response;
    @Mock
    private ServletConfig config;
    @Mock
    private MultipartFormDataInput parts;
    @Mock
    private CoreProxy productCoreProxy;
    @Mock
    private CoreProxy generalCoreProxy;
    @Mock
    private Response rsResponse;
    @Mock
    private Policy policy;
    @Mock
    private ServiceRequestRecord serviceRequestRecord;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        mockStatic(PageFlowContext.class);
        mockStatic(CoreContext.class);
        mockStatic(Functions.class);

        sut = spy(new RestfulServiceBridge(COMMAND, request, response, config).product(PRODUCT));

        when(Functions.productNameToConfigurationNamespace(PRODUCT)).thenReturn(PRODUCT_NAMESPACE);
        whenNew(CoreProxy.class).withArguments(PRODUCT_NAMESPACE).thenReturn(productCoreProxy);
        whenNew(CoreProxy.class).withNoArguments().thenReturn(generalCoreProxy);
        when(PageFlowContext.getRemoteUser()).thenReturn(OWNER);
        when(PageFlowContext.getCoreProxy()).thenReturn(productCoreProxy);
        when(CoreContext.getRestfulResponse()).thenReturn(null);
        when(CoreContext.getServiceRequestRecord()).thenReturn(serviceRequestRecord);
        when(request.getHeader(eq("User-Agent"))).thenReturn(USER_AGENT);
        when(policy.xpathGet(eq("i:test(attribute[id='UserAgent'])"))).thenReturn(false);
        when(request.getHeader(eq("Interface-Version"))).thenReturn("1.0");
        when(policy.xpathGet(eq("i:test(attribute[id='InterfaceVersion'])"))).thenReturn(false);

        doNothing().when(sut).execute(eq("SelectPageFlowCommand"));
        doNothing().when(sut).execute(eq(COMMAND));
        doNothing().when(sut).execute(eq("PersistPolicyFromPageFlowCommand"));
        doReturn(policy).when(generalCoreProxy).queryUnique(anyString(), eq(POLICY_ID));
        doReturn(OWNER).when(policy).getOwningUser();
    }


    @Test
    public void confirmThatPageFlowContextIsInitialisedCorrectly() throws JSONException {

        sut.policyId(POLICY_ID).jsonData(JSON_DATA).multiparts(parts).invoke();

        verifyStatic();
        PageFlowContext.setCoreProxy(eq(productCoreProxy));
        verifyStatic();
        PageFlowContext.setProductName(eq(PRODUCT));
        verifyStatic();
        PageFlowContext.setPageFlowName(eq("QuotationPageFlow"));
        verifyStatic();
        PageFlowContext.setPolicy(eq(policy));
    }

    @Test
    public void checkThatPolicyQueryDoesNotExecuteIfPolicyIdIsNotDefined() {
        sut.invoke();
        verify(generalCoreProxy, never()).queryUnique(anyString(), anyObject());
    }

    @Test
    public void checkThatPolicyQueryExecutesIfPolicyIdIsDefined() {
        sut.policyId(POLICY_ID).invoke();
        verify(generalCoreProxy, times(1)).queryUnique(anyString(), eq(POLICY_ID));
    }

    @Test
    public void checkThatAttributesAreAddedToPolicy() {
        sut.policyId(POLICY_ID).invoke();
        verify(policy, times(2)).addAttribute((Attribute)anyObject());
        verify(policy, never()).xpathSet(eq("attribute[id='UserAgent']/value"), anyString());
        verify(policy, never()).xpathSet(eq("attribute[id='InterfaceVersion']/value"), anyString());

    }

    @Test
    public void checkThatAttributesAreSetInPolicy() {
        when(policy.xpathGet(eq("i:test(attribute[id='UserAgent'])"))).thenReturn(true);
        when(policy.xpathGet(eq("i:test(attribute[id='InterfaceVersion'])"))).thenReturn(true);
        sut.policyId(POLICY_ID).invoke();
        verify(policy, never()).addAttribute((Attribute)anyObject());
        verify(policy, times(1)).xpathSet(eq("attribute[id='UserAgent']/value"), anyString());
        verify(policy, times(1)).xpathSet(eq("attribute[id='InterfaceVersion']/value"), anyString());

    }

    @Test
    public void checkThatAnErrorIsReturnedWhenThePolicyCannotBeFound() {
        doReturn(null).when(generalCoreProxy).queryUnique(anyString(), eq(POLICY_ID));
        Response resp = sut.policyId(POLICY_ID).invoke();
        assertThat(resp.getStatus(), is(HTTP_NOT_FOUND));
    }

    @Test
    public void checkThatAnErrorIsReturnedWhenTheRemoteUserDoesNotOwnThePolicy() {
        when(PageFlowContext.getRemoteUser()).thenReturn(321L);
        Response resp = sut.policyId(POLICY_ID).invoke();
        assertThat(resp.getStatus(), is(HTTP_NOT_FOUND));
    }

    @Test
    public void errorReturnedIfPolicyIdAndProductAreNull() {
        sut.product(null);
        sut.policyId(null);
        Response resp = sut.invoke();
        assertThat(resp.getStatus(), is(HTTP_BAD_REQUEST));
    }

    @Test
    public void checkThatEmptyServiceResultsReturnsValidJSON() throws JSONException {
        RestfulServiceReturn restfulServiceReturn = Mockito.mock(DummyRestfulServiceReturn.class);
        ResponseBuilder responseBuilder = Mockito.mock(ResponseBuilder.class);

        mockStatic(Response.class);

        when(CoreContext.getRestfulResponse()).thenReturn(restfulServiceReturn);
        when(CoreContext.getCoreProxy().toJSON(eq(restfulServiceReturn))).thenReturn("{}");
        when(Response.status(0)).thenReturn(responseBuilder);

        doReturn(responseBuilder).when(responseBuilder).header(anyString(), anyObject());
        doReturn(responseBuilder).when(responseBuilder).entity(anyObject());

        sut.buildResponse();

        verify(responseBuilder).entity(eq("{\"success\" : true}"));
    }

    class DummyRestfulServiceReturn extends RestfulServiceReturn {
        public DummyRestfulServiceReturn(int status) {
            super(status);
        }
    }
}
