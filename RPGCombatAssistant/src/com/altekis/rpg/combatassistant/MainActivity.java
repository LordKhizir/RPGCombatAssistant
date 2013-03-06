package com.altekis.rpg.combatassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void clickCharacters(View view) {
        startActivity(new Intent(this, CharacterActivity.class));
    }

    public void clickAttacks(View view) {
        startActivity(new Intent(this, AttackActivity.class));
    }
}