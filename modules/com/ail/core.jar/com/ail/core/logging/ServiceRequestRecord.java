package com.ail.core.logging;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;

import com.ail.annotation.TypeDefinition;
import com.ail.core.Type;

/**
 * Records the details of HTTP request/responses processed by the RestfulBridge.
 */
@Entity
@TypeDefinition
@NamedQueries({
    @NamedQuery(name = "get.record.by.policy.id", query = "select rec from ServiceRequestRecord rec where rec.externalPolicyId = ?"),
})
public class ServiceRequestRecord extends Type {

    private static final int REQUEST_MAX_LENGHT = 4096;
    private static final int RESPONSE_MAX_LENGHT = 4096;

    @Column(columnDefinition="DATETIME")
    private Timestamp exitTimestamp;

    @Column(columnDefinition="DATETIME")
    private Timestamp entryTimestamp;

    private String product;

    private String command;

    private String externalPolicyId;

    @Column(length = REQUEST_MAX_LENGHT)
    private String request;

    @Column(length = RESPONSE_MAX_LENGHT)
    private String response;

    public ServiceRequestRecord() {
    }

    public ServiceRequestRecord(String product, String command, String externalPolicyId) {
        super();
        this.entryTimestamp = new Timestamp(System.currentTimeMillis());
        this.product = product;
        this.command = command;
        this.externalPolicyId = externalPolicyId;
    }

    public Timestamp getExitTimestamp() {
        return exitTimestamp;
    }

    public void setExitTimestamp(Timestamp date) {
        this.exitTimestamp = date;
    }

    public void setExitTimestamp() {
        setExitTimestamp(new Timestamp(System.currentTimeMillis()));
    }

    public Timestamp getEntryTimestamp() {
        return entryTimestamp;
    }

    public void setEntryTimestamp(Timestamp date) {
        this.entryTimestamp = date;
    }

    public String getRequest() {
        return request;
    }

    public void setRequest(String request) {
        if (request!=null && request.length() >= REQUEST_MAX_LENGHT) {
            this.request = request.substring(0, REQUEST_MAX_LENGHT);
        } else {
            this.request = request;
        }
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        if (response!=null && response.length() >= RESPONSE_MAX_LENGHT) {
            this.response = response.substring(0, RESPONSE_MAX_LENGHT);
        } else {
            this.response = response;
        }
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getCommand() {
        return command;
    }

    public void setCommand(String command) {
        this.command = command;
    }

    public String getExternalPolicyId() {
        return externalPolicyId;
    }

    public void setExternalPolicyId(String externalPolicyId) {
        this.externalPolicyId = externalPolicyId;
    }
}
