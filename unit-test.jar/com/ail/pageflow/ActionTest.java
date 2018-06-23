package com.ail.pageflow;

import static com.ail.core.product.Product.AGGREGATOR_CONFIGURATION_GROUP_NAME;
import static com.ail.pageflow.ActionType.ON_PROCESS_VALIDATIONS;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.configure.Group;
import com.ail.core.configure.Parameter;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionInSubProductService.ExecutePageActionInSubProductCommand;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionCommand;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PageFlowContext.class, CoreContext.class})
public class ActionTest {

    Action sut;
    @Mock
    private Policy policy;
    @Mock
    private CoreProxy coreProxy;
    @Mock
    private ExecutePageActionCommand executePageActionCommand;
    @Mock
    private ExecutePageActionInSubProductCommand executePageActionInSubProductCommand;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = spy(new Action());

        mockStatic(PageFlowContext.class);
        mockStatic(CoreContext.class);

        when(PageFlowContext.getCoreProxy()).thenReturn(coreProxy);
        when(PageFlowContext.getPolicy()).thenReturn(policy);

        doReturn(executePageActionCommand).when(coreProxy).newCommand(eq(ExecutePageActionCommand.class));
        doReturn(executePageActionInSubProductCommand).when(coreProxy).newCommand(eq(ExecutePageActionInSubProductCommand.class));
        doReturn(false).when(policy).isAggregator();
    }

    @Test
    public void nonAggregatedCommandsMustExecuteInCurrentContext() {
        sut.setWhen(ON_PROCESS_VALIDATIONS);
        sut.executeAction(policy, ON_PROCESS_VALIDATIONS);
        verify(sut).execute(eq(policy));
        verify(sut, never()).executeInAggregatedProducts(eq(policy));
    }

    @Test
    public void executeInAggregatedProductsMustBeCalledIfExecuteInAggregatedIsTrue() {
        doReturn(true).when(policy).isAggregator();

        Group group = mock(Group.class);
        Parameter productOne = mock(Parameter.class);
        doReturn("PRODUCT_ONE_NAME").when(productOne).getName();
        doReturn(group).when(coreProxy).getGroup(eq(AGGREGATOR_CONFIGURATION_GROUP_NAME));
        List<Parameter> params=Arrays.asList(productOne);
        doReturn(params).when(group).getParameter();

        sut.setWhen(ON_PROCESS_VALIDATIONS);
        sut.setExecuteInAggregated(true);
        sut.executeAction(policy, ON_PROCESS_VALIDATIONS);

        verify(sut).executeInAggregatedProducts(eq(policy));
    }

    @Test(expected=RenderingError.class)
    public void executeInAggregatedProductsMustFailIfPolicyIsNotAnAggregator() {
        doReturn(false).when(policy).isAggregator();

        sut.setWhen(ON_PROCESS_VALIDATIONS);
        sut.setExecuteInAggregated(true);
        sut.executeAction(policy, ON_PROCESS_VALIDATIONS);
    }
}
