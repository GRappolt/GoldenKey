package com.roachcitysoftware.goldenkey;

import android.app.Application;
import android.test.ApplicationTestCase;

/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {
    public ApplicationTest() {
        super(Application.class);
    }

    private MainActivityTest mainActivityTest;

    public void testMainActivity () {
        mainActivityTest = new MainActivityTest();
        mainActivityTest.run();
    }
}