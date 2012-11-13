package com.altekis.rpg.combatassistant;

import com.altekis.rpg.combatassistant.LoadingTask.LoadingTaskFinishedListener;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.widget.TextView;

public class SplashScreen extends Activity implements LoadingTaskFinishedListener {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Show the splash screen
        setContentView(R.layout.activity_splash_screen);
        // Ready status output
        TextView status = (TextView)findViewById(R.id.status);
        // Start your loading
        new LoadingTask(status, this).execute(); // 
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
