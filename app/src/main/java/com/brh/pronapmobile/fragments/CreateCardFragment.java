package com.brh.pronapmobile.fragments;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.brh.pronapmobile.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateCardFragment extends Fragment {

    AppCompatButton validateButton;

    public CreateCardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_create_card, container, false);

        validateButton = rootView.findViewById(R.id.validate);

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createCard();
            }
        });

        return rootView;
    }

    public void createCard() {
        validateButton.setEnabled(false);

        // TODO : Validate Data
        if(!validate()) {
            Toast.makeText(getContext(), "Problem with data, enter the data properly and try again!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        // BRH will validate information and send User a unique Vendor Code
        // TODO : add that Code to the Vendor Model and attach the Vendor Model to the User Model

        Toast.makeText(getContext(), "Card created!",
                Toast.LENGTH_LONG).show();
    }

    public boolean validate() {
        // TODO : implement validation to return true or false
        return true;
    }

}
