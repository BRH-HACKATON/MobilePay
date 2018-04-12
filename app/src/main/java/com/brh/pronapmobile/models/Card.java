package com.brh.pronapmobile.models;

import java.io.Serializable;

/**
 * Created by Keitel on 4/11/18.
 */

public class Card implements Serializable {

    private String number;
    private String holderName;
    private String expiryDate;
    private String cvv;

    private User user;

    public Card(String number, String holderName, String expiryDate, String cvv) {
        this.number = number;
        this.holderName = holderName;
        this.expiryDate = expiryDate;
        this.cvv = cvv;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getHolderName() {
        return holderName;
    }

    public void setHolderName(String holderName) {
        this.holderName = holderName;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public boolean isExipred() {
        return false;
    }
}
