package com.roachcitysoftware.goldenkey;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class BuildListFeedbackActivity extends AppCompatActivity {

    private static final String TAG = BuildListFeedbackActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_build_list_feedback);
/*        Intent startIntent = getIntent();
        int silentFlag = startIntent.getIntExtra("silent", 0);
        if (silentFlag == 1){
            startActivity(new Intent("com.roachcitysoftware.goldenkey.action.build_list"));
            Log.d(TAG, "startActivity");
//        } else {
//            setContentView(R.layout.activity_build_list_feedback);
//            Log.d(TAG, "onCreate");
        }
*/
        Log.d(TAG, "onCreate");
    }
}
