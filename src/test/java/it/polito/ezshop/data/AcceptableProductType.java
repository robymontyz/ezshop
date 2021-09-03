package it.polito.ezshop.data;

import org.junit.*;
import org.junit.Before;

import static org.junit.Assert.*;

public class AcceptableProductType {

     MyProductType myProductType;

    @Before
    public void newProductType()
    {
        myProductType = new MyProductType(1, "2143325343648", "CiaoDescrizione", 23.5,21, "NoteOn","Shelf");
    }

    @Test
    public void testGetQuantity() {
        assertEquals(1, myProductType.getId().intValue());
    }

    @Test
    public void testSetQuantity() {
        myProductType.setQuantity(22);
        assertEquals(22, myProductType.getQuantity().intValue());
    }

    @Test
    public void testGetLocation() {
        assertEquals("Shelf", myProductType.getLocation());
    }

    @Test
    public void testSetLocation() {
        myProductType.setLocation("Boh");
        assertEquals("Boh", myProductType.getLocation());
    }

    @Test
    public void testGetNote() {
        assertEquals("NoteOn", myProductType.getNote());
    }

    @Test
    public void testSetNote() {
        myProductType.setNote("Note2");
        assertEquals("Note2", myProductType.getNote());
    }

    @Test
    public void testGetProductDescription() {
        assertEquals("CiaoDescrizione", myProductType.getProductDescription());
    }

    @Test
    public void testSetProductDescription() {
        myProductType.setProductDescription("CiaoDescrizione2");
        assertEquals("CiaoDescrizione2", myProductType.getProductDescription());
    }

    @Test
    public void testGetBarCode() {
        assertEquals("2143325343648", myProductType.getBarCode());
    }

    @Test
    public void testSetBarCode() {
        myProductType.setBarCode("11234567890125");
        assertEquals("11234567890125", myProductType.getBarCode());
    }

    @Test
    public void testGetPricePerUnit() {
        assertEquals(23.5, myProductType.getPricePerUnit(),0.0);
    }

    @Test
    public void testSetPricePerUnit() {
        myProductType.setPricePerUnit(24.9);
        assertEquals(24.9, myProductType.getPricePerUnit(),0.0);
    }

    @Test
    public void testGetId() {
        assertEquals(1, myProductType.getId().intValue());
    }

    @Test
    public void setId() {
        myProductType.setId(2);
        assertEquals(2, myProductType.getId().intValue());
    }

    @Test
    public void testValidationProductCode()
    {
        assertTrue(MyProductType.validateProductCode("11234567890125"));
        assertFalse(MyProductType.validateProductCode("12345678901234"));
        assertTrue(MyProductType.validateProductCode("1234567890128"));
        assertTrue(MyProductType.validateProductCode("123456789012"));
        assertFalse(MyProductType.validateProductCode("123456789"));
        assertFalse(MyProductType.validateProductCode("12345678901111112"));
        assertTrue(MyProductType.validateProductCode("11234567890200"));


    }


}
