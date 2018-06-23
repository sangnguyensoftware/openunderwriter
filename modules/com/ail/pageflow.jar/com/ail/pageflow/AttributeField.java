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

import static com.ail.core.CoreContext.getRequestWrapper;
import static com.ail.pageflow.PageFlowContext.getPolicy;
import static com.ail.pageflow.util.Functions.simpleDateFormatToJqueryDateFormat;
import static java.lang.String.format;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import com.ail.core.Attribute;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.PostconditionException;
import com.ail.core.Type;
import com.ail.core.TypeXPathException;
import com.ail.core.command.VelocityServiceError;
import com.ail.pageflow.render.RenderService.RenderArgument;
import com.ail.pageflow.render.RenderService.RenderCommand;
import com.ail.pageflow.util.Choice;
import com.ail.pageflow.util.Functions;

/**
 * <p>
 * An AttributeField is an internal PageFlowElement which is used by other elements to
 * render input fields. {@link Question}, {@link RowScroller}, @{link QuestionWithDetails} all make use of
 * AttributeField. An AttributeField is bound to an {@link com.ail.core.Attribute Attribute} and it is
 * the Attribute which defines validations to be applied and it is to the Attribute that the value entered
 * will ultimately be written (it is the Model).
 * </p>
 * <p>
 * The Attribute which the AttributeField is bound to dictates the HTML form
 * elements used to represent it, and the validations that will be applied to
 * it. For example, a "choice" attribute is rendered as a drop down list; a
 * "string" is rendered as a right justified text field.
 * </p>
 * <p>
 * Each AttributeField defines it's column {@link #getTitle() title}, and an
 * optional {@link #getSubTitle() subTitle} which can be used when the titles
 * are fixed. Dynamic titles - the text of which is picked up from another part
 * of the quote - can be defined using {@link #getTitleBinding() titleBinding}
 * and {@link #getSubTitleBinding() subTitleBinding}. It may also define
 * JavaScript to be executed either onLoad (when the page is loaded); or
 * onChange (when a fields value is changed).
 * </p>
 * <p>
 * The AttributeField also supports the concept of RenderHints. The rendering
 * engine will take these hints into account as it displays the field within the
 * page. The values of hint supported are dependent on the Attribute being
 * rendered. The supported hints are listed below:
 * </p>
 * <table border="1">
 * <tr>
 * <th>Attribute Type</th>
 * <th>Render Hints</th>
 * </tr>
 * <tr>
 * <td>choice</td>
 * <td>"dropdown" - Display the options on a drop down menu (default)<br/>
 * "radio" - Display the choice list as radio buttons
 * </tr>
 * <tr>
 * <td>yesorno</td>
 * <td>"dropdown" - Display the options on a drop down menu (default)<br/>
 * "checkbox" - Display as a checkbox<br/>
 * "radio" - Display Yes/No as radio buttons
 * </tr>
 * <tr>
 * <td>string</td>
 * <td>Any of the HTML5 input types: "color", "date", "datetime",
 * "datetime-local", "email", "month", "number", "range", "search", "tel",
 * "time", "url", "week" - The effect these have is highly browser specific.</td>
 * </tr>
 * <tr>
 * <td>date</td>
 * <td>"no-picker" - suppress the use of a date-picker for this attribute.</td>
 * </tr>
 * </table>
 *
 * @see RowScroller
 * @see com.ail.core.Attribute
 * @version 1.1
 */
public class AttributeField extends PageElement {
    private static final long serialVersionUID = 7118438575837087257L;

    /** The fixed subtitle to be displayed with the answer */
    private String subTitle;

    /** JavaScript to be executed when the page loads */
    private String onLoad;

    /** JavaScript to be executed when a field's value is changed */
    private String onChange;

    /**
     * An optional XPath expression. If defined the expression is evaluated
     * against the quotation immediately before the action is to be executed.
     * The action will only be executed if the expression returns true (i.e.
     * <code>(Boolean)model.xpathGet(condition)==true</code>
     */
    private String columnCondition;

    public AttributeField() {
        super();
    }

    /**
     * The fixed sub title to be displayed with the answer. This method returns
     * the raw sub title without expanding embedded variables (i.e. Xpath
     * references like ${person/firstname}).
     *
     * @see #getExpendedSubTitle(Type)
     * @return value of title
     */
    public String getSubTitle() {
        return subTitle;
    }

    /**
     * @see #getSubTitle()
     * @param subTitle
     */
    public void setSubTitle(String subTitle) {
        this.subTitle = subTitle;
    }

    public String getColumnCondition() {
        return columnCondition;
    }

    public void setColumnCondition(String columnCondition) {
        this.columnCondition = columnCondition;
    }

    /**
     * Javascript to be executed when a field's value is changed
     *
     * @return java script
     */
    public String getOnChange() {
        return onChange;
    }

    /**
     * @see #getOnChange()
     * @param onChange
     */
    public void setOnChange(String onChange) {
        this.onChange = onChange;
    }

    /**
     * JavasSript to be executed when the page loads.
     *
     * @return java script
     */
    public String getOnLoad() {
        return onLoad;
    }

    /**
     * @see #getOnLoad()
     * @param onLoad
     */
    public void setOnLoad(String onLoad) {
        this.onLoad = onLoad;
    }

    /**
     * Get the sub title with all variable references expanded. References are
     * expanded with reference to the models passed in. Relative xpaths (i.e.
     * those starting ./) are expanded with respect to <i>local</i>, all others
     * are expanded with respect to the current quotation (from
     * {@link PageFlowContext}).
     *
     * @param local
     *            Model to expand local references (xpaths starting ./) with
     *            respect to.
     * @return Title with embedded references expanded
     * @since 1.1
     */
    public String formattedSubTitle(RenderArgument args) {
        if (getTitle() != null) {
            return expand(i18n(getSubTitle()), PageFlowContext.getPolicy(), args.getModelArgRet());
        } else {
            return null;
        }
    }

    public String urlEncode(String value) {
        return encodeUTF8(value).replace("+", "%20");
    }

    /**
     * Encodes a String into UTF-8. Wraps the potential UnsupportedEncodingException in a try catch as since we are
     * encoding to standard UTF-8 it really should not be thrown.
     * @param unencoded the raw string
     * @return  the UTF-8 encoded string
     */
    public static String encodeUTF8(String unencoded) {
        try {
            return URLEncoder.encode(unencoded, StandardCharsets.UTF_8.name());
        } catch (UnsupportedEncodingException thisWouldBeExtremelySurprising) {
            CoreContext.getCoreProxy().logError("Failed to UTF-8 encode: " + unencoded, thisWouldBeExtremelySurprising);
        }
        return unencoded;
    }

    @Override
    public Type applyRequestValues(Type model) {
        return applyRequestValues(model, "");
    }

    public Type applyRequestValues(Type model, String rowContext) {
        return applyAttributeValues(model, getBinding(), rowContext);
    }

    @Override
    public boolean processValidations(Type model) {
        return applyAttributeValidation(model, getBinding());
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return renderResponse(model, "");
    }

    public Type renderResponse(Type model, String rowContext) throws IllegalStateException, IOException {
        return renderAttribute(model, getBinding(), rowContext, getOnChange(), getOnLoad(), getRenderHint());
    }

    @Override
    public Type renderPageHeader(Type model) throws IllegalStateException, IOException {
        renderPageLevelResponse(model, "");
        return model;
    }

    public void renderPageLevelResponse(Type model, String rowContext) throws IllegalStateException, IOException {
        renderAttributePageLevel(model, getBinding(), rowContext);
    }

    /**
     * Render an AttributeField on the response. In output of this method
     * depends on the mime type of the response being constructed and the
     * specifics of the attribute being rendered. In the context of a text/html
     * response the result will be some kind of form element (input, select,
     * textarea, etc) being returned as a String. The actual element returned
     * depends on the specifics of the Attribute it is being rendered for.
     *
     * @param data
     *            The 'model' (in MVC terms) containing the AttributeField to be
     *            rendered
     * @param boundTo
     *            An XPath expressing pointing at the AttributeField in 'data'
     *            that we're rendering.
     * @param rowContext
     *            If we're rendering into a scroller, this'll be the row number
     *            in xpath predicate format (e.g. "[1]"). Otherwise ""
     * @param onChange
     *            JavaScript onChange event
     * @param onLoad
     *            JavaScript onLoad event
     * @return The HTML representing the attribute as a form element.
     * @throws IllegalStateException
     * @throws IOException
     * @throws PostconditionException
     */
    public Type renderAttribute(Type model, String boundTo, String rowContext, String onChange, String onLoad, String renderHint) throws IllegalStateException, IOException {

        Attribute attr = fetchBoundObject(boundTo, model, Attribute.class);
        attr.setParentType(model);

        // If the attribute has no value, and it does have a "source" then
        // initialise it's value from that source.
        if (attributeHasNoValue(attr)) {
            if (attr.getSource() != null) {
                attr.setValue(PageFlowContext.getPolicy().xpathGet(attr.getSource(), String.class));
            }
        }

        RenderCommand command = buildRenderCommand("AttributeField", attr);
        command.setRowContextArg(rowContext);
        command.setRenderIdArg(encodeId(rowContext + boundTo));
        command.setStyleClassArg(getStyleClass());
        command.setOnChangeArg(onChange);
        command.setOnLoadArg(onLoad);
        command.setRenderHintArg(renderHint);

        try {
            command.invoke();
            return command.getModelArgRet();
        } catch(VelocityServiceError e) {
            e.addBinding(binding);
            throw e;
        } catch (BaseException e) {
            throw new IllegalStateException(e);
        }
    }

    private boolean attributeHasNoValue(Attribute attr) {
        return attr.getValue() == null || attr.getValue().length() == 0 || "0".equals(attr.getValue()) || "?".equals(attr.getValue());
    }

    /**
     * Validate that the values contained in the model (at a specific xpath) are
     * valid. If errors are found, the details are added to the attribute as a
     * sub-attribute with the id 'error'. The value of this attribute indicates
     * the error type.
     *
     * @param model
     *            Model containing data to be validated
     * @param boundTo
     *            XPath expression identifying attribute to validate.
     * @return true if any errors are found, false otherwise.
     */
    protected boolean applyAttributeValidation(Type model, String boundTo) {
        // If we're not bound to anything, don't validate anything.
        // If the field's condition is not met, don't validate anything.
        if (boundTo == null || !conditionIsMet(model) || !columnConditionIsMet(model)) {
            return false;
        }

        Attribute attr = (Attribute) fetchBoundObject(boundTo, model);
        boolean error = false;

        // if there's an error there already, remove it.
        try {
            Functions.removeErrorMarkers(attr);
        } catch (TypeXPathException e) {
            // ignore this - it'll get thrown if there weren't any errors
        }

        if (attr.isChoiceType() && !attr.getValue().equals(i18n("i18n_?"))) {
            String type = attr.getFormatOption("type");

            if (type != null) {
                String valueOfMaster = null;

                if (attr.isChoiceSlaveType()) {
                    valueOfMaster = findValueOfMaster(model, attr, boundTo);

                    for (Attribute assetAttr: model.getAttribute()) {
                        if (valueOfMaster.equals(assetAttr.getId())) {
                            valueOfMaster = assetAttr.getValue();
                            break;
                        }
                    }
                }

                if (!hasChoice(type, attr.getValue(), valueOfMaster)) {
                    Functions.addError("error", "invalid value", attr);
                }
            }
        }

        // Check if the value is undefined or invalid and add the error marker
        // as appropriate
        if (!"no".equals(attr.getFormatOption("required"))) {
            if (!attr.isChoiceType()) {
                if (attr.isUndefined()) {
                    Functions.addError("error", i18n("i18n_required_error"), attr);
                    error = true;
                }
            } else if (!attr.isFreeChoiceType()) {
                if (attr.isUndefined()) {
                    Functions.addError("error", i18n("i18n_required_error"), attr);
                    error = true;
                }
            } else {
                if (attr.getValue().equals(i18n("i18n_?"))) {
                    Functions.addError("error", i18n("i18n_required_error"), attr);
                    error = true;
                }
            }
        }

        if (!error && !attr.isChoiceType() && attr.isInvalid()) {
            Functions.addError("error", i18n("i18n_invalid_error"), attr);
            error = true;
        }

        return error;
    }

    /**
     * Given the details of a slave choice attribute, find the value of the related master.
     * Two things make this possible: 1) A slave attribute's definition includes the master's ID; 2)
     * The master attribute will always be in the same attribute set as the slave. This means
     * that if we take the slave's binding, we can substitute the slave ID with the master ID
     * to arrive at the master's binding. That can then be evaluated against the model object
     * to get the master's value.
     * @param model Model into which the <code>slaveBinding</code> refers.
     * @param slaveAttr The slave attribute
     * @param slaveBinding The slave attribute's binding.
     * @return Value of master attribute
     */
    String findValueOfMaster(Type model, Attribute slaveAttr, String slaveBinding) {
        String slaveAttrId = slaveAttr.getId();

        String masterAttrId = slaveAttr.findChoiceMaster();

        String masterBinding = slaveBinding.replaceFirst("\\[id=['\"]"+slaveAttrId+"['\"]\\]", "[id='"+masterAttrId+"']");

        return model.xpathGet(masterBinding+"/value", String.class);
    }

    private boolean hasChoice(String choiceTypeName, String value, String master) {
        Choice allChoice = (Choice)PageFlowContext.getCoreProxy().newType(choiceTypeName);

        Choice choice = null;

        if (master == null) {
            choice = allChoice;
        } else {
            for(Choice c: allChoice.getChoice()) {
                if (c.getName().equals(master)) {
                    choice = c;
                    break;
                }
            }
        }
        if (choice != null) {
            for(Choice c: choice.getChoice()) {
                if (c.getName().equals(value)) {
                    return true;
                }
            }
        }
        return false;
    }

    private Type renderAttributePageLevel(Type model, String boundTo, String rowContext) throws IllegalStateException, IOException {
        Attribute attr = (Attribute) fetchBoundObject(boundTo, model);

        return executeTemplateCommand("AttributeFieldPageLevel", attr);
    }

    /**
     * Check if 'request' contains a new value for the 'boundTo', and if it
     * does, update model with the new value. If row is other than -1 it is
     * taken to indicate the row within a scroller that boundTo relates to.
     *
     * @param model
     *            Model to be updated
     * @param boundTo
     *            xpath expression pointing into 'model' at the property to be
     *            updated.
     * @param row
     *            The row if the attribute is in a scroller, otherwise -1.
     * @param portletRequest
     *            The request whose parameters should be checked.
     * @return potentially modified model
     */
    protected Type applyAttributeValues(Type model, String boundTo, String rowContext) {
        // If we're not bound to anything, apply nothing.
        // If our condition isn't met, apply nothing.
        if (boundTo == null || !conditionIsMet(model) || !columnConditionIsMet(model)) {
            return model;
        }

        String name = encodeId(rowContext + boundTo);
        String value = getRequestWrapper().getParameter(name);
        Attribute attr;

        attr = (isAbsoluteXpath(boundTo)) ? getPolicy().xpathGet(boundTo, Attribute.class)
                                          : model.xpathGet(boundTo, Attribute.class);

        if (value != null) {
            attr.setValue(value);
        } else if (value == null && "checkbox".equals(getRenderHint()) && attr.isYesornoType()) {
            attr.setValue("No");
        }

        return model;
    }
    private boolean isAbsoluteXpath(String xpath) {
        return xpath.charAt(0) == '/';
    }

    /**
     * If <code>attribute</code> represents a choice slave, return the path of
     * the master's binding.
     *
     * @param attribute
     *            Slave choice.
     * @return master's binding; or, and empty string if <code>attribute</code>
     *         is not a choice slave.
     */
    public String getMastersBinding(Attribute attribute) {
        if (!attribute.isChoiceSlaveType()) {
            return "";
        }

        String masterName = attribute.findChoiceMaster();
        int dotIndex = attribute.getId().indexOf(".");
        String attributeIdShort = dotIndex > 0 ? attribute.getId().substring(dotIndex + 1, attribute.getId().length()) : attribute.getId();

        return getBinding().replaceFirst("([\"']?)" + attributeIdShort + "[\"']?", "$1" + masterName + "$1");
    }

    /**
     * If <code>attribute</code> represents a choice master, return the slaves
     * binding.
     *
     * @param attribute
     *            Master choice.
     * @return slave's binding; or, and empty string if <code>attribute</code>
     *         is not a choice master.
     */
    public String getSlavesBinding(Attribute attribute) {
        if (!attribute.isChoiceMasterType()) {
            return "";
        }

        String slaveName = attribute.findChoiceSlave();
        int dotIndex = attribute.getId().indexOf(".");
        String attributeIdShort = dotIndex > 0 ? attribute.getId().substring(dotIndex + 1, attribute.getId().length()) : attribute.getId();

        return getBinding().replaceFirst("([\"']?)" + attributeIdShort + "[\"']?", "$1" + slaveName + "$1");
    }

    /**
     * Attributes define date patterns using the conventions of Java's SimpleDateFormatter, but
     * jQuery uses it's own format. This method converts from SimpleDateFormatter format to
     * jQuery format.
     * @param attrPattern pattern in SimpleDateFormatter format
     * @return pattern in jQuery datepicker format
     */
    public String patternToJqueryDateFormat(String attrPattern) {
        return simpleDateFormatToJqueryDateFormat(attrPattern);
    }

    public boolean columnConditionIsMet(Type model) {
        return columnCondition == null || (Boolean) model.xpathGet(columnCondition) == true || (Boolean) PageFlowContext.getPolicy().xpathGet(columnCondition) == true;
    }

    public String getOptionsURL(String choiceTypeName) {
        return format("%s://%s:%d/product/%s/GetChoiceOptions?name=%s", CoreContext.getRequestWrapper().getScheme(), CoreContext.getRequestWrapper().getServerName(),
                                                                    CoreContext.getRequestWrapper().getServerPort(), CoreContext.getProductName(), encodeUTF8(choiceTypeName));
    }

    public String getSlaveOptionsURL(String choiceTypeName, Attribute slaveAttribute) {
        String masterValue = findValueOfMaster(slaveAttribute.getParentType(), slaveAttribute, getBinding());
        return format("%s://%s:%d/product/%s/GetChoiceOptions?name=%s&masterValue=%s", CoreContext.getRequestWrapper().getScheme(), CoreContext.getRequestWrapper().getServerName(),
                                                                    CoreContext.getRequestWrapper().getServerPort(), CoreContext.getProductName(), encodeUTF8(choiceTypeName), encodeUTF8(masterValue));
    }
}
