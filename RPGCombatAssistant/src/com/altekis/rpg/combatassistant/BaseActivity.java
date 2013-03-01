package com.altekis.rpg.combatassistant;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.altekis.rpg.combatassistant.db.DatabaseHelper;
import com.altekis.rpg.combatassistant.fragments.DBFragmentActivity;
import com.j256.ormlite.android.apptools.OpenHelperManager;

public class BaseActivity extends SherlockFragmentActivity implements DBFragmentActivity {

    private DatabaseHelper databaseHelper = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    @Override
    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

}
