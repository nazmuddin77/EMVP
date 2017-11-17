package com.example.efragment;

import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by nazmuddinmavliwala on 23/11/16.
 */
public class EFragment {

    public static final String AUTO_MAPPING_CLASS_NAME = "AutoEFragmentBinder";
    public static final String AUTO_MAPPING_PACKAGE = "com.aasaanjobs.employee";
    public static final String AUTO_MAPPING_QUALIFIED_CLASS =
            AUTO_MAPPING_PACKAGE + "." + AUTO_MAPPING_CLASS_NAME;

    private static EFragmentBinder autoMappingBinder;

    @SuppressWarnings("unchecked")
    public static <T extends Fragment> View bind (
            @NonNull T target
            ,@NonNull LayoutInflater inflater
            ,@NonNull ViewGroup container) {

        if(autoMappingBinder == null) {
            try {
                Class<?> c = Class.forName(AUTO_MAPPING_QUALIFIED_CLASS);
                autoMappingBinder = (EFragmentBinder) c.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if(autoMappingBinder != null) {
           return   autoMappingBinder.bind(
                        target
                        , inflater
                        , container);
        }
        throw new RuntimeException("Unable to instantiate autofragmentbinder ");
    }

}


