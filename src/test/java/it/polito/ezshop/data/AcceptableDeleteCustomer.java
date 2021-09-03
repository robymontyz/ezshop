package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidCustomerIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

public class AcceptableDeleteCustomer {
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
        //shop.deleteCustomer(customerId);
        shop.logout();
        shop.reset();
    }

    @Test
    public void authTest() throws Exception {
        shop.logout();
        assertThrows(UnauthorizedException.class, () ->
                shop.deleteCustomer(customerId));

        shop.login("admin","ciao");
    }

    @Test
    public void testId() throws Exception {
        assertThrows(InvalidCustomerIdException.class, () ->
                shop.deleteCustomer(-3)
        );
        assertThrows(InvalidCustomerIdException.class, () ->
                shop.deleteCustomer(null)
        );
        assertFalse(shop.deleteCustomer(333));

    }

    @Test
    public void testCorrectCase() throws Exception {
        assertTrue(shop.deleteCustomer(customerId));
    }



}
