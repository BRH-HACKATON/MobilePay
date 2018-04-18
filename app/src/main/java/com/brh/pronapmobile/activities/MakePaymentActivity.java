package com.brh.pronapmobile.activities;

import android.graphics.Bitmap;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.adapters.CardArrayAdapter;
import com.brh.pronapmobile.fragments.PinDialogFragment;
import com.brh.pronapmobile.models.Card;
import com.brh.pronapmobile.models.Payment;
import com.brh.pronapmobile.utils.BitmapEncoder;
import com.brh.pronapmobile.utils.SMSUtils;
import com.google.zxing.WriterException;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

public class MakePaymentActivity extends AppCompatActivity {

    public static final String TAG = "MakePaymentActivity";

    private View rootView;
    private JSONObject paymentData = null;

    private ImageView ivQRCode;
    private TextView tvVendor;
    private TextView tvPrice;
    private TextView tvProduct;
    private AppCompatButton payButton;
    private AppCompatButton cancelButton;

    private RecyclerView rvCards;

    private ArrayList<Card> cards;
    private CardArrayAdapter aCards;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_payment);

        // Adding Toolbar to Main screen
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Adding menu icon to Toolbar
        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            supportActionBar.setDisplayHomeAsUpEnabled(true);

            supportActionBar.setTitle("Effectuer un Paiement");
        }


        ivQRCode = findViewById(R.id.ivQRCode);
        tvVendor = findViewById(R.id.tvVendor);
        tvPrice = findViewById(R.id.tvPrice);
        tvProduct = findViewById(R.id.tvProduct);
        payButton = findViewById(R.id.pay);
        cancelButton = findViewById(R.id.cancel_payment);
        rvCards = findViewById(R.id.rvCards);

        // initialize Card ArrayList
        cards = new ArrayList<>();
        // initialize Card Array Adapter
        aCards = new CardArrayAdapter(this, cards, true);
        // connect adapter to list view
        rvCards.setAdapter(aCards);
        LinearLayoutManager horizontalLayoutManager
                = new LinearLayoutManager(MakePaymentActivity.this, LinearLayoutManager.HORIZONTAL, false);
        rvCards.setLayoutManager(horizontalLayoutManager);


        listCards();

        // Select default position (0 for now) TODO : Setup default position in Settings
        aCards.selectItem(0);

        // Change String JSONObject
        String qrCodeString = getIntent().getStringExtra("qrCodeString");
        try {
            paymentData = new JSONObject(qrCodeString);
            // fill the TextViews with the JSONObject
            loadPaymentViews(qrCodeString);

        } catch (Exception e) {
            e.printStackTrace();
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


    public void listCards() {
        rvCards.setVisibility(View.VISIBLE);

        // retrieve all cards from DB
        cards = Card.all();

        aCards.setCards(cards);
        aCards.notifyDataSetChanged();
    }

    public void loadPaymentViews(String qrCodeString) {
        //Log.d(TAG, "QR CODE JSONObject : " + paymentData.toString());
        try {
            tvVendor.setText(paymentData.getString("vendor_name"));
            tvPrice.setText(paymentData.getString("price"));
            tvProduct.setText(paymentData.getString("product"));
        } catch (Exception e) {
            e.printStackTrace();

            Toast.makeText(this, "Les informations de ce QR Code ne sont pas compatibles avec notre système de paiement",
                    Toast.LENGTH_LONG).show();
        }

        // Create QR Code image again for vendor
        try {
            Bitmap bitmap = BitmapEncoder.encodeAsBitmap(qrCodeString, 1000);
            ivQRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        // set click listener on buttons
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log card
                Log.d(TAG, "Paying using Card : " + aCards.getSelectedItem().getNumber());
                // Send SMS to Phone Number
                sendSMS();
            }
        });

        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    public void sendSMS() {
        payButton.setEnabled(false);
        try {
            // TODO : replace the values later with attributes from Vendor Model
            Card card = (Card) aCards.getItem(0);

            if(card != null) {
                Log.d(TAG, "Card Selected: " + card.getHolder());

                paymentData.put("card_number", card.getNumber());
                paymentData.put("card_holder_name", card.getHolder());
                paymentData.put("card_expiry_date", card.getHolder());
                paymentData.put("card_cvv", card.getCvv());

                // Send SMS to BRH phone number and BHR will get this Phone number automatically
                // TODO : Simulate BRH phone with Dev Phone
                SMSUtils.sendSMS(this, "+50937567873", paymentData.toString(), true);
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur survenue en envoyant le SMS!",
                    Toast.LENGTH_LONG).show();
        }
    }

    public void simulateBRHCatchingSMS() {
        Toast.makeText(this, "BRH va recevoir le SMS!",
                Toast.LENGTH_LONG).show();
    }

    public void confirmCreditCardWithPIN() {
        //Toast.makeText(getContext(), "Enter Credit Card PIN to confirm!",
        //       Toast.LENGTH_LONG).show();

        PinDialogFragment newFragment = PinDialogFragment.newInstance("Confirmer Paiement");
        getSupportFragmentManager().beginTransaction().add(newFragment, "tag").commit();
    }

    public void onPaymentSuccess() {

        try {
            // save Payment object
            Payment payment = new Payment();
            payment.setAmount(Double.parseDouble(paymentData.getString("price")));
            payment.setDate(Calendar.getInstance().getTime());
            payment.setVendorName(paymentData.getString("vendor_name"));
            payment.setVendorAccount(paymentData.getString("vendor_account_number"));
            payment.setCardNumber(paymentData.getString("card_number"));
            payment.setCardHolder(paymentData.getString("card_holder_name"));

            payment.save();

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur : Paiement non sauvegardé!",
                    Toast.LENGTH_LONG).show();
        }

        // Act like BRH Server and send SMS confirmation
        sendSMSConfirmationToVendor();
    }

    public void sendSMSConfirmationToVendor() {
        // BRH JOB
        try {
            SMSUtils.sendSMS(this, paymentData.getString("vendor_phone"),
                    "Votre Paiement a été recu de l'acheteur "+paymentData.getString("card_holder_name"), false);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Erreur survenue en envoyant le SMS de Confirmation!",
                    Toast.LENGTH_LONG).show();
        }

        payButton.setEnabled(true);

        // We close the Fragment
        cancelButton.callOnClick();
    }

    public void resumePayment() {
        payButton.setEnabled(true);
    }
}
