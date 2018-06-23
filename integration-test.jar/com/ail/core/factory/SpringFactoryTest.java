package com.ail.core.factory;

import static org.junit.Assert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.mockito.Mockito.doReturn;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.ail.core.addressbook.AddressService;
import com.ail.core.configure.Type;

public class SpringFactoryTest {

    SpringFactory sut = new SpringFactory();

    @Mock
    private Type typeSpec;

    @Before
    public void setup() {
        MockitoAnnotations.initMocks(this);

        doReturn("com.ail.core.addressbook.AddressService").when(typeSpec).getKey();
        doReturn("addressService").when(typeSpec).getName();
    }

    @Test
    public void test() {
        AddressService as = (AddressService) sut.instantiateType(typeSpec);
        
        assertThat(as, notNullValue());
        assertThat(as.getAddressDAO(), notNullValue());
    }

}
