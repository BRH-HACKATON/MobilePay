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
import com.brh.pronapmobile.models.Vendor;

import org.w3c.dom.Text;

import java.util.ArrayList;

/**
 * Created by Keitel on 4/12/18.
 */

public class VendorArrayAdapter extends RecyclerView.Adapter<VendorArrayAdapter.ViewHolder> {

    private static final String TAG = "VendorArrayAdapter";
    private boolean IS_SELECTABLE = false;

    // Store a member variable for the vendors
    private ArrayList<Vendor> mVendors;
    private int selectedPos = RecyclerView.NO_POSITION;

    private VendorArrayAdapter.OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    // Provide a direct reference to each of the views within a data item
    // Used to cache the views within the item layout for fast access
    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        public CardView cvVendor;
        public ImageView ivCheck;
        public Toolbar toolbar;
        public TextView tvName;
        public TextView tvCode;
        public TextView tvAccount;
        public TextView tvRouting;
        public TextView tvPhone;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            cvVendor = itemView.findViewById(R.id.cvVendor);
            ivCheck = itemView.findViewById(R.id.ivCheck);
            toolbar = itemView.findViewById(R.id.card_toolbar);
            tvName = itemView.findViewById(R.id.tvName);
            tvCode = itemView.findViewById(R.id.tvCode);
            tvAccount = itemView.findViewById(R.id.tvAccount);
            tvRouting = itemView.findViewById(R.id.tvRouting);
            tvPhone = itemView.findViewById(R.id.tvPhone);
        }

        public boolean isSelected(int position) {
            return selectedPos == position;
        }

        public void bind(final int position, final OnItemClickListener listener) {
            // Get the data model based on position
            final Vendor vendor = mVendors.get(position);

            // Highlight the background
            //cvVendor.setBackgroundColor(selectedPos == position ? itemView.getContext().getResources().getColor(R.color.colorAccent) : Color.TRANSPARENT);
            if(IS_SELECTABLE) {
                // Scale Up if it's selected and Scale Down if it's not selected
                if(isSelected(position)) {
                    cvVendor.setScaleX(1f);
                    cvVendor.setScaleY(1f);
                    ivCheck.setVisibility(View.VISIBLE);
                } else {
                    cvVendor.setScaleX(0.8f);
                    cvVendor.setScaleY(0.8f);
                    ivCheck.setVisibility(View.GONE);
                }
            } else {
                // Add a Toolbar Menu for editing and deleting
                toolbar.inflateMenu(R.menu.menu_card_vendor);
                toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        int id = item.getItemId();

                        if(id == R.id.action_edit) {
                            Log.d(TAG, "Menu edit clicked for " + vendor.getName());
                        } else if(id == R.id.action_delete) {
                            Log.d(TAG, "Menu delete clicked for " + vendor.getName());
                        }

                        return false;
                    }
                });
            }

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

                //Log.d(TAG, String.valueOf(selectedPos) + " - vendor : " + mVendors.get(selectedPos).get...());
            }

            // Do your another stuff for your onClick
        }
    }

    // Pass in the context, the vendor array into the constructor
    public VendorArrayAdapter(ArrayList<Vendor> vendors) {
        mVendors = vendors;
    }

    // Pass in the context, the vendor array into the constructor and specify if item can be selected
    public VendorArrayAdapter(ArrayList<Vendor> vendors, boolean isSelectable, OnItemClickListener listener) {
        mVendors = vendors;
        IS_SELECTABLE = isSelectable;
        this.listener = listener;
    }


    // Usually involves inflating a layout from XML and returning the holder
    @Override
    public VendorArrayAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);

        // Inflate the custom layout
        View vendorView = inflater.inflate(R.layout.item_vendor, parent, false);

        // Return a new holder instance
        ViewHolder viewHolder = new ViewHolder(vendorView);
        return viewHolder;
    }

    // Involves populating data into the item through holder
    @Override
    public void onBindViewHolder(VendorArrayAdapter.ViewHolder viewHolder, int position) {
        viewHolder.bind(position, listener);
    }

    // Returns the total count of items in the list
    @Override
    public int getItemCount() {
        return mVendors.size();
    }

    public Vendor getItem(int position) {
        return mVendors.get(position);
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

    public Vendor getSelectedItem() {
        return getItem(selectedPos);
    }

    // Pass in the vendor array into the constructor
    public void setVendors(ArrayList<Vendor> vendors) {
        mVendors = vendors;
    }

    private String addLastFourDigits(String text, String maskedText) {
        maskedText += text.substring(Math.max(text.length() - 4, 0), text.length());

        return maskedText;
    }

}
