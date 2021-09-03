package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidCustomerCardException;
import it.polito.ezshop.exceptions.InvalidCustomerIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableAttachCardToCustomer {
    EZShop shop;
    String card,cardAssengated;
    Integer customerId, customerIdWithCard;

    @Before
    public void before() throws Exception {
        shop = new EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("23","12345");
        card=shop.createCard();
        customerId=shop.defineCustomer("Jonny");
        customerIdWithCard=shop.defineCustomer("Frank");
        cardAssengated= shop.createCard();
        shop.attachCardToCustomer(cardAssengated,customerIdWithCard);
    }

    @After
    public void after() throws Exception{
        shop.logout();
        shop.reset();
    }

    @Test
    public void authTest() throws Exception {
        shop.logout();
        assertThrows(UnauthorizedException.class, () -> shop.attachCardToCustomer(card,customerId));
        shop.login("23","12345");
    }

    @Test
    public void invalidCustomerId() {
        assertThrows(InvalidCustomerIdException.class, () -> shop.attachCardToCustomer(card,0));
        assertThrows(InvalidCustomerIdException.class, () -> shop.attachCardToCustomer(card,-1));
        assertThrows(InvalidCustomerIdException.class, () -> shop.attachCardToCustomer(card,null));
    }

    @Test
    public void invalidCardId() {
        assertThrows(InvalidCustomerCardException.class, () -> shop.attachCardToCustomer("",customerId));
        assertThrows(InvalidCustomerCardException.class, () -> shop.attachCardToCustomer("00000001",customerId));
        assertThrows(InvalidCustomerCardException.class, () -> shop.attachCardToCustomer("000000000100",customerId));
        assertThrows(InvalidCustomerCardException.class, () -> shop.attachCardToCustomer(null,customerId));
    }

    @Test
    public void noUser() throws InvalidCustomerIdException, UnauthorizedException, InvalidCustomerCardException {
        assertFalse(shop.attachCardToCustomer(card,748374));
    }

    @Test
    public void alreadyAssigned() throws InvalidCustomerIdException, UnauthorizedException, InvalidCustomerCardException {
        assertFalse(shop.attachCardToCustomer(cardAssengated,customerId));
    }

    @Test
    public void correctCase() throws InvalidCustomerIdException, UnauthorizedException, InvalidCustomerCardException {
        assertTrue(shop.attachCardToCustomer(card,customerId));
    }
}
