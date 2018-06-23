package com.ail.pageflow.render;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;

import javax.mail.MessagingException;

import org.springframework.stereotype.Component;

import com.ail.core.BaseException;
import com.ail.core.Functions;
import com.ail.pageflow.PageFlowContext;

/**
 * Add the OpenUnderwriter css style sheet to a PrintWriter. 
 */
@Component
public class AddStyleForEmail {
    public void invoke(PrintWriter writer) throws BaseException, MessagingException, MalformedURLException, IOException {
        String host=PageFlowContext.getCoreProxy().getParameterValue("ProductRepository.Host");
        String port=PageFlowContext.getCoreProxy().getParameterValue("ProductRepository.Port");
        
        writer.append("<style type='text/css'>");
        writer.append(Functions.loadUrlContentAsString(new URL("http://"+host+":"+port+"/openunderwriter-theme/css/main.css?&minifierType=css")));
        writer.append(Functions.loadUrlContentAsString(new URL("http://"+host+":"+port+"/pageflow-portlet/css/pageflow.css?minifierType=css")));
        writer.append("</style>");
   }
}