/* Copyright Applied Industrial Logic Limited 2006. All rights reserved. */
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
package com.ail.core.document.model;

import static com.ail.core.language.I18N.i18n;

import com.ail.core.Identified;
import com.ail.core.Type;
/**
 * Node of the document structure object graph.
 */
public abstract class ItemData extends Type implements Identified, Comparable<ItemData> {
    private String id;
    private String title;

    private Long order;
    private String value;
    private String style = "string";

    /**
     * An optional XPath expression. If defined the expression is evaluated against the quotation
     * immediately before the action is to be executed. The action will only be executed if the expression
     * returns true (i.e. <code>(Boolean)model.xpathGet(condition)==true</code>
     */
    private String condition;

    public String getStyle() {
        return style;
    }

    public void setStyle(String format) {
        this.style = format;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    protected boolean conditionIsMet(Type model) {
        return condition==null || (Boolean)model.xpathGet(condition)==true;
    }

    /**
     * The <i>order</i> of this item with respect to other items owned by the
     * same parent. Note: lower order items appear first.
     *
     * @return container's order
     */
    public Long getOrder() {
        return order;
    }

    /**
     * @see #getOrder()
     * @param order
     */
    public void setOrder(Long order) {
        this.order = order;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    protected String idAsAttribute() {
        return (id != null) ? " id=\"" + id + "\"" : "";
    }

    protected String titleAsAttribute() {
        return (title != null) ? " title=\"" + i18n(title) + "\"" : "";
    }

    protected String orderAsAttribute() {
        return (order != null) ? " order=\"" + order + "\"" : "";
    }

    protected String styleClassAsAttribute() {
        return (style != null) ? " class=\"" + style + "\"" : "";
    }

    @Override
    public boolean compareById(Identified that) {
        if (that instanceof ItemData) {
            return (id != null && id.endsWith(((ItemData) that).getId()));
        } else {
            return false;
        }
    }

    @Override
    public int compareTo(ItemData that) {
        if (this.order == null && that.order == null) {
            return 0;
        } else if (this.order != null && that.order == null) {
            return 1;
        } else if (this.order == null && that.order != null) {
            return -1;
        } else {
            return (int) (this.order - that.order);
        }
    }

    public abstract void render(RenderContext context);
}
