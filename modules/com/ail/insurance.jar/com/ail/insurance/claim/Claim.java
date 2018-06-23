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

package com.ail.insurance.claim;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static com.ail.core.Functions.isEmpty;
import static com.ail.insurance.claim.RecoveryStatus.UNDEFINED;
import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;
import static javax.persistence.FetchType.EAGER;
import static org.hamcrest.Matchers.is;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Index;
import org.hibernate.annotations.LazyCollection;
import org.hibernate.annotations.LazyCollectionOption;
import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.HasDocuments;
import com.ail.core.HasLabels;
import com.ail.core.HasNotes;
import com.ail.core.HasReferenceNumber;
import com.ail.core.Identified;
import com.ail.core.Note;
import com.ail.core.Owned;
import com.ail.core.document.Document;
import com.ail.core.document.DocumentPlaceholder;
import com.ail.core.document.DocumentType;
import com.ail.financial.CurrencyAmount;
import com.ail.insurance.HasAssets;
import com.ail.insurance.HasClauses;
import com.ail.insurance.policy.AssessmentSheet;
import com.ail.insurance.policy.Asset;
import com.ail.insurance.policy.Clause;
import com.ail.insurance.policy.Policy;
import com.ail.party.HasPartyRoles;
import com.ail.party.PartyRole;
@Audited
@Entity
@TypeDefinition
@NamedQueries({
    @NamedQuery(name = "get.claim.by.claimNumber", query = "select cla from Claim cla where cla.claimNumber = ?"),
    @NamedQuery(name = "get.claim.by.systemId", query = "select cla from Claim cla where cla.systemId = ?"),
    @NamedQuery(name = "get.claim.by.externalSystemId", query = "select cla from Claim cla where cla.externalSystemId = ?")
})
public class Claim extends com.ail.core.Type implements Owned, Identified, HasAssets, HasNotes, HasLabels, HasDocuments, HasClauses, HasReferenceNumber, HasPartyRoles {
    private static final long serialVersionUID = -3990863191815395871L;

    @ManyToOne(fetch = EAGER)
    @JoinColumn(name="policyUIDpol")
    private Policy policy;

    transient private AssessmentSheet assessmentSheet;

    @Enumerated(STRING)
    @Index(name = "status")
    private ClaimStatus status;

    @Index(name = "claimNumber")
    private String claimNumber;

    private String id;

    @Enumerated(STRING)
    private RecoveryStatus recoveryStatus = UNDEFINED;

    @OneToMany(cascade = { ALL }, mappedBy = "claim")
    private List<ClaimSection> section = new ArrayList<>();

    @OneToMany(cascade = ALL)
    private List<Asset> asset = new ArrayList<>();

    @OneToMany(cascade = ALL)
    private List<Note> note = new ArrayList<>();

    @Column(columnDefinition = "BIT")
    private boolean subrogationPotential;

    @Column(columnDefinition = "BIT")
    private boolean subrogationWaiver;

    @Column(columnDefinition = "BIT")
    private boolean paid;

    @Column(columnDefinition = "BIT")
    private boolean litigationFlag;

    @Column(columnDefinition = "BIT", name = "claAdrFlag")
    private boolean adrFlag;

    @OneToMany(cascade = ALL)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<Document> document = new ArrayList<>();

    @OneToMany(cascade = ALL)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<Document> archivedDocument = new ArrayList<>();

    @OneToMany(cascade = ALL)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<DocumentPlaceholder> documentPlaceholder = new ArrayList<>();

    /** Date the claim started. The exact interpretation of "start" is product specific. */
    @Column(name = "claStartDate")
    private Date startDate;

    /** Date the claim ended. The exact interpretation of "end" is product specific. */
    @Column(name = "claEndDate")
    private Date endDate;

    @OneToMany(cascade = ALL)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<PartyRole> partyRole;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "totalEstimatedRecoveryAmount")),  @AttributeOverride(name = "currency", column = @Column(name = "totalEstimatedRecoveryCurrency"))
    })
    private CurrencyAmount totalEstimatedRecovery;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "totalRecoveredAmount")),  @AttributeOverride(name = "currency", column = @Column(name = "totalRecoveredCurrency"))
    })
    private CurrencyAmount totalRecovered;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "outstandingReserveAmount")),  @AttributeOverride(name = "currency", column = @Column(name = "outstandingReserveCurrency"))
    })
    private CurrencyAmount outstandingReserve;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "estimatedReserveAmount")),  @AttributeOverride(name = "currency", column = @Column(name = "estimatedReserveCurrency"))
    })
    private CurrencyAmount estimatedReserve;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "subrogationReserveAmount")),  @AttributeOverride(name = "currency", column = @Column(name = "subrogationReserveCurrency"))
    })
    private CurrencyAmount subrogationReserve;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "salvageReserveAmount")),  @AttributeOverride(name = "currency", column = @Column(name = "salvageReserveCurrency"))
    })
    private CurrencyAmount salvageReserve;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "deductableReserveAmount")),  @AttributeOverride(name = "currency", column = @Column(name = "deductableReserveCurrency"))
    })
    private CurrencyAmount deductableReserve;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "contributionReserveAmount")),  @AttributeOverride(name = "currency", column = @Column(name = "contributionReserveCurrency"))
    })
    private CurrencyAmount contributionReserve;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "amount", column = @Column(name = "outstandingTotalAmount")),  @AttributeOverride(name = "currency", column = @Column(name = "outstandingTotalCurrency"))
    })
    private CurrencyAmount outstandingTotal;

    @Index(name = "owningUser")
    private Long owningUser;

    @ElementCollection
    private Set<String> label;

    @OneToMany(cascade = ALL)
    private List<Clause> clause = new ArrayList<>();

    public Claim() {
    }

    public ClaimStatus getStatus() {
        return status;
    }

    /**
     * Get the claim status as a string (as opposed to an instance of
     * ClaimStatus).
     *
     * @return String representation of the claim status, or null if the status
     *         property has not been set.
     */
    public String getStatusAsString() {
        if (status != null) {
            return status.name();
        }
        return null;
    }

    public void setStatus(ClaimStatus claimStatus) {
        this.status = claimStatus;
    }

    /**
     * Set the claim status as a String. The String must represents a valid claim
     * status. i.e. it must be suitable for a call to ClaimStatus.forName().
     *
     * @see ClaimStatus#forName
     * @param status
     *            New claim status
     * @throws IndexOutOfBoundsException
     *             If status is not a valid claim status.
     */
    public void setStatusAsString(String status) throws IndexOutOfBoundsException {
        this.status = ClaimStatus.valueOf(status);
    }

    public List<ClaimSection> getSection() {
        if (section == null) {
            section = new ArrayList<>();
        }

        return section;
    }

    public void setSection(List<ClaimSection> section) {
        this.section = section;
    }

    public RecoveryStatus getRecoveryStatus() {
        return recoveryStatus;
    }

    public void setRecoveryStatus(RecoveryStatus recoveryStatus) {
        this.recoveryStatus = recoveryStatus;
    }

    public boolean isSubrogationPotential() {
        return subrogationPotential;
    }

    public void setSubrogationPotential(boolean subrogationPotential) {
        this.subrogationPotential = subrogationPotential;
    }

    public boolean isSubrogationWaiver() {
        return subrogationWaiver;
    }

    public void setSubrogationWaiver(boolean subrogationWaiver) {
        this.subrogationWaiver = subrogationWaiver;
    }

    public CurrencyAmount getTotalEstimatedRecovery() {
        return totalEstimatedRecovery;
    }

    public void setTotalEstimatedRecovery(CurrencyAmount totalEstimatedRecovery) {
        this.totalEstimatedRecovery = totalEstimatedRecovery;
    }

    public CurrencyAmount getTotalRecovered() {
        return totalRecovered;
    }

    public void setTotalRecovered(CurrencyAmount totalRecovered) {
        this.totalRecovered = totalRecovered;
    }

    public CurrencyAmount getOutstandingReserve() {
        return outstandingReserve;
    }

    public void setOutstandingReserve(CurrencyAmount outstandingReserve) {
        this.outstandingReserve = outstandingReserve;
    }

    public CurrencyAmount getEstimatedReserve() {
        return estimatedReserve;
    }

    public void setEstimatedReserve(CurrencyAmount estimatedReserve) {
        this.estimatedReserve = estimatedReserve;
    }

    public CurrencyAmount getSubrogationReserve() {
        return subrogationReserve;
    }

    public void setSubrogationReserve(CurrencyAmount subrogationReserve) {
        this.subrogationReserve = subrogationReserve;
    }

    public CurrencyAmount getSalvageReserve() {
        return salvageReserve;
    }

    public void setSalvageReserve(CurrencyAmount salvageReserve) {
        this.salvageReserve = salvageReserve;
    }

    public CurrencyAmount getDeductableReserve() {
        return deductableReserve;
    }

    public void setDeductableReserve(CurrencyAmount deductableReserve) {
        this.deductableReserve = deductableReserve;
    }

    public CurrencyAmount getContributionReserve() {
        return contributionReserve;
    }

    public void setContributionReserve(CurrencyAmount contributionReserve) {
        this.contributionReserve = contributionReserve;
    }

    public boolean isPaid() {
        return paid;
    }

    public void setPaid(boolean paid) {
        this.paid = paid;
    }

    public boolean isLitigationFlag() {
        return litigationFlag;
    }

    public void setLitigationFlag(boolean litigationFlag) {
        this.litigationFlag = litigationFlag;
    }

    public boolean isAdrFlag() {
        return adrFlag;
    }

    public void setAdrFlag(boolean adrFlag) {
        this.adrFlag = adrFlag;
    }

    public CurrencyAmount getOutstandingTotal() {
        return outstandingTotal;
    }

    public void setOutstandingTotal(CurrencyAmount outstandingTotal) {
        this.outstandingTotal = outstandingTotal;
    }

    public Policy getPolicy() {
        return policy;
    }

    public void setPolicy(Policy policy) {
        this.policy = policy;
    }

    public AssessmentSheet getAssessmentSheet() {
        return assessmentSheet;
    }

    public void setAssessmentSheet(AssessmentSheet assessmentSheet) {
        this.assessmentSheet = assessmentSheet;
    }

    public String getClaimNumber() {
        return claimNumber;
    }

    public void setClaimNumber(String claimNumber) {
        this.claimNumber = claimNumber;
    }

    @Override
    public String getReferenceNumber() {
        return !isEmpty(claimNumber) ? claimNumber : super.getReferenceNumber();
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public List<Clause> getClause() {
        if (clause == null) {
            clause = new ArrayList<>();
        }

        return clause;
    }

    @Override
    public void setClause(List<Clause> clause) {
        this.clause = clause;
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

    @Override
    public boolean compareById(Identified that) {
        if (that.getId() != null && this.getId() != null && this.getClass().isAssignableFrom(that.getClass())) {
            return (this.id.equals(that.getId()));
        } else {
            return false;
        }
    }

    @Override
    public Long getOwningUser() {
        return owningUser;
    }

    @Override
    public void setOwningUser(Long owningUserId) {
        this.owningUser = owningUserId;
    }

    @Override
    public List<Asset> getAsset() {
        if (asset == null) {
            asset = new ArrayList<>();
        }

        return asset;
    }

    @Override
    public void setAsset(List<Asset> asset) {
        this.asset = asset;
    }

    @Override
    public void addAsset(Asset asset) {
        getAsset().add(asset);
    }

    @Override
    public void removeAsset(Asset asset) {
        getAsset().remove(asset);
    }

    @Override
    public List<Note> getNote() {
        if (note == null) {
            note = new ArrayList<>();
        }
        return note ;
    }

    @Override
    public void setNote(List<Note> note) {
        this.note = note;
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

    @Override
    public List<DocumentPlaceholder> getDocumentPlaceholder() {
        return this.documentPlaceholder;
    }

    @Override
    public void setDocumentPlaceholder(List<DocumentPlaceholder> documentPlaceholder) {
        this.documentPlaceholder = documentPlaceholder;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.typeHashCode();
        result = prime * result + (adrFlag ? 1231 : 1237);
        result = prime * result + ((asset == null) ? 0 : asset.hashCode());
        result = prime * result + ((claimNumber == null) ? 0 : claimNumber.hashCode());
        result = prime * result + ((clause == null) ? 0 : clause.hashCode());
        result = prime * result + ((contributionReserve == null) ? 0 : contributionReserve.hashCode());
        result = prime * result + ((deductableReserve == null) ? 0 : deductableReserve.hashCode());
        result = prime * result + ((document == null) ? 0 : document.hashCode());
        result = prime * result + ((estimatedReserve == null) ? 0 : estimatedReserve.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + (litigationFlag ? 1231 : 1237);
        result = prime * result + ((note == null) ? 0 : note.hashCode());
        result = prime * result + ((outstandingReserve == null) ? 0 : outstandingReserve.hashCode());
        result = prime * result + ((outstandingTotal == null) ? 0 : outstandingTotal.hashCode());
        result = prime * result + ((owningUser == null) ? 0 : owningUser.hashCode());
        result = prime * result + (paid ? 1231 : 1237);
        result = prime * result + ((partyRole == null) ? 0 : partyRole.hashCode());
        result = prime * result + ((policy == null) ? 0 : policy.hashCode());
        result = prime * result + ((recoveryStatus == null) ? 0 : recoveryStatus.hashCode());
        result = prime * result + ((salvageReserve == null) ? 0 : salvageReserve.hashCode());
        result = prime * result + ((section == null) ? 0 : section.hashCode());
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        result = prime * result + (subrogationPotential ? 1231 : 1237);
        result = prime * result + ((subrogationReserve == null) ? 0 : subrogationReserve.hashCode());
        result = prime * result + (subrogationWaiver ? 1231 : 1237);
        result = prime * result + ((totalEstimatedRecovery == null) ? 0 : totalEstimatedRecovery.hashCode());
        result = prime * result + ((totalRecovered == null) ? 0 : totalRecovered.hashCode());
        result = prime * result + ((startDate == null) ? 0 : startDate.hashCode());
        result = prime * result + ((endDate == null) ? 0 : endDate.hashCode());
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
        Claim other = (Claim) obj;
        if (adrFlag != other.adrFlag)
            return false;
        if (asset == null) {
            if (other.asset != null)
                return false;
        } else if (!asset.equals(other.asset))
            return false;
        if (claimNumber == null) {
            if (other.claimNumber != null)
                return false;
        } else if (!claimNumber.equals(other.claimNumber))
            return false;
        if (clause == null) {
            if (other.clause != null)
                return false;
        } else if (!clause.equals(other.clause))
            return false;
        if (contributionReserve == null) {
            if (other.contributionReserve != null)
                return false;
        } else if (!contributionReserve.equals(other.contributionReserve))
            return false;
        if (deductableReserve == null) {
            if (other.deductableReserve != null)
                return false;
        } else if (!deductableReserve.equals(other.deductableReserve))
            return false;
        if (document == null) {
            if (other.document != null)
                return false;
        } else if (!document.equals(other.document))
            return false;
        if (estimatedReserve == null) {
            if (other.estimatedReserve != null)
                return false;
        } else if (!estimatedReserve.equals(other.estimatedReserve))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (litigationFlag != other.litigationFlag)
            return false;
        if (note == null) {
            if (other.note != null)
                return false;
        } else if (!note.equals(other.note))
            return false;
        if (outstandingReserve == null) {
            if (other.outstandingReserve != null)
                return false;
        } else if (!outstandingReserve.equals(other.outstandingReserve))
            return false;
        if (outstandingTotal == null) {
            if (other.outstandingTotal != null)
                return false;
        } else if (!outstandingTotal.equals(other.outstandingTotal))
            return false;
        if (owningUser == null) {
            if (other.owningUser != null)
                return false;
        } else if (!owningUser.equals(other.owningUser))
            return false;
        if (paid != other.paid)
            return false;
        if (partyRole == null) {
            if (other.partyRole != null)
                return false;
        } else if (!partyRole.equals(other.partyRole))
            return false;
        if (policy == null) {
            if (other.policy != null)
                return false;
        } else if (!policy.equals(other.policy))
            return false;
        if (recoveryStatus != other.recoveryStatus)
            return false;
        if (salvageReserve == null) {
            if (other.salvageReserve != null)
                return false;
        } else if (!salvageReserve.equals(other.salvageReserve))
            return false;
        if (section == null) {
            if (other.section != null)
                return false;
        } else if (!section.equals(other.section))
            return false;
        if (status != other.status)
            return false;
        if (subrogationPotential != other.subrogationPotential)
            return false;
        if (subrogationReserve == null) {
            if (other.subrogationReserve != null)
                return false;
        } else if (!subrogationReserve.equals(other.subrogationReserve))
            return false;
        if (subrogationWaiver != other.subrogationWaiver)
            return false;
        if (totalEstimatedRecovery == null) {
            if (other.totalEstimatedRecovery != null)
                return false;
        } else if (!totalEstimatedRecovery.equals(other.totalEstimatedRecovery))
            return false;
        if (totalRecovered == null) {
            if (other.totalRecovered != null)
                return false;
        } else if (!totalRecovered.equals(other.totalRecovered))
            return false;
        if (startDate == null) {
            if (other.startDate != null)
                return false;
        } else if (!startDate.equals(other.startDate))
            return false;
        if (endDate == null) {
            if (other.endDate != null)
                return false;
        } else if (!endDate.equals(other.endDate))
            return false;
        return super.typeEquals(obj);
    }
}
