package com.example.nazmuddinmavliwala.emvp.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.efragment.EFragment;

/**
 * Created by nazmuddinmavliwala on 24/11/16.
 */
public class BaseEFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view =  EFragment.bind(this,inflater,container);
        return view;
    }
}
