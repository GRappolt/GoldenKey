package com.roachcitysoftware.goldenkey;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;


/**
 * Tests of main application screen by GeorgeR created 11/9/2015.
 */
public class MainActivityTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private MainActivity mMainActivity;
    private TextView mTitle1;
//    private TextView mTitle2;
    private Button mInstructions;
    private Button mBuildList;
    private Button mPractice;

    public MainActivityTest() {
        super(MainActivity.class);
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mMainActivity = getActivity();
        mTitle1 = (TextView) mMainActivity.findViewById(R.id.textView);
//        mTitle2 = (TextView) mMainActivity.findViewById(R.id.textView2);
        mInstructions = (Button) mMainActivity.findViewById(R.id.details_button);
        mBuildList = (Button) mMainActivity.findViewById(R.id.list_button);
        mPractice = (Button) mMainActivity.findViewById(R.id.practice_button);
    }

    public void testPreconditions() {
        assertNotNull("mMainActivity is null.", mMainActivity);
        assertNotNull("mTitle1 is null.", mTitle1);
     //   assertNotNull("mTitle2 is null.", mTitle2);
        assertNotNull("mInstructions is null.", mInstructions);
        assertNotNull("mBuildList is null.", mBuildList);
        assertNotNull("mPractice is null.", mPractice);
    }

    public void testMainActivityTitle1_labelText()
    {
        final String expected = mMainActivity.getString(R.string.title_1);
        final String actual = mTitle1.getText().toString();
        assertEquals(expected, actual);
    }

/*    public void testMainActivityTitle2_labelText()
    {
        final String expected = mMainActivity.getString(R.string.title_2);
        final String actual = mTitle2.getText().toString();
        assertEquals(expected, actual);
    }
*/
    public void testMainActivityInstructions_layout ()
    {
        final View decorView = mMainActivity.getWindow().getDecorView();

        ViewAsserts.assertOnScreen(decorView, mInstructions);

        final ViewGroup.LayoutParams layoutParams = mInstructions.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void testMainActivityBuildList_layout ()
    {
        final View decorView = mMainActivity.getWindow().getDecorView();

        ViewAsserts.assertOnScreen(decorView, mBuildList);

        final ViewGroup.LayoutParams layoutParams = mBuildList.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void testMainActivityPractice_layout ()
    {
        final View decorView = mMainActivity.getWindow().getDecorView();

        ViewAsserts.assertOnScreen(decorView, mPractice);

        final ViewGroup.LayoutParams layoutParams = mPractice.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }
}