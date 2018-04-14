package com.brh.pronapmobile.activities;

import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.adapters.CardArrayAdapter;
import com.brh.pronapmobile.fragments.CreateCardFragment;
import com.brh.pronapmobile.models.Card;

import java.util.ArrayList;

public class CardActivity extends AppCompatActivity {

    private final static String TAG = "CardActivity";

    private ArrayList<Card> cards;
    private CardArrayAdapter aCards;
    private ListView lvCards;

    FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_card);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);

            supportActionBar.setTitle("Vos Cartes Débit");
        }

        // initialize Card ArrayList
        cards = new ArrayList<>();
        // initialize Card Array Adapter
        aCards = new CardArrayAdapter(this, cards);
        // find list view
        lvCards = findViewById(R.id.lvCards);
        // connect adapter to list view
        lvCards.setAdapter(aCards);

        // Adding Floating Action Button to bottom right of main view
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCreateFragment();
            }
        });

        if (getIntent().getBooleanExtra("create", false)) {
            Toast.makeText(CardActivity.this, "Create Card Form will be displayed", Toast.LENGTH_SHORT).show();
            launchCreateFragment();
        } else {
            // Populate Cards
            listCards();
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
        lvCards.setVisibility(View.GONE);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment = new CreateCardFragment();
        fm.beginTransaction().replace(R.id.flCard, fragment).commit();
    }

    public void listCards() {
        lvCards.setVisibility(View.VISIBLE);
        aCards.clear();

        // retrieve all cards from DB
        cards = Card.all();

        aCards.addAll(cards);
        aCards.notifyDataSetChanged();
    }

    public void clearCards() {
        aCards.clear();
    }

}
