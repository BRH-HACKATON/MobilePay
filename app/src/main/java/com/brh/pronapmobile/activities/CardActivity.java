package com.brh.pronapmobile.activities;

import android.content.Intent;
import android.icu.text.LocaleDisplayNames;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.adapters.CardArrayAdapter;
import com.brh.pronapmobile.models.Card;
import com.cooltechworks.creditcarddesign.CardEditActivity;
import com.cooltechworks.creditcarddesign.CreditCardUtils;

import java.util.ArrayList;

public class CardActivity extends AppCompatActivity {

    private final static String TAG = "CardActivity";
    final int GET_NEW_CARD = 2;

    private ArrayList<Card> cards;
    private CardArrayAdapter aCards;
    private RecyclerView rvCards;

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
        rvCards = findViewById(R.id.rvCards);
        // connect adapter to list view
        rvCards.setAdapter(aCards);
        // Set layout manager to position the items
        rvCards.setLayoutManager(new LinearLayoutManager(this));

        // Adding Floating Action Button to bottom right of main view
        fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchCardEditActivity();
            }
        });

        if (getIntent().getBooleanExtra("create", false)) {
            Toast.makeText(CardActivity.this, "Create Card Form will be displayed", Toast.LENGTH_SHORT).show();
            launchCardEditActivity();
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

    public void launchCardEditActivity() {
        Intent intent = new Intent(CardActivity.this, CardEditActivity.class);
        startActivityForResult(intent,GET_NEW_CARD);
    }

    @Override
    public void onActivityResult(int reqCode, int resultCode, Intent data) {

        if(resultCode == RESULT_OK && reqCode == GET_NEW_CARD) {

            String cardHolderName = data.getStringExtra(CreditCardUtils.EXTRA_CARD_HOLDER_NAME);
            String cardNumber = data.getStringExtra(CreditCardUtils.EXTRA_CARD_NUMBER);
            String expiry = data.getStringExtra(CreditCardUtils.EXTRA_CARD_EXPIRY);
            String cvv = data.getStringExtra(CreditCardUtils.EXTRA_CARD_CVV);

            try {
                Card card = new Card();
                card.setNumber(cardNumber);
                card.setHolder(cardHolderName);
                card.setExpiry(expiry);
                card.setCvv(cvv);

                card.save();
                Log.d(TAG, "Card added : " + card.getId());

                Toast.makeText(CardActivity.this, "Carte de Crédit ajoutée!", Toast.LENGTH_LONG).show();

                listCards();

            } catch (Exception e) {
                e.printStackTrace();

                Toast.makeText(CardActivity.this, "Une Erreur est survenue, essayez encore!", Toast.LENGTH_LONG).show();
            }

        }
    }

    public void listCards() {
        // retrieve all cards from DB
        cards = Card.all();
        //Log.d(TAG, "cards size: " + String.valueOf(cards.size()));

        aCards.setCards(cards);
        aCards.notifyDataSetChanged();
    }

    public void clearCards() {
        rvCards.removeAllViews();
    }

}
