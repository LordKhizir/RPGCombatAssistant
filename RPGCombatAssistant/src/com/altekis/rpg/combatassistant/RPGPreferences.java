package com.altekis.rpg.combatassistant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import com.actionbarsherlock.app.SherlockPreferenceActivity;
import com.altekis.rpg.combatassistant.db.DatabaseHelper;
import com.altekis.rpg.combatassistant.db.RuleSystem;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public class RPGPreferences extends SherlockPreferenceActivity implements Preference.OnPreferenceChangeListener {

    public static final int REQUEST_EDIT_SYSTEMS = 1;
    public static final long SYSTEM_MERP = 1;

    private static RuleSystem ruleSystem;

    private DatabaseHelper databaseHelper = null;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (databaseHelper != null) {
            OpenHelperManager.releaseHelper();
            databaseHelper = null;
        }
    }

    public DatabaseHelper getHelper() {
        if (databaseHelper == null) {
            databaseHelper = OpenHelperManager.getHelper(this, DatabaseHelper.class);
        }
        return databaseHelper;
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        ListPreference listPreference = (ListPreference) findPreference("ruleSystem");
        reloadSystems(listPreference);
        listPreference.setOnPreferenceChangeListener(this);

        Preference pref = findPreference("manageRuleSystem");
        pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                startActivityForResult(new Intent(RPGPreferences.this, ImportActivity.class), REQUEST_EDIT_SYSTEMS);
                return true;
            }
        });
    }

    private void reloadSystems(ListPreference listPreference) {
        try {
            Dao<RuleSystem, Long> daoSystem = getHelper().getDaoSystem();
            List<RuleSystem> lst = daoSystem.query(daoSystem.queryBuilder().orderBy(RuleSystem.FIELD_NAME, true).prepare());
            final String[] entries = new String[lst.size()];
            final String[] entriesValues = new String[entries.length];
            int pos = 0;
            boolean found = false;
            for (RuleSystem rs : lst) {
                entries[pos] = rs.getName();
                entriesValues[pos] = Long.toString(rs.getId());
                if (entriesValues[pos].equals(listPreference.getValue())) {
                    found = true;
                }
                pos++;
            }
            listPreference.setEntries(entries);
            listPreference.setEntryValues(entriesValues);
            if (!found) {
                listPreference.setValue(Long.toString(SYSTEM_MERP));
            }
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Error reading database", e);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_SYSTEMS) {
            if (resultCode == RESULT_OK) {
                reloadSystems((ListPreference) findPreference("ruleSystem"));
            }
        }
    }

    public static RuleSystem getSystem(Context ctx, DatabaseHelper dbHelper) {
        if (ruleSystem == null) {
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(ctx);
            try {
                Dao<RuleSystem, Long> dao = dbHelper.getDaoSystem();
                ruleSystem = dao.queryForId(Long.parseLong(sp.getString("ruleSystem", Long.toString(SYSTEM_MERP))));
                if (ruleSystem == null) {
                    ruleSystem = dao.queryForId(SYSTEM_MERP);
                }
            } catch (SQLException e) {
                Log.e("RPGCombatAssistan", "Error reading database", e);
            }
        }
        return ruleSystem;
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        ruleSystem = null;
        return true;
    }
}