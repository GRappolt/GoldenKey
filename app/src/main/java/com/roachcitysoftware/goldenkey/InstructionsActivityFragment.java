package com.roachcitysoftware.goldenkey;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.Html;
import android.widget.TextView;

/**
 * This fragment displays the instructions for Golden Key.
 */
public class InstructionsActivityFragment extends Fragment {

    private static final String TAG = InstructionsActivityFragment.class.getSimpleName();

    public InstructionsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_instructions, container, false);
        TextView instructionsView = (TextView) v.findViewById(R.id.textView3);
        StringBuffer data = new StringBuffer();
        BuildInstructionsBuffer(data);
        instructionsView.setText(Html.fromHtml(data.toString(), null, null));
        Log.d(TAG, "onCreateView");
        return v;
    }

    private void BuildInstructionsBuffer (StringBuffer data) {
        data.append("<body>");
        AddHeader(data, R.string.section_1_header);
        AddParagraph(data, R.array.section_1_para_1);
        AddParagraph(data, R.array.section_1_para_2);
        AddParagraph(data, R.array.section_1_para_3);
        AddParagraph(data, R.array.section_1_para_4);
        AddParagraph(data, R.array.section_1_para_5);
        AddHeader(data, R.string.section_2_header);
        AddParagraph(data, R.array.section_2_para_1);
        AddParagraph(data, R.array.section_2_para_2);
        AddParagraph(data, R.array.section_2_para_3);
        AddParagraph(data, R.array.section_2_para_4);
        data.append("</body>");
    }

    private void AddHeader (StringBuffer data, int headerId) {
        data.append("<H2>");
        String header = getString(headerId);
        data.append(header);
        data.append("</H2>");
    }

    private void AddParagraph (StringBuffer data, int paraId) {
        data.append("<P>");
        Resources res = getResources();
        String [] paraText = res.getStringArray(paraId);
        for (String item : paraText) {
            data.append(item);
            data.append(" ");
        }
        data.append("</P>");
    }
}
