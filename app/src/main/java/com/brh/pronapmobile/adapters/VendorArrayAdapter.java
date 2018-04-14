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
import com.brh.pronapmobile.models.Vendor;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Keitel on 4/12/18.
 */

public class VendorArrayAdapter extends ArrayAdapter<Vendor> {

    //constructor
    public VendorArrayAdapter(Context context, ArrayList<Vendor> list) {
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

        final Vendor vendor = getItem(position);
        if(vendor.getId() > 0) {
            Log.d("VendorArrayAdapter", "Vendor : " + vendor.getId());
        }

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_vendor, parent, false);
        }

        TextView tvName = convertView.findViewById(R.id.tvName);
        TextView tvCode = convertView.findViewById(R.id.tvCode);
        TextView tvAccount = convertView.findViewById(R.id.tvAccount);
        TextView tvRouting = convertView.findViewById(R.id.tvRouting);
        TextView tvPhone = convertView.findViewById(R.id.tvPhone);

        if(vendor.getName() != null){
            tvName.setText(vendor.getName());
        }

        if(vendor.getCode() != null){
            tvCode.setText(addLastFourDigits(vendor.getCode(), "XXXXXX"));
        }

        if(vendor.getAccount() != null){
            tvAccount.setText(addLastFourDigits(vendor.getAccount(), "XXXXXX"));
        }

        if(vendor.getRouting() != null){
            tvRouting.setText(addLastFourDigits(vendor.getRouting(), "XXXXXX"));
        }

        if(vendor.getPhone() != null){
            tvPhone.setText(vendor.getPhone());
        }

        return convertView;
    }

    private String addLastFourDigits(String text, String maskedText) {
        maskedText += text.substring(Math.max(text.length() - 4, 0), text.length());

        return maskedText;
    }

}
