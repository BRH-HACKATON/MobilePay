package com.brh.pronapmobile.fragments;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.activities.MakePaymentActivity;


public class PinDialogFragment extends DialogFragment {

    public static PinDialogFragment newInstance(String title) {
        PinDialogFragment frag = new PinDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String title = getArguments().getString("title");

        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle(title);

        LayoutInflater inflater = getActivity().getLayoutInflater();

        builder.setView(inflater.inflate(R.layout.fragment_pin_dialog, null));

        return builder
                .setMessage("PIN de la Carte de Débit")
                .setNegativeButton("Annuler", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        Toast.makeText(getContext(), "Paiement Annulé!", Toast.LENGTH_SHORT).show();
                    }
                })

                .setPositiveButton("Confirmer", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        //Toast.makeText(getContext(), "Paiement confirmé!", Toast.LENGTH_SHORT).show();
                        ((MakePaymentActivity)getActivity()).sendSMS();
                    }
                })
                .create();
    }
}