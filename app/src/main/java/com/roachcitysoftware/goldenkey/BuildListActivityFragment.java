package com.roachcitysoftware.goldenkey;

import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.Date;
import java.util.Random;

/**
 * A placeholder fragment containing a simple view.
 */
public class BuildListActivityFragment extends Fragment  {

//    private static final String TAG = BuildListActivityFragment.class.getSimpleName();
    private EditText mNewBlessing;
    private boolean mHintsShown;
    private Button mHintButton;
    private TextView mHintText;
    private String [] mHintList;
    private int mCurrentHint;
    private long mEventId;
    private int mItemsAdded;

    public BuildListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_build_list, container, false);
        mNewBlessing = (EditText) v.findViewById(R.id.new_list_items);
        Button mAddButton = (Button) v.findViewById(R.id.add_button);
        mAddButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ContentProviderClient cpc =
                        v.getContext().getContentResolver().acquireContentProviderClient(GrandContract.CONTENT_URI_1);
                if (cpc == null) {
                    return;
                }
                BlessingProvider bp = (BlessingProvider) cpc.getLocalContentProvider();
                if (bp == null) {
                    return;
                }
                boolean added = bp.onAdd(mNewBlessing.getText().toString());
                cpc.release();
                if (added) {
                    mNewBlessing.setText("");
                    ++mItemsAdded;
                }
            }
        });
        // Set mHinntsShown, mCurrentHint and mHintList from savedInstanceState
        if (savedInstanceState != null)
        {
            if (savedInstanceState.getInt("hintsShown") > 0) {
                mHintsShown = true;
                mCurrentHint = savedInstanceState.getInt("currentHint");
                mHintList = savedInstanceState.getStringArray("hintList");
            } else {
                mHintsShown = false;
                mCurrentHint = 0;
            }
            mEventId = savedInstanceState.getLong("eventId", -1);
            mItemsAdded = savedInstanceState.getInt("itemsAdded", 0);
        }
        else {
            mHintsShown = false;
            mCurrentHint = 0;
            mEventId = -1;
            mItemsAdded = 0;
        }
        mHintButton = (Button) v.findViewById(R.id.hint_button);
        mHintText = (TextView) v.findViewById(R.id.hint_items);
        if (mHintsShown) {
            mHintButton.setText(R.string.hint_button_2);
            mHintText.setText(mHintList[mCurrentHint]);
            mHintText.setVisibility(View.VISIBLE);
        }
        mHintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mHintsShown)
                    GetNextHint();
                else
                    StartHints();
            }
        });
        v.getContext().sendBroadcast(new Intent(
                "com.roachcitysoftware.goldenkey.action.REMINDER").putExtra(
                "Target", ReminderService.BUILD_LIST).putExtra("Action", ReminderService.CANCEL));
        return v;
    }

    public void StartHints ()
    {
        mHintsShown = true;
        mHintButton.setText(R.string.hint_button_2);
        // Set up hint list
        Resources res = getResources();
        mHintList = res.getStringArray(R.array.hint_list);
        Date dt = new Date();
        long seed = dt.getTime();
        RandomizeList(mHintList, seed);
        // Set initial hint text
        mHintText.setText(mHintList[mCurrentHint]);
        mHintText.setVisibility(View.VISIBLE);
    }

    public void GetNextHint ()
    {
        ++mCurrentHint;
        if (mCurrentHint >= mHintList.length)
            mCurrentHint = 0;
        mHintText.setText(mHintList[mCurrentHint]);
    }

    public void RandomizeList (String [] list, long seed)
    {
        String temp;
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

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mHintsShown) {
            outState.putInt("hintsShown", 1);
            outState.putInt("currentHint", mCurrentHint);
            outState.putStringArray("hintList", mHintList);
        }
        else {
            outState.putInt("hintsShown", 0);
        }
        outState.putLong("eventId", mEventId);
        outState.putInt("itemsAdded", mItemsAdded);
    }

    public void recordEvent () {
        View v = getView();
        if (v == null){
            return;
        }
        Context ctx = v.getContext();
        if (ctx == null){
            return;
        }
        ctx.sendBroadcast(new Intent(
                "com.roachcitysoftware.goldenkey.action.REMINDER").putExtra(
                "Target", ReminderService.BUILD_LIST).putExtra("Action", ReminderService.CANCEL));

        ContentProviderClient cpc =
                ctx.getContentResolver().acquireContentProviderClient(GrandContract.CONTENT_URI_2);
        if (cpc == null){
            return;
        }
        BlessingProvider bp = (BlessingProvider) cpc.getLocalContentProvider();
        if (bp == null){
            cpc.release();
            return;
        }
        String itemsAdded = Integer.toString(mItemsAdded);
        if (mEventId == -1) {
            mEventId = bp.onAddEvent(GrandContract.BUILD_LIST_EVENT, itemsAdded);
        } else
        {
            bp.onUpdateEvent(mEventId, GrandContract.BUILD_LIST_EVENT, itemsAdded);
         }
        cpc.release();
    }
}
