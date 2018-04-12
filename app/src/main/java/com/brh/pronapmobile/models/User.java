package com.brh.pronapmobile.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Keitel on 4/11/18.
 */

public class User implements Serializable {

    private String name;
    private String email;
    private Vendor vendor;

    private ArrayList<Payment> payments;
    private ArrayList<Card> cards;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Vendor getVendor() {
        return vendor;
    }

    public void setVendor(Vendor vendor) {
        this.vendor = vendor;
    }

    public ArrayList<Payment> getPayments() {
        return payments;
    }

    public ArrayList<Card> getCards() {
        return cards;
    }



    public boolean hasVendorProfile() {
        return vendor != null;
    }

    public void addCard(Card card) {
        cards.add(card);
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
    }
}
