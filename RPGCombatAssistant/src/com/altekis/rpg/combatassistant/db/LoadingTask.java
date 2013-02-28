package com.altekis.rpg.combatassistant.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.attack.AttackTable;
import com.altekis.rpg.combatassistant.critical.Critical;
import com.altekis.rpg.combatassistant.critical.CriticalTable;
import com.altekis.rpg.combatassistant.db.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class LoadingTask extends AsyncTask<SQLiteDatabase, String, Boolean> {

    private static final String ATTACKS_PATH = "tables/attacks";
    private static final String CRITICALS_PATH = "tables/criticals";

    public interface LoadingTaskFinishedListener {
		void onTaskFinished(boolean success); // If you want to pass something back to the listener add a param to this method
	}

	// TextView to use as status output
	private final TextView status;
	// Progress bar
	private final ProgressBar progressBar;
	// This is the listener that will be told when this task is finished
	private final LoadingTaskFinishedListener finishedListener;
	// Context for database opening
	private final Context context;

	public LoadingTask(TextView textView, ProgressBar progressBar, LoadingTaskFinishedListener listener, Context context) {
		this.status = textView;
		this.progressBar = progressBar;
		this.finishedListener = listener;
		this.context = context;
	}

	@Override
	protected Boolean doInBackground(SQLiteDatabase... params) {
        boolean result = false;
        // Total number of lines
        progressBar.setMax(345);
        AssetManager assets = context.getResources().getAssets();
        final SQLiteDatabase db = params[0];
        db.beginTransaction();
        try {
            String line;
            String[] lineArray;
            ContentValues values = new ContentValues();
            long id;
            // Critical types
            // Delete all elements
            db.delete(DatabaseHelper.TABLE_CRITICAL, null, null);
            InputStream is = assets.open("tables/critical_types.txt");
            BufferedReader r = new BufferedReader(new InputStreamReader(is));
            while ((line = r.readLine()) != null) {
                if (!line.startsWith("#")) {
                    lineArray = line.split(";");
                    if (lineArray.length == 2) {
                        values.put(DatabaseHelper.FIELD_ID, Long.parseLong(lineArray[0]));
                        values.put(Critical.FIELD_NAME, lineArray[1]);
                        db.insert(DatabaseHelper.TABLE_CRITICAL, null, values);
                        publishProgress("Loading criticals");
                    }
                }
            }
            r.close();
            // Critical
            // Delete all elements
            db.delete(DatabaseHelper.TABLE_CRITICAL_TABLE, null, null);
            // Now load all criticals, the files are located in 'tables/criticals'
            String[] list = assets.list(CRITICALS_PATH);
            for (int j = 0 ; j < list.length ; j++) {
                is = assets.open(CRITICALS_PATH + "/" + list[j]);
                r = new BufferedReader(new InputStreamReader(is));
                values.clear();
                id = 1;
                while ((line = r.readLine()) != null) {
                    if (!line.startsWith("#")) {
                        lineArray = line.split(";");
                        if (lineArray.length >= 3) {
                            values.put(DatabaseHelper.FIELD_ID, id++);
                            values.put(CriticalTable.FIELD_CRITICAL_ID, Long.parseLong(lineArray[0]));
                            values.put(CriticalTable.FIELD_MINIMUM, Integer.parseInt(lineArray[1]));
                            values.put(CriticalTable.FIELD_TYPE_A, lineArray[2]);
                            if (lineArray.length == 7) {
                                // Rolemaster type B, C, D or E
                                values.put(CriticalTable.FIELD_TYPE_B, lineArray[3]);
                                values.put(CriticalTable.FIELD_TYPE_C, lineArray[4]);
                                values.put(CriticalTable.FIELD_TYPE_D, lineArray[5]);
                                values.put(CriticalTable.FIELD_TYPE_E, lineArray[6]);
                            }
                            db.insert(DatabaseHelper.TABLE_CRITICAL_TABLE, null, values);
                            publishProgress("Loading critical tables");
                        }
                    }
                }
                r.close();
            }
            // Attack types
            // Delete all elements
            db.delete(DatabaseHelper.TABLE_ATTACK, null, null);
            is = assets.open("tables/attack_types.txt");
            r = new BufferedReader(new InputStreamReader(is));
            values.clear();
            while ((line = r.readLine()) != null) {
                if (!line.startsWith("#")) {
                    lineArray = line.split(";");
                    if (lineArray.length == 5) {
                        values.put(DatabaseHelper.FIELD_ID, Long.parseLong(lineArray[0]));
                        values.put(Attack.FIELD_NAME, lineArray[1]);
                        values.put(Attack.FIELD_RM, Integer.parseInt(lineArray[2]));
                        values.put(Attack.FIELD_FUMBLE, Integer.parseInt(lineArray[3]));
                        values.put(Attack.FIELD_CRITICAL_ID, Long.parseLong(lineArray[4]));
                        db.insert(DatabaseHelper.TABLE_ATTACK, null, values);
                        publishProgress("Loading attacks & weapons");
                    }
                }
            }
            r.close();
            // Attack table
            // Delete all elements
            db.delete(DatabaseHelper.TABLE_ATTACK_TABLE, null, null);
            // Now load all attacks, the files are located in 'tables/attacks'
            list = assets.list(ATTACKS_PATH);
            for (int j = 0 ; j < list.length ; j++) {
                is = assets.open(ATTACKS_PATH + "/" + list[j]);
                r = new BufferedReader(new InputStreamReader(is));
                values.clear();
                id = 1;
                while ((line = r.readLine()) != null) {
                    if (!line.startsWith("#")) {
                        lineArray = line.split(";");
                        if (lineArray.length >= 7) {
                            values.put(DatabaseHelper.FIELD_ID, id++);
                            values.put(AttackTable.FIELD_ATTACK_ID, Long.parseLong(lineArray[0]));
                            values.put(AttackTable.FIELD_MINIMUM, Integer.parseInt(lineArray[1]));
                            if (lineArray.length == 22) {
                                // Rolemaster armor 1-20
                                for (int i = 1 ; i <= 20 ; i++) {
                                    values.put(AttackTable.FIELD_ARMOR_TYPE + i, lineArray[i + 1]);
                                }
                            } else {
                                // MERP armor 1, 5, 10, 15 and 20
                                values.put(AttackTable.FIELD_ARMOR_TYPE + "1", lineArray[2]);
                                values.put(AttackTable.FIELD_ARMOR_TYPE + "5", lineArray[3]);
                                values.put(AttackTable.FIELD_ARMOR_TYPE + "10", lineArray[4]);
                                values.put(AttackTable.FIELD_ARMOR_TYPE + "15", lineArray[5]);
                                values.put(AttackTable.FIELD_ARMOR_TYPE + "20", lineArray[6]);
                            }
                            db.insert(DatabaseHelper.TABLE_ATTACK_TABLE, null, values);
                            publishProgress("Loading attacks table");
                        }
                    }
                }
                r.close();
            }
            db.setTransactionSuccessful();
            result = true;
        } catch (IOException e) {
            Log.e("RPGCombatAssistant", "Unable to read elements from \"tables\" folder", e);
        } finally {
            db.endTransaction();
        }
        return result;
	}

	@Override
	protected void onProgressUpdate(String... values ) {
		super.onProgressUpdate(values);
		status.setText(values[0]); // This is ran on the UI thread so it is ok to update our progress bar ( a UI view ) here
		progressBar.incrementProgressBy(1);
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		finishedListener.onTaskFinished(result); // Tell whoever was listening we have finished
	}
}
