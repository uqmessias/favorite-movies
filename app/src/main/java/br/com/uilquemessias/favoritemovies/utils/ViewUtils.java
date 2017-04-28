package br.com.uilquemessias.favoritemovies.utils;

import android.view.View;

public class ViewUtils {
    private static ViewUtils sInstance;

    public static ViewUtils getInstance() {
        if (sInstance == null) {
            sInstance = new ViewUtils();
        }

        return sInstance;
    }

    private ViewUtils() {
        //
    }

    public ViewUtils visible(View... views) {
        if (views != null) {
            for (View view : views) {
                view.setVisibility(View.VISIBLE);
            }
        }

        return getInstance();
    }

    public ViewUtils gone(View... views) {
        if (views != null) {
            for (View view : views) {
                view.setVisibility(View.GONE);
            }
        }

        return getInstance();
    }

}
