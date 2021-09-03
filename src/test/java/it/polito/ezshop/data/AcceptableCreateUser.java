package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidPasswordException;
import it.polito.ezshop.exceptions.InvalidRoleException;
import it.polito.ezshop.exceptions.InvalidUsernameException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;


public class AcceptableCreateUser {

    private EZShop shop;

    @Before
    public void before() throws InvalidPasswordException, InvalidUsernameException, InvalidRoleException {
        shop = new EZShop();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
    }

    @After
    public void after() {
        shop.logout();
        shop.reset();
    }

    @Test
    public void testUsername(){
        assertThrows(InvalidUsernameException.class, ()->
            shop.createUser("", "125465", "Cashier")
        );
        assertThrows(InvalidUsernameException.class, ()->
            shop.createUser(null, "password", "Cashier")
        );


    }
    @Test
    public void testPassword(){

        assertThrows(InvalidPasswordException.class, ()->
            shop.createUser("Carlo", "", "Cashier")
        );
        assertThrows(InvalidPasswordException.class, ()->
            shop.createUser("Carlo", null, "Cashier")
        );
    }

    @Test
    public void testRole(){
        assertThrows(InvalidRoleException.class, ()->
            shop.createUser("Carlo", "1234", "")
        );
        assertThrows(InvalidRoleException.class, ()->
            shop.createUser("Carlo", "1234", null)
        );
        assertThrows(InvalidRoleException.class, ()->
            shop.createUser("Carlo", "1234", "asd")
        );

    }

    @Test
    public void testDuplicateUsername() throws Exception{
        Integer id=null;
            try {
                id=shop.createUser("Carlo", "abCd", "Cashier");

            }catch(Exception ignored){

            }
        assertEquals(-1, shop.createUser("Carlo", "sadF", "Cashier").intValue());
        shop.deleteUser(id);
    }

    @Test
    public void testCorrectCase() throws Exception{
        Integer id=shop.createUser("Carlo", "sadF", "Cashier");
        assertTrue(id>0);
        shop.deleteUser(id);
    }

}
