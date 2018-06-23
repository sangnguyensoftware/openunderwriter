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
package com.ail.core.configure;

import java.util.ArrayList;
import java.util.Date;

import com.ail.core.VersionEffectiveDate;

/**
 * This class represents the top level of the composite. Configuration contains
 * a Types object, and any number of Parameter objects. It
 * also includes a version number - which indicates the version of the
 * properties held, and caching information which is used to detect stale
 * configuration details.
 */
public class Configuration extends Group {
    static final long serialVersionUID = -1645988206689900185L;
    /** Timeout in milliseconds. The config will be reloaded if it has been cached for longer than this. A setting of -1 Means "never reload". */
    private int timeout=-1;

    /** The name of another configuration which this one inherits from.*/
    private String parentNamespace=null;

    /** Configurations version number. */
    private String version=null;

    /** Who (which user) owns this configuration */
    private String who=null;

    /** The name of a service/component which manages this configuration */
    private String manager=null;

    /** Date at which the configuration was loaded from persistent storage */
    private Date loadedAt=null;

    /** The URL that this configuration was loaded from at reset time. This
     * will be a class resource URL for all the configurations reset from
     * <code>*DefaultConfig.xml</code> files in the package hierarchy; or
     * for configs from CMS it'll be the URL of the CMS file. */
    private String source=null;

    /** Date from which this configuration is valid.*/
    private VersionEffectiveDate validFrom=null;

    /** Date to which this configuration is valid.*/
    private VersionEffectiveDate validTo=null;

	/**
     * Default constructor.
     */
    public Configuration() {
    }

	/**
     * Set the Types associated with this configuration. There is only
     * ever one Types element in a configuration, and it is stored in
     * as a normal group in the Groups list. This method will either
     * replace an existing Types element with the one specified, of if
     * there isn't a Types element in the Configuration it will create
     * one.
     * @param types The Types to set
     */
    public void setTypes(Types types) {
		int idx;

        for(idx=getGroupCount()-1; idx>=0 ; idx--) {
            if (getGroup(idx) instanceof Types) {
                break;
            }
        }

		if (idx==-1) {
	        addGroup(types);
        }
        else {
			setGroup(idx, types);
        }
    }

    /**
     * Get the Types group associated with this configuration. There is only
     * ever one Types group in a configuration.
     * @return The types group, or null if not such group was defined.
     */
    public Types getTypes() {
        for(int idx=getGroupCount()-1; idx>=0 ; idx--) {
            if (getGroup(idx) instanceof Types) {
                return (Types)getGroup(idx);
            }
        }

        return null;
    }

	/**
     * Set the Builders associated with this configuration. There is only
     * ever one Builders element in a configuration, and it is stored in
     * as a normal group in the Groups list. This method will either
     * replace an existing Builders element with the one specified, or if
     * there isn't a Builders element in the Configuration it will create
     * one.
     * @param builders The Builders to set
     */
    public void setBuilders(Builders builders) {
		int idx;

        for(idx=getGroupCount()-1; idx>=0 ; idx--) {
            if (getGroup(idx) instanceof Builders) {
                break;
            }
        }

		if (idx==-1) {
	        addGroup(builders);
        }
        else {
			setGroup(idx, builders);
        }
    }

    /**
     * Get the Builders group associated with this configuration. There is only
     * ever one Builders group in a configuration.
     * @return The types group, or null if not such group was defined.
     */
    public Builders getBuilders() {
        for(int idx=getGroupCount()-1; idx>=0 ; idx--) {
            if (getGroup(idx) instanceof Builders) {
                return (Builders)getGroup(idx);
            }
        }

        return null;
    }

	/**
	 * Get just the Groups in this configuration - ignoring Types & Builders.
	 * Types and Builders are both sub-types of Group, so calling getGroups on
	 * a configuration will return a List of Groups including Builders and
	 * Types. This is not always appropriate. The List returned by this
	 * method will exclude the Builder and Type elements, and return only the
	 * other groups.
	 * @return A List of Groups
	 */
	public ArrayList<Group> getOnlyGroups() {
        // Castor work around: When castor reads from XML it'll use this method
        // to get the list to put <group> elements into. This anonymous class makes
        // sure that the groups added to this list also find their way into the
        // real destination (e.g. addGroup()). This ret list is only referenced by
        // castor.
		ArrayList<Group> ret=new ArrayList<Group>() {
        };

		for(int idx=getGroupCount()-1 ; idx>=0 ; idx--) {
			if (!(getGroup(idx) instanceof Builders || getGroup(idx) instanceof Types)) {
				ret.add(getGroup(idx));
			}
		}

		return ret;
	}

	/**
	 *
	 * @param groups
	 */
	public void setOnlyGroups(ArrayList<Group> groups) {
		for(Group g: groups) {
              getGroup().add(g);
        }
    }

    /**
     * Get this configuration's timeout. The timeout specifies the number
     * of millisecond that can be allowed to elapse between when the
     * configuration was read from persistence, and when it becomes stale and
     * needs to be read again. The configuration system uses this setting to
     * ensure that the configuration is kept up to date, but at the same time
     * allows the administrator some flexibility in reducing system load.
     * @return Timeout in milliseconds.
     */
	public int getTimeout() {
        return timeout;
    }

    /**
     * Set the configuration's timeout.
     * @see #getTimeout
     * @param timeout Number of milliseconds before configuration becomes stale.
     */
	public void setTimeout(int timeout) {
        this.timeout=timeout;
    }

    /**
     * Fetch the configuration's version. This version number generally
     * originates from the source file that held the version when it was
     * under version control.
     * @return The version description string.
     */
	public String getVersion() {
        return version;
    }

    /**
     * Set the configuration's version string.
     * @see #getVersion
     * @param version The version string.
     */
	public void setVersion(String version) {
        this.version=version;
    }

    /**
     * Get the configuration's loaded date. This date indicates when the
     * configuration was loaded by the configuration loader, and is used
     * in conjunction with <code>timeout</code> to determine when a reload
     * is needed.
     * @return The date that the configuration was loaded.
     */
	public Date getLoadedAt() {
		return loadedAt;
    }

    /**
     * Set the configuration's laoded at property.
     * @see #getLoadedAt
     * @param loadedAt The date the configuration was loaded.
     */
	public void setLoadedAt(Date loadedAt) {
        this.loadedAt=loadedAt;
    }

    /**
     * Get the date that the configuration is (was) valid from. Configuration
     * information is maintained in a version safe fashion. This allows the
     * core system to select the historically correct version of any given
     * configuration to match the user's versionEffectiveDate. The validFrom
     * and validTo properties define the date range between which the configuration
     * is valid.
     * @return The valid from date for this configuration.
     */
    public VersionEffectiveDate getValidFrom() {
        return validFrom;
    }

    /**
     * Set the valid from date for this configuration.
     * @see #getValidFrom
     * @param validFrom The valid from date for this configuration.
     */
    public void setValidFrom(VersionEffectiveDate validFrom) {
        this.validFrom=validFrom;
    }

    /**
     * Set the valid to date for this configuration.
     * @see #getValidFrom
     * @param validTo This configuration's valid to date.
     */
    public void setValidTo(VersionEffectiveDate validTo) {
        this.validTo=validTo;
    }

    /**
     * Get this configuration's valid to date.
     * @see #getValidFrom
     * @return This configuration valid to date.
     */
    public VersionEffectiveDate getValidTo() {
        return validTo;
    }

    /**
     * Set the name of the modifier of this configuration. Depending
     * on the system's configuration this may be a user or a system.
     * @param who The name of the modifier.
     */
    public void setWho(String who) {
        this.who=who;
    }

    /**
     * Get the configuration's modifier
     * @see #setWho(String)
     * @return The modifier
     */
    public String getWho() {
        return this.who;
    }

    /**
     * Find a type by its name.
     * @param typeName
     * @return The type, or null if it cannot be found.
     */
    public Type findType(String typeName) {
        return (Type)getTypes().findGroup(typeName);
    }

    /**
     * Find a builder by its name.
     * @param builderName
     * @return The builder, or null if it cannot be found.
     */
    public Builder findBuilder(String builderName) {
        return (Builder)getBuilders().findGroup(builderName);
    }

    /**
     * Configurations are generally used only by the owner - i.e. the class
     * responsible for managing the configuration is also the only class that
     * reads it. However, a given configuration may be shared by many classes
     * if each of the classes returns the same namespace when their getNamespace()
     * method is called. In this case, one of the classes must be elected as
     * the manager. This class will be used to load/save and otherwise manage
     * the configuration.<br/>
     * If handler is not set, the value of {@link #getName() name} is returned instead.
     * @return The name of this configuration's handling class.
     */
    public String getManager() {
        if (manager==null) {
            return getNamespace();
        }

        return manager;
    }

    /**
     * @see #getManager()
     * @param handler The name of this configuration's handling class.
     */
    public void setManager(String manager) {
        this.manager = manager;
    }

    /**
     * The name of another configuration that this one inherits from.
     * All configuration have com.ail.core.Core as their implicit base
     * parent - i.e. it sits at the top of the namespace hierarchy.
     * @return parent's namespace, or null if none is specified.
     */
    public String getParentNamespace() {
        return parentNamespace;
    }

    /**
     * The name of another configuration that this one inherits from.
     * All configuration have com.ail.core.Core as their implicit base
     * parent - i.e. it sits at the top of the namespace hierarchy.
     * @param parentNamespace parent's namespace, or null if none is specified.
     */
    public void setParentNamespace(String parentNamespace) {
        this.parentNamespace = parentNamespace;
    }

    /**
     * <p>Get the source of the configuration. The configuration loader may use
     * this property to store details of how the configuration was loaded, and
     * from where.</p>
     * <p>This will be a class resource URL for all the configurations reset from
     * <code>*DefaultConfig.xml</code> files in the package hierarchy; or
     * for configs from CMS it'll be the URL of the CMS file.</p>
     * @return A string describing the source from which configuration was loaded.
     */
    public String getSource() {
        return source;
    }

    /**
     * <p>Get the source of the configuration. The configuration loader may use
     * this property to store details of how the configuration was loaded, and
     * from where.</p>
     * <p>This will be a class resource URL for all the configurations reset from
     * <code>*DefaultConfig.xml</code> files in the package hierarchy; or
     * for configs from CMS it'll be the URL of the CMS file.</p>
     * @param source A string describing the source from which configuration was loaded.
     */
    public void setSource(String source) {
        this.source=source;
    }

}
