package com.openclassrooms.realestatemanager;

import android.content.Context;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;

import org.junit.Test;
import org.junit.runner.RunWith;

import static com.openclassrooms.realestatemanager.utils.Utils.isInternetAvailable;
import static org.junit.Assert.assertTrue;

@RunWith(AndroidJUnit4.class)
public class UtilsAndroidTest {

    @Test
    public void isInternetAvailableTest() {
        // Context of the app under test.
        Context appContext = InstrumentationRegistry.getInstrumentation().getContext();

        assertTrue(isInternetAvailable(appContext));
    }

}
