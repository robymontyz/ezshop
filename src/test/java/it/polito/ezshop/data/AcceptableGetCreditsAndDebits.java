package it.polito.ezshop.data;

import it.polito.ezshop.exceptions.UnauthorizedException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;

import static org.junit.Assert.*;

public class AcceptableGetCreditsAndDebits {
    private it.polito.ezshop.data.EZShop shop;

    @Before
    public void before() throws Exception{
        shop = new EZShop();
        shop.reset();
        shop.createUser("admin","ciao","Administrator");
        shop.createUser("23","12345","Cashier");
        shop.login("admin","ciao");
        shop.recordBalanceUpdate(100);
        shop.recordBalanceUpdate(899);
        shop.recordBalanceUpdate(172);
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
                shop.getCreditsAndDebits(LocalDate.of(2021,5,24),LocalDate.of(2021,5,25)));
        shop.login("23","12345");
        assertThrows(UnauthorizedException.class, () ->
                shop.getCreditsAndDebits(LocalDate.of(2021,5,24),LocalDate.of(2021,5,25)));
        shop.logout();
        shop.login("admin","ciao");
    }

    @Test
    public void testCorrectCase() throws Exception{
        LocalDate today = LocalDate.now();
        LocalDate yesterday= today.minusDays(1);

        assertTrue(shop.getCreditsAndDebits(null, null).size()>=1);
        //assertEquals(3,shop.getCreditsAndDebits(yesterday,today.plusDays(1)).size());
        assertEquals(3, shop.getCreditsAndDebits(null, today).size());
        assertEquals(3, shop.getCreditsAndDebits(yesterday, null).size());
        assertEquals(0, shop.getCreditsAndDebits(null, yesterday).size());
        //assertEquals(3,shop.getCreditsAndDebits(null, today.plusDays(1)).size());
    }
}
