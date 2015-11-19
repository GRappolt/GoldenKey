package com.roachcitysoftware.goldenkey;

import android.content.ContentValues;
import android.content.ContentProviderClient;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

/**
 * A placeholder fragment containing a simple view.
 */
public class BuildListActivityFragment extends Fragment {

    private static final String TAG = BuildListActivityFragment.class.getSimpleName();
    private EditText mNewBlessing;

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
                Log.d(TAG, "Add button Clicked");
                ContentProviderClient cpc =
                        v.getContext().getContentResolver().acquireContentProviderClient(GrandContract.CONTENT_URI_1);
                if (cpc == null) {
                    Log.d(TAG, "Add failed - can't get Content Resolver");
                    return;
                }
                BlessingProvider bp = (BlessingProvider) cpc.getLocalContentProvider();
                if (bp == null) {
                    Log.d(TAG, "Add failed - can't get BlessingProvider");
                    return;
                }
                boolean added = bp.onAdd(mNewBlessing.getText().toString());
                cpc.release();
                if (added){
                    mNewBlessing.setText("");
                }
            }
        });
        Log.d(TAG, "onCreateView");
        return v;
    }
}
