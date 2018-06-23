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

import static javax.persistence.CascadeType.ALL;
import static javax.persistence.EnumType.STRING;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToMany;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Identified;
import com.ail.insurance.HasClauses;
import com.ail.util.YesNo;

/**
 * A section with the policy.
 */
@TypeDefinition
@Audited
@Entity
@NamedQueries({
    @NamedQuery(name = "get.section.by.systemId", query = "select sec from Section sec where sec.systemId = ?"),
})
public class Section extends com.ail.core.Type implements Identified, HasClauses {
    static final long serialVersionUID = -1451057528960584717L;

    public static final String DEFAULT_ASSESSMENT_SHEET_NAME = "default";

    @Column(name = "secId") // Looks odd? See OU-1064
    private String ref;

    @Type(type="com.ail.core.persistence.hibernate.XMLBlob", parameters = @Parameter(name = "type", value = "java.util.ArrayList"))
    private List<String> assetId = new ArrayList<>();

    @Type(type="com.ail.core.persistence.hibernate.XMLBlob", parameters = @Parameter(name = "type", value = "java.util.ArrayList"))
    private List<String> uninsuredAssetId = new ArrayList<>();

    @OneToMany(cascade = ALL)
    private List<Coverage> coverage=new ArrayList<>();

    @OneToMany(cascade = ALL)
    private List<Clause> clause=new ArrayList<>();

    @OneToMany(cascade = ALL)
    @MapKeyColumn(name="sheet_name")
    private Map<String,AssessmentSheet> assessmentSheetList=new HashMap<>();

    private String sectionTypeId;

    /** A collection of the wordings associated with this section. */
    @Type(type="com.ail.core.persistence.hibernate.XMLBlob", parameters = @Parameter(name = "type", value = "java.util.ArrayList"))
    private List<Wording> wording = new ArrayList<>();

    /**
     * If a section is "excluded" (this property==Yes), then there will be no sum insured
     * or premium calculated but a warranty will be printed on the proposal form
     * e.g. earthquake excluded.
     */
    @Enumerated(STRING)
    private YesNo excluded;

    /**
     * If a section is included (this property==Yes), then no sum insured is collected or
     * premium calculated but this is an active cover for a claim and is not excluded.
     * The fixed sum insured, if defined, is added into the aggregate limit.
     */
    @Enumerated(STRING)
    private YesNo included;

    /** A collection of the IDs of the excesses associated with this section */
    @Type(type="com.ail.core.persistence.hibernate.XMLBlob", parameters = @Parameter(name = "type", value = "java.util.ArrayList"))
    private List<String> excessId = new ArrayList<>();

    /**
     * Get this section's ID. This ID is unique within a policy.
     * @return Section's ID
     */
    @Override
    public String getId() {
        return ref; // Looks odd? See OU-1064
    }

    public String getRef() {
        return ref;
    }

    /**
     * Set this section's ID.
     * @see #getId
     * @param id Section's ID
     */
    @Override
    public void setId(String id) {
        this.ref = id; // Looks odd? See OU-1064
    }

    public void setRef(String ref) {
        this.ref = ref;
    }

    /**
     * Get the collection of Coverages associated with this section. Coverages exist at both the
     * section and section levels, this method will only return those associated with the section.
     * Use {@link Section#getCoverage} to get those associated with specific sections.
     * @return A collection of Coverages
     */
    public List<Coverage> getCoverage() {
        if (coverage == null) {
            coverage = new ArrayList<>();
        }
        return coverage;
    }

    /**
     * Set the collection of Coverages associated with this section.
     * @see #getCoverage()
     * @param coverage The collection of coverages to be associated with the section.
     */
    public void setCoverage(List<Coverage> coverage) {
        this.coverage = (coverage != null) ? coverage : new ArrayList<>();
    }

    /**
     * Add a coverage to the list associated with this section.
     * @param coverage Coverage to add to the list.
     */
    public void addCoverage(Coverage coverage) {
        getCoverage().add(coverage);
    }

    /**
     * Remove the specified coverage from the list associated with this section.
     * @param coverage Coverage to be removed
     */
    public void removeCoverage(Coverage coverage) {
        getCoverage().remove(coverage);
    }

    /**
     * Fetch the coverage with a specific id from the collection associated with this section.
     * @param coverageId ID of coverage to be returned
     * @return Coverage, or null if no match is found.
     */
    public Coverage getCoverageById(String coverageId) {
        for(Coverage c: getCoverage()) {
            if (c.getId().equals(coverageId)) {
                return c;
            }
        }

        return null;
    }

    /**
     * Fetch the collection of assetId objects associated with this instance.
     * @return A collection of instances of String.
     */
    public List<String> getAssetId() {
        if (assetId == null) {
            assetId = new ArrayList<>();
        }
        return assetId;
    }

    /**
     * Set the collection of instances of String associated with this object.
     * @param assetId A collection of instances of String
     */
    public void setAssetId(List<String> assetId) {
        this.assetId = assetId;
    }

    /**
     * Add a assetId to the collection associated with this object.
     * @param assetId Instance of String to add.
     */
    public void addAssetId(String assetId) {
        getAssetId().add(assetId);
    }

    /**
     * Remove a specific instance of String from the collection associated with this object.
     * @param assetId Object to be removed from the collection.
     */
    public void removeAssetId(String assetId) {
        getAssetId().remove(assetId);
    }

    /**
     * Fetch the collection of uninsuredAssetId objects associated with this instance.
     * @return A collection of instances of String.
     */
    public List<String> getUninsuredAssetId() {
        if (uninsuredAssetId == null) {
            uninsuredAssetId = new ArrayList<>();
        }
        return uninsuredAssetId;
    }

    /**
     * Set the collection of instances of uninsuredAssetId associated with this object.
     * @param uninsuredAssetId A collection of instances of String
     */
    public void setUninsuredAssetId(List<String> uninsuredAssetId) {
        this.uninsuredAssetId = uninsuredAssetId;
    }

    /**
     * Add a uninsuredAssetId to the collection associated with this object.
     * @param uninsuredAssetId Instance of String to add.
     */
    public void addUninsuredAssetId(String uninsuredAssetId) {
        getUninsuredAssetId().add(uninsuredAssetId);
    }

    /**
     * Remove a specific instance of uninsuredAssetId from the collection associated with this object.
     * @param uninsuredAssetId Object to be removed from the collection.
     */
    public void removeUninsuredAssetId(String uninsuredAssetId) {
        getUninsuredAssetId().remove(uninsuredAssetId);
    }

    /**
     * Get the assessment sheet  associated with this section.
     * @return The assessment sheet.
     */
    public AssessmentSheet getAssessmentSheet() {
        return getAssessmentSheetList().get(DEFAULT_ASSESSMENT_SHEET_NAME);
    }

    /**
     * Set the assessment sheet associated with this section.
     * @param sheet The assessment sheet
     */
    public void setAssessmentSheet(AssessmentSheet sheet) {
        getAssessmentSheetList().put(DEFAULT_ASSESSMENT_SHEET_NAME, sheet);
    }

    public AssessmentSheet getAssessmentSheetFor(String name) {
        return getAssessmentSheetList().get(name);
    }

    public void setAssessmentSheetFor(String name, AssessmentSheet sheet) {
        getAssessmentSheetList().put(name, sheet);
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
     * Get the section type id associated with this section. This id is used to associate
     * an instance of a section with the rules used to process it.
     * @return Section type id.
     */
    public String getSectionTypeId() {
        return sectionTypeId;
    }

    /**
     * Set the section type id.
     * @see #getSectionTypeId
     * @param sectionTypeId
     */
    public void setSectionTypeId(String sectionTypeId) {
        this.sectionTypeId = sectionTypeId;
    }

    /**
     * Get the collection of instances of com.ail.insurance.policy.Wording associated with this object.
     * @return wording A collection of instances of Wording
     * @see #setWording
     */
    public List<Wording> getWording() {
        if (wording == null) {
            wording = new ArrayList<>();
        }
        return wording;
    }

    /**
     * Set the collection of instances of com.ail.insurance.policy.Wording associated with this object.
     * @param wording A collection of instances of Wording
     * @see #getWording
     */
    public void setWording(List<Wording> wording) {
        this.wording = wording;
    }

    /**
     * Remove the specified instance of com.ail.insurance.policy.Wording from the list.
     * @param wording Instance to be removed
     */
    public void removeWording(Wording wording) {
        getWording().remove(wording);
    }

    /**
     * Add an instance of com.ail.insurance.policy.Wording to the list associated with this object.
     * @param wording Instance to add to list
     */
    public void addWording(Wording wording) {
        getWording().add(wording);
    }

    /**
     * Getter returning the value of the excluded property. If a section is "excluded" (this property==Yes), then there will
     * be no sum insured or premium calculated but a warranty will be printed on the proposal form e.g. earthquake excluded.
     * @return Value of the excluded property
     */
    public YesNo getExcluded() {
        return excluded;
    }

    /**
     * Setter to update the value of the excluded property. If a section is "excluded" (this property==Yes), then there will
     * be no sum insured or premium calculated but a warranty will be printed on the proposal form e.g. earthquake excluded.
     * @param excluded New value for the excluded property
     */
    public void setExcluded(YesNo excluded) {
        this.excluded = excluded;
    }

    /**
     * Get the value of the excluded property as a string (as opposed to an instance of YesNo).
     * @return String representation of the excluded, or null if the property has not been set.
     */
    public String getExcludedAsString() {
        if (excluded != null) {
            return excluded.name();
        }
        return null;
    }

    /**
     * Set the excluded property as a String. The String must represents a valid
     * YesNo. i.e. it must be suitable for a call to YesNo.forName().
     * @param excluded New value for property.
     * @throws IndexOutOfBoundsException If excluded is not a valid YesNo.
     */
    public void setExcludedAsString(String excluded) throws IndexOutOfBoundsException {
        this.excluded = YesNo.forName(excluded);
    }

    /**
     * Getter returning the value of the included property. If a section is included (this property==Yes), then no sum insured
     * is collected or premium calculated but that is an active cover for a claim and is not excluded. The fixed sum insured,
     * if exists, is added into the aggregate limit.
     * @return Value of the included property
     */
    public YesNo getIncluded() {
        return included;
    }

    /**
     * Setter to update the value of the included property. If a section is included (this property==Yes), then no sum insured
     * is collected or premium calculated but that is an active cover for a claim and is not excluded. The fixed sum insured,
     * if exists, is added into the aggregate limit.
     * @param included New value for the included property
     */
    public void setIncluded(YesNo included) {
        this.included = included;
    }

    /**
     * Get the value of the included property as a string (as opposed to an instance of YesNo).
     * @return String representation of the included, or null if the property has not been set.
     */
    public String getIncludedAsString() {
        if (included != null) {
            return included.name();
        }
        return null;
    }

    /**
     * Set the included property as a String. The String must represents a valid
     * YesNo. i.e. it must be suitable for a call to YesNo.forName().
     * @param included New value for property.
     * @throws IndexOutOfBoundsException If included is not a valid YesNo.
     */
    public void setIncludedAsString(String included) throws IndexOutOfBoundsException {
        this.included = YesNo.forName(included);
    }

    /**
     * Get the collection of instances of String associated with this object.
     * @return excessId A collection of instances of Excess
     * @see #setExcessId
     */
    public List<String> getExcessId() {
        if (excessId == null) {
            excessId = new ArrayList<>();
        }
        return excessId;
    }

    /**
     * Set the collection of instances of String associated with this object.
     * @param excessId A collection of instances of Excess
     * @see #getExcessId
     */
    public void setExcessId(List<String> excessId) {
        this.excessId = excessId;
    }

    /**
     * Remove the specified instance of String from the list.
     * @param excessId Instance to be removed
     */
    public void removeExcessId(String excessId) {
        this.excessId.remove(excessId);
    }

    /**
     * Add an instance of String to the list associated with this object.
     * @param excessId Instance to add to list
     */
    public void addExcessId(String excessId) {
        this.excessId.add(excessId);
    }

    /**
     * Fetch the list of clauses associated with this policy. An empty list may be returned, but a null will never be returned.
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
     * @param clause the clause to set
     */
    @Override
    public void setClause(List<Clause> clause) {
        this.clause = clause;
    }

    @Override
    public boolean compareById(Identified that) {
        if (that.getId() != null && this.getId() != null && this.getClass().isAssignableFrom(that.getClass())) {
            return (this.ref.equals(that.getId()));
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((assessmentSheetList == null) ? 0 : assessmentSheetList.hashCode());
        result = prime * result + ((assetId == null) ? 0 : assetId.hashCode());
        result = prime * result + ((clause == null) ? 0 : clause.hashCode());
        result = prime * result + ((coverage == null) ? 0 : coverage.hashCode());
        result = prime * result + ((excessId == null) ? 0 : excessId.hashCode());
        result = prime * result + ((excluded == null) ? 0 : excluded.hashCode());
        result = prime * result + ((ref == null) ? 0 : ref.hashCode());
        result = prime * result + ((included == null) ? 0 : included.hashCode());
        result = prime * result + ((sectionTypeId == null) ? 0 : sectionTypeId.hashCode());
        result = prime * result + ((uninsuredAssetId == null) ? 0 : uninsuredAssetId.hashCode());
        result = prime * result + ((wording == null) ? 0 : wording.hashCode());
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
        Section other = (Section) obj;
        if (assessmentSheetList == null) {
            if (other.assessmentSheetList != null)
                return false;
        } else if (!assessmentSheetList.equals(other.assessmentSheetList))
            return false;
        if (assetId == null) {
            if (other.assetId != null)
                return false;
        } else if (!assetId.equals(other.assetId))
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
        if (excessId == null) {
            if (other.excessId != null)
                return false;
        } else if (!excessId.equals(other.excessId))
            return false;
        if (excluded != other.excluded)
            return false;
        if (ref == null) {
            if (other.ref != null)
                return false;
        } else if (!ref.equals(other.ref))
            return false;
        if (included != other.included)
            return false;
        if (sectionTypeId == null) {
            if (other.sectionTypeId != null)
                return false;
        } else if (!sectionTypeId.equals(other.sectionTypeId))
            return false;
        if (uninsuredAssetId == null) {
            if (other.uninsuredAssetId != null)
                return false;
        } else if (!uninsuredAssetId.equals(other.uninsuredAssetId))
            return false;
        if (wording == null) {
            if (other.wording != null)
                return false;
        } else if (!wording.equals(other.wording))
            return false;
        return true;
    }
}
