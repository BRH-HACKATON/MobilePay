package com.brh.pronapmobile.models;

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

/**
 * Created by Keitel on 4/11/18.
 */
@Table(database = PronapDatabase.class)
public class Card extends BaseModel implements Serializable {

    @Column
    @PrimaryKey (autoincrement=true)
    private int id;

    @Column
    private String number;

    @Column
    private String holder;

    @Column
    private String expiry;

    @Column
    private String cvv;

    public Card() {

    }

    public Card(String number, String holder, String expiry, String cvv) {
        this.number = number;
        this.holder = holder;
        this.expiry = expiry;
        this.cvv = cvv;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getHolder() {
        return holder;
    }

    public void setHolder(String holder) {
        this.holder = holder;
    }

    public String getExpiry() {
        return expiry;
    }

    public void setExpiry(String expiry) {
        this.expiry = expiry;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }

    public boolean isExipred() {
        return false;
    }


    public String getMaskedNumber() {
        String maskedText = getNumber().substring(0, 4);
        maskedText += "XXXXXXXXX";
        maskedText += getNumber().substring(Math.max(getNumber().length() - 3, 0), getNumber().length());
        return maskedText;
    }
    public static ArrayList<Card> all() {
        long count = SQLite.select(Method.count()).from(Card.class).count();
        Log.d("CardModel", String.valueOf(count));
        return (ArrayList<Card>) SQLite.select().from(Card.class).queryList();
    }

}
