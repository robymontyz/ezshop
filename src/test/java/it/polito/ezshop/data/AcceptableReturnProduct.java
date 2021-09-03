package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableReturnProduct {

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
        shop.updateQuantity(idProd,40);
        shop.logout();
        shop.login("23","12345");
        idSaleTransaction = shop.startSaleTransaction();
        shop.addProductToSale(idSaleTransaction,"2424242424239",11);
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
                shop.returnProduct(idReturnTransaction,"",1));

        shop.login("admin","ciao");
    }

    @Test
    public void testInvalidProductCode() {
        assertThrows(InvalidProductCodeException.class, ()-> shop.returnProduct(idReturnTransaction, null, 1));
        assertThrows(InvalidProductCodeException.class,()-> shop.returnProduct(idReturnTransaction,"",1));
        assertThrows(InvalidProductCodeException.class,()-> shop.returnProduct(idReturnTransaction,"11",1));
    }

    @Test
    public void testInvalidQuantity(){
        assertThrows(InvalidQuantityException.class, ()-> shop.returnProduct(idReturnTransaction, "2424242424239", 0));
        assertThrows(InvalidQuantityException.class,()-> shop.returnProduct(idReturnTransaction,"2424242424239",-1));
    }

    @Test
    public void testIdCorrect() {
        assertThrows(InvalidTransactionIdException.class, () ->
                shop.returnProduct(null,"2424242424239",1));

        assertThrows(InvalidTransactionIdException.class, () ->
                shop.returnProduct(0,"2424242424239",1));

        assertThrows(InvalidTransactionIdException.class, () ->
                shop.returnProduct(-1,"2424242424239",1));
    }

    @Test
    public void testNoProductInSaleTransaction() throws Exception {
        assertFalse(shop.returnProduct(idReturnTransaction,"2143325343648",1));
    }

    @Test
    public void testNoProductInProductType() throws Exception{
        assertFalse(shop.returnProduct(idReturnTransaction,"21421342351248",1));
    }

    @Test
    public void testTooHighQuantity() throws Exception{
        assertFalse(shop.returnProduct(idReturnTransaction,"2424242424239",12));
    }

    @Test
    public void testNoReturnTransactionId() throws Exception{
        assertFalse(shop.returnProduct(999,"2424242424239",1));
    }

    @Test
    public void testCorrectCase() throws Exception{
        assertTrue(shop.returnProduct(idReturnTransaction,"2424242424239",2));
    }

}
