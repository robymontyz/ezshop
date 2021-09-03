package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidCustomerNameException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.junit.Assert.assertThrows;

public class AcceptableDefineCustomer {
    private it.polito.ezshop.data.EZShop shop;

    @Before
    public void before() throws Exception{
        shop = new it.polito.ezshop.data.EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
    }

    @After
    public void after() throws Exception{
        shop.logout();
        shop.reset();
    }

    @Test
    public void authTest() throws Exception {
        shop.logout();
        assertThrows(UnauthorizedException.class, () ->
                shop.defineCustomer("Trump")
        );
        shop.login("admin","ciao");
    }

    @Test
    public void TestCustomerName() {

        assertThrows(InvalidCustomerNameException.class, () ->
                shop.defineCustomer("")
        );

        assertThrows(InvalidCustomerNameException.class, () ->
                shop.defineCustomer(null)
        );
    }

    @Test
    public void TestCorrectCase() throws Exception {
        Integer customerId2 = shop.defineCustomer("Biden");
        assertTrue(customerId2>0);
        shop.deleteCustomer(customerId2);
    }
}
