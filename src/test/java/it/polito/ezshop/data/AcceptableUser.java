package it.polito.ezshop.data;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableUser {

    private MyUser myUser;

    @Before
    public void newUser() {
        myUser = new MyUser(1, "ciao", "2020", "Cashier");
    }

    @Test
    public void testGetId() {
        assertEquals(1, myUser.getId().intValue());
    }

    @Test
    public void testGetUsername() {
        assertEquals("ciao", myUser.getUsername());
    }

    @Test
    public void testGetPassword(){
        assertEquals("2020", myUser.getPassword());
    }

    @Test
    public void testGetRole(){
        assertEquals("Cashier", myUser.getRole());
    }

    @Test
    public void testSetId() {
        myUser.setId(2);
        assertEquals(2, myUser.getId().intValue());
    }

    @Test
    public void testSetUsername() {
        myUser.setUsername("newUsername");
        assertEquals("newUsername", myUser.getUsername());
    }

    @Test
    public void testSetPassword() {
        myUser.setPassword("newPass");
        assertEquals("newPass", myUser.getPassword());
    }
    @Test
    public void testSetRole() {
        myUser.setRole("Administrator");
        assertEquals("Administrator", myUser.getRole());
    }

}

