package com.roachcitysoftware.goldenkey;

import android.app.Application;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.text.Html;
import android.widget.TextView;
import android.content.ContextWrapper;
import android.widget.Toast;

import java.io.File;
import java.io.FileReader;

/**
 * A placeholder fragment containing a simple view.
 */
public class InstructionsActivityFragment extends Fragment {

    private final String instructionsText = "<body><H1>Background</H1>" +
            "<P>This world can be both heaven and hell.  It's hell when your mind" +
            " is filled with bad things - problems, failures, loss and pain.  It's" +
            " heaven when your mind is filled with good things.</P>" +
            "<P>You can train your mind to focus more on the good things of the world." +
            "  It's a matter of practicing thinking about good things, and feeling" +
            " happy about them.  As with any kind of training, steady practice is key.</P>" +
            "<P> This app helps you train.  You start by building a list of good things," +
            " and then use that list to practice thinking about good things.</P>" +
            "<P>After a week of regular, daily practice, you will find yourself starting" +
            " to sometimes feel really good for no particular reason.  Keep on training," +
            " and this will happen more and more often, and more strongly.  After a" +
            " month of steady practice, you will feel happy most of the time.</P>" +
            "<P>Steady practice is the golden key to paradise.</P>" +
            "<H1>Build List</H1>" +
            "<P>Before you can practice, you need a list of good things.  Use the <B>Build List</B>" +
            " screen to create that list.  Enter things that make you happy into the text" +
            "box, and press the <B>Add</B> button after each one.</P>" +
            "<P>For example, let's say you like hotdogs.  Enter \"hotdogs\" in the text box –" +
            "all you need is the one word.  Notice that the example is hotdogs - probably not" +
            " the greatest thing you've ever eaten, but good.  You need a lot of things that make" +
            " you happy for your list – but they don't all have to be wonderful.  Just good is" +
            " good enough.</P>" +
            "<P>You will need lots of things that make you happy in your list - and lots of" +
            " different kinds of things.  Food, pets, friends, things you are proud of, natural" +
            " beauty, special moments in your life, music, movies, books, sex, big and little" +
            " victories, insights, escapes from danger and relief from pain - anything and" +
            " everything you can feel good about.  Humor (favorite jokes, funny happenings) is" +
            " especially good. </P>" +
            "<P>Once you've made a good start on your list, you can begin training.  However," +
            " that shouldn't stop you from adding to your list.  Keep growing the list as long as" +
            " you keep practicing.</P>" +
            "</body>";

    public InstructionsActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_instructions, container, false);
        TextView instructionsView = (TextView) v.findViewById(R.id.textView3);
        StringBuffer data = new StringBuffer();
        boolean gotIt = ReadInstructionsFile (data);
        if (gotIt) {
            instructionsView.setText(Html.fromHtml(data.toString(), null, null));
        }
        else {
            instructionsView.setText(Html.fromHtml(instructionsText, null, null));
        }
        return v;
    }

    private boolean ReadInstructionsFile (StringBuffer data) {
        android.app.Activity window = this.getActivity();
        Application app = window.getApplication();
        File dataFile = app.getFileStreamPath("instructions.txt");
        String error;
        try {
            FileReader dataReader = new FileReader(dataFile);
            int chunk = 100;
            char[] stuff = new char [chunk];
            int bytesRead = dataReader.read(stuff, 0, chunk);
            while (bytesRead > 0){
                data.append(stuff);
            }
            return true;
        }
        catch (java.io.FileNotFoundException fex) {
           error = fex.getMessage();
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
        catch (java.io.IOException iox){
            error = iox.getMessage();
            Toast.makeText(getActivity(), error, Toast.LENGTH_LONG).show();
        }
        return false;
    }
}
