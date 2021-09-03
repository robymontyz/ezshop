package it.polito.ezshop.data;


import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableIssueOrder {
    EZShop shop;
    @Before
    public  void before() throws Exception {
        shop=new EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
        shop.createProductType("Latte", "2424242424239", 10.0,"ok");
    }

    @After
    public void after(){
        shop.logout();
        shop.reset();
    }

    @Test
    public void authTest() throws Exception
    {
        shop.logout();
        assertThrows(UnauthorizedException.class, () ->
                shop.issueOrder("2424242424239",5,30.0)
        );
        shop.login("23", "12345");
        assertThrows(UnauthorizedException.class, () ->
                shop.issueOrder("2424242424239",5,30.0)
        );
    }

    @Test
    public void invalidProductCode()
    {
        assertThrows(InvalidProductCodeException.class, () ->
                shop.issueOrder("01010100101010",5,30.0)
        );
    }

    @Test
    public void validProductCodeNotInDB() throws Exception
    {
        assertEquals(-1,shop.issueOrder("652343546457",5,30.0).intValue());
    }

    @Test
    public void invalidPricePerUnit()
    {
        assertThrows(InvalidPricePerUnitException.class, () ->
                shop.issueOrder("2424242424239",5,-30.0)
        );
    }

    @Test
    public void invalidQuantity()
    {
        assertThrows(InvalidQuantityException.class, () ->
                shop.issueOrder("2424242424239",-5,30.0)
        );
        assertThrows(InvalidQuantityException.class, () ->
                shop.issueOrder("2424242424239",0,30.0)
        );
    }

    @Test
    public void validOrder() throws Exception
    {
        Integer id=shop.issueOrder("2424242424239",5,30.0);
        assertTrue(id>0);

    }
}
