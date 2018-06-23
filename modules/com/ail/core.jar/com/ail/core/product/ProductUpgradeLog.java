package com.ail.core.product;

import static javax.persistence.FetchType.LAZY;

import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;

import org.hibernate.annotations.NamedQueries;
import org.hibernate.annotations.NamedQuery;
import org.hibernate.annotations.Parameter;

import com.ail.core.ExceptionRecord;
import com.ail.core.configure.Type;

@Entity
@NamedQueries({
    @NamedQuery(name = "get.sucessful.product.upgrade.by.product.and.command", query = "select pul from ProductUpgradeLog pul where pul.productName = ? and pul.commandName = ? and pul.success = true"),
    @NamedQuery(name = "get.product.upgrade.since", query = "select pul from ProductUpgradeLog pul where pul.runDate >= ?"),
})
public class ProductUpgradeLog extends Type {

    public static final Long NULL_CHECKSUM = -1L;

    private String productName;

    private String commandName;

    private Long checkSum;

    @Column(name = "pulRunDate", columnDefinition = "TIMESTAMP (4) NULL DEFAULT NULL")
    private Date runDate;

    @Column(columnDefinition = "BIT")
    private boolean success;

    @org.hibernate.annotations.Type(type = "com.ail.core.persistence.hibernate.XMLBlob", parameters = @Parameter(name = "type", value = "com.ail.core.ExceptionRecord"))
    @Basic(fetch = LAZY)
    private ExceptionRecord exception = null;

    public ProductUpgradeLog() {
    }

    protected ProductUpgradeLog(String productName, String commandName, Long checkSum, boolean success, Throwable error) {
        super();
        this.productName = productName;
        this.commandName = commandName;
        this.checkSum = checkSum;
        this.success = success;
        this.runDate = new Date();
        if (error != null) {
            this.exception = new ExceptionRecord(error);
        }
    }

    public ProductUpgradeLog(String productName, String commandName) {
        this(productName, commandName, NULL_CHECKSUM, true, null);
    }

    public ProductUpgradeLog(String productName, String commandName, Throwable error) {
        this(productName, commandName, NULL_CHECKSUM, false, error);
    }

    public String getProductName() {
        return productName;
    }

    public String getCommandName() {
        return commandName;
    }

    public Long getCheckSum() {
        return checkSum;
    }

    public Date getRunDate() {
        return runDate;
    }

    public boolean isSuccess() {
        return success;
    }

    public ExceptionRecord getException() {
        return exception;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.typeHashCode();
        result = prime * result + ((checkSum == null) ? 0 : checkSum.hashCode());
        result = prime * result + ((commandName == null) ? 0 : commandName.hashCode());
        result = prime * result + ((productName == null) ? 0 : productName.hashCode());
        result = prime * result + ((runDate == null) ? 0 : runDate.hashCode());
        result = prime * result + ((exception == null) ? 0 : exception.hashCode());
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
        ProductUpgradeLog other = (ProductUpgradeLog) obj;
        if (checkSum == null) {
            if (other.checkSum != null)
                return false;
        } else if (!checkSum.equals(other.checkSum))
            return false;
        if (commandName == null) {
            if (other.commandName != null)
                return false;
        } else if (!commandName.equals(other.commandName))
            return false;
        if (productName == null) {
            if (other.productName != null)
                return false;
        } else if (!productName.equals(other.productName))
            return false;
        if (runDate == null) {
            if (other.runDate != null)
                return false;
        } else if (!runDate.equals(other.runDate))
            return false;
        if (exception == null) {
            if (other.exception != null)
                return false;
        } else if (!exception.equals(other.exception))
            return false;
        return super.typeEquals(obj);
    }

    @Override
    public String toString() {
        return "ProductUpgradeLog [productName=" + productName + ", commandName=" + commandName + ", checkSum=" + checkSum + ", runDate=" + runDate + "]";
    }
}
