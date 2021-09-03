package it.polito.ezshop.data;
import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class AcceptableGetAllUsers {

    EZShop shop;

    @Before
    public void before() throws InvalidPasswordException, InvalidRoleException, InvalidUsernameException {
        shop=new EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
    }

    @After
    public void after() {
        shop.reset();
    }

    @Test
    public void testAuthorization() throws Exception {
        shop.login("23", "12345");
        assertThrows(UnauthorizedException.class, shop::getAllUsers);
    }

    @Test
    public void testCorrect() throws Exception {
        shop.login("admin", "ciao");
        assertTrue(shop.getAllUsers() instanceof ArrayList);
    }
}

