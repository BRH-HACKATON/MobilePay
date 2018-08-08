package com.brh.pronapmobile.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.telephony.SmsManager;
import android.telephony.SmsMessage;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Keitel on 4/11/18.
 */

public class SmsBroadcastReceiver extends BroadcastReceiver {

    private static final String TAG = "SmsBroadcastReceiver";

    public interface onSmsSentListener {
        void onSmsSent(boolean isSent, @Nullable String message);
    }

    public interface onSmsReceivedListener {
        void onSmsReceived(boolean isReceived, @Nullable SmsMessage smsMessage);
    }

    private onSmsSentListener sentListener;
    private onSmsReceivedListener receivedListener;

    public static final String SENT_SMS_ACTION_NAME = "SMS_SENT";
    public static final String DELIVERED_SMS_ACTION_NAME = "SMS_DELIVERED";
    public static final String RECEIVED_SMS_ACTION_NAME = "android.provider.Telephony.SMS_RECEIVED";
    public static final String SMS_EXTRA_NAME = "pdus";

    private static HashMap<String, Boolean> smsSended = new HashMap<>();
    private static String message = "";

    public void setOnSmsSentListener(onSmsSentListener sentListener) {
        this.sentListener = sentListener;
    }

    public void setOnSmsReceivedListener(onSmsReceivedListener receivedListener) {
        this.receivedListener = receivedListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        Log.d(TAG, "Received action : " + intent.getAction());

        //Detect sms sent
        if (intent.getAction().equals(SENT_SMS_ACTION_NAME)) {
            // Set Failure Message
            String failureMessage = null;

            switch (getResultCode()) {
                case Activity.RESULT_OK: // Sms sent
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE: // generic failure
                    //Toast.makeText(context, "SMS not sent", Toast.LENGTH_LONG).show();
                    failureMessage = "SMS not sent - Generic Failure";
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE: // No service
                    //Toast.makeText(context, "SMS not sent No Service", Toast.LENGTH_LONG).show();
                    failureMessage = "SMS not sent - No Service";
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU: // null pdu
                    //Toast.makeText(context, "SMS not sent", Toast.LENGTH_LONG).show();
                    failureMessage = "SMS not sent - Null PDU";
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF: //Radio off
                    //Toast.makeText(context, "SMS not sent No Radio", Toast.LENGTH_LONG).show();
                    failureMessage = "SMS not sent - Radio off";
                    break;
            }

            /*
            //Toast.makeText(context, "SMS Sent", Toast.LENGTH_LONG).show();
            // Sometimes SMS is divided multiple SMSs to be able to send all content length
            // so the broadcast may receive SENT SMS multiple times.
            // Check if one part of the SMS has already been broadcasted (Hashmap contain)
            Log.d(TAG, "SMS message part already sent ? : " + smsSended.get(message));
            if (!smsSended.get(message)) {

            }*/

            if (failureMessage != null) {
                if(sentListener != null) {
                    sentListener.onSmsSent(false, failureMessage);
                }
            } else {
                //Toast.makeText(context, "SMS Sent", Toast.LENGTH_LONG).show();
                Log.d(TAG, "SMS sent");

                // Tell SmsBroadcastReceiver that part of this message has now been broadcasted to prevent
                // further duplicate operation
                //smsSended.put(message, true);

                // SMS has been sent, send response to Context (MakePaymentFragment in our case)
                if (sentListener != null) {
                    sentListener.onSmsSent(true, "SMS Sent");
                }
            }
        }
        //detect sms delivered
        else if (intent.getAction().equals(DELIVERED_SMS_ACTION_NAME)) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "SMS delivered", Toast.LENGTH_LONG).show();

                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(context, "SMS not delivered", Toast.LENGTH_LONG).show();
                    break;
            }

        }

        //detect sms received
        /*else if (intent.getAction().equals(RECEIVED_SMS_ACTION_NAME)) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "SMS received", Toast.LENGTH_LONG).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(context, "SMS not received", Toast.LENGTH_LONG).show();
                    break;
            }
        }*/

        //detect sms received
        // based on code from https://stackoverflow.com/questions/39526138/broadcast-receiver-for-receive-sms-is-not-working-when-declared-in-manifeststat
        else if(intent.getAction().equals(RECEIVED_SMS_ACTION_NAME)) {
            // Set Failure Message
            String failureMessage = null;

            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "SMS received", Toast.LENGTH_LONG).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(context, "SMS not received", Toast.LENGTH_LONG).show();
                    failureMessage = "SMS not received";
                    break;
            }

            if (failureMessage != null) {
                if(receivedListener != null) {
                    receivedListener.onSmsReceived(false, null);
                }
            } else {

                // Get the SMS map from Intent
                Bundle extras = intent.getExtras();

                String messages = "";

                if (extras != null) {
                    // Get received SMS array
                    Object[] smsExtra = (Object[]) extras.get(SMS_EXTRA_NAME);

                    // Get ContentResolver object for pushing encrypted SMS to the incoming folder
                    //ContentResolver contentResolver = context.getContentResolver();

                    for (int i = 0; i < smsExtra.length; ++i) {
                        SmsMessage sms = SmsMessage.createFromPdu((byte[]) smsExtra[i]);

                        String body = sms.getMessageBody();
                        String address = sms.getOriginatingAddress();
                        //sms.getDisplayOriginatingAddress();

                        //messages += "SMS from " + address + " :\n";
                        //messages += body + "\n";
                        //Log.d(TAG, messages);

                        //Communicate message to listeners
                        if (receivedListener != null) {
                            receivedListener.onSmsReceived(true, sms);
                        }
                    }

                    // Display SMS message
                    //Toast.makeText(context, messages, Toast.LENGTH_SHORT).show();
                }

                // WARNING!!!
                // If you uncomment the next line then received SMS will not be put to incoming.
                // Be careful!
                // this.abortBroadcast();
            }
        }
    }

    /**
     * Test if device can send SMS
     * @param context
     * @return
     */
    public static boolean canSendSMS(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_TELEPHONY);
    }

    public void sendSMS(final Context context, String phoneNumber, String message) {
        if (!canSendSMS(context)) {
            Toast.makeText(context, "Cannot send SMS", Toast.LENGTH_LONG).show();
            return;
        }

        SmsBroadcastReceiver.message = message;

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT_SMS_ACTION_NAME), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED_SMS_ACTION_NAME), 0);

        //register for sending and delivery
        context.registerReceiver(this, new IntentFilter(SENT_SMS_ACTION_NAME));
        context.registerReceiver(this, new IntentFilter(DELIVERED_SMS_ACTION_NAME));

        // register RECEIVED SMS outside so it can stay longer (for ever)
        //mContext.registerReceiver(this, new IntentFilter(RECEIVED_SMS_ACTION_NAME));

        SmsManager sms = SmsManager.getDefault();
        //ArrayList<String> parts = sms.divideMessage(message);

        ArrayList<PendingIntent> sendList = new ArrayList<>();
        sendList.add(sentPI);

        ArrayList<PendingIntent> deliverList = new ArrayList<>();
        deliverList.add(deliveredPI);

        // TODO : uncomment after testing
        // Tell the Class that no part of this message has been received yet
        //smsSended.put(message, false);

        //sms.sendMultipartTextMessage(phoneNumber,null, parts, sendList, deliverList);
        sms.sendTextMessage(phoneNumber, null, message, sentPI, deliveredPI);

        //we unsubscribed in 30 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    context.unregisterReceiver(SmsBroadcastReceiver.this);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, 30000);

    }
}
