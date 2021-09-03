package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableStartReturnTransaction {
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
        shop.updateQuantity(idProd,40);
        shop.logout();
        shop.login("23","12345");
        idSaleTransaction = shop.startSaleTransaction();
        shop.addProductToSale(idSaleTransaction,"2424242424239",11);
        shop.endSaleTransaction(idSaleTransaction);
        shop.receiveCashPayment(idSaleTransaction,1000.0);
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
                shop.startReturnTransaction(idSaleTransaction));

        shop.login("admin","ciao");
    }

    @Test
    public void testIdCorrect() {
        assertThrows(InvalidTransactionIdException.class, () ->
                shop.startReturnTransaction(null));

        assertThrows(InvalidTransactionIdException.class, () ->
                shop.startReturnTransaction(0));

        assertThrows(InvalidTransactionIdException.class, () ->
                shop.startReturnTransaction(-1));
    }

    @Test
    public void testNoTransactionPresent() throws Exception{
        assertSame(-1,shop.startReturnTransaction(999));
    }

    @Test
    public void testCorrectCase() throws Exception{
        int idReturnTransaction= shop.startReturnTransaction(idSaleTransaction);
        assertTrue( idReturnTransaction>0);
        shop.deleteReturnTransaction(idReturnTransaction);
    }
}
