package com.ail.pageflow;

import static com.ail.core.product.Product.AGGREGATOR_CONFIGURATION_GROUP_NAME;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.PreconditionException;
import com.ail.core.Type;
import com.ail.core.configure.Group;
import com.ail.core.configure.Parameter;
import com.ail.pageflow.ExecutePageActionInSubProductService.ExecutePageActionInSubProductArgument;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionCommand;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CoreContext.class, PageFlowContext.class, CoreProxy.class, ExecutePageActionInSubProductService.class})
public class ExecutePageActionInSubProductServiceTest {
    private static final String CURRENT_PRODUCT_TYPE_ID = "CURRENT_PRODUCT_TYPE_ID";
    private static final String TARGET_PRODUCT_TYPE_ID = "TARGET_PRODUCT_TYPE_ID";
    private static final String COMMAND_NAME = "COMMAND_NAME";

    private ExecutePageActionInSubProductService sut;

    @Mock
    private ExecutePageActionInSubProductArgument args;

    @Mock
    private Type model;

    @Mock
    private CoreProxy currentCoreProxy;

    @Mock
    private CoreProxy targetCoreProxy;

    @Mock
    private ExecutePageActionCommand executePageActionCommand;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(PageFlowContext.class);
        PowerMockito.mockStatic(CoreContext.class);

        sut=new ExecutePageActionInSubProductService();

        sut.setArgs(args);

        when(PageFlowContext.getProductName()).thenReturn(CURRENT_PRODUCT_TYPE_ID);
        when(PageFlowContext.getCoreProxy()).thenReturn(currentCoreProxy).thenReturn(targetCoreProxy);

        doReturn(COMMAND_NAME).when(args).getCommandNameArg();
        doReturn(TARGET_PRODUCT_TYPE_ID).when(args).getProductTypeIdArg();
        doReturn(false).when(args).getExecuteInAllSubProductsArg();
        doReturn(model).when(args).getModelArgRet();

        whenNew(CoreProxy.class).withArguments(TARGET_PRODUCT_TYPE_ID+".Registry").thenReturn(targetCoreProxy);

        doReturn(executePageActionCommand).when(targetCoreProxy).newCommand(eq(ExecutePageActionCommand.class));
    }

    @Test(expected=PreconditionException.class)
    public void testCommandNamePrecondition() throws PreconditionException {
        doReturn(null).when(args).getCommandNameArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testProductTypeIdPrecondition() throws PreconditionException {
        doReturn(null).when(args).getProductTypeIdArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void testModelPrecondition() throws PreconditionException {
        doReturn(null).when(args).getModelArgRet();
        sut.invoke();
    }

    @Test
    public void verifyContextSwitchAndRestore() throws PreconditionException {
        sut.invoke();

        verifyStatic();
        PageFlowContext.setProductName(eq(TARGET_PRODUCT_TYPE_ID));

        verifyStatic();
        PageFlowContext.setCoreProxy(eq(targetCoreProxy));

        verifyStatic();
        PageFlowContext.setProductName(eq(CURRENT_PRODUCT_TYPE_ID));

        verifyStatic();
        PageFlowContext.setCoreProxy(eq(currentCoreProxy));
    }

    @Test(expected=PreconditionException.class)
    public void confimProductTypeIdAndExecuteInAllSubProductsArgIsTrapped() throws PreconditionException {
        doReturn(null).when(args).getProductTypeIdArg();
        doReturn(false).when(args).getExecuteInAllSubProductsArg();
        sut.invoke();
    }

    @Test(expected=PreconditionException.class)
    public void confimBothProductTypeIdAndExecuteInAllSubProductsArgIsTrapped() throws PreconditionException {
        doReturn(TARGET_PRODUCT_TYPE_ID).when(args).getProductTypeIdArg();
        doReturn(true).when(args).getExecuteInAllSubProductsArg();
        sut.invoke();
    }

    @Test
    public void verifyContextSwitchRestoredAfterException() throws BaseException {
        Throwable preconditionException = mock(PreconditionException.class);

        doThrow(preconditionException).when(executePageActionCommand).invoke();

        try {
            sut.invoke();
            fail("Expected exception was not thrown");
        }
        catch(Error e) {
            // ignore - this is what we're expecting.
        }

        verifyStatic();
        PageFlowContext.setProductName(eq(TARGET_PRODUCT_TYPE_ID));

        verifyStatic();
        PageFlowContext.setCoreProxy(eq(targetCoreProxy));

        verifyStatic();
        PageFlowContext.setProductName(eq(CURRENT_PRODUCT_TYPE_ID));

        verifyStatic();
        PageFlowContext.setCoreProxy(eq(currentCoreProxy));
    }

    @Test
    public void verifyTargetProductsForSpecifiedProduct() throws PreconditionException {
        doReturn(TARGET_PRODUCT_TYPE_ID).when(args).getProductTypeIdArg();
        doReturn(false).when(args).getExecuteInAllSubProductsArg();

        List<String> res = sut.targetProducts();

        assertThat(res, hasSize(1));
        assertThat(res, contains(TARGET_PRODUCT_TYPE_ID));
    }

    @Test
    public void verifyTargetProductsForAllSubProducts() throws PreconditionException {
        doReturn(null).when(args).getProductTypeIdArg();
        doReturn(true).when(args).getExecuteInAllSubProductsArg();

        Group group = new Group(AGGREGATOR_CONFIGURATION_GROUP_NAME);
        group.addParameter(new Parameter("PRODUCT_ONE", null));
        group.addParameter(new Parameter("PRODUCT_TWO", null));
        group.addParameter(new Parameter("PRODUCT_THREE", null));

        doReturn(group).when(currentCoreProxy).getGroup(eq(AGGREGATOR_CONFIGURATION_GROUP_NAME));

        List<String> res = sut.targetProducts();

        assertThat(res, hasSize(3));
        assertThat(res, contains("PRODUCT_ONE", "PRODUCT_TWO", "PRODUCT_THREE"));
    }

    @Test
    public void validationFailedMustDefaultToFalse() throws PreconditionException {
        sut.invoke();

        verify(args, times(1)).setValidationFailedRet(eq(false));
    }
}
