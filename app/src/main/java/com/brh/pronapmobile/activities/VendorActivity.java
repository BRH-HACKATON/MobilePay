package com.brh.pronapmobile.activities;


import android.content.res.Resources;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.adapters.VendorArrayAdapter;
import com.brh.pronapmobile.fragments.CreateVendorFragment;
import com.brh.pronapmobile.models.Vendor;

import java.util.ArrayList;

public class VendorActivity extends AppCompatActivity {

    private ArrayList<Vendor> vendors;
    private VendorArrayAdapter aVendors;
    private ListView lvVendors;
    
    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vendor);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            supportActionBar.setTitle("Vos Comptes BRH");
        }

        // initialize Vendor ArrayList
        vendors = new ArrayList<>();
        // initialize Vendor Array Adapter
        aVendors = new VendorArrayAdapter(this, vendors);
        // find list view
        lvVendors = findViewById(R.id.lvVendors);
        // connect adapter to list view
        lvVendors.setAdapter(aVendors);

        // Adding Floating Action Button to bottom right of main view
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCreateFragment();
            }
        });

        if (getIntent().getBooleanExtra("create", false)) {
            Toast.makeText(this, "Create Vendor Form will be displayed", Toast.LENGTH_SHORT).show();
            launchCreateFragment();
        } else {
            listVendors();
        }

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

    public void launchCreateFragment() {
        // hide listView and Floating Action Button
        fab.setVisibility(View.GONE);
        lvVendors.setVisibility(View.GONE);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = new CreateVendorFragment();
        fm.beginTransaction().replace(R.id.flVendor, fragment).commit();
    }

    public void listVendors() {
        lvVendors.setVisibility(View.VISIBLE);
        aVendors.clear();

        // retrieve all vendors from DB
        vendors = Vendor.all();

        aVendors.addAll(vendors);
        aVendors.notifyDataSetChanged();
    }

    public void clearVendors() {
        aVendors.clear();
    }
}
