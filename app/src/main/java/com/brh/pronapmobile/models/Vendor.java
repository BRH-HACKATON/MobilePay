package com.brh.pronapmobile.models;

import com.raizlabs.android.dbflow.annotation.Column;
import com.raizlabs.android.dbflow.annotation.PrimaryKey;
import com.raizlabs.android.dbflow.annotation.Table;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Created by Keitel on 4/11/18.
 */
@Table(database = PronapDatabase.class)
public class Vendor implements Serializable {

    @Column
    @PrimaryKey
    private int id;

    @Column
    private String name;

    @Column
    private String code;

    @Column
    private String account;

    @Column
    private String routing;

    @Column
    private String phone;

    private ArrayList<Payment> payments;

    public Vendor() {

    }

    public Vendor(String name, String code, String account, String routing, String phone) {
        this.name = name;
        this.code = code;
        this.account = account;
        this.routing = routing;
        this.phone = phone;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getRouting() {
        return routing;
    }

    public void setRouting(String routing) {
        this.routing = routing;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public ArrayList<Payment> getPayments() {
        return payments;
    }

    public void setPayments(ArrayList<Payment> payments) {
        this.payments = payments;
    }


    public void addPayment(Payment payment) {
        payments.add(payment);
    }
}
