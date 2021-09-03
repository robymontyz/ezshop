package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidProductIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableDeleteProductType {

    private EZShop shop;

    @Before
    public void before() throws  Exception{
        shop = new EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
    }

    @After
    public void after() {
        shop.logout();
        shop.reset();
    }

    @Test
    public void testAuthorization() throws Exception{
        shop.logout();
        assertThrows(UnauthorizedException.class,()->
                shop.deleteProductType(1)
        );
        shop.login("23","12345");
        assertThrows(UnauthorizedException.class,()->
                shop.deleteProductType(1)
        );


    }
    @Test
    public void testProductId() throws Exception{


        shop.login("admin","ciao");

        assertThrows(InvalidProductIdException.class,()->
            shop.deleteProductType(-1)
        );

        assertThrows(InvalidProductIdException.class,()->
            shop.deleteProductType(null )
        );

    }

    @Test
    public void testNoIdToDelete() throws Exception{
        shop.login("admin","ciao");
        assertFalse(shop.deleteProductType(432));
    }

    @Test
    public void testCorrectCase() throws Exception{
        shop.login("admin","ciao");
        assertTrue(shop.deleteProductType(shop.createProductType("Latte","653462536237",1.0,"Boh")));
    }
}
