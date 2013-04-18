package com.altekis.rpg.combatassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.actionbarsherlock.view.MenuItem;
import com.altekis.rpg.combatassistant.fragments.MovingFragment;
import com.altekis.rpg.combatassistant.fragments.MovingFumbleFragment;
import com.altekis.rpg.combatassistant.maneuver.DifficultyType;

public class MovingActivity extends BaseActivity implements MovingFragment.CallBack, MovingFumbleFragment.CallBack {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(R.id.main_content) == null) {
            fm.beginTransaction().replace(R.id.main_content, new MovingFragment()).commit();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void cancelMovingManeuver() {
        finish();
    }

    @Override
    public void rollFumble(DifficultyType difficultyType) {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frg = MovingFumbleFragment.newInstance(difficultyType);
        fm.beginTransaction().replace(R.id.main_content, frg).addToBackStack("fumble").commit();
    }

    @Override
    public void cancelMovingFumble() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }
}