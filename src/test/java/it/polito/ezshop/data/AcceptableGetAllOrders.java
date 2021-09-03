package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

public class AcceptableGetAllOrders {

    private it.polito.ezshop.data.EZShop shop;

    @Before
    public void before() throws Exception {
        shop = new it.polito.ezshop.data.EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");

        shop.createProductType("Pane","2424242424239",10.0,"Boh");
        shop.issueOrder("2424242424239",1,1.1);
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
                shop.getAllOrders()
        );
        shop.login("23", "12345");
        assertThrows(UnauthorizedException.class, () ->
                shop.getAllUsers()
        );
    }

    @Test
    public void correctCase() throws Exception {
        List<Order> list= shop.getAllOrders();
        assertFalse(list.isEmpty());
    }


}
