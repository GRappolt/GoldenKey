package com.roachcitysoftware.goldenkey;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

//    private static final String TAG = MainActivityFragment.class.getSimpleName();


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
        Button mListButton = v.findViewById(R.id.list_button);
        mListButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("com.roachcitysoftware.goldenkey.action.build_list"));
            }
        });
        Button mPracticeButton = v.findViewById(R.id.practice_button);
        mPracticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent("com.roachcitysoftware.goldenkey.action.practice"));
            }
        });
        return v;
    }
}
