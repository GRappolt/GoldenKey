package com.roachcitysoftware.goldenkey;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;

import junit.framework.TestCase;

/**
 * Created by GeorgeR on 11/9/2015.
 */
class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mMainActivity;
    private TextView mTitle1;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mMainActivity = getActivity();
        mTitle1 = (TextView) mMainActivity.findViewById(R.id.textView);
    }

    public void testPreconditions() {
        assertNotNull("mMainActivity is null.", mMainActivity);
        assertNotNull("mTitle1 is null.", mTitle1);
    }

    public void testMainActivityTitle1_labelText()
    {
        final String expected = mMainActivity.getString(R.string.title_1);
        final String actual = mTitle1.getText().toString();
        assertEquals(expected, actual);
    }
}