package com.brh.pronapmobile.fragments;


import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatButton;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.activities.MainActivity;
import com.brh.pronapmobile.utils.BitmapEncoder;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.util.HashMap;

/**
 * A simple {@link Fragment} subclass.
 */
public class RequestPaymentFragment extends Fragment {

    public static final String TAG = "RequestPaymentFragment";

    public final static int SIZE = 1000;

    private View rootView;
    private ImageView qrCodeImageView;
    private AppCompatButton generateButton;

    private HashMap<String, String> paymentData;

    public RequestPaymentFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        rootView = inflater.inflate(R.layout.fragment_request_payment, container, false);

        qrCodeImageView = rootView.findViewById(R.id.qrCode);
        generateButton = rootView.findViewById(R.id.generate);

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQRCOde();
            }
        });

        ImageView closeImageView = rootView.findViewById(R.id.ivClose);
        closeImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Close Fragment clicked");
                //getActivity().onBackPressed();
                //getActivity().getFragmentManager().popBackStack();
                ((MainActivity)getActivity()).displayFloatingButtons();
                getActivity().getSupportFragmentManager().beginTransaction()
                        .remove(RequestPaymentFragment.this).commit();
            }
        });

        return rootView;
    }

    public void generateQRCOde() {
        // Fill HasMap with input informations
        EditText etProduct = rootView.findViewById(R.id.product);
        EditText etPrice = rootView.findViewById(R.id.price);

        paymentData = new HashMap<>();
        paymentData.put("product", etProduct.getText().toString());
        paymentData.put("price", etPrice.getText().toString());

        // Add vendor static informations to HashMap
        // TODO : get info laster from Vendor Model attached to User Model
        paymentData.put("vendor", "G&G Sportif");
        paymentData.put("app_code", "PD$BVQRC");

        // Convert HashMap to JSON with Gson
        Gson gson = new Gson();
        String json = gson.toJson(paymentData);
        Log.d(TAG, json);

        // Convert JSON string back to Map.
        /*Type type = new TypeToken<Map<String, String>>(){}.getType();
        Map<String, String> map = gson.fromJson(json, type);
        for (String key : map.keySet()) {
            System.out.println("map.get = " + map.get(key));
        }*/

        try {
            Bitmap bitmap = BitmapEncoder.encodeAsBitmap(json, SIZE);
            qrCodeImageView.setImageBitmap(bitmap);

            // hide layout Payment Info
            LinearLayout llPayment = rootView.findViewById(R.id.llPaymentInfo);
            llPayment.setVisibility(View.GONE);

            // unhide layout QRCode
            LinearLayout llCode = rootView.findViewById(R.id.llPaymentQRCode);
            llCode.setVisibility(View.VISIBLE);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }


}
