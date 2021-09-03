package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableReturnCashPayment {

    private EZShop shop;
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
        shop.receiveCashPayment(idSaleTransaction,10.0);
        idReturnTransaction= shop.startReturnTransaction(idSaleTransaction);
        shop.returnProduct(idReturnTransaction,"2424242424239",2);
        shop.endReturnTransaction(idReturnTransaction,true);
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
                shop.returnCashPayment(idReturnTransaction));

        shop.login("admin","ciao");
    }

    @Test
    public void testIdCorrect() {
        assertThrows(InvalidTransactionIdException.class, () ->
                shop.returnCashPayment(null));

        assertThrows(InvalidTransactionIdException.class, () ->
                shop.returnCashPayment(0));

        assertThrows(InvalidTransactionIdException.class, () ->
                shop.returnCashPayment(-1));
    }

    @Test
    public void testNoReturnTransactionId() throws Exception{
        assertEquals(-1.0,shop.returnCashPayment(9999),0.1);
    }

    @Test
    public void testNoEnded() throws Exception{
        int idReturnTransaction2= shop.startReturnTransaction(idSaleTransaction);
        // adding the product to return
        shop.returnProduct(idReturnTransaction2,"2424242424239",1);

        shop.returnCashPayment(idReturnTransaction2);
    }

    @Test
    public void testCorrectCase() throws Exception{
        assertEquals(2.0, shop.returnCashPayment(idReturnTransaction),0.00);
    }
}
