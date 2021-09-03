package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class AcceptableLogin {

    private  EZShop shop;
    Integer userId;

    @Before
    public void before() throws Exception {
        shop=new EZShop();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
        userId=shop.createUser("Carlo", "1321", "Cashier");
    }


    @After
    public void after() throws Exception {
        shop.login("admin","ciao");
        shop.deleteUser(userId);
        shop.reset();
        shop.logout();
    }
    @Test
    public void testUsername(){
        shop.logout();
        assertThrows(InvalidUsernameException.class, ()->
                shop.login("", "125465")
        );
        assertThrows(InvalidUsernameException.class, ()->
                shop.login(null, "password")
        );


    }
    @Test
    public void testPassword(){
        shop.logout();
        assertThrows(InvalidPasswordException.class, ()->
                shop.login("Carlo", "")
        );
        assertThrows(InvalidPasswordException.class, ()->
                shop.login("Carlo", null)
        );

    }

   @Test
   public void testTwoLoggedUser() throws Exception{
        assertNull(shop.login("23","1234"));
    }
}
