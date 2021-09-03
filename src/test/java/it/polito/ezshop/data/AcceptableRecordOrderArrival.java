package it.polito.ezshop.data;
import it.polito.ezshop.exceptions.InvalidLocationException;
import it.polito.ezshop.exceptions.InvalidOrderIdException;
import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.Test;
import org.junit.After;
import org.junit.Before;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertThrows;

public class AcceptableRecordOrderArrival {

    private it.polito.ezshop.data.EZShop shop;
    private int orderIdPayed, orderIdPayed2;

    @Before
    public void before() throws Exception{
        shop = new it.polito.ezshop.data.EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
        int idProd = shop.createProductType("Latte","2424242424239",1.0,"Scaduto");
        shop.createProductType("Duriano","12345678901286",1.0,"Solidificato");
        shop.updatePosition(idProd,"13-cacca-14");
        shop.recordBalanceUpdate(1000);
        orderIdPayed = shop.payOrderFor("2424242424239",1,1.0);
        orderIdPayed2= shop.payOrderFor("12345678901286",1,1.0);
    }

    @After
    public void after(){
        shop.logout();
        shop.reset();
    }

    @Test
    public void authTest() throws Exception {
        shop.logout();
        assertThrows(UnauthorizedException.class, () ->
                shop.recordOrderArrival(orderIdPayed)
        );
        shop.login("23", "12345");
        assertThrows(UnauthorizedException.class, () ->
                shop.recordOrderArrival(orderIdPayed)
        );
    }

    @Test
    public void testInvalidOrderId() throws Exception {
        shop.login("admin", "ciao");
        assertFalse(shop.recordOrderArrival(300));
        assertThrows(InvalidOrderIdException.class, () ->
                shop.recordOrderArrival(-300)
        );
        assertThrows(InvalidOrderIdException.class, () ->
                shop.recordOrderArrival(null)
        );
    }

    @Test
    public void testCompletedState() throws Exception {
        assertFalse(shop.recordOrderArrival(255));
    }

    @Test
    public void testNoLocation() {
        assertThrows(InvalidLocationException.class,()->shop.recordOrderArrival(orderIdPayed2));
    }

    @Test
    public void testCorrectCase() throws Exception {
        assertTrue(shop.recordOrderArrival(orderIdPayed));
    }


}
