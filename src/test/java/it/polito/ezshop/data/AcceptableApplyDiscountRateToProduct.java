package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidDiscountRateException;
import it.polito.ezshop.exceptions.UnauthorizedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableApplyDiscountRateToProduct {
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
    public void after() {
        shop.logout();
        shop.reset();
    }

    @Test
    public void authTest() throws Exception {
        // no logged user
        shop.logout();
        assertThrows(UnauthorizedException.class, () -> shop.applyDiscountRateToProduct(1,"2424242424239",0.1));
    }

    @Test
    public void invalidTransactionId() {
        // transactionId 0
        assertThrows(InvalidTransactionIdException.class, () -> shop.applyDiscountRateToProduct(0,"2424242424239",0.1));
        // transactionId <0
        assertThrows(InvalidTransactionIdException.class, () -> shop.applyDiscountRateToProduct(-1,"2424242424239",0.1));
        // transactionId null
        assertThrows(InvalidTransactionIdException.class, () -> shop.applyDiscountRateToProduct(null,"2424242424239",0.1));
    }

    @Test
    public void invalidProductCodeException() {
        // productCode invalid
        assertThrows(InvalidProductCodeException.class, () -> shop.applyDiscountRateToProduct(idSaleTransaction,"278732878273",0.1));
        assertThrows(InvalidProductCodeException.class, () -> shop.applyDiscountRateToProduct(idSaleTransaction,"2121",0.1));
        assertThrows(InvalidProductCodeException.class, () -> shop.applyDiscountRateToProduct(idSaleTransaction,"27873287827362737",0.1));
        // productCode empty
        assertThrows(InvalidProductCodeException.class, () -> shop.applyDiscountRateToProduct(idSaleTransaction,"",0.1));
        // productCode null
        assertThrows(InvalidProductCodeException.class, () -> shop.applyDiscountRateToProduct(idSaleTransaction,null,0.1));
    }

    @Test
    public void invalidDiscountRateException() {
        // discountRate<0
        assertThrows(InvalidDiscountRateException.class, () -> shop.applyDiscountRateToProduct(idSaleTransaction,"2424242424239",-1.0));
        // 0<discountRate<1
        assertThrows(InvalidDiscountRateException.class, () -> shop.applyDiscountRateToProduct(idSaleTransaction,"2424242424239",10.0));
        // discountRate=1
        assertThrows(InvalidDiscountRateException.class, () -> shop.applyDiscountRateToProduct(idSaleTransaction,"2424242424239",1));
    }

    @Test
    public void nonExistingProductCode() throws Exception {
        assertFalse(shop.applyDiscountRateToProduct(idSaleTransaction,"3456243422340",0.1));
    }

    @Test
    public void transactionNotOpen() throws Exception {
        shop.endSaleTransaction(idSaleTransaction);
        assertFalse(shop.applyDiscountRateToProduct(idSaleTransaction,"2424242424239",0.1));
    }

    @Test
    public void correctCase() throws Exception {
        assertTrue(shop.applyDiscountRateToProduct(idSaleTransaction,"2424242424239",0.1));
    }
}
