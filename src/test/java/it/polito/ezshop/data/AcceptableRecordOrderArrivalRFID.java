package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidLocationException;
import it.polito.ezshop.exceptions.InvalidOrderIdException;
import it.polito.ezshop.exceptions.InvalidRFIDException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableRecordOrderArrivalRFID {
    private it.polito.ezshop.data.EZShop shop;
    private int orderIdPayed, orderIdPayed2;
    int idProd2;
    @Before
    public void before() throws Exception{
        shop = new it.polito.ezshop.data.EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
        int idProd = shop.createProductType("Latte","2424242424239",1.0,"Scaduto");
        idProd2 = shop.createProductType("Duriano","12345678901286",1.0,"Solidificato");
        shop.updatePosition(idProd,"13-cacca-14");
        shop.recordBalanceUpdate(1000);
        orderIdPayed = shop.payOrderFor("2424242424239",10,1.0);
        orderIdPayed2= shop.payOrderFor("12345678901286",10,1.0);
    }

    @After
    public void after(){
        shop.logout();
        shop.reset();
    }

    @Test
    public void invalidFormatRFID(){
        assertThrows(InvalidRFIDException.class, ()-> shop.recordOrderArrivalRFID(orderIdPayed,"000000001"));
        assertThrows(InvalidRFIDException.class, ()-> shop.recordOrderArrivalRFID(orderIdPayed,"00000000100"));
        assertThrows(InvalidRFIDException.class, ()-> shop.recordOrderArrivalRFID(orderIdPayed,"000000001a"));
        assertThrows(InvalidRFIDException.class, ()-> shop.recordOrderArrivalRFID(orderIdPayed,"0000-00001"));
    }

    @Test
    public void duplicatedRFID() throws Exception{
        shop.recordOrderArrivalRFID(orderIdPayed,"000000000010");
        shop.updatePosition(idProd2,"13-culo-14");
        assertThrows(InvalidRFIDException.class, ()-> shop.recordOrderArrivalRFID(orderIdPayed2,"000000000010"));
        assertThrows(InvalidRFIDException.class, ()-> shop.recordOrderArrivalRFID(orderIdPayed2,"000000000001"));
        assertThrows(InvalidRFIDException.class, ()-> shop.recordOrderArrivalRFID(orderIdPayed2,"000000000009"));


    }

    @Test
    public void authTest() throws Exception {
        shop.logout();
        assertThrows(UnauthorizedException.class, () ->
                shop.recordOrderArrivalRFID(orderIdPayed,"000000000010")
        );
        shop.login("23", "12345");
        assertThrows(UnauthorizedException.class, () ->
                shop.recordOrderArrivalRFID(orderIdPayed,"000000000010")
        );
    }

    @Test
    public void testInvalidOrderId() throws Exception {
        shop.login("admin", "ciao");
        assertFalse(shop.recordOrderArrivalRFID(300,"000000000010"));
        assertThrows(InvalidOrderIdException.class, () ->
                shop.recordOrderArrivalRFID(-300,"000000000010")
        );
        assertThrows(InvalidOrderIdException.class, () ->
                shop.recordOrderArrivalRFID(null,"000000000010")
        );
    }

    @Test
    public void testCompletedState() throws Exception {
        assertFalse(shop.recordOrderArrivalRFID(255,"000000000010"));
    }

    @Test
    public void testNoLocation() {
        assertThrows(InvalidLocationException.class,()->shop.recordOrderArrivalRFID(orderIdPayed2,"000000000010"));
    }

    @Test
    public void testCorrectCase() throws Exception {
        assertTrue(shop.recordOrderArrivalRFID(orderIdPayed,"000000000010"));
    }
}
