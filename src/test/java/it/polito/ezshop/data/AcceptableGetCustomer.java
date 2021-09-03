package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidCustomerIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableGetCustomer {

    private it.polito.ezshop.data.EZShop shop;
    private int customerId;

    @Before
    public void before() throws Exception{
        shop = new it.polito.ezshop.data.EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
        customerId=shop.defineCustomer("Obama");

    }

    @After
    public void after() throws Exception {
        shop.deleteCustomer(customerId);
        shop.logout();
    }

    @Test
    public void authTest() throws Exception {
        shop.logout();
        assertThrows(UnauthorizedException.class, () ->
                shop.getCustomer(customerId));

        shop.login("admin","ciao");
    }

    @Test
    public void testId() throws Exception {
        assertThrows(InvalidCustomerIdException.class, () ->
                shop.getCustomer(-3)
        );
        assertThrows(InvalidCustomerIdException.class, () ->
                shop.getCustomer(null)
        );
        assertNull(shop.getCustomer(333));

    }

    @Test
    public void testCorrectCase() throws Exception {
        assertEquals("Obama",shop.getCustomer(customerId).getCustomerName());
    }
}
