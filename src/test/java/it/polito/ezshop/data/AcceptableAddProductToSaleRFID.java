package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableAddProductToSaleRFID {
    EZShop shop;

    @Before
    public void before() throws Exception
    {
        shop = new EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("23","12345");
    }

    @After
    public void after()
    {
        shop.logout();
        shop.reset();
    }

    @Test
    public void authTest() throws Exception
    {
        shop.logout();
        assertThrows(UnauthorizedException.class, () -> shop.addProductToSaleRFID(1,"000000000010"));
        shop.login("23","12345");
    }

    @Test
    public void invalidTransactionId()
    {
        assertThrows(InvalidTransactionIdException.class, () -> shop.addProductToSaleRFID(0,"000000000010"));
        assertThrows(InvalidTransactionIdException.class, () -> shop.addProductToSaleRFID(-1,"000000000010"));
        assertThrows(InvalidTransactionIdException.class, () -> shop.addProductToSaleRFID(null,"000000000010"));
    }

    @Test
    public void invalidFormatRFID(){
        assertThrows(InvalidRFIDException.class, ()-> shop.addProductToSaleRFID(1,"000000001"));
        assertThrows(InvalidRFIDException.class, ()-> shop.addProductToSaleRFID(1,"00000000100"));
        assertThrows(InvalidRFIDException.class, ()-> shop.addProductToSaleRFID(1,"000000001a"));
        assertThrows(InvalidRFIDException.class, ()-> shop.addProductToSaleRFID(1,"0000-00001"));
        assertThrows(InvalidRFIDException.class, ()-> shop.addProductToSaleRFID(1, null));
        assertThrows(InvalidRFIDException.class, ()-> shop.addProductToSaleRFID(1,""));
    }

    @Test
    public void RFIDNotPresent() throws Exception{
        shop.logout();
        shop.login("admin","ciao");
        Integer id = shop.startSaleTransaction();
        Integer idProduct = shop.createProductType("Vino","2424242424239",10.0,"Buono");
        shop.updatePosition(idProduct,"14-Boh-15");
        shop.updateQuantity(idProduct,70);
        shop.logout();
        shop.login("23","12345");
        assertFalse(shop.addProductToSaleRFID(id,"111111111111"));
    }

    @Test
    public void notStartedSaleTransaction() throws Exception
    {
        assertFalse(shop.addProductToSaleRFID(99999,"000000000010"));
    }

    @Test
    public void correctCase() throws Exception
    {
        shop.logout();
        shop.login("admin","ciao");
        Integer id = shop.startSaleTransaction();
        Integer idProduct = shop.createProductType("Vino","2424242424239",10.0,"Buono");
        shop.updatePosition(idProduct,"14-Boh-15");
        //shop.updateQuantity(idProduct,70);
        shop.recordBalanceUpdate(1000.0);
        Integer idOrder = shop.payOrderFor("2424242424239",70,1);
        shop.recordOrderArrivalRFID(idOrder,"000000000010");
        shop.logout();
        shop.login("23","12345");
        assertTrue(shop.addProductToSaleRFID(id,"000000000010"));
    }

    @Test
    public void testMultipleAddSameProductToSale() throws Exception {
        shop.logout();
        shop.login("admin","ciao");
        Integer id = shop.startSaleTransaction();
        Integer idProduct = shop.createProductType("Vino","2424242424239",10.0,"Buono");
        shop.updatePosition(idProduct,"14-Boh-15");
        //shop.updateQuantity(idProduct,70);
        shop.recordBalanceUpdate(1000.0);
        Integer idOrder = shop.payOrderFor("2424242424239",70,1);
        shop.recordOrderArrivalRFID(idOrder,"000000000010");
        shop.logout();
        shop.login("23","12345");
        shop.addProductToSaleRFID(id,"000000000010");
        assertFalse(shop.addProductToSaleRFID(id,"000000000010"));
    }
}
