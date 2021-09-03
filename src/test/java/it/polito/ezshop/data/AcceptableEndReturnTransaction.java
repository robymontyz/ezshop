package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableEndReturnTransaction {
    private it.polito.ezshop.data.EZShop shop;
    private int idSaleTransaction;
    private int idReturnTransaction;

    @Before
    public void before() throws Exception{
        shop = new it.polito.ezshop.data.EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
        idSaleTransaction = shop.startSaleTransaction();
        Integer idProd = shop.createProductType("Latte","2424242424239",1.0,"Scaduto");
        shop.updatePosition(idProd,"13-cacca-14");
        shop.updateQuantity(idProd,4);
        shop.logout();
        shop.login("23","12345");
        shop.addProductToSale(idSaleTransaction,"2424242424239",3);
        shop.endSaleTransaction(idSaleTransaction);
        shop.receiveCashPayment(idSaleTransaction, 100);
        idReturnTransaction= shop.startReturnTransaction(idSaleTransaction);
        shop.returnProduct(idReturnTransaction,"2424242424239",2);
    }

    @After
    public void after() throws Exception{
        shop.reset();
        shop.logout();
    }

    @Test
    public void authTest() throws Exception {
        shop.logout();
        assertThrows(UnauthorizedException.class, () ->
                shop.endReturnTransaction(idReturnTransaction,true));

        shop.login("23","12345");
    }

    @Test
    public void testIdCorrect() {
        assertThrows(InvalidTransactionIdException.class, () ->
                shop.endReturnTransaction(null,true));

        assertThrows(InvalidTransactionIdException.class, () ->
                shop.endReturnTransaction(0,true));

        assertThrows(InvalidTransactionIdException.class, () ->
                shop.endReturnTransaction(-1,true));
    }

    @Test
    public void testNoReturnTransactionId() throws Exception{
        assertFalse(shop.endReturnTransaction(9999,true));
    }

    @Test
    public void testCloseAgain() throws Exception{
        int idReturnTransaction2= shop.startReturnTransaction(idSaleTransaction);
        shop.endReturnTransaction(idReturnTransaction2,true);
        assertFalse(shop.endReturnTransaction(idReturnTransaction2,true));
        shop.deleteReturnTransaction(idReturnTransaction2);
    }

    @Test
    public void testCorrectCase() throws Exception{
        assertTrue(shop.endReturnTransaction(idReturnTransaction,true));
    }
}
