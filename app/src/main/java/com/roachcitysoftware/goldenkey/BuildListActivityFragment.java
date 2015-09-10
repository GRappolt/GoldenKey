package com.roachcitysoftware.goldenkey;

import android.content.ContentValues;
import android.net.Uri;
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
                ContentValues values = new ContentValues();
                values.clear();
                values.put(GrandContract.BlessingsColumn.ID, "");
                values.put(GrandContract.BlessingsColumn.BLESSING, mNewBlessing.getText().toString());
                Uri uri = v.getContext().getContentResolver().insert(GrandContract.CONTENT_URI_1, values);
            }
        });
        Log.d(TAG, "onCreateView");
        return v;
    }
}
