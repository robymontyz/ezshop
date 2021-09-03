package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableComputePointsForSale {
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
    }

    @After
    public void afterEach() {
        shop.logout();
        shop.reset();
    }

    @Test
    public void authTest() throws Exception {
        shop.logout();
        assertThrows(UnauthorizedException.class, () -> shop.computePointsForSale(1));
    }

    @Test
    public void invalidTransactionId() {
        // transactionId 0
        assertThrows(InvalidTransactionIdException.class, () -> shop.computePointsForSale(0));
        // transactionId <0
        assertThrows(InvalidTransactionIdException.class, () -> shop.computePointsForSale(-1));
        // transactionId null
        assertThrows(InvalidTransactionIdException.class, () -> shop.computePointsForSale(null));
    }

    @Test
    public void nonExistingTransaction() throws Exception {
        assertSame(-1, shop.computePointsForSale(666));
    }

    @Test
    public void correctCase() throws Exception {
        shop.addProductToSale(idSaleTransaction, "2424242424239", 12);
        assertSame(2, shop.computePointsForSale(idSaleTransaction));
    }
}
