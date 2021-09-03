package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.Before;
import org.junit.After;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableGetAllProductTypes {

    private EZShop shop;

    @Before
    public void before() throws Exception
    {
        shop = new EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
        shop.createProductType("Pane","2424242424239",10.0,"Boh");
        shop.logout();
        shop.login("23","12345");
    }

    @After
    public void after(){
        shop.logout();
        shop.reset();
    }

    @Test
    public void testAuthorization() throws Exception {
        shop.logout();
        assertThrows(UnauthorizedException.class, shop::getAllUsers);
    }

    @Test
    public void testCorrectCase() throws Exception {
        assertTrue(shop.getAllProductTypes().size()>0);
    }


}
