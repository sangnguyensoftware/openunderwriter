
/* Copyright Applied Industrial Logic Limited 2017. All rights reserved. */
import com.ail.core.Note;
import com.ail.insurance.policy.Policy;
import com.ail.pageflow.ExecutePageActionService.ExecutePageActionArgument;

public class LabelDetailsCreateNoteService {
    public static void invoke(ExecutePageActionArgument args) {

        Policy policy = (Policy) args.getModelArgRet();

        policy.getNote().add(new Note());
    }
}