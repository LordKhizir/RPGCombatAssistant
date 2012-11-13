package com.altekis.rpg.combatassistant;

import java.io.IOException;

import com.altekis.rpg.combatassistant.critical.Critical;
import com.altekis.rpg.combatassistant.critical.LAOCritical;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

public class LoadingTask extends AsyncTask<String, String, Integer> {

	public interface LoadingTaskFinishedListener {
		void onTaskFinished(); // If you want to pass something back to the listener add a param to this method
	}

	// TextView to use as status output
	private final TextView status;
	// This is the listener that will be told when this task is finished
	private final LoadingTaskFinishedListener finishedListener;

	public LoadingTask(TextView textView, LoadingTaskFinishedListener listener) {
		this.status = textView;
		this.finishedListener = listener;
	}

	@Override
	protected Integer doInBackground(String... params) {
		if (!RPGCombatAssistant.initialized) {
			Context context = status.getContext(); // We need a context... use that of the status text
			// LOAD TABLES 
			// Loop through all assets, inflating them
			String[] elements;
			try {
				elements = status.getContext().getResources().getAssets().list("tables");
				for (String element:elements) {
					if (element.endsWith(".critico.xml")) {
						Critical crit = LAOCritical.loadCritical(context, element);
						// Add critical to the store
						RPGCombatAssistant.criticals.put(crit.getKey(), crit);
						// Info for the user
						publishProgress("Loaded critical \"" + crit.getName() + "\"");
					} else {
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
	protected void onProgressUpdate(String... values) {
		super.onProgressUpdate(values);
		status.setText(values[0]); // This is ran on the UI thread so it is ok to update our progress bar ( a UI view ) here
	}

	@Override
	protected void onPostExecute(Integer result) {
		super.onPostExecute(result);
		finishedListener.onTaskFinished(); // Tell whoever was listening we have finished
	}
}
