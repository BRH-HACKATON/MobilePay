package com.brh.pronapmobile.models;

import android.icu.text.SimpleDateFormat;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.ForeignKey;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by Keitel on 4/11/18.
 */
@Table(database = PronapDatabase.class)
public class Payment implements Serializable {

    @Column
    @PrimaryKey (autoincrement=true)
    private int id;

    @Column
    private Date date;

    @Column
    private Double amount;

    @Column
    @ForeignKey(saveForeignKeyModel = false)
    private Vendor vendor;

    public Payment(Date date, Double amount) {
        this.date = date;
        this.amount = amount;
    }

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

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

}
