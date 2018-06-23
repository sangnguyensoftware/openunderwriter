package com.ail.core.product;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceInterface;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.product.liferay.LiferayDoesProductRootFolderExistService;

@ServiceInterface
public class DoesProductRootFolderExistService {
    
    @ServiceArgument
    public interface DoesProductRootFolderExistArgument extends Argument {
        void setProductRootFolderExistsRet(boolean productRootFolderExistsRet);
        boolean getProductRootFolderExistsRet();
    }
    
    @ServiceCommand(defaultServiceClass=LiferayDoesProductRootFolderExistService.class)
    public interface DoesProductRootFolderExistCommand extends DoesProductRootFolderExistArgument, Command {
    }
}
