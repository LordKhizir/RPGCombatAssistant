package com.altekis.rpg.combatassistant;

import android.content.Context;
import android.os.Bundle;
import android.view.Window;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.altekis.rpg.combatassistant.db.LoadingTask;

public class SplashScreenActivity extends BaseActivity implements LoadingTask.CallBack {

    public static final String PATH_URI = "pathUri";

    private TextView textView;
    private ProgressBar progressBar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show the splash screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        // Ready status output
        textView = (TextView) findViewById(R.id.status);
        progressBar = (ProgressBar) findViewById(R.id.splash_progressBar);

        // Start your loading
        setResult(RESULT_CANCELED);
        // Check if there is data
        String path = null;
        if (getIntent() != null && getIntent().hasExtra(PATH_URI)) {
            path = getIntent().getStringExtra(PATH_URI);
        }
        new LoadingTask(this, path).execute(getHelper().getWritableDatabase());
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public TextView getTextViewStatus() {
        return textView;
    }

    @Override
    public ProgressBar getProgressBar() {
        return progressBar;
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
