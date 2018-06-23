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
import static ch.lambdaj.Lambda.on;
import static ch.lambdaj.Lambda.sort;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.Basic;
import javax.persistence.Entity;
import javax.persistence.FetchType;

import org.hibernate.annotations.Parameter;
import org.hibernate.annotations.Type;
import org.hibernate.envers.Audited;

import com.ail.annotation.TypeDefinition;
import com.ail.core.ProxyMap;
import com.ail.core.Reference;
import com.ail.financial.CurrencyAmount;
import com.ail.util.Rate;

/**
 * Groups together a collection of assessment lines and provides utility methods for manipulating them.
 */
@Audited
@Entity
@TypeDefinition
public class AssessmentSheet extends com.ail.core.Type {
    private static final long serialVersionUID = 4050081334317365104L;

    public static final String DEFAULT_SHEET_NAME = "default";
    public static final String TOTAL_PREMIUM_LINE_NAME = "total premium";

    @Type(type="com.ail.core.persistence.hibernate.XMLBlob", parameters = @Parameter(name = "type", value = "com.ail.core.ProxyMap"))
    @Basic(fetch = FetchType.LAZY)
    private ProxyMap<String,AssessmentLine> assessmentLines= new ProxyMap<>();

    /**
     * An assessment sheet must be locked to an actor before entries can be made. This allows the sheet to know who is making
     * entries and to associate the entries with that actor.
     */
    private transient String lockingActor=null;

    /**
     * Holds the next priority level to be used by the auto priority generator.
     */
    private int autoPriority=1;

    /**
     * Holds the next order to be used by line ordering
     */
    private int processedOrderCounter=1;

    private transient AssessmentStage assessmentStage;

    public AssessmentSheet() {
    }

    /**
     * Get a map of all the lines in the sheet of a specific type.
     * @param clazz The type to search for.
     * @return Map of lines keyed on Id, this may be an empty table.
     */
    @SuppressWarnings("unchecked")
    public <T> Map<String,T> getLinesOfType(Class<T> clazz, boolean includeDisabled) {
        Map<String,T> ret=new HashMap<>();

        for(AssessmentLine asl: getAssessmentLines().values()) {
            if ((!asl.isDisabled() || includeDisabled) && clazz.isAssignableFrom(asl.getClass())) {
                ret.put(asl.getId(), (T)asl);
            }
        }

        return ret;
    }

    /**
     * Get a map of all the lines in the sheet of a specific type.
     * @param clazz The type to search for.
     * @param includeDisabled Include lines marked as disabled.
     * @return Map of lines keyed on Id, this may be an empty table.
     */
    public <T> Map<String,T> getLinesOfType(Class<T> clazz) {
        return getLinesOfType(clazz, false);
    }

    /**
     * Get a map of all the lines in the sheet of a specific behaviour type.
     * @param behaviourType The type of behaviour to search for.
     * @return Map of lines keyed on Id, this may be an empty table.
     */
    public Map<String,Behaviour> getLinesOfBehaviourType(BehaviourType behaviourType) {
        Map<String,Behaviour> ret=new HashMap<>();

        for(Behaviour b: getLinesOfType(Behaviour.class).values()) {
            if (behaviourType.equals(b.getType()) && !b.isDisabled()) {
                ret.put(b.getId(), b);
            }
        }

        return ret;
    }

    /**
     * Return a list of the assessment notes associated with this sheet. The resulting list is ordered by line.priority.
     * @return List of note lines, this list may be empty but it will not be null.
     */
    public List<AssessmentNote> noteLines() {
        List<AssessmentNote> list=new ArrayList<>(getLinesOfType(AssessmentNote.class).values());
        return sort(list, on(AssessmentNote.class).getPriority());
    }

    /**
     * Return a list of the calculation lines associated with this sheet. The resulting list is ordered by line.priority.
     * @return List of calculation lines, this list may be empty but it will not be null.
     */
    public List<CalculationLine> calculationLines() {
        List<CalculationLine> list=new ArrayList<>(getLinesOfType(CalculationLine.class).values());
        return sort(list, on(CalculationLine.class).getPriority());
    }

    /**
     * Return a list of the marker lines associated with this sheet. The resulting list is ordered by line.priority.
     * @return List of marker lines, this list may be empty but it will not be null.
     */
    public List<Marker> markerLines() {
        return markerLines(false);
    }

    /**
     * Return a list of the marker lines associated with this sheet. The resulting list is ordered by line.priority.
     * @param includeDisabled Include disabled marker lines
     * @return List of marker lines including disabled, this list may be empty but it will not be null.
     */
    public List<Marker> markerLines(boolean includeDisabled) {
        List<Marker> list=new ArrayList<>(getLinesOfType(Marker.class, includeDisabled).values());
        return sort(list, on(Marker.class).getPriority());
    }

    /**
     * Return a list of the behaviour lines associated with this sheet. The resulting list is ordered by line.priority.
     * @return List of behaviour lines, this list may be empty but it will not be null.
     */
    public List<Behaviour> behaviourLines() {
        List<Behaviour> list=new ArrayList<>(getLinesOfType(Behaviour.class).values());
        return sort(list, on(Behaviour.class).getPriority());
    }

    /**
     * Fetch the collection of line objects associated with this instance. The Collection returned includes
     * {@link AssessmentLine#isDisabled() disabled} lines. Use {@link #getEnabledLine getEnabledLine} to fetch
     * only those that are enabled.
     * @return A collection of instances of AssessmentLine.
     */
    public Collection<AssessmentLine> getLine() {
        return getAssessmentLines().values();
    }

    /**
     * Get a list of the assessment lines associated with this sheet as a Map keyed on the line IDs.
     * @return Lines associated with this sheet.
     */
    public Map<String,AssessmentLine> getAssessmentLine() {
        return getAssessmentLines().getMap();
    }

    /**
     * Set the lines associated with this sheet to the values defined in a Map.
     * @param assessmentLine New values.
     */
    public void setAssessmentLine(Map<String,AssessmentLine> assessmentLine) {
        getAssessmentLines().setMap(assessmentLine);
    }

    /**
     * Fetch a collection of all the line objects associated with this instance. The Collection returned excludes
     * {@link AssessmentLine#isDisabled() disabled} lines. Use {@link #getEnabledLine getLine} to fetch
     * all line.
     * @return A collection of instances of AssessmentLine. The list may be empty, but it will never be null.
     */
    public Collection<AssessmentLine> getEnabledLine() {
        Collection<AssessmentLine> ret = new ArrayList<>();

        for (AssessmentLine l : getAssessmentLines().values()) {
            if (!l.isDisabled()) {
                ret.add(l);
            }
        }

        return ret;
    }

    /**
     * Fetch a collection of the {@link AssessmentLine#isDisabled() disabled}
     * line objects associated with this instance.
     * @return A collection of instances of AssessmentLine. The list may be
     *         empty, but it will never be null.
     */
    public Collection<AssessmentLine> getDisabledLine() {
        Collection<AssessmentLine> ret = new ArrayList<>();

        for (AssessmentLine l : getAssessmentLines().values()) {
            if (l.isDisabled()) {
                ret.add(l);
            }
        }

        return ret;
    }

    /**
     * Set the collection of AssessmentLines associated with this sheet.
     * @param line Collection of AssessmentLines.
     */
    public void setLine(Collection<AssessmentLine> line) {
        this.assessmentLines=new ProxyMap<>();

        for(AssessmentLine l: line) {
            getAssessmentLines().put(l.getId(), l);
        }
    }

    /**
     * Fetch a count of the number of line objects associated with this instance.
     * @return Count of line objects. The count includes disabled lines, use {@link #getEnabledLineCount getEnabledLineCount}
     * to get a count of the enabled lines only.
     */
    public int getLineCount() {
        return getAssessmentLines().size();
    }

    /**
     * Fetch a count of the number of line objects associated with this instance. The count excludes disabled lines, use {@link #getLineCount getLineCount}
     * to get a count of all lines.
     * @return Count of line objects.
     */
    public int getEnabledLineCount() {
      int count=0;

      for(AssessmentLine asl: getAssessmentLines().values()) {
        if (!asl.isDisabled()) {
            count++;
        }
      }

      return count;
    }

    /**
     * Fetch a count of the number of disabled line objects associated with this instance.
     * @return Count of line objects.
     */
    public int getDisabledLineCount() {
      int count=0;

      for(AssessmentLine asl: getAssessmentLines().values()) {
        if (asl.isDisabled()) {
            count++;
        }
      }

      return count;
    }

    /**
     * Add a line to the collection associated with this object. If the line being added has an origin (creating actor)
     * of null, then this sheet must be locked to an actor (lockingActor) and that actor will be used as the origin.
     * @param line Instance of AssessmentLine to add.
     * @throws IllegalStateException If line.origin is null and this sheet is not locked to an actor.
     * @throws DuplicateAssessmentLineError If a line with the same id as <code>line</code> is already in the sheet
     * @return The newly added line
     */
    public AssessmentLine addLine(AssessmentLine line) {
        if (line.getOrigin()==null) {
            if (lockingActor==null) {
                throw new IllegalStateException("Atempt to add a line with null origin to an unlocked AssessmentSheet");
            }
            line.setOrigin(lockingActor);
        }

        if (getAssessmentLines().containsKey(line.getId())) {
            throw new DuplicateAssessmentLineError(line.getId());
        }

        if (line.getPriority()==0) {
            line.setPriority(generateAutoPriority());
        }

        getAssessmentLines().put(line.getId(), line);

        return line;
    }

    /**
     * Remove a specific instance of AssessmentLine from the collection associated with this object.
     * @param line Object to be removed from the collection.
     */
    public void removeLine(AssessmentLine line) {
        getAssessmentLines().remove(line.getId());
    }

    /**
     * Disable the specified line. If the line is already disabled this has no effect.
     * @throws IllegalStateException If the specified line does not exist.
     * @param id The id of the line to disable.
     */
    public void disableLine(String id) {
      try {
        AssessmentLine l=(AssessmentLine)getAssessmentLines().get(id);
        l.setDisabled(true);
      } catch(NullPointerException e) {
        throw new IllegalStateException("Line (id='"+id+"') does not exist");
      }
    }

    /**
     * Disable the specified line. If the line is already disabled this has no effect.
     * @param line The line to disable.
     */
    public void disableLine(AssessmentLine line) {
      line.setDisabled(true);
    }

    /**
     * Enable the specified line. If the line is already enabled this has no effect.
     * @throws IllegalStateException If the specified line does not exist.
     * @param id The id of the line to enabled.
     */
    public void enableLine(String id) {
      try {
        AssessmentLine l=(AssessmentLine)getAssessmentLines().get(id);
        l.setDisabled(false);
      } catch(NullPointerException e) {
        throw new IllegalStateException("Line (id='"+id+"') does not exist");
      }
    }

    /**
     * Enable the specified line. If the line is already enabled this has no effect.
     * @param line The line to enable.
     */
    public void enableLine(AssessmentLine line) {
      line.setDisabled(false);
    }

    /**
     * Remove the lines "owned" (or created) by an actor. Each assessment line has an origin(ating) actor - the name
     * of the actor who created the line. That actor may remove the lines later using this method. Lines will be removed
     * even if they are disabled.
     * @param origin Name of the origin to remove lines for
     */
    public void removeLinesByOrigin(String origin) {
        ArrayList<String> delete=new ArrayList<>(); // list of the id's of lines to delete

        // This method may look odd - why not simply iterate through the 'line' hashtable's
        // values deleting the appropriate ones as we go? Well, before JDK1.5 that worked, but
        // since then it throws a 'ConcurrentModificationException' because you can't delete
        // objects from a collection that you're iterating across.

        // check for lines that can be deleted
        for(AssessmentLine l: getAssessmentLines().values()) {
            if (origin.equals(l.getOrigin())) {
                delete.add(l.getId());
            }
        }

        // remove each of the lines
        for(String id: delete) {
            getAssessmentLines().remove(id);
        }
    }

    /**
     * Clear the "fired" indicator on all of the control lines
     * found in this assessment sheet;
     */
    public void resetAssessmentControlLines() {
        for(AssessmentLine l: getAssessmentLines().values()) {
            if (l instanceof ControlLine) {
                ((ControlLine) l).setFired(false);
            }
        }
    }

    /**
     * Fetch a named entry from the sheet. This method will <u>not</u> return
     * {@link AssessmentLine#isDisabled() disabled} lines.
     * @param id The id of the entry to return
     * @return Instance of line for id, or null if id is not known.
     */
    public AssessmentLine findLineById(String id) {
      AssessmentLine l=(AssessmentLine)getAssessmentLines().get(id);

      if (l!=null && l.isDisabled()) {
        return null;
      }
      else {
        return l;
      }
    }

    /**
     * Fetch a named entry from the sheet. This method will <u>not</u> return
     * {@link AssessmentLine#isDisabled() disabled} lines.
     * @param id The id of the entry to return
     * @param clazz Class of line to be returned
     * @return Instance of line for id, or null if id is not known.
     */
    @SuppressWarnings("unchecked")
    public <T> T findLineById(String id, Class<T> clazz) {
        return (T)findLineById(id);
    }

    /**
     * Find and return the resolution for a marker. {@link AssessmentLine#isDisabled() Disabled} lines are
     * ignored by this method.
     * @param id Marker's id
     * @return resolution, or null if the marker is not resolved.
     */
    public MarkerResolution findResolutionByMarkerId(String id) {
        Map<String,MarkerResolution> resolutions=getLinesOfType(MarkerResolution.class);

        for(MarkerResolution markerRes: resolutions.values()) {
            if (markerRes.getRelatesTo()!=null && markerRes.getRelatesTo().getId().equals(id)) {
                return markerRes;
            }
        }

        return null;
    }

    /**
     * Determine if the sheet is marked for decline or not. The
     * sheet is marked for decline if any Markers of type decline
     * are found, and are not resolved. {@link AssessmentLine#isDisabled() Disabled}
     * lines are ignored by this method.
     * @return true if the sheet is marked for decline, false otherwise.
     */
    public boolean isMarkedForDecline() {
        Map<String,Marker> markers=getLinesOfType(Marker.class);

        for(Marker marker: markers.values()) {
            if (marker.getType().equals(MarkerType.DECLINE)
            &&  findResolutionByMarkerId(marker.getId())==null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Determine if the sheet is marked for referral or not. The
     * sheet is marked for referral if any Markers of type refer
     * are found, and are not resolved. {@link AssessmentLine#isDisabled() Disabled}
     * lines are ignored by this method.
     * @return true if the sheet is marked for referral, false otherwise.
     */
    public boolean isMarkedForRefer() {
        Map<String,Marker> markers=getLinesOfType(Marker.class);

        for(Marker marker: markers.values()) {
            if (marker.getType().equals(MarkerType.REFER)
            &&  findResolutionByMarkerId(marker.getId())==null) {
                return true;
            }
        }

        return false;
    }

    public boolean isTotalPremiumDefined() {
        return getTotalPremium() != null;
    }

    public CurrencyAmount getTotalPremium() {
        CalculationLine line = findLineById(TOTAL_PREMIUM_LINE_NAME, CalculationLine.class);

        return (line!=null) ? line.getAmount() : null;
    }

    /**
     * Determine if the sheet includes an unresolved subjectivity. The
     * sheet is marked for subjectivity if any Markers of type {@link Subjectivity subjectivity}
     * are found, that are not resolved. {@link AssessmentLine#isDisabled() Disabled}
     * lines are ignored by this method.
     * @return true if the sheet is marked for decline, false otherwise.
     */
    public boolean isMarkedForSubjectivity() {
        Map<String,Marker> markers=getLinesOfType(Marker.class);

        for(Marker marker: markers.values()) {
            if (marker.getType().equals(MarkerType.SUBJECTIVITY)
            &&  findResolutionByMarkerId(marker.getId())==null) {
                return true;
            }
        }

        return false;
    }

    /**
     * Generate a unique lineId. Each line in an assessment sheet must have a
     * unique id - lines refer to each other using this id. This method makes up
     * a random id, checks that it is not in use and returns it.
     * @return unique (unused) line id
     */
    public synchronized String generateLineId() {
        String id;

        // generate IDs until we find one that isn't being used
        do {
            id="#"+Integer.toHexString(((int)(Math.random()*Integer.MAX_VALUE))).toUpperCase();
        } while (id.length()!=8 && getAssessmentLines().containsKey(id));

        return id;
    }

    /**
     * Return the next auto priority. This method of line prioritization orders lines in the
     * sheet by the order they were added to the sheet.
     * @return next priority;
     */
    public synchronized int generateAutoPriority() {
        return autoPriority++;
    }

    /**
     * Add a rate based loading entry to the sheet. This helper method simply
     * creates a {@link RateBehaviour RateBehaviour} instance with the arguments
     * supplied and adds that instance to the sheet as a new line.<p>
     * @param id The Id to use for this line
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param dependsOn The Id of the line that this on is derived from.
     * @param rate The rate to be used in the calculation.
     * @param priority The priority of this line wrt other lines in this sheet (low value=low priority)
     * @return newly added line
     */
    public AssessmentLine addLoading(String id, String reason, Reference relatesTo, String contributesTo, String dependsOn, Rate rate, int priority) {
        return addLine(new RateBehaviour(id, reason, relatesTo, contributesTo, dependsOn,  BehaviourType.LOAD, rate, priority));
    }

    /**
     * Add a loading with a generated lineId, and the specified arguments.
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param dependsOn The Id of the line that this on is derived from.
     * @param rate The rate to be used in the calculation.
     * @param priority The priority of this line wrt other lines in this sheet (low value=low priority)
     * @return newly added line
     */
    public AssessmentLine addLoading(String reason, Reference relatesTo, String contributesTo, String dependsOn, Rate rate, int priority) {
        return addLoading(generateLineId(), reason, relatesTo, contributesTo, dependsOn, rate, priority);
    }

    /**
     * Add a rate based loading entry to the sheet. This helper method simply
     * creates a {@link RateBehaviour RateBehaviour} instance with the arguments
     * supplied and adds that instance to the sheet as a new line.<p>
     * Note: Lines added using this methods are automatically assigned a priority based on the order they are added.
     * @param id The Id to use for this line
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param dependsOn The Id of the line that this on is derived from.
     * @param rate The rate to be used in the calculation.
     * @return newly added line
     */
    public AssessmentLine addLoading(String id, String reason, Reference relatesTo, String contributesTo, String dependsOn, Rate rate) {
        return addLoading(id, reason, relatesTo, contributesTo, dependsOn, rate, generateAutoPriority());
    }

    /**
     * Add a rate based loading with a generated lineId, and the specified arguments.
     * <br/>Note: Lines added using this methods are automatically assigned a priority based on the order they are added.
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param dependsOn The Id of the line that this on is derived from.
     * @param rate The rate to be used in the calculation.
     * @return newly added line
     */
    public AssessmentLine addLoading(String reason, Reference relatesTo, String contributesTo, String dependsOn, Rate rate) {
        return addLoading(generateLineId(), reason, relatesTo, contributesTo, dependsOn, rate);
    }

    /**
     * Add a rate based loading with a generated lineId, and the specified arguments.
     * <br/>Note: Lines added using this method are automatically assigned a priority based on the order they are added.
     * @param reason Free text reason for this behaviour being created.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param dependsOn The Id of the line that this on is derived from.
     * @param rate The rate to be used in the calculation.
     * @return newly added line
     */
    public AssessmentLine addLoading(String reason, String contributesTo, String dependsOn, Rate rate) {
        return addLoading(reason, (Reference)null, contributesTo, dependsOn, rate);
    }

    /**
     * Add a rate based loading with a generated lineId, and the specified arguments.
     * <br/>Note: Lines added using this method are automatically assigned a priority based on the order they are added.
     * @param id The Id to use for this line
     * @param reason Free text reason for this behaviour being created.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param dependsOn The Id of the line that this on is derived from.
     * @param rate The rate to be used in the calculation.
     * @return newly added line
     */
    public AssessmentLine addLoading(String id, String reason, String contributesTo, String dependsOn, Rate rate) {
        return addLoading(id, reason, null, contributesTo, dependsOn, rate);
    }

    /**
     * Add a fixed sum based loading entry to the sheet. This helper method simply
     * creates a {@link RateBehaviour RateBehaviour} instance with the arguments
     * supplied and adds that instance to the sheet as a new line.
     * @param id The Id to use for this line
     * @param reason Free text reason for this Loading being created.
     * @param relatesTo Optional reference to the part of the policy that caused this Loading.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param currencyAmount The amount to be loaded
     * @param priority The priority of this line wrt other lines in this sheet (low value=low priority)
     * @return newly added line
     */
    public AssessmentLine addLoading(String id, String reason, Reference relatesTo, String contributesTo, CurrencyAmount currencyAmount, int priority) {
        return addLine(new SumBehaviour(id, reason, relatesTo, contributesTo,  BehaviourType.LOAD, currencyAmount, priority));
    }

    /**
     * Add a fixed sum based loading entry to the sheet. This helper method simply
     * creates a {@link RateBehaviour RateBehaviour} instance with the arguments
     * supplied and adds that instance to the sheet as a new line.
     * The line's priority and id are automatically generated.
     * @param reason Free text reason for this Loading being created.
     * @param relatesTo Optional reference to the part of the policy that caused this Loading.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param currencyAmount The amount to be loaded
     * @return newly added line
     */
    public AssessmentLine addLoading(String reason, Reference relatesTo, String contributesTo, CurrencyAmount currencyAmount) {
        return addLoading(generateLineId(), reason, relatesTo, contributesTo, currencyAmount, generateAutoPriority());
    }

    /**
     * Add a fixed sum based loading entry to the sheet. This helper method simply
     * creates a {@link RateBehaviour RateBehaviour} instance with the arguments
     * supplied and adds that instance to the sheet as a new line.
     * The line's priority and id are automatically generated.
     * @param reason Free text reason for this Loading being created.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param currencyAmount The amount to be loaded
     * @return newly added line
     */
    public AssessmentLine addLoading(String reason, String contributesTo, CurrencyAmount currencyAmount) {
        return addLoading(reason, (Reference)null, contributesTo, currencyAmount);
    }

    /**
     * Add a fixed sum based loading entry to the sheet. This helper method simply
     * creates a {@link RateBehaviour RateBehaviour} instance with the arguments
     * supplied and adds that instance to the sheet as a new line.
     * The line's priority and id are automatically generated.
     * @param reason Free text reason for this Loading being created.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param currencyAmount The amount to be loaded
     * @return newly added line
     */
    public AssessmentLine addLoading(String id, String reason, String contributesTo, CurrencyAmount currencyAmount) {
        return addLoading(id, reason, null, contributesTo, currencyAmount, generateAutoPriority());
    }

    /**
     * Add a rate based discount entry to the sheet. This helper method simply
     * creates a {@link RateBehaviour RateBehaviour} instance with the arguments
     * supplied and adds that instance to the sheet as a new line.
     * @param id The Id to use for this line
     * @param reason Free text reason for this discount being created.
     * @param relatesTo Optional reference to the part of the policy that caused this discount.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param dependsOn The Id of the line that this on is derived from.
     * @param rate The rate to be used in the calculation.
     * @param priority The priority of this line wrt other lines in this sheet (low value=low priority)
     * @return newly added line
     */
    public AssessmentLine addDiscount(String id, String reason, Reference relatesTo, String contributesTo, String dependsOn, Rate rate, int priority) {
        return addLine(new RateBehaviour(id, reason, relatesTo, contributesTo, dependsOn,  BehaviourType.DISCOUNT, rate, priority));
    }

    /**
     * Add a rate based discount with a generated line id.
     * @param reason Free text reason for this discount being created.
     * @param relatesTo Optional reference to the part of the policy that caused this discount.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param dependsOn The Id of the line that this on is derived from.
     * @param rate The rate to be used in the calculation.
     * @param priority The priority of this line wrt other lines in this sheet (low value=low priority)
     * @return newly added line
     * @see #addDiscount
     */
    public AssessmentLine addDiscount(String reason, Reference relatesTo, String contributesTo, String dependsOn, Rate rate, int priority) {
        return addDiscount(generateLineId(), reason, relatesTo, contributesTo, dependsOn, rate, priority);
    }

    /**
     * Add a rate based discount with a generated line id and priority.
     * @param reason Free text reason for this discount being created.
     * @param relatesTo Optional reference to the part of the policy that caused this discount.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param dependsOn The Id of the line that this on is derived from.
     * @param rate The rate to be used in the calculation.
     * @return newly added line
     * @see #addDiscount
     */
    public AssessmentLine addDiscount(String reason, Reference relatesTo, String contributesTo, String dependsOn, Rate rate) {
        return addDiscount(reason, relatesTo, contributesTo, dependsOn, rate, generateAutoPriority());
    }

    /**
     * Add a rate based discount with a generated priority and id.
     * @param reason Free text reason for this discount being created.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param dependsOn The Id of the line that this one is derived from.
     * @param rate The rate to be used in the calculation.
     * @return newly added line
     */
    public AssessmentLine addDiscount(String reason, String contributesTo, String dependsOn, Rate rate) {
        return addDiscount(reason, (Reference)null, contributesTo, dependsOn, rate);
    }

    /**
     * Add a rate based discount with a generated priority and id.
     * @param id The Id to use for this line
     * @param reason Free text reason for this discount being created.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param dependsOn The Id of the line that this one is derived from.
     * @param rate The rate to be used in the calculation.
     * @return newly added line
     */
    public AssessmentLine  addDiscount(String id, String reason, String contributesTo, String dependsOn, Rate rate) {
        return addDiscount(id, reason, null, contributesTo, dependsOn, rate, generateAutoPriority());
    }

    /**
     * Add a fixed sum based discount entry to the sheet. This helper method simply
     * creates a {@link RateBehaviour RateBehaviour} instance with the arguments
     * supplied and adds that instance to the sheet as a new line.
     * @param id The Id to use for this line
     * @param reason Free text reason for this discount being created.
     * @param relatesTo Optional reference to the part of the policy that caused this discount.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param currencyAmount The amount to be discounted
     * @param priority The priority of this line wrt other lines in this sheet (low value=low priority)
     * @return newly added line
     */
    public AssessmentLine addDiscount(String id, String reason, Reference relatesTo, String contributesTo, CurrencyAmount currencyAmount, int priority) {
        return addLine(new SumBehaviour(id, reason, relatesTo, contributesTo,  BehaviourType.DISCOUNT, currencyAmount, priority));
    }

    /**
     * Add a fixed sum based discount entry to the sheet. This helper method simply
     * creates a {@link RateBehaviour RateBehaviour} instance with the arguments
     * supplied and adds that instance to the sheet as a new line.
     * The line's priority and id are automatically generated.
     * @param reason Free text reason for this discount being created.
     * @param relatesTo Optional reference to the part of the policy that caused this discount.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param currencyAmount The amount to be discounted
     * @return newly added line
     */
    public AssessmentLine addDiscount(String reason, Reference relatesTo, String contributesTo, CurrencyAmount currencyAmount) {
        return addDiscount(generateLineId(), reason, relatesTo, contributesTo, currencyAmount, generateAutoPriority());
    }

    /**
     * Add a fixed sum based discount entry to the sheet. This helper method simply
     * creates a {@link RateBehaviour RateBehaviour} instance with the arguments
     * supplied and adds that instance to the sheet as a new line.
     * The line's priority and id are automatically generated.
     * @param reason Free text reason for this discount being created.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param currencyAmount The amount to be discounted
     * @return newly added line
     */
    public AssessmentLine addDiscount(String reason, String contributesTo, CurrencyAmount currencyAmount) {
        return addDiscount(reason, (Reference)null, contributesTo, currencyAmount);
    }

    /**
     * Add a fixed sum based discount entry to the sheet. This helper method simply
     * creates a {@link RateBehaviour RateBehaviour} instance with the arguments
     * supplied and adds that instance to the sheet as a new line.
     * The line's priority and id are automatically generated.
     * @param id The Id to use for this line
     * @param reason Free text reason for this discount being created.
     * @param contributesTo The Id of the line that this one contributes to.
     * @param currencyAmount The amount to be discounted
     * @return newly added line
     */
    public AssessmentLine addDiscount(String id, String reason, String contributesTo, CurrencyAmount currencyAmount) {
        return addDiscount(id, reason, null, contributesTo, currencyAmount, generateAutoPriority());
    }

    /**
     * Add a referral line to this sheet. This helper method simply creates a
     * {@link Marker Marker} instance with the arguments supplied and adds it to
     * the sheet as a new line.
     * @param id The Id to use for this line
     * @param reason Free text reason for this referral being created.
     * @param relatesTo Optional reference to the part of the policy that caused referral.
     * @return newly added line
     */
    public AssessmentLine addReferral(String id, String reason, Reference relatesTo) {
        return addLine(new Marker(id, reason, relatesTo, MarkerType.REFER));
    }

    /**
     * Same as addReferral, but automatically generates the line id.
     * @param reason Free text reason for this referral being created.
     * @param relatesTo Optional reference to the part of the policy that caused referral.
     * @return newly added line
     * @see #addReferral
     */
    public AssessmentLine addReferral(String reason, Reference relatesTo) {
        return addReferral(generateLineId(), reason, relatesTo);
    }

    /**
     * Same as addReferral, but automatically generates the line id.
     * @param reason Free text reason for this referral being created.
     * @return newly added line
     * @see #addReferral
     */
    public AssessmentLine addReferral(String reason) {
        return addReferral(reason, null);
    }

    /**
     * Add a decline line to this sheet. This helper method simply creates a
     * {@link Marker Marker} instance with the arguments supplied and adds it to
     * the sheet as a new line.
     * @param id The Id to use for this line
     * @param reason Free text reason for this decline being created.
     * @param relatesTo Optional reference to the part of the policy that caused decline line.
     * @return newly added line
     */
    public AssessmentLine addDecline(String id, String reason, Reference relatesTo) {
        return addLine(new Marker(id, reason, relatesTo, MarkerType.DECLINE));
    }

    /**
     * Same as addDecline, but automatically generates a line id
     * @param reason Free text reason for this decline being created.
     * @param relatesTo Optional reference to the part of the policy that caused decline line.
     * @return newly added line
     * @see #addDecline
     */
    public AssessmentLine addDecline(String reason, Reference relatesTo) {
        return addDecline(generateLineId(), reason, relatesTo);
    }

    /**
     * Same as addDecline, but automatically generates a line id
     * @param reason Free text reason for this decline being created.
     * @return newly added line
     * @see #addDecline
     */
    public AssessmentLine addDecline(String reason) {
        return addDecline(reason, null);
    }

    /**
     * Add a fixed sum line to this sheet. This helper method simply creates a
     * {@link FixedSum FixedSum} instance using the arguments supplied and adds
     * it to the sheet as a new line.
     * <br/>Note: Lines added using this methods are automatically assigned a priority based on the order they are added.
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this one will contribute to.
     * @param amount The amount to be contributed.
     * @return newly added line
     */
    public AssessmentLine addFixedSum(String id, String reason, CurrencyAmount amount) {
        return addLine(new FixedSum(id, reason, amount));
    }

    /**
     * Add a fixed sum line to this sheet. This helper method simply creates a
     * {@link FixedSum FixedSum} instance using the arguments supplied and adds
     * it to the sheet as a new line.
     * <br/>Note: Lines added using this methods are automatically assigned a priority based on the order they are added.
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this one will contribute to.
     * @param amount The amount to be contributed.
     * @return newly added line
     */
    public AssessmentLine addFixedSum(String id, String reason, Reference relatesTo, String contributesTo, CurrencyAmount amount) {
        return addLine(new FixedSum(id, reason, relatesTo, contributesTo, amount));
    }

    /**
     * Add a fixed sum line to this sheet. This helper method simply creates a
     * {@link FixedSum FixedSum} instance using the arguments supplied and adds
     * it to the sheet as a new line.
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this one will contribute to.
     * @param amount The amount to be contributed.#
     * @param priority The priority of this line wrt other lines in this sheet (low value=low priority)
     * @return newly added line
     */
    public AssessmentLine addFixedSum(String id, String reason, Reference relatesTo, String contributesTo, CurrencyAmount amount, int priority) {
        return addLine(new FixedSum(id, reason, relatesTo, contributesTo, amount, priority));
    }

    /**
     * Same as addFixedSum, but generates a line id automatically.
     * <br/>Note: Lines added using this methods are automatically assigned a priority based on the order they are added.
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this one will contribute to.
     * @param amount The amount to be contributed.
     * @return newly added line
     * @see #addFixedSum
     */
    public AssessmentLine addFixedSum(String reason, Reference relatesTo, String contributesTo, CurrencyAmount amount) {
        return addLine(new FixedSum(generateLineId(), reason, relatesTo, contributesTo, amount));
    }

    /**
     * Same as addFixedSum, but generates a line id automatically.
     * <br/>Note: Lines added using this methods are automatically assigned a priority based on the order they are added.
     * @param id This line's Id
     * @param reason Free text reason for this behaviour being created.
     * @param contributesTo The Id of the line that this one will contribute to.
     * @param amount The amount to be contributed.
     * @return newly added line
     * @see #addFixedSum
     */
    public AssessmentLine addFixedSum(String id, String reason, String contributesTo, CurrencyAmount amount) {
        return addLine(new FixedSum(id, reason, null, contributesTo, amount));
    }

    /**
     * Same as addFixedSum, but generates a line id automatically.
     * @param reason Free text reason for this behaviour being created.
     * @param relatesTo Optional reference to the part of the policy that caused this behaviour.
     * @param contributesTo The Id of the line that this one will contribute to.
     * @param amount The amount to be contributed.
     * @param priority The priority of this line wrt other lines in this sheet (low value=low priority)
     * @return newly added line
     * @see #addFixedSum
     */
    public AssessmentLine addFixedSum(String reason, Reference relatesTo, String contributesTo, CurrencyAmount amount, int priority) {
        return addLine(new FixedSum(generateLineId(), reason, relatesTo, contributesTo, amount, priority));
    }

    /**
     * Add an assessment note to this sheet. This helper method simply creates a {@link AssessmentNote AssessmentNote}
     * to the sheet using the reason passed in. The line's ID is automatically generated.
     * @param reason Free text of note.
     * @return newly added line
     * @return newly added line
     * @see #addAssessmentNote
     */
    public AssessmentLine addAssessmentNote(String reason) {
        return addLine(new AssessmentNote(generateLineId(), reason, null));
    }

    /**
     * Add an assessment note to this sheet. This helper method simply creates a {@link AssessmentNote AssessmentNote}
     * to the sheet using the reason and releatesTo values passed in. The line's ID is automatically generated.
     * @param reason Free text of note.
     * @param relatesTo Optional reference to the part of the policy that the note related to (may be null).
     * @return newly added line
     * @return newly added line
     * @see #addAssessmentNote
     */
    public AssessmentLine addAssessmentNote(String reason, Reference relatesTo) {
        return addLine(new AssessmentNote(generateLineId(), reason, relatesTo));
    }

    /**
     * Add an assessment note to this sheet. This helper method simply creates a {@link AssessmentNote AssessmentNote}
     * to the sheet using the reason and releatesTo values passed in. The line's ID is automatically generated.
     * @param id The Id to use for this line
     * @param reason Free text of note.
     * @param relatesTo Optional reference to the part of the policy that the note related to (may be null).
     * @return newly added line
     * @see #addAssessmentNote
     */
    public AssessmentLine addAssessmentNote(String id, String reason, Reference relatesTo) {
        return addLine(new AssessmentNote(id, reason, relatesTo));
    }

    /**
     * Add a totalizer to this sheet. This helper method simply creates a
     * {@link Totalizer Totalizer} to the sheet using the id, reason and
     * releatesTo values passed in. The line's priority is set to follow on from
     * the previous line that was added.
     * @param id The Id to use for this line
     * @param reason Free text of note.
     * @param dependsOn A comma separated list of the IDs of the lines that this one depends on (will sum).
     * @return newly added line
     */
    public AssessmentLine addTotalizer(String id, String reason, String dependsOn) {
        return addLine(new Totalizer(id, reason, dependsOn));
    }

    /**
     * Add an ExpressionLine to this sheet. This helper method simply creates a
     * {@link ExpressionLine ExpressionLine} to the sheet using the id, reason
     * and expression values passed in. The line's priority is set to follow on
     * from the previous line that was added.
     * @param id The Id to use for this line
     * @param reason Free text of note.
     * @param expression Mathematical expression to be evaluated.
     * @return newly added line
     */
    public AssessmentLine addExpressionLine(String id, String reason, String expression) {
        return addLine(new ExpressionLine(id, reason, expression));
    }

    /**
     * Getter returning the value of the lockingActor property. An assessment sheet must be locked to an actor before entries
     * can be made. This allows the sheet to know who is making entries and to associate the entries with that actor.
     * @return Value of the lockingActor property
     */
    public synchronized String getLockingActor() {
        return lockingActor;
    }

    /**
     * Setter to update the value of the lockingActor property. An assessment sheet must be locked to an actor before entries
     * can be made. This allows the sheet to know who is making entries and to associate the entries with that actor.
     * @param lockingActor New value for the lockingActor property
     * @throws IllegalStateException If the sheet is already locked by another actor
     */
    public synchronized void setLockingActor(String lockingActor) {
        if (this.lockingActor!=null
        && !this.lockingActor.equals(lockingActor)
        && !lockingActor.startsWith(this.lockingActor+'.')) {
            throw new IllegalStateException("Attempt to lock locked sheet. Attempt by:"+lockingActor+" already locked by:"+this.lockingActor);
        }
        this.lockingActor = lockingActor;
    }

    /**
     * Unlock the assessment sheet - set locking actor to null.
     */
    public void clearLockingActor() {
        int lastDot=lockingActor.lastIndexOf('.');
        if (lastDot==-1) {
            this.lockingActor=null;
        }
        else {
            this.lockingActor=lockingActor.substring(0, lastDot);
        }
    }

    @Override
    public Object clone() throws CloneNotSupportedException {
        AssessmentSheet that=(AssessmentSheet)super.clone();
        that.setLockingActor(null);
        return that;
    }

    /**
     * When lines are added to the assessment sheet the caller may or may not give them
     * a specific priority (which is used to order the lines during processing). If the
     * caller does not specify a priority, the current value of <code>autoPriority</code>
     * is used for the added line. The autoPriority property is incremented each time it
     * is used, thus meaning that lines have a priority defined by the order in which
     * they are added to the sheet.
     * @return the autoPriority
     */
    public int getAutoPriority() {
        return autoPriority;
    }

    /**
     * @see #getAutoPriority()
     * @param autoPriority the autoPriority to set
     */
    public void setAutoPriority(int autoPriority) {
        this.autoPriority = autoPriority;
    }

    /**
     * Lines in an assessment sheets have an order property (
     * @return
     */
    public int getProcessedOrderCounter() {
        return processedOrderCounter;
    }

    public void setProcessedOrderCounter(int autoProcessedOrderCounter) {
        this.processedOrderCounter = autoProcessedOrderCounter;
    }

    public int getNextProcessOrderIndex() {
        return ++processedOrderCounter;
    }

    /**
     * Get the assessment stage that the sheet is currently in. The process
     * of calculating assessment sheets involves a number of stages. Assessment lines have
     * the option of responding differently depending upon which stage they are invoked in.
     * @return current stage
     */
    public AssessmentStage getAssessmentStage() {
        return assessmentStage;
    }

    /**
     * @see #getAssessmentStage()
     * @param stage
     */
    public void setAssessmentStage(AssessmentStage stage) {
        this.assessmentStage=stage;
    }

    public ProxyMap<String,AssessmentLine> getAssessmentLines() {
        if (assessmentLines == null) {
            assessmentLines = new ProxyMap<>();
        }

        return assessmentLines;
    }

    public void setAssessmentLines(ProxyMap<String,AssessmentLine> assessmentLines) {
        this.assessmentLines = assessmentLines;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((assessmentLines == null) ? 0 : assessmentLines.hashCode());
        result = prime * result + autoPriority;
        result = prime * result + processedOrderCounter;
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
        AssessmentSheet other = (AssessmentSheet) obj;
        if (assessmentLines == null) {
            if (other.assessmentLines != null)
                return false;
        } else if (!assessmentLines.equals(other.assessmentLines))
            return false;
        if (autoPriority != other.autoPriority)
            return false;
        if (processedOrderCounter != other.processedOrderCounter)
            return false;
        return true;
    }
}
