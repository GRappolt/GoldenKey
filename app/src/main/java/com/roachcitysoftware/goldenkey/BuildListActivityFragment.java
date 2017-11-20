package com.roachcitysoftware.goldenkey;

import android.content.ContentProviderClient;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ListView;

import java.util.Date;
import java.util.Random;

/**
 * A placeholder fragment containing a simple view.
 */
public class BuildListActivityFragment extends Fragment  {

//    private static final String TAG = BuildListActivityFragment.class.getSimpleName();
    private EditText mNewBlessing;
    private boolean mExamplesShown;
    private String [] mExampleList;
    private long mEventId;
    private int mItemsAdded;
    private TextView mItemCount;
    private TextView mNewItemCount;
    private RelativeLayout mExampleLayout;

    public BuildListActivityFragment() {
    }

    public class ReturnWatcher implements TextWatcher  {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            for (int j = 0; j < i2; ++j) {
                if (charSequence.charAt(i + j) == '\n') {
                    View v = getView();
                    AddItem(v);
                }
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_build_list, container, false);
        ReturnWatcher returnWatcher = new ReturnWatcher();
        mNewBlessing = v.findViewById(R.id.new_list_items);
        mNewBlessing.addTextChangedListener(returnWatcher);
        Button addButton = v.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddItem(v);
             }
        });
        // Set mExamplesShown and mExampleList from savedInstanceState
        if (savedInstanceState != null)
        {
            if (savedInstanceState.getInt("hintsShown") > 0) {
                mExamplesShown = true;
                mExampleList = savedInstanceState.getStringArray("hintList");
            } else {
                mExamplesShown = false;
            }
            mEventId = savedInstanceState.getLong("eventId", -1);
            mItemsAdded = savedInstanceState.getInt("itemsAdded", 0);
        }
        else {
            mExamplesShown = false;
            mEventId = -1;
            mItemsAdded = 0;
        }
        Button exampleButton = v.findViewById(R.id.example_button);
        ListView exampleText = v.findViewById(R.id.example_items);
        mExampleLayout = v.findViewById(R.id.examples_layout);
        if (mExamplesShown) {
            mExampleLayout.setVisibility(View.VISIBLE);
        }
        exampleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mExamplesShown)
                    StartExamples();
            }
        });
        AdapterView.OnItemClickListener exampleSelector = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String item = (String)((TextView) view).getText();
                mNewBlessing.setText(item);
            }
        };
        exampleText.setOnItemClickListener(exampleSelector);
        mItemCount = v.findViewById(R.id.list_count);
        mNewItemCount = v.findViewById(R.id.new_item_count);
        updateCounts(v);
        v.getContext().sendBroadcast(new Intent(
                "com.roachcitysoftware.goldenkey.action.REMINDER").putExtra(
                "Target", ReminderService.BUILD_LIST).putExtra("Action", ReminderService.CANCEL));
        return v;
    }

    public void StartExamples()
    {
        mExamplesShown = true;
        // Set up hint list
        Resources res = getResources();
        mExampleList = res.getStringArray(R.array.example_list);
        Date dt = new Date();
        long seed = dt.getTime();
        RandomizeList(mExampleList, seed);
        mExampleLayout.setVisibility(View.VISIBLE);
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
        if (mExamplesShown) {
            outState.putInt("hintsShown", 1);
            outState.putStringArray("hintList", mExampleList);
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

    private void updateCounts (View v) {
        int ListSize = getListSize(v);
        String itemCount = Integer.toString(ListSize);
        int stringSize = itemCount.length();
        if (stringSize == 1)
            itemCount = "000" + itemCount;
        else if (stringSize == 2)
            itemCount = "00" + itemCount;
        else if (stringSize == 3)
            itemCount = "0" + itemCount;
       mItemCount.setText(itemCount);
       if (ListSize == 0)
           mItemCount.setBackgroundColor(Color.RED);
       else if (ListSize < 50)
           mItemCount.setBackgroundColor(Color.YELLOW);
       else
           mItemCount.setBackgroundColor(Color.GREEN);

        String itemsAdded = Integer.toString(mItemsAdded);
        stringSize = itemsAdded.length();
        if (stringSize == 1)
            itemsAdded = "00" + itemsAdded;
        else if (stringSize == 2)
            itemsAdded = "0" + itemsAdded;
        mNewItemCount.setText(itemsAdded);
        if (mItemsAdded == 0)
            mNewItemCount.setBackgroundColor(Color.RED);
        else if (mItemsAdded < 10)
            mNewItemCount.setBackgroundColor(Color.YELLOW);
        else
            mNewItemCount.setBackgroundColor(Color.GREEN);
    }

    private int getListSize (View v) {
        ContentProviderClient cpc =
                v.getContext().getContentResolver().acquireContentProviderClient(GrandContract.CONTENT_URI_1);
        if (cpc == null) {
            return 0;
        }
        BlessingProvider bp = (BlessingProvider) cpc.getLocalContentProvider();
        if (bp == null) {
            cpc.release();
            return 0;
        }
        Cursor blessingCursor = bp.query(GrandContract.CONTENT_URI_1, null, null, null, null);
        if ((blessingCursor == null) || (!blessingCursor.moveToFirst())) {
            cpc.release();
            return 0;
        }
        int ListSize = blessingCursor.getCount();
        blessingCursor.close();
        cpc.release();
        return ListSize;
    }

    private void AddItem (View v) {
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
        updateCounts(v);
    }
}
