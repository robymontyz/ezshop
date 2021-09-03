package it.polito.ezshop.data;

import org.junit.*;
import it.polito.ezshop.exceptions.*;

import java.util.List;

import static org.junit.Assert.*;

public class AcceptableGetProductTypesByDescription {
    private EZShop shop;

    @Before
    public void before() throws Exception{
        shop = new EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
        Integer idProd = shop.createProductType("Latte","2424242424239",1.0,"Scaduto");
        shop.updatePosition(idProd,"13-cacca-14");
        shop.updateQuantity(idProd,4);
    }

    @After
    public void after(){
        shop.logout();
        shop.reset();
    }

    @Test
    public void testAuthorization() throws Exception {

        shop.logout();
        assertThrows(UnauthorizedException.class, () ->
                shop.getProductTypesByDescription("Latte")
        );
        shop.login("23", "12345");
        assertThrows(UnauthorizedException.class, () ->
                shop.getProductTypesByDescription("Latte")
        );
    }

    @Test
    public void testValid() throws Exception
    {
        Integer id1 = shop.createProductType("Latte","23452427445635",23.0,"Parzialmente scremato");
        Integer id2 = shop.createProductType("Latte","1231344234229",23.0,"Parzialmente scremato 2");
        List<ProductType> l=shop.getProductTypesByDescription("Latte");
        assertFalse(l.isEmpty());
        shop.deleteProductType(id1);
        shop.deleteProductType(id2);
    }

    @Test
    public void testMultipleValid() throws Exception
    {
        shop.createProductType("Latte","23452427445635",23.0,"Parzialmente scremato");
        List<ProductType> l=shop.getProductTypesByDescription("Latte");
        assertTrue(l.size()>1);
    }

    @Test
    public void testNotValid() throws Exception
    {
        List<ProductType> l=shop.getProductTypesByDescription("Cacca");
        assertTrue(l.isEmpty());
    }
}
