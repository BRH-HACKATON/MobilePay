package com.brh.pronapmobile.models;

import android.util.Log;

import com.raizlabs.android.dbflow.sql.language.Method;
import com.raizlabs.android.dbflow.sql.language.SQLite;
import com.raizlabs.android.dbflow.sql.language.Select;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Keitel on 4/11/18.
 */

public class User {

    public static boolean hasCard() {
        return SQLite.select(Method.count()).from(Card.class).hasData();
    }

    public static boolean hasVendorProfile() {
        return SQLite.select(Method.count()).from(Vendor.class).hasData();
    }
}