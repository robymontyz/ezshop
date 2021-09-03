package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableDeleteSaleTransaction {
    private it.polito.ezshop.data.EZShop shop;
    private int idSaleTransaction;

    @Before
    public void before() throws Exception{
        shop = new it.polito.ezshop.data.EZShop();
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
        assertThrows(UnauthorizedException.class, () -> shop.deleteSaleTransaction(idSaleTransaction));
    }

    @Test
    public void invalidTransactionId() {
        // transactionid 0
        assertThrows(InvalidTransactionIdException.class, () -> shop.deleteSaleTransaction(0));
        // transactionid <0
        assertThrows(InvalidTransactionIdException.class, () -> shop.deleteSaleTransaction(-1));
        // transactionid null
        assertThrows(InvalidTransactionIdException.class, () -> shop.deleteSaleTransaction(null));
    }

    @Test
    public void nonExistingTransaction() throws Exception {
        assertFalse(shop.deleteSaleTransaction(666));
    }

    @Test
    public void transactionAlreadyPayed() throws Exception {
        shop.endSaleTransaction(idSaleTransaction);
        shop.receiveCashPayment(idSaleTransaction, 12.0);

        assertFalse(shop.deleteSaleTransaction(idSaleTransaction));
    }

    @Test
    public void correctCase() throws Exception {
        assertTrue(shop.deleteSaleTransaction(idSaleTransaction));
    }
}
