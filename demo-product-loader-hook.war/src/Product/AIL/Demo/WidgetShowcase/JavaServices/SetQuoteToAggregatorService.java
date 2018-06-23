/* Copyright Applied Industrial Logic Limited 2014. All rights reserved. */
import com.ail.core.CoreProxy;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.Section;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

public class SetQuoteToAggregatorService {
    public static void invoke(ExecutePageActionArgument args) {
        Policy policy = (Policy) args.getModelArgRet();

        policy.setAggregator(true);

        Section section=(Section)new CoreProxy().newProductType("AIL.Demo.WidgetShowcase","AggregatedProductAssessmentSheet", Section.class);

        policy.setAssessmentSheetList(section.getAssessmentSheetList());
    }
}