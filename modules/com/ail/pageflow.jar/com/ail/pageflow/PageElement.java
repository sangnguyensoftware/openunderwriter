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

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.StringUtils;

import com.ail.core.Attribute;
import com.ail.core.BaseException;
import com.ail.core.CoreContext;
import com.ail.core.CoreProxy;
import com.ail.core.Identified;
import com.ail.core.Type;
import com.ail.core.command.VelocityServiceError;
import com.ail.core.context.RequestWrapper;
import com.ail.core.context.ResponseWrapper;
import com.ail.core.language.I18N;
import com.ail.core.product.HasPermissionService.HasPermissionCommand;
import com.ail.pageflow.action.ActionService.ActionCommand;
import com.ail.pageflow.portlet.PageFlowCommon;
import com.ail.pageflow.portlet.PageFlowPortlet;
import com.ail.pageflow.render.RenderService.RenderArgument;
import com.ail.pageflow.render.RenderService.RenderCommand;
import com.ail.pageflow.util.ErrorText;
import com.ail.pageflow.util.Functions;
import com.ail.pageflow.util.HelpText;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * Base class for all UI elements. Base properties common to all elements are implemented here along
 * with implementations of life-cycle methods.
 */
public abstract class PageElement extends Type implements Identified, Comparable<PageElement> {
    private static final long serialVersionUID = 1L;
    private static Pattern OUTLINE_FORMAT=Pattern.compile("^[0-9.]*$");
    public static final String OPERATION_PARAM="op";

    /** Separator used in the {@link #id} property */
    protected static final char ID_SEPARATOR='-';
    private transient PropertyChangeSupport changes=new PropertyChangeSupport(this);

    /** Id which is unique within the element's container */
    protected String id;

    /**
     * Optional order indicator in outline style (e.g. "1" "1.2" "1.2.1"). Container elements use this
     * to order their elements during rendering
     */
    protected String order;

    /**
     * JXPath binding related to the context of this element. The expression always relates to some part
     * of the {@link com.ail.openquote.Policy Policy} model. Most containers pass their element's
     * some sub part of the Policy to work with. For example, the RowScroller element passes each of
     * it's elements a context pointing to the thing they are 'Scrolling' over.
     */
	protected String binding;

    /** Optional class to be referred to by style sheets */
    protected String styleClass;

    /** Hints to the UI rendering engine specifying details of how this field should be rendered. The values supported
     * are specific to the type of attribute being rendered. */
    private String renderHint;

    /** Optional ref for the elements presentation layer */
    protected String ref;

    /** Optional help texts for the elements presentation layer */
    protected HelpText helpText;
    protected HelpText hintText;
    protected List<ErrorText> errorText;

    /**
     * List of action associated with this element.
     */
    private ArrayList<Action> action;

    /**
     * An optional XPath expression. If defined the expression is evaluated against the quotation
     * immediately before the action is to be executed. The action will only be executed if the expression
     * returns true (i.e. <code>(Boolean)model.xpathGet(condition)==true</code>
     */
    private String condition;

    /** The fixed title to be displayed with the answer */
    private String title;

    /** The permissions that apply to this */
    protected String permissions;

    /** Optional property limiting the submit scope of this element to the pageSection with the id specified. */
    private String submitTarget = null;

    /**
     * Default constructor
     */
    public PageElement() {
        super();
        action = new ArrayList<>();
        errorText=new ArrayList<>();;
    }

    public PageElement(String condition) {
        this();
    	this.condition=condition;
    }

    /**
     * The fixed title to be displayed with the element. This method returns the raw title without
     * expanding embedded variables (i.e. xpath references like ${person/firstname}).
     * @see #getExpandedTitle(Type)
     * @return value of title
     */
    public String getTitle() {
        return title;
    }

    /**
     * @see #getTitle()
     * @param title
     */
    public void setTitle(String title) {
        this.title = title;
    }

    /**
     * Get the title with all variable references expanded. References are expanded with
     * reference to the models passed in. Relative xpaths (i.e. those starting ./) are
     * expanded with respect to <i>local</i>, all others are expanded with respect to
     * <i>root</i>.
     * @param root Model to expand references with respect to.
     * @param local Model to expand local references (xpaths starting ./) with respect to.
     * @return Title with embedded references expanded or null if no title is defined
     * @since 1.1
     */
    public String formattedTitle(RenderArgument args) {
        if (getTitle()!=null) {
            return expand(i18n(getTitle()), PageFlowContext.getPolicy(), args.getModelArgRet());
        }
        else {
            return null;
        }
    }

    /**
     * The permissions Action that applies to this PageElement
     * @return  the permissions
     */
    public String getPermissions() {
        return permissions;
    }

    /**
     * @see #getPermissions()
     * @param permissions
     */
    public void setPermissions(String permissions) {
        this.permissions = permissions;
    }

    /**
     * Evaluates any conditions present on this PageElement as well as any permissions.
     * If no conditions or permissions exist then returns true.
     * @param model
     * @return  True if there is no condition or it evaluates to true AND there is no permission or permission is permitted for the current user
     */
    protected boolean conditionIsMet(Type model) {
        return (condition==null || (Boolean)model.xpathGet(condition)==true) && hasPermission();
    }

    /**
     * Checks to see if the current user has permission for this resource. If there is no permission set for this resource then by default it
     * is unrestricted and access is permitted, otherwise the current user will be checked to see if they have the right permission.
     * @return  true if the current user has permission for this resource or no permissions have been set, else false
     */
    protected boolean hasPermission() {
        if (StringUtils.isEmpty(permissions)) {
            return true;
        } else {
            HasPermissionCommand hpc = PageFlowContext.getCoreProxy().newCommand(HasPermissionCommand.class);
            hpc.setNameArg(PageElement.class.getName());
            hpc.setPermissionsArg(permissions);
            try {
                hpc.invoke();
            } catch (BaseException e) {
                throw new IllegalStateException(e);
            }
            return hpc.getPermittedRet();
        }
    }

    /**
     * The property change listener is primarily provided for listening
     * to changes to the {@link #getOrder() order} property. The {@link com.ail.pageflow.util.OrderedList}
     * uses this listener to detect changes to element's order properties.
     * @param l Listener
     */
    public void addPropertyChangeListener(PropertyChangeListener l) {
        changes.addPropertyChangeListener(l);
    }

    /**
     * Get the model binding if any. The model binding is an xpath expression
     * which binds this page element to some part of the model. In the case of
     * elements held within a {@link Repeater} (e.g. a {@link RowScroller}) the
     * binding is partial. The Repeater is bound to a collection in the
     * model, and the PageElements within the Repeater are bound relative to the
     * Repeater.
     * @return binding XPath expression
	 */
    public String getBinding() {
		return binding;
	}

    /**
     * Set the model binding for this page element.
     * @see #getBinding()
     * @param binding Page element's model binding
     */
    public void setBinding(String binding) {
		this.binding = binding;
	}

    /**
     * Elements must have an ID associated with them, by default the system will generate an ID
     * for each element as it is created, but these should be considered as unreliable as they
     * change each time the system is restarted.
     * The IDs are important for
     * @return Element's ID
     */
    @Override
    public String getId() {
		return id;
	}

    /**
     * @see #getId()
     * @param id Page element's ID
     */
    @Override
    public void setId(String id) {
		this.id = id;
	}

    /**
     * Apply the specified ID to this element (see {@link #getId()}/{@link #setId(String)}). The ID
     * should only be applied if one is not already defined. By default, the elements of a PageFlow
     * may be defined with or without IDs, however for page actions also need to be able to
     * identify the element they relate to. For this reason, once the PageFlow is created, this
     * method is invoked in the {@link PageFlow} object in order to generate IDs for all elements.
     * @param id
     */
    public void applyElementId(String id) {
		if (this.id==null) {
			setId(id);
		}
	}

	/**
     * Get the style class if any - for this page element.
     * @return style class name
     */
    public String getStyleClass() {
        return styleClass;
    }

    /**
     * Set the style class for this page element.
     * @see #getStyleClass()
     * @param styleClass Page element's style class.
     */
    public void setStyleClass(String styleClass) {
        this.styleClass = styleClass;
    }

    /**
     * Get the ref if any - for this page element.
     * @return ref
     */
    public String getRef() {
        return ref;
    }

    /**
     * Set the ref for this page element.
     * @see #getRef()
     * @param ref Page element's ref.
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    /**
     * Hints to the UI rendering engine specifying details of how this field should be rendered. The values supported
     * are specific to the type of attribute being rendered.
     * @return the renderHint
     */
    public String getRenderHint() {
        return renderHint;
    }

    /**
     * @see #getRenderHint()
     * @param renderHint the renderHint to set
     */
    public void setRenderHint(String renderHint) {
        this.renderHint = renderHint;
    }

    /**
     * Get the hints list if any - for this page element.
     * @return hint
     */
    public HelpText getHintText(){
    	return hintText;
    }

    /**
     * Set the hints for this page element.
     * @see #getHint()
     * @param hint Page element's hint.
     */
    public void setHintText(HelpText hintText){
    	this.hintText = hintText;
    }

    /**
     * Get the help if any - for this page element.
     * @return help
     */
    public HelpText getHelpText(){
    	return helpText;
    }

    /**
     * Set the help for this page element.
     * @see #getHelp()
     * @param note Page element's help.
     */
    public void setHelpText(HelpText helpText){
    	this.helpText = helpText;
    }

    /**
     * Get the error list if any - for this page element.
     * @return alert
     */
    public List<ErrorText> getErrorText(){
    	return errorText;
    }

    /**
     * Set the error list for this page element.
     * @see #getAlert()
     * @param alert Page element's error.
     */
    public void setErrorText(List<ErrorText> errorText){
    	this.errorText = errorText;
    }

    /**
     * Fetch the errors (from this PageElement's error list) which are applicable to an error ID.
     * A page element may define any number of errors, each with an optional ID. When the render
     * process detects a validation error, it inspects the element's error list to determine what
     * to render and how to render it. Within a page element, the same error ID may be defined
     * more than once, leading to more than one action being taken.
     * @param errorId Error to lookup in the element's error list.
     * @return List of matching errors, may be zero length. Never null.
     */
    List<ErrorText> fetchErrors(String errorId) {
    	List<ErrorText> ret=new ArrayList<>();

    	for(ErrorText err: errorText) {
    		if (err.getError()==null || err.getError().equals(errorId)) {
    			ret.add(err);
    		}
    	}

    	return ret;
    }

    /**
     * A list of the actions associated with this element. An action is some processing which
     * takes place in response to a life-cycle even, or a page event (e.g. pressing a button).
     * @return List of Actions for this element.
     */
    public ArrayList<Action> getAction() {
        return action;
    }

    /**
     * @see #getAction()
     * @param action Actions to be associated with this element.
     */
    public void setAction(ArrayList<Action> action) {
        this.action = action;
    }

    /**
     * get the page element's condition
     * @see #condition
     * @return current value of property
     */
    public String getCondition() {
        return condition;
    }

    /**
     * Set the page element's condition.
     * @see #condition
     * @param condition
     */
    public void setCondition(String condition) {
        this.condition = condition;
    }

    public String getSubmitTarget() {
        return submitTarget;
    }

    public void setSubmitTarget(String submitTarget) {
        this.submitTarget = submitTarget;
    }

    /**
     * Life-cycle method invoked at the beginning of the response handling process.
     * @param model
     * @return potentially modified model
     */
    public void onStartProcessAction(Type model) {
        for(Action a: action) {
            a.onStartProcessAction(model);
        }
    }

    /**
     * Life-cycle method invoked at the beginning of the response handling process. UI components
     * are expected to use this event to update their model state with respect to the values
     * passed back from the page in the <code>request</code> parameter.
     * @param model
     * @return potentially modified model
     */
    public Type applyRequestValues(Type model) {
        // default implementation does nothing.
    	return model;
    }

    /**
     * Life-cycle method invoked following {@link #applyRequestValues(ActionRequest, ActionResponse, Type)}.
     * Components use this as an opportunity to validate their model state.
     * Note: This step is skipped if the request is marked as {@link PageFlowPortlet#immediate(ActionRequest) immediate}
     * @param model
     * @return true if any validation errors are found, false otherwise.
     */
    public boolean processValidations(Type model) {
	    return false;
    }

    /**
     * Life-cycle method invoked following {@link #applyRequestValues(ActionRequest, ActionResponse, Type)}. This
     * event is only invoked if the request is marked as {@link PageFlowCommon#immediate(ActionRequest) immediate}, or
     * {@link #processValidations(ActionRequest, ActionResponse, Type)} returned false - indicating that there are no
     * page errors.
     * @param model
     * @return potentially modified model
     */
    public Type processActions(Type model) throws BaseException {
        for(Action a: action) {
            model=a.processActions(model);
        }

        return model;
    }

    /**
     * This event is fired just prior to {@link #renderResponse(RenderRequest, RenderResponse, Type)} to give components
     * the chance to generate page level output before the portlet page's main form opens. For example, components might
     * add JavaScript code to the start of the portlet using this event. Note:
     * As we're in a portlet environment this method does not write to HTML &lt;HEAD&gt; itself, it simply outputs at the beginning
     * of the portlet.
     * @param portletWrapper
     * @param response
     * @param model
     * @throws IllegalStateException
     * @throws IOException
     */
    public Type renderPageHeader(Type model) throws IllegalStateException, IOException {
        // default implementation does nothing.
        return model;
    }

    /**
     * This event is fired just after {@link #renderResponse(RenderRequest, RenderResponse, Type)} to give components
     * the chance to generate page level output after the portlet page's main form closes.
     * @param portletWrapper
     * @param response
     * @param model
     * @throws IllegalStateException
     * @throws IOException
     */
    public Type renderPageFooter(Type model) throws IllegalStateException, IOException {
        // default implementation does nothing.
        return model;
    }

    /**
     * This is the last event fired in the request/response process. Elements are expected to render themselves at this point.
     * @param portletWrapper
     * @param response
     * @param model
     * @throws IllegalStateException
     * @return potentially modified model
     * @throws IOException
     * @throws BaseException
     */
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        // by default only process actions.
        for(Action a: action) {
            model=a.renderResponse(model);
        }

        return model;
    }

    /**
     * Page elements are considered to be the same by id if their id's match and are not null.
     */
    @Override
    public boolean compareById(Identified that) {
        if ((that instanceof PageElement)) {
            PageElement thatPageElement=(PageElement)that;

            return (this.id!=null && this.id.equals(thatPageElement.id));
        }
        else {
            return false;
        }
    }

    /**
     * The comparison of PageElements is based on the value of their respective {@link #order} fields.
     * <ol><li>If the values of <i>order</i> are identical, a zero is returned indicating that they should be considered
     * to be same.</li>
     * <li>If both have outline style IDs (e.g. "1.1", "1.1.3" etc) - that is to say they match the
     * regular expression '^[0-9.]$' - then they are compared and ordered as they would be in the context
     * of a document (1.1 comes before 1.2; 10.5 comes after 10.).</li>
     * <li>As a fall back, a simple {@link String#compareTo(String)} is used.</li></ol>
     */
    @Override
    public int compareTo(PageElement that) {
        if (that.order==null) {
            return 1;
        }
        else if (this.order.equals(that.order)) {
            return 0;
        }
        else if (OUTLINE_FORMAT.matcher(this.order).matches() && OUTLINE_FORMAT.matcher(that.order).matches()) {
            String[] saThis=this.order.split("\\.");
            String[] saThat=that.order.split("\\.");

            try {
                for(int i=0 ; i<saThis.length ; i++) {
                    if (!saThis[i].equals(saThat[i])) {
                        return Integer.parseInt(saThis[i]) - Integer.parseInt(saThat[i]);
                    }
                }

                return -1;
            }
            catch(NoSuchElementException e) {
                return 1;
            }
        }
        else {
            return this.order.compareTo(that.order);
        }
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        String oldOrder=this.order;
        this.order = order;
        changes.firePropertyChange("order", oldOrder, order);
    }

    public String i18n(String key) {
        return I18N.i18n(key);
    }

    public String i18n(String key, Object... args) {
        String format = I18N.i18n(key);
        Formatter formatter = new Formatter();
        String ret = formatter.format(format, args).toString();
        formatter.close();
        return ret;
    }

    /**
     * @param commandName The name of the render command
     * @param model The type representing the data to be rendered
     * @param onChange JavaScript to be attached to any onchange event
     * @param onLoad JavaScript to be attached to any onload event
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    protected Type executeTemplateCommand(String commandName, Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand(commandName, model, "");
    }

    /**
     * @param commandName The name of the render command
     * @param model The type representing the data to be rendered
     * @param rowContext Unique string representing the row being rendered
     * @param onChange JavaScript to be attached to any onchange event
     * @param onLoad JavaScript to be attached to any onload event
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    protected Type executeTemplateCommand(String commandName, Type model, String rowContext) throws IllegalStateException, IOException {
        if (!conditionIsMet(model)) {
            return model;
        }

        RenderCommand command = buildRenderCommand(commandName, model, rowContext);

        return invokeRenderCommand(command);
    }

    /**
     * Build, but do not execute, a render command
     * @param commandName The name of the render command
     * @param model The type representing the data to be rendered
     * @param rowContext Unique string representing the row being rendered
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    protected RenderCommand buildRenderCommand(String commandName, Type model, String rowContext) throws IllegalStateException, IOException {
        RenderCommand command = buildRenderCommand(commandName, model);

        command.setRowContextArg(rowContext);

        return command;
    }

    /**
     * Build, but do not execute, a render command
     * @param commandName The name of the render command
     * @param model The type representing the data to be rendered
     * @return
     * @throws IllegalStateException
     * @throws IOException
     */
    protected RenderCommand buildRenderCommand(String commandName, Type model) throws IllegalStateException, IOException {
        RenderCommand command = null;

        RequestWrapper request = PageFlowContext.getRequestWrapper();
        ResponseWrapper response = PageFlowContext.getResponseWrapper();

        command = PageFlowContext.getCoreProxy().newCommand(commandName, request.getResponseContentType(), RenderCommand.class);

        command.setRequestArg(request);
        command.setResponseArgRet(response);
        command.setModelArgRet(model);
        command.setPolicyArg(PageFlowContext.getPolicy());
        command.setPageElementArg(this);
        command.setWriterArg(response.getWriter());
        command.setStyleClassArg(getStyleClass());
        command.setRefArg(getRef());
        command.setCoreArg(PageFlowContext.getCoreProxy().getCore());
        command.setPageFlowSessionTypeArg(PageFlowContext.getSessionTemp());
        command.setRenderHintArg(getRenderHint());
        command.setRenderIdArg(getId());

        return command;
    }

    protected Type invokeRenderCommand(RenderCommand command) {
        if (!conditionIsMet(command.getModelArgRet())) {
            return command.getModelArgRet();
        }

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

    private static void lookupErrorTranslation(String error, StringBuffer errors, List<ErrorText> errorList) {
        boolean errorFound=false;

        if (errorList.size()!=0) {
            for(ErrorText e: errorList) {
                if (error.equals(e.getError())) {
                    if (errors.length()!=0) {
                        errors.append(", ");
                    }
                    errors.append(I18N.i18n(e.getText()));
                    errorFound=true;
                }
            }
        }

        if (!errorFound) {
            errors.append(I18N.i18n(error));
        }
    }

    /**
     * Find all the the errors (if any) associated with an element in a model, and return them.
     * @param model The model to look in for the error
     * @return The error message, or "&nbsp;" (an empty String) if no message is found.
     */
    public String findErrors(Type model, PageElement element) {
        return findError("", model, element);
    }

    /**
     * Find the error(s) (if any) associated with an element in a model, and return them.
     * @param regexPattern The regex pattern for the errors to return, e.g. "(duration|overlap).*"
     * @param model The model to look in for the error
     * @return A JSON friendly, comma separated list of error strings, e.g. "Error 1", "Error 2"
     */
    public String findErrorsByRegex(String regexPattern, Type model, PageElement element) {
        return Functions.findErrorsByRegex(regexPattern, model, element);
    }

    /**
     * Find the error(s) (if any) associated with an element in a model, and return them.
     * @param errorFilter Which errors to return
     * @param model The model to look in for the error
     * @return The error message, or "&nbsp;" (an empty String) if no message is found.
     */
    public String findError(String errorFilter, Type model, PageElement element) {
        StringBuffer error=new StringBuffer();

        if (model!=null) {
            for(Attribute attr: model.getAttribute()) {
                if (attr.getId().startsWith("error."+errorFilter)) {
                    lookupErrorTranslation(attr.getValue(), error, element.getErrorText());
                }
            }
        }

        return (error.length()==0) ? "&nbsp;" : error.toString();
    }

    /**
     * return true if the specified object has a specific error marker associated with it.
     * @see #hasErrorMarker(Type)
     * @param id Name of error marker to look for
     * @param model Object to check for the error marker
     * @return true if the error marker is found, false otherwise (including if model==null).
     */
    public boolean hasErrorMarker(String id, Type model) {
        if (model!=null) {
            for(Attribute a: model.getAttribute()) {
                if(a.getId().startsWith("error."+id)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Return true if the specified object has any UI error markers associated with it. UI
     * components use the conversion of attaching error attributes to model element to indicate
     * validation failures. This method will return true if it finds any such attributes
     * associated with the specified object.
     * @param model Object to check for error markers
     * @return true if error markers are found, false otherwise (including if model==null).
     */
    public boolean hasErrorMarker(Type model) {
        if (model!=null) {
            for(Attribute a: model.getAttribute()) {
                if(a.getId().startsWith("error.")) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * Products frequently refer to content from their Registry or Pageflows by "relative" URLs. This method
     * expands relative URLs into absolute product URLs - i.e. a URL using the "product:" protocol. A relative URL
     * is one that starts with "~/", where "~" is shorthand for the product's home location. None relative URLs are
     * returned without modification.
     * @param url URL to be checked and expanded if necessary
     * @param productTypeId Product to be used in the expanded URL
     * @return Expanded URL if it was relative, URL as passed in otherwise.
     */
    public String expandRelativeUrlToProductUrl(String url, String productTypeId) {
        if (url.startsWith("~/")) {
            RequestWrapper request=CoreContext.getRequestWrapper();
            return "product://"+request.getServerName()+":"+request.getServerPort()+"/"+productTypeId.replace('.', '/')+url.substring(1);
        }
        else {
            return url;
        }
    }

    /**
     * @see encodeId
     * @param HTML Id
     * @return XPath expression
     */
    public String decodeId(String id) {
        return new String(Base64.decodeBase64(id));
    }

    /**
     * Convert an XPath expression in to a format that will be accepted as an HTML element's id.
     * The data binding mechanism used in openquote's UI is based on xpath. A field in a UI form
     * is bound to the quote object by means of the field's 'id'; as the pages are generated the
     * IDs are give the value of an xpath expression pointing into the quote model.<p>
     * However, xpath expressions may contain characters that aren't compatible with HTML IDs (one
     * example being the single quote character). This method converts xpaths into a form that is
     * safe to be used as IDs, and is also able to be converted back into a xpath by the {@link PageElement#decodeId(String)}
     * method.
     * @param XPath expression
     * @return HTML Id
     */
    public String encodeId(String xpath) {
        return new String(Base64.encodeBase64URLSafe(xpath.getBytes()));
    }

    protected Type executeActionCommand(String commandName, Type model) throws BaseException {
        CoreProxy core = PageFlowContext.getCoreProxy();
        ActionCommand lsac = core.newCommand(commandName, ActionCommand.class);
        lsac.setPageElementArg(this);
        lsac.setTypeArgRet(model);
        lsac.invoke();
        return lsac.getTypeArgRet();
    }

    /**
     * Utility method to expand 'variables' embedded in a string with respect to a model. Variables
     * are in the form '${&lt;xpath&gt;}', where xpath is an expression compatible with JXPath. The
     * xpath expression is evaluated against a <i>model</i> and the result placed into the string returned.</p>
     * <p>Two models are supported: <code>root</code> and <code>local</code>. XPath expressions starting with '.'
     * are evaluated against <code>local</code>; all others are evaluated against <code>root</code>.</p>
     * For example: if the <i>src</i> value of <b>"Your quote number is: ${/quoteNumber}"</b> is passed in with a
     * <i>model</i> containing value of 'FQ1234' in it's <code>quoteNumber</code> property; this method would
     * return <b>"Your quote number is: FQ1234"</b>.
     * @param src Source string containing embedded variables
     * @param root Any xpath expression not starting with '.' is evaluated against this instance
     * @param local Any xpath expression starting with '.' is evaluated against this instance
     * @return A string matching <i>src</i> but with variable references expanded.
     * @see #expand(String, Type)
     */
    public String expand(String src, Type root, Type local) {
        if (src!=null) {
            int tokenStart, tokenEnd;
            StringBuilder buf=new StringBuilder(src);

            do {
                tokenStart=buf.indexOf("${");
                tokenEnd=buf.indexOf("}", tokenStart);

                if (tokenStart>=0 && tokenEnd>tokenStart) {
                    String val=null;

                    try {
                        if (buf.charAt(tokenStart+2)!='/') {
                            val=(String)local.xpathGet(buf.substring(tokenStart+2, tokenEnd));
                        }
                        else {
                            val=(String)root.xpathGet(buf.substring(tokenStart+2, tokenEnd));
                        }
                    }
                    catch(Throwable t) {
                        // ignore this - the 'if val==null' below will handle the problem.
                    }

                    if (val==null) {
                        val="<b>could not resolve: "+buf.substring(tokenStart+2, tokenEnd)+"</b>";
                    }

                    buf.replace(tokenStart, tokenEnd+1, val);
                }
            } while(tokenStart>=0 && tokenEnd>=0);

            return buf.toString();
        }
        else {
            return null;
        }
    }

    /**
     * <p>Fetch the object bound to this page element with respect to the value of the element's {@link #binding} property.
     * If the binding is absolute (starts with a '/') then it is evaluated against the current policy object, otherwise (
     * if it is relative) it is evaluated against <code>model</code>.</p>
     * <p>If any error is encountered during the evaluation of the binding, then the value of the <code>fallback</code> parameter
     * is returned instead.</p>
     * @param model Context object to use when the binding is relative.
     * @param fallBack Default return if evaluation fails.
     * @param clazz Type of object to be returned.
     * @return Evaluated result.
     */
    public <T> T fetchBoundObject(Type model, T fallBack, Class<T> clazz) {
        if (model == null) {
            return fallBack;
        }

        if (getBinding()!=null && getBinding().charAt(0) == '/') {
            return PageFlowContext.getPolicy().xpathGet(getBinding(), fallBack, clazz);
        }
        else {
            return model.xpathGet(getBinding(), fallBack, clazz);
        }
    }

    /**
     * @deprecated Use {@link #fetchBoundObject(Type, Object, Class)} instead.
     */
    @Deprecated
    public Object fetchBoundObject(Type model, Object fallBack) {
        return fetchBoundObject(model, fallBack, Object.class);
    }

    /**
     * Fetch the object bound to the specified binding. If the binding is absolute
     * (starts with a '/') then it is evaluated against the current policy.
     * Otherwise (if it is relative) it is evaluated against the <code>model</code>.
     * @param binding XPath to be evaluated
     * @param model Context object to use when the binding is relative.
     * @param clazz Type of object to be returned.
     * @return Evaluated result.
     */
    public <T> T fetchBoundObject(String binding, Type model, Class<T> clazz) {
        if (model == null) {
            return null;
        }

        if (binding!=null && binding.charAt(0) == '/') {
            return PageFlowContext.getPolicy().xpathGet(binding, clazz);
        }
        else {
            return model.xpathGet(binding, clazz);
        }
    }

    /**
     * @deprecated Use {@link #fetchBoundObject(String, Type, Class)} instead.
     */
    @Deprecated
    public Object fetchBoundObject(String binding, Type model) {
        return fetchBoundObject(binding, model, Object.class);

    }

    /**
     *
     * @param model
     * @return
     * @deprecated use {@link #fetchBoundObject(Type, Object)} instead, supplying a fallback argument
     */
    @Deprecated
    public Object fetchBoundObject(Type model) {
        return fetchBoundObject(getBinding(), model);
    }

    @SuppressWarnings("unchecked")
    public Iterator<? extends Object> fetchBoundCollection(Type model) {
        if (model == null) {
            return null;
        }

        if (binding!=null && binding.charAt(0) == '/') {
            return PageFlowContext.getPolicy().xpathIterate(binding);
        }
        else {
            return model.xpathIterate(binding);
        }
    }

    /**
     * Utility method to expand 'variables' embedded in a string with respect to a model. Variables
     * are in the form '${&lt;xpath&gt;}', where xpath is an expression compatible with JXPath. The
     * xpath expression is evaluated against <i>root</i> and the result placed into the string returned.</p>
     * For example: if the <i>src</i> value of <b>"Your quote number is: ${/quoteNumber}"</b> is passed in with a
     * <i>model</i> containing value of 'FQ1234' in it's <code>quoteNumber</code> property; this method would
     * return <b>"Your quote number is: FQ1234"</b>.
     * @param src Source string containing embedded variables
     * @param model All xpath expressions are evaluated against this object
     * @return A string matching <i>src</i> but with variable references expanded.
     * @see #expand(String, Type, Type)
     */
    public String expand(String src, Type model) {
        StringWriter writer = new StringWriter();
        BufferedReader reader = null;

        try {
            URL url=new URL(src);
            reader=new BufferedReader(new InputStreamReader(url.openStream()));

            for(String line=reader.readLine() ; line!=null ; line=reader.readLine()) {
                writer.write(expand(line, model, model));
            }
        }
        catch(Exception e) {
            PageFlowContext.getCoreProxy().logError("Failed to read input stream.", e);
        }
        finally {
            if (reader!=null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    PageFlowContext.getCoreProxy().logError("Failed to read input stream.", e);
                }
            }
        }

        return writer.toString();
    }

    public String getUsernameForUserId(Long userId) {
        try {
            return UserLocalServiceUtil.getUser(userId).getScreenName();
        } catch (PortalException | SystemException e) {
            return "error";
        } catch (NullPointerException e) {
            return userId.toString();
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        PageElement other = (PageElement) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }
}
