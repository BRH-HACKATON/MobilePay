package com.brh.pronapmobile.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.brh.pronapmobile.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestPaymentFragment extends Fragment {

    public static final String TAG = "RequestPaymentFragment";

    public RequestPaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_request_payment, container, false);
    }

}
