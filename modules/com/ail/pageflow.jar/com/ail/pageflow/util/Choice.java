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
package com.ail.pageflow.util;

import static com.ail.core.language.I18N.i18n;

import java.util.ArrayList;
import java.util.List;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;

/**
 * Utility class which assist in the rendering of Choice types. The values made
 * available to the user in a choice may be hard wired into the
 * {@link com.ail.core.Attribute Attribute} or derived from a Choice Type.
 * Typically, a choice Type is used when the number of options becomes
 * unmanageably large. The Choice type also supports sub-choices, for example
 * vehicle Make and Model choices.
 */
@TypeDefinition
public class Choice extends Type {
	private static final long serialVersionUID = -7252168449721481890L;
	private List<Choice> choice = null;
	private String name;
	private int value=0;
	private String undefinedName="?";
    private transient String compiled;
    private transient String xmlCompiled;

	public Choice() {
		choice = new ArrayList<Choice>();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Choice> getChoice() {
		return choice;
	}

	public void setChoice(List<Choice> choice) {
		this.choice = choice;
	}

	/**
	 * Optional integer value to associate with this element. The value here is analogous to the
	 * integer values associated with option list defined in-line with an attribute's format. For
	 * example the following format defines values of -1, 1, 2 and 3 for the options.
	 * choice,options=-1#?|1#Increased power|2#Left hand drive conversion|3#Other
	 * @return value
	 */
	public int getValue() {
		return value;
	}

	/**
	 * @see #getValue()
	 * @param value
	 */
	public void setValue(int value) {
		this.value = value;
	}

	/**
	 * Define the option which should be considered as "undefined" if selected. This defaults
	 * to "?" if not specified. If the attribute associated with this choice has this value
	 * during validation it will be treated as though no option has been selected.
	 * @param undefinedName
	 */
	public void setUndefinedName(String undefinedName) {
		this.undefinedName = undefinedName;
	}

	public String getUndefinedName() {
		return undefinedName;
	}

	public String renderAsJavaScriptArray(String arrayName) {
		if (compiled == null) {
			StringBuffer buf = new StringBuffer();
			String name=null;
			int i = 1;

			buf.append("<script type='text/javascript'>");
			buf.append(arrayName).append("=new Array();");

			for (Choice m : choice) {
				name=i18n(m.getName()).replace("'", "\\'");
				buf.append(arrayName).append("[").append(i++).append("]=new Array('").append(name).append("'");
				for (Choice s : m.getChoice()) {
					name=i18n(s.getName()).replace("'", "\\'");
					buf.append(",'").append(name).append("'");
				}
				buf.append(");");
			}

			buf.append("</script>");

			compiled = buf.toString();
		}

		return compiled;
	}

	public String renderAsXmlArray(String arrayName) {
		if (xmlCompiled == null) {
			StringBuffer buf = new StringBuffer();
			String name;

			for (Choice m : choice) {
				name=i18n(m.getName());
				buf.append("<choices><label>").append(name).append("</label>").append("<value>").append(name).append("</value>");

				for (Choice s : m.getChoice()) {
					name=i18n(s.getName());
					buf.append("<item><label>").append(name).append("</label><value>").append(name).append("</value></item>");
				}
				buf.append("</choices>");
			}
			xmlCompiled = buf.toString();
		}

		return xmlCompiled;
	}
}
