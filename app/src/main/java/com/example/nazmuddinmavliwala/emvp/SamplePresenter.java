package com.example.nazmuddinmavliwala.emvp;

import android.content.Context;
import android.support.annotation.NonNull;

import com.example.nazmuddinmavliwala.emvp.base.BasePresenter;
import com.example.nazmuddinmavliwala.emvp.base.BaseViewListener;

/**
 * Created by nazmuddinmavliwala on 17/11/2017.
 */

public class SamplePresenter extends BasePresenter {

    public SamplePresenter(@NonNull Context context,
                           @NonNull BaseViewListener viewListener) {
        super(context, viewListener);
    }
}
