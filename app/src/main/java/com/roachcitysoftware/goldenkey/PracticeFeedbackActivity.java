package com.roachcitysoftware.goldenkey;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class PracticeFeedbackActivity extends AppCompatActivity {

    private static final String TAG = PracticeFeedbackActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practice_feedback);
        Log.d(TAG, "onCreate");
    }
}
