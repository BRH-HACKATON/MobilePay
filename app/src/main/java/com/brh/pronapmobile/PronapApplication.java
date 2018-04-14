package com.brh.pronapmobile;

import android.app.Application;

import com.brh.pronapmobile.models.PronapDatabase;
import com.raizlabs.android.dbflow.config.DatabaseConfig;
import com.raizlabs.android.dbflow.config.DatabaseDefinition;
import com.raizlabs.android.dbflow.config.FlowConfig;
import com.raizlabs.android.dbflow.config.FlowManager;
import com.raizlabs.android.dbflow.structure.database.DatabaseHelperListener;
import com.raizlabs.android.dbflow.structure.database.OpenHelper;

/**
 * Created by Keitel on 4/12/18.
 */

public class PronapApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        // This instantiates DBFlow (With Database encryption config customize by Keitel)
        FlowManager.init(new FlowConfig.Builder(this)
                .addDatabaseConfig(
                        new DatabaseConfig.Builder(PronapDatabase.class)
                                .openHelper(new DatabaseConfig.OpenHelperCreator() {
                                    @Override
                                    public OpenHelper createHelper(DatabaseDefinition databaseDefinition, DatabaseHelperListener helperListener) {
                                        return new SQLCipherHelperImpl(databaseDefinition, helperListener);
                                    }
                                })
                                .build())
                .build());

        // add for verbose logging
        // FlowLog.setMinimumLoggingLevel(FlowLog.Level.V);
    }

}
