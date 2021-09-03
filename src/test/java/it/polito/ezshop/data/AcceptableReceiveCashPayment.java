package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidPaymentException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableReceiveCashPayment {

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
    }

    @After
    public void after() throws Exception{
        shop.logout();
        shop.reset();
    }

    @Test
    public void authTest() throws Exception {
        shop.logout();
        assertThrows(UnauthorizedException.class, () ->
                shop.receiveCashPayment(idSaleTransaction, 100));
        shop.login("23","12345");
    }

    @Test
    public void testIdCorrect() {
        assertThrows(InvalidTransactionIdException.class, () ->
                shop.receiveCashPayment(null,100.0));

        assertThrows(InvalidTransactionIdException.class, () ->
                shop.receiveCashPayment(0,100.0));

        assertThrows(InvalidTransactionIdException.class, () ->
                shop.receiveCashPayment(-1,100.0));
    }

    @Test
    public void testInvalidPayment(){
        assertThrows(InvalidPaymentException.class, () ->
                shop.receiveCashPayment(idSaleTransaction,-100.0));
        assertThrows(InvalidPaymentException.class, () ->
                shop.receiveCashPayment(idSaleTransaction,0));
    }

    @Test
    public void testSaleDoesNotExist() throws Exception{
       assertTrue(shop.receiveCashPayment(999,100.0)<0);
    }

    @Test
    public void testNoMoney() throws Exception{
        assertTrue(shop.receiveCashPayment(idSaleTransaction,0.001)<0);
    }

    @Test
    public void testCorrectCase() throws Exception{
        assertEquals(1.0, shop.receiveCashPayment(idSaleTransaction,12.0),0.00);
    }
}
