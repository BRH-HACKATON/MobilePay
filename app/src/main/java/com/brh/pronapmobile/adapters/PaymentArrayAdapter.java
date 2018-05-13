package com.brh.pronapmobile.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.models.Payment;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Created by Keitel on 4/13/18.
 */

public class PaymentArrayAdapter extends RecyclerView.Adapter<PaymentArrayAdapter.ViewHolder> {

    private static final String TAG = "PaymentArrayAdapter";
    private boolean IS_SELECTABLE = false;

    // Store a member variable for the payments
    private ArrayList<Payment> mPayments;
    private int selectedPos = RecyclerView.NO_POSITION;

    private PaymentArrayAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public CardView cvPayment;
        TextView tvVendor;
        TextView tvDate;
        TextView tvAmount;
        TextView tvHolder;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            tvVendor = itemView.findViewById(R.id.tvVendor);
            tvDate = itemView.findViewById(R.id.tvDate);
            tvAmount = itemView.findViewById(R.id.tvAmount);
            tvHolder = itemView.findViewById(R.id.tvHolder);
        }

        public boolean isSelected(int position) {
            return selectedPos == position;
        }

        public void bind(final int position, final OnItemClickListener listener) {
            // Get the data model based on position
            final Payment payment = mPayments.get(position);

            // Highlight the background
            //cvPayment.setBackgroundColor(selectedPos == position ? itemView.getContext().getResources().getColor(R.color.colorAccent) : Color.TRANSPARENT);
            if(IS_SELECTABLE) {
                if(isSelected(position)) {

                } else {

                }
            } else {


            }

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


            //itemView was stored in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(listener != null) {
                        listener.onItemClick(position);
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

                //Log.d(TAG, String.valueOf(selectedPos) + " - payment : " + mPayments.get(selectedPos).get...());
            }

            // Do your another stuff for your onClick
        }
    }

    // Pass in the context, the payment array into the constructor
    public PaymentArrayAdapter(ArrayList<Payment> payments) {
        mPayments = payments;
    }

    // Pass in the context, the payment array into the constructor and specify if item can be selected
    public PaymentArrayAdapter(ArrayList<Payment> payments, boolean isSelectable, OnItemClickListener listener) {
        mPayments = payments;
        IS_SELECTABLE = isSelectable;
        this.listener = listener;
    }


    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public PaymentArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View paymentView = inflater.inflate(R.layout.item_payment, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(paymentView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(PaymentArrayAdapter.ViewHolder viewHolder, int position) {
        viewHolder.bind(position, listener);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mPayments.size();
    }

    public Payment getItem(int position) {
        return mPayments.get(position);
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

    public Payment getSelectedItem() {
        return getItem(selectedPos);
    }

    // Pass in the payment array into the constructor
    public void setPayments(ArrayList<Payment> payments) {
        mPayments = payments;
    }

    private String addLastFourDigits(String text, String maskedText) {
        maskedText += text.substring(Math.max(text.length() - 4, 0), text.length());

        return maskedText;
    }

}
