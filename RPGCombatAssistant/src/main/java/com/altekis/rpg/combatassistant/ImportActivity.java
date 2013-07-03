package com.altekis.rpg.combatassistant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
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
import com.altekis.rpg.combatassistant.fragments.ImportFileFragment;
import com.altekis.rpg.combatassistant.fragments.RuleSystemListFragment;

import java.io.File;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ImportActivity extends BaseActivity implements RuleSystemListFragment.CallBack, ImportFileFragment.CallBack {

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
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onMenuItemSelected(featureId, item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMPORT) {
            if (resultCode == RESULT_OK) {
                dataChanged();
            } else {
                Crouton.makeText(this, "The file was not imported", Style.ALERT).show();
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
    public void importRuleSystem() {
        boolean filePicker = false;
        if (isExternalStorageReadable()) {
            File directory = Environment.getExternalStorageDirectory();
            if (directory != null) {
                filePicker = true;
                FragmentManager fm = getSupportFragmentManager();
                ImportFileFragment frg = ImportFileFragment.newInstance(directory.getAbsolutePath());
                fm.beginTransaction().replace(android.R.id.content, frg).addToBackStack("file_picker").commit();
            }
        }
        if (!filePicker) {
            Crouton.makeText(this, com.altekis.rpg.combatassistant.R.string.sdcard_inaccesible, Style.ALERT).show();
        }
    }

    @Override
    public void ruleSystemClick(final long id) {
        if (id != RPGPreferences.SYSTEM_MERP) {
            AlertDialog.Builder dialog = new AlertDialog.Builder(this);
            dialog.setTitle(R.string.dialog_delete_system_title);
            dialog.setMessage(R.string.dialog_delete_system_message);
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

    @Override
    public void selectedFile(File file) {
        if (file == null) {
            Crouton.makeText(this, R.string.file_inaccesible, Style.ALERT).show();
        } else {
            getSupportFragmentManager().popBackStack();
            Intent intent = new Intent(this, SplashScreenActivity.class);
            intent.putExtra(SplashScreenActivity.PATH_URI, file.toURI().toString());
            startActivityForResult(intent, REQUEST_IMPORT);
        }
    }

    @Override
    public void cancel() {
        getSupportFragmentManager().popBackStack();
    }

    /* Checks if external storage is available to at least read */
    public boolean isExternalStorageReadable() {
        String state = Environment.getExternalStorageState();
        return Environment.MEDIA_MOUNTED.equals(state) ||
                Environment.MEDIA_MOUNTED_READ_ONLY.equals(state);
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