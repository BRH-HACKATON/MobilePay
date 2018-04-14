package com.brh.pronapmobile.models;

import android.icu.text.SimpleDateFormat;
import android.util.Log;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;
import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.structure.BaseModel;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Keitel on 4/11/18.
 */
@Table(database = PronapDatabase.class)
public class Payment extends BaseModel implements Serializable {

    @Column
    @PrimaryKey (autoincrement=true)
    private int id;

    @Column
    private Date date;

    @Column
    private Double amount;

    @Column
    private String vendorName;

    @Column
    private String vendorAccount;

    @Column
    private String cardNumber;

    @Column
    private String cardHolder;



    public Payment() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Double getAmount() {
        return amount;
    }

    public void setAmount(Double amount) {
        this.amount = amount;
    }

    public String getVendorName() {
        return vendorName;
    }

    public void setVendorName(String vendorName) {
        this.vendorName = vendorName;
    }

    public String getVendorAccount() {
        return vendorAccount;
    }

    public void setVendorAccount(String vendorAccount) {
        this.vendorAccount = vendorAccount;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }


    public String getCardHolder() {
        return cardHolder;
    }

    public void setCardHolder(String cardHolder) {
        this.cardHolder = cardHolder;
    }


    public static ArrayList<Payment> all() {
        long count = SQLite.select(Method.count()).from(Payment.class).count();
        Log.d("PaymentModel", String.valueOf(count));
        return (ArrayList<Payment>) SQLite.select().from(Payment.class).queryList();
    }
}
