package com.altekis.rpg.combatassistant;

import android.app.Activity;
import com.altekis.rpg.combatassistant.db.DatabaseHelper;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class BaseActivity extends Activity {

    private DatabaseHelper databaseHelper = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    protected DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

}
