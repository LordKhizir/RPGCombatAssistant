package com.altekis.rpg.combatassistant;

import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.altekis.rpg.combatassistant.db.DatabaseHelper;
import com.altekis.rpg.combatassistant.db.LoadingTask;
import com.altekis.rpg.combatassistant.db.LoadingTask.LoadingTaskFinishedListener;

public class SplashScreen extends BaseActivity implements LoadingTaskFinishedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show the splash screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        // Ready status output
        TextView status = (TextView)findViewById(R.id.status);
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.splash_progressBar);

        // Start your loading
        setResult(RESULT_CANCELED);
        DatabaseHelper dbHelper = getHelper();
        new LoadingTask(status, progressBar, this, this.getApplicationContext()).execute(dbHelper.getWritableDatabase());
    }

	@Override
	public void onTaskFinished(boolean success) {
		// Loading finished...
        if (success) {
            setResult(RESULT_OK);
        }
        finish();
	}
}
