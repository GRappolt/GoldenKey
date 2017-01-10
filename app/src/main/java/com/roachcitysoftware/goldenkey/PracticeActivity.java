package com.roachcitysoftware.goldenkey;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class PracticeActivity extends AppCompatActivity
        implements PracticeActivityFragment.FeedBackDisplay {

    private static final String TAG = PracticeActivity.class.getSimpleName();
    private int mPracticeRun;
    private int mMaxPracticeRun;
    private String mPraise;
    private String [] mPraiseList;
    private String mPracticeFeedback;
    private String mMaxFeedback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice);
        Log.d(TAG, "onCreate");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_practice, menu);
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
            return true;
        }

        if (id == android.R.id.home) {
            Log.d(TAG, "onOptionsItemSelected HOME");
            RecordFragmentEvent();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        Log.d(TAG, "onBackPressed");
        RecordFragmentEvent();
        finish();
    }

    @Override
    public boolean onNavigateUp(){
        Log.d(TAG, "onNavigateUp");
        RecordFragmentEvent();
        return super.onNavigateUp();
    }

    private void RecordFragmentEvent ()
    {
        Log.d(TAG, "RecordFragmentEvent starting");
        FragmentManager fm = getSupportFragmentManager();
        if (fm == null){
            Log.d(TAG, "RecordFragmentEvent failed - null FragmentManager");
            return;
        }
        android.support.v4.app.Fragment frag = fm.findFragmentById(R.id.practice_fragment);
        if (frag == null){
            Log.d(TAG, "RecordFragmentEvent failed - null Fragment");
            return;
        }
        PracticeActivityFragment paf;
        if (frag instanceof PracticeActivityFragment)
            paf = (PracticeActivityFragment) frag;
        else {
            Log.d(TAG, "RecordFragmentEvent failed - wrong fragment type");
            return;
        }
        paf.recordEvent();
        Log.d(TAG, "RecordFragmentEvent succeeded (done)");
    }

    private void InitializeData(){
        Log.d(TAG, "InitializeData starting");
        long dateTime;
        Date current = new Date();
        Date previous = new Date();
        boolean firstRun = true;
        int runLength = 0;
        int daysBefore = 0;
        mPracticeRun = 0;
        mMaxPracticeRun = 0;
        ContentResolver cr = getApplicationContext().getContentResolver();
        if (cr == null) {
            Log.d(TAG, "InitializeData failed - can't get Content Resolver");
            return;
        }
        ContentProviderClient cpc =
                cr.acquireContentProviderClient(GrandContract.CONTENT_URI_2);
        if (cpc == null) {
            Log.d(TAG, "InitializeData failed - can't get Content Resolver");
            return;
        }
        BlessingProvider bp = (BlessingProvider) cpc.getLocalContentProvider();
        if (bp == null) {
            Log.d(TAG, "InitializeData failed - can't get BlessingProvider");
            cpc.release();
            return;
        }
        // Retrieve Practice history, count continuous Practice sessions
        Cursor practiceCursor = bp.query(GrandContract.CONTENT_URI_2, null,
                " eventType = '" + GrandContract.PRACTICE_EVENT + "' AND  extraData = 'Done' ",
                null, null);
        if ((practiceCursor != null) && (practiceCursor.moveToFirst())){
            dateTime =
                    practiceCursor.getLong(practiceCursor.getColumnIndex(GrandContract.HistoryColumn.DATE_TIME));
            current.setTime(dateTime);
            runLength = 1;
            while (practiceCursor.moveToNext()){
                dateTime =
                        practiceCursor.getLong(practiceCursor.getColumnIndex(GrandContract.HistoryColumn.DATE_TIME));
                previous.setTime(dateTime);
                daysBefore = FindDaysBefore(current, previous);
                switch (daysBefore){
                    case 0:
                        break;
                    case 1:
                        runLength++;
                        break;
                    case 2:
                        if (firstRun)
                            mPracticeRun = runLength;
                        firstRun = false;
                        if (mMaxPracticeRun < runLength)
                            mMaxPracticeRun = runLength;
                        runLength = 1;      // reset to minimum
                        break;
                    default:
                        Log.d(TAG, "InitializeData unexpected error - daysBefore out of range");
                }
                Log.d(TAG, "InitializeData daysBefore: " + daysBefore + " runLength: " + runLength +
                        " mMaxPracticeRun: " + mMaxPracticeRun);
                current.setTime(dateTime);
            }
            // if in earliest run when data ends
            if (daysBefore < 2){
                if (firstRun)
                    mPracticeRun = runLength;
                if (mMaxPracticeRun < runLength)
                    mMaxPracticeRun = runLength;
            }
            practiceCursor.close();
        } else {
            Log.d(TAG, "failed to get Practice history");
        }
        cpc.release();
        Log.d(TAG, "InitializeData done");
    }

    private int FindDaysBefore(Date current, Date previous){
        // Verify times are within 48 hours
        long currentTime = current.getTime();
        long previousTime = previous.getTime();
        Log.d(TAG, "FindDaysBefore currentTime: " + currentTime + " previousTime: " + previousTime);
        if (currentTime - previousTime > 2 * AlarmManager.INTERVAL_DAY){
            Log.d(TAG, "FindDaysBefore first return");
           return 2;
        }
        // Check day-of-week difference
        GregorianCalendar curCal = new GregorianCalendar();
        curCal.setTime(current);
        int currentDay = curCal.get(Calendar.DAY_OF_WEEK);
        GregorianCalendar prevCal = new GregorianCalendar();
        prevCal.setTime(previous);
        int previousDay = prevCal.get(Calendar.DAY_OF_WEEK);
        Log.d(TAG, "FindDaysBefore currentDay: " + currentDay + " previousDay: " + previousDay);
        if (currentDay == previousDay){
            Log.d(TAG, "FindDaysBefore second return");
            return 0;
        }
        if (currentDay - previousDay == 1){
            Log.d(TAG, "FindDaysBefore third return");
            return 1;
        }
        if ((currentDay == Calendar.SUNDAY) && (previousDay == Calendar.SATURDAY)){
            Log.d(TAG, "FindDaysBefore fourth return");
            return 1;
        }
        Log.d(TAG, "FindDaysBefore fifth return");
        return 2;
    }

    private void BuildStrings(){
        Log.d(TAG, "BuildStrings starting");
        Resources res = getResources();
        mPraiseList = res.getStringArray(R.array.praise_list);
        Date dt = new Date();
        long seed = dt.getTime();
        int size = mPraiseList.length;
        Random rn = new Random(seed);
        int selection = rn.nextInt(size);
        mPraise = mPraiseList[selection];
        if (mPracticeRun == 1)
            mPracticeFeedback = getString(R.string.practice_feedback_base) + " " +
                    Integer.toString(mPracticeRun) + " " + getString(R.string.practice_feedback_single);
        else
            mPracticeFeedback = getString(R.string.practice_feedback_base) + " " +
                    Integer.toString(mPracticeRun) + " " + getString(R.string.practice_feedback_plural);
        mMaxFeedback = getString(R.string.max_practice_feedback) + " " +
                Integer.toString(mMaxPracticeRun);
        Log.d(TAG, "BuildStrings done");
    }

    public void displayFeedback () {
        Log.d(TAG, "DisplayFeedback");
        InitializeData();
        Log.d(TAG, Integer.toString(mPracticeRun));
        if (mPracticeRun < 1){
            // Don't display feedback for zero list items
            finish();
            return;
        }
        BuildStrings();
        AlertDialog.Builder feedbackDialog = new AlertDialog.Builder(
                PracticeActivity.this);
        Log.d(TAG, "DisplayFeedback dialog builder created");
        // Setting Dialog Title
        feedbackDialog.setTitle("Golden Key");
        // Setting Dialog Message
        feedbackDialog.setMessage
                (mPraise + "\n" + mPracticeFeedback + "\n" + mMaxFeedback );
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
        Log.d(TAG, "DisplayFeedback dialog before show");
        feedbackDialog.show();
        Log.d(TAG, "DisplayFeedback dialog after show");
    }
}
