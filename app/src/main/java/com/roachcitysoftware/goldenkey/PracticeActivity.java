package com.roachcitysoftware.goldenkey;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.Cursor;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

public class PracticeActivity extends AppCompatActivity
        implements PracticeActivityFragment.FeedBackDisplay {

//    private static final String TAG = PracticeActivity.class.getSimpleName();
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_practice, menu);
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
            RecordFragmentEvent();
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        RecordFragmentEvent();
        finish();
    }

    @Override
    public boolean onNavigateUp(){
        RecordFragmentEvent();
        return super.onNavigateUp();
    }

    private void RecordFragmentEvent ()
    {
        FragmentManager fm = getSupportFragmentManager();
        if (fm == null){
            return;
        }
        android.support.v4.app.Fragment frag = fm.findFragmentById(R.id.practice_fragment);
        if (frag == null){
            return;
        }
        PracticeActivityFragment paf;
        if (frag instanceof PracticeActivityFragment)
            paf = (PracticeActivityFragment) frag;
        else {
            return;
        }
        paf.recordEvent();
    }

    private void InitializeData(){
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
            return;
        }
        ContentProviderClient cpc =
                cr.acquireContentProviderClient(GrandContract.CONTENT_URI_2);
        if (cpc == null) {
            return;
        }
        BlessingProvider bp = (BlessingProvider) cpc.getLocalContentProvider();
        if (bp == null) {
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
                }
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
        }
        cpc.release();
    }

    private int FindDaysBefore(Date current, Date previous){
        // Verify times are within 48 hours
        long currentTime = current.getTime();
        long previousTime = previous.getTime();
        if (currentTime - previousTime > 2 * AlarmManager.INTERVAL_DAY){
           return 2;
        }
        // Check day-of-week difference
        GregorianCalendar curCal = new GregorianCalendar();
        curCal.setTime(current);
        int currentDay = curCal.get(Calendar.DAY_OF_WEEK);
        GregorianCalendar prevCal = new GregorianCalendar();
        prevCal.setTime(previous);
        int previousDay = prevCal.get(Calendar.DAY_OF_WEEK);
        if (currentDay == previousDay){
            return 0;
        }
        if (currentDay - previousDay == 1){
            return 1;
        }
        if ((currentDay == Calendar.SUNDAY) && (previousDay == Calendar.SATURDAY)){
            return 1;
        }
        return 2;
    }

    private void BuildStrings(){
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
    }

    public void displayFeedback () {
        InitializeData();
        if (mPracticeRun < 1){
            // Don't display feedback for zero list items
            finish();
            return;
        }
        BuildStrings();
        AlertDialog.Builder feedbackDialog = new AlertDialog.Builder(
                PracticeActivity.this);
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
        feedbackDialog.show();
    }
}
