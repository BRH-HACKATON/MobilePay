package com.brh.pronapmobile.models;

import android.icu.text.SimpleDateFormat;

import java.io.Serializable;

/**
 * Created by Keitel on 4/11/18.
 */

public class Payment implements Serializable {

    private int id;
    private SimpleDateFormat date;

    private Vendor vendor;
    private User buyer;
    private Card card;

    public Payment(SimpleDateFormat date, Vendor vendor, User buyer, Card card) {
        this.date = date;
        this.vendor = vendor;
        this.buyer = buyer;
        this.card = card;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public SimpleDateFormat getDate() {
        return date;
    }

    public void setDate(SimpleDateFormat date) {
        this.date = date;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public User getBuyer() {
        return buyer;
    }

    public void setBuyer(User buyer) {
        this.buyer = buyer;
    }

    public Card getCard() {
        return card;
    }

    public void setCard(Card card) {
        this.card = card;
    }
}
