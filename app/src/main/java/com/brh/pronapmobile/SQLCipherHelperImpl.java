package com.brh.pronapmobile;

import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.sqlcipher.SQLCipherOpenHelper;
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperListener;

/**
 * Created by Keitel on 4/14/18.
 */

public class SQLCipherHelperImpl extends SQLCipherOpenHelper {

    public SQLCipherHelperImpl(DatabaseDefinition databaseDefinition, DatabaseHelperListener listener) {
        super(databaseDefinition, listener);
    }

    @Override
    protected String getCipherSecret() {
        // This is the Password of the Database, we will retrieve online via special secure request
        // TODO : Replace this password with one from online request with OAuth2.0
        return "HD&#S#JD83LskdiWF01S38Ske93WW@hd%alde#KD79";
    }
}
