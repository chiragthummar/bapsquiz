package com.bittechnologies.bapsquiz.util;

import android.app.Activity;
import android.graphics.Color;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.TextView;

/**
 * Created by chira on 2/2/2016.
 */
public class StaticData {


    public static void snackNormalMessage(Activity c, String msg) {
        Snackbar snackbar = Snackbar
                .make(c.findViewById(android.R.id.content), msg, Snackbar.LENGTH_SHORT);
        snackbar.setActionTextColor(Color.RED);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.DKGRAY);
        TextView textView = (TextView) snackbarView.findViewById(android.support.design.R.id.snackbar_text);
        textView.setTextColor(Color.YELLOW);
        snackbar.show();
    }
}
