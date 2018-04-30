package com.brh.pronapmobile.adapters;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.media.Image;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.models.Card;
import com.cooltechworks.creditcarddesign.CreditCardView;

import java.util.ArrayList;

/**
 * Created by Keitel on 4/12/18.
 */

public class CardArrayAdapter extends RecyclerView.Adapter<CardArrayAdapter.ViewHolder> {

    private static final String TAG = "CardArrayAdapter";
    private boolean IS_SELECTABLE = false;

    // Store a member variable for the cards
    private ArrayList<Card> mCards;
    private int selectedPos = RecyclerView.NO_POSITION;

    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position, CreditCardView creditCardView, ImageView ivCheck);
    }


    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public CreditCardView ccvCard;
        public ImageView ivCheck;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            ccvCard = itemView.findViewById(R.id.ccvCard);
            ivCheck = itemView.findViewById(R.id.ivCheck);
        }

        public boolean isSelected(int position) {
            return selectedPos == position;
        }

        public void bind(final int position, final OnItemClickListener listener) {
            // Get the data model based on position
            Card card = mCards.get(position);

            // Highlight the background
            //ccvCard.setBackgroundColor(selectedPos == position ? itemView.getContext().getResources().getColor(R.color.colorAccent) : Color.TRANSPARENT);
            if(IS_SELECTABLE) {
                // Scale Up if it's selected and Scale Down if it's not selected
                if(isSelected(position)) {
                    ccvCard.setScaleX(1f);
                    ccvCard.setScaleY(1f);
                    ivCheck.setVisibility(View.VISIBLE);
                } else {
                    ccvCard.setScaleX(0.8f);
                    ccvCard.setScaleY(0.8f);
                    ivCheck.setVisibility(View.GONE);
                }
            }

            if(card.getNumber() != null){
                ccvCard.setCardNumber(card.getMaskedNumber());
            }

            if(card.getHolder() != null){
                ccvCard.setCardHolderName(card.getHolder());
            }

            if(card.getExpiry() != null){
                ccvCard.setCardExpiry(card.getExpiry());
            }

            if(card.getCvv() != null){
                ccvCard.setCVV("XXX");
            }

            //itemView was stored in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        listener.onItemClick(position, ccvCard, ivCheck);
                    }
                }
            });
        }


        public void onClick(View v) {
            // Select a Item only if Adapter is selectable
            if(IS_SELECTABLE) {
                Log.d(TAG, "Adapter Position : " + String.valueOf(getAdapterPosition()));

                // Below line is just like a safety check, because sometimes holder could be null,
                // in that case, getAdapterPosition() will return RecyclerView.NO_POSITION
                if (getAdapterPosition() == RecyclerView.NO_POSITION) return;

                // Updating old as well as new positions
                notifyItemChanged(selectedPos);
                selectedPos = getAdapterPosition();
                notifyItemChanged(selectedPos);

                //Log.d(TAG, String.valueOf(selectedPos) + " - card : " + mCards.get(selectedPos).getMaskedNumber());
            }

            // Do your another stuff for your onClick
        }
    }

    // Pass in the context, the card array into the constructor
    public CardArrayAdapter(ArrayList<Card> cards) {
        mCards = cards;
    }

    // Pass in the context, the card array into the constructor and specify if item can be selected
    public CardArrayAdapter(ArrayList<Card> cards, boolean isSelectable, OnItemClickListener listener) {
        mCards = cards;
        IS_SELECTABLE = isSelectable;
        this.listener = listener;
    }


    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public CardArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View cardView = inflater.inflate(R.layout.item_card, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(cardView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(CardArrayAdapter.ViewHolder viewHolder, int position) {
        viewHolder.bind(position, listener);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mCards.size();
    }

    public Card getItem(int position) {
        return mCards.get(position);
    }

    public void selectItem(int position) {
        if(IS_SELECTABLE) {
            // Below line is just like a safety check, because sometimes holder could be null,
            // in that case, getAdapterPosition() will return RecyclerView.NO_POSITION
            if (position == RecyclerView.NO_POSITION)
                return;

            // prevent reassignment position
            if(selectedPos == position)
                return;

            // Updating old as well as new positions
            notifyItemChanged(selectedPos);
            selectedPos = position;
            notifyItemChanged(selectedPos);
        }
    }

    public Card getSelectedItem() {
        return getItem(selectedPos);
    }

    // Pass in the card array into the constructor
    public void setCards(ArrayList<Card> cards) {
        mCards = cards;
    }

}
