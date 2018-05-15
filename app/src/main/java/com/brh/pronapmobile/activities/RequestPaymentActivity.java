package com.brh.pronapmobile.activities;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.LinearSmoothScroller;
import android.support.v7.widget.LinearSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.adapters.VendorArrayAdapter;
import com.brh.pronapmobile.models.Vendor;
import com.brh.pronapmobile.utils.BitmapEncoder;
import com.brh.pronapmobile.utils.MiddleItemFinder;
import com.google.gson.Gson;
import com.google.zxing.WriterException;

import java.util.ArrayList;
import java.util.HashMap;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;

public class RequestPaymentActivity extends AppCompatActivity {

    public static final String TAG = "RequestPaymentActivity";

    public final static int SIZE = 1000;

    private ImageView qrCodeImageView;
    private AppCompatButton generateButton;
    private RecyclerView rvVendors;
    private LinearLayoutManager layoutManager;

    private ArrayList<Vendor> vendors;
    private VendorArrayAdapter aVendors;

    private HashMap<String, String> paymentData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request_payment);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);

            supportActionBar.setTitle("Recevoir un Paiement");
        }

        qrCodeImageView = findViewById(R.id.qrCode);
        generateButton = findViewById(R.id.generate);
        rvVendors = findViewById(R.id.rvVendors);

        final RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(this) {
            @Override protected int getHorizontalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };


        // initialize Vendor ArrayList
        vendors = new ArrayList<>();
        // initialize Vendor Array Adapter
        aVendors = new VendorArrayAdapter(vendors, true, null);
        // connect adapter to list view
        rvVendors.setAdapter(aVendors);


        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvVendors.setLayoutManager(layoutManager);

        setupRecyclerViewVendorsListeners();

        listVendors();

        // Select default position (0 for now) TODO : Setup default position in Settings
        aVendors.selectItem(0);

        generateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                generateQRCode();
            }
        });

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    public void setupRecyclerViewVendorsListeners() {
        rvVendors.setItemAnimator(new FadeInUpAnimator());

        final SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvVendors);

        MiddleItemFinder.MiddleItemCallback callback =
                new MiddleItemFinder.MiddleItemCallback() {
                    @Override
                    public void scrollFinished(int middleElement) {
                        // interaction with middle item
                        Log.d(TAG, "Middle Item : " + middleElement);
                        aVendors.selectItem(middleElement);
                    }
                };

        rvVendors.addOnScrollListener(
                new MiddleItemFinder(this, layoutManager,
                        callback, RecyclerView.SCROLL_STATE_IDLE));
    }

    public void listVendors() {
         // retrieve all vendors from DB
        vendors = Vendor.all();

        aVendors.setVendors(vendors);
        aVendors.notifyDataSetChanged();
    }

    public void generateQRCode() {
        // Fill HasMap with input informations
        EditText etProduct = findViewById(R.id.product);
        EditText etPrice = findViewById(R.id.price);

        paymentData = new HashMap<>();
        paymentData.put("product", etProduct.getText().toString());
        paymentData.put("price", etPrice.getText().toString());

        // Add vendor information to HashMap

        Vendor vendor = (Vendor) aVendors.getItem(0);
        if(vendor != null) {
            Log.d(TAG, "Vendor Selected: " + vendor.getName());
            paymentData.put("vendor_name", vendor.getName());
            paymentData.put("vendor_brh_code", vendor.getCode());
            paymentData.put("vendor_account_number", vendor.getAccount());
            paymentData.put("vendor_routing_number", vendor.getRouting());
            paymentData.put("vendor_phone", vendor.getPhone());

            // to verify QR Code is compatible with the App
            paymentData.put("pronapp_code", "PD$BVQRC");

        } else {
            Toast.makeText(this, "Aucun Compte BRH n'a été sélectionné!", Toast.LENGTH_LONG).show();
            return;
        }

        // Convert HashMap to JSON with Gson
        Gson gson = new Gson();
        String json = gson.toJson(paymentData);

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
            LinearLayout llPayment = findViewById(R.id.llPaymentInfo);
            llPayment.setVisibility(View.GONE);

            // unhide layout QRCode
            LinearLayout llCode = findViewById(R.id.llPaymentQRCode);
            llCode.setVisibility(View.VISIBLE);
        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

}
