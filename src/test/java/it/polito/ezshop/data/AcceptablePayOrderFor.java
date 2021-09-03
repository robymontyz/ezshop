package it.polito.ezshop.data;


import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptablePayOrderFor {

    private EZShop shop;


    @Before
    public void before() throws Exception{
        shop = new EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
        shop.createProductType("Latte","2424242424239",1.0,"Scaduto");
        shop.recordBalanceUpdate(1000);
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
                shop.payOrderFor("2424242424239", 5, 30.0)
        );
        shop.login("23", "12345");
        assertThrows(UnauthorizedException.class, () ->
                shop.payOrderFor("2424242424239", 5, 30.0)
        );
    }

    @Test
    public void invalidProductCode()  {
        assertThrows(InvalidProductCodeException.class, () ->
                shop.payOrderFor("01010100101010", 5, 30.0)
        );
    }

    @Test
    public void validProductCodeNotInDB() throws Exception {
        assertSame(-1, shop.payOrderFor("652343546457", 5, 30.0));
    }

    @Test
    public void invalidPricePerUnit()
    {
        assertThrows(InvalidPricePerUnitException.class, () ->
                shop.payOrderFor("2424242424239", 5, -30.0)
        );
    }

    @Test
    public void invalidQuantity()  {
        assertThrows(InvalidQuantityException.class, () ->
                shop.payOrderFor("2424242424239", -5, 30.0)
        );
        assertThrows(InvalidQuantityException.class, () ->
                shop.payOrderFor("2424242424239", 0, 30.0)
        );

    }

    @Test
    public void notEnoughBalanceValidOrder() throws Exception
    {
        assertSame(-1,shop.payOrderFor("2424242424239",5,10000.0));
    }

    @Test
    public void validOrder() throws Exception
    {
        Integer id=shop.payOrderFor("2424242424239",1,1);
        assertTrue(id>0);
    }
}