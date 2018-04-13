package com.brh.pronapmobile.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.models.Card;

import java.util.ArrayList;

/**
 * Created by Keitel on 4/12/18.
 */

public class CardArrayAdapter extends ArrayAdapter<Card> {

    //constructor
    public CardArrayAdapter(Context context, ArrayList<Card> list) {
        super(context, android.R.layout.simple_list_item_1, list);
    }

    //custom view to populate data
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final Card card = getItem(position);
        if(card.getId() > 0) {
            Log.d("CardArrayAdapter", "Card : " + card.getId());
        }

        if(convertView == null){
            LayoutInflater inflater = LayoutInflater.from(getContext());
            convertView = inflater.inflate(R.layout.item_card, parent, false);
        }

        TextView tvNumber = convertView.findViewById(R.id.tvNumber);
        TextView tvExpiry = convertView.findViewById(R.id.tvExpiry);

        if(card.getNumber() != null){
            // Process Text of Card
            String hiddenText = "XXXX XXXX XXX ";
            // Add only the 4 last Digits
            hiddenText += card.getNumber().substring(Math.max(card.getNumber().length() - 4, 0), card.getNumber().length());
            tvNumber.setText(hiddenText);
        }

        if(card.getExpiry() != null){
            tvExpiry.setText(card.getExpiry());
        }

        return convertView;
    }

}
