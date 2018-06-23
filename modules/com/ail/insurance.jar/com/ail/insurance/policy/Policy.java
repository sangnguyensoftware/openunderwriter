/* Copyright Applied Industrial Logic Limited 2002. All rights Reserved */
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

package com.ail.insurance.policy;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static com.ail.core.Functions.isEmpty;
import static com.ail.core.MimeType.APPLICATION_PDF;
import static com.ail.core.document.DocumentType.CERTIFICATE;
import static com.ail.core.document.DocumentType.ENDORSEMENT;
import static com.ail.core.document.DocumentType.INVOICE;
import static com.ail.core.document.DocumentType.POLICY;
import static com.ail.core.document.DocumentType.QUOTATION;
import static com.ail.core.document.DocumentType.SCHEDULE;
import static com.ail.core.document.DocumentType.WORDING;
import static com.ail.core.document.Multiplicity.ONE;
import static com.ail.core.language.I18N.i18n;
import static com.ail.financial.MoneyProvisionPurpose.ARREARS;
import static com.ail.financial.MoneyProvisionStatus.AUTHORISED;
import static com.ail.financial.ledger.JournalType.PREMIUM_RECEIVED;
import static com.ail.insurance.claim.ClaimStatus.CLOSED;
import static com.ail.insurance.claim.ClaimStatus.REJECTED;
import static com.ail.insurance.policy.AssessmentSheet.DEFAULT_SHEET_NAME;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.LAZY;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.hibernate.annotations.CascadeType.DETACH;
import static org.hibernate.annotations.CascadeType.MERGE;
import static org.hibernate.annotations.CascadeType.PERSIST;
import static org.hibernate.annotations.CascadeType.REFRESH;
import static org.hibernate.annotations.CascadeType.SAVE_UPDATE;

import java.text.DateFormat;
import java.text.ParseException;
import java.time.Period;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.Index;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.NamedNativeQueries;
import org.hibernate.annotations.NamedNativeQuery;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.AuditJoinTable;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Allowable;
import com.ail.core.Attribute;
import com.ail.core.CoreContext;
import com.ail.core.ExceptionRecord;
import com.ail.core.HasDocuments;
import com.ail.core.HasLabels;
import com.ail.core.HasNotes;
import com.ail.core.HasReferenceNumber;
import com.ail.core.Note;
import com.ail.core.Owned;
import com.ail.core.PageVisit;
import com.ail.core.ThreadLocale;
import com.ail.core.VersionEffectiveDate;
import com.ail.core.document.Document;
import com.ail.core.document.DocumentLink;
import com.ail.core.document.DocumentPlaceholder;
import com.ail.core.document.DocumentType;
import com.ail.core.product.HasProduct;
import com.ail.financial.Currency;
import com.ail.financial.CurrencyAmount;
import com.ail.financial.MoneyProvision;
import com.ail.financial.PaymentHoliday;
import com.ail.financial.PaymentRecord;
import com.ail.financial.PaymentSchedule;
import com.ail.financial.ledger.Journal;
import com.ail.insurance.HasAssets;
import com.ail.insurance.HasClauses;
import com.ail.insurance.claim.Claim;
import com.ail.party.HasPartyRoles;
import com.ail.party.Party;
import com.ail.party.PartyRole;

/**
 * A policy represents the contract between the insured and the insurer. It captures all of the information related to that contact including the assets
 * covered, the type of coverage included and the associated financial payments.
 */
@Audited
@Entity
@TypeDefinition
@NamedQueries({ @NamedQuery(name = "get.policy.by.user.and.product", query = "select pol from Policy pol where pol.owningUser = ? and pol.productTypeId = ? and pol.userSaved = true"),
        @NamedQuery(name = "get.policy.by.quotationNumber", query = "select pol from Policy pol where pol.quotationNumber = ?"),
        @NamedQuery(name = "get.policy.by.systemId", query = "select pol from Policy pol where pol.systemId = ?"),
        @NamedQuery(name = "get.policy.by.policyNumber", query = "select pol from Policy pol where pol.policyNumber = ?"),
        @NamedQuery(name = "get.policies.updated.since", query = "select pol from Policy pol where pol.updatedDate >= ?"),
        @NamedQuery(name = "get.policy.by.externalSystemId", query = "select pol from Policy pol where pol.externalSystemId = ?"),
        @NamedQuery(name = "get.policies.by.owningUser", query = "select pol from Policy pol where pol.owningUser = ?") })
@NamedNativeQueries({
        @NamedNativeQuery(name = "get.policies.by.attribute.value.mysql", query = "select * from polPolicy where ExtractValue(polAttribute,'boolean(//attribute[@id='?'][@value='?'])')", resultClass = Policy.class) })
public class Policy extends com.ail.core.Type implements Owned, HasProduct, HasNotes, HasDocuments, HasAssets, HasLabels, HasClauses, HasReferenceNumber, HasPartyRoles {
    static final long serialVersionUID = 3175904078936470552L;

    public static final String PROPOSER_PARTY_ROLE_TYPE = "i18n_party_role_policy_proposer";
    public static final String POLICY_HOLDER_PARTY_ROLE_TYPE = "i18n_party_role_policy_policy_holder";
    public static final String CLIENT_PARTY_ROLE_TYPE = "i18n_party_role_policy_client";
    public static final String BROKER_PARTY_ROLE_TYPE = "i18n_party_role_policy_broker";

    @Type(type = "com.ail.core.persistence.hibernate.XMLBlob", parameters = @Parameter(name = "type", value = "java.util.ArrayList"))
    @Basic(fetch = LAZY)
    private List<Allowable> allowable = new ArrayList<>();

    @Type(type = "com.ail.core.persistence.hibernate.XMLBlob", parameters = @Parameter(name = "type", value = "java.util.ArrayList"))
    @Basic(fetch = LAZY)
    private List<Excess> excess = new ArrayList<>();

    @OneToMany(cascade = ALL)
    private List<Asset> asset = new ArrayList<>();

    @OneToMany(cascade = ALL)
    private List<Section> section = new ArrayList<>();

    @OneToMany(cascade = ALL)
    private List<Coverage> coverage = new ArrayList<>();

    @OneToMany(cascade = ALL)
    private List<Clause> clause = new ArrayList<>();

    private String id = null;

    @Enumerated(STRING)
    @Index(name = "status")
    private PolicyStatus status = null;

    @OneToMany(cascade = ALL)
    @MapKeyColumn(name = "sheet_name")
    private Map<String, AssessmentSheet> assessmentSheetList = new HashMap<>();;

    private String productTypeId = null;

    private String productName = null;

    /** The quotation number associated with this policy (if any) */
    private String quotationNumber = null;

    /** The policy number associated with this policy (if any) */
    private String policyNumber = null;

    @OneToMany(cascade = { ALL }, mappedBy = "policy")
    private List<Claim> claim = new ArrayList<>();

    @Transient
    private ThreadLocale locale = null;

    /**
     * True if the user requested this policy be saved (as opposed to automated
     * system saves)
     */
    @Column(columnDefinition = "BIT")
    private boolean userSaved;

    /** True if this policy is a test case and not a real policy */
    @Column(columnDefinition = "BIT")
    private boolean testCase;

    /** True if this quote covers a number of products */
    @Column(columnDefinition = "BIT")
    private boolean aggregator;

    /** Details of all exceptions thrown during the processing of this quote. */
    @Type(type = "com.ail.core.persistence.hibernate.XMLBlob", parameters = @Parameter(name = "type", value = "java.util.ArrayList"))
    @Basic(fetch = LAZY)
    private List<ExceptionRecord> exception = new ArrayList<>();

    /** A list of the pages visited by this policy (by pageflow) */
    @Type(type = "com.ail.core.persistence.hibernate.XMLBlob", parameters = @Parameter(name = "type", value = "java.util.ArrayList"))
    @Basic(fetch = LAZY)
    @NotAudited
    private List<PageVisit> pageVisit = new ArrayList<>();

    /** Date that this policy was quoted */
    private Date quotationDate;

    /** Date that the quote made for this policy expires */
    private Date quotationExpiryDate;

    /** The default currency under which this policy is offered */
    @Enumerated(STRING)
    private Currency baseCurrency = Currency.GBP;

    /**
     * Links to other polices. Defines relationships to build concepts like: renewal
     * chain, MTA chain, etc.
     */
    @Type(type = "com.ail.core.persistence.hibernate.XMLBlob", parameters = @Parameter(name = "type", value = "java.util.ArrayList"))
    @Basic(fetch = LAZY)
    private List<PolicyLink> policyLink = new ArrayList<>();

    /** The date when the policy was incepted (created) */
    private Date inceptionDate;

    /** The date at which the policy will (or has) expired */
    private Date expiryDate;

    /**
     * A collection of the wordings associated with the policy (additional wordings
     * may exist at the section level).
     */
    @Type(type = "com.ail.core.persistence.hibernate.XMLBlob", parameters = @Parameter(name = "type", value = "java.util.ArrayList"))
    @Basic(fetch = LAZY)
    private List<Wording> wording = new ArrayList<>();

    /** A list of all of the valid payment option available under this policy */
    @OneToMany(cascade = ALL)
    private List<PaymentSchedule> paymentOption = new ArrayList<>();

    /**
     * The payment method selected (from {@link #paymentOption}) for use on this
     * policy
     */
    @OneToOne(cascade = ALL)
    @JoinColumn(name = "paymentDetailsUIDpsc", referencedColumnName = "UID")
    private PaymentSchedule paymentDetails;

    @OneToMany(cascade = ALL)
    private List<PaymentRecord> paymentHistory = new ArrayList<>();

    /** The effective date used to select rules/product etc for this policy */
    @Embedded
    @AttributeOverrides({ @AttributeOverride(name = "ved", column = @Column(name = "versionEffectiveDate")), })
    private VersionEffectiveDate versionEffectiveDate = null;

    @OneToMany(cascade = ALL)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<Document> document = new ArrayList<>();

    @OneToMany(cascade = ALL)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<Document> archivedDocument = new ArrayList<>();

    @OneToMany(cascade = ALL)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<DocumentPlaceholder> documentPlaceholder = new ArrayList<>();

    @Index(name = "owningUser")
    private Long owningUser;

    @OneToMany(cascade = ALL)
    private List<Note> note;

    /**
     * Index indicating which renewal this is in policy history. When a policy is
     * renewed, the renewalIndex is incremented in the new policy.
     */
    private Long renewalIndex;

    @OneToMany(cascade = ALL)
    @JoinColumn(name = "phoPolicyUIDpol")
    @AuditJoinTable(name = "jPolPhoPho_")
    List<PaymentHoliday> paymentHoliday = new ArrayList<>();

    /**
     * Index indicating which MTA this is is policy history. Each time a new MAT is
     * created, the MTA Index is incremented when the MTA is applied.
     */
    @Column(name = "polMtaIndex")
    private Long mtaIndex;

    @ElementCollection
    private Set<String> label;

    @OneToMany()
    @LazyCollection(LazyCollectionOption.TRUE)
    @Cascade({SAVE_UPDATE, DETACH, MERGE, PERSIST, REFRESH})
    private List<PartyRole> partyRole;

    public Policy() {
    }

    public Broker getBroker() {
        return (Broker)fetchPartyForRoleType(BROKER_PARTY_ROLE_TYPE);
    }

    public void setBroker(Broker broker) {
        setPartyForRoleTypes(broker, BROKER_PARTY_ROLE_TYPE);
    }

    /**
     * @deprecated - Use {@link #getClient()} instead.
     */
    @Deprecated
    public Party getProposer() {
        return getClient();
    }

    /**
     * @deprecated - Use {@link #setClient(Party)} instead.
     */
    @Deprecated
    public void setProposer(Party proposer) {
        setClient(proposer);
    }

    /**
     * @deprecated - Use {@link #getClient()} instead.
     */
    @Deprecated
    public Party getPolicyHolder() {
        return getClient();
    }

    /**
     * @deprecated - Use {@link #setClient(Party)} instead.
     */
    @Deprecated
    public void setPolicyHolder(Party policyHolder) {
       setClient(policyHolder);
    }

    public Date getQuotationDate() {
        return quotationDate;
    }

    public String getQuotationDateAsString() {
        return DateFormat.getDateInstance(DateFormat.SHORT).format(getQuotationDate());
    }

    public void setQuotationDate(Date quotationDate) {
        this.quotationDate = quotationDate;
    }

    public Date getQuotationExpiryDate() {
        return quotationExpiryDate;
    }

    public void setQuotationExpiryDate(Date quotationExpiryDate) {
        this.quotationExpiryDate = quotationExpiryDate;
    }

    public String getQuotationExpiryDateAsString() {
        return DateFormat.getDateInstance(DateFormat.SHORT).format(getQuotationExpiryDate());
    }

    /**
     * Get the collection of Coverages associated with this policy. Coverages exist
     * at both the policy and section levels, this method will only return those
     * associated with the policy. Use {@link Section#getCoverage} to get those
     * associated with specific sections.
     *
     * @return A collection of Coverages
     */
    public List<Coverage> getCoverage() {
        if (coverage == null) {
            coverage = new ArrayList<>();
        }

        return coverage;
    }

    /**
     * Set the collection of Coverages associated with this policy.
     *
     * @see #getCoverage()
     * @param coverage
     *            The collection of coverages to be associated with the policy.
     */
    public void setCoverage(List<Coverage> coverage) {
        this.coverage = coverage;
    }

    /**
     * Add a coverage to the list associated with this policy.
     *
     * @param coverage
     *            Coverage to add to the list.
     */
    public void addCoverage(Coverage coverage) {
        getCoverage().add(coverage);
    }

    /**
     * Remove the specified coverage from the list associated with this policy.
     *
     * @param coverage
     *            Coverage to be removed
     */
    public void removeCoverage(Coverage coverage) {
        getCoverage().remove(coverage);
    }

    /**
     * Fetch the coverage with a specific id from the collection associated with
     * this policy.
     *
     * @param coverageId
     *            ID of coverage to be returned
     * @return Coverage, or null if no match is found.
     */
    public Coverage getCoverageById(String coverageId) {
        for (Coverage c : getCoverage()) {
            if (c.getId().equals(coverageId)) {
                return c;
            }
        }

        return null;
    }

    /**
     * Fetch the collection of excess objects associated with this instance.
     *
     * @return A collection of instances of Excess.
     */
    public List<Excess> getExcess() {
        if (excess == null) {
            excess = new ArrayList<>();
        }

        return excess;
    }

    /**
     * Set the collection of instances of Excess associated with this object.
     *
     * @param excess
     *            A collection of instances of Excess
     * @see #getExcess
     */
    public void setExcess(List<Excess> excess) {
        this.excess = excess;
    }

    /**
     * Fetch a collection of the excesses associated with a specific section. If no
     * excesses are found, or the section is unknown an empty list is returned.
     *
     * @param sectionId
     *            ID of the section to fetch excesses for
     * @return Collection of Excess instances
     */
    public List<Excess> getExcessBySectionId(String sectionId) {
        List<Excess> ret = new ArrayList<>();

        Section section = getSectionById(sectionId);

        if (section != null) {
            for (Excess that : getExcess()) {
                if (section.getExcessId().contains(that.getId())) {
                    ret.add(that);
                }
            }
        }

        return ret;
    }

    /**
     * Add a excess to the collection associated with this object.
     *
     * @param excess
     *            Instance of Excess to add.
     */
    public void addExcess(Excess excess) {
        getExcess().add(excess);
    }

    /**
     * Remove a specific instance of Excess from the collection associated with this
     * object.
     *
     * @param excess
     *            Object to be removed from the collection.
     */
    public void removeExcess(Excess excess) {
        getExcess().remove(excess);
    }

    /**
     * Fetch a reference to a excess by its ID. Each excess has a unique id within a
     * policy which other parts of the policy (other excess, sections, etc) use to
     * refer to it. This method searches through the excess in the policy the
     * appropriate excess if found.
     *
     * @param excessId
     *            excess's id
     * @return A excess, or null if an excess by this ID cannot be found.
     */
    public Excess getExcessById(String excessId) {
        for (Excess that : getExcess()) {
            if (that.getId().equals(excessId)) {
                return that;
            }
        }

        return null;
    }

    /**
     * Fetch a collection of reference to excesses of a specific type in this
     * policy. A policy may contain more than one excess of the same type. This
     * method searches through the excess matching the excessTypeId specified
     * against each excess's excessTypeId.
     *
     * @param excessTypeId
     *            Type of excess to search for.
     * @return A collection of the excesses found, or an empty Collection if none
     *         are found.
     */
    public List<Excess> getExcessByTypeId(String excessTypeId) {
        List<Excess> ret = new ArrayList<>();

        for (Excess that : getExcess()) {
            if (that.getExcessTypeId().equals(excessTypeId)) {
                ret.add(that);
            }
        }

        return ret;
    }

    /**
     * Fetch a collection of reference to assets of a specific type in this policy.
     * A policy may contain more than one asset of the same type (more than one car
     * on a motor policy for example). This method searches through the assets
     * matching the typeId specified against each asset's assetTypeId.
     *
     * @param typeId
     *            Type of Asset to search for.
     * @return A collection of the assets found, or an empty Collection if none are
     *         found.
     */
    public List<Asset> getAssetByTypeId(String typeId) {
        List<Asset> ret = new ArrayList<>();

        for (Asset that : getAsset()) {
            if (that.getAssetTypeId() != null && that.getAssetTypeId().equals(typeId)) {
                ret.add(that);
            }
        }

        return ret;
    }

    /**
     * Fetch the collection of asset objects associated with this instance.
     *
     * @return A collection of instances of Asset.
     */
    @Override
    public List<Asset> getAsset() {
        if (asset == null) {
            asset = new ArrayList<>();
        }

        return asset;
    }

    /**
     * Set the collection of instances of Asset associated with this object.
     *
     * @param asset
     *            A collection of instances of Asset
     */
    @Override
    public void setAsset(List<Asset> asset) {
        this.asset = asset;
    }

    /**
     * Add a asset to the collection associated with this object.
     *
     * @param asset
     *            Instance of Asset to add.
     */
    @Override
    public void addAsset(Asset asset) {
        getAsset().add(asset);
    }

    /**
     * Remove a specific instance of Asset from the collection associated with this
     * object.
     *
     * @param asset
     *            Object to be removed from the collection.
     */
    @Override
    public void removeAsset(Asset asset) {
        String id = asset.getId();
        getAsset().remove(asset);

        // remove asset from sections
        if (id == null) {
            return;
        }

        List<Section> sections = getSection();
        if (sections == null || sections.isEmpty()) {
            return;
        }

        for (Section section : getSection()) {
            section.removeAssetId(id);
        }
    }

    /**
     * Remove a specific instance of Asset from the collection associated with this
     * object by its ID. Each asset has a unique id within a policy which other
     * parts of the policy (Sections, excesses, etc) use to refer to it. This method
     * searches through the assets in the policy the appropriate asset if found.
     *
     * @param id
     *            AssetId
     */
    public void removeAssetById(String id) {
        removeAsset(getAssetById(id));
    }

    /**
     * Fetch a reference to an asset by its ID. Each asset has a unique id within a
     * policy which other parts of the policy (Sections, excesses, etc) use to refer
     * to it. This method searches through the assets in the policy the appropriate
     * asset if found.
     *
     * @param id
     *            AssetId
     * @return An asset, or null if an asset by this ID cannot be found.
     */
    public Asset getAssetById(String id) {
        if (id != null) {
            for (Asset that : getAsset()) {
                if (id.equals(that.getId())) {
                    return that;
                }
            }
        }
        return null;
    }

    /**
     * Fetch a collection of the assets associated with a specific section. This
     * method returns <u>all</u> of the assets associated, those in the section's
     * insured and uninsured asset lists.
     * </p>
     * If no assets are found, or the section is unknown an empty list is returned.
     *
     * @param sectionId
     *            ID of the section to fetch assets for
     * @return Collection of Asset instances
     */
    public List<Asset> getAssetBySectionId(String sectionId) {
        List<Asset> ret = new ArrayList<>();

        Section section = getSectionById(sectionId);

        if (section != null) {
            for (Asset that : getAsset()) {
                if (section.getAssetId().contains(that.getId()) || section.getUninsuredAssetId().contains(that.getId())) {
                    ret.add(that);
                }
            }
        }

        return ret;
    }

    /**
     * Fetch a collection of the assets associated with a specific section. This
     * method returns only the section's insured assets.
     * </p>
     * If no assets are found, or the section is unknown an empty list is returned.
     *
     * @see #getAssetBySectionId(String)
     * @see #getUninsuredAssetBySectionId(String)
     * @param sectionId
     *            ID of the section to fetch assets for
     * @return Collection of Asset instances
     */
    public List<Asset> getInsuredAssetBySectionId(String sectionId) {
        List<Asset> ret = new ArrayList<>();

        Section section = getSectionById(sectionId);

        if (section != null) {
            for (Asset that : getAsset()) {
                if (section.getAssetId().contains(that.getId())) {
                    ret.add(that);
                }
            }
        }

        return ret;
    }

    /**
     * Fetch a collection of the assets associated with a specific section. This
     * method returns only the section's uninsured assets.
     * </p>
     * If no assets are found, or the section is unknown an empty list is returned.
     *
     * @see #getAssetBySectionId(String)
     * @see #getInsuredAssetBySectionId(String)
     * @param sectionId
     *            ID of the section to fetch assets for
     * @return Collection of Asset instances
     */
    public List<Asset> getUninsuredAssetBySectionId(String sectionId) {
        List<Asset> ret = new ArrayList<>();

        Section section = getSectionById(sectionId);

        if (section != null) {
            for (Asset that : getAsset()) {
                if (section.getUninsuredAssetId().contains(that.getId())) {
                    ret.add(that);
                }
            }
        }

        return ret;
    }

    /**
     * Fetch a reference to an attribute by its ID. Each attribute has a unique id
     * within a policy which other parts of the policy (Sections, excesses, etc) use
     * to refer to it. This method searches through the attributes in the policy the
     * appropriate attribute if found.
     *
     * @param id
     *            AttributeId
     * @return An attribute, or null if an attribute by this ID cannot be found.
     */
    public Attribute getAttributeById(String id) {
        if (id != null) {
            Attribute that;

            for (Iterator<Attribute> it = getAttribute().iterator(); it.hasNext();) {
                that = it.next();
                if (id.equals(that.getId())) {
                    return that;
                }
            }
        }
        return null;
    }

    /**
     * Fetch the collection of section objects associated with this instance.
     *
     * @return A collection of instances of Section.
     */
    public List<Section> getSection() {
        if (section == null) {
            section = new ArrayList<>();
        }

        return section;
    }

    /**
     * Set the collection of instances of Section associated with this object.
     *
     * @param section
     *            A collection of instances of Section
     */
    public void setSection(List<Section> section) {
        this.section = section;
    }

    /**
     * Add a section to the collection associated with this policy.
     *
     * @param section
     *            Instance of Section to add.
     */
    public void addSection(Section section) {
        getSection().add(section);
    }

    /**
     * Remove a specific instance of Asset from the collection associated with this
     * object.
     *
     * @param section
     *            Object to be removed from the collection.
     */
    public void removeSection(Section section) {
        getSection().remove(section);
    }

    /**
     * Fetch a reference to a section by its ID. Each section has a unique id within
     * a policy which other parts of the policy (other sections, excesses, etc) use
     * to refer to it. This method searches through the sections in the policy the
     * appropriate section if found.
     *
     * @param sectionId
     *            section's id
     * @return A section, or null if an section by this ID cannot be found.
     */
    public Section getSectionById(String sectionId) {
        for (Section that : getSection()) {
            if (that.getId().equals(sectionId)) {
                return that;
            }
        }

        return null;
    }

    /**
     * Fetch a collection of reference to sections of a specific type in this
     * policy. A policy may contain more than one section of the same type. This
     * method searches through the sections matching the sectionTypeId specified
     * against each section's sectionTypeId.
     *
     * @param sectionTypeId
     *            Type of Section to search for.
     * @return A collection of the sections found, or an empty Collection if none
     *         are found.
     */
    public List<Section> getSectionByTypeId(String sectionTypeId) {
        List<Section> ret = new ArrayList<>();

        for (Section that : getSection()) {
            if (that.getSectionTypeId().equals(sectionTypeId)) {
                ret.add(that);
            }
        }

        return ret;
    }

    /**
     * Get this policy ID. This id is unique to the policy
     *
     * @return The policy ID.
     */
    public String getId() {
        return id;
    }

    /**
     * Set the policy ID
     *
     * @param id
     *            New policy ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the policy status. This status indicates where the policy is in its
     * lifecycle. Examples are Application, Quotation, OnRisk...
     *
     * @return The current policy status.
     */
    public PolicyStatus getStatus() {
        return status;
    }

    /**
     * Get the policy status as a string (as opposed to an instance of
     * PolicyStatus).
     *
     * @return String representation of the policy status, or null if the status
     *         property has not been set.
     */
    public String getStatusAsString() {
        if (status != null) {
            return status.name();
        }
        return null;
    }

    /**
     * Move the policy to another status.
     *
     * @see #getStatus
     */
    public void setStatus(PolicyStatus status) {
        this.status = status;
    }

    /**
     * Set the policy status as a String. The String must represents a valid policy
     * status. i.e. it must be suitable for a call to PolicyStatus.forName().
     *
     * @see PolicyStatus#forName
     * @param status
     *            New policy status
     * @throws IndexOutOfBoundsException
     *             If status is not a valid policy status.
     */
    public void setStatusAsString(String status) throws IndexOutOfBoundsException {
        this.status = PolicyStatus.valueOf(status);
    }

    /**
     * Get the AssessmentSheet associated with this policy. A policy may have many
     * assessment sheets (see {@link #assessmentSheetList}) but only one can be
     * directly related to the policy. That is the "default" assessment sheet. The
     * others all represent alternative assessments that were created for some
     * reason (typically quote aggregation) but have not been selected.
     *
     * @return Assessment sheet. An empty assessment sheet may be returned, but null
     *         is never returned.
     * @throws IllegalAccessError
     *             if this policy is an aggregator policy ({@link #isAggregator()}).
     *             Aggregator policies have multiple assessment sheets so this
     *             method cannot be used. Use {@link #getAssessmentSheetFor(String)}
     *             instead.
     */
    public AssessmentSheet getAssessmentSheet() {
        if (isAggregator()) {
            throw new IllegalAccessError("getAssessmentSheet cannot be reference in an aggregated policy. Use getAssessmentSheetFor() instead.");
        }

        Map<String, AssessmentSheet> asl = getAssessmentSheetList();

        if (asl.get(DEFAULT_SHEET_NAME) == null) {
            asl.put(DEFAULT_SHEET_NAME, new AssessmentSheet());
        }

        return asl.get(DEFAULT_SHEET_NAME);
    }

    /**
     * Set the assessment sheet for this policy
     *
     * @param assessmentSheet
     *            Assessment sheet
     * @throws IllegalAccessError
     *             if this policy is an aggregator policy ({@link #isAggregator()}).
     *             Aggregator policies have multiple assessment sheets so this
     *             method cannot be used. Use {@link #getAssessmentSheetFor(String)}
     *             instead.
     */
    public void setAssessmentSheet(AssessmentSheet assessmentSheet) {
        if (isAggregator()) {
            throw new IllegalAccessError("getAssessmentSheet cannot be reference in an aggregated policy. Use getAssessmentSheetFor() instead.");
        }

        getAssessmentSheetList().put(DEFAULT_SHEET_NAME, assessmentSheet);
    }

    public AssessmentSheet getAssessmentSheetFor(String productTypeId) {
        return getAssessmentSheetList().get(productTypeId);
    }

    public void setAssessmentSheetFor(String productTypeId, AssessmentSheet sheet) {
        getAssessmentSheetList().put(productTypeId, sheet);
    }

    public Map<String, AssessmentSheet> getAssessmentSheetList() {
        if (assessmentSheetList == null) {
            assessmentSheetList = new HashMap<>();
        }

        return assessmentSheetList;
    }

    public void setAssessmentSheetList(Map<String, AssessmentSheet> assessmentSheetList) {
        this.assessmentSheetList = assessmentSheetList;
    }

    /**
     * Get the ProductTypeId. This string identifies the product that the policy
     * relates to. Among other things it allows services (e.g. assessRisk) to
     * identify the appropriate rules to run when processing this policy.
     *
     * @return The product type id.
     */
    @Override
    public String getProductTypeId() {
        return productTypeId;
    }

    /**
     * Set the product type id associated with this policy.
     *
     * @param productTypeId
     *            New product type id
     * @see #getProductTypeId
     */
    @Override
    public void setProductTypeId(String productTypeId) {
        this.productTypeId = productTypeId;
    }

    /**
     * Get the policy number associated with this policy.
     *
     * @return The policy number.
     */
    public String getPolicyNumber() {
        return policyNumber;
    }

    /**
     * Set the policy number associated with this policy
     *
     * @param policyNumber
     *            New policy number
     */
    public void setPolicyNumber(String policyNumber) {
        this.policyNumber = policyNumber;
    }

    /**
     * Getter returning the value of the quotationNumber property. The quotation
     * number associated with this policy
     *
     * @return Value of the quotationNumber property
     */
    public String getQuotationNumber() {
        return quotationNumber;
    }

    /**
     * Setter to update the value of the quotationNumber property. The quotation
     * number associated with this policy
     *
     * @param quotationNumber
     *            New value for the quotationNumber property
     */
    public void setQuotationNumber(String quotationNumber) {
        this.quotationNumber = quotationNumber;
    }

    /**
     * Check if this policy is marked as declined. This involves checking the
     * assessment sheet on the policy itself and on all of its sections.
     *
     * @return true if a decline flag is found, false otherwise.
     */
    public boolean isMarkedForDecline() {

        AssessmentSheet assessmentSheet = getAssessmentSheet();

        if (assessmentSheet != null && assessmentSheet.isMarkedForDecline()) {
            return true;
        }

        for (Section s : getSection()) {
            if (s.getAssessmentSheet() != null && s.getAssessmentSheet().isMarkedForDecline()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if this policy is marked as refer. This involves checking the
     * assessment sheet on the policy itself and on all of its sections.
     *
     * @return true if a refer flag is found, false otherwise.
     */
    public boolean isMarkedForRefer() {

        AssessmentSheet assessmentSheet = getAssessmentSheet();

        if (assessmentSheet != null && assessmentSheet.isMarkedForRefer()) {
            return true;
        }

        for (Section s : getSection()) {
            if (s.getAssessmentSheet() != null && s.getAssessmentSheet().isMarkedForRefer()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Check if this policy is marked with a subjectivity. This involves checking
     * the assessment sheet on the policy itself and on all of its sections.
     *
     * @return true if a refer flag is found, false otherwise.
     */
    public boolean isMarkedForSubjectivity() {
        AssessmentSheet assessmentSheet = getAssessmentSheet();

        if (assessmentSheet == null) {
            return false;
        } else if (assessmentSheet.isMarkedForSubjectivity()) {
            return true;
        }

        for (Section section : getSection()) {
            if (section.getAssessmentSheet().isMarkedForSubjectivity()) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determine if the policy has a total premium value associated with it.
     *
     * @return true if a total premium is defined, false otherwise.
     */
    public boolean isTotalPremiumDefined() {
        if (isAggregator()) {
            return false;
        } else {
            AssessmentSheet assessmentSheet = getAssessmentSheet();

            return assessmentSheet != null && assessmentSheet.isTotalPremiumDefined();
        }
    }

    /**
     * Get the total premium. The premium is a calculate value held in the
     * assessment sheet. It will only be present once the premium has been
     * calculated, and is never present on aggregator policies. Use
     * {@link #isTotalPremiumDefined()} to determine if a total premium is defined
     * or not.
     *
     * @return Total premium (if defined)
     * @throws IllegalStateException
     *             if the policy is an aggregator ({@link #isAggregator()} returns
     *             true) or if the assessment sheet does not define a total premium
     *             line.
     */
    public CurrencyAmount getTotalPremium() {
        if (isAggregator()) {
            throw new IllegalStateException("Assessment line: 'total premium' cannot be defined in a aggregator policy");
        } else {
            AssessmentSheet assessmentSheet = getAssessmentSheet();

            if (assessmentSheet != null) {
                CurrencyAmount total = assessmentSheet.getTotalPremium();
                if (total != null) {
                    return total;
                }
            }

            throw new IllegalStateException("Assessment line: 'total premium' is not defined for this policy");
        }
    }

    /**
     * Lock all the assessment sheets of the specified name in the policy to the
     * named actor.
     *
     * @param sheetName
     *            The name of the assessment sheet to be locked.
     * @param actor
     *            The name of the locking actor
     */
    public void lockAllAssessmentSheets(String sheetName, String actor) {
        AssessmentSheet assessmentSheet;

        assessmentSheet = getAssessmentSheetFor(sheetName);

        if (assessmentSheet != null) {
            assessmentSheet.setLockingActor(actor);
        }

        for (Section sect : getSection()) {
            assessmentSheet = sect.getAssessmentSheetFor(sheetName);
            if (assessmentSheet != null) {
                assessmentSheet.setLockingActor(actor);
            }
        }
    }

    /**
     * Unlock all the assessment sheets in the policy with the specified sheet name.
     *
     * @param sheetName
     *            The name of the sheets to be unlocked.
     */
    public void unlockAllAssessmentSheets(String sheetName) {
        AssessmentSheet assessmentSheet;

        assessmentSheet = getAssessmentSheetFor(sheetName);

        if (assessmentSheet != null) {
            assessmentSheet.clearLockingActor();
        }

        for (Section sect : getSection()) {
            assessmentSheet = sect.getAssessmentSheetFor(sheetName);
            if (assessmentSheet != null) {
                assessmentSheet.clearLockingActor();
                ;
            }
        }
    }

    /**
     * Remove all the assessment sheet lines in the named sheets which were added by
     * the specified origin. Lines are removed from both the policy and section
     * levels.
     *
     * @param sheetName
     *            The name of the sheets to be processed.
     * @param origin
     *            The name of the origin to remove lines for.
     */
    public void removeAssessmentLinesByOrigin(String sheetName, String origin) {
        AssessmentSheet assessmentSheet;

        assessmentSheet = getAssessmentSheetFor(sheetName);

        // If the policy has an assessment sheet, remove it's lines by origin
        if (assessmentSheet != null) {
            assessmentSheet.removeLinesByOrigin(origin);
        }

        // If any section has an assessment sheet, remove their lines by origin
        for (Section sect : getSection()) {
            assessmentSheet = sect.getAssessmentSheetFor(sheetName);
            if (assessmentSheet != null) {
                assessmentSheet.removeLinesByOrigin(origin);
            }
        }
    }

    /**
     * Reset the "fired" status on any control lines in the assessment sheets named
     * which are associated with the policy and it's sections.
     *
     * @param sheetName
     *            The name of the sheets to be reset.
     */
    public void resetAssessmentControlLines(String sheetName) {
        AssessmentSheet assessmentSheet;

        assessmentSheet = getAssessmentSheetFor(sheetName);

        // If the policy has an assessment sheet, reset its control lines.
        if (assessmentSheet != null) {
            assessmentSheet.resetAssessmentControlLines();
        }

        // If any section has a assessment sheet, clean their control lines.
        for (Section sect : getSection()) {
            assessmentSheet = sect.getAssessmentSheetFor(sheetName);
            if (assessmentSheet != null) {
                assessmentSheet.resetAssessmentControlLines();
            }
        }
    }

    /**
     * Getter returning the value of the inceptionDate property. The date when the
     * policy was incepted (created)
     *
     * @return Value of the inceptionDate property
     */
    public Date getInceptionDate() {
        return inceptionDate;
    }

    /**
     * Setter to update the value of the inceptionDate property. The date when the
     * policy was incepted (created)
     *
     * @param inceptionDate
     *            New value for the inceptionDate property
     */
    public void setInceptionDate(Date inceptionDate) {
        this.inceptionDate = inceptionDate;
    }

    /**
     * Getter returning the value of the inceptionDate in DataFormat.SHORT. The date
     * when the policy was incepted (created)
     *
     * @return Value of the inceptionDate property
     */
    public String getInceptionDateAsString() {
        return DateFormat.getDateInstance(DateFormat.SHORT).format(getInceptionDate());
    }

    /**
     * Setter to update the value of the inceptionDate property from a String. The
     * supplied date must be in DateFormat.SHORT format.
     *
     * @param inceptionDate
     *            New value for the inceptionDate property
     */
    public void setInceptionDateAsString(String inceptionDate) throws ParseException {
        setInceptionDate(DateFormat.getDateInstance(DateFormat.SHORT).parse(inceptionDate));
    }

    /**
     * Getter returning the value of the expiryDate property. The date at which the
     * policy will (of has) expired
     *
     * @return Value of the expiryDate property
     */
    public Date getExpiryDate() {
        return expiryDate;
    }

    /**
     * Setter to update the value of the expiryDate property. The date at which the
     * policy will (of has) expired
     *
     * @param expiryDate
     *            New value for the expiryDate property
     */
    public void setExpiryDate(Date expiryDate) {
        this.expiryDate = expiryDate;
    }

    /**
     * Getter returning the value of the expiryDate property wrapped in CDATA tags.
     * The date at which the policy will (of has) expired
     *
     * @return Value of the expiryDate property
     */
    public String getExpiryDateAsString() {
        return DateFormat.getDateInstance(DateFormat.SHORT).format(getExpiryDate());
    }

    public void setExpiryDateAsString(String expiryDate) throws ParseException {
        setExpiryDate(DateFormat.getDateInstance(DateFormat.SHORT).parse(expiryDate));
    }

    /**
     * Get the collection of instances of com.ail.insurance.policy.Wording
     * associated with this object.
     *
     * @return wording A collection of instances of Excess
     * @see #setWording
     */
    public List<Wording> getWording() {
        if (wording == null) {
            wording = new ArrayList<>();
        }

        return wording;
    }

    /**
     * Set the collection of instances of com.ail.insurance.policy.Wording
     * associated with this object.
     *
     * @param wording
     *            A collection of instances of Excess
     * @see #getWording
     */
    public void setWording(List<Wording> wording) {
        this.wording = wording;
    }

    /**
     * Remove the specified instance of com.ail.insurance.policy.Wording from the
     * list.
     *
     * @param wording
     *            Instance to be removed
     */
    public void removeWording(Wording wording) {
        getWording().remove(wording);
    }

    /**
     * Add an instance of com.ail.insurance.policy.Wording to the list associated
     * with this object.
     *
     * @param wording
     *            Instance to add to list
     */
    public void addWording(Wording wording) {
        getWording().add(wording);
    }

    /**
     * Getter returning the value of the paymentDetails property. The proposed
     * method of payment and its details
     *
     * @return Value of the paymentDetails property
     */
    public PaymentSchedule getPaymentDetails() {
        return paymentDetails;
    }

    /**
     * Setter to update the value of the paymentDetails property. The proposed
     * method of payment and its details
     *
     * @param paymentDetails
     *            New value for the paymentDetails property
     */
    public void setPaymentDetails(PaymentSchedule paymentDetails) {
        this.paymentDetails = paymentDetails;
    }

    /**
     * Is the policy currently in arrears?
     * @return true if it is in arrears, else false;
     */
    public boolean isInArrears() {
        Date startDate = getArrearsStartDate();

        if (startDate == null) {
            return false;
        }

        return true;
    }

    /**
     * Find the start date of the policy arrears, if applicable.
     * @return the start date of the arrears if the policy is in arrears, else null.
     */
    public Date getArrearsStartDate() {
        if (getPaymentDetails() != null) {
            for (MoneyProvision payment : getPaymentDetails().getMoneyProvision()) {
                if (payment.getPurpose() == ARREARS && payment.getStatus() == AUTHORISED) {
                    Date today = new Date();
                    if (payment.getPaymentsStartDate().before(today) && payment.getPaymentsEndDate().after(today)) {
                        return payment.getPaymentsStartDate();
                    }
                }
            }
        }
        return null;
    }

    /**
     * Get the collection of instances of com.ail.core.Allowable associated with
     * this object.
     *
     * @return A collection of instances of Allowable
     * @see #setAllowable
     */
    public List<Allowable> getAllowable() {
        if (allowable == null) {
            allowable = new ArrayList<>();
        }

        return allowable;
    }

    /**
     * Set the collection of instances of com.ail.core.Allowable associated with
     * this object.
     *
     * @param allowable
     *            A collection of instances of Allowable
     * @see #getAllowable
     */
    public void setAllowable(List<Allowable> allowable) {
        this.allowable = allowable;
    }

    /**
     * Remove the specified instance of com.ail.core.Allowable from the list.
     *
     * @param wording
     *            Instance to be removed
     */
    public void removeAllowable(Allowable wording) {
        getAllowable().remove(wording);
    }

    /**
     * Add an instance of com.ail.core.Allowable to the list associated with this
     * object.
     *
     * @param allowable
     *            Instance to add to list
     */
    public void addAllowable(Allowable allowable) {
        getAllowable().add(allowable);
    }

    /**
     * Get the textual name of the product which this policy is an instance of. This
     * is the name which the product is known by outside of the system, and will
     * appear on screens and documents.
     *
     * @return product name
     */
    public String getProductName() {
        return productName;
    }

    /**
     * @see #getProductName()
     * @param productName
     */
    public void setProductName(String productName) {
        this.productName = productName;
    }

    /**
     * Fetch the list of clauses associated with this policy. An empty list may be
     * returned, but a null will never be returned.
     *
     * @return the clause
     */
    @Override
    public List<Clause> getClause() {
        if (clause == null) {
            clause = new ArrayList<>();
        }

        return clause;
    }

    /**
     * @param clause
     *            the clause to set
     */
    @Override
    public void setClause(List<Clause> clause) {
        this.clause = clause;
    }

    /**
     * Get the locale <b>currently</b> associated with this policy. This indicates
     * the locale for which the policy is currently being processed, so it may
     * change through the life time of the policy. It is intended to influence the
     * way in which the policy is presented. For example, when a US policy is being
     * administered by a US office, you would expect all presentation, documentation
     * etc to render US$ amounts simply as '$'. However, if the same policy was
     * being administered in a office in Canada, a US$ amount should be shown as
     * US$, and not '$'.
     *
     * @return Locale being used for processing
     */
    public ThreadLocale getLocale() {
        return locale;
    }

    /**
     * @see #getLocale()
     * @param locale
     */
    public void setLocale(ThreadLocale locale) {
        this.locale = locale;
    }

    /**
     * Get the most recently visited page for a given PageFlow. A policy may be
     * viewed in the context of many different page flows, details of each of those
     * page views is stored within the {@link #getPageVisit()} collection. This
     * method will return the name of page most recently visited in the specified
     * page flow.
     *
     * @param pageFlowName
     * @return page name, or null if the policy has not been viewed in the specified
     *         page flow.
     */
    public String getPage(String pageFlowName) {
        PageVisit latest = null;

        for (PageVisit visit : getPageVisit()) {
            if (visit.getPageFlowName().equals(pageFlowName)) {
                if (latest == null || visit.getVisited().after(latest.getVisited())) {
                    latest = visit;
                }
            }
        }

        return latest == null ? null : latest.getPageName();
    }

    public void setPage(String pageFlowName, String page) {
        addPageVisit(new PageVisit(pageFlowName, page, new Date()));
    }

    public List<PaymentSchedule> getPaymentOption() {
        if (paymentOption == null) {
            paymentOption = new ArrayList<>();
        }

        return paymentOption;
    }

    public void setPaymentOption(List<PaymentSchedule> paymentOption) {
        this.paymentOption = paymentOption;
    }

    public boolean isUserSaved() {
        return userSaved;
    }

    public void setUserSaved(boolean userSaved) {
        this.userSaved = userSaved;
    }

    public boolean isTestCase() {
        return testCase;
    }

    /**
     * Returns true if this policy is an aggregated quote. An aggregated quote is
     * one that covers a number of products, normally from different insurers.
     *
     * @return true if this is an aggregated policy, false otherwise.
     */
    public boolean isAggregator() {
        return aggregator;
    }

    /**
     * @see #isAggregator()
     * @param aggregator
     */
    public void setAggregator(boolean aggregator) {
        this.aggregator = aggregator;
    }

    public void setTestCase(boolean testCase) {
        this.testCase = testCase;
    }

    public List<ExceptionRecord> getException() {
        if (exception == null) {
            exception = new ArrayList<>();
        }

        return exception;
    }

    public void setException(List<ExceptionRecord> exception) {
        this.exception = exception;
    }

    public void addException(ExceptionRecord exception) {
        getException().add(exception);
    }

    public List<PageVisit> getPageVisit() {
        if (pageVisit == null) {
            pageVisit = new ArrayList<>();
        }

        return pageVisit;
    }

    public void setPageVisit(List<PageVisit> pageVisit) {
        this.pageVisit = pageVisit;
    }

    protected void addPageVisit(PageVisit pageVisit) {
        getPageVisit().add(pageVisit);
    }

    /**
     * The payment history is a record of the payment events that have occurred with
     * respect to this policy. This record not only includes the details of actual
     * transactions, it also records the details of requests made to payment
     * services (e.g. PayPal), and the outcome of those requests. An empty list may
     * be returned, but a null will never be returned.
     *
     * @return Payment History
     */
    public List<PaymentRecord> getPaymentHistory() {
        if (paymentHistory == null) {
            paymentHistory = new ArrayList<>();
        }

        return paymentHistory;
    }

    /**
     * @See #getPaymentHistory()
     * @param paymentHistory
     */
    public void setPaymentHistory(List<PaymentRecord> paymentHistory) {
        this.paymentHistory = paymentHistory;
    }

    /**
     * The version effective date to be used when processing this policy. This will
     * be used to select the appropriate configurations to use during processing.
     *
     * @return Version effective date or null.
     */
    public VersionEffectiveDate getVersionEffectiveDate() {
        return versionEffectiveDate;
    }

    /**
     * @see #getVersionEffectiveDate()
     */
    public void setVersionEffectiveDate(VersionEffectiveDate versionEffectiveDate) {
        this.versionEffectiveDate = versionEffectiveDate;
    }

    public Currency getBaseCurrency() {
        return baseCurrency;
    }

    public void setBaseCurrency(Currency baseCurrency) {
        this.baseCurrency = baseCurrency;
    }

    public void setBaseCurrencyAsString(String baseCurrency) {
        setBaseCurrency(Currency.valueOf(baseCurrency));
    }

    public String getBaseCurrencyAsString() {
        return getBaseCurrency().getName();
    }

    public List<PolicyLink> getPolicyLink() {
        if (policyLink == null) {
            policyLink = new ArrayList<>();
        }

        return policyLink;
    }

    public void setPolicyLink(List<PolicyLink> links) {
        this.policyLink = links;
    }

    @Override
    public List<Document> getDocument() {
        if (document == null) {
            document = new ArrayList<>();
        }

        return document;
    }

    @Override
    public void setDocument(List<Document> document) {
        this.document = document;
    }

    @Override
    public List<Document> getArchivedDocument() {
        if (archivedDocument == null) {
            archivedDocument = new ArrayList<>();
        }

        return archivedDocument;
    }

    @Override
    public void setArchivedDocument(List<Document> archivedDocument) {
        this.archivedDocument = archivedDocument;
    }

    @Override
    public void archiveDocument(Document document) {
        if (this.document.contains(document)) {
            this.archivedDocument.add(document);
            this.document.remove(document);
        }
    }

    @Override
    public void restoreDocument(Document document) {
        if (this.archivedDocument.contains(document)) {
            this.document.add(document);
            this.archivedDocument.remove(document);
        }
    }

    @Override
    public Document retrieveDocumentOfType(DocumentType type) {
        return retrieveDocumentOfType(type.longName());
    }

    @Override
    public Document retrieveDocumentOfType(String type) {
        List<Document> res = filter(having(on(Document.class).getType(), is(type)), getDocument());

        if (res.size() == 1) {
            return res.get(0);
        } else {
            return null;
        }
    }

    private Document addDocumentOfType(DocumentType type, byte[] doc, String filename, String title) {
        if (type.getMultiplicity() == ONE) {
            setDocument(filter(having(on(Document.class).getType(), is(not(type.longName()))), getDocument()));
        }

        Document document = new Document(type, doc, title, filename, APPLICATION_PDF, getProductTypeId());

        getDocument().add(document);

        return document;
    }

    @Override
    public List<DocumentPlaceholder> getDocumentPlaceholder() {
        return this.documentPlaceholder;
    }

    @Override
    public void setDocumentPlaceholder(List<DocumentPlaceholder> documentPlaceholder) {
        this.documentPlaceholder = documentPlaceholder;
    }

    public Document retrieveQuotationDocument() {
        return retrieveDocumentOfType(QUOTATION);
    }

    public Document attachQuotationDocument(byte[] quotationDocument) {
        String title = String.format(i18n("i18n_document_generation_quotation_document_title"), getQuotationNumber());
        String filename = String.format(i18n("i18n_document_generation_quotation_document_filename"), getQuotationNumber());
        return addDocumentOfType(QUOTATION, quotationDocument, filename, title);
    }

    public Document retrieveInvoiceDocument() {
        return retrieveDocumentOfType(INVOICE);
    }

    public Document attachInvoiceDocument(byte[] invoiceDocument) {
        String title = String.format(i18n("i18n_document_generation_invoice_document_title"), getPolicyNumber());
        String filename = String.format(i18n("i18n_document_generation_invoice_document_filename"), getPolicyNumber());
        return addDocumentOfType(INVOICE, invoiceDocument, filename, title);
    }

    /**
     * Return either the wording from the policy's document list (by preference) or,
     * if there is none but the policy does define a single {@link #wording}, and
     * that wording has a URL associated with it, return a {@link DocumentLink} to
     * that URL.
     */
    public Document retrieveWordingDocument() {
        Document wordingDocument = retrieveDocumentOfType(WORDING);

        if (wordingDocument != null) {
            return wordingDocument;
        }

        if (wording.size() == 1 && wording.get(0).hasUrl()) {
            return new DocumentLink(WORDING, wording.get(0).getUrl(), "Wording");
        }

        return null;
    }

    public Document attachWordingDocument(byte[] wordingDocument) {
        String title = String.format(i18n("i18n_document_generation_wording_document_title"), getPolicyNumber());
        String filename = String.format(i18n("i18n_document_generation_wording_document_filename"), getPolicyNumber());

        return addDocumentOfType(WORDING, wordingDocument, filename, title);
    }

    public Document retrieveCertificateDocument() {
        return retrieveDocumentOfType(CERTIFICATE);
    }

    public Document attachCertificateDocument(byte[] certificateDocument) {
        String title = String.format(i18n("i18n_document_generation_certificate_document_title"), getPolicyNumber());
        String filename = String.format(i18n("i18n_document_generation_certificate_document_filename"), getPolicyNumber());

        return addDocumentOfType(CERTIFICATE, certificateDocument, filename, title);
    }

    public Document retrieveEndorsementDocument() {
        return retrieveDocumentOfType(ENDORSEMENT);
    }

    public Document attachEndorsementDocument(byte[] endorsementDocument) {
        String title = String.format(i18n("i18n_document_generation_endorsement_document_title"), getPolicyNumber());
        String filename = String.format(i18n("i18n_document_generation_endorsement_document_filename"), getPolicyNumber());

        return addDocumentOfType(ENDORSEMENT, endorsementDocument, filename, title);
    }

    public Document retrieveScheduleDocument() {
        return retrieveDocumentOfType(SCHEDULE);
    }

    public Document attachScheduleDocument(byte[] scheduleDocument) {
        String title = String.format(i18n("i18n_document_generation_schedule_document_title"), getPolicyNumber());
        String filename = String.format(i18n("i18n_document_generation_schedule_document_filename"), getPolicyNumber());

        return addDocumentOfType(SCHEDULE, scheduleDocument, filename, title);
    }

    public Document retrievePolicyDocument() {
        return retrieveDocumentOfType(POLICY);
    }

    public Document attachPolicyDocument(byte[] policyDocument) {
        String title = String.format(i18n("i18n_document_generation_policy_document_title"), getPolicyNumber());
        String filename = String.format(i18n("i18n_document_generation_policy_document_filename"), getPolicyNumber());

        return addDocumentOfType(POLICY, policyDocument, filename, title);
    }

    @Override
    public Long getOwningUser() {
        return owningUser;
    }

    @Override
    public void setOwningUser(Long owningUser) {
        this.owningUser = owningUser;
    }

    public Party getClient() {
        return fetchPartyForRoleType(CLIENT_PARTY_ROLE_TYPE);
    }

    public void setClient(Party client) {
        setPartyForRoleTypes(client, CLIENT_PARTY_ROLE_TYPE);
    }

    @Override
    public List<Note> getNote() {
        if (note == null) {
            note = new ArrayList<>();
        }
        return note;
    }

    @Override
    public void setNote(List<Note> note) {
        this.note = note;
    }

    @Override
    public List<PartyRole> getPartyRole() {
        if (partyRole == null) {
            partyRole = new ArrayList<>();
        }

        return partyRole;
    }

    @Override
    public void setPartyRole(List<PartyRole> partyRole) {
        this.partyRole = partyRole;
    }

    public List<PaymentHoliday> getPaymentHoliday() {
        if (paymentHoliday == null) {
            paymentHoliday = new ArrayList<>();
        }

        return paymentHoliday;
    }

    public void setPaymentHoliday(List<PaymentHoliday> paymentHoliday) {
        this.paymentHoliday = paymentHoliday;
    }

    /**
     * Retrieves the payment holidays on the policy, sorts them by created date and returns.
     * @return a list of sorted payment holidays by created date
     */
    public List<PaymentHoliday> getPaymentHolidaySortedByCreatedDate() {
        List<PaymentHoliday> holidays = getPaymentHoliday();
        Collections.sort(holidays, new Comparator<PaymentHoliday>(){
            @Override
            public int compare(PaymentHoliday thisHoliday, PaymentHoliday thatHoliday) {
                Date thisHolidayCreatedDate = thisHoliday.getCreatedDate();
                Date thatHolidayCreateDate = thatHoliday.getCreatedDate();

                if (thisHolidayCreatedDate == null && thatHolidayCreateDate == null){
                    return 0;
                }
                else if (thisHolidayCreatedDate == null) {
                    return 1;
                }
                else if (thatHolidayCreateDate == null) {
                    return -1;
                }

                return thisHoliday.getCreatedDate().compareTo(thatHoliday.getCreatedDate());
            }
        });
        return holidays;
    }

    /**
     * Returns the total month duration for all payments as an integer.
     * If no payment holidays have been taken 0 is returned.
     * @return Duration of all premium holidays as an int.
     */
    public int calculatePaymentHolidaysTotalDurationValue() {
        int totalMonths = 0;

        for (PaymentHoliday holiday : getPaymentHoliday()) {
            Period duration = holiday.durationPeriod();
            totalMonths += duration.getMonths();
        }

        return totalMonths;
    }

    /**
     * Return the remaining months duration for payment holidays in a format of "48 months".
     * @return Duration remaining for premium holidays as an int.
     */
    public int calculatePaymentHolidaysDurationRemaining() {
        int monthsTaken = calculatePaymentHolidaysTotalDurationValue();
        String lifetimeMonthsString = CoreContext.getCoreProxy().getGroup("PaymentHoliday").findParameterValue("lifetimeMonthsPermitted", "");
        int lifetimeMonths = lifetimeMonthsString.isEmpty()?0:Integer.parseInt(lifetimeMonthsString);
        int monthsRemaining = lifetimeMonths - monthsTaken;

        return monthsRemaining;
    }

    /**
     * Checks all payment holidays in the policy and if one is active
     * returns true, else returns false.
     * @return true if there is an active payment holiday, otherwise false.
     */
    public boolean isInPaymentHolidayNow() {
        for (PaymentHoliday holiday : getPaymentHoliday()) {
            boolean inHolidayNow = holiday.isInPaymentHolidayNow();
            if (inHolidayNow) {
                return true;
            }
        }
        return false;
    }

    public Long getRenewalIndex() {
        return renewalIndex;
    }

    public void setRenewalIndex(Long renewalIndex) {
        this.renewalIndex = renewalIndex;
    }

    public Long getMtaIndex() {
        return mtaIndex;
    }

    public void setMtaIndex(Long mtaIndex) {
        this.mtaIndex = mtaIndex;
    }

    public List<Claim> getClaim() {
        if (claim == null) {
            claim = new ArrayList<>();
        }
        return claim;
    }

    public void setClaim(List<Claim> claim) {
        this.claim = claim;
    }

    public Claim addClaim(Claim claim) {
        claim.setPolicy(this);
        getClaim().add(claim);
        return claim;
    }

    /**
     * Checks to see if there's an active claim on the policy.
     * @return true if there's an active claim, otherwise false.
     */
    public boolean hasActiveClaim() {
        List<Claim> claims = getClaim();

        for (Claim claim : claims) {
            if (!Arrays.asList(CLOSED, REJECTED).contains(claim.getStatus())) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Set<String> getLabel() {
        if (label == null) {
            label = new HashSet<>();
        }

        return label;
    }

    @Override
    public void setLabel(Set<String> label) {
        this.label = label;
    }

    @Override
    public String getReferenceNumber() {
        return !isEmpty(policyNumber) ? policyNumber : !isEmpty(quotationNumber) ? quotationNumber : super.getReferenceNumber();
    }

    /**
     * Fetch a list of the journals associated with this policy.
     * @return list of Journals with a reference related to this policy.
     */
    @SuppressWarnings("unchecked")
    public List<Journal> fetchJournals() {
        return (List<Journal>)CoreContext.getCoreProxy().query("get.journal.by.subject", Policy.class.getName(), getExternalSystemId());
    }

    /**
     * Determine whether a payment has ever been made against this policy.
     * @return true if a Journal of type PREMIUM_RECIEVED is present; false otherwise.
     */
    public boolean hasPremiumPayments() {
        List<?> journals = CoreContext.getCoreProxy().query("get.journal.by.subject.and.journal.type", Policy.class.getName(), getExternalSystemId(), PREMIUM_RECEIVED);
        return journals != null && journals.size() > 0;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (aggregator ? 1231 : 1237);
        result = prime * result + ((allowable == null) ? 0 : allowable.hashCode());
        result = prime * result + ((assessmentSheetList == null) ? 0 : assessmentSheetList.hashCode());
        result = prime * result + ((asset == null) ? 0 : asset.hashCode());
        result = prime * result + ((baseCurrency == null) ? 0 : baseCurrency.hashCode());
        result = prime * result + ((clause == null) ? 0 : clause.hashCode());
        result = prime * result + ((coverage == null) ? 0 : coverage.hashCode());
        result = prime * result + ((document == null) ? 0 : document.hashCode());
        result = prime * result + ((exception == null) ? 0 : exception.hashCode());
        result = prime * result + ((excess == null) ? 0 : excess.hashCode());
        result = prime * result + ((expiryDate == null) ? 0 : expiryDate.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((inceptionDate == null) ? 0 : inceptionDate.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((locale == null) ? 0 : locale.hashCode());
        result = prime * result + ((mtaIndex == null) ? 0 : mtaIndex.hashCode());
        result = prime * result + ((pageVisit == null) ? 0 : pageVisit.hashCode());
        result = prime * result + ((paymentDetails == null) ? 0 : paymentDetails.hashCode());
        result = prime * result + ((partyRole == null) ? 0 : partyRole.hashCode());
        result = prime * result + ((paymentHistory == null) ? 0 : paymentHistory.hashCode());
        result = prime * result + ((paymentOption == null) ? 0 : paymentOption.hashCode());
        result = prime * result + ((paymentHoliday == null) ? 0 : paymentHoliday.hashCode());
        result = prime * result + ((policyLink == null) ? 0 : policyLink.hashCode());
        result = prime * result + ((policyNumber == null) ? 0 : policyNumber.hashCode());
        result = prime * result + ((productName == null) ? 0 : productName.hashCode());
        result = prime * result + ((productTypeId == null) ? 0 : productTypeId.hashCode());
        result = prime * result + ((quotationDate == null) ? 0 : quotationDate.hashCode());
        result = prime * result + ((quotationExpiryDate == null) ? 0 : quotationExpiryDate.hashCode());
        result = prime * result + ((quotationNumber == null) ? 0 : quotationNumber.hashCode());
        result = prime * result + ((renewalIndex == null) ? 0 : renewalIndex.hashCode());
        result = prime * result + ((section == null) ? 0 : section.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + (testCase ? 1231 : 1237);
        result = prime * result + (userSaved ? 1231 : 1237);
        result = prime * result + ((owningUser == null) ? 0 : owningUser.hashCode());
        result = prime * result + ((versionEffectiveDate == null) ? 0 : versionEffectiveDate.hashCode());
        result = prime * result + ((wording == null) ? 0 : wording.hashCode());
        result = prime * result + ((claim == null) ? 0 : claim.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Policy other = (Policy) obj;
        if (aggregator != other.aggregator)
            return false;
        if (allowable == null) {
            if (other.allowable != null)
                return false;
        } else if (!allowable.equals(other.allowable))
            return false;
        if (assessmentSheetList == null) {
            if (other.assessmentSheetList != null)
                return false;
        } else if (!assessmentSheetList.equals(other.assessmentSheetList))
            return false;
        if (asset == null) {
            if (other.asset != null)
                return false;
        } else if (!asset.equals(other.asset))
            return false;
        if (baseCurrency != other.baseCurrency)
            return false;
        if (clause == null) {
            if (other.clause != null)
                return false;
        } else if (!clause.equals(other.clause))
            return false;
        if (coverage == null) {
            if (other.coverage != null)
                return false;
        } else if (!coverage.equals(other.coverage))
            return false;
        if (document == null) {
            if (other.document != null)
                return false;
        } else if (!document.equals(other.document))
            return false;
        if (exception == null) {
            if (other.exception != null)
                return false;
        } else if (!exception.equals(other.exception))
            return false;
        if (excess == null) {
            if (other.excess != null)
                return false;
        } else if (!excess.equals(other.excess))
            return false;
        if (expiryDate == null) {
            if (other.expiryDate != null)
                return false;
        } else if (!expiryDate.equals(other.expiryDate))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (inceptionDate == null) {
            if (other.inceptionDate != null)
                return false;
        } else if (!inceptionDate.equals(other.inceptionDate))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (locale == null) {
            if (other.locale != null)
                return false;
        } else if (!locale.equals(other.locale))
            return false;
        if (mtaIndex == null) {
            if (other.mtaIndex != null)
                return false;
        } else if (!mtaIndex.equals(other.mtaIndex))
            return false;
        if (pageVisit == null) {
            if (other.pageVisit != null)
                return false;
        } else if (!pageVisit.equals(other.pageVisit))
            return false;
        if (partyRole == null) {
            if (other.partyRole != null)
                return false;
        } else if (!partyRole.equals(other.partyRole))
            return false;
        if (paymentDetails == null) {
            if (other.paymentDetails != null)
                return false;
        } else if (!paymentDetails.equals(other.paymentDetails))
            return false;
        if (paymentHistory == null) {
            if (other.paymentHistory != null)
                return false;
        } else if (!paymentHistory.equals(other.paymentHistory))
            return false;
        if (paymentOption == null) {
            if (other.paymentOption != null)
                return false;
        } else if (!paymentOption.equals(other.paymentOption))
            return false;
        if (policyLink == null) {
            if (other.policyLink != null)
                return false;
        } else if (!policyLink.equals(other.policyLink))
            return false;
        if (policyNumber == null) {
            if (other.policyNumber != null)
                return false;
        } else if (!policyNumber.equals(other.policyNumber))
            return false;
        if (productName == null) {
            if (other.productName != null)
                return false;
        } else if (!productName.equals(other.productName))
            return false;
        if (productTypeId == null) {
            if (other.productTypeId != null)
                return false;
        } else if (!productTypeId.equals(other.productTypeId))
            return false;
        if (quotationDate == null) {
            if (other.quotationDate != null)
                return false;
        } else if (!quotationDate.equals(other.quotationDate))
            return false;
        if (quotationExpiryDate == null) {
            if (other.quotationExpiryDate != null)
                return false;
        } else if (!quotationExpiryDate.equals(other.quotationExpiryDate))
            return false;
        if (quotationNumber == null) {
            if (other.quotationNumber != null)
                return false;
        } else if (!quotationNumber.equals(other.quotationNumber))
            return false;
        if (section == null) {
            if (other.section != null)
                return false;
        } else if (!section.equals(other.section))
            return false;
        if (paymentHoliday == null) {
            if (other.paymentHoliday != null)
                return false;
        } else if (!paymentHoliday.equals(other.paymentHoliday))
            return false;
        if (renewalIndex == null) {
            if (other.renewalIndex != null)
                return false;
        } else if (!renewalIndex.equals(other.renewalIndex))
            return false;
        if (status != other.status)
            return false;
        if (testCase != other.testCase)
            return false;
        if (userSaved != other.userSaved)
            return false;
        if (owningUser == null) {
            if (other.owningUser != null)
                return false;
        } else if (!owningUser.equals(other.owningUser))
            return false;
        if (versionEffectiveDate == null) {
            if (other.versionEffectiveDate != null)
                return false;
        } else if (!versionEffectiveDate.equals(other.versionEffectiveDate))
            return false;
        if (wording == null) {
            if (other.wording != null)
                return false;
        } else if (!wording.equals(other.wording))
            return false;
        if (claim == null) {
            if (other.claim != null)
                return false;
        } else if (!claim.equals(other.claim))
            return false;
        return true;
    }
}
