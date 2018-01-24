package com.roachcitysoftware.goldenkey;

import android.content.res.Resources;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.Html;
import android.widget.TextView;

/**
 * This fragment displays the instructions for Golden Key.
 */
public class DetailsActivityFragment extends Fragment {

//    private static final String TAG = DetailsActivityFragment.class.getSimpleName();

    public DetailsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_details, container, false);
        TextView detailsView = v.findViewById(R.id.textView3);
        StringBuffer data = new StringBuffer();
        BuildDetailsBuffer(data);
        detailsView.setText(Html.fromHtml(data.toString(), null, null));
        return v;
    }

    private void BuildDetailsBuffer(StringBuffer data) {
        data.append("<body>");
        AddHeader(data, R.string.section_1_header);
        AddParagraph(data, R.array.section_1_para_1);
        AddHeader(data, R.string.section_2_header);
        AddParagraph(data, R.array.section_2_para_1);
        AddParagraph(data, R.array.section_2_para_2);
        AddParagraph(data, R.array.section_2_para_3);
        AddParagraph(data, R.array.section_2_para_4);
        AddParagraph(data, R.array.section_2_para_5);
        AddParagraph(data, R.array.section_2_para_6);
        AddHeader(data, R.string.section_3_header);
        AddParagraph(data, R.array.section_3_para_1);
        AddParagraph(data, R.array.section_3_para_2);
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
