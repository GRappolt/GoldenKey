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

    private static final String TAG = BuildListActivity.class.getSimpleName();
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
        Log.d(TAG, "onCreate");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_build_list, menu);
        Log.d(TAG, "onCreateOptionsMenu");
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Log.d(TAG, "onOptionsItemSelected item " + id);

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Log.d(TAG, "onOptionsItemSelected ACTION SETTINGS");
            return true;
        }

        if (id == android.R.id.home) {
            Log.d(TAG, "onOptionsItemSelected HOME");
            DisplayFeedback();
            return true;
        }

        Log.d(TAG, "onOptionsItemSelected OTHER");
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        Log.d(TAG, "onBackPressed");
        DisplayFeedback();
    }

    @Override
    public boolean onNavigateUp(){
        Log.d(TAG, "onNavigateUp");
        DisplayFeedback();
        return super.onNavigateUp();
    }

    private void DisplayFeedback () {
        Log.d(TAG, "DisplayFeedback");
        RecordFragmentEvent();
        InitializeData();
        Log.d(TAG, Integer.toString(mBuildListRun));
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
        Log.d(TAG, "InitializeData starting");
        String itemCount;
        int itemsAdded;
        mBuildListRun = 0;
        mMaxListRun = 0;
        mListSizeRun = 0;
        ContentResolver cr = getApplicationContext().getContentResolver();
        if (cr == null) {
            Log.d(TAG, "InitializeData failed - can't get Content Resolver");
            return;
        }
        ContentProviderClient cpch =
                cr.acquireContentProviderClient(GrandContract.CONTENT_URI_2);
        if (cpch == null) {
            Log.d(TAG, "InitializeData failed - can't get history Content Provider Client");
            return;
        }
        BlessingProvider bph = (BlessingProvider) cpch.getLocalContentProvider();
        if (bph == null) {
            Log.d(TAG, "InitializeData failed - can't get history BlessingProvider");
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
            Log.d(TAG, "Initial item count: " + itemCount);
            itemsAdded = Integer.parseInt(itemCount);
            Log.d(TAG, "Initial itema added: " + Integer.toString(itemsAdded));
            mBuildListRun = itemsAdded;
            mMaxListRun = mBuildListRun;
            while (buildCursor.moveToNext()){
                itemCount =
                        buildCursor.getString(buildCursor.getColumnIndex(GrandContract.HistoryColumn.EXTRA_DATA));
                Log.d(TAG, "Item count: " + itemCount);
                itemsAdded = Integer.parseInt(itemCount);
                if (itemsAdded > mMaxListRun)
                    mMaxListRun = itemsAdded;
            }
            buildCursor.close();
        } else {
            Log.d(TAG, "failed to get Build List history");
        }
        cpch.release();
        ContentProviderClient cpcb =
                cr.acquireContentProviderClient(GrandContract.CONTENT_URI_1);
        if (cpcb == null) {
            Log.d(TAG, "InitializeData failed - can't get blessing Content Provider Client");
            return;
        }
        BlessingProvider bpb = (BlessingProvider) cpcb.getLocalContentProvider();
        if (bpb == null) {
            Log.d(TAG, "InitializeData failed - can't get blessing BlessingProvider");
            cpcb.release();
            return;
        }
        // Do the real work here
        Cursor blessingCursor = bpb.query(GrandContract.CONTENT_URI_1, null, null, null, null);
        if ((blessingCursor == null) || (!blessingCursor.moveToFirst())) {
            Log.d(TAG, "failed to get blessings");
            cpcb.release();
            return;
        }
        mListSizeRun = blessingCursor.getCount();
        blessingCursor.close();
        cpcb.release();
        Log.d(TAG, "InitializeData done");
    }

    private void BuildStrings(){
        Log.d(TAG, "BuildStrings starting");
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
        Log.d(TAG, "BuildStrings done");
    }

    private void RecordFragmentEvent ()
    {
        Log.d(TAG, "RecordFragmentEvent starting");
        FragmentManager fm = getSupportFragmentManager();
        if (fm == null){
            Log.d(TAG, "RecordFragmentEvent failed - null FragmentManager");
            return;
        }
        android.support.v4.app.Fragment frag = fm.findFragmentById(R.id.build_list_fragment);
        if (frag == null){
            Log.d(TAG, "RecordFragmentEvent failed - null Fragment");
            return;
        }
        BuildListActivityFragment blaf;
        if (frag instanceof BuildListActivityFragment)
            blaf = (BuildListActivityFragment) frag;
        else {
            Log.d(TAG, "RecordFragmentEvent failed - wrong fragment type");
            return;
        }
        blaf.recordEvent();
        Log.d(TAG, "RecordFragmentEvent succeeded (done)");
    }
}
