package com.brh.pronapmobile.fragments;


import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.activities.VendorActivity;
import com.brh.pronapmobile.models.Vendor;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreateVendorFragment extends Fragment {

    private static final String TAG = "CreateVendorFragment";

    View rootView;
    AppCompatButton validateButton;
    TextInputLayout tilName;
    TextInputLayout tilCode;
    TextInputLayout tilAccount;
    TextInputLayout tilRouting;
    TextInputLayout tilPhone;

    public CreateVendorFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        rootView = inflater.inflate(R.layout.fragment_create_vendor, container, false);

        validateButton = rootView.findViewById(R.id.validate);
        tilName = rootView.findViewById(R.id.vendor_name);
        tilCode = rootView.findViewById(R.id.brh_code);
        tilAccount = rootView.findViewById(R.id.vendor_account);
        tilRouting = rootView.findViewById(R.id.routing_number);
        tilPhone = rootView.findViewById(R.id.phone);

        validateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createProfile();
            }
        });

        return rootView;
    }

    public void createProfile() {
        validateButton.setEnabled(false);

        // TODO : Validate Data
        if(!validate()) {
            Toast.makeText(getContext(), "Erreur dans vos données, vérifiez et essayez encore!",
                    Toast.LENGTH_LONG).show();
            return;
        }

        try {
            Vendor vendor = new Vendor();
            vendor.setName(tilName.getEditText().getText().toString());
            vendor.setCode(tilCode.getEditText().getText().toString());
            vendor.setAccount(tilAccount.getEditText().getText().toString());
            vendor.setRouting(tilRouting.getEditText().getText().toString());
            vendor.setPhone(tilPhone.getEditText().getText().toString());

            vendor.save();
            Log.d(TAG, "Vendor Profile added : " + vendor.getId());

            Toast.makeText(getContext(), "Compte Vendeur ajoutée!", Toast.LENGTH_LONG).show();

            // close fragment
            ((VendorActivity) getActivity()).listVendors();
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
