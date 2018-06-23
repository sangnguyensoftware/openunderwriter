
/* Copyright Applied Industrial Logic Limited 2016. All rights reserved. */

import static java.net.HttpURLConnection.HTTP_OK;

import com.ail.core.BaseException;
import com.ail.core.RestfulServiceInvoker;
import com.ail.core.RestfulServiceReturn;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

public class SampleBasicRestfulService extends RestfulServiceInvoker {

	public static void invoke(ExecutePageActionArgument args) throws BaseException {
		new SampleBasicRestfulService().invoke();
	}

	public RestfulServiceReturn service() {
		return new RestfulServiceReturn(HTTP_OK);
	}
}
