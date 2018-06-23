/* Copyright Applied Industrial Logic Limited 2017. All rights Reserved */
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
package com.ail.core.context.servlet;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import com.ail.core.CoreContext;

@RunWith(PowerMockRunner.class)
@PrepareForTest({CoreContext.class, ServletRequestAdaptor.class})
public class ServletRequestAdaptorTest {
    ServletRequestAdaptor sut;

    @Mock
    private HttpServletRequest httpServletRequest;

    private Map<String,String[]> parameters = new HashMap<>();

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        mockStatic(CoreContext.class);

        sut = spy(new ServletRequestAdaptor(httpServletRequest));

        doReturn(parameters).when(sut).convertJsonParameterToParameters(anyString());
    }

    @Test
    public void testJsonParam() {
        parameters.put("json", new String[]{"{\"param-name-1\":\"value-1\",\"param-name-2\":\"value-2\"}"});

        doReturn("application/x-www-form-urlencoded").when(httpServletRequest).getContentType();
        doReturn(parameters).when(httpServletRequest).getParameterMap();
        doReturn("{\"param-name-1\":\"value-1\",\"param-name-2\":\"value-2\"}").when(httpServletRequest).getParameter(eq("json"));

        sut = new ServletRequestAdaptor(httpServletRequest);

        assertThat(sut.getParameter("param-name-1"), is("value-1"));
        assertThat(sut.getParameter("param-name-2"), is("value-2"));
    }

    @Test
    public void testPageFlowService() throws IOException {

        ByteArrayInputStream jsonByteArray = new ByteArrayInputStream("{\"param-name-1\":\"value-3\",\"param-name-2\":\"value-4\"}".getBytes(StandardCharsets.UTF_8));
        ServletInputStream servletInputStream=new ServletInputStream(){
            @Override
            public int read() throws IOException {
              return jsonByteArray.read();
            }
        };

        doReturn("application/json").when(httpServletRequest).getContentType();
        doReturn("/pageflow").when(httpServletRequest).getContextPath();
        doReturn(servletInputStream).when(httpServletRequest).getInputStream();

        sut = new ServletRequestAdaptor(httpServletRequest);

        assertThat(sut.getParameter("param-name-1"), is("value-3"));
        assertThat(sut.getParameter("param-name-2"), is("value-4"));
    }

    @Test
    public void nullContentTypeDoesNotCauseNPE() {
        doReturn(null).when(httpServletRequest).getContentType();
        sut = new ServletRequestAdaptor(httpServletRequest);
    }
}
