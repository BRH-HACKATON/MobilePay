package com.brh.pronapmobile.fragments;


import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.activities.CardActivity;
import com.brh.pronapmobile.models.Card;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateCardFragment extends Fragment {

    private static final String TAG = "CreateCardFragment";

    View rootView;
    AppCompatButton validateButton;
    TextInputLayout tilNumber;
    TextInputLayout tilHolder;
    TextInputLayout tilExpiry;
    TextInputLayout tilCvv;

    public CreateCardFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_create_card, container, false);

        validateButton = rootView.findViewById(R.id.validate);
        tilNumber = rootView.findViewById(R.id.card_number);
        tilHolder = rootView.findViewById(R.id.holder_name);
        tilExpiry = rootView.findViewById(R.id.expiry_date);
        tilCvv = rootView.findViewById(R.id.cvv);

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
            Toast.makeText(getContext(), "Erreur dans vos données, vérifiez et essayez encore!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Card card = new Card();
            card.setNumber(tilNumber.getEditText().getText().toString());
            card.setHolder(tilHolder.getEditText().getText().toString());
            card.setExpiry(tilExpiry.getEditText().getText().toString());
            card.setCvv(tilCvv.getEditText().getText().toString());

            card.save();
            Log.d(TAG, "Card added : " + card.getId());

            Toast.makeText(getContext(), "Carte de Crédit ajoutée!", Toast.LENGTH_LONG).show();

            // close fragment
            ((CardActivity) getActivity()).listCards();
            getActivity().getSupportFragmentManager().beginTransaction().remove(this).commit();

        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(getContext(), "Une Erreur est survenue, essayez encore!", Toast.LENGTH_LONG).show();
        }
    }

    public boolean validate() {
        // TODO : implement validation to return true or false
        return true;
    }

}
