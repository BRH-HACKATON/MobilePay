package com.brh.pronapmobile.utils;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Handler;
import android.telephony.SmsManager;
import android.widget.Toast;

import com.brh.pronapmobile.activities.MakePaymentActivity;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Keitel on 4/11/18.
 */

public class SMSUtils extends BroadcastReceiver {

    public static final String SENT_SMS_ACTION_NAME = "SMS_SENT";
    public static final String DELIVERED_SMS_ACTION_NAME = "SMS_DELIVERED";
    public static final String RECEIVED_SMS_ACTION_NAME = "SMS_RECEIVED";

    private static Activity activity;
    private static boolean notifyActivity = false;
    private static HashMap<String, Boolean> smsSended = new HashMap<>();
    private static String message = "";


    @Override
    public void onReceive(Context context, Intent intent) {
        //Detect l'envoie de sms
        if (intent.getAction().equals(SENT_SMS_ACTION_NAME)) {
            switch (getResultCode()) {
                case Activity.RESULT_OK: // Sms sent
                    //Toast.makeText(context, "SMS Sent", Toast.LENGTH_LONG).show();
                    break;
                case SmsManager.RESULT_ERROR_GENERIC_FAILURE: // generic failure
                    //Toast.makeText(context, "SMS not sent", Toast.LENGTH_LONG).show();
                    break;
                case SmsManager.RESULT_ERROR_NO_SERVICE: // No service
                    //Toast.makeText(context, "SMS not sent No Service", Toast.LENGTH_LONG).show();
                    break;
                case SmsManager.RESULT_ERROR_NULL_PDU: // null pdu
                    //Toast.makeText(context, "SMS not sent", Toast.LENGTH_LONG).show();
                    break;
                case SmsManager.RESULT_ERROR_RADIO_OFF: //Radio off
                    //Toast.makeText(context, "SMS not sent No Radio", Toast.LENGTH_LONG).show();
                    break;
            }

            // Sometimes SMS is divided multiple SMSs to be able to send all content length
            // so the broadcast may receive SENT SMS multiple times.
            // Check if one part of thi SMS has already been broadcasted (Hashmap contain)
            if(!smsSended.get(message)) {
                if(getResultCode() == Activity.RESULT_OK) {
                    Toast.makeText(context, "SMS Sent", Toast.LENGTH_LONG).show();
                    // SMS has been sent, send response to Context (MakePaymentFragment in our case)
                    if (SMSUtils.notifyActivity)
                        ((MakePaymentActivity) activity).confirmCreditCardWithPIN();

                    // Tell SMSUtils that part of this message has now been broadcasted to prevent
                    // further duplicate operation
                    smsSended.put(message, true);
                } else {
                    Toast.makeText(context, "SMS not sent", Toast.LENGTH_LONG).show();
                    if (SMSUtils.notifyActivity)
                        ((MakePaymentActivity) activity).resumePayment();
                }
            }
        }
        //detect la livraison d'un sms
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

        //detect la reception d'un sms
        else if (intent.getAction().equals(RECEIVED_SMS_ACTION_NAME)) {
            switch (getResultCode()) {
                case Activity.RESULT_OK:
                    Toast.makeText(context, "SMS received", Toast.LENGTH_LONG).show();
                    break;
                case Activity.RESULT_CANCELED:
                    Toast.makeText(context, "SMS not received", Toast.LENGTH_LONG).show();
                    break;
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

    public static void sendSMS(final Context context, String phoneNumber, String message, boolean notifyActivity) {

        if (!canSendSMS(context)) {
            Toast.makeText(context, "Cannot send SMS", Toast.LENGTH_LONG).show();
            return;
        }

        SMSUtils.activity = ((Activity)context);
        SMSUtils.message = message;
        SMSUtils.notifyActivity = notifyActivity;

        PendingIntent sentPI = PendingIntent.getBroadcast(context, 0, new Intent(SENT_SMS_ACTION_NAME), 0);
        PendingIntent deliveredPI = PendingIntent.getBroadcast(context, 0, new Intent(DELIVERED_SMS_ACTION_NAME), 0);

        final SMSUtils smsUtils = new SMSUtils();
        //register for sending and delivery
        context.registerReceiver(smsUtils, new IntentFilter(SMSUtils.SENT_SMS_ACTION_NAME));
        context.registerReceiver(smsUtils, new IntentFilter(DELIVERED_SMS_ACTION_NAME));

        SmsManager sms = SmsManager.getDefault();
        ArrayList<String> parts = sms.divideMessage(message);

        ArrayList<PendingIntent> sendList = new ArrayList<>();
        sendList.add(sentPI);

        ArrayList<PendingIntent> deliverList = new ArrayList<>();
        deliverList.add(deliveredPI);

        // TODO : uncomment after testing
        smsSended.put(message, false);

        sms.sendMultipartTextMessage(phoneNumber, null, parts, sendList, deliverList);

        //if(notifyActivity)
        //    ((MakePaymentActivity)SMSUtils.activity).confirmCreditCardWithPIN();

        //we unsubscribed in 10 seconds
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                try {
                    context.unregisterReceiver(smsUtils);
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
        }, 10000);

    }
}
