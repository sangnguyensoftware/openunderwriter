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

import com.ail.core.Type;

/**
 * <p>
 * Page element to display a summary of a referral. The ReferralSummary element
 * is designed to render as a complete page detailing a referral which is
 * presented to the user following premium calculation if that calculation
 * results in a referral or a decline (as opposed to a quotation).
 * </p>
 * <p>
 * <img src="doc-files/ReferralSummary.png"/>
 * </p>
 * <p>
 * The element is made up of three sections:
 * <ul>
 * <li><b>referral notification</b> The content rendered in this section is read
 * from the URL indicated by the {@link #getReferralNotificationUrl()
 * getReferralNotificationUrl} property. The content is parsed using an instance
 * of {@link ParsedUrlContent} allowing dynamic references to fields within the
 * quotation to be included. In the above example, the quote number and contact
 * telephone number are examples of this.</li>
 * <li><b>navigation section</b> provides options for how the user would like to
 * proceed. Two options are presented:
 * <ul>
 * <li>Requote creates a new quote containing all the data provided for this
 * quote and takes the user back to the page indicated by the
 * {@link #getRequoteDestinationPageId() requoteDestinationPageId} property;</li>
 * <li>Quit ends the user's session which has the effect of taking them back to
 * the product home page.</li>
 * </ul>
 * <li><b>requirement summary</b> Few restrictions are placed on what
 * PageElements can be included here, but the intention is that
 * {@link AnswerSection AnswerSections} and {@link AnswerScroller
 * AnswerScrollers} will be used to repeat key information collected during the
 * quotation process. Any page elements added to the {@link #getPageElement()
 * pageElement} list will be rendered in this area.</li>
 * 
 * @see QuotationSummarys
 * @see ParsedUrlContent
 * @see AnswerSection
 * @see AnswerScroller
 */
public class ReferralSummary extends PageContainer {
    private static final long serialVersionUID = -4810599045554021748L;
    private NavigationSection navigationSection;
    private ParsedUrlContent referralNotification;
    private String referralNotificationUrl;
    private String requoteDestinationPageId;
    private String saveDestinationPageId;

    public ReferralSummary() {
        super();
    }

    public String getRequoteDestinationPageId() {
        return requoteDestinationPageId;
    }

    public void setRequoteDestinationPageId(String requoteDestinationPageId) {
        this.requoteDestinationPageId = requoteDestinationPageId;
    }

    public String getSaveDestinationPageId() {
        return saveDestinationPageId;
    }

    public void setSaveDestinationPageId(String saveDestinationPageId) {
        this.saveDestinationPageId = saveDestinationPageId;
    }

    public String getReferralNotificationUrl() {
        return referralNotificationUrl;
    }

    public void setReferralNotificationUrl(String referralNotificationUrl) {
        this.referralNotificationUrl = referralNotificationUrl;
    }

    @Override
    public Type applyRequestValues(Type model) {
        return model;
    }

    @Override
    public boolean processValidations(Type model) {
        return false;
    }

    @Override
    public Type renderResponse(Type model) throws IllegalStateException, IOException {
        return executeTemplateCommand("ReferralSummary", model);
    }

    public NavigationSection getNavigationSection() {
        if (navigationSection == null) {
            navigationSection = new NavigationSection();
            navigationSection.setId(this.id + ".nav");

            // The Save button is optional - only add it if a destination was
            // specified
            if (saveDestinationPageId != null && saveDestinationPageId.length() != 0) {
                SaveButtonAction save = new SaveButtonAction();
                save.setDestinationPageId(saveDestinationPageId);
                save.setId(this.id + ".save");
                navigationSection.getPageElement().add(save);
            }

            RequoteButtonAction requote = new RequoteButtonAction();
            requote.setLabel("i18n_requote_button_label");
            requote.setDestinationPageId(requoteDestinationPageId);
            requote.setId(this.id + ".requote");
            navigationSection.getPageElement().add(requote);

            getPageElement().add(navigationSection);
        }

        return navigationSection;
    }

    public ParsedUrlContent getReferralNotificationSection() {
        if (referralNotification == null) {
            referralNotification = new ParsedUrlContent();
            referralNotification.setUrl(referralNotificationUrl);
        }

        return referralNotification;
    }

    @Override
    public void applyElementId(String basedId) {
        if (getNavigationSection() != null) {
            getNavigationSection().applyElementId(basedId + ID_SEPARATOR + "nav");
        }
        if (getReferralNotificationSection() != null) {
            getReferralNotificationSection().applyElementId(basedId + ID_SEPARATOR + "not");
        }
        super.applyElementId(basedId);
    }
}
