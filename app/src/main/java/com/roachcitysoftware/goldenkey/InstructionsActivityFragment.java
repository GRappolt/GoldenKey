package com.roachcitysoftware.goldenkey;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.Html;
import android.widget.TextView;

/**
 * A placeholder fragment containing a simple view.
 */
public class InstructionsActivityFragment extends Fragment {

    private final String instructionsText = "<body><H1>Background</H1>" +
            "<P>This world can be both heaven and hell.</P></body>";

    public InstructionsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_instructions, container, false);
        TextView instructionsView = (TextView) v.findViewById(R.id.textView3);
        instructionsView.setText(Html.fromHtml(instructionsText, null, null));
        return v;
    }
}
