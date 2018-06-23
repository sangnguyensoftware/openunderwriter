/* Copyright Applied Industrial Logic Limited 2006. All rights Reserved */
/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin Street, Fifth Floor, Boston, MA 02110-1301, USA.
 */
package com.ail.pageflow;

import static com.ail.pageflow.util.Functions.convertListToSemiColonString;
import static com.ail.pageflow.util.Functions.convertSemiColonStringToList;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.ail.core.Attribute;
import com.ail.core.Type;
import com.ail.core.TypeXPathException;
import com.ail.pageflow.render.RenderService.RenderArgument;
import com.ail.pageflow.render.RenderService.RenderCommand;
import com.ail.pageflow.util.Functions;

/**
 * <p>
 * This element handles the common situation where selecting 'yes' in answer to
 * a question leads to the user having to answer a supplementary question (e.g.
 * "If yes, please supply details").
 * </p>
 * <p>
 * This is a simplification of {@link QuestionWithSubSection
 * QuestionWithSubSection} which isn't limited to a single supplementary
 * question.
 * </p>
 * <p>
 * <img src="doc-files/QuestionWithDetails.png"/>
 * </p>
 * <p>
 * The main question must be a YesOrNo question, and must be phrased in such a
 * way that "Yes" leads to more detail being required. As with the
 * {@link Question Question} page element, the detail question's answer field is
 * rendered based on the properties of the {@link com.ail.core.Attribute
 * Attribute} that it is bound to.
 * </p>
 * <p>
 * The detail field is only enabled if the question has been answered
 * appropriately. By default this means that the answer is "Yes"; but this may
 * be overridden by setting the detailsEnabledFor property to a semicolon
 * separated list of the answers that the details should be enabled for..
 * </p>
 * <p>
 * Validation is applied to the YesOrNo question, an answer is considered
 * mandatory. Validation applied to the detail question's answer is again
 * defined by the properties of the {@link com.ail.core.Attribute Attribute}
 * that it is bound to.
 * </p>
 *
 * @version 1.1
 */
public class QuestionWithDetails extends Question {
    private static final long serialVersionUID = 7118438575837087257L;
    private String detailsTitle;
    private String detailsBinding;
    /**
     * Hints to the UI rendering engine specifying details of how the details
     * field should be rendered. The values supported are specific to the type
     * of attribute being rendered.
     */
    private String detailsRenderHint = null;
    private String detailsOnChange = null;
    private String detailsOnLoad = null;
    private List<String> detailsEnabledForList;

    public QuestionWithDetails() {
        super();
        detailsEnabledForList = new ArrayList<String>();
        detailsEnabledForList.add("Yes");
    }

    public List<String> getDetailsEnabledForList() {
        if (detailsEnabledForList == null) {
            detailsEnabledForList = new ArrayList<>();
        }

        return detailsEnabledForList;
    }

    public void setDetailsEnabledForList(List<String> detailsEnabledForList) {
        this.detailsEnabledForList = detailsEnabledForList;
    }

    /**
     * @see #setDetailsEnabledFor(String)
     * @return List of answers for which the details field should be enabled.
     */
    public String getDetailsEnabledFor() {
        return convertListToSemiColonString(detailsEnabledForList);
    }

    /**
     * Define the answers for which the details field should be enabled.
     *
     * @param detailsEnabledFor
     *            A comma separated list of answers for which the details field
     *            should be enabled.
     */
    public void setDetailsEnabledFor(String detailsEnabledFor) {
        this.detailsEnabledForList = convertSemiColonStringToList(detailsEnabledFor);
    }

    public String getDetailsBinding() {
        return detailsBinding;
    }

    public void setDetailsBinding(String detailsBinding) {
        this.detailsBinding = detailsBinding;
    }

    /**
     * The details title to be displayed with the answer. This method returns
     * the raw title without expanding embedded variables (i.e. xpath references
     * like ${person/firstname}).
     *
     * @see #getExpendedDetailsTitle(Type)
     * @return value of title
     */
    public String getDetailsTitle() {
        return detailsTitle;
    }

    /**
     * @see #getDetailsTitle()
     * @param detailsTitle
     */
    public void setDetailsTitle(String detailsTitle) {
        this.detailsTitle = detailsTitle;
    }

    /**
     * Hints to the UI rendering engine specifying details of how the details
     * field should be rendered. The values supported are specific to the type
     * of attribute being rendered.
     *
     * @return the renderHint
     */
    public String getDetailsRenderHint() {
        return detailsRenderHint;
    }

    public void setDetailsRenderHint(String detailsRenderHint) {
        this.detailsRenderHint = detailsRenderHint;
    }

    /**
     * Get the title with all variable references expanded. References are
     * expanded with reference to the models passed in. Relative xpaths (i.e.
     * those starting ./) are expanded with respect to <i>local</i>, all others
     * are expanded with respect to <i>root</i>.
     *
     * @param local
     *            Model to expand local references (xpaths starting ./) with
     *            respect to.
     * @return Title with embedded references expanded
     * @since 1.1
     */
    public String getExpandedDetailsTitle(Type local) {
        return expand(getDetailsTitle(), PageFlowContext.getPolicy(), local);
    }

    /**
     * Get the title with all variable references expanded. References are
     * expanded with reference to the models passed in. Relative xpaths (i.e.
     * those starting ./) are expanded with respect to <i>local</i>, all others
     * are expanded with respect to <i>root</i>.
     *
     * @param root
     *            Model to expand references with respect to.
     * @param local
     *            Model to expand local references (xpaths starting ./) with
     *            respect to.
     * @return Title with embedded references expanded, or null if there is no
     *         title
     * @since 1.1
     */
    public String formattedDetailsTitle(RenderArgument args) {
        if (getTitle() != null) {
            return i18n(expand(getDetailsTitle(), args.getPolicyArg(), args.getModelArgRet()));
        } else {
            return null;
        }
    }

    @Override
    public Type applyRequestValues(Type model) {
        return applyRequestValues(model, "");
    }

    @Override
    public Type applyRequestValues(Type model, String rowContext) {
        model = super.applyRequestValues(model, rowContext);
        model = applyAttributeValues(model, getDetailsBinding(), rowContext);
        return model;
    }

    @Override
    public boolean processValidations(Type model) {
        boolean error = false;

        // validate the yes/no part of the question
        error |= super.processValidations(model);

        // if there's an error there already on the details attribute, remove
        // it.
        try {
            Functions.removeErrorMarkers(model.xpathGet(getDetailsBinding(), Attribute.class));
        } catch (TypeXPathException e) {
            // ignore this - it'll get thrown if there weren't any errors
        }

        // if the question's answer is one for which details are enabled,
        // validate the details
        for (String def : detailsEnabledForList) {
            if (def.equals(model.xpathGet(getBinding() + "/value"))) {
                error |= applyAttributeValidation(model, getDetailsBinding());
            }
        }

        return error;
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return renderResponse(model, "");
    }

    @Override
    public Type renderResponse(Type model, String rowContext) throws IllegalStateException, IOException {
        RenderCommand command = buildRenderCommand("QuestionWithDetails", model, rowContext);

        command.setDetailIdArg(encodeId(rowContext + detailsBinding));
        command.setRenderIdArg(encodeId(rowContext + binding));

        return invokeRenderCommand(command);
    }

    public String getDetailsOnChange() {
        return detailsOnChange;
    }

    public void setDetailsOnChange(String detailsOnChange) {
        this.detailsOnChange = detailsOnChange;
    }

    public String getDetailsOnLoad() {
        return detailsOnLoad;
    }

    public void setDetailsOnLoad(String detailsOnLoad) {
        this.detailsOnLoad = detailsOnLoad;
    }
}
