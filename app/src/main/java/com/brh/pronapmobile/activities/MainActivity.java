package com.brh.pronapmobile.activities;

import android.content.Intent;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.adapters.PaymentArrayAdapter;

import com.brh.pronapmobile.models.Payment;
import com.brh.pronapmobile.models.User;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton fabBuyer;
    private FloatingActionButton fabVendor;
    private FloatingActionMenu faMenu;

    private IntentIntegrator qrScan;

    private ArrayList<Payment> payments;
    private PaymentArrayAdapter aPayments;
    private ListView lvPayments;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Create Navigation drawer and inflate layout
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            //supportActionBar.setDisplayUseLogoEnabled(true);
            //supportActionBar.setDisplayShowTitleEnabled(true);
            //supportActionBar.setIcon(R.drawable.logo_blue);
            supportActionBar.setTitle("BRH - Pronap Mobile");
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        // initialize Card ArrayList
        payments = new ArrayList<>();
        // initialize Card Array Adapter
        aPayments = new PaymentArrayAdapter(this, payments);
        // find list view
        lvPayments = findViewById(R.id.lvPayments);
        // connect adapter to list view
        lvPayments.setAdapter(aPayments);


        // Set behavior of Navigation drawer
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    // This method will trigger on item Click of navigation menu
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // Set item in checked state
                        menuItem.setChecked(true);
                        processNavigationItemSelection(menuItem);
                        mDrawerLayout.closeDrawers();
                        return true;
                    }
                });


        User user = User.find(1);
        if(user != null) {
            TextView tvEmail = navigationView.getHeaderView(0).findViewById(R.id.tvEmail);
            tvEmail.setText(user.getEmail());
        }

        // Setup Floating Action Button
        setupFloatingActionButtons();

        listPayments();

    }


    @Override
    protected void onPostResume() {
        super.onPostResume();

        listPayments();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Toast.makeText(MainActivity.this, "Action Settings",
                    Toast.LENGTH_SHORT).show();
            return true;
        } else if (id == android.R.id.home) {
            mDrawerLayout.openDrawer(GravityCompat.START);
        }
        return super.onOptionsItemSelected(item);
    }

    //@SuppressWarnings("StatementWithEmptyBody")
    public void processNavigationItemSelection(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        Log.d(TAG, String.valueOf(item.getItemId()));

        if (id == R.id.nav_vendor) {
            startActivity(new Intent(getApplicationContext(), VendorActivity.class));
        } else if (id == R.id.nav_card) {
            startActivity(new Intent(getApplicationContext(), CardActivity.class));
        } else if (id == R.id.nav_logout) {
            logout();
        }
    }


    public void setupFloatingActionButtons() {
        faMenu = findViewById(R.id.faMenu);

        // Adding Floating Action Buttons to bottom right of main view
        fabBuyer = findViewById(R.id.fabBuyer);
        fabBuyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Check if User does not have debit card to propose to create new one
                if(User.hasCard()) {
                    scanQRCode();
                } else {
                    addDebitCard();
                }
            }
        });

        fabVendor = findViewById(R.id.fabVendor);
        fabVendor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(User.hasVendorProfile()) {
                    requestPayment();
                } else {
                    addVendorProfile();
                }
            }
        });

    }

    public void addDebitCard() {
        Intent i = new Intent(this, CardActivity.class);
        i.putExtra("create", true);
        startActivity(i);
    }

    public void addVendorProfile() {
        Intent i = new Intent(this, VendorActivity.class);
        i.putExtra("create", true);
        startActivity(i);
    }

    public void requestPayment() {
        Intent i = new Intent(this, RequestPaymentActivity.class);
        i.putExtra("create", true);
        startActivity(i);
    }

    public void scanQRCode() {
        //intializing scan object

        qrScan = new IntentIntegrator(this);
        qrScan.initiateScan();
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    JSONObject obj = new JSONObject(result.getContents());
                    //Log.d(TAG, result.getContents());

                    Intent i = new Intent(this, MakePaymentActivity.class);
                    i.putExtra("qrCodeString", result.getContents());
                    startActivity(i);

                } catch (JSONException e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    public void logout() {
        User user = User.find(1);
        user.setStatus(0);
        user.save();

        finish();
    }

    public void listPayments() {
        lvPayments.setVisibility(View.VISIBLE);
        aPayments.clear();

        // retrieve all cards from DB
        payments = Payment.all();

        aPayments.addAll(payments);
        aPayments.notifyDataSetChanged();
    }

}
