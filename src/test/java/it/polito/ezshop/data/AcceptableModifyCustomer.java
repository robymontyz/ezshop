package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;


import static org.junit.Assert.*;

public class AcceptableModifyCustomer {

    private EZShop shop;
    Integer id,id2;
    String card, card2;

    @Before
    public void before() throws Exception {
        shop = new EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("23","12345");
        id = shop.defineCustomer("Brazorf");
        id2 = shop.defineCustomer("Ajeje");
        card = shop.createCard();
        card2 = shop.createCard();
        shop.attachCardToCustomer(card,id2);
        //shop.modifyCustomer(id2,"Pino",card);
    }

    @After
    public void after() throws Exception{
        shop.reset();
        shop.logout();
    }

    @Test
    public void authTest() throws Exception {
        shop.logout();
        assertThrows(UnauthorizedException.class, () ->
                shop.modifyCustomer(id,"Mauro",card2)
        );
        shop.login("23","12345");
    }

    @Test
    public void modifyNameOnly() throws Exception {
        assertTrue(shop.modifyCustomer(id,"Francesco",null));

    }

    @Test
    public void removeCard() throws Exception {
        assertTrue(shop.modifyCustomer(id,"Francesco",""));
    }

    @Test
    public void modifyCard() throws Exception {
        assertTrue(shop.modifyCustomer(id,"Francesco",card2));
    }

    @Test
    public void modifyCardAlreadyAssigned() throws Exception {
        assertFalse(shop.modifyCustomer(id,"Francesco",card));
    }

    @Test
    public void invalidName() {
        assertThrows(InvalidCustomerNameException.class, () -> shop.modifyCustomer(id,"",""));
        assertThrows(InvalidCustomerNameException.class, () -> shop.modifyCustomer(id,null,""));
    }

    @Test
    public void invalidCard() {
        assertThrows(InvalidCustomerCardException.class, () -> shop.modifyCustomer(id,"Ok","156161"));
        assertThrows(InvalidCustomerCardException.class, () -> shop.modifyCustomer(id,"Ok","aaa711"));
    }

}
