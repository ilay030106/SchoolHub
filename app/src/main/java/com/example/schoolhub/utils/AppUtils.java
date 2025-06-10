package com.example.schoolhub.utils;

import android.view.View;

import com.google.android.material.snackbar.Snackbar;

public class AppUtils {
    public static void showSnackbar(View view, String message) {
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT).show();
        }
    }

    public static void showSnackbar(View view, String message, String actionText, View.OnClickListener actionListener) {
        if (view != null) {
            Snackbar.make(view, message, Snackbar.LENGTH_SHORT)
                    .setAction(actionText, actionListener)
                    .show();
        }
    }
}
