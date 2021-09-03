package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidProductCodeException;
import it.polito.ezshop.exceptions.InvalidQuantityException;
import it.polito.ezshop.exceptions.InvalidTransactionIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableDeleteProductFromSale {
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
        assertThrows(UnauthorizedException.class, () -> shop.deleteProductFromSale(1,"2424242424239",1));
    }

    @Test
    public void invalidTransactionId()  {
        // transactionid 0
        assertThrows(InvalidTransactionIdException.class, () -> shop.deleteProductFromSale(0,"2424242424239",3));
        // transactionid <0
        assertThrows(InvalidTransactionIdException.class, () -> shop.deleteProductFromSale(-1,"2424242424239",3));
        // transactionid null
        assertThrows(InvalidTransactionIdException.class, () -> shop.deleteProductFromSale(null,"2424242424239",3));
    }

    @Test
    public void invalidProductCodeException() {

        // productCode invalid
        assertThrows(InvalidProductCodeException.class, () -> shop.deleteProductFromSale(idSaleTransaction,"278732878273",3));
        assertThrows(InvalidProductCodeException.class, () -> shop.deleteProductFromSale(idSaleTransaction,"2121",3));
        assertThrows(InvalidProductCodeException.class, () -> shop.deleteProductFromSale(idSaleTransaction,"27873287827362737",3));
        // productCode empty
        assertThrows(InvalidProductCodeException.class, () -> shop.deleteProductFromSale(idSaleTransaction,"",3));
        // productCode null
        assertThrows(InvalidProductCodeException.class, () -> shop.deleteProductFromSale(idSaleTransaction,null,3));
    }

    @Test
    public void invalidQuantity() {
        // quantity <0
        assertThrows(InvalidQuantityException.class, () -> shop.deleteProductFromSale(idSaleTransaction,"2424242424239",-1));
    }

    @Test
    public void nonExistingProductCode() throws Exception {
        assertFalse(shop.deleteProductFromSale(idSaleTransaction,"3456243422340",4));
    }

    @Test
    public void notEnoughQuantity() throws Exception {
        assertFalse(shop.deleteProductFromSale(idSaleTransaction,"2424242424239",80));
    }

    @Test
    public void transactionNotOpen() throws Exception {
        shop.endSaleTransaction(idSaleTransaction);
        assertFalse(shop.deleteProductFromSale(idSaleTransaction,"2424242424239",1));
    }

    @Test
    public void correctCase() throws Exception {
        assertTrue(shop.deleteProductFromSale(idSaleTransaction,"2424242424239",1));
        shop.endSaleTransaction(idSaleTransaction);
        shop.logout();
        shop.login("admin","ciao");
        assertEquals(2,shop.getProductTypeByBarCode("2424242424239").getQuantity().intValue());
    }
}
