package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableDeleteReturnTransaction {

    private it.polito.ezshop.data.EZShop shop;
    private int idSaleTransaction;
    private int idReturnTransaction;

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
        shop.logout();
        shop.login("23","12345");
        idSaleTransaction = shop.startSaleTransaction();
        shop.addProductToSale(idSaleTransaction,"2424242424239",3);
        shop.endSaleTransaction(idSaleTransaction);
        shop.receiveCashPayment(idSaleTransaction,200.0);
        // adding the product to return
        idReturnTransaction= shop.startReturnTransaction(idSaleTransaction);
        shop.returnProduct(idReturnTransaction,"2424242424239",2);
    }

    @Test
    public void authTest() throws Exception {
        shop.logout();
        assertThrows(UnauthorizedException.class, () ->
                shop.deleteReturnTransaction(idReturnTransaction));

        shop.login("23","12345");
    }

    @Test
    public void testIdCorrect() {
        assertThrows(InvalidTransactionIdException.class, () ->
                shop.deleteReturnTransaction(null));

        assertThrows(InvalidTransactionIdException.class, () ->
                shop.deleteReturnTransaction(0));

        assertThrows(InvalidTransactionIdException.class, () ->
                shop.deleteReturnTransaction(-1));
    }

    @Test
    public void testNoReturnTransactionId() throws Exception{
        assertFalse(shop.deleteReturnTransaction(9999));
    }

    @Test
    public void testCorrectCase() throws Exception{
        assertTrue(shop.deleteReturnTransaction(idReturnTransaction));
        // resetting the status
        idReturnTransaction= shop.startReturnTransaction(idSaleTransaction);
        shop.addProductToSale(idSaleTransaction,"2424242424239",3);
        // adding the product to return
        shop.returnProduct(idReturnTransaction,"2424242424239",2);
    }

    @Test
    public void testPayed() throws Exception{
        shop.endReturnTransaction(idReturnTransaction,true);
        shop.returnCashPayment(idReturnTransaction);
        assertFalse(shop.deleteReturnTransaction(idReturnTransaction));
    }

    @After
    public void after() throws Exception{
        shop.logout();
        shop.reset();
    }

}
