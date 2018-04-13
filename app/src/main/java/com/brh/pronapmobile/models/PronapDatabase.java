package com.brh.pronapmobile.models;

import com.raizlabs.android.dbflow.annotation.Database;

/**
 * Created by Keitel on 4/12/18.
 */

@Database(name = PronapDatabase.NAME, version = PronapDatabase.VERSION)
public class PronapDatabase {
    public static final String NAME = "PronapDataBase";

    public static final int VERSION = 1;
}
