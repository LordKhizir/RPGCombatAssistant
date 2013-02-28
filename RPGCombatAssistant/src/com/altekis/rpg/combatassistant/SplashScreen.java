package com.altekis.rpg.combatassistant;

import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.altekis.rpg.combatassistant.db.LoadingTask;
import com.altekis.rpg.combatassistant.db.LoadingTask.LoadingTaskFinishedListener;
import com.altekis.rpg.combatassistant.db.DatabaseHelper;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_splah_screen, menu);
        return true;
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
