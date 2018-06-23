package com.ail.core.configure;

import static com.ail.core.Core.CORE_NAMESPACE;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.doReturn;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.core.classloader.annotations.SuppressStaticInitializationFor;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.Core;
import com.ail.core.CoreUser;

@RunWith(PowerMockRunner.class)
@PrepareForTest({AnnotationConfiguration.class, BootstrapConfiguration.class})
@SuppressStaticInitializationFor("com.ail.core.configure.AnnotationConfiguration")
public class ConfigurationHandlerTest {
    private static final String TEST_GROUP_NAME = "_Types.com.ail.core.xmlbinding.FromXMLService.FromXMLCommand";
    private static final String OWNER_NAMESPACE = "OWNER_NAMESPACE";

    ConfigurationHandler sut;

    @Mock
    ConfigurationOwner configurationOwner;

    @Mock
    CoreUser coreUser;

    @Mock
    Core core;

    @Mock
    Configuration emptyConfiguration;

    @Mock
    Configuration bootstrapConfiguration;

    @Mock
    Configuration annotationConfiguration;

    @Mock
    Group bootstrapGroup;

    @Mock
    Group annotationGroup;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);

        sut = spy(new ConfigurationHandler());

        mockStatic(BootstrapConfiguration.class);
        mockStatic(AnnotationConfiguration.class);

        when(BootstrapConfiguration.getInstance()).thenReturn(bootstrapConfiguration);
        when(AnnotationConfiguration.getInstance()).thenReturn(annotationConfiguration);

        doReturn(bootstrapGroup).when(bootstrapConfiguration).findGroup(eq(TEST_GROUP_NAME));
        doReturn(annotationGroup).when(annotationConfiguration).findGroup(eq(TEST_GROUP_NAME));

        doReturn(OWNER_NAMESPACE).when(configurationOwner).getConfigurationNamespace();

        doReturn(emptyConfiguration).when(sut).findConfiguration(eq(OWNER_NAMESPACE), eq(coreUser), eq(core));
        doReturn(emptyConfiguration).when(sut).findConfiguration(eq(CORE_NAMESPACE), eq(coreUser), eq(core));
    }

    @Test
    public void confirmThatBootstrapIsUsedInPreferenceToAnnotation() {

        Group res = sut.getGroup(TEST_GROUP_NAME, configurationOwner, coreUser, core);

        assertThat(res, is(notNullValue()));
        assertThat(res, is(equalTo(bootstrapGroup)));
    }

    @Test
    public void confirmThatAnnotationIsUsedWhenBootstrapGroupIsNotFound() {

        doReturn(null).when(bootstrapConfiguration).findGroup(eq(TEST_GROUP_NAME));

        Group res = sut.getGroup(TEST_GROUP_NAME, configurationOwner, coreUser, core);

        assertThat(res, is(notNullValue()));
        assertThat(res, is(equalTo(annotationGroup)));
    }

    @Test
    public void confirmThatNullIsReturnedIfGroupIsNotFoundInBothBootstrapAndAnnotation() {
        doReturn(null).when(bootstrapConfiguration).findGroup(eq(TEST_GROUP_NAME));
        doReturn(null).when(annotationConfiguration).findGroup(eq(TEST_GROUP_NAME));

        Group res = sut.getGroup(TEST_GROUP_NAME, configurationOwner, coreUser, core);

        assertThat(res, is(nullValue()));
    }
}
