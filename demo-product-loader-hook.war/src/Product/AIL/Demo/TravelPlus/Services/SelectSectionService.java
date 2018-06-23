
/* Copyright Applied Industrial Logic Limited 2014. All rights reserved. */

import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.Section;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.util.YesNo;
import com.ail.insurance.policy.PolicyStatus;

public class SelectSectionService {
    /**
     * Service entry point. when a quoted section is selected, enable that section
     * (passed in "selected" action parameter), disable the others (passed in
     * "notselected" action parameter - comma seperated list), and copy selected
     * sections assessment sheet to policy level assessment sheet.
     * 
     * @param args
     *            Contains the quotation object available for initialisation.
     */
    public static void invoke(ExecutePageActionArgument args) {
        Policy policy = (Policy) args.getModelArgRet();

        // get pageflow action parameter 'selected' and enable selected sections and
        // copy sections assessment sheet to policy
        String id = (String) args.getActionArg().xpathGet("attribute[id='selected']/value");
        Section section = policy.getSectionById(id);
        section.setIncluded(YesNo.YES);
        section.setExcluded(YesNo.NO);
        section.xpathSet("attribute[id='sectionEnabled']/value", YesNo.YES.getLongName());
        AssessmentSheet assessmentSheet = section.getAssessmentSheet();
        policy.setAssessmentSheet(assessmentSheet);
        policy.setStatus(PolicyStatus.QUOTATION);

        // get pageflow action parameter 'selected' and disable not selected sections
        String ids = (String) args.getActionArg().xpathGet("attribute[id='notselected']/value");
        String[] idArray = ids.trim().split("\\s*,\\s*");
        for (int x = 0; x < idArray.length; x++) {
            section = policy.getSectionById(idArray[x]);
            section.setIncluded(YesNo.NO);
            section.setExcluded(YesNo.YES);
            section.xpathSet("attribute[id='sectionEnabled']/value", YesNo.NO.getLongName());
        }

    }
}