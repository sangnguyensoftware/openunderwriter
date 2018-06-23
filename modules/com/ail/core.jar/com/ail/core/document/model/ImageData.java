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

import javax.xml.bind.DatatypeConverter;

import com.ail.annotation.TypeDefinition;

/**
 * Node of the document structure object graph.
 */
@TypeDefinition
public class ImageData extends ItemData {
    private String binding;

    private String imageFilePath;
    /**
     * Image output dimension
     */
    private int width = 0;
    /**
     * Image output dimension
     */
    private int height = 0;

    public String getBinding() {
        return binding;
    }

    public void setBinding(String binding) {
        this.binding = binding;
    }

    public String getImageFilePath() {
        return imageFilePath;
    }

    public void setImageFilePath(String imageFilePath) {
        this.imageFilePath = "src='" + imageFilePath + "' ";
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    @Override
    public void render(RenderContext context) {
        if (conditionIsMet(context.getModel())) {
            context.getOutput().printf("<itemData%s%s%s%s", idAsAttribute(), titleAsAttribute(), styleClassAsAttribute(), orderAsAttribute());
            if (getImageFilePath() == null || getImageFilePath().length() == 0) {
                context.getOutput().printf("%s", this.buildImageArrayFromBinding(context));
            } else {
                context.getOutput().printf("%s", this.buildImageFilePathAsAttribute());
            }
            context.getOutput().printf("%s%s></itemData>", this.buildHeightAttribute(), this.buildWidthAttribute());
        }
    }

    private String buildImageFilePathAsAttribute() {
        return (imageFilePath != null) ? " source=\"" + imageFilePath + "\"" : "";
    }

    private String buildImageArrayFromBinding(RenderContext context) {
        byte[] image = (byte[]) context.getModel().xpathGet(getBinding() + "/document");
        String mime = (String) context.getModel().xpathGet(getBinding() + "/mimeType");

        // <fo:external-graphic
        // src="url('data:image/jpeg;base64,/9j/4AAQSkZJRgABAQAA....')"/>

        String output = "url('data:";
        output += mime;
        output += ";base64,";
        output += DatatypeConverter.printBase64Binary(image) + "') ";

        return (image != null) ? " source=\"" + output + "\"" : "";
    }

    private String buildWidthAttribute() {
        return (width != 0) ? " c-width==\"" + width + "px\"" : "";
    }

    private String buildHeightAttribute() {
        return (height != 0) ? " c-height==\"" + height + "px\"" : "";
    }
}
