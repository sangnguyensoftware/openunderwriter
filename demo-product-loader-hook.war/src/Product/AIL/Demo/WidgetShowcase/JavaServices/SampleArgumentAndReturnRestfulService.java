
/* Copyright Applied Industrial Logic Limited 2016. All rights reserved. */

import static java.net.HttpURLConnection.HTTP_OK;

import com.ail.core.BaseException;
import com.ail.core.RestfulServiceInvoker;
import com.ail.core.RestfulServiceReturn;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.service.product.ProductService;

/**
 * This sample service demonstrates how to create a restful service in OpenUnderwriter which
 * takes an argument and returns a result. The process is as follows:<ol>
 */
public class SampleArgumentAndReturnRestfulService extends RestfulServiceInvoker {

    /**
     * Entry point. This is the method invoked by the {@link ProductService} in response to
     * a RESTful request. This is, more or less, boilerplate code. In the case of this
     * service, we do expect to be passed an argument; so we pass a Class into {@link RestfulServiceInvoker#invoke() invoke()}
     * to indicate the class which JSon argument should unmarshaled into.
     * Once unmarshalling is complete, the resulting object will be passed into the {@link #service(Argument) service} method.
     */
    public static void invoke(ExecutePageActionArgument args) throws BaseException {
	    new SampleArgumentAndReturnRestfulService().invoke(Argument.class);
	}

    /**
     * This is the business logic of the service. This called with an instance of Argument representing the JSon
     * that was passed into the endpoint.
     */
    public Return service(Argument argument) {
        // Create a return object specifying the HTTP status to be passed back to the client
        Return ret = new Return(HTTP_OK);

		ret.message = "Your name is: "+argument.name+", and your number is: "+argument.number;

		return ret;
	}

	public static class Argument {
		public String name;
		public Long number;
	}

	public static class Return extends RestfulServiceReturn {
	    public Return(int status) {
	        super(status);
	    }
		public String message;
	}
}
