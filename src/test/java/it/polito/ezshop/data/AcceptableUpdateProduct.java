package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableUpdateProduct {

    EZShop shop;
    int prodID,prod2;
    @Before
    public void before() throws Exception
    {
        shop=new EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
        prodID=shop.createProductType("Latte", "2424242424239", 1.5, "scaduto");
        prod2=shop.createProductType("Lattosio", "12345678901286", 1.5, "scaduto");

    }

    @After
    public void after() throws UnauthorizedException, InvalidProductIdException {
        shop.logout();
        shop.reset();
    }

    @Test
    public void testAuthorization() throws Exception{
        shop.logout();
        assertThrows(UnauthorizedException.class,()->
                shop.updateProduct(prodID,"Ma boh", "2424242424239", 10.0, "Boh")
        );
        shop.login("23","12345");
        assertThrows(UnauthorizedException.class,()->
                shop.updateProduct(prodID,"Ma boh", "2424242424239", 10.0, "Boh")
        );
    }
    @Test
    public void testProductCode() {

        assertThrows(InvalidProductCodeException.class,()->
                shop.updateProduct(prodID, "asdf", "", 1.6, "la crisi")
        );

        assertThrows(InvalidProductCodeException.class,()->
                shop.updateProduct(prodID, "asdf", null, 1.6, "la crisi")
        );

        assertThrows(InvalidProductCodeException.class, ()->
                shop.updateProduct(prodID, "asdf", "12345678910", 1.6, "la crisi")
        );

    }

    @Test
    public void testPricePerUnit() {
        assertThrows(InvalidPricePerUnitException.class,()->
                shop.updateProduct(prodID, "asdf", "2424242424239", -1.0, "la crisi")
        );
    }

    @Test
    public void testDescription() {
        assertThrows(InvalidProductDescriptionException.class,()->
                shop.updateProduct(prodID, "", "2424242424239", 1.0, "la crisi")
        );
        assertThrows(InvalidProductDescriptionException.class,()->
                shop.updateProduct(prodID, null, "2424242424239", 1.0, "la crisi")
        );

    }

    @Test
    public void testProductId() {
        assertThrows(InvalidProductIdException.class,()-> shop.updateProduct(null, "Latte", "2424242424239", 13.3, "Vecchio"));
        assertThrows(InvalidProductIdException.class,()-> shop.updateProduct(-5,"Latte", "2424242424239", 0.1, "Vecchio" ));
    }

    @Test
    public void testNoProductId() throws Exception{
        assertFalse(shop.updateProduct(4839483, "Latte", "11234567890125", 13.3, "Vecchio"));
    }

    @Test
    public void testCorrectCase() throws Exception{
        assertTrue(shop.updateProduct(prodID, "Pomodori", "826427647372", 13.3, "Echo"));
    }

    @Test
    public void testSameBarcodePresent() throws Exception{
        assertFalse(shop.updateProduct(prod2, "Latte", "2424242424239", 13.3, "Echo"));
    }

    @Test
    public void testValidBarcode() {
        assertTrue(MyProductType.validateProductCode("11234567890125"));
        assertFalse(MyProductType.validateProductCode("12345678901234"));
        assertTrue(MyProductType.validateProductCode("1234567890128"));
        assertTrue(MyProductType.validateProductCode("123456789012"));
    }



}
