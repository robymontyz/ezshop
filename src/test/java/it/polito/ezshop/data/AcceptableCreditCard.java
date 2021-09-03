package it.polito.ezshop.data;

import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;



public class AcceptableCreditCard {
    private MyCreditCard card;

    @Before
    public void newCustomer(){
        card = new MyCreditCard("4916560768986372",  370);
    }

    @Test
    public void testGetCardNumber() {
        assertEquals("4916560768986372", card.getCardNumber());
    }

    @Test
    public void testGetBalance() {
        assertEquals(370, card.getBalance(), 0);
    }
    
    @Test
    public void testSetCardNumber() {
        card.setCardNumber("4916168367711421");
        assertEquals("4916168367711421", card.getCardNumber());
    }

    @Test
    public void testSetBalance() {
        card.setBalance(333);
        assertEquals(333, card.getBalance(),0);
    }

    @Test
    public void testValidateWithLuhn() {
        assertTrue(MyCreditCard.validateWithLuhn("4485370086510891"));
        assertTrue(MyCreditCard.validateWithLuhn("5100293991053009"));
        assertTrue(MyCreditCard.validateWithLuhn("4716258050958645"));
        assertFalse(MyCreditCard.validateWithLuhn("4324332424"));
    }

}
