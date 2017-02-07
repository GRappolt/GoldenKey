package com.roachcitysoftware.goldenkey;

import android.support.v4.app.FragmentManager;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.app.AlertDialog;
import android.content.DialogInterface;
import java.util.Date;
import java.util.Random;


public class BuildListActivity extends AppCompatActivity {

//    private static final String TAG = BuildListActivity.class.getSimpleName();
    private int mBuildListRun;
    private int mMaxListRun;
    private int mListSizeRun;
    private String mPraise;
    private String mBuildListFeedback;
    private String mMaxListFeedback;
    private String mListSizeFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_list);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_build_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == android.R.id.home) {
            DisplayFeedback();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        DisplayFeedback();
    }

    @Override
    public boolean onNavigateUp(){
        DisplayFeedback();
        return super.onNavigateUp();
    }

    private void DisplayFeedback () {
        RecordFragmentEvent();
        InitializeData();
        if (mBuildListRun < 1){
            // Don't display feedback for zero list items
            finish();
            return;
        }
        BuildStrings();
        AlertDialog.Builder feedbackDialog = new AlertDialog.Builder(
                BuildListActivity.this);
        // Setting Dialog Title
        feedbackDialog.setTitle("Golden Key");
        // Setting Dialog Message
        feedbackDialog.setMessage
                (mPraise + "\n" + mBuildListFeedback + "\n" + mMaxListFeedback + "\n" + mListSizeFeedback);
        // Setting Icon to Dialog
        feedbackDialog.setIcon(R.mipmap.gk_icon);
        // Setting Positive "Yes" Button
        feedbackDialog.setPositiveButton("DONE",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });
        // Showing Alert Message
        feedbackDialog.show();
    }

    private void InitializeData(){
        String itemCount;
        int itemsAdded;
        mBuildListRun = 0;
        mMaxListRun = 0;
        mListSizeRun = 0;
        ContentResolver cr = getApplicationContext().getContentResolver();
        if (cr == null) {
            return;
        }
        ContentProviderClient cpch =
                cr.acquireContentProviderClient(GrandContract.CONTENT_URI_2);
        if (cpch == null) {
            return;
        }
        BlessingProvider bph = (BlessingProvider) cpch.getLocalContentProvider();
        if (bph == null) {
            cpch.release();
            return;
        }
        // Retrieve Build List history, get items added in each session
        Cursor buildCursor = bph.query(GrandContract.CONTENT_URI_2, null,
                " eventType = '" + GrandContract.BUILD_LIST_EVENT + "'",
                null, GrandContract.DEFAULT_SORT_2);
        if ((buildCursor != null) && (buildCursor.moveToFirst())){
            itemCount =
                    buildCursor.getString(buildCursor.getColumnIndex(GrandContract.HistoryColumn.EXTRA_DATA));
            itemsAdded = Integer.parseInt(itemCount);
            mBuildListRun = itemsAdded;
            mMaxListRun = mBuildListRun;
            while (buildCursor.moveToNext()){
                itemCount =
                        buildCursor.getString(buildCursor.getColumnIndex(GrandContract.HistoryColumn.EXTRA_DATA));
                itemsAdded = Integer.parseInt(itemCount);
                if (itemsAdded > mMaxListRun)
                    mMaxListRun = itemsAdded;
            }
            buildCursor.close();
        }
        cpch.release();
        ContentProviderClient cpcb =
                cr.acquireContentProviderClient(GrandContract.CONTENT_URI_1);
        if (cpcb == null) {
            return;
        }
        BlessingProvider bpb = (BlessingProvider) cpcb.getLocalContentProvider();
        if (bpb == null) {
            cpcb.release();
            return;
        }
        // Do the real work here
        Cursor blessingCursor = bpb.query(GrandContract.CONTENT_URI_1, null, null, null, null);
        if ((blessingCursor == null) || (!blessingCursor.moveToFirst())) {
            cpcb.release();
            return;
        }
        mListSizeRun = blessingCursor.getCount();
        blessingCursor.close();
        cpcb.release();
    }

    private void BuildStrings(){
        Resources res = getResources();
        String [] praiseList = res.getStringArray(R.array.praise_list);
        Date dt = new Date();
        long seed = dt.getTime();
        int size = praiseList.length;
        Random rn = new Random(seed);
        int selection = rn.nextInt(size);
        mPraise = praiseList[selection];
        if (mBuildListRun == 1)
            mBuildListFeedback = getString(R.string.build_list_feedback_base) + " " +
                    Integer.toString(mBuildListRun) + " " + getString(R.string.build_list_feedback_single);
        else
            mBuildListFeedback = getString(R.string.build_list_feedback_base) + " " +
                    Integer.toString(mBuildListRun) + " " + getString(R.string.build_list_feedback_plural);
        mMaxListFeedback = getString(R.string.max_list_feedback) + " " + Integer.toString(mMaxListRun);
        mListSizeFeedback = getString(R.string.list_size_feedback_base) + " " +
                Integer.toString(mListSizeRun) + " " + getString(R.string.list_size_feedback_end);
    }

    private void RecordFragmentEvent ()
    {
        FragmentManager fm = getSupportFragmentManager();
        if (fm == null){
            return;
        }
        android.support.v4.app.Fragment frag = fm.findFragmentById(R.id.build_list_fragment);
        if (frag == null){
            return;
        }
        BuildListActivityFragment blaf;
        if (frag instanceof BuildListActivityFragment)
            blaf = (BuildListActivityFragment) frag;
        else {
            return;
        }
        blaf.recordEvent();
    }
}
