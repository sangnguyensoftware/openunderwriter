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

import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import com.ail.core.Attribute;
import com.ail.core.BaseException;
import com.ail.core.Type;
import com.ail.core.context.RequestWrapper;
import com.ail.core.data.XPath;
import com.ail.insurance.HasAssets;
import com.ail.insurance.policy.Asset;
import com.ail.insurance.policy.Policy;
import com.ail.insurance.policy.Section;
import com.ail.pageflow.util.OrderedList;

/**
 * A Repeater represents Collections of data on the UI. Subclasses of this type define how the
 * data is actually rendered on the UI, this class provides general support for dealing with
 * repeating UI elements.
 * @version 1.1
 */
public abstract class Repeater extends PageElement {
	private static final long serialVersionUID = -6043887157243002172L;
	/**
     * The name of the Type being repeated. The type must be recognizable by {@link com.ail.core.CoreProxy#newType(String) Core.newType(String)}.
     * The repeater will create instances of this type in response to the 'add' link being selected. If the {@link #isAddAndDeleteEnabled() addDeleteEnabled}
     * property is false, this property may be null as it will never be referred to.
	 */
    protected String type;

    /**
     * Set to true if the user should be able to add and remove elements from the repeater.
     */
    protected boolean addAndDeleteEnabled=true;

    /**
     * Minimum number of repeated items. If {@link #isAddAndDeleteEnabled() addAndDeleteEnabled} is true, the user
     * will be prevented from removing items from the list if removal would take the could below this number. Also, note
     * that the default value of 0 means that users can remove all items.
     */
    protected int minRows=0;

    /**
     * The maximum number of items that can appear in the list. The default value of -1 means there is no limit.
     */
    protected int maxRows=-1;   // -1 means 'no limit'

    /**
     * List of repeated fields. In a {@link RowScroller} this defines the columns and what they are bound to; in
     * a {@link SectionScroller} it defines the items listed in each section.
     */
    protected List<AttributeField> item=null;

    /**
     * The title of each repeated element within the repeater.
     */
    private String repeatedTitle = null;
    /**
     * Default constructor
     */
    public Repeater() {
        super();
        item=new OrderedList<>();
    }

	/**
	 * @return the repeatedTitle
     * @since 1.1
	 */
	public String getRepeatedTitle() {
		return repeatedTitle;
	}

	/**
	 * @param repeatedTitle the repeatedTitle to set
     * @since 1.1
	 */
	public void setRepeatedTitle(String repeatedTitle) {
		this.repeatedTitle = repeatedTitle;
	}

    /**
     * Get the repeated title with all variable references expanded. References are expanded with
     * reference to the models passed in. Relative xpaths (i.e. those starting ./) are
     * expanded with respect to <i>local</i>, all others are expanded with respect to
     * the current quotation (from {@link PageFlowContext}).
     * @param root Model to expand references with respect to.
     * @param local Model to expand local references (xpaths starting ./) with respect to.
     * @return Title with embedded references expanded
     * @since 1.1
     */
	public String formattedRepeatedTitle(Type local) {
		return i18n(expand(getRepeatedTitle(), PageFlowContext.getPolicy(), local));
	}

	/**
     * The maximum number of items that can appear in the list. The default value of -1 means there is no limit.
     * @return Number of records allowed in the repeater.
     */
    public int getMaxRows() {
        return maxRows;
    }

    /**
     * @see #getMaxRows()
     * @param maxRows Number of records allowed in the repeater.
     */
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }

    /**
     * Minimum number of repeated items. If {@link #isAddAndDeleteEnabled() addAndDeleteEnabled} is true, the user
     * will be prevented from removing items from the list if removal would take the could below this number. Also, note
     * that the default value of 0 means that users can remove all items.
     * @return Minimum number of records allowed in the list.
     */
    public int getMinRows() {
        return minRows;
    }

    /**
     * @see #getMinRows()
     * @param minRows Minimum number of records allowed in the list.
     */
    public void setMinRows(int minRows) {
        this.minRows = minRows;
    }

    /**
     * The name of the Type being repeated. The type must be recognizable by {@link com.ail.core.CoreProxy#newType(String) Core.newType(String)}.
     * The repeater will create instances of this type in response to the 'add' link being selected. If the {@link #isAddAndDeleteEnabled() addDeleteEnabled}
     * property is false, this property may be null as it will never be referred to.
     * @return Name of type, or null.
     */
    public String getType() {
        return type;
    }

    /**
     * @see #getType()
     * @param type Name of type, or null.
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * List of repeated fields. In a {@link RowScroller} this defines the columns and what they are bound to; in
     * a {@link SectionScroller} it defines the items listed in each section.
     * @return List of repeating fields.
     */
    public List<AttributeField> getItem() {
        return item;
    }

    /**
     * @see #getId()
     * @param item List of repeating fields.
     */
    public void setItem(List<AttributeField> item) {
        this.item = item;
    }

    /**
     * Alternate method to access the {@link #getItem()} List as a Collection.
     * @return Collection of repeating fields.
     */
    public Collection<AttributeField> getItemCollection() {
        return item;
    }

    /**
     * @see #getItemCollection()
     * @param item Collection of repeating fields.
     */
    public void setItemCollection(Collection<AttributeField> item) {
        this.item=new OrderedList<>(item);
    }

    /**
     * Determine if the user is allow to add or remove elements from the data.
     * @return True if data can be added or removed, false otherwise.
     */
    public boolean isAddAndDeleteEnabled() {
        return addAndDeleteEnabled;
    }

    /**
     * Enable or disable permission to add and remove data from the table.
     * @param addAndDeleteEnabled
     */
    public void setAddAndDeleteEnabled(boolean addAndDeleteEnabled) {
        this.addAndDeleteEnabled = addAndDeleteEnabled;
    }

    @Override
    public Type processActions(Type model) throws BaseException {
        if (isAddAndDeleteEnabled()) {
            Properties opParams = PageFlowContext.getOperationParameters();
            String op = PageFlowContext.getRequestedOperation();
            String opId = opParams.getProperty("id");

            if ("add".equals(op) && (id == null || id.equals(opId))) {
                // Create the object we'll be adding.
                Type newType = PageFlowContext.getCoreProxy().newType(type, Type.class);

                // Get the target collection
                String targetAssetXpath = XPath.xpath(getBinding());
                if (targetAssetXpath.contains("/")) {
                    targetAssetXpath = targetAssetXpath.substring(0, targetAssetXpath.lastIndexOf('/'));
                    targetAssetXpath = (targetAssetXpath.length()==0) ? "/" : targetAssetXpath;
                }

                switch (newType.getClass().getSimpleName()) {
                case "Asset":
                    model.xpathGet(targetAssetXpath, HasAssets.class).addAsset((Asset) newType);
                    break;
                case "Section":
                    model.xpathGet(targetAssetXpath, Policy.class).addSection((Section) newType);
                    break;
                }

                PageFlowContext.flagActionAsProcessed();
            } else if ("delete".equals(op) && (id == null || id.equals(opId))) {
                // The 'row' param indicates the row number who's trash can has been clicked.
                int row = Integer.parseInt(opParams.getProperty("row"));

                // Loop through the collection the scroller is bound to and find the object to
                // be removed.
                Iterator<?> it = model.xpathIterate(getBinding());
                for (; row > 0; row--, it.next());
                Type delType = (Type) it.next();

                // Get the target collection
                String targetAssetXpath = XPath.xpath(getBinding());
                if (targetAssetXpath.contains("/")) {
                    targetAssetXpath = targetAssetXpath.substring(0, targetAssetXpath.lastIndexOf('/'));
                    targetAssetXpath = (targetAssetXpath.length()==0) ? "/" : targetAssetXpath;
                }

                switch (delType.getClass().getSimpleName()) {
                case "Asset":
                    model.xpathGet(targetAssetXpath, HasAssets.class).removeAsset((Asset)delType);
                    break;
                case "Section":
                    model.xpathGet(targetAssetXpath, Policy.class).removeSection((Section)delType);
                    break;
                }

                PageFlowContext.flagActionAsProcessed();
            }
        }

        return model;
    }

    @Override
    public Type applyRequestValues(Type model) {
        int rowCount=0;

        // Loop through the rows
        for(Iterator<Type> it=model.xpathIterate(getBinding(), Type.class) ; it.hasNext() ; ) {
            Type subModel=it.next();

            // loop through the columns
            for(AttributeField a: item) {
                a.applyRequestValues(subModel, getBinding()+"["+rowCount+"]");
            }

            rowCount++;
        }

        handleAddByNewType(model);

        return model;
    }

    private void handleAddByNewType(Type model) {
        // Check for new values added
        RequestWrapper request = PageFlowContext.getRequestWrapper();
        String newType = request.getParameter("newType");
        if (newType == null || newType.equals(type) == false) {
            return;
        }

        // Create the object we'll be adding.
        Type newInstance = (Type) PageFlowContext.getCoreProxy().newType(type, Type.class);

        // Get the target collection
        String targetXpath = XPath.xpath(getBinding());

        if (targetXpath.contains("/")) {
            targetXpath = targetXpath.substring(0, targetXpath.lastIndexOf('/'));
            targetXpath = (targetXpath.length()==0) ? "/" : targetXpath;
        }
        // Loop through properties and try to find request values
        for (Attribute att : newInstance.getAttribute()) {
            String paramValue = request.getParameter(encodeId(att.getId()));

            if (paramValue != null && paramValue.length() > 0) {
                att.setValue(paramValue);
            }
        }

        if (newInstance instanceof Asset) {
            model.xpathGet(targetXpath, HasAssets.class).addAsset((Asset)newInstance);
        }
        else if (newInstance instanceof Section) {
            ((Policy)model).addSection((Section)newInstance);
        }

        PageFlowContext.flagActionAsProcessed();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean processValidations(Type model) {
        boolean error=false;

        // Loop through the rows
        for(Iterator<Type> it=model.xpathIterate(getBinding()) ; it.hasNext() ; ) {
            Type subModel=it.next();

            // loop through the columns
            for(AttributeField a: item) {
                error|=a.processValidations(subModel);
            }
        }

        return error;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Type renderPageHeader(Type model) throws IllegalStateException, IOException {
        Iterator<Type> it=model.xpathIterate(getBinding());

        if (it.hasNext()) {
            Type t=it.next();

            for(AttributeField a: item) {
                a.renderPageLevelResponse(t, getBinding()+"[0]");
            }
        }

        return model;
    }

    @Override
    public void applyElementId(String basedId) {
    	int idx=0;
    	for(PageElement e: item) {
    		e.applyElementId(basedId+ID_SEPARATOR+(idx++));
    	}
    	super.applyElementId(basedId);
   	}
}

