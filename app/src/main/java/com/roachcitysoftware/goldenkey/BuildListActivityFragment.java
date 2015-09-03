package com.roachcitysoftware.goldenkey;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * A placeholder fragment containing a simple view.
 */
public class BuildListActivityFragment extends Fragment {

    private static final String TAG = BuildListActivityFragment.class.getSimpleName();

    public BuildListActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_build_list, container, false);
        Log.d(TAG, "onCreateView");
        return v;
    }
}
