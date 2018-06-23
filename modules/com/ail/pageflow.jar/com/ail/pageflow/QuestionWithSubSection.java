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

import com.ail.core.BaseException;
import com.ail.core.Type;
import com.ail.pageflow.render.RenderService.RenderArgument;
import com.ail.pageflow.render.RenderService.RenderCommand;

/**
 * <p>
 * If the answer to a given question is yes, a number of subsequent questions
 * are asked. This page elements presents the user with a YesOrNo question. If
 * Yes is selected, and panel of subsequent questions is revealed.
 * </p>
 * <p>
 * <img src="doc-files/QuestionWithSubSection-1.png"/>
 * </p>
 * <p>
 * If the question is unanswered, or is answered "No" then the sub section
 * remains hidden as shown above. If "Yes" is selected, the sub section is
 * revealed as below.
 * </p>
 * <p>
 * <img src="doc-files/QuestionWithSubSection-2.png"/>
 * </p>
 * QuestionWithSubSection provides a more flexible alternative to {@see
 * QuestionWithDetails QuestionWithDetails}. It is more flexible in that any
 * number of additional questions can be asked the sub section (including {@see
 * RowScroller} and {@see SectionScroller} elements); whereas
 * QuestionWithDetails only supports a single question.</p>
 * <p>
 * The subsection is only enabled if the question has been answered
 * appropriately. By default this means that the answer is "Yes"; but this may
 * be overridden by setting the detailsEnabledFor property to a semicolon
 * separated list of the answers that the subsection should be enabled for..
 * </p>
 *
 * @see QuestionWithDetails
 * @see RowScroller
 * @see SectionScroller
 */
public class QuestionWithSubSection extends Question {
    private static final long serialVersionUID = 7118438575837087257L;
    private List<String> detailsEnabledForList;

    /** PageElement to be rendered/filled if the sub section is enabled. */
    private List<PageElement> subSection = new ArrayList<PageElement>();

    public QuestionWithSubSection() {
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
     * @return List of answers for which the subsection should be enabled.
     */
    public String getDetailsEnabledFor() {
        return convertListToSemiColonString(detailsEnabledForList);
    }

    /**
     * Define the answers for which the subsection should be enabled.
     *
     * @param detailsEnabledFor
     *            A semicolon separated list of answers for which the subsection
     *            should be enabled.
     */
    public void setDetailsEnabledFor(String detailsEnabledFor) {
        this.detailsEnabledForList = convertSemiColonStringToList(detailsEnabledFor);
    }

    /**
     * Determine if the sub section is enabled for a given model.
     *
     * @param model
     * @return
     */
    public boolean isDetailsEnabled(Type model) {
        com.ail.core.Attribute attr = (com.ail.core.Attribute) model.xpathGet(getBinding());
        return detailsEnabledForList.contains(attr.getValue());
    }

    /**
     * PageElement to be rendered/filled if the Question is answered "Yes".
     *
     * @return sub section page element
     */
    public List<PageElement> getSubSection() {
        return subSection;
    }

    /**
     * @see #getSubSection()
     * @param subSection
     *            sub section page element
     */
    public void setSubSection(List<PageElement> subSection) {
        this.subSection = subSection;
    }

    /**
     * Apply the specified ID to this element and cascade to sub-elements (see
     * {@link #getId()}/{@link #setId(String)}). The ID will only applied if one
     * is not already defined.
     *
     * @param id
     */
    @Override
    public void applyElementId(String basedId) {
        int idx = 0;
        for (PageElement e : subSection) {
            e.applyElementId(basedId + ID_SEPARATOR + (idx++));
        }
        super.applyElementId(basedId);
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
    @Override
    public String formattedTitle(RenderArgument args) {
        if (getTitle() != null) {
            return i18n(expand(getTitle(), args.getPolicyArg(), args.getModelArgRet()));
        } else {
            return null;
        }
    }

    @Override
    public Type applyRequestValues(Type model) {
        return applyRequestValues(model, "");
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        model = super.processActions(model);
        for (PageElement ss : subSection) {
            model = ss.processActions(model);
        }
        return model;
    }

    @Override
    public Type applyRequestValues(Type model, String rowContext) {
        model = super.applyRequestValues(model, rowContext);
        for (PageElement ss : subSection) {
            model = ss.applyRequestValues(model);
        }
        return model;
    }

    @Override
    public boolean processValidations(Type model) {
        boolean error = false;

        // validate the main question which is either a yes/no or a choice
        error |= super.processValidations(model);

        // If 'Yes' is selected, validate the subsection
        com.ail.core.Attribute attr = (com.ail.core.Attribute) model.xpathGet(getBinding());
        if ((attr.isYesornoType() || attr.isChoiceType()) && detailsEnabledForList.contains(attr.getValue())) {
            for (PageElement ss : subSection) {
                error |= ss.processValidations(model);
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
        RenderCommand command = buildRenderCommand("QuestionWithSubSection", model, rowContext);

        command.setRenderIdArg(encodeId(rowContext + binding));

        return invokeRenderCommand(command);
    }

    @Override
    public Type renderPageHeader(Type model) throws IllegalStateException, IOException {
        model = super.renderPageHeader(model);
        for (PageElement ss : subSection) {
            ss.renderPageHeader(model);
        }
        return model;
    }
}
