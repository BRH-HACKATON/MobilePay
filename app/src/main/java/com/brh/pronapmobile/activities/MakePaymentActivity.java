package com.brh.pronapmobile.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.brh.pronapmobile.R;
import com.brh.pronapmobile.adapters.CardArrayAdapter;
import com.brh.pronapmobile.utils.AlertMessage;
import com.brh.pronapmobile.fragments.PinDialogFragment;
import com.brh.pronapmobile.models.Card;
import com.brh.pronapmobile.models.Payment;
import com.brh.pronapmobile.utils.BitmapEncoder;
import com.brh.pronapmobile.utils.MiddleItemFinder;
import com.brh.pronapmobile.utils.Procryptor;
import com.brh.pronapmobile.utils.SMSUtils;
import com.google.zxing.WriterException;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.SecretKey;

import jp.wasabeef.recyclerview.animators.FadeInUpAnimator;

public class MakePaymentActivity extends AppCompatActivity {

    public static final String TAG = "MakePaymentActivity";

    // QR Code Scan Preparation
    private IntentIntegrator qrScan;
    private AppCompatButton btnScan;
    private ScrollView svScanCodePreparation;


    // QR Code Scan Results
    private JSONObject paymentData = null;
    private ScrollView svScanCodeResult;
    private ImageView ivQRCode;
    private TextView tvVendor;
    private TextView tvPrice;
    private TextView tvProduct;
    private AppCompatButton payButton;
    private AppCompatButton rescanButton;

    private RecyclerView rvCards;
    private LinearLayoutManager layoutManager;

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

        btnScan = findViewById(R.id.scan);
        svScanCodePreparation = findViewById(R.id.svScanCodePreparation);
        svScanCodeResult = findViewById(R.id.svScanCodeResult);

        // Prepare the Payment Views
        ivQRCode = findViewById(R.id.ivQRCode);
        tvVendor = findViewById(R.id.tvVendor);
        tvPrice = findViewById(R.id.tvPrice);
        tvProduct = findViewById(R.id.tvProduct);
        payButton = findViewById(R.id.pay);
        rescanButton = findViewById(R.id.rescan);
        rvCards = findViewById(R.id.rvCards);

        // Hide QR Code Result Layout
        svScanCodeResult.setVisibility(View.GONE);

        // Scan QR Code on click of Scan Button
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQRCode();
            }
        });

        final RecyclerView.SmoothScroller smoothScroller = new LinearSmoothScroller(this) {
            @Override protected int getHorizontalSnapPreference() {
                return LinearSmoothScroller.SNAP_TO_START;
            }
        };

        // initialize Card ArrayList
        cards = new ArrayList<>();
        // initialize Card Array Adapter
        aCards = new CardArrayAdapter(cards, true, null);

        // connect adapter to list view
        rvCards.setAdapter(aCards);
        layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvCards.setLayoutManager(layoutManager);

        setupRecyclerViewCardsListeners();

        listCards();

        // Select default position (0 for now) TODO : Setup default position in Settings
        aCards.selectItem(0);
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

    public void scanQRCode() {
        //intializing scan object
        if(qrScan == null)
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
                //Log.d(TAG, result.getContents());
                setupQRCodeResult(result.getContents());
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setupQRCodeResult(String qrCodeEncryptedString) {

        // STEP 1 - Verify that the QR Code String is from PRONAPP application

        try {
            // DECRYPT QR Code to JSON String
            SecretKey secretKey = Procryptor.generateKey("K83SJKF5JS9PN83SKD340SNC");
            Log.d(TAG, "RAW String found in QR Code : " + qrCodeEncryptedString);

            byte[] qrCodeEncryptedBytes = Base64.decode(qrCodeEncryptedString, Base64.NO_WRAP);
            //Log.d(TAG, "JSON Payment encrypted : " + qrCodeEncryptedString);

            String qrCodeString = Procryptor.decrypt(qrCodeEncryptedBytes, secretKey);
            //Log.d(TAG, "JSON Payment bytes decrypted : " + qrCodeString);

            paymentData = new JSONObject(qrCodeString);
            Log.d(TAG, paymentData.toString());

            // STEP 2 --------------
            // If everything goes well
            // check if JSONObject is ok and contains key "pronap_code" with value "PD$BVQRC"
            if(paymentData.has("pronapp_code") && paymentData.getString("pronapp_code").equals("PD$BVQRC")) {

                // Hide QR Code Preparation Layout and Show QR Code Result Layout
                svScanCodePreparation.setVisibility(View.GONE);
                svScanCodeResult.setVisibility(View.VISIBLE);


                // fill the TextViews with the JSONObject
                loadPaymentViews(qrCodeEncryptedString);
            } else {
                // Then the QR Code is not compatible with Pronapp, Alert Message
                alertIncompatibleQRCode();
            }



        } catch (Exception e) {
            e.printStackTrace();
            // if the decryption is not correct, then probably it is an incompatible QR Code
            alertIncompatibleQRCode();
        }
    }

    public void listCards() {
        rvCards.setVisibility(View.VISIBLE);

        // retrieve all cards from DB
        cards = Card.all();

        aCards.setCards(cards);
        aCards.notifyDataSetChanged();
    }

    public void setupRecyclerViewCardsListeners() {
        rvCards.setItemAnimator(new FadeInUpAnimator());

        final SnapHelper snapHelper = new LinearSnapHelper();
        snapHelper.attachToRecyclerView(rvCards);

        MiddleItemFinder.MiddleItemCallback callback =
                new MiddleItemFinder.MiddleItemCallback() {
                    @Override
                    public void scrollFinished(int middleElement) {
                        // interaction with middle item
                        Log.d(TAG, "Middle Item : " + middleElement);
                        aCards.selectItem(middleElement);
                    }
                };

        rvCards.addOnScrollListener(
                new MiddleItemFinder(this, layoutManager,
                        callback, RecyclerView.SCROLL_STATE_IDLE));
    }

    public void alertIncompatibleQRCode() {
        AlertMessage alert = new AlertMessage(this);
        alert.setTitle("QR Code Icompatible");
        alert.setMessage("Le QR Code scanné ne provient pas d'une requête de Paiement" +
                " de PRONAPP MOBILE. Rescanner pour continuer.");
        alert.setHasPositiveButton(true);
        alert.setBanner(R.drawable.ic_error_outline_black_48dp);
        alert.setBannerViewColorFilter(Color.RED);
        alert.setModal(true);

        alert.setPositiveText("RESCANNER");
        alert.setOnPositiveClickListener(new AlertMessage.OnPositiveClickListener() {
            @Override
            public void onPositiveClick() {
                // relaunch the QR Code Scanning
                scanQRCode();
            }
        });

        alert.setNegativeText("QUITTER");
        alert.setOnNegativeClickListener(new AlertMessage.OnNegativeClickListener() {
            @Override
            public void onNegativeClick() {
                // Quit Activity
                MakePaymentActivity.this.finish();
            }
        });

        alert.show(this.ivQRCode);
    }

    public void loadPaymentViews(String qrCodeEncryptedString) {
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
            Bitmap bitmap = BitmapEncoder.encodeAsBitmap(qrCodeEncryptedString, 1000);
            ivQRCode.setImageBitmap(bitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }

        // set click listener on buttons
        payButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Log card
                //Log.d(TAG, "Paying using Card : " + aCards.getSelectedItem().getMaskedNumber());
                // Send SMS to Phone Number
                //sendSMS();

                // Jump to SMS Payment
                // TODO : Move the code below after Payment has been confirmed with Debit Card PIN
                popupPaymentSuccess();
            }
        });

        rescanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanQRCode();
            }
        });

    }

    public void sendSMS() {
        payButton.setEnabled(false);
        try {
            // TODO : replace the values later with attributes from Card Model
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

        finish();
    }

    public void resumePayment() {
        payButton.setEnabled(true);
    }

    public void popupPaymentSuccess() {
        AlertMessage alert = new AlertMessage(this);
        alert.setBanner(R.drawable.mobile_money_transfer);
        alert.setTitle("Paiement Effectué");
        alert.setMessage("Votre Paiement a été effectué avec Succès");
        alert.setHasPositiveButton(false);
        alert.setPositiveText("VALIDER");
        alert.setNegativeText("QUITTER");
        alert.setModal(false);

        alert.setOnDismissListener(new AlertMessage.OnDismissListener() {
            @Override
            public void onDismiss() {
                //Toast.makeText(MakePaymentActivity.this, "Alert Message dismissed", Toast.LENGTH_SHORT).show();
                MakePaymentActivity.this.finish();
            }
        });

        alert.show(ivQRCode);
    }
}
