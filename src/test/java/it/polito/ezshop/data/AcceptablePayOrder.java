package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidOrderIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

public class AcceptablePayOrder {

    private it.polito.ezshop.data.EZShop shop;
    private int orderIdPayed, orderIdIssued;

    @Before
    public void before() throws Exception{
        shop = new it.polito.ezshop.data.EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
        Integer idProd = shop.createProductType("Latte","2424242424239",1.0,"Scaduto");
        shop.recordBalanceUpdate(1000);
        orderIdPayed = shop.payOrderFor("2424242424239",1,1.0);
        orderIdIssued= shop.issueOrder("2424242424239",1,1.0);
    }

    @After
    public void after(){
        shop.logout();
        shop.reset();
    }

    @Test
    public void authTest() throws Exception {
        shop.logout();
        assertThrows(UnauthorizedException.class, () ->
                shop.payOrder(1)
        );
        shop.login("23", "12345");
        assertThrows(UnauthorizedException.class, () ->
                shop.payOrder(1)
        );
    }

    @Test
    public void testInvalidOrderId() throws Exception {
        assertFalse(shop.payOrder(300));

        assertThrows(InvalidOrderIdException.class, () ->
                shop.payOrder(-300)
        );
        assertThrows(InvalidOrderIdException.class, () ->
                shop.payOrder(null)
        );
    }

    @Test
    public void testJustPayed() throws Exception {
        assertFalse(shop.payOrder(orderIdPayed));
    }

    @Test
    public void testCorrectCase() throws Exception {
        assertTrue(shop.payOrder(orderIdIssued));
    }
}
