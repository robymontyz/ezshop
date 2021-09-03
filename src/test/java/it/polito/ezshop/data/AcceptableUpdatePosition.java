package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.InvalidLocationException;
import it.polito.ezshop.exceptions.InvalidProductIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.Before;
import org.junit.Test;
import org.junit.After;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThrows;

public class AcceptableUpdatePosition {

    EZShop shop;
    int prodID, prodId2;
    @Before
    public void before() throws Exception
    {
        shop=new EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
        prodID=shop.createProductType("Latte", "2424242424239", 1.5, "scaduto");
        prodId2=shop.createProductType("Choco", "12345678901286", 1.5, "scaduto");
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
                shop.updatePosition(prodID,"332-casa-321")
        );
        shop.login("23","12345");
        assertThrows(UnauthorizedException.class,()->
                shop.updatePosition(prodID,"332-casa-321")
        );

    }
    @Test
    public void testProductId() {
        assertThrows(InvalidProductIdException.class,()-> shop.updatePosition(null,"332-casa-321"));

        assertThrows(InvalidProductIdException.class,()-> shop.updatePosition(-1,"332-casa-321"));

    }

    @Test
    public void testCorrectPosition() {
        assertThrows(InvalidLocationException.class,()-> shop.updatePosition(prodID,"332-331-321"));
        assertThrows(InvalidLocationException.class,()-> shop.updatePosition(prodID,"casa-331-casa"));
        assertThrows(InvalidLocationException.class,()-> shop.updatePosition(prodID,"33a2-3a31-3a21"));


    }
    @Test
    public void testDuplicatePosition() throws Exception{
        shop.updatePosition(prodID,"12-as-12" );
        assertFalse(shop.updatePosition(prodId2,"12-as-12" ));
        shop.updatePosition(prodID,"14-per-14");
    }

    @Test
    public void testCorrectCase() throws Exception{
        assertTrue(shop.updatePosition(prodID,"13-a-13" ));
    }
}
