package it.polito.ezshop.data;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class AcceptableTicketEntry {
    private MyTicketEntry myTicketEntry;

    @Before
    public void newEntry(){
        myTicketEntry = new MyTicketEntry("11234567890125", "product", 123, 3.70, 1.0 );
    }


    @Test
    public void testGetBarcode() {
        assertEquals("11234567890125", myTicketEntry.getBarCode());
    }

    @Test
    public void testGetProductDescription() {
        assertEquals("product", myTicketEntry.getProductDescription());
    }

    @Test
    public void testGetAmount() {
        assertEquals(123, myTicketEntry.getAmount());
    }

    @Test
    public void testGetPricePerUnit() {
        assertEquals(3.70, myTicketEntry.getPricePerUnit(),0);
    }
    @Test
    public void testGetDiscountRate() {
        assertEquals(1.0, myTicketEntry.getDiscountRate(),0);
    }

    @Test
    public void testSetBarcode() {
        myTicketEntry.setBarCode("2143325343648");
        assertEquals("2143325343648", myTicketEntry.getBarCode());
    }

    @Test
    public void testSetProductDescription() {
        myTicketEntry.setProductDescription("prodotto");
        assertEquals("prodotto", myTicketEntry.getProductDescription());
    }

    @Test
    public void testSetAmount() {
        myTicketEntry.setAmount(555);
        assertEquals(555, myTicketEntry.getAmount());
    }

    @Test
    public void testSetPricePerUnit() {
        myTicketEntry.setPricePerUnit(8.88);
        assertEquals(8.88, myTicketEntry.getPricePerUnit(),0);
    }
    @Test
    public void testSetDiscountRate() {
        myTicketEntry.setDiscountRate(0.0);
        assertEquals(0.0, myTicketEntry.getDiscountRate(),0);
    }
}
