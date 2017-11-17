package com.example.nazmuddinmavliwala.emvp.base;

import android.content.Context;
import android.support.annotation.NonNull;

/**
 * Created by nazmuddinmavliwala on 17/11/2017.
 */

public abstract class BasePresenter implements Presenter {

    @NonNull
    private final Context context;
    @NonNull
    private final BaseViewListener viewListener;

    public BasePresenter(@NonNull Context context,
                         @NonNull BaseViewListener viewListener) {
        this.context = context;
        this.viewListener = viewListener;
    }
}
