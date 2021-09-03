package it.polito.ezshop.data;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;



public class AcceptableCustomer {
    private MyCustomer myCustomer;

    @Before
    public void newCustomer(){
        myCustomer = new MyCustomer(1, "Pinuccio", "2143254542", 370);
    }

    @Test
    public void testGetId() {
        assertEquals(1, myCustomer.getId().intValue());
    }

    @Test
    public void testGetLoyaltyCardId() {
        assertEquals("2143254542", myCustomer.getCustomerCard());
    }

    @Test
    public void testGetCustomerName(){
        assertEquals("Pinuccio", myCustomer.getCustomerName());
    }

    @Test
    public void testGetPoints(){
        assertEquals(370, myCustomer.getPoints().intValue());
    }

    @Test
    public void testSetId() {
        myCustomer.setId(2);
        assertEquals(2, myCustomer.getId().intValue());
    }

    @Test
    public void testSetLoyaltyCardId() {
        myCustomer.setCustomerCard("12343213");
        assertEquals("12343213", myCustomer.getCustomerCard());
    }

    @Test
    public void testSetCustomerName(){
        myCustomer.setCustomerName("Rispondi");
        assertEquals("Rispondi", myCustomer.getCustomerName());
    }

    @Test
    public void testSetPoints(){
        myCustomer.setPoints(360);
        assertEquals(360, myCustomer.getPoints().intValue());
    }

}