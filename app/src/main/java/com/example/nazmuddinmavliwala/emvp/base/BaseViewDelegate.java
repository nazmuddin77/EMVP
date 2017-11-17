package com.example.nazmuddinmavliwala.emvp.base;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.View;

/**
 * Created by nazmuddinmavliwala on 17/11/2017.
 */

public abstract class BaseViewDelegate implements ViewDelegate {


    private final View itemView;
    @NonNull
    private final Context context;
    private BaseDelegateInteractor interactor;

    public BaseViewDelegate(@NonNull View itemView,
                            @NonNull Context context,
                            @NonNull BaseDelegateInteractor interactor) {
        this.itemView = itemView;
        this.context = context;
        this.interactor = interactor;
    }
}
