package com.brh.pronapmobile.models;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Keitel on 4/11/18.
 */

public class Vendor implements Serializable {

    private String name;
    private String code;
    private String accountNumber;
    private String bankRouting;
    private String phone;

    private User user;
    private ArrayList<Payment> payments;

    public Vendor(String name, String code, String accountNumber, String bankRouting, String phone) {
        this.name = name;
        this.code = code;
        this.accountNumber = accountNumber;
        this.bankRouting = bankRouting;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getBankRouting() {
        return bankRouting;
    }

    public void setBankRouting(String bankRouting) {
        this.bankRouting = bankRouting;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public ArrayList<Payment> getPayments() {
        return payments;
    }



    public void addPayment(Payment payment) {
        payments.add(payment);
    }
}
