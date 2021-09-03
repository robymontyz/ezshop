package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableGetSaleTransaction {

    private it.polito.ezshop.data.EZShop shop;
    private int idSaleTransaction;

    @Before
    public void before() throws Exception{
        shop = new it.polito.ezshop.data.EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
        idSaleTransaction = shop.startSaleTransaction();
        Integer productId=shop.createProductType("Latte","2424242424239",1.0,"Scaduto");
        shop.updatePosition(productId, "123-a-456");
        shop.updateQuantity(productId,4);
        shop.addProductToSale(idSaleTransaction,"2424242424239",3);
        shop.endSaleTransaction(idSaleTransaction);
    }

    @Test
    public void authTest() throws Exception {
        shop.logout();
        assertThrows(UnauthorizedException.class, () ->
                shop.getSaleTransaction(idSaleTransaction));

        shop.login("admin","ciao");
    }

    @Test
    public void testIdCorrect() {
        assertThrows(InvalidTransactionIdException.class, () ->
                shop.getSaleTransaction(null));

        assertThrows(InvalidTransactionIdException.class, () ->
                shop.getSaleTransaction(0));

        assertThrows(InvalidTransactionIdException.class, () ->
                shop.getSaleTransaction(-1));
    }

    @Test
    public void testNoTransactionPresent() throws Exception{
        assertNull(shop.getSaleTransaction(999));
    }

    @Test
    public void testCorrectCase() throws Exception{
        assertNotNull(shop.getSaleTransaction(idSaleTransaction));

    }

    @After
    public void after() throws Exception {
        shop.deleteSaleTransaction(idSaleTransaction);
        shop.logout();
        shop.reset();
    }

}
