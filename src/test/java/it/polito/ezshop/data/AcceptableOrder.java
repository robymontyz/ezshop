package it.polito.ezshop.data;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


public class AcceptableOrder {

    MyOrder myOrder;

    @Before
    public void newOrder ()
    {
        myOrder = new MyOrder(1, "2143325343648", 25.8, 23, "ISSUED");
    }

    @Test
    public void getId()
    {
        assertEquals(1, myOrder.getOrderId().intValue());
    }

    @Test
    public void getProductCode()
    {
        assertEquals("2143325343648", myOrder.getProductCode());
    }

    @Test
    public void getPrice()
    {
        assertEquals(25.8, myOrder.getPricePerUnit(),0);
    }

    @Test
    public void getQuantity()
    {
        assertEquals(23, myOrder.getQuantity());
    }

    @Test
    public void getStatus()
    {
        assertEquals("ISSUED", myOrder.getStatus());
    }

    @Test
    public void getBalanceId()
    {
        assertEquals(0, myOrder.getBalanceId().intValue());
    }

    @Test
    public void setBalanceId()
    {
        myOrder.setBalanceId(2);
        assertEquals(2, myOrder.getBalanceId().intValue());
    }

    @Test
    public void setId()
    {
        myOrder.setOrderId(2);
        assertEquals(2, myOrder.getOrderId().intValue());
    }

    @Test
    public void setProductCode()
    {
        myOrder.setProductCode("11234567890125");
        assertEquals("11234567890125", myOrder.getProductCode());
    }

    @Test
    public void setPrice()
    {
        myOrder.setPricePerUnit(28.9);
        assertEquals(28.9, myOrder.getPricePerUnit(),0);
    }


    @Test
    public void setQuantity()
    {
        myOrder.setQuantity(22);
        assertEquals(22, myOrder.getQuantity());
    }

    @Test
    public void setStatus()
    {
        myOrder.setStatus("PAYED");
        assertEquals("PAYED", myOrder.getStatus());
    }

    @Test
    public void newEmptyOrder()
    {
        MyOrder or= new MyOrder(5);
        assertEquals(5,or.getOrderId().intValue());
    }

    @Test
    public void newFullOrder ()
    {
        MyOrder or = new MyOrder(2, "2143325343648", 25.8, 23, "ISSUED");
        assertEquals(2,or.getOrderId().intValue());
    }

}
