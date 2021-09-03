package it.polito.ezshop.data;

import org.junit.*;

import java.util.ArrayList;

import static org.junit.Assert.*;

public class AcceptableReset {
    EZShop shop;

    @Before
    public void before() throws Exception
    {
        shop = new EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
    }

    @Test
    public void check1() throws Exception{
        assertSame(0,shop.getCreditsAndDebits(null,null).size());
    }

    @Test
    public void check2() throws Exception{
        assertSame(0,shop.getAllProductTypes().size());
    }

    @Test
    public void check3() throws Exception{
        assertSame(0,shop.getAllOrders().size());
    }

    @Test
    public void check4() throws Exception{
        assertTrue(shop.getAllCustomers() instanceof ArrayList);
        assertTrue(shop.getAllUsers() instanceof ArrayList);
    }

    @After
    public void after() throws Exception{
        shop.reset();
    }

}
