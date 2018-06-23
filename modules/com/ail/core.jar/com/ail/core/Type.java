/* Copyright Applied Industrial Logic Limited 2002. All rights reserved. */
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
package com.ail.core;

import static javax.persistence.GenerationType.TABLE;
import static javax.persistence.InheritanceType.SINGLE_TABLE;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Inheritance;
import javax.persistence.MappedSuperclass;
import javax.persistence.Transient;
import javax.persistence.Version;

import org.apache.commons.jxpath.JXPathContext;
import org.apache.commons.jxpath.JXPathException;
import org.hibernate.CallbackException;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Parameter;
import org.hibernate.envers.Audited;

import com.ail.core.data.XPath;
import com.ail.core.persistence.LifeCycleService.LifeCycleCommand;
import com.ail.core.product.HasProduct;
import com.ail.core.product.Product;

/**
 * <i>Type</i> is the base of all 'type model' classes that are part of the domain
 * model. All model classes must either extend this class, or
 * another that itself extends this class..
 */
@MappedSuperclass
@Inheritance(strategy = SINGLE_TABLE)
public abstract class Type implements Serializable, Cloneable, SupportsXpath, HasReferenceNumber {
    static final long serialVersionUID = -7687502065734633603L;
    public static final long NOT_PERSISTED = -1L;

    @Id
    @Audited
    @GeneratedValue(generator = "table", strategy = TABLE)
    @Column(name = "UID")
    @GenericGenerator(name = "table", strategy = "enhanced-table", parameters = {
            @Parameter(name = "table_name", value = "sgeSequenceGenerator")
    })
    private long systemId=NOT_PERSISTED;

    @Column(unique = true, nullable=false)
    @Audited
    private String externalSystemId=null;

    @Version
    private long serialVersion;

    @Column(columnDefinition = "BIT")
    private boolean lock=false;

    @Audited
    private String foreignSystemId=null;

    @Audited
    @org.hibernate.annotations.Type(type="com.ail.core.persistence.hibernate.XMLBlob", parameters = @Parameter(name = "type", value = "java.util.ArrayList"))
    private List<Attribute> attribute = new ArrayList<>();

    @Audited
    private Long createdBy;

    @Audited
    private Date createdDate;

    @Audited
    private Long updatedBy;

    @Audited
    private Date updatedDate;

    @Transient
    public transient JXPathContext jXPathContext;

    public boolean getLock() {
        return lock;
    }

    public void setLock(boolean lock) {
		this.lock=lock;
    }

    public long getSerialVersion() {
        return serialVersion;
    }

    public void setSerialVersion(long serialVersion) {
        this.serialVersion=serialVersion;
    }

    public long getSystemId() {
        return systemId;
    }

    public void setSystemId(long systemId) {
        this.systemId=systemId;
    }

    /**
     * Return true if this object has been persisted.
     * @return true if persisted, false otherwise.
     */
    public boolean isPersisted() {
        return systemId!=NOT_PERSISTED;
    }

    /**
     * Disassociate this object with it's persisted counterpart.
     */
    public void markAsNotPersisted() {
        systemId=NOT_PERSISTED;
        externalSystemId=null;
        serialVersion=0;
    }


	@Override
    public JXPathContext fetchJXPathContext() {
		if (jXPathContext==null) {
            jXPathContext=JXPathContext.newContext(this);
            jXPathContext.setFunctions(TypeXPathFunctionRegister.getInstance().getFunctionLibrary());
		}

		return jXPathContext;
	}

    /**
     * Execute the given XPath expression on <i>this</i> and return the single result.
     * @param xpath Expression to evaluate
     * @exception TypeXPathException If evaluation of the expression fails.
     * @return Result of evaluation.
	 */
    @Override
    public Object xpathGet(String xpath) {
        if (xpath==null) {
            return null;
        }

        try {
            return fetchJXPathContext().getValue(XPath.xpath(xpath));
        } catch (JXPathException e) {
            throw new TypeXPathException(e);
        }
    }

    /**
     * Execute the given XPath expression on <i>this</i> and return the single
     * result; or, if the xpath expression fails to evaluate for any reason,
     * return <code>alternative</code>
     *
     * @param xpath
     *            Expression to evaluate
     * @param alternative
     *            Returned if xpath is null or fails to evaluate.
     * @return Result of evaluation; or, alternative.
     */
    @Override
    public Object xpathGet(String xpath, Object alternative) {
        if (xpath==null) {
            return alternative;
        }

        try {
            return fetchJXPathContext().getValue(XPath.xpath(xpath));
        } catch (Throwable e) {
            return alternative;
        }
    }

    /**
     * Evaluate the given xpath expression on <i>this</i> and return the result as an instance
     * of the class <i>clazz</i>.
     * @param xpath Expression to evaluate
     * @param clazz Class to return an instance of
     * @return Result.
     */
    @Override
    public <T extends Object> T xpathGet(String xpath, Class<T> clazz) {
        return xpathGet(xpath, clazz, true);
    }

    /**
     * Evaluate the given xpath expression on <i>this</i> and return the result as an instance
     * of the class <i>clazz</i>.
     * @param xpath Expression to evaluate
     * @param clazz Class to return an instance of
     * @param dictionaryLookup whether to check the dictionary or not
     * @return Result.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Object> T xpathGet(String xpath, Class<T> clazz, boolean dictionaryLookup) {
        if (xpath==null) {
            return null;
        }

        try {
            if (dictionaryLookup) {
                return (T)fetchJXPathContext().getValue(XPath.xpath(xpath), clazz);
            } else {
                return (T)fetchJXPathContext().getValue(xpath, clazz);
            }
        } catch (JXPathException e) {
            throw new TypeXPathException(e);
        }
    }

    /**
     * Evaluate the given xpath expression on <i>this</i> and return the result as an instance
     * of the class <i>clazz</i>.
     * @param xpath Expression to evaluate
     * @param alternative Returned if xpath is null or fails to evaluate.
     * @param clazz Class to return an instance of
     * @return Result.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Object> T xpathGet(String xpath, T alternative, Class<T> clazz) {
        if (xpath==null) {
            return alternative;
        }

        try {
            return (T)fetchJXPathContext().getValue(XPath.xpath(xpath), clazz);
        } catch (JXPathException e) {
            return alternative;
        }
    }

    /**
     * Execute the given XPath expression on <i>this</i>. The expectation is that the XPath expression
     * evaluates to more than one node. This method returns the matching nodes as an iteration.
     * @param xpath Expression to evaluate
     * @exception TypeXPathException If evaluation of the expression fails.
     * @return Result of evaluation.
     */
    @Override
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public Iterator xpathIterate(String xpath) {
        if (xpath==null) {
            return null;
        }

        try {
            return (Iterator)fetchJXPathContext().iterate(XPath.xpath(xpath));
        } catch (JXPathException e) {
            throw new TypeXPathException(e);
        }
    }

    /**
     * Execute the given xpath expression on <i>this</i>. The expectation is that the xpath expression
     * evaluates to more than one node. This method returns the matching nodes as an iteration.
     * @param xpath Expression to evaluate
     * @exception TypeXPathException If evaluation of the expression fails.
     * @return Result of evaluation.
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T extends Object> Iterator<T> xpathIterate(String xpath, Class<T> clazz) {
        if (xpath==null) {
            return null;
        }

        try {
            return (Iterator<T>)fetchJXPathContext().iterate(XPath.xpath(xpath));
        } catch (JXPathException e) {
            throw new TypeXPathException(e);
        }
    }

    /**
     * Execute the given xpath expression on <i>this</i>. The expectation is that the xpath expression
     * evaluates to more than one node. This method returns the matching nodes as an iteration.
     * @param xpath Expression to evaluate
     * @param alternative Value to be returned if the evaluation of <code>xpath</code> fails.
     * @param clazz Return instance of this class in an {@link Iterator}
     * @return Result of evaluation.
     */
    @SuppressWarnings("unchecked")
    @Override
    public <T extends Object> Iterator<T> xpathIterate(String xpath, Iterator<T> alternative, Class<T> clazz) {
        if (xpath==null) {
            return alternative;
        }

        try {
            return (Iterator<T>)fetchJXPathContext().iterate(XPath.xpath(xpath));
        } catch (JXPathException e) {
            return alternative;
        }
    }

    /**
     * Set the value of a property within <i>this</i> identified by an xpath expression to a the value <i>obj</i>.
     * @param xpath Expression identifying the property to set.
     * @param obj value to set the property to.
     */
    @Override
    public void xpathSet(String xpath, Object obj) {
        try {
            fetchJXPathContext().setValue(XPath.xpath(xpath), obj);
        } catch (JXPathException e) {
            throw new TypeXPathException(e);
        }
    }

    /**
     * Clone this object. This clone method is used by all Type subclasses to handle deep cloning.
     * For the factory to operate correctly it is essential that Types can be deep cloned, as it
     * hangs onto prototyped instances by name and simply clones them when a request is made for
     * an instance of a named type.
     * @throws CloneNotSupportedException If the type cannot be deep cloned.
     */
    @Override
    public Object clone() throws CloneNotSupportedException {
	    return new TypeCloner(this, super.clone()).invoke();
	}

    /**
     * Merge data from a specified type into <i>this</i>. This method does not demand that the donor and 'this' (the subject) have
     * to be of the same or even compatible types; we will simply copy whatever we can based on the property names and types.
     * @param donor Take values from here into <i>this</i>
	 */
    public void mergeWithDataFrom(Type donor, Core core) {
        new TypeMerger(donor, this, core).invoke();
    }

	/**
	 * Get the collection of instances of Attribute associated with this object.
	 * @return attribute A collection of instances of Excess
	 * @see #setAttribute
	 */
	public List<Attribute> getAttribute() {
	    if (attribute==null) {
	        attribute = new ArrayList<>();
	    }

	    return attribute;
	}

	/**
	 * Set the collection of instances of Attribute associated with this object.
	 * @param attribute A collection of instances of Excess
	 * @see #getAttribute
	 */
	public void setAttribute(List<Attribute> attribute) {
		this.attribute = attribute;
	}

	/**
	 * Remove the specified instance of Attribute from the list.
	 * @param attribute Instance to be removed
	 */
	public void removeAttribute(Attribute attribute) {
		getAttribute().remove(attribute);
	}

	/**
	 * Add an instance of Attribute to the list associated with this object.
	 * @param attribute Instance to add to list
	 */
	public void addAttribute(Attribute attribute) {
		getAttribute().add(attribute);
	}

    /**
     * The foreign system id is provided in order to make mapping to/from external system somewhat easier. The
     * expectation is that information extracted from foreign systems will have IDs of some kind which those
     * system uses to identify the data.
     * @return The ID
     */
    public String getForeignSystemId() {
        return foreignSystemId;
    }

    /**
     * @see #getForeignSystemId()
     * @param foreignSystemId
     */
    public void setForeignSystemId(String foreignSystemId) {
        this.foreignSystemId = foreignSystemId;
    }

    /**
     * The date when this record was first persisted.
     */
    public Date getCreatedDate() {
        return createdDate;
    }

    /**
     * The date when this record was last updated.
     */
    public Date getUpdatedDate() {
        return updatedDate;
    }

    public Long getCreatedBy() {
        return createdBy;
    }

    public Long getUpdatedBy() {
        return updatedBy;
    }

    public String getExternalSystemId() {
        return externalSystemId;
    }

    public void setExternalSystemId(String externalSystemId) {
        this.externalSystemId = externalSystemId;
    }

    @Override
    public String getReferenceNumber() {
        return Long.toString(getSystemId());
    }

    protected boolean onLifeCycle(String commandName) throws CallbackException {
        try {
            String productTypeId = (this instanceof HasProduct) ? ((HasProduct)this).getProductTypeId() : Product.BASE_PRODUCT_TYPE_ID;

            CoreProxy core = new CoreProxy(Functions.productNameToConfigurationNamespace(productTypeId));

            LifeCycleCommand osc = core.newCommand(commandName, LifeCycleCommand.class);
            osc.setObjectArgRet(this);
            osc.invoke();
        } catch (BaseException e) {
            throw new CallbackException(e);
        }
        return false;
    }

    protected int typeHashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
        result = prime * result + ((createdDate == null) ? 0 : createdDate.hashCode());
        result = prime * result + ((externalSystemId == null) ? 0 : externalSystemId.hashCode());
        result = prime * result + ((foreignSystemId == null) ? 0 : foreignSystemId.hashCode());
        result = prime * result + (lock ? 1231 : 1237);
        result = prime * result + (int) (serialVersion ^ (serialVersion >>> 32));
        result = prime * result + (int) (systemId ^ (systemId >>> 32));
        result = prime * result + ((updatedDate == null) ? 0 : updatedDate.hashCode());
        return result;
    }

    protected boolean typeEquals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Type other = (Type) obj;
        if (attribute == null) {
            if (other.attribute != null)
                return false;
        } else if (!attribute.equals(other.attribute))
            return false;
        if (createdDate == null) {
            if (other.createdDate != null)
                return false;
        } else if (!createdDate.equals(other.createdDate))
            return false;
        if (externalSystemId == null) {
            if (other.externalSystemId != null)
                return false;
        } else if (!externalSystemId.equals(other.externalSystemId))
            return false;
        if (foreignSystemId == null) {
            if (other.foreignSystemId != null)
                return false;
        } else if (!foreignSystemId.equals(other.foreignSystemId))
            return false;
        if (lock != other.lock)
            return false;
        if (serialVersion != other.serialVersion)
            return false;
        if (systemId != other.systemId)
            return false;
        if (updatedDate == null) {
            if (other.updatedDate != null)
                return false;
        } else if (!updatedDate.equals(other.updatedDate))
            return false;
        return true;
    }
}
