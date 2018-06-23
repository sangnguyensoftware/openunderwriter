package com.ail.core.addressbook;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class AddressService {
    private AddressDAO addressDAO;
    
    @Autowired
    public AddressService(AddressDAO addressDAO) {
        this.addressDAO=addressDAO;
    }

    public AddressDAO getAddressDAO() {
        return addressDAO;
    }
}
