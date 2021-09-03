package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableDeleteProductFromSaleRFID {
    EZShop shop;
    Integer idSaleTransaction;

    @Before
    public void before() throws Exception {
        shop = new EZShop();

        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
        Integer idProd = shop.createProductType("Latte","2424242424239",1.0,"Scaduto");
        shop.updatePosition(idProd,"13-cacca-14");
        //shop.updateQuantity(idProd,4);
        shop.recordBalanceUpdate(1000.0);
        Integer idOrder = shop.payOrderFor("2424242424239",4,1.0);
        shop.recordOrderArrivalRFID(idOrder,"000000000010");

        shop.logout();
        shop.login("23","12345");
        idSaleTransaction = shop.startSaleTransaction();
        shop.addProductToSaleRFID(idSaleTransaction,"000000000010");
    }

    @After
    public void after() {
        shop.logout();
        shop.reset();
    }

    @Test
    public void authTest() throws Exception {
        // no logged user
        shop.logout();
        assertThrows(UnauthorizedException.class, () -> shop.deleteProductFromSaleRFID(1,"000000000010"));
    }

    @Test
    public void invalidTransactionId()  {
        // transactionid 0
        assertThrows(InvalidTransactionIdException.class, () -> shop.deleteProductFromSaleRFID(0,"000000000010"));
        // transactionid <0
        assertThrows(InvalidTransactionIdException.class, () -> shop.deleteProductFromSaleRFID(-1,"000000000010"));
        // transactionid null
        assertThrows(InvalidTransactionIdException.class, () -> shop.deleteProductFromSaleRFID(null,"000000000010"));
    }

    @Test
    public void invalidFormatRFID(){
        assertThrows(InvalidRFIDException.class, ()-> shop.deleteProductFromSaleRFID(1,"000000001"));
        assertThrows(InvalidRFIDException.class, ()-> shop.deleteProductFromSaleRFID(1,"00000000100"));
        assertThrows(InvalidRFIDException.class, ()-> shop.deleteProductFromSaleRFID(1,"000000001a"));
        assertThrows(InvalidRFIDException.class, ()-> shop.deleteProductFromSaleRFID(1,"0000-00001"));
        assertThrows(InvalidRFIDException.class, ()-> shop.deleteProductFromSaleRFID(1, null));
        assertThrows(InvalidRFIDException.class, ()-> shop.deleteProductFromSaleRFID(1,""));
    }


    @Test
    public void transactionNotOpen() throws Exception {
        shop.endSaleTransaction(idSaleTransaction);
        assertFalse(shop.deleteProductFromSaleRFID(idSaleTransaction,"000000000010"));
    }

    @Test
    public void correctCase() throws Exception {
        assertTrue(shop.deleteProductFromSaleRFID(idSaleTransaction,"000000000010"));
        shop.endSaleTransaction(idSaleTransaction);
        shop.logout();
        shop.login("admin","ciao");
        assertSame(4,shop.getProductTypeByBarCode("2424242424239").getQuantity());
    }
}
