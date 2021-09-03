package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.runner.OrderWith;
import org.junit.runner.manipulation.Alphanumeric;

import static org.junit.Assert.*;

@OrderWith(Alphanumeric.class)
public class AcceptableGetUser {

    private EZShop shop;
    @Before
    public void before() throws InvalidUsernameException, InvalidPasswordException, InvalidRoleException {
        shop=new EZShop();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin", "ciao");
    }

    @Test
    public void testAuthorization() throws Exception {

        shop.logout();
        assertThrows(UnauthorizedException.class, shop::getAllUsers);
        shop.login("23", "12345");
        assertThrows(UnauthorizedException.class, shop::getAllUsers);
    }
    @Test
    public void testCorrectId() throws Exception{

        shop.login("admin","ciao");
        assertThrows(InvalidUserIdException.class, ()->
           shop.getUser(-4)
        );
        assertThrows(InvalidUserIdException.class, ()->
            shop.getUser(null)
        );


    }

    @Test
    public void testGetUser() throws Exception {
        Integer id = shop.createUser("Franco","ciaoCiao","Cashier");
        assertEquals(id, shop.getUser(id).getId());
        shop.deleteUser(id);
    }

    @Test
    public void testNotFoundUser() throws Exception{
       assertNull(shop.getUser(999));
    }

}
