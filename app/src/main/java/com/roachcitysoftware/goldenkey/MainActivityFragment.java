package com.roachcitysoftware.goldenkey;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private static final String TAG = MainActivityFragment.class.getSimpleName();

//    private Button mInstructionsButton;
    private Button mListButton;
    private Button mPracticeButton;

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);
        Button mInstructionsButton = (Button) v.findViewById(R.id.instructions_button);
        mInstructionsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("com.roachcitysoftware.goldenkey.action.instructions"));
            }
        });
        Log.d(TAG, "onCreateView");
        return v;
    }
}
