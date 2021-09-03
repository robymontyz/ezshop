package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidProductIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

public class AcceptableUpdateQuantity {

    EZShop shop;
    int prodID;

    @Before
    public void before() throws Exception
    {
        shop=new EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");

        shop.login("admin","ciao");
        prodID=shop.createProductType("Latte", "2424242424239", 1.5, "scaduto");
        shop.updatePosition(prodID,"12-aaa-12");
    }

    @After
    public void after(){
        shop.logout();
        shop.reset();
    }

    @Test
    public void testAuthorization() throws Exception{

        shop.logout();
        assertThrows(UnauthorizedException.class,()->
                shop.updateQuantity(prodID,1)
        );
        shop.login("23","12345");
        assertThrows(UnauthorizedException.class,()->
                shop.updateQuantity(prodID,1)
        );

    }
    @Test
    public void testProductId() {
        assertThrows(InvalidProductIdException.class,()-> shop.updateQuantity(null,1));
        assertThrows(InvalidProductIdException.class,()-> shop.updateQuantity(-5,1 ));

    }
    @Test
    public void testToBeAdded() throws Exception{
        assertFalse(shop.updateQuantity(prodID,-1 ));
        assertFalse(shop.updateQuantity(132432,1 ));
    }

    @Test
    public void testCorrectCase() throws Exception{
        assertTrue(shop.updateQuantity(prodID,100));
    }
}
