/* Copyright Applied Industrial Logic Limited 2014. All rights reserved. */
import com.ail.core.CoreProxy;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.Section;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

public class SetQuoteToNormalService {
    public static void invoke(ExecutePageActionArgument args) {
        Policy policy = (Policy) args.getModelArgRet();

        policy.setAggregator(false);

        Section section = (Section)new CoreProxy().newProductType("AIL.Demo.WidgetShowcase", "SingleProductAssessmentSheet", Section.class);

        policy.setAssessmentSheetList(section.getAssessmentSheetList());
    }
}