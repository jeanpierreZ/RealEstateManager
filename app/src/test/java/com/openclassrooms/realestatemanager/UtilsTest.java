package com.openclassrooms.realestatemanager;

import org.junit.Before;
import org.junit.Test;

import static com.openclassrooms.realestatemanager.utils.Utils.convertDollarToEuro;
import static com.openclassrooms.realestatemanager.utils.Utils.convertEuroToDollar;
import static com.openclassrooms.realestatemanager.utils.Utils.getTodayDate;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

public class UtilsTest {

    private int dollars;
    private int euros;
    private String todayFormat;

    @Before
    public void setForTests() {
        dollars = 100000;
        euros = 100000;
        todayFormat = "dd/MM/yyyy";
    }

    @Test
    public void convertDollarToEuroTest() {
        assertEquals(92430, convertDollarToEuro(dollars), 0);
    }

    @Test
    public void convertEuroToDollarTest() {
        assertEquals(108180, convertEuroToDollar(euros), 0);
    }

    @Test
    public void getTodayDateTest() {
        assertNotNull(getTodayDate());
        assertEquals(todayFormat.length(), getTodayDate().length());
        assertTrue(getTodayDate().contains("/"));
    }

}
