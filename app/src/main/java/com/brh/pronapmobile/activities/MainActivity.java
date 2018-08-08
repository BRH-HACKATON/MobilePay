package com.brh.pronapmobile.activities;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsMessage;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.brh.pronapmobile.PronapApplication;
import com.brh.pronapmobile.R;
import com.brh.pronapmobile.adapters.PaymentArrayAdapter;

import com.brh.pronapmobile.models.Payment;
import com.brh.pronapmobile.models.User;
import com.brh.pronapmobile.utils.AlertMessage;
import com.brh.pronapmobile.utils.Procryptor;
import com.brh.pronapmobile.utils.SmsBroadcastReceiver;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Calendar;

import javax.crypto.SecretKey;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private static Context mContext;

    private static JSONObject pendingPaymentData;

    private SmsBroadcastReceiver smsReceiver;
    private DrawerLayout mDrawerLayout;
    private NavigationView navigationView;
    private FloatingActionButton fabBuyer;
    private FloatingActionButton fabVendor;
    private FloatingActionMenu faMenu;

    private ArrayList<Payment> payments;
    private PaymentArrayAdapter aPayments;
    private RecyclerView rvPayments;

    public static Context getContext() {
        return mContext;
    }

    public static void setPendingPaymentData(@Nullable JSONObject paymentData) {
        pendingPaymentData = paymentData;
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
            supportActionBar.setTitle("BRH - Pronap Mobile");
            supportActionBar.setHomeAsUpIndicator(R.drawable.ic_menu_white);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
        }

        mContext = this;

        // initialize Card ArrayList
        payments = new ArrayList<>();
        // initialize Card Array Adapter
        aPayments = new PaymentArrayAdapter(payments);
        // find list view
        rvPayments = findViewById(R.id.rvPayments);
        // connect adapter to list view
        rvPayments.setAdapter(aPayments);
        // Set layout manager to position the items
        rvPayments.setLayoutManager(new LinearLayoutManager(this));

        // set RecyclerView animation on items
        // jp.wasabeef.recyclerview.animators.{Animators Class}
        rvPayments.setItemAnimator(new jp.wasabeef.recyclerview.animators.FadeInLeftAnimator());

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

        manageSms();
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
                    makePayment();
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

    public void makePayment() {
        Intent i = new Intent(this, MakePaymentActivity.class);
        startActivity(i);
    }


    public void logout() {
        User user = User.find(1);
        user.setStatus(0);
        user.save();

        finish();
    }

    public void listPayments() {
        rvPayments.setVisibility(View.VISIBLE);

        // retrieve all cards from DB
        payments = Payment.all();

        aPayments.setPayments(payments);
        aPayments.notifyDataSetChanged();
    }

    public void manageSms() {
        // ---- SmsBroadcasListener facts ----
        // - SmsBroadcastReceiver works when registering for action like (Sent, Delivered, Received)
        // - Receiver listens to SMS actions (1 or multiple) forever until we unregister the Receiver
        // - Each instance of SmsBroadcastReceiver listen to SMS independantly from other instances

        // ---- Our Strategy ----
        // We will register Sms Received action for ever
        // We will register Sms Sent action for 1 minute

        // 1- SMS is Sent by App when making a Payment (after QR Code scanning)
        // * Listening to SMS Sent rules :
        //   - If SMS fails to be sent, then propose to try again
        //     However, the receiver is register for 60 seconds and when retrying a new instance
        //     will create 2 receivers with 1 of 60 seconds and the old one wit less than 60 seconds.
        //     So to avoid it, we must ensure that the same SmsBroadcastReceiver instance is used
        //     when Paying, and unregister Receiver before Sending new SMS to get fresh 60s again
        //   - If SMS is successfully sent, alert message and unregister the Receiver manual or auto


        // 2- SMS is received by Vendor App
        // * Listening to SMS Received rules :
        //   - Put a Transaction ID in the receiver instance to find the appropriate the sms to process



        // 3- SMS is received by Payer App

        // Unregister smsReceiver each time to
        try {
            if (smsReceiver != null) {
                this.unregisterReceiver(smsReceiver);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

        smsReceiver = new SmsBroadcastReceiver();
        // register RECEIVED SMS for ever (until we stop it)
        this.registerReceiver(smsReceiver, new IntentFilter(SmsBroadcastReceiver.RECEIVED_SMS_ACTION_NAME));

        // set SMS received listener
        smsReceiver.setOnSmsReceivedListener(new SmsBroadcastReceiver.onSmsReceivedListener() {
            @Override
            public void onSmsReceived(boolean isReceived, SmsMessage smsMessage) {

                if(isReceived) {
                    Log.d(TAG, smsMessage.getDisplayMessageBody());

                    // Process the SmsMessage
                    processSMSAsBRHServer(smsMessage);
                }
            }
        });

    }

    public void processSMSAsBRHServer(SmsMessage smsMessage) {
        Toast.makeText(this, "BRH received " + smsMessage.getDisplayMessageBody(), Toast.LENGTH_LONG).show();

        // ***** 1- SMS is received by VENDOR APP *****
        // * Listening to SMS Received rules :
        //   - Put a Transaction ID in the receiver instance to find the appropriate the sms to process

        String address = smsMessage.getDisplayOriginatingAddress();
        String encryptedBody = smsMessage.getDisplayMessageBody();

        Log.d(TAG, "Message from " + address);
        Log.d(TAG, "Message Encrypted Body : " + encryptedBody);

        //Toast.makeText(this, encryptedBody, Toast.LENGTH_SHORT).show();


        // STEP 1 -------------
        // check if message is from PRONAP MOBILE

        //if(!address.equals("+50937567873"))
        //    return;

        if(pendingPaymentData == null)
            return;


        try {
            // DECRYPT QR Code to JSON String
            /*SecretKey secretKey = Procryptor.generateKey("K83SJKF5JS9PN83SKD340SNC");
            Log.d(TAG, "SMS RECEIVED - RAW String found in QR Code : " + encryptedBody);

            byte[] qrCodeEncryptedBytes = Base64.decode(encryptedBody, Base64.NO_WRAP);
            //Log.d(TAG, "JSON Payment encrypted : " + qrCodeEncryptedString);

            String qrCodeString = Procryptor.decrypt(qrCodeEncryptedBytes, secretKey);
            Log.d(TAG, "SMS RECEIVED - JSON Payment bytes decrypted : " + qrCodeString);*/

            // Test with no encryption
            JSONObject paymentData = new JSONObject(encryptedBody);
            Log.d(TAG, paymentData.toString());

            // STEP 2 --------------
            // If everything goes well
            // check if JSONObject is ok and contains key "pronap_code" with value "PD$BVQRC"
            if(paymentData.has("p_code") && paymentData.getString("p_code").equals("PD$BVQRC")
                    && paymentData.getString("t_id").equals(pendingPaymentData.get("t_id"))) {
                Log.d(TAG, "MESSAGE HAS BEEN RECEIVED FROM PRONAPP MOBILE");

                String messages = "SMS from " + address + " :\n";
                messages += encryptedBody + "\n";
                //Log.d(TAG, messages);


                // Toast SMS message
                Toast.makeText(this, messages, Toast.LENGTH_LONG).show();

                // TODO : Stop the SMS from Broadcasting farther than that so that Message App can't find it
                //smsReceiver.br

                // Now we can save the Payment into Vendor App Database
                // save Payment object
                Payment payment = new Payment();

                // From Pending Payment Data
                payment.setAmount(Double.parseDouble(pendingPaymentData.getString("price")));
                payment.setDate(Calendar.getInstance().getTime());
                payment.setVendorName(pendingPaymentData.getString("vendor_name"));
                payment.setVendorAccount(pendingPaymentData.getString("vendor_account_number"));


                // From passed sms payment data
                payment.setCardNumber(paymentData.getString("c_num"));
                payment.setCardHolder(paymentData.getString("c_hold"));

                payment.save();
                pendingPaymentData = null;

                // Refresh Payments list
                listPayments();

                popupReceivedPaymentSuccess("Un Paiement de " + payment.getAmount() + " HTG a été reçu de "
                        + payment.getCardHolder());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void popupReceivedPaymentSuccess(String message) {
        AlertMessage alert = new AlertMessage(this);
        alert.setBanner(R.drawable.mobile_money_transfer);
        alert.setTitle("Paiement Recu");
        alert.setMessage(message);
        alert.setHasPositiveButton(false);
        alert.setNegativeText("FERMER");
        alert.setModal(false);

        alert.show(rvPayments);
    }
}
