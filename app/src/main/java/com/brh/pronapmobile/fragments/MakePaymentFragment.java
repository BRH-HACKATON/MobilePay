package com.brh.pronapmobile.fragments;


import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.activities.MainActivity;
import com.brh.pronapmobile.utils.BitmapEncoder;
import com.brh.pronapmobile.utils.SMSUtils;
import com.google.zxing.WriterException;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class MakePaymentFragment extends Fragment {

    public static final String TAG = "MakePaymentFragment";

    private View rootView;
    private JSONObject paymentData;

    private ImageView ivQRCode;
    private TextView tvVendor;
    private TextView tvPrice;
    private TextView tvProduct;
    private AppCompatButton payButton;
    private AppCompatButton cancelButton;


    public MakePaymentFragment() {
        // Required empty public constructor
    }

    public static MakePaymentFragment newInstance(String qrCodeString) {
        Bundle args = new Bundle();
        args.putString("qrCodeString", qrCodeString);
        MakePaymentFragment fragment = new MakePaymentFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_make_payment, container, false);

        ivQRCode = rootView.findViewById(R.id.ivQRCode);
        tvVendor = rootView.findViewById(R.id.tvVendor);
        tvPrice = rootView.findViewById(R.id.tvPrice);
        tvProduct = rootView.findViewById(R.id.tvProduct);
        payButton = rootView.findViewById(R.id.pay);
        cancelButton = rootView.findViewById(R.id.cancel_payment);

        // Change String JSONObject
        String qrCodeString = getArguments().getString("qrCodeString");
        try {
            paymentData = new JSONObject(qrCodeString);
            // fill the TextViews with the JSONObject
            loadPaymentViews(qrCodeString);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return rootView;
    }

    public void loadPaymentViews(String qrCodeString) {
        Log.d(TAG, "QR CODE JSONObject : " + paymentData.toString());
        try {
            tvVendor.setText(paymentData.getString("vendor"));
            tvPrice.setText(paymentData.getString("price"));
            tvProduct.setText(paymentData.getString("product"));
        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(getContext(), "Les informations de ce QR Code ne sont pas compatibles avec notre système de paiement",
                    Toast.LENGTH_LONG).show();
        }

        // Create QR Code image again for vendor
        try {
            Bitmap bitmap = BitmapEncoder.encodeAsBitmap(qrCodeString, 1000);
            ivQRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        // set click listener on buttons
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Send SMS to Phone Number
                sendSMS();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).displayFloatingButtons();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(MakePaymentFragment.this).commit();
            }
        });

    }

    public void sendSMS() {
        try {
            // TODO : replace the values later with attributes from Vendor Model
            paymentData.put("code", "S23F-S6DH-23KS-W3ZS");
            paymentData.put("account_number", "123-456-90");
            paymentData.put("routing", "012345678");
            paymentData.put("phone", "+50937567873");

            // Send SMS to BRH phone number
            // TODO : Simulate BRH phone with Dev Phone
            SMSUtils.sendSMS(getContext(), "+50937567873", paymentData.toString());

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getContext(), "Erreur survenue en envoyant le SMS!",
                    Toast.LENGTH_LONG).show();
        }
    }

}
