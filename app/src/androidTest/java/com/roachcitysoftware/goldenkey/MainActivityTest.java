package com.roachcitysoftware.goldenkey;

import android.test.ActivityInstrumentationTestCase2;
import android.widget.TextView;


/**
 * Tests of main application screen by GeorgeR created 11/9/2015.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mMainActivity;
    private TextView mTitle1;
    private TextView mTitle2;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mMainActivity = getActivity();
        mTitle1 = (TextView) mMainActivity.findViewById(R.id.textView);
        mTitle2 = (TextView) mMainActivity.findViewById(R.id.textView2);
    }

    public void testPreconditions() {
        assertNotNull("mMainActivity is null.", mMainActivity);
        assertNotNull("mTitle1 is null.", mTitle1);
        assertNotNull("mTitle2 is null.", mTitle2);
    }

    public void testMainActivityTitle1_labelText()
    {
        final String expected = mMainActivity.getString(R.string.title_1);
        final String actual = mTitle1.getText().toString();
        assertEquals(expected, actual);
    }

    public void testMainActivityTitle2_labelText()
    {
        final String expected = mMainActivity.getString(R.string.title_2);
        final String actual = mTitle2.getText().toString();
        assertEquals(expected, actual);
    }
}