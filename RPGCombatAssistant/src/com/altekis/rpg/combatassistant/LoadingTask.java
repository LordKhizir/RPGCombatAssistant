package com.altekis.rpg.combatassistant;

import java.io.IOException;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.altekis.rpg.combatassistant.attack.AttackType;
import com.altekis.rpg.combatassistant.attack.LAOAttackType;
import com.altekis.rpg.combatassistant.critical.Critical;
import com.altekis.rpg.combatassistant.critical.LAOCritical;

public class LoadingTask extends AsyncTask<String, String, Integer> {

	public interface LoadingTaskFinishedListener {
		void onTaskFinished(); // If you want to pass something back to the listener add a param to this method
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
	protected Integer doInBackground(String... params) {
		if (!RPGCombatAssistant.initialized) {
			// READY DATABASE & HELPER
			LocalDatabaseHelper.initialize(context);
			// LOAD TABLES 
			// Loop through all assets, inflating them
			String[] elements;
			try {
				elements = status.getContext().getResources().getAssets().list("tables");
				progressBar.setMax(elements.length);
				for (String element:elements) {
					if (element.endsWith(".critico.xml")) {
						Critical crit = LAOCritical.loadCritical(context, element);
						// Add critical to the store
						RPGCombatAssistant.criticals.put(crit.getKey(), crit);
						// Info for the user
						publishProgress("Critical \"" + crit.getName() + "\"");
					} else if (element.endsWith(".tipo_ataque.xml")) {
						AttackType attackType = LAOAttackType.loadAttackType(context, element);
						// Add attack type to the store
						RPGCombatAssistant.attackTypes.put(attackType.getKey(), attackType);
						// Info for the user
						publishProgress("Attack \"" + attackType.getName() + "\"");
					} 
					else {
						publishProgress("Ignored \"" + element + "\"");
						Log.w("RPGCombatAssistant", "Resource " + element + " not matched by any loader. IGNORED!");
					}
				}
			} catch (IOException e) {
				Log.e("RPGCombatAssistant", "Unable to read elements from \"tables\" folder");
			}
			RPGCombatAssistant.initialized = true;
		}
		return 1;
	}

	@Override
	protected void onProgressUpdate(String... values ) {
		super.onProgressUpdate(values);
		status.setText(values[0]); // This is ran on the UI thread so it is ok to update our progress bar ( a UI view ) here
		progressBar.incrementProgressBy(1);
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		finishedListener.onTaskFinished(); // Tell whoever was listening we have finished
	}
}
