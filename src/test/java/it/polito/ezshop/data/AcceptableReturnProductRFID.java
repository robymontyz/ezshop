package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidRFIDException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableReturnProductRFID {
    EZShop shop;
    Integer idSaleTransaction,idReturnTransaction;

    @Before
    public void before() throws Exception {
        shop = new EZShop();

        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
        Integer idProd = shop.createProductType("Latte","2424242424239",1.0,"Scaduto");
        shop.updatePosition(idProd,"13-cacca-14");
        //shop.updateQuantity(idProd,40);
        shop.recordBalanceUpdate(1000.0);
        Integer idOrder = shop.payOrderFor("2424242424239",4,1.0);
        shop.recordOrderArrivalRFID(idOrder,"000000000010");
        shop.logout();
        shop.login("23","12345");
        idSaleTransaction = shop.startSaleTransaction();
        shop.addProductToSaleRFID(idSaleTransaction,"000000000010");
        shop.endSaleTransaction(idSaleTransaction);
        shop.receiveCashPayment(idSaleTransaction,1000.0);
        idReturnTransaction=shop.startReturnTransaction(idSaleTransaction);
    }

    @After
    public void after() throws Exception {
        shop.reset();
        shop.logout();
    }

    @Test
    public void authTest() throws Exception {
        shop.logout();
        assertThrows(UnauthorizedException.class, () ->
                shop.returnProductRFID(idReturnTransaction,"000000000010"));

        shop.login("admin","ciao");
    }

    @Test
    public void testInvalidRFID() {
        assertThrows(InvalidRFIDException.class, ()-> shop.returnProductRFID(idReturnTransaction, null));
        assertThrows(InvalidRFIDException.class, ()-> shop.returnProductRFID(idReturnTransaction,""));
        assertThrows(InvalidRFIDException.class, ()-> shop.returnProductRFID(idReturnTransaction,"11"));
        assertThrows(InvalidRFIDException.class, ()-> shop.returnProductRFID(1,"000000001"));
        assertThrows(InvalidRFIDException.class, ()-> shop.returnProductRFID(1,"00000000100"));
        assertThrows(InvalidRFIDException.class, ()-> shop.returnProductRFID(1,"000000001a"));
        assertThrows(InvalidRFIDException.class, ()-> shop.returnProductRFID(1,"0000-00001"));
    }

    @Test
    public void testInvalidQuantity() throws Exception {
        shop.returnProductRFID(idReturnTransaction, "000000000010");
        assertFalse(shop.returnProductRFID(idReturnTransaction, "000000000010"));
    }

    @Test
    public void testIdCorrect() {
        assertThrows(InvalidTransactionIdException.class, () ->
                shop.returnProductRFID(null,"000000000010"));

        assertThrows(InvalidTransactionIdException.class, () ->
                shop.returnProductRFID(0,"000000000010"));

        assertThrows(InvalidTransactionIdException.class, () ->
                shop.returnProductRFID(-1,"000000000010"));
    }

    @Test
    public void testNoRFIDInSaleTransaction() throws Exception {
        assertFalse(shop.returnProductRFID(idReturnTransaction,"000000000111"));
    }

    @Test
    public void testNoReturnTransactionId() throws Exception{
        assertFalse(shop.returnProductRFID(999,"000000000010"));
    }

    @Test
    public void testCorrectCase() throws Exception{
        assertTrue(shop.returnProductRFID(idReturnTransaction,"000000000010"));
    }

    @Test
    public void testMultipleReturnTransaction() throws Exception {
        shop.returnProductRFID(idReturnTransaction,"000000000010");
        shop.endReturnTransaction(idReturnTransaction, true);
        int idReturnTransaction2 = shop.startReturnTransaction(idSaleTransaction);
        assertFalse(shop.returnProductRFID(idReturnTransaction2,"000000000010"));
    }

    @Test
    public void testMultipleReturnSameRFID() throws Exception {
        shop.returnProductRFID(idReturnTransaction,"000000000010");
        assertFalse(shop.returnProductRFID(idReturnTransaction,"000000000010"));
    }
}
