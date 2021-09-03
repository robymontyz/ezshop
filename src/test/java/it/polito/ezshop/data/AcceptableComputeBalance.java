package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThrows;

public class AcceptableComputeBalance {
    private it.polito.ezshop.data.EZShop shop;


    @Before
    public void before() throws Exception{
        shop = new it.polito.ezshop.data.EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");

    }

    @Test
    public void authTest() throws Exception {
        shop.logout();
        assertThrows(UnauthorizedException.class, () ->
                shop.computeBalance());
        shop.login("23","12345");
        assertThrows(UnauthorizedException.class, () ->
                shop.computeBalance());
        shop.logout();
        shop.login("admin","ciao");
    }

    @Test
    public void testCorrectCase() throws Exception{
        double start = shop.computeBalance();
        shop.recordBalanceUpdate(100);

        assertEquals(start+100, shop.computeBalance(),0.01);
        shop.recordBalanceUpdate(-100);
        assertEquals(start, shop.computeBalance(),0.01);

    }

    @After
    public void after(){
        shop.logout();
        shop.reset();
    }
}
