package com.ail.core.product;

import java.net.URL;
import java.net.URLConnection;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.product.liferay.LiferayProductUrlToConnectionService;

@ServiceInterface
public class ProductUrlToConnectionService {
    
    @ServiceArgument
    public interface ProductUrlToConnectionArgument extends Argument {
        public void setProductUrlArg(URL productUrlArg);
        public URL getProductUrlArg();
        public void setURLConnectionRet(URLConnection urlConnectionRet);
        public URLConnection getURLConnectionRet();
    }
    
    @ServiceCommand(defaultServiceClass=LiferayProductUrlToConnectionService.class)
    public interface ProductUrlToConnectionCommand extends ProductUrlToConnectionArgument, Command {
    }
}
