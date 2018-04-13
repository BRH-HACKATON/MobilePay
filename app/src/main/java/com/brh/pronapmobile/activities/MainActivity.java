package com.brh.pronapmobile.activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.fragments.MakePaymentFragment;
import com.brh.pronapmobile.fragments.RequestPaymentFragment;

import com.brh.pronapmobile.models.Card;
import com.brh.pronapmobile.models.User;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import static android.view.View.GONE;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton fabBuyer;
    private FloatingActionButton fabVendor;

    // by default all Users come with No role
    private UserRole activeRole = UserRole.NONE;
    private Card currentCard;

    private IntentIntegrator qrScan;

    private enum UserRole{
        BUYER,
        VENDOR,
        NONE
    }

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
            //supportActionBar.setTitle(R.string.app_name);
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

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

        // Setup Floating Action Button
        setupFloatingActionButtons();

        // Set Listener to Menu Items Role CheckBox
        setupListenerForRoles();

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
        }
    }

    public void setupListenerForRoles() {
        // Set Listener to Menu Items Role CheckBox
        final MenuItem vendorSwitchItem = navigationView.getMenu().findItem(R.id.nav_vendor_role);
        final CompoundButton vendorSwitchView = (CompoundButton) MenuItemCompat.getActionView(vendorSwitchItem);

        final MenuItem buyerSwitchItem = navigationView.getMenu().findItem(R.id.nav_buyer_role);
        final CompoundButton buyerSwitchView = (CompoundButton) MenuItemCompat.getActionView(buyerSwitchItem);

        vendorSwitchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    buyerSwitchView.setChecked(false);
                } else {
                    if(!buyerSwitchView.isChecked())
                        vendorSwitchView.setChecked(true);
                }

                // Set active Role to Vendor either case
                activeRole = UserRole.VENDOR;
                fabBuyer.setVisibility(GONE);
                fabVendor.setVisibility(View.VISIBLE);
            }
        });

        buyerSwitchView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked) {
                    vendorSwitchView.setChecked(false);
                } else {
                    if(!vendorSwitchView.isChecked())
                        buyerSwitchView.setChecked(true);
                }

                // Set active Role to Buyer either case
                activeRole = UserRole.BUYER;
                fabVendor.setVisibility(GONE);
                fabBuyer.setVisibility(View.VISIBLE);
            }
        });

        if(activeRole == UserRole.BUYER) {
            buyerSwitchView.setChecked(true);
        } else if(activeRole == UserRole.VENDOR) {
            vendorSwitchView.setChecked(true);
        }
    }

    public void setupFloatingActionButtons() {
        // Adding Floating Action Buttons to bottom right of main view
        fabBuyer = findViewById(R.id.fabBuyer);
        fabBuyer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO : Check if User does not have debit card to propose to create new one
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
        fabBuyer.setVisibility(GONE);
        fabVendor.setVisibility(GONE);

        FragmentManager fm = getSupportFragmentManager();
        RequestPaymentFragment fragment = new RequestPaymentFragment();
        fm.beginTransaction().replace(R.id.flMainContent, fragment).commit();
    }

    public void scanQRCode() {
        fabBuyer.setVisibility(GONE);
        fabVendor.setVisibility(GONE);

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
                    Log.d(TAG, result.getContents());

                    //Display MakePaymentFragment

                    FragmentManager fm = getSupportFragmentManager();
                    MakePaymentFragment fragment = MakePaymentFragment.newInstance(result.getContents());
                    fm.beginTransaction().replace(R.id.flMainContent, fragment).commitAllowingStateLoss();;

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

    public void displayFloatingButtons() {
        if(fabBuyer != null)
            fabBuyer.setVisibility(View.VISIBLE);

        if(fabVendor != null)
            fabVendor.setVisibility(View.VISIBLE);
    }

}
