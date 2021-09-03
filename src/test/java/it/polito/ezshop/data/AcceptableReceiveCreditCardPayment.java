package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidCreditCardException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

public class AcceptableReceiveCreditCardPayment {
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
                shop.receiveCreditCardPayment(idSaleTransaction, "4716258050958645"));
        shop.login("admin","ciao");
    }

    @Test
    public void testIdCorrect() {
        assertThrows(InvalidTransactionIdException.class, () ->
                shop.receiveCreditCardPayment(null,"4716258050958645"));

        assertThrows(InvalidTransactionIdException.class, () ->
                shop.receiveCreditCardPayment(0,"4716258050958645"));

        assertThrows(InvalidTransactionIdException.class, () ->
                shop.receiveCreditCardPayment(-1,"4716258050958645"));
    }

    @Test
    public void testCreditCard(){
        assertThrows(InvalidCreditCardException.class, () ->
                shop.receiveCreditCardPayment(idSaleTransaction,""));
        assertThrows(InvalidCreditCardException.class, () ->
                shop.receiveCreditCardPayment(idSaleTransaction,null));
        assertThrows(InvalidCreditCardException.class, () ->
                shop.receiveCreditCardPayment(idSaleTransaction,"4716258000958645"));
    }

    @Test
    public void testNoSaleTransactionId() throws Exception{
        assertFalse(shop.receiveCreditCardPayment(9999, "4716258050958645"));
    }

    @Test
    public void testCorrectCase() throws Exception{
        assertTrue(shop.receiveCreditCardPayment(idSaleTransaction,"4716258050958645"));
    }

}
