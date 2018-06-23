package com.ail.core.encryption;

import com.ail.core.BaseError;
import com.ail.core.BaseException;

public class EncryptorException extends BaseException {

    public EncryptorException(BaseError e) {
        super(e);
    }

    public EncryptorException(String description, Throwable target) {
        super(description, target);
    }

    public EncryptorException(String description) {
        super(description);
    }
}
