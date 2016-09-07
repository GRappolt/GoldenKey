package com.roachcitysoftware.goldenkey;


import android.app.Activity;
import android.content.ContentProviderClient;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class PracticeFeedbackActivityFragment extends Fragment {

    private static final String TAG = PracticeFeedbackActivityFragment.class.getSimpleName();
    private int mPracticeRun;
    private int mMaxPracticeRun;
    private String mPraise;
    private String mPracticeFeedback;
    private String mMaxFeedback;
    private TextView mPraiseText;
    private TextView mPracticeText;
    private TextView mMaxText;
    private Button mCloseButton;

    public PracticeFeedbackActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView starting");
        View v = inflater.inflate(R.layout.fragment_practice_feedback_activity, container, false);
        InitializeData(v);
        BuildStrings();
        mPraiseText = (TextView)v.findViewById(R.id.praise);
        mPraiseText.setText(mPraise);
        mPracticeText = (TextView)v.findViewById(R.id.practice_feedback);
        mPracticeText.setText(mPracticeFeedback);
        mMaxText = (TextView)v.findViewById(R.id.max_feedback);
        mMaxText.setText(mMaxFeedback);
        mCloseButton = (Button) v.findViewById(R.id.close_button);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Activity a = getActivity();
                a.finish();
            }
        });
        Log.d(TAG, "onCreateView done");
        return v;
    }

    private void InitializeData(View v){
        Log.d(TAG, "InitializeData starting");
        long dateTime = 0;
        Date current = new Date();
        GregorianCalendar curCal = new GregorianCalendar();
        int currentDay = 0;
        Date previous = new Date();
        GregorianCalendar prevCal = new GregorianCalendar();
        int previousDay = 0;
        boolean firstRun = true;
        int runLength = 0;
        boolean inRun = true;
        mPracticeRun = 0;
        mMaxPracticeRun = 0;
        ContentProviderClient cpc =
                v.getContext().getContentResolver().acquireContentProviderClient(GrandContract.CONTENT_URI_2);
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
            curCal.setTime(current);
            currentDay = curCal.get(Calendar.DAY_OF_MONTH);
            runLength = 1;
            while (practiceCursor.moveToNext()){
                dateTime =
                        practiceCursor.getLong(practiceCursor.getColumnIndex(GrandContract.HistoryColumn.DATE_TIME));
                previous.setTime(dateTime);
                prevCal.setTime(previous);
                previousDay = prevCal.get(Calendar.DAY_OF_MONTH);
                inRun = (currentDay - previousDay == 1) || ((currentDay == 1) &&
                        (previousDay == prevCal.getMaximum(Calendar.DAY_OF_MONTH)));
                if (inRun) {
                    runLength++;
                } else {
                    if (firstRun)
                        mPracticeRun = runLength;
                    firstRun = false;
                    if (mMaxPracticeRun < runLength)
                        mMaxPracticeRun = runLength;
                    runLength = 1;      // reset to minimum
                }
                currentDay = previousDay;
            }
            // if in earliest run when data ends
            if (inRun){
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

    private void BuildStrings(){
        Log.d(TAG, "BuildStrings starting");
        mPraise = "Way to go!";
        if (mPracticeRun == 1)
            mPracticeFeedback = getString(R.string.practice_feedback_base) +
                    Integer.toString(mPracticeRun) + getString(R.string.practice_feedback_single);
        else
            mPracticeFeedback = getString(R.string.practice_feedback_base) +
                    Integer.toString(mPracticeRun) + getString(R.string.practice_feedback_plural);
        mMaxFeedback = getString(R.string.max_practice_feedback) +
                Integer.toString(mMaxPracticeRun);
        Log.d(TAG, "BuildStrings done");
    }
}
