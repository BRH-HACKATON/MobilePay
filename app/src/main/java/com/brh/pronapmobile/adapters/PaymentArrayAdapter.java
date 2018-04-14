package com.brh.pronapmobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.models.Payment;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Keitel on 4/13/18.
 */

public class PaymentArrayAdapter extends ArrayAdapter<Payment> {

    //constructor
    public PaymentArrayAdapter(Context context, ArrayList<Payment> list) {
        super(context, android.R.layout.simple_list_item_1, list);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView,
                                @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    //custom view to populate data
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Payment payment = getItem(position);
        if(payment.getId() > 0) {
            Log.d("PaymentArrayAdapter", "Payment : " + payment.getId());
        }

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_payment, parent, false);
        }

        TextView tvVendor = convertView.findViewById(R.id.tvVendor);
        TextView tvDate = convertView.findViewById(R.id.tvDate);
        TextView tvAmount = convertView.findViewById(R.id.tvAmount);
        TextView tvHolder = convertView.findViewById(R.id.tvHolder);

        if(payment.getAmount() != null) {
            tvAmount.setText(String.valueOf(payment.getAmount()) + " gdes");
        }

        if(payment.getVendorName() != null) {
            tvVendor.setText(payment.getVendorName());
        }

        if(payment.getDate() != null) {
            tvDate.setText(new SimpleDateFormat("yyyy-MM-dd").format(payment.getDate()));
        }

        if(payment.getCardHolder() != null) {
            tvHolder.setText(payment.getCardHolder());
        }

        return convertView;
    }

}

