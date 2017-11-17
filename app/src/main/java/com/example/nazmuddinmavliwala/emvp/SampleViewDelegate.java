package com.example.nazmuddinmavliwala.emvp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

import com.example.nazmuddinmavliwala.emvp.base.BaseDelegateInteractor;
import com.example.nazmuddinmavliwala.emvp.base.BaseViewDelegate;

/**
 * Created by nazmuddinmavliwala on 17/11/2017.
 */

public class SampleViewDelegate extends BaseViewDelegate {

    public SampleViewDelegate(@NonNull View itemView,
                              @NonNull Context context,
                              @NonNull BaseDelegateInteractor interactor) {
        super(itemView, context, interactor);
    }
}
