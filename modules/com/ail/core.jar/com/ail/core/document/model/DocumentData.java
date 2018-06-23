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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.ail.annotation.TypeDefinition;

/**
 */
@TypeDefinition
public class DocumentData extends ItemData {
    private List<ChapterData> chapterData = new ArrayList<ChapterData>();
    private String watermark;
    private List<HeaderData> headerData = new ArrayList<HeaderData>();
    private List<FooterData> footerData = new ArrayList<FooterData>();

    @Override
    public void render(RenderContext context) {
        if (conditionIsMet(context.getModel())) {  // This should never be the case.

            context.getOutput().print("<documentData>");

            if (watermark != null) {
                context.getOutput().printf("<watermark>%s</watermark>", getWatermark());
            }

            Collections.sort(headerData);

            for (HeaderData s : headerData) {
                s.render(context);
            }

            Collections.sort(footerData);

            for (FooterData s : footerData) {
                s.render(context);
            }

            Collections.sort(chapterData);

            for (ChapterData s : chapterData) {
                s.render(context);
            }

            context.getOutput().print("</documentData>");
        }
    }

    public List<ChapterData> getChapterData() {
        return chapterData;
    }

    public void setChapterData(List<ChapterData> chapterData) {
        this.chapterData = chapterData;
    }

    public void setWatermark(String watermark) {
        this.watermark = watermark;
    }

    public void setHeaderData(List<HeaderData> headerData) {
        this.headerData = headerData;
    }

    public void setFooterData(List<FooterData> footerData) {
        this.footerData = footerData;
    }

    public String getWatermark() {
        return watermark;
    }

    public List<HeaderData> getHeaderData() {
        return headerData;
    }

    public List<FooterData> getFooterData() {
        return footerData;
    }
}
