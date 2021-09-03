package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableDeleteUser {
    EZShop shop;

    @Before
    public void before() throws Exception
    {
        shop = new EZShop();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
    }

    @After
    public void after() throws Exception {
        shop.reset();
    }

    @Test
    public void deleteUsernameNotAuthorized() throws InvalidPasswordException, InvalidUsernameException {
        shop.login("23","12345"); //not an Admin
        assertThrows(UnauthorizedException.class, ()->
            shop.deleteUser(1)
        );
        shop.logout();
    }

    @Test
    public void deleteUsernameNotPresent() throws InvalidUserIdException, UnauthorizedException, InvalidPasswordException, InvalidUsernameException {
        shop.login("admin","ciao");
        assertFalse(shop.deleteUser(900));
        shop.logout();

    }

    @Test
    public void userNotLogged() {
        assertThrows(UnauthorizedException.class, ()->
            shop.deleteUser(1)
        );

    }

    @Test
    public void invalidUserId() throws InvalidPasswordException, InvalidUsernameException {
        shop.login("admin","ciao");
        assertThrows(InvalidUserIdException.class, ()->
            shop.deleteUser(-1)
        );
        shop.logout();
    }

    @Test
    public void userDeletable() throws InvalidPasswordException, InvalidUsernameException, InvalidUserIdException, UnauthorizedException, InvalidRoleException {
        shop.login("admin","ciao");
        assertTrue(shop.deleteUser(shop.createUser("temp","temp","Administrator")));
        shop.logout();
    }
}
