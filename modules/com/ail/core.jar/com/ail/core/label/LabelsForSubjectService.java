package com.ail.core.label;

import static com.ail.core.Functions.isEmpty;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import com.ail.annotation.ServiceArgument;
import com.ail.annotation.ServiceCommand;
import com.ail.annotation.ServiceImplementation;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.PreconditionException;
import com.ail.core.Service;
import com.ail.core.Type;
import com.ail.core.command.Argument;
import com.ail.core.command.Command;
import com.ail.core.factory.UndefinedTypeError;
import com.ail.core.label.LabelsForSubjectService.LabelsForSubjectArgument;

/**
 * Retrieve a list of the labels which are applicable for a subject (target
 * class).
 */
@ServiceImplementation
@Component
public class LabelsForSubjectService extends Service<LabelsForSubjectArgument> {

    @ServiceArgument
    public interface LabelsForSubjectArgument extends Argument {
        void setSubjectArg(Class<?> subjectArg);

        Class<?> getSubjectArg();

        void setLabelsRet(Set<String> labelsRet);

        Type getRootModelArg();

        void setRootModelArg(Type rootModelArg);

        Type getLocalModelArg();

        void setLocalModelArg(Type localModelArg);

        Set<String> getLabelsRet();

        void setDiscriminatorArg(String discriminator);

        String getDiscriminatorArg();
    }

    @ServiceCommand(defaultServiceClass = LabelsForSubjectService.class)
    public interface LabelsForSubjectCommand extends Command, LabelsForSubjectArgument {
    }

    @Override
    public void invoke() throws BaseException {
        if (args.getSubjectArg() == null) {
            throw new PreconditionException("args.getSubjectArg() == null");
        }

        Labels labels = fetchLabels();

        if (labels == null) {
            args.setLabelsRet(new HashSet<>());
            return;
        }

        Set<String> matchingLabels = new HashSet<>();

        searchLabels(matchingLabels, labels, null);

        args.setLabelsRet(matchingLabels);
    }

    private void searchLabels(Set<String> results, Labels labels, String inheritedDiscriminator) {
        for (Labels lbls : labels.getLabels()) {
            if (subjectIsOkay(lbls) && discriminatorIsOkay(lbls, inheritedDiscriminator) && conditionIsOkay(lbls)) {
                searchLabels(results, lbls, lbls.getDiscriminator() != null ? lbls.getDiscriminator() : inheritedDiscriminator);
            }
        }
        for (Label lbl : labels.getLabel()) {
            if (discriminatorIsOkay(lbl, inheritedDiscriminator) && conditionIsOkay(lbl)) {
                results.add(lbl.getText());
            }
        }
    }

    private boolean conditionIsOkay(Constraint lbl) {
        if (isEmpty(lbl.getCondition())) {
            return true;
        }

        if (lbl.getCondition().charAt(0) == '/') {
            if (args.getRootModelArg() != null) {
                return (Boolean) args.getRootModelArg().xpathGet(lbl.getCondition());
            }
        } else {
            if (args.getLocalModelArg() != null) {
                return (Boolean) args.getLocalModelArg().xpathGet(lbl.getCondition());
            }
        }

        return false;
    }

    private boolean discriminatorIsOkay(Constraint constraint, String inheritedDiscriminator) {
        String thisDiscriminator = constraint.getDiscriminator() != null ? constraint.getDiscriminator() : inheritedDiscriminator;

        if (isEmpty(thisDiscriminator) && isEmpty(args.getDiscriminatorArg())) {
            return true;
        } else if (!isEmpty(thisDiscriminator) && !isEmpty(args.getDiscriminatorArg())) {
            return thisDiscriminator.matches(args.getDiscriminatorArg());
        } else if (!isEmpty(args.getDiscriminatorArg()) && constraint instanceof Labels) {
            return true;
        }

        return false;
    }

    private boolean subjectIsOkay(Labels lbls) {
        return lbls.getTarget() == null || args.getSubjectArg().getName().matches(lbls.getTarget());
    }

    private Labels fetchLabels() {
        try {
            return CoreContext.getCoreProxy().newType("Labels", Labels.class);
        } catch (UndefinedTypeError t) {
            return null;
        }
    }
}
