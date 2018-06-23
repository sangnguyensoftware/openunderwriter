/* Copyright Applied Industrial Logic Limited 2015. All rights Reserved */
/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later 
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or 
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51 
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.ail.core.product.liferay;

import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.net.URLConnection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.BaseException;
import com.ail.core.Core;
import com.ail.core.PostconditionException;
import com.ail.core.PreconditionException;
import com.ail.core.product.ProductUrlToConnectionService.ProductUrlToConnectionArgument;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.model.User;
import com.liferay.portal.security.auth.PrincipalThreadLocal;
import com.liferay.portal.security.permission.PermissionCheckerFactoryUtil;
import com.liferay.portal.security.permission.PermissionThreadLocal;
import com.liferay.portal.service.UserLocalServiceUtil;

@RunWith(PowerMockRunner.class)
@PrepareForTest({UserLocalServiceUtil.class, PrincipalThreadLocal.class, PermissionCheckerFactoryUtil.class, PermissionThreadLocal.class})
public class LiferayProductUrlToConnectionServiceTest {
    LiferayProductUrlToConnectionService sut;

    @Mock
    ProductUrlToConnectionArgument args;

    URL url=null;

    @Mock
    private FileEntry fileEntry;
    @Mock 
    private URLConnection urlConnection;
    @Mock
    private User user;

    @Mock
    private Core core;
    
    @Before
    public void setup() throws Throwable {
        MockitoAnnotations.initMocks(this);
        
        sut=new LiferayProductUrlToConnectionService();
        sut.setArgs(args);
        sut.setCore(core);
        
        url=new URL("http://localhost:8080");
        
        doReturn(url).when(args).getProductUrlArg();
        doReturn(urlConnection).when(args).getURLConnectionRet();

        PowerMockito.mockStatic(UserLocalServiceUtil.class);
        when(UserLocalServiceUtil.getUserByScreenName(anyLong(), anyString())).thenReturn(user);
        
        PowerMockito.mockStatic(PrincipalThreadLocal.class);
        PowerMockito.mockStatic(PermissionCheckerFactoryUtil.class);
        PowerMockito.mockStatic(PermissionThreadLocal.class);
    }
    
    
    @Test(expected=PreconditionException.class)
    public void productUrlArgCannotBeNull() throws BaseException {
        doReturn(null).when(args).getProductUrlArg();
        sut.invoke();
    }

    @Test(expected=PostconditionException.class)
    public void urlConnectionRetCannotBeNull() throws BaseException {
        doReturn(null).when(args).getURLConnectionRet();
        sut.invoke();
    }
}
