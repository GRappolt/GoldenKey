package com.roachcitysoftware.goldenkey;


import android.content.ContentProviderClient;
import android.database.Cursor;
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
        public boolean changed;
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
        Log.d(TAG, "onCreateView");
        return v;
    }

    private void LoadBlessingList (View v)
    {
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
        if (!cursor.moveToFirst()) {
            Log.d(TAG, "LoadBlessingList failed - can't get BlessingProvider");
            return;
        }
        mBlessingCount = cursor.getCount();
        mBlessingList = new BlessingEntry [mBlessingCount];
        int current = 0;
        while (current < mBlessingCount)
        {
            BlessingEntry entry = new BlessingEntry();
            entry.rowID =
                    cursor.getLong(cursor.getColumnIndex(GrandContract.BlessingsColumn.ID));
            entry.blessingText =
                    cursor.getString(cursor.getColumnIndex(GrandContract.BlessingsColumn.BLESSING));
            entry.changed = false;
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

    private void RandomizeList (BlessingEntry [] list, long seed)
    {
        BlessingEntry temp;
        int a;
        int b;
        int size = list.length;
        Random rn = new Random(seed);
        for (a = 0; a < size; ++a)
        {
            b = rn.nextInt(size);
            if (a!= b) {
                temp = list [a];
                list [a] = list [b];
                list [b] = temp;
            }
        }
    }

}

