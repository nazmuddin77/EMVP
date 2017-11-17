package com.example.efragment;

import android.support.annotation.NonNull;
import android.support.annotation.UiThread;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nazmuddinmavliwala on 24/11/16.
 */
public interface EFragmentBinder {

    @UiThread
    View bind(
            @NonNull Object fragment
            , @NonNull LayoutInflater inflater
            , @NonNull ViewGroup container);
}
