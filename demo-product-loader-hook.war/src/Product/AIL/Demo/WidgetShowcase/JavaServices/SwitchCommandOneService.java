/* Copyright Applied Industrial Logic Limited 2017. All rights reserved. */
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.insurance.policy.Policy;

public class SwitchCommandOneService {
	public static void invoke(ExecutePageActionArgument args) {
		Policy policy = (Policy) args.getModelArgRet();

		String value = (String)policy.xpathGet("/asset[id='switch']/attribute[id='switchSelect']/value", "null");

		args.setSelectedSwitchIdRet(value);
	}
}