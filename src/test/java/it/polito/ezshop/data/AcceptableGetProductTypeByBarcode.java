package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.UnauthorizedException;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableGetProductTypeByBarcode {
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
    @Test
    public void testAuthorization() throws Exception{
        shop.logout();
        assertThrows(UnauthorizedException.class,()->
                shop.getProductTypeByBarCode("2424242424239")
        );
        shop.login("23","12345");
        assertThrows(UnauthorizedException.class,()->
                shop.getProductTypeByBarCode("2424242424239")
        );

    }

    @Test
    public void testProductCode(){



        assertThrows(InvalidProductCodeException.class,()-> shop.getProductTypeByBarCode(""));

        assertThrows(InvalidProductCodeException.class,()-> shop.getProductTypeByBarCode(null));

        assertThrows(InvalidProductCodeException.class,()-> shop.getProductTypeByBarCode("123345657686542"));

        assertThrows(InvalidProductCodeException.class,()-> shop.getProductTypeByBarCode("1dasdadsasdas"));

        assertThrows(InvalidProductCodeException.class,()-> shop.getProductTypeByBarCode("aaa1232143214421"));
    }

    @Test
    public void testNoProduct() throws Exception{


        assertNull(shop.getProductTypeByBarCode("1234565432436"));

    }

    @Test
    public void testCorrectCase() throws Exception{

        shop.login("admin","ciao");
        assertTrue(shop.getProductTypeByBarCode("2424242424239") instanceof MyProductType);

    }

    @After
    public void after() throws Exception{

        shop.logout();
        shop.reset();
    }

}
