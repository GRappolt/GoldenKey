package com.roachcitysoftware.goldenkey;


import android.content.ContentProviderClient;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
        LoadBlessingList(v);
        if (mBlessingCount > 0)
            mBlessing.setText(mBlessingList[mCurrentBlessing].blessingText);
        mNextButton.setOnClickListener(new View.OnClickListener() {
                                           @Override
                                           public void onClick(View v) {
                                               String caption = mBlessing.getText().toString();
                                                ProcessUpdate(caption, v);
                                               ++mCurrentBlessing;
                                               if (mCurrentBlessing >= mBlessingCount)
                                               {
                                                   // Run follow-up activity (exit this activity)
                                                   return;
                                               }
                                               else
                                               {
                                                   mBlessing.setText(mBlessingList[mCurrentBlessing].blessingText);
                                                   if (mCurrentBlessing == mBlessingCount - 1)
                                                       mNextButton.setText(R.string.next_button_done);
                                               }
                                           }
                                       });
                Log.d(TAG, "onCreateView");
                return v;
            }

    private void LoadBlessingList(View v) {
        mCurrentBlessing = 0;
        mBlessingCount = 0;
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
        try {
            if (!cursor.moveToFirst()) {
                Log.d(TAG, "LoadBlessingList failed - can't get blessings");
                return;
            }
        }
        catch (NullPointerException e) {
            Log.d(TAG, "LoadBlessingList failed - null cursor contents");
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
        long seed = dt.getTime();
        RandomizeList(mBlessingList, seed);
        // Clean up
        cursor.close();
        cpc.release();
        Log.d(TAG, "LoadBlessingList");

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

}

