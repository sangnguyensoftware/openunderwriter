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
package com.ail.party;

import static ch.lambdaj.Lambda.filter;
import static ch.lambdaj.Lambda.having;
import static ch.lambdaj.Lambda.on;
import static com.ail.core.Functions.isBetween;
import static com.ail.core.Functions.isEmpty;
import static com.ail.party.Address.MAIN_POSTAL_ADDRESS;
import static com.ail.party.EmailAddress.MAIN_EMAIL_ADDRESS;
import static com.ail.party.PhoneNumber.MAIN_PHONE_NUMBER;
import static com.ail.party.PhoneNumber.MOBILE_PHONE_NUMBER;
import static java.util.Arrays.asList;
import static javax.persistence.CascadeType.ALL;
import static org.hamcrest.Matchers.is;
import static org.hibernate.annotations.CascadeType.DETACH;
import static org.hibernate.annotations.CascadeType.MERGE;
import static org.hibernate.annotations.CascadeType.PERSIST;
import static org.hibernate.annotations.CascadeType.REFRESH;
import static org.hibernate.annotations.CascadeType.SAVE_UPDATE;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.OneToMany;

import org.hibernate.annotations.Cascade;
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
import com.ail.core.Identified;
import com.ail.core.Note;
import com.ail.core.Type;
import com.ail.core.document.Document;
import com.ail.core.document.DocumentPlaceholder;
import com.ail.core.document.DocumentType;
import com.ail.financial.PaymentMethod;

@TypeDefinition
@Audited
@Entity
@DiscriminatorColumn(name = "parDSC", discriminatorType = DiscriminatorType.STRING)
@NamedQueries({ @NamedQuery(name = "get.party.by.systemId", query = "select par from Party par where par.systemId = ?"),
        @NamedQuery(name = "get.party.by.externalSystemId", query = "select par from Party par where par.externalSystemId = ?"),
        @NamedQuery(name = "get.parties.by.legalName", query = "select par from Party par where par.legalName like ?"),
        @NamedQuery(name = "get.parties.by.emailAddress", query = "select par from Party par join par.contactSystem csy where csy.class = EmailAddress and csy.endDate = null and csy.emailAddress like ?"),
        @NamedQuery(name = "get.parties.by.postcode", query = "select par from Party par join par.contactSystem csy where csy.class = Address and csy.endDate = null and csy.postcode like ?"),
        @NamedQuery(name = "get.parties.by.mobileNumber", query = "select par from Party par join par.contactSystem csy where csy.class = PhoneNumber and csy.endDate = null and csy.type='"+PhoneNumber.MOBILE_PHONE_NUMBER+"' and csy.phoneNumber like ?")
})
public class Party extends Type implements HasDocuments, HasNotes, HasLabels, Identified, HasPartyRelationships {
    static final long serialVersionUID = -593625795936961828L;
    public static final String PARTY_ROLE_TYPES_LABEL_DISCRIMINATOR = "party_role_types";
    public static final String PARTY_RELATIONSHIP_TYPES_LABEL_DISCRIMINATOR = "relationship_types";

    @Index(name = "partyId")
    private String partyId;

    @Index(name = "legalName")
    private String legalName;

    @OneToMany(cascade = ALL)
    private List<ContactSystem> contactSystem = new ArrayList<>();

    private String id;

    @OneToMany(cascade = ALL)
    private List<Note> note;

    @Enumerated(EnumType.STRING)
    private ContactPreference contactPreference = ContactPreference.UNDEFINED;

    @OneToMany()
    @Cascade({SAVE_UPDATE, DETACH, MERGE, PERSIST, REFRESH})
    private List<PaymentMethod> paymentMethod = new ArrayList<>();

    @ElementCollection
    private Set<String> label;

    @OneToMany(cascade = ALL)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<Document> document = new ArrayList<>();

    @OneToMany(cascade = ALL)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<Document> archivedDocument = new ArrayList<>();


    @OneToMany(cascade = ALL)
    @LazyCollection(LazyCollectionOption.TRUE)
    private List<DocumentPlaceholder> documentPlaceholder = new ArrayList<>();

    @OneToMany()
    @LazyCollection(LazyCollectionOption.TRUE)
    @Cascade({SAVE_UPDATE, DETACH, MERGE, PERSIST, REFRESH})
    private List<PartyRelationship> partyRelationship;

    public Party() {
    }

    public Party(String partyId, String legalName, String emailAddress, String mobilephoneNumber, String telephoneNumber, Address address) {
        super();
        this.partyId = partyId;
        this.legalName = legalName;
        setEmailAddress(emailAddress);
        setMobilephoneNumber(mobilephoneNumber);
        setTelephoneNumber(telephoneNumber);
        setAddress(address);
    }

    /**
     * Fetch the contact systems valid for a specific date, and for classes and
     * types that match specified patterns. This allows, for example, for all
     * Addresses of type "office", "branch", "shop" that are valid on a specific
     * date to be returned. Contacts systems are returned regardless of whether they
     * are primary or not. The list returned is sorted in order of the
     * <code>types</code> specified (left-most first) and then the
     * <code>primary<code> value on the contact system.
     *
     * @param effectiveDate
     *            Date for which results will be returned.
     * @param classes
     *            A Pattern against which the contact systems are matched.
     * @param types
     *            A Pattern against which contact system types are matched.
     * @return List List of Contact systems.
     */
    @SuppressWarnings("unchecked")
    public <T extends ContactSystem> List<T> fetchContactSystems(Date effectiveDate, Pattern classes, Pattern types) {

        List<ContactSystem> results = getContactSystem().
                                      stream().
                                      filter(cs ->
                                          classes.matcher(cs.getClass().getName()).matches() &&
                                          (cs.getType() == null || types.matcher(cs.getType()).matches())  &&
                                          isBetween(cs.getStartDate(), cs.getEndDate(), effectiveDate)
                                      ).
                                      sorted((cs1, cs2) -> {
                                          if (cs1.isPrimary() && !cs2.isPrimary()) {
                                              return -1;
                                          }
                                          if (!cs1.isPrimary() && cs2.isPrimary()) {
                                              return 1;
                                          }
                                          return 0;
                                      }).
                                      collect(Collectors.toList());

        return (List<T>)results;
    }

    /**
     * Fetch the contact systems valid for a specific date, class and list of types.
     * This allows, for example, for all Addresses of type "office", "branch",
     * "shop" that are valid on a specific date to be returned. Contacts systems are
     * returned regardless of whether they are primary or not. The list returned is
     * sorted in order of the <code>types</code> specified (left-most first) and then
     * the <code>primary<code> value on the contact system.
     *
     * @param effectiveDate
     *            Date for which results will be returned.
     * @param clazz
     *            (e.g. {@link PhoneNumber}, {@link Address}, etc.)
     * @param types
     *            List of types to be included.
     * @return List List of Contact systems.
     */
    @SuppressWarnings("unchecked")
    public <T extends ContactSystem> List<T> fetchContactSystems(Date effectiveDate, Class<T> clazz, String... types) {
        List<String> typesList = (types != null && types.length != 0) ? asList(types) : null;

        List<ContactSystem> results = getContactSystem().
                                      stream().
                                      filter(
                                          cs -> clazz.isAssignableFrom(cs.getClass()) &&
                                          (typesList == null || typesList.contains(cs.getType())) &&
                                          isBetween(cs.getStartDate(), cs.getEndDate(), effectiveDate)
                                      ).
                                      sorted((cs1, cs2) -> {
                                          if (typesList != null) {
                                              return typesList.indexOf(cs1.getType()) - typesList.indexOf(cs2.getType());
                                          }
                                          if (cs1.isPrimary() && !cs2.isPrimary()) {
                                              return -1;
                                          }
                                          if (!cs1.isPrimary() && cs2.isPrimary()) {
                                              return 1;
                                          }
                                          return 0;
                                      }).
                                      collect(Collectors.toList());

        return (List<T>)results;
    }

    /**
     * Fetch the primary contact system by effective date, class and type. "primary"
     * is defined by the order of the <code>types</code> list specified (left-most first)
     * and the value of the each ContactSystem's {@link ContactSystem#isPrimary() primary}
     * indicator.
     *
     * @param effectiveDate
     *            Date for which results will be returned.
     * @param clazz
     *            Class of ContactSystem to search for (e.g. {@link PhoneNumber},
     *            {@link Address}, etc.)
     * @param types
     *            Types of ContactSystem (e.g. for a clazz of {@link PhoneNumber},
     *            this might be {@link PhoneNumber#HOME_PHONE_NUMBER
     *            HOME_PHONE_NUMBER}, {@link PhoneNumber#MOBILE_PHONE_NUMBER
     *            MOBILE_PHONE_NUMBER} etc). If multiple types are specified, their
     *            order is treated as an indication of priority. The 1st being the
     *            highest priority. If null then all ContactSystem of the class will
     *            be returned.
     * @return Contact system, or null if non is found.
     */
    public <T extends ContactSystem> Optional<T> fetchContactSystem(Date effectiveDate, Class<T> clazz, String... types) {
        List<T> contact = fetchContactSystems(effectiveDate, clazz, types);

        T result = null;

        if (contact.size() > 0) {
            result = contact.iterator().next();
        }

        return result == null ? Optional.empty() : Optional.of(result);
    }

    /**
     * Fetch the primary ContactSystems of a specified class that is valid for the
     * specified date. This might for example be used to fetch the phone number
     * ContactSystems associated with the party regardless of its type. The result
     * might be a phone number, mobile number, office number or fax number etc.
     *
     * @param effectiveDate
     *            Date for which results will be returned.
     * @param clazz
     *            Class of ContactSystem to search for (e.g. {@link PhoneNumber},
     *            {@link Address}, etc.)
     * @return Contact system, or null if non is found.
     */
    public <T extends ContactSystem> Optional<T> fetchPrimaryContactSystem(Date effectiveDate, Class<T> clazz) {
        return fetchContactSystem(new Date(), clazz, (String[]) null);
    }

    /**
     * Fetch the primary ContactSystem of a specified class that is valid at
     * this moment. This might for example be used to fetch the phone number
     * ContactSystems associated with the party regardless of its type. The result
     * might be a phone number, mobile number, office number or fax number etc.
     *
     * @param effectiveDate
     *            Date for which results will be returned.
     * @param clazz
     *            Class of ContactSystem to search for (e.g. {@link PhoneNumber},
     *            {@link Address}, etc.)
     * @return Contact system, or null if non is found.
     */
    public <T extends ContactSystem> Optional<T> fetchPrimaryContactSystem(Class<T> clazz) {
        return fetchPrimaryContactSystem(new Date(), clazz);
    }

    /**
     * Fetch the primary ContactSystem of a specified class that is valid at
     * this moment of a specific type. This might for example be used to fetch the phone number
     * ContactSystems associated with the party.
     *
     * @param clazz
     *            Class of ContactSystem to search for (e.g. {@link PhoneNumber},
     *            {@link Address}, etc.)
     * @param type
     *            Types of ContactSystem (e.g. for {@link PhoneNumber}, this might be
     *            {@link PhoneNumber#HOME_PHONE_NUMBER HOME_PHONE_NUMBER}. If null
     *            then all ContactSystem of the class will be returned.
     * @return Contact system, or null if non is found.
     */
    public <T extends ContactSystem> Optional<T> fetchContactSystem(Class<T> clazz, String... type) {
        return fetchContactSystem(new Date(), clazz, type);
    }

    /**
     * Add a new contact system (potentially end-dating any existing matching record). If
     * an record exists matching the class and type, it will be end dated. If the record
     * being added is already present, no action is taken. If no start date is defined for
     * the new record, the date/time now will be applied to it.
     * @param contact Contact System to be added.
     */
    public void addContactSystem(ContactSystem contact) {
        if (isEmpty(contact.getType())) {
            throw new IllegalArgumentException("isEmpty(contact.getType)");
        }

        AtomicBoolean alreadyPresent = new  AtomicBoolean();

        getContactSystem().
            stream().
            filter(cs -> contact.getClass().isAssignableFrom(cs.getClass()) && cs.getEndDate() == null).
            forEach(cs -> {
                if (cs.isSameContactAs(contact)) {
                    if (contact.isPrimary()) {
                        cs.setPrimary(true);
                    }
                    alreadyPresent.set(true);
                }
                else if (cs.getType().equals(contact.getType())) {
                    cs.endDate();
                }
                else if (contact.isPrimary()) {
                    cs.setPrimary(false);
                }
            });

        if (!alreadyPresent.get()) {
            if (contact.getStartDate() == null) {
                contact.setStartDate(new Date());
            }
            getContactSystem().add(contact);
        }
    }

    /**
     * Add a ContactSystem to the party and mark it as the primary for its type. It
     * a matching ContactSystem is already defined (i.e. one with the same details)
     * it will be marked as primary and any other ContactSystem of the same class
     * will be set to non-primary. If an old record exist for a ContactSystem of the
     * same class and type, it will be end-dated. If the ContactSystem being added
     * does not specify a startDate, its startDate will be set to now.
     *
     * @param contact ContactSystem to be added.
     */
    public void addPrimaryContactSystem(ContactSystem contact) {
        contact.setPrimary(true);
        addContactSystem(contact);
    }

    /**
     * Fetch the contact systems valid for today with a specified class and list of types.
     * This allows, for example, for all Addresses of type "office", "branch",
     * "shop" that are valid today to be returned. Contacts systems are
     * returned regardless of whether they are primary or not.
     *
     * @param clazz
     *            (e.g. {@link PhoneNumber}, {@link Address}, etc.)
     * @param types
     *            List of types to be included.
     * @return List List of Contact systems.
     */
    public <T extends ContactSystem> List<T> fetchContactSystems(Class<T> clazz, String... types) {
        return fetchContactSystems(new Date(), clazz, types);
    }

    /**
     * Fetch all contact systems that are valid today and are of a specific class
     * regardless of type.
     *
     * @param clazz
     *            (e.g. {@link PhoneNumber}, {@link Address}, etc.)
     * @return List List of Contact systems.
     */
    public <T extends ContactSystem> List<T> fetchContactSystems(Class<T> clazz) {
        return fetchContactSystems(clazz, (String[])null);
    }

    /**
     * Fetch all contact systems valid today regardless of class or type.
     *
     * @return List List of Contact systems.
     */
    public List<ContactSystem> fetchContactSystems() {
        return fetchContactSystems(ContactSystem.class, (String[])null);
    }

    /**
     * Remove (end-date) the specified contact system.
     * @param contact Contact System to be removed
     */
    public void removeContactSystem(ContactSystem contact) {
        getContactSystem().
            stream().
            filter(cs -> cs.equals(contact) && cs.getEndDate() == null).
            forEach(cs -> cs.endDate());
    }

    /**
     * Remove (end-date) the specified contact system. All contact systems that matches
     * the criteria will be end dated. If <code>type</code> is null, all contact systems
     * of the specified class will be end-dated regardless of their type.
     * @param clazz Class of contact system(s) to be removed.
     * @param type The type of contact system(s) to be removed.
     */
    public void removeContactSystem(Class<? extends ContactSystem> clazz, String type) {
        getContactSystem().
            stream().
            filter(cs -> cs.getClass() == clazz && (type==null || cs.getType().equals(type)) && cs.getEndDate() == null).
            forEach(cs -> cs.endDate());
    }

    public String getLegalName() {
        return legalName;
    }

    /**
     * The short name of the party. This defaults to the {@link #legalName} unless overridden by the implementation.
     * @return
     */
    public String getShortName() {
        return getLegalName();
    }

    public void setLegalName(String legalName) {
        this.legalName = legalName;
    }

    public Address getAddress() {
        return fetchPrimaryContactSystem(Address.class).orElse(null);
    }

    public void setAddress(Address address) {
        removeContactSystem(Address.class, MAIN_POSTAL_ADDRESS);
        if (address != null) {
            if (isEmpty(address.getType())) {
                address.setType(MAIN_POSTAL_ADDRESS);
            }
            addContactSystem(address);
        }
    }

    public String getEmailAddress() {
        return fetchContactSystem(EmailAddress.class, MAIN_EMAIL_ADDRESS).map(EmailAddress::getEmailAddress).orElse(null);
    }

    public void setEmailAddress(String emailAddress) {
        removeContactSystem(EmailAddress.class, MAIN_EMAIL_ADDRESS);
        if (emailAddress != null) {
            addContactSystem(new EmailAddress(MAIN_EMAIL_ADDRESS, emailAddress));
        }
    }

    public String getTelephoneNumber() {
        return fetchContactSystem(PhoneNumber.class, MAIN_PHONE_NUMBER).map(PhoneNumber::getPhoneNumber).orElse(null);
    }

    public void setTelephoneNumber(String telephoneNumber) {
        removeContactSystem(PhoneNumber.class, MAIN_PHONE_NUMBER);
        if (telephoneNumber != null) {
            addContactSystem(new PhoneNumber(MAIN_PHONE_NUMBER, telephoneNumber));
        }
    }

    public String getMobilephoneNumber() {
        return fetchContactSystem(PhoneNumber.class, MOBILE_PHONE_NUMBER).map(PhoneNumber::getPhoneNumber).orElse(null);
    }

    public void setMobilephoneNumber(String mobilephoneNumber) {
        removeContactSystem(PhoneNumber.class, MOBILE_PHONE_NUMBER);
        if (mobilephoneNumber != null) {
            addContactSystem(new PhoneNumber(MOBILE_PHONE_NUMBER, mobilephoneNumber));
        }
    }

    public String getPartyId() {
        return partyId;
    }

    public void setPartyId(String partyId) {
        this.partyId = partyId;
    }

    public ContactPreference getContactPreference() {
        return contactPreference;
    }

    public void setContactPreference(ContactPreference contactPreference) {
        this.contactPreference = contactPreference;
    }

    /**
     * Get the party contact preference as a string (as opposed to an instance of
     * ContactPreference).
     *
     * @return String representation of the party contact preference, or null if the contactPreference
     *         property has not been set.
     */
    public String getContactPreferenceAsString() {
        if (contactPreference != null) {
            return contactPreference.name();
        }
        return null;
    }

    public List<PaymentMethod> getPaymentMethod() {
        if (paymentMethod == null) {
            paymentMethod = new ArrayList<>();
        }
        return paymentMethod;
    }

    public void setPaymentMethod(List<PaymentMethod> paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    @Override
    public Set<String> getLabel() {
        if (label == null) {
            label = new HashSet<>();
        }

        return label;
    }

    @Override
    public void setLabel(Set<String> labels) {
        this.label = labels;
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

    /**
     * Get the raw list of contact systems associated with this party. The 'fetch'
     * methods listed below are often more convenient than raw access.
     * @see #fetchContactSystems()
     * @see #fetchContactSystems(Class)
     * @see #fetchContactSystems(Class, String...)
     * @see #fetchContactSystems(Date, Class, String...)
     * @see #fetchPrimaryContactSystem(Class)
     * @see #fetchPrimaryContactSystem(Date, Class)
     * @see #fetchPrimaryContactSystem(Date, Class, String...)
     * @return
     */
    public List<ContactSystem> getContactSystem() {
        if (contactSystem == null) {
            contactSystem = new ArrayList<>();
        }
        return contactSystem;
    }

    public void setContactSystem(List<ContactSystem> contactSystem) {
        this.contactSystem = contactSystem;
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

    @Override
    public List<PartyRelationship> getPartyRelationship() {
        if (partyRelationship == null) {
            partyRelationship = new ArrayList<>();
        }
        return partyRelationship;
    }

    @Override
    public void setPartyRelationship(List<PartyRelationship> partyRelationship) {
        this.partyRelationship = partyRelationship;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((isEmpty(legalName)) ? 0 : legalName.hashCode());
        result = prime * result + ((document == null) ? 0 : document.hashCode());
        result = prime * result + ((label == null) ? 0 : label.hashCode());
        result = prime * result + ((partyId == null) ? 0 : partyId.hashCode());
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        result = prime * result + ((contactPreference == null) ? 0 : contactPreference.hashCode());
        result = prime * result + ((paymentMethod == null) ? 0 : paymentMethod.hashCode());
        result = prime * result + ((partyRelationship == null) ? 0 : partyRelationship.hashCode());
        result = prime * result + ((contactSystem == null) ? 0 : contactSystem.hashCode());
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
        Party other = (Party) obj;
        if (legalName == null) {
            if (other.legalName != null)
                return false;
        } else if (!legalName.equals(other.legalName))
            return false;
        if (label == null) {
            if (other.label != null)
                return false;
        } else if (!label.equals(other.label))
            return false;
        if (partyId == null) {
            if (other.partyId != null)
                return false;
        } else if (!partyId.equals(other.partyId))
            return false;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        if (contactPreference != other.contactPreference)
            return false;
        if (paymentMethod == null) {
            if (other.paymentMethod != null)
                return false;
        } else if (!paymentMethod.equals(other.paymentMethod))
            return false;
        if (contactSystem == null) {
            if (other.contactSystem != null)
                return false;
        } else if (!contactSystem.equals(other.contactSystem))
            return false;
        if (partyRelationship == null) {
            if (other.partyRelationship != null)
                return false;
        } else if (!partyRelationship.equals(other.partyRelationship))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Party [getSystemId()=" + getSystemId() + ", getExternalSystemId()=" + getExternalSystemId() + ", getCreatedDate()=" + getCreatedDate() + ", partyId=" + partyId + ", legalName="
                + legalName + ", contactPreference=" + contactPreference + "]";
    }
}
