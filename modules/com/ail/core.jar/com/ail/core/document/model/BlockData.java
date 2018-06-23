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

/**
 * An instance of BlockData defines the data that should appear in some area of
 * the rendered document.
 */
public class BlockData extends ItemContainer {
    private Placement placement = Placement.BODY;
    private Applicability applicability = Applicability.ALL;
    private String watermark;
    private boolean border = false;

    /**
     * Return an indicator detailing which whether this block has a border.
     * 
     * @return the {@linkplain #border}
     */
    public boolean isBorder() {
        return this.border;
    }

    /**
     * @see #isBorder()
     * @param assign
     *            _border to {@linkplain #border}
     */
    public void setBorder(boolean _border) {
        this.border = _border;
    }

    /**
     * @see #isBorder()
     * @param assign
     *            _border to {@linkplain #border}
     */
    public String getBorderAsString() {
        return Boolean.toString(this.border).toLowerCase();
    }

    /**
     * Return an indicator detailing which where this block is applicable
     * (should be shown) in the document.
     * 
     * @return Applicability indicator.
     */
    public Applicability getApplicability() {
        return applicability;
    }

    /**
     * @see #getApplicability()
     * @param applicability
     */
    public void setApplicability(Applicability applicability) {
        this.applicability = applicability;
    }

    /**
     * @see #getApplicability()
     * @param applicability
     */
    public void setApplicabilityAsString(String applicability) {
        this.applicability = Applicability.forName(applicability.toUpperCase());
    }

    /**
     * @see #getApplicability()
     * @param applicability
     */
    public String getApplicabilityAsString() {
        return applicability.getName().toLowerCase();
    }

    /**
     * Get an indicator detailing where this block should be included on the
     * page.
     * 
     * @return Placement indicator.
     */
    public Placement getPlacement() {
        return placement;
    }

    /**
     * @see #getPlacement()
     * @param applicability
     */
    public String getPlacementAsString() {
        return placement.getName().toLowerCase();
    }

    /**
     * @see #getPlacement()
     * @param applicability
     */
    public void setPlacementAsString(String position) {
        this.placement = Placement.forName(position.toUpperCase());
    }

    /**
     * @see #getPlacement()
     * @param applicability
     */
    public void setPlacement(Placement position) {
        this.placement = position;
    }

    @Override
    public void render(RenderContext context) {
        if (conditionIsMet(context.getModel())) {

            context.getOutput().printf("<block%s%s%s%s placement=\"%s\" applicability=\"%s\" border=\"%s\">", idAsAttribute(), titleAsAttribute(), styleClassAsAttribute(), orderAsAttribute(),
                    getPlacementAsString(), getApplicabilityAsString(), getBorderAsString());
            super.render(context);
            context.getOutput().println("</block>");
        }
    }

    /**
     * If the block has a watermark (image) this property holds it's URL.
     * 
     * @return watermark URL
     */
    public String getWatermark() {
        return watermark;
    }

    /**
     * @see #getWatermark()
     * @param watermark
     */
    public void setWatermark(String watermark) {
        this.watermark = watermark;
    }
}
