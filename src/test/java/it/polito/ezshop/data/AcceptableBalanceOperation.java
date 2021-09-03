package it.polito.ezshop.data;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;


import java.time.LocalDate;

public class AcceptableBalanceOperation {
    private MyBalanceOperation bo;
    private LocalDate now = LocalDate.now();

    @Before
    public void newSaleTransaction(){
        bo = new MyBalanceOperation(1,now,10.0,"DEBIT" );
    }

    @Test
    public void testGetBalanceId() {
        assertEquals(1, bo.getBalanceId());
    }
    @Test
    public void testGetDate() {
        assertEquals(now, bo.getDate());
    }
    @Test
    public void testGetMoney() {
        assertEquals(10.0, bo.getMoney(),0);
    }
    @Test
    public void testGetType() {
        assertEquals("DEBIT", bo.getType());
    }

    @Test
    public void testSetBalanceId() {
        bo.setBalanceId(2);
        assertEquals(2, bo.getBalanceId());
    }
    @Test
    public void testSetDate() {
        now= LocalDate.now();
        bo.setDate(now);
        assertEquals(now, bo.getDate());
    }
    @Test
    public void testSetMoney() {
        bo.setMoney(11.0);
        assertEquals(11.0, bo.getMoney(), 0);
    }
    @Test
    public void testSetType() {
        bo.setType("DEBIT");
        assertEquals("DEBIT", bo.getType());
    }

}
