package com.brh.pronapmobile.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.brh.pronapmobile.R;

/**
 * Created by Keitel on 4/26/18.
 */

public class AlertMessage {

    private OnPositiveClickListener positiveListener;
    private OnNegativeClickListener negativeListener;
    // after closing
    private OnDismissListener dismissListener;

    public interface OnPositiveClickListener {
        void onPositiveClick();
    }

    public interface OnNegativeClickListener {
        void onNegativeClick();
    }

    public interface OnDismissListener {
        void onDismiss();
    }

    private Context mContext;
    private PopupWindow popupWindow;

    private RelativeLayout rlBackground;
    private View popupView;
    private ImageView ivBanner;
    // if banner view color filter was passed via set method
    private boolean bannerColorFilterAdded = false;
    private TextView tvTitle;
    private TextView tvMessage;
    private Button btnPositive;
    private Button btnNegative;

    private int banner = R.drawable.ic_check_black_48dp;
    private String title = "Opération Réussie";
    private String message = "Votre opération a été réussie  avec succès.";
    private String positiveText = "OK";
    private String negativeText = "QUITTER";
    private boolean hasPositiveButton = false;
    private boolean isModal = true;

    public AlertMessage(Context context) {
        this.mContext = context;
        onCreate();
    }

    public void setBanner(int banner) {
        this.banner = banner;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setPositiveText(String positiveText) {
        this.positiveText = positiveText;
    }

    public void setNegativeText(String negativeText) {
        this.negativeText = negativeText;
    }

    public void setHasPositiveButton(boolean hasPositiveButton) {
        this.hasPositiveButton = hasPositiveButton;
    }

    public void setBannerViewColorFilter(int color) {
        this.ivBanner.setColorFilter(color);
        this.bannerColorFilterAdded = true;
    }

    public void setModal(boolean modal) {
        isModal = modal;
    }


    public void onCreate() {
        // 1 - Inflate Alert Message View
        // 2 - Set Click Listener to Positive and Negative Button

        // Inflate the layout for this fragment
        LayoutInflater inflater = (LayoutInflater) mContext.getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView =  inflater.inflate(R.layout.alert_message, null);

        rlBackground = popupView.findViewById(R.id.rlBackground);
        ivBanner = popupView.findViewById(R.id.banner);
        tvTitle = popupView.findViewById(R.id.title);
        tvMessage = popupView.findViewById(R.id.message);
        btnPositive = popupView.findViewById(R.id.btnPositive);
        btnNegative = popupView.findViewById(R.id.btnNegative);

        btnPositive.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close Alert anyway
                closePopupWindow();
                if(positiveListener != null)
                    positiveListener.onPositiveClick();
            }
        });

        btnNegative.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closePopupWindow();
                if(negativeListener != null)
                    negativeListener.onNegativeClick();
            }
        });
    }

    public void show(View parentView) {
        // 1 - Set values to Views
        // 2 - Apply Logic behaviour with value from Setters (hasPositiveButton, isModal)
        // 3 - Show PopupWindow with the Alert Message View with animation style
        // 4 - Make a Post Delay to set PopupView background to Semi-Transparent after the animation
        //     duration time has elapsed
        // 5 - On Dismiss the PopupWindow, set PopupView background to transparent

        // Set values to Views
        if(banner == R.drawable.ic_check_black_48dp) {
            ivBanner.setColorFilter(R.color.colorVariantSecondary);
        } else {
            if(!bannerColorFilterAdded) {
                ivBanner.setScaleType(ImageView.ScaleType.FIT_XY);
                ivBanner.setColorFilter(null);
            }
        }

        ivBanner.setImageResource(banner);

        tvTitle.setText(title);
        tvMessage.setText(message);
        btnNegative.setText(negativeText);
        btnPositive.setText(positiveText);

        rlBackground.setBackgroundColor(Color.TRANSPARENT);

        // Setup Alert Logics
        setupLogics();

        popupWindow = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        // If the PopupWindow should be focusable
        popupWindow.setFocusable(true);

        int location[] = new int[2];

        // Get the View's(the one that was clicked in the Fragment) location
        popupView.getLocationOnScreen(location);

        popupWindow.setAnimationStyle(R.style.PopupAnimation);
        //popupWindow.update();

        // Using location, the PopupWindow will be displayed right under anchorView
        popupWindow.showAtLocation(parentView, Gravity.CENTER,
                location[0], location[1] );

        // We cannot set PopupWindow animation style with an animation object, so we will hack
        // background
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rlBackground.setBackgroundColor(Color.parseColor("#77000000"));
            }
        }, 300);

    }

    public void setupLogics() {
        if(!hasPositiveButton) {
            btnPositive.setVisibility(View.GONE);
        }

        if(!hasPositiveButton && !isModal) {
            // If you need the PopupWindow to dismiss when when touched outside
            //popupWindow.setBackgroundDrawable(new ColorDrawable());
            // skip method above because we have a semi transparent background
            // instead dismiss PopupWindow when click on semi transparent layout
            rlBackground.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    closePopupWindow();
                }
            });
        }
    }

    public void closePopupWindow() {
        rlBackground.setBackgroundColor(Color.TRANSPARENT);

        // Post delay the dismiss to prevent undesired behavior
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                popupWindow.dismiss();

                if(dismissListener != null)
                    dismissListener.onDismiss();
            }
        }, 50);
    }

    public void setOnPositiveClickListener(AlertMessage.OnPositiveClickListener listener) {
        this.positiveListener = listener;
    }

    public void setOnNegativeClickListener(AlertMessage.OnNegativeClickListener listener) {
        this.negativeListener = listener;
    }

    public void setOnDismissListener(AlertMessage.OnDismissListener listener) {
        this.dismissListener = listener;
    }
}
