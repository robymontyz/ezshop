package it.polito.ezshop.data;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;


public class AcceptableLogout {

    private it.polito.ezshop.data.EZShop shop;

    @Before
    public void before() throws Exception {
        shop=new EZShop();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
    }


    @After
    public void after() throws Exception {
        shop.login("admin","ciao");
        shop.reset();
        shop.logout();
    }

        @Test
        public void testNoLoggedUser() {
            shop= new it.polito.ezshop.data.EZShop();
            assertFalse(shop.logout());
        }

        @Test
        public void testCorrectCase() throws Exception {
            shop= new it.polito.ezshop.data.EZShop();
            shop.login("admin","ciao");
            assertTrue(shop.logout());
        }


}
