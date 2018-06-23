package com.ail.core.product.liferay;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.net.URL;

import javax.servlet.http.HttpServletRequest;

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
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.context.RequestWrapper;
import com.ail.core.product.ProductUrlToExternalUrlService.ProductUrlToExternalUrlArgument;
import com.liferay.portal.model.User;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.util.PortalUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({ CoreContext.class, PortalUtil.class, UserLocalServiceUtil.class })
public class LiferayProductUrlToExternalUrlServiceTest {

    private LiferayProductUrlToExternalUrlService sut;

    @Mock
    ProductUrlToExternalUrlArgument args;
    @Mock
    RequestWrapper portletWrapper;
    @Mock
    HttpServletRequest httpServletRequest;
    @Mock
    User user;

    URL productUrl;

    @Before
    public void setup() throws Throwable {
        MockitoAnnotations.initMocks(this);

        PowerMockito.mockStatic(CoreContext.class);
        PowerMockito.mockStatic(PortalUtil.class);
        PowerMockito.mockStatic(UserLocalServiceUtil.class);

        when(CoreContext.getRequestWrapper()).thenReturn(portletWrapper);
        when(portletWrapper.getServletRequest()).thenReturn(httpServletRequest);
        when(httpServletRequest.getRequestURL()).thenReturn(new StringBuffer("http://localhost:8080"));
        when(UserLocalServiceUtil.getUserByScreenName(anyLong(), anyString())).thenReturn(user);

        sut = new LiferayProductUrlToExternalUrlService();
        sut.setArgs(args);

        productUrl = new URL("http://localhost:8080");

        doReturn(productUrl).when(args).getProductUrlArg();
    }

    @Test(expected = PreconditionException.class)
    public void nullProductUrlArgShouldBeTrapped() throws BaseException {
        doReturn((URL) null).when(args).getProductUrlArg();
        sut.invoke();
    }

    @Test(expected = PostconditionException.class)
    public void checkThatNullExternalUrlPostconditionIsTrapped() throws BaseException {
        doReturn(null).when(args).getExternalUrlRet();
        sut.invoke();
    }

}
