package com.roachcitysoftware.goldenkey;


import android.app.Activity;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;
import java.util.Random;


/**
 * A simple {@link Fragment} subclass.
 */
public class BuildListFeedbackActivityFragment extends Fragment {

    private static final String TAG = PracticeFeedbackActivityFragment.class.getSimpleName();
    private int mBuildListRun;
    private int mMaxListRun;
    private int mListSizeRun;
    private String mPraise;
    private String [] mPraiseList;
    private String mBuildListFeedback;
    private String mMaxListFeedback;
    private String mListSizeFeedback;
    private TextView mPraiseText;
    private TextView mBuildListText;
    private TextView mMaxListText;
    private TextView mListSizeText;
    private Button mCloseButton;

    public BuildListFeedbackActivityFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView starting");
        View v = inflater.inflate(R.layout.fragment_build_list_feedback_activity, container, false);
        InitializeData(v);
        if (mBuildListRun < 1){
            // Don't display feedback for null practice
            Activity a = getActivity();
            a.finish();
        }
        BuildStrings();
        mPraiseText = (TextView)v.findViewById(R.id.praise);
        mPraiseText.setText(mPraise);
        mBuildListText = (TextView)v.findViewById(R.id.build_list_feedback);
        mBuildListText.setText(mBuildListFeedback);
        mMaxListText = (TextView)v.findViewById(R.id.max_list_feedback);
        mMaxListText.setText(mMaxListFeedback);
        mListSizeText = (TextView)v.findViewById(R.id.list_size_feedback);
        mListSizeText.setText(mListSizeFeedback);
        mCloseButton = (Button) v.findViewById(R.id.close_button);
        mCloseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Activity a = getActivity();
                a.finish();
            }
        });
        Log.d(TAG, "onCreateView done");
        return v;
    }

    private void InitializeData(View v){
        Log.d(TAG, "InitializeData starting");
        mBuildListRun = 0;
        mMaxListRun = 0;
        mListSizeRun = 0;
    }

    private void BuildStrings(){
        Log.d(TAG, "BuildStrings starting");
        Resources res = getResources();
        mPraiseList = res.getStringArray(R.array.praise_list);
        Date dt = new Date();
        long seed = dt.getTime();
        int size = mPraiseList.length;
        Random rn = new Random(seed);
        int selection = rn.nextInt(size);
        mPraise = mPraiseList[selection];
        if (mBuildListRun == 1)
            mBuildListFeedback = getString(R.string.build_list_feedback_base) +
                    Integer.toString(mBuildListRun) + getString(R.string.build_list_feedback_single);
        else
            mBuildListFeedback = getString(R.string.build_list_feedback_base) +
                    Integer.toString(mBuildListRun) + getString(R.string.build_list_feedback_plural);
        mListSizeFeedback = getString(R.string.list_size_feedback_base) +
                Integer.toString(mListSizeRun)  + getString(R.string.list_size_feedback_end);
        Log.d(TAG, "BuildStrings done");
    }
    }
