package com.example.nazmuddinmavliwala.emvp;

import com.example.efragmentannotation.BindFragment;
import com.example.efragmentannotation.BindPresenter;
import com.example.efragmentannotation.BindViewDelegate;
import com.example.nazmuddinmavliwala.emvp.base.BaseEFragment;

/**
 * Created by nazmuddinmavliwala on 17/11/2017.
 */

@BindFragment(R.layout.fragment_sample)
public class SampleFragment extends BaseEFragment implements SampleView, SampleDelegateInteractor {


    @BindViewDelegate
    SampleViewDelegate viewDelegate;

    @BindPresenter
    SamplePresenter presenter;

}
