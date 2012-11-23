package com.altekis.rpg.combatassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.Window;
import android.widget.TextView;

import com.altekis.rpg.combatassistant.LoadingTask.LoadingTaskFinishedListener;

public class SplashScreen extends Activity implements LoadingTaskFinishedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show the splash screen
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_splash_screen);
        // Ready status output
        TextView status = (TextView)findViewById(R.id.status);
        // Start your loading
        new LoadingTask(status, this, this.getApplicationContext()).execute(); // 
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_splah_screen, menu);
        return true;
    }

	@Override
	public void onTaskFinished() {
		// Loading finished... now start main activity
		Intent intent = new Intent(this, CharacterListActivity.class);
        startActivity(intent);
		finish(); // destroy splash screen
	}
}
