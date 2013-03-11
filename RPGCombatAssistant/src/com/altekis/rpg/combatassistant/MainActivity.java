package com.altekis.rpg.combatassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import com.actionbarsherlock.app.SherlockActivity;
import com.altekis.rpg.combatassistant.db.DatabaseHelper;

public class MainActivity extends SherlockActivity {

    private static final int REQUEST_INIT_DB = 1;

    private boolean mDbInitialized;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Check if database is initialised
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mDbInitialized = sp.getBoolean(DatabaseHelper.DB_INITIALISED, false);
        if (!mDbInitialized) {
            // Not initialised, launch splash
            startActivityForResult(new Intent(this, SplashScreenActivity.class), REQUEST_INIT_DB);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INIT_DB) {
            if (resultCode == RESULT_OK) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                sp.edit().putBoolean(DatabaseHelper.DB_INITIALISED, true).commit();
                mDbInitialized = true;
            } else {
                // TODO Advice user
                finish();
            }
        }
    }

    public void clickButton(View view) {
        if (mDbInitialized) {
            if (view.getId() == R.id.main_characters) {
                startActivity(new Intent(this, CharacterActivity.class));
            } else if (view.getId() == R.id.main_attacks) {
                startActivity(new Intent(this, AttackActivity.class));
            } else if (view.getId() == R.id.main_moving) {
                startActivity(new Intent(this, MovingActivity.class));
            }
        }
    }
}