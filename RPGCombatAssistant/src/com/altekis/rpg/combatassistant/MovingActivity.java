package com.altekis.rpg.combatassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.altekis.rpg.combatassistant.fragments.MovingFragment;
import com.altekis.rpg.combatassistant.fragments.MovingFumbleFragment;
import com.altekis.rpg.combatassistant.maneuver.DifficultyType;

public class MovingActivity extends BaseActivity implements MovingFragment.CallBack, MovingFumbleFragment.CallBack {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        FragmentManager fm = getSupportFragmentManager();
        if (fm.findFragmentById(R.id.main_content) == null) {
            fm.beginTransaction().replace(R.id.main_content, new MovingFragment()).commit();
        }
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