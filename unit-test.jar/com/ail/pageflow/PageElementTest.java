package com.ail.pageflow;

import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.inOrder;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InOrder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.Type;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.render.RenderService.RenderArgument;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ PageFlowContext.class})
public class PageElementTest {
    private static final String I18N_TITLE = "I18N_TITLE";
    private static final String EXPANDED_TITLE = "EXPANDED_TITLE";
    private static final String DUMMY_TITLE = "DUMMY_TITLE";

    PageElement sut;

    @Mock
    private RenderArgument renderArgument;
    @Mock
    private Policy rootObject;
    @Mock
    private Type nodeObject;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        sut = spy(new PageElement() {});

        mockStatic(PageFlowContext.class);
        when(PageFlowContext.getPolicy()).thenReturn(rootObject);

        doReturn(nodeObject).when(renderArgument).getModelArgRet();
    }

    @Test
    public void titleFormatShoudExpandAfterI18N() {
        doReturn(DUMMY_TITLE).when(sut).getTitle();
        doReturn(I18N_TITLE).when(sut).i18n(eq(DUMMY_TITLE));
        doReturn(EXPANDED_TITLE).when(sut).expand(eq(I18N_TITLE), eq(rootObject), eq(nodeObject));

        sut.formattedTitle(renderArgument);

        InOrder order=inOrder(sut);
        order.verify(sut).i18n(eq(DUMMY_TITLE));
        order.verify(sut).expand(eq(I18N_TITLE), eq(rootObject), eq(nodeObject));
    }

}
