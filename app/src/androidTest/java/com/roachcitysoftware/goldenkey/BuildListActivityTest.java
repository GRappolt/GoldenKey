package com.roachcitysoftware.goldenkey;

import android.test.ActivityInstrumentationTestCase2;
import android.test.ViewAsserts;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

/**
 * Unit tests for BuildListActivity by GeorgeR created 11/12/2015.
 */
public class BuildListActivityTest extends ActivityInstrumentationTestCase2<BuildListActivity>
{
    private BuildListActivity mBuildListActivity;
    private TextView mNewListItems;
    private TextView mHintItems;
    private Button mAddButton;
    private Button mHintsButon;

    public BuildListActivityTest () { super(BuildListActivity.class);}

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        mBuildListActivity = getActivity();
        mNewListItems = mBuildListActivity.findViewById(R.id.new_list_items);
        mHintItems = mBuildListActivity.findViewById(R.id.example_items);
        mAddButton = mBuildListActivity.findViewById(R.id.add_button);
        mHintsButon = mBuildListActivity.findViewById(R.id.example_button);
    }

    public void testPreconditions() {
        assertNotNull("mBuildListActivity is null.", mBuildListActivity);
        assertNotNull("mNewListItems is null.", mNewListItems);
        assertNotNull("mHintItems is null.", mHintItems);
        assertNotNull("mAddButton is null.", mAddButton);
        assertNotNull("mHintsButon is null.", mHintsButon);
    }

    public void testBuildListActivityAddButton_text ()
    {
        final String expected = mBuildListActivity.getString(R.string.add_button);
        final String actual = mAddButton.getText().toString();
        assertEquals(expected, actual);
    }

    public void testBuildListActivityAddButton_layout ()
    {
        final View decorView = mBuildListActivity.getWindow().getDecorView();

        ViewAsserts.assertOnScreen(decorView, mAddButton);

        final ViewGroup.LayoutParams layoutParams = mAddButton.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }


    public void testBuildListActivityHintsButton_layout ()
    {
        final View decorView = mBuildListActivity.getWindow().getDecorView();

        ViewAsserts.assertOnScreen(decorView, mHintsButon);

        final ViewGroup.LayoutParams layoutParams = mHintsButon.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.WRAP_CONTENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void testBuildListActivityNewListItems_layout ()
    {
        final View decorView = mBuildListActivity.getWindow().getDecorView();

        ViewAsserts.assertOnScreen(decorView, mNewListItems);

        final ViewGroup.LayoutParams layoutParams = mNewListItems.getLayoutParams();
        assertNotNull(layoutParams);
        assertEquals(layoutParams.width, WindowManager.LayoutParams.MATCH_PARENT);
        assertEquals(layoutParams.height, WindowManager.LayoutParams.WRAP_CONTENT);
    }

    public void testBuildListActivityHintItems_layout ()
    {
        final View decorView = mBuildListActivity.getWindow().getDecorView();

        ViewAsserts.assertOnScreen(decorView, mHintItems);
        assertTrue(View.INVISIBLE == mHintItems.getVisibility());
    }
}
