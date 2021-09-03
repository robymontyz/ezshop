package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.InvalidDiscountRateException;
import it.polito.ezshop.exceptions.UnauthorizedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableApplyDiscountRateToSale {
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
        shop.updateQuantity(idProd,4);
        shop.logout();
        shop.login("23","12345");
        idSaleTransaction = shop.startSaleTransaction();
        shop.addProductToSale(idSaleTransaction,"2424242424239",3);
    }

    @After
    public void afterEach() {
        shop.logout();
        shop.reset();
    }

    @Test
    public void authTest() throws Exception {
        // no logged user
        shop.logout();
        assertThrows(UnauthorizedException.class, () -> shop.applyDiscountRateToSale(1,0.1));

    }

    @Test
    public void invalidTransactionId() {
        // transactionId 0
        assertThrows(InvalidTransactionIdException.class, () -> shop.applyDiscountRateToSale(0,0.1));
        // transactionId <0
        assertThrows(InvalidTransactionIdException.class, () -> shop.applyDiscountRateToSale(-1,0.1));
        // transactionId null
        assertThrows(InvalidTransactionIdException.class, () -> shop.applyDiscountRateToSale(null,0.1));
    }

    @Test
    public void invalidDiscountRateException()  {
        // discountRate<0
        assertThrows(InvalidDiscountRateException.class, () -> shop.applyDiscountRateToSale(idSaleTransaction,-1.0));
        // 0<discountRate<1
        assertThrows(InvalidDiscountRateException.class, () -> shop.applyDiscountRateToSale(idSaleTransaction,21.2));
        // discountRate=1
        assertThrows(InvalidDiscountRateException.class, () -> shop.applyDiscountRateToSale(idSaleTransaction,1));
    }

    @Test
    public void nonExistingTransaction() throws Exception {
        assertFalse(shop.applyDiscountRateToSale(666,0.1));
    }

    @Test
    public void correctCase() throws Exception {
        assertTrue(shop.applyDiscountRateToSale(idSaleTransaction,0.1));
    }
}
