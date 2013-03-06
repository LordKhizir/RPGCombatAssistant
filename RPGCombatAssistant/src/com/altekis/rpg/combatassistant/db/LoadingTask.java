package com.altekis.rpg.combatassistant.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.altekis.rpg.combatassistant.R;
import com.altekis.rpg.combatassistant.RPGPreferences;
import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.attack.AttackTable;
import com.altekis.rpg.combatassistant.critical.Critical;
import com.altekis.rpg.combatassistant.critical.CriticalTable;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;

public class LoadingTask extends AsyncTask<SQLiteDatabase, Integer, Boolean> {

    private static final String ATTACKS_PATH = "tables/attacks";
    private static final String CRITICALS_PATH = "tables/criticals";
    private static final int MAX = 100;

    public interface CallBack {
        Context getContext();
        TextView getTextViewStatus();
        ProgressBar getProgressBar();
		void onTaskFinished(boolean success); // If you want to pass something back to the listener add a param to this method
	}

	// This is the listener that will be told when this task is finished
	private CallBack callBack;
    // Path of zip file
    private final String path;

	public LoadingTask(CallBack callBack, String path) {
		this.callBack = callBack;
        this.path = path;
	}

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        callBack.getProgressBar().setMax(MAX);
    }

    @Override
	protected Boolean doInBackground(SQLiteDatabase... params) {
        boolean result = false;
        AssetManager assets = callBack.getContext().getResources().getAssets();
        final SQLiteDatabase db = params[0];
        db.beginTransaction();
        try {
            if (path != null) {
                publishProgress(R.string.splash_progress_reading_zip, 2);
                InputStream zipFile;
                if (path.startsWith("file://")) {
                    zipFile = new FileInputStream(new File(new URI(path)));
                } else {
                    zipFile = callBack.getContext().getContentResolver().openInputStream(Uri.parse(path));
                }
                publishProgress(R.string.splash_progress_unzip, 5);
                File tempDir = unzipFile(zipFile);
                publishProgress(R.string.splash_progress_unzipped, 10);
                if (tempDir != null) {
                    // System
                    File tmpFile = new File(tempDir, "system");
                    FileInputStream is = new FileInputStream(tmpFile);
                    long idSystem = importSystem(db, is, false);
                    tmpFile.delete();

                    if (idSystem > 0 ) {
                        // Critical
                        tmpFile = new File(tempDir, "critical_types");
                        is = new FileInputStream(tmpFile);
                        File dir = new File(tempDir, "criticals");
                        Map<String, Long> criticalIds = importCritical(db, idSystem, is, dir.list(), null, dir);
                        tmpFile.delete();
                        dir.delete();

                        // Attack types
                        tmpFile = new File(tempDir, "attack_types");
                        is = new FileInputStream(tmpFile);
                        dir = new File(tempDir, "attacks");
                        importAttacks(db, idSystem, criticalIds, is, dir.list(), null, dir);
                        tmpFile.delete();
                        dir.delete();
                    }
                }
            } else {
                // System
                InputStream is = assets.open("tables/system");
                importSystem(db, is, true);

                // Critical
                is = assets.open("tables/critical_types");
                String[] list = assets.list(CRITICALS_PATH);
                Map<String, Long> criticalIds = importCritical(db, RPGPreferences.SYSTEM_MERP, is, list, assets, null);

                // Attack types
                is = assets.open("tables/attack_types");
                list = assets.list(ATTACKS_PATH);
                importAttacks(db, RPGPreferences.SYSTEM_MERP, criticalIds, is, list, assets, null);
            }
            db.setTransactionSuccessful();
            result = true;
        } catch (IOException e) {
            Log.e("RPGCombatAssistant", "Unable to read files", e);
        } catch (URISyntaxException e) {
            Log.e("RPGCombatAssistant", "Unable to read files", e);
        } finally {
            db.endTransaction();
        }
        return result;
	}

    private long importSystem(SQLiteDatabase db, InputStream is, boolean assets) throws IOException {
        if (assets) {
            db.delete(DatabaseHelper.TABLE_SYSTEM, null, null);
        }
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        String line;
        String[] lineArray;
        ContentValues values = new ContentValues();
        long idSystem = 0;
        while ((line = r.readLine()) != null) {
            if (!line.startsWith("#")) {
                lineArray = line.split(";");
                if (lineArray.length >= 3) {
                    if (assets) {
                        values.put(DatabaseHelper.FIELD_ID, RPGPreferences.SYSTEM_MERP);
                    }
                    values.put(RuleSystem.FIELD_NAME, lineArray[0]);
                    values.put(RuleSystem.FIELD_ARMOR_TYPE, Integer.parseInt(lineArray[1].trim()));
                    values.put(RuleSystem.FIELD_CRITICAL_TYPE, Integer.parseInt(lineArray[2].trim()));
                    idSystem = db.insert(DatabaseHelper.TABLE_SYSTEM, null, values);
                    if (lineArray.length == 4) {
                        int numberLines = Integer.parseInt(lineArray[3].trim());
                        if (!assets) {
                            // Add the unzip progress
                            numberLines += 10;
                        }
                        callBack.getProgressBar().setMax(numberLines);
                    }
                    break;
                }
            }
        }
        r.close();
        return idSystem;
    }

    private Map<String, Long> importCritical(SQLiteDatabase db, long idSystem, InputStream criticalTypes, String[] list, AssetManager assets, File criticalDir) throws IOException {
        if (assets != null) {
            db.delete(DatabaseHelper.TABLE_CRITICAL, null, null);
        }
        BufferedReader r = new BufferedReader(new InputStreamReader(criticalTypes));
        String line;
        String[] lineArray;
        ContentValues values = new ContentValues();
        Map<String, Long> mapIds = new HashMap<String, Long>();
        while ((line = r.readLine()) != null) {
            if (!line.startsWith("#")) {
                lineArray = line.split(";");
                if (lineArray.length == 2) {
                    values.put(Critical.FIELD_NAME, lineArray[1]);
                    values.put(Critical.FIELD_SYSTEM_ID, idSystem);
                    long id = db.insert(DatabaseHelper.TABLE_CRITICAL, null, values);
                    mapIds.put(lineArray[0], id);
                    publishProgress(R.string.splash_progress_critical);
                }
            }
        }
        r.close();
        // Critical
        // Delete all elements
        if (assets != null) {
            db.delete(DatabaseHelper.TABLE_CRITICAL_TABLE, null, null);
        }
        // Now load all criticals, the files are located in 'tables/criticals'
        InputStream is;
        File tempFile;
        for (String aList1 : list) {
            if (assets == null) {
                tempFile = new File(criticalDir, aList1);
                is = new FileInputStream(tempFile);
            } else {
                tempFile = null;
                is = assets.open(CRITICALS_PATH + "/" + aList1);
            }
            r = new BufferedReader(new InputStreamReader(is));
            values.clear();
            while ((line = r.readLine()) != null) {
                if (!line.startsWith("#")) {
                    lineArray = line.split(";");
                    if (lineArray.length >= 3 && mapIds.containsKey(lineArray[0])) {
                        values.put(CriticalTable.FIELD_CRITICAL_ID, mapIds.get(lineArray[0]));
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
                        publishProgress(R.string.splash_progress_critical_table);
                    }
                }
            }
            if (assets == null && tempFile != null) {
                tempFile.delete();
            }
            r.close();
        }
        return mapIds;
    }

    private void importAttacks(SQLiteDatabase db, long idSystem, Map<String, Long> criticalIds, InputStream attackTypes, String[] list, AssetManager assets, File attacksDir) throws IOException {
        if (assets != null) {
            // Delete all elements
            db.delete(DatabaseHelper.TABLE_ATTACK, null, null);
        }
        BufferedReader r = new BufferedReader(new InputStreamReader(attackTypes));
        String line;
        String[] lineArray;
        ContentValues values = new ContentValues();
        Map<String, Long> mapIds = new HashMap<String, Long>();
        while ((line = r.readLine()) != null) {
            if (!line.startsWith("#")) {
                lineArray = line.split(";");
                if (lineArray.length >= 4 && criticalIds.containsKey(lineArray[3])) {
                    //values.put(DatabaseHelper.FIELD_ID, Long.parseLong(lineArray[0]));
                    values.put(Attack.FIELD_NAME, lineArray[1]);
                    values.put(Attack.FIELD_SYSTEM_ID, idSystem);
                    values.put(Attack.FIELD_FUMBLE, Integer.parseInt(lineArray[2]));
                    values.put(Attack.FIELD_CRITICAL_ID, criticalIds.get(lineArray[3]));
                    if (lineArray.length == 5) {
                        values.put(Attack.FIELD_ICON, lineArray[4]);
                    } else {
                        values.remove(Attack.FIELD_ICON);
                    }
                    long id = db.insert(DatabaseHelper.TABLE_ATTACK, null, values);
                    mapIds.put(lineArray[0], id);
                    publishProgress(R.string.splash_progress_attack);
                }
            }
        }
        r.close();
        // Attack table
        if (assets != null) {
            // Delete all elements
            db.delete(DatabaseHelper.TABLE_ATTACK_TABLE, null, null);
        }
        // Now load all attacks, the files are located in 'tables/attacks'
        InputStream is;
        File tempFile;
        for (String aList : list) {
            if (assets == null) {
                tempFile = new File(attacksDir, aList);
                is = new FileInputStream(tempFile);
            } else {
                tempFile = null;
                is = assets.open(ATTACKS_PATH + "/" + aList);
            }
            r = new BufferedReader(new InputStreamReader(is));
            values.clear();
            while ((line = r.readLine()) != null) {
                if (!line.startsWith("#")) {
                    lineArray = line.split(";");
                    if (lineArray.length >= 7 && mapIds.containsKey(lineArray[0])) {
                        values.put(AttackTable.FIELD_ATTACK_ID, mapIds.get(lineArray[0]));
                        values.put(AttackTable.FIELD_MINIMUM, Integer.parseInt(lineArray[1]));
                        if (lineArray.length == 22) {
                            // Rolemaster armor 1-20
                            for (int i = 1; i <= 20; i++) {
                                values.put(AttackTable.FIELD_ARMOR_TYPE + i, parseAttackCell(criticalIds, lineArray[i + 1]));
                            }
                        } else {
                            // MERP armor 1, 5, 10, 15 and 20
                            int[] types = new int[] { 1, 5, 10, 15, 20 };
                            for (int i = 0; i < types.length; i++) {
                                values.put(AttackTable.FIELD_ARMOR_TYPE + types[i], parseAttackCell(criticalIds, lineArray[i + 1]));
                            }
                        }
                        db.insert(DatabaseHelper.TABLE_ATTACK_TABLE, null, values);
                        publishProgress(R.string.splash_progress_attack_table);
                    }
                }
            }
            if (assets == null && tempFile != null) {
                tempFile.delete();
            }
            r.close();
        }
    }

    private File unzipFile(InputStream zipFileStream) {
        File cacheDir = callBack.getContext().getCacheDir();
        File tempDir = checkDir(cacheDir, "tmp");
        checkDir(tempDir, "criticals");
        checkDir(tempDir, "attacks");
        ZipEntry zipEntry;
        try {
            ZipInputStream zipInputStream = new ZipInputStream(zipFileStream);
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                Log.d("RPGCombatAssistant", "Unzipping " + zipEntry.getName());

                if (zipEntry.isDirectory()) {
                    checkDir(tempDir, zipEntry.getName());
                } else {
                    FileOutputStream fout = new FileOutputStream(new File(tempDir, zipEntry.getName()));
                    for (int c = zipInputStream.read(); c != -1; c = zipInputStream.read()) {
                        fout.write(c);
                    }
                    zipInputStream.closeEntry();
                    fout.close();
                }
            }
            zipInputStream.close();
        } catch (ZipException e) {
            Log.e("RPGCombatAssistant", "Error unzipping", e);
        } catch (IOException e) {
            Log.e("RPGCombatAssistant", "Error unzipping", e);
        } finally {
            if (zipFileStream != null) {
                try {
                    zipFileStream.close();
                } catch (IOException e) {
                    // Nothing to do
                }
            }
        }
        return tempDir;
    }

    private String parseAttackCell(Map<String, Long> criticalIds, String cell) {
        String[] cellArray = cell.split("-");
        String result;
        if (cellArray.length == 3 && criticalIds.containsKey(cellArray[2])) {
            // The third element is a critical id, we need to overwrite with the right id
            result = cellArray[0] + "-" + cellArray[1] + "-" + criticalIds.get(cellArray[2]);
        } else {
            result = cell;
        }
        return result;
    }

    private File checkDir(File tempDir, String dir) {
        File f = new File(tempDir, dir);
        if (!f.isDirectory()) {
            f.mkdirs();
        }
        return f;
    }

	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
        if (callBack.getTextViewStatus() != null) {
            // This is ran on the UI thread so it is ok to update our progress bar ( a UI view ) here
            callBack.getTextViewStatus().setText(values[0]);
            callBack.getProgressBar().incrementProgressBy(1);
        }
	}

	@Override
	protected void onPostExecute(Boolean result) {
		super.onPostExecute(result);
		callBack.onTaskFinished(result); // Tell whoever was listening we have finished
	}
}
