package com.altekis.rpg.combatassistant;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.attack.AttackTable;
import com.altekis.rpg.combatassistant.character.RPGCharacterAttack;
import com.altekis.rpg.combatassistant.critical.Critical;
import com.altekis.rpg.combatassistant.critical.CriticalTable;
import com.altekis.rpg.combatassistant.db.DatabaseHelper;
import com.altekis.rpg.combatassistant.fragments.RuleSystemListFragment;

public class ImportActivity extends BaseActivity implements RuleSystemListFragment.CallBack {

    private static final int REQUEST_FILE = 1;
    private static final int REQUEST_IMPORT = 2;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FragmentManager fm = getSupportFragmentManager();
        Fragment frg = fm.findFragmentById(android.R.id.content);
        if (frg == null) {
            frg = new RuleSystemListFragment();
            fm.beginTransaction().add(android.R.id.content, frg).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_import, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        } else if (item.getItemId() == R.id.menu_import) {
            // Use the GET_CONTENT intent from the utility class
            Intent target = createGetContentIntent();
            // Create the chooser Intent
            Intent intent = Intent.createChooser(target, "Elija un fichero");
            try {
                startActivityForResult(intent, REQUEST_FILE);
            } catch (ActivityNotFoundException e) {
                // The reason for the existence of aFileChooser
            }
        }
        return true;
    }

    public static Intent createGetContentIntent() {
        // Implicitly allow the user to select a particular kind of data
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // The MIME data type filter
        intent.setType("application/zip");
        // Only return URIs that can be opened with ContentResolver
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        return intent;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_FILE && resultCode == RESULT_OK) {
            String path = data.getDataString();
            Intent intent = new Intent(this, SplashScreenActivity.class);
            intent.putExtra(SplashScreenActivity.PATH_URI, path);
            startActivityForResult(intent, REQUEST_IMPORT);
        } else if (requestCode == REQUEST_IMPORT) {
            if (resultCode == RESULT_OK) {
                dataChanged();
            }
        }
    }

    private void dataChanged() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frg = fm.findFragmentById(android.R.id.content);
        if (frg != null) {
            ((RuleSystemListFragment) frg).loadData();
        }
        setResult(RESULT_OK);
    }

    @Override
    public void ruleSystemClick(final long id) {
        if (id != RPGPreferences.SYSTEM_MERP) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle("¿Desea eliminar?");
            dialog.setMessage("Pulse aceptar para eliminar el sistema de reglas. Se eliminarán también los ataques de los jugadores");
            dialog.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    new DeleteTask(getHelper().getWritableDatabase()).execute(id);
                }
            });
            dialog.setNegativeButton(android.R.string.cancel, null);
            dialog.show();
        }
    }

    class DeleteTask extends AsyncTask<Long, Void, Boolean> {

        private SQLiteDatabase sqLiteDatabase;

        DeleteTask(SQLiteDatabase sqLiteDatabase) {
            this.sqLiteDatabase = sqLiteDatabase;
        }

        @Override
        protected Boolean doInBackground(Long... params) {
            boolean result = false;
            sqLiteDatabase.beginTransaction();
            try {
                final String[] args = new String[] { params[0].toString() };
                // Remove character attacks
                String where = RPGCharacterAttack.FIELD_ATTACK_ID
                        + " IN (SELECT " + DatabaseHelper.FIELD_ID  + " FROM " + DatabaseHelper.TABLE_ATTACK
                        + " WHERE " + Attack.FIELD_SYSTEM_ID  + " = ?)";
                sqLiteDatabase.delete(DatabaseHelper.TABLE_CHARACTER_ATTACKS, where, args);

                // Remove attacks table
                where = AttackTable.FIELD_ATTACK_ID
                        + " IN (SELECT " + DatabaseHelper.FIELD_ID  + " FROM " + DatabaseHelper.TABLE_ATTACK
                        + " WHERE " + Attack.FIELD_SYSTEM_ID  + " = ?)";
                sqLiteDatabase.delete(DatabaseHelper.TABLE_ATTACK_TABLE, where, args);

                // Remove attacks
                where = Attack.FIELD_SYSTEM_ID  + " = ?";
                sqLiteDatabase.delete(DatabaseHelper.TABLE_ATTACK, where, args);

                // Remove criticals table
                where = CriticalTable.FIELD_CRITICAL_ID
                        + " IN (SELECT " + DatabaseHelper.FIELD_ID  + " FROM " + DatabaseHelper.TABLE_CRITICAL
                        + " WHERE " + Critical.FIELD_SYSTEM_ID  + " = ?)";
                sqLiteDatabase.delete(DatabaseHelper.TABLE_CRITICAL_TABLE, where, args);

                // Remove criticals
                where = Critical.FIELD_SYSTEM_ID  + " = ?";
                sqLiteDatabase.delete(DatabaseHelper.TABLE_CRITICAL, where, args);

                // Remove system
                where = DatabaseHelper.FIELD_ID + " = ?";
                sqLiteDatabase.delete(DatabaseHelper.TABLE_SYSTEM, where, args);

                sqLiteDatabase.setTransactionSuccessful();
                result = true;
            } catch (Exception e) {
                Log.e("RPGCombatAssistant", "Error removing data", e);
            } finally {
                sqLiteDatabase.endTransaction();
            }

            return result;
        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                dataChanged();
            }
        }
    }

}