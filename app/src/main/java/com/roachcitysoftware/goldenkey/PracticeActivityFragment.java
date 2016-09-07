package com.roachcitysoftware.goldenkey;


import android.app.Activity;
import android.app.AlarmManager;
import android.content.ContentProviderClient;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import java.util.Date;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class PracticeActivityFragment extends Fragment {

    private static final String TAG = PracticeActivityFragment.class.getSimpleName();
    private EditText mBlessing;
    private Button mNextButton;
    private class BlessingEntry {
        public long rowID;
        public String blessingText;
    }
    private BlessingEntry [] mBlessingList;
    private int mCurrentBlessing;
    private int mBlessingCount;
    private long mStartTime;
    private boolean mDone;
    private long mPracticeTime;
    private long mEventId;

    public PracticeActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_practice, container, false);
        mBlessing = (EditText) v.findViewById(R.id.blessing_items);
        mNextButton = (Button) v.findViewById(R.id.next_button);
        if ((savedInstanceState != null) && (savedInstanceState.getInt("blessingCount", 0) > 0)) {
            RestoreBlessingList(savedInstanceState);
        }
        else {
            LoadBlessingList(v);
        }
        if (mBlessingCount > 0)
            mBlessing.setText(mBlessingList[mCurrentBlessing].blessingText);
        else {
            mBlessing.setTextColor(Color.GRAY);
            mBlessing.setText(R.string.practice_hint);
        }
        if (mBlessingCount < 2) {
            mNextButton.setText(R.string.next_button_done);
            mDone = true;
        }
        mNextButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               String caption = mBlessing.getText().toString();
                                                ProcessUpdate(caption, v);
                                               if (mDone)
                                               {
                                                   recordEvent();
                                                   // Run follow-up activity (exit this activity)
                                                   startActivity(new Intent("com.roachcitysoftware.goldenkey.action.practice_feedback"));
                                                   Activity a = getActivity();
                                                   a.finish();
                                               }
                                               else
                                               {
                                                   mBlessing.setInputType(0);
                                                   ++mCurrentBlessing;
                                                   mBlessing.setText(mBlessingList[mCurrentBlessing].blessingText);
                                                   Date dt = new Date();
                                                   long now = dt.getTime();
                                                   if ((mCurrentBlessing == mBlessingCount - 1) ||
                                                           (now - mStartTime > mPracticeTime))
                                                   {
                                                       mNextButton.setText(R.string.next_button_done);
                                                       mDone = true;
                                                   }
                                               }
                                           }
                                       });
        mBlessing.setInputType(0);
        mBlessing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                mBlessing.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE);
            }
        });
        Log.d(TAG, "onCreateView");
        return v;
    }

    private void LoadBlessingList(View v) {
        mCurrentBlessing = 0;
        mBlessingCount = 0;
        mDone = true;
        mPracticeTime = AlarmManager.INTERVAL_FIFTEEN_MINUTES;
        mEventId = -1;
        ContentProviderClient cpc =
                v.getContext().getContentResolver().acquireContentProviderClient(GrandContract.CONTENT_URI_1);
        if (cpc == null) {
            Log.d(TAG, "LoadBlessingList failed - can't get Content Resolver");
            return;
        }
        BlessingProvider bp = (BlessingProvider) cpc.getLocalContentProvider();
        if (bp == null) {
            Log.d(TAG, "LoadBlessingList failed - can't get BlessingProvider");
            return;
        }
        // Do the real work here
        Cursor cursor = bp.query(GrandContract.CONTENT_URI_1, null, null, null, null);
        if ((cursor == null) || (!cursor.moveToFirst())) {
            Log.d(TAG, "LoadBlessingList failed - can't get blessings");
            return;
        }
        mBlessingCount = cursor.getCount();
        mBlessingList = new BlessingEntry[mBlessingCount];
        int current = 0;
        while (current < mBlessingCount) {
            BlessingEntry entry = new BlessingEntry();
            entry.rowID =
                    cursor.getLong(cursor.getColumnIndex(GrandContract.BlessingsColumn.ID));
            entry.blessingText =
                    cursor.getString(cursor.getColumnIndex(GrandContract.BlessingsColumn.BLESSING));
            mBlessingList[current] = entry;
            if (!cursor.moveToNext())
                break;
            ++current;
        }
        Date dt = new Date();
        mStartTime = dt.getTime();
        RandomizeList(mBlessingList, mStartTime);
        if (mBlessingCount > 0)
            mDone = false;
        // Clean up
        cursor.close();
        cpc.release();
        Log.d(TAG, "LoadBlessingList");

    }

    private void RestoreBlessingList (Bundle inState) {
        mDone = inState.getBoolean("done", true);
        mBlessingCount = inState.getInt("blessingCount");
        mCurrentBlessing = inState.getInt("currentBlessing", 0);
        long [] rowIDList  = inState.getLongArray("rowIDarray");
        String [] blessingTextList = inState.getStringArray("blessingTextarray");
        String msg;
        if (rowIDList == null) {
            msg = "null rowListID";
            mBlessing.setText(msg);
            mDone = true;
            mBlessingCount = 0;
            return;
        }
        if (blessingTextList == null){
            msg = "null blessingTextList";
            mBlessing.setText(msg);
            mDone = true;
            mBlessingCount = 0;
            return;
        }
        if (rowIDList.length != mBlessingCount){
            msg = "rowIDList length " + rowIDList.length + " mBlessingCount " + mBlessingCount;
            mBlessing.setText(msg);
            mDone = true;
            mBlessingCount = 0;
            return;
        }
        if (blessingTextList.length != mBlessingCount){
            msg = "blessingTextList length " + blessingTextList.length + " mBlessingCount " + mBlessingCount;
            mBlessing.setText(msg);
            mDone = true;
            mBlessingCount = 0;
            return;
        }
        mBlessingList = new BlessingEntry[mBlessingCount];
        int ndx;
        for (ndx = 0; ndx < mBlessingCount; ++ndx) {
            BlessingEntry entry = new BlessingEntry();
            entry.rowID = rowIDList[ndx];
            entry.blessingText = blessingTextList[ndx];
            mBlessingList[ndx] = entry;
        }
        mStartTime = inState.getLong("startTime");
        mPracticeTime = inState.getLong("practiceTime");
        mEventId = inState.getLong("eventId", -1);
        Log.d(TAG, "RestoreBlessingList");
    }


    private void RandomizeList(BlessingEntry[] list, long seed) {
        BlessingEntry temp;
        int a;
        int b;
        int size = list.length;
        Random rn = new Random(seed);
        Log.d(TAG, "Before Randomize");
        for (a = 0; a < size; ++a) {
            Log.d(TAG, "blessing[" + a + "] text: " + list[a].blessingText + " ID: " + list[a].rowID);
        }
        for (a = 0; a < size; ++a) {
            b = rn.nextInt(size);
            if (a != b) {
                temp = list[a];
                list[a] = list[b];
                list[b] = temp;
            }
            Log.d(TAG, "swapped " + a + ", " + b);
        }
        Log.d(TAG, "After Randomize");
        for (a = 0; a < size; ++a) {
            Log.d(TAG, "blessing[" + a + "] text: " + list[a].blessingText + " ID: " + list[a].rowID);
        }
    }

    private void ProcessUpdate (String caption, View v)
    {
        if (mCurrentBlessing >= mBlessingCount)
            return;     // illegal condition - possible in development code
       if (caption.compareTo(mBlessingList[mCurrentBlessing].blessingText) == 0)
           return;      // no change - no update
        // Set up access to BlessingProvider
        ContentProviderClient cpc =
                v.getContext().getContentResolver().acquireContentProviderClient(GrandContract.CONTENT_URI_1);
        if (cpc == null) {
            Log.d(TAG, "ProcessUpdate failed - can't get Content Resolver");
            return;
        }
        BlessingProvider bp = (BlessingProvider) cpc.getLocalContentProvider();
        if (bp == null) {
            Log.d(TAG, "ProcessUpdate failed - can't get BlessingProvider");
            return;
        }
        Uri target = ContentUris.withAppendedId(GrandContract.CONTENT_URI_1, mBlessingList[mCurrentBlessing].rowID);
        // Do the work
        caption = caption.trim();
        if (caption.isEmpty()) {
            bp.delete(target, null, null);
//            bp.delete(GrandContract.CONTENT_URI_1,null,null);
        }
        else
        {
            ContentValues entry = new ContentValues();
            entry.clear();
            entry.put(GrandContract.BlessingsColumn.BLESSING, caption);
            bp.update(target,entry,null,null);
        }
        // Clean up
        cpc.release();
        Log.d(TAG, "ProcessUpdate");
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        recordEvent();
        outState.putInt("blessingCount", mBlessingCount);
        outState.putInt("currentBlessing", mCurrentBlessing);
        if (mBlessingCount > 0) {
            long[] rowIDList = new long[mBlessingCount];
            String[] blessingTextList = new String[mBlessingCount];
            int ndx;
            for (ndx = 0; ndx < mBlessingCount; ++ndx) {
               rowIDList [ndx] = mBlessingList [ndx].rowID;
               blessingTextList [ndx] = mBlessingList [ndx].blessingText;
            }
            outState.putLongArray("rowIDarray", rowIDList);
            outState.putStringArray("blessingTextarray", blessingTextList);
        }
        outState.putLong("startTime", mStartTime);
        outState.putLong("practiceTime", mPracticeTime);
        outState.putBoolean("done", mDone);
        outState.putLong("eventId", mEventId);
        Log.d(TAG, "onSaveInstanceState");
    }

    private void recordEvent () {
        View v = getView();
        if (v == null){
            Log.d(TAG, "recordEvent failed - can't get View");
            return;
        }
        Context ctx = v.getContext();
        if (ctx == null){
            Log.d(TAG, "recordEvent failed - can't get Context");
            return;
        }
        ContentProviderClient cpc =
                ctx.getContentResolver().acquireContentProviderClient(GrandContract.CONTENT_URI_2);
        if (cpc == null){
            Log.d(TAG, "recordEvent failed - can't get ContentProviderClient");
            return;
        }
        BlessingProvider bp = (BlessingProvider) cpc.getLocalContentProvider();
        if (bp == null){
            Log.d(TAG, "recordEvent failed - can't get BlessingProvider");
            cpc.release();
            return;
        }
        boolean mComplete = mDone && (mBlessingCount > 0);
        String done = mComplete ? "Done" : "No";
        if (mEventId == -1) {
            mEventId = bp.onAddEvent(GrandContract.PRACTICE_EVENT, done);
            Log.d(TAG, "recordEvent success - onAddEvent " + GrandContract.PRACTICE_EVENT +
                    " " + done);
        } else
        {
            bp.onUpdateEvent(mEventId, GrandContract.PRACTICE_EVENT, done);
            Log.d(TAG, "recordEvent success - onUpdateEvent " + GrandContract.PRACTICE_EVENT +
                    " " + done + "eventID: " + Long.toString(mEventId));
        }
        cpc.release();
    }
}

