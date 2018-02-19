package com.roachcitysoftware.goldenkey;

import android.content.ContentProviderClient;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.os.Bundle;
// import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;



/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

//    private static final String TAG = MainActivityFragment.class.getSimpleName();
    private Button mListButton;
    private Button mPracticeButton;
    static boolean sOldList = false;


    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        Button mDetailsButton = v.findViewById(R.id.details_button);
        mDetailsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("com.roachcitysoftware.goldenkey.action.details"));
            }
        });
        mListButton = v.findViewById(R.id.list_button);
        mListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("com.roachcitysoftware.goldenkey.action.build_list"));
            }
        });
        mPracticeButton = v.findViewById(R.id.practice_button);
        mPracticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("com.roachcitysoftware.goldenkey.action.practice"));
            }
        });

        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        if (NeedBuildList()){
            mListButton.setBackgroundColor(Color.YELLOW);
        } else {
            mPracticeButton.setBackgroundColor(Color.YELLOW);
        }
    }


    static void SetOldList (boolean tooOld) {
        sOldList = tooOld;
    }


    private boolean NeedBuildList () {
        return (ListTooShort(getView()) || ListTooOld());
    }

    private boolean ListTooShort (View v) {
        int blessingCount;
        int mMinListSize = 50;
        ContentProviderClient cpc =
                v.getContext().getContentResolver().acquireContentProviderClient(GrandContract.CONTENT_URI_1);
        if (cpc == null) {
            return false;
        }
        BlessingProvider bp = (BlessingProvider) cpc.getLocalContentProvider();
        if (bp == null) {
            cpc.release();
            return false;
        }
        // Do the real work here
        Cursor cursor = bp.query(GrandContract.CONTENT_URI_1, null, null, null, null);
        if ((cursor == null) || (!cursor.moveToFirst())) {
            cpc.release();
            return false;
        }
        blessingCount = cursor.getCount();
        cursor.close();
        cpc.release();
        return (blessingCount < mMinListSize);
    }

    private boolean ListTooOld () {
        return sOldList;
    }
}
