package com.cj.mst;

import android.content.Context;

import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;


/**
 * Instrumented BizGuideModuleDelegate, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    @Test
    public void useAppContext() throws Exception {
        // Context of the app under BizGuideModuleDelegate.
        Context appContext = InstrumentationRegistry.getTargetContext();

        assertEquals("com.cj.mst", appContext.getPackageName());
    }
}
