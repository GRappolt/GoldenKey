package com.roachcitysoftware.goldenkey;


import android.content.ContentProviderClient;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


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
        Log.d(TAG, "onCreateView");
        return v;
    }

    private void LoadBlessingList (View v)
    {
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

        // Clean up
        cpc.release();
    }
}
