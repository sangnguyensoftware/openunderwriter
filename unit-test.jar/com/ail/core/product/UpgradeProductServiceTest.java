package com.ail.core.product;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.whenNew;

import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.CoreProxy;
import com.ail.core.CoreUser;
import com.ail.core.PreconditionException;
import com.ail.core.configure.Group;
import com.ail.core.configure.Parameter;
import com.ail.core.product.UpgradeProductService.UpgradeProductArgument;
import com.ail.core.product.UpgradeProductService.UpgradeProductCommand;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreProxy.class, UpgradeProductService.class})
public class UpgradeProductServiceTest {
    private static final String PRODUCT_NAME = "DUMMY_PRODUCT_NAME";
    private static final String UPGRADE_COMMAND_NAME_2 = "COMMAND_2";
    private static final String UPGRADE_COMMAND_NAME_1 = "COMMAND_1";

    private UpgradeProductService sut;

    @Mock
    private UpgradeProductArgument args;
    @Mock
    private CoreProxy coreProxy;
    @Mock
    private Group group;
    @Mock
    private Parameter upgradeCommanParam1;
    @Mock
    private Parameter upgradeCommanParam2;
    @Mock
    private Parameter upgradeCommanParam3;
    @Mock
    private CoreUser coreUser;
    @Mock
    private UpgradeProductCommand upgradeCommand1;
    @Mock
    private UpgradeProductCommand upgradeCommand2;
    @Mock
    private UpgradeProductCommand upgradeCommand3;
    @Mock
    private ProductUpgradeLog productUpgradeLog;
    @Captor
    ArgumentCaptor<ProductUpgradeLog> productUpgradeLogCaptor;

    private List<Parameter> params;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = new UpgradeProductService();
        sut.setArgs(args);

        doReturn(PRODUCT_NAME).when(args).getProductNameArg();
        doReturn(coreUser).when(args).getCallersCore();

        params = new ArrayList<>();
        params.add(upgradeCommanParam1);
        params.add(upgradeCommanParam2);

        doReturn(UPGRADE_COMMAND_NAME_1).when(upgradeCommanParam1).getName();
        doReturn(UPGRADE_COMMAND_NAME_2).when(upgradeCommanParam2).getName();

        whenNew(CoreProxy.class).withAnyArguments().thenReturn(coreProxy);
        doReturn(group).when(coreProxy).getGroup(eq(UpgradeProductService.PRODUCT_UPGRADE_GROUP_NAME + "." + PRODUCT_NAME));
        doReturn(params).when(group).getParameter();

        doReturn(upgradeCommand1).when(coreProxy).newCommand(eq(UPGRADE_COMMAND_NAME_1), eq(UpgradeProductCommand.class));
        doReturn(upgradeCommand2).when(coreProxy).newCommand(eq(UPGRADE_COMMAND_NAME_2), eq(UpgradeProductCommand.class));

        doReturn(null).when(coreProxy).queryUnique(eq("get.sucessful.product.upgrade.by.product.and.command"), eq(PRODUCT_NAME), eq(UPGRADE_COMMAND_NAME_1));
        doReturn(productUpgradeLog).when(coreProxy).queryUnique(eq("get.sucessful.product.upgrade.by.product.and.command"), eq(PRODUCT_NAME), eq(UPGRADE_COMMAND_NAME_2));
    }

    @Test(expected = PreconditionException.class)
    public void testPrecondition() throws BaseException {
        doReturn(null).when(args).getProductNameArg();

        sut.invoke();
    }

    @Test
    public void testThatUpgradeCommandsAreNotRerun() throws BaseException {
        sut.executeScripts(coreProxy, PRODUCT_NAME);

        verify(upgradeCommand2, never()).invoke();
    }

    @Test
    public void testThatNewUpgradeCommandsAreRun() throws BaseException {
        sut.executeScripts(coreProxy, PRODUCT_NAME);

        verify(upgradeCommand1, times(1)).invoke();

        verify(coreProxy, times(1)).create(productUpgradeLogCaptor.capture());
        ProductUpgradeLog created=productUpgradeLogCaptor.getValue();
        assertThat(created.getProductName(), is(PRODUCT_NAME));
        assertThat(created.getCommandName(), is(UPGRADE_COMMAND_NAME_1));
    }

    @Test
    public void testThatUpgradeCommandsAreRecorded() throws BaseException {
        sut.executeScripts(coreProxy, PRODUCT_NAME);

        verify(coreProxy, times(1)).create(productUpgradeLogCaptor.capture());
        ProductUpgradeLog created=productUpgradeLogCaptor.getValue();
        assertThat(created.getProductName(), is(PRODUCT_NAME));
        assertThat(created.getCommandName(), is(UPGRADE_COMMAND_NAME_1));
    }
}
