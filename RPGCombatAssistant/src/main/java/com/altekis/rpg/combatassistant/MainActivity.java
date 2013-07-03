package com.altekis.rpg.combatassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.altekis.rpg.combatassistant.character.RPGCharacter;
import com.altekis.rpg.combatassistant.db.DatabaseHelper;
import com.altekis.rpg.combatassistant.fragments.CharacterListFragment;
import com.altekis.rpg.combatassistant.fragments.MainMenuFragment;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class MainActivity extends BaseActivity implements MainMenuFragment.CallBack {

    private static final int REQUEST_INIT_DB = 1;

    private boolean mDbInitialized;
    private boolean mEmptyPlayers;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Check if database is initialised
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mDbInitialized = sp.getBoolean(DatabaseHelper.DB_INITIALISED, false);
        if (!mDbInitialized) {
            // Not initialised, launch splash
            startActivityForResult(new Intent(this, SplashScreenActivity.class), REQUEST_INIT_DB);
        }

        try {
            Dao<RPGCharacter, Long> dao = getHelper().getDaoRPGCharacter();
            mEmptyPlayers = dao.countOf() == 0;
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Error loading characters", e);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new MainMenuFragment()).commit();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            startActivity(new Intent(this, RPGPreferences.class));
            return true;
        } else {
            return super.onMenuItemSelected(featureId, item);
        }
    }

    @Override
    public void optionMenuClicked(MainMenuFragment.MenuOption menuOption) {
        if (mDbInitialized) {
            Intent intent = null;
            if (menuOption == MainMenuFragment.MenuOption.PLAYERS_PC
                    || menuOption == MainMenuFragment.MenuOption.PLAYERS_NPC
                    || menuOption == MainMenuFragment.MenuOption.PLAYERS_ALL) {
                intent = new Intent(this, CharacterActivity.class);
                int filter;
                if (menuOption == MainMenuFragment.MenuOption.PLAYERS_PC) {
                    filter = CharacterListFragment.FILTER_PC;
                } else if (menuOption == MainMenuFragment.MenuOption.PLAYERS_NPC) {
                    filter = CharacterListFragment.FILTER_NPC;
                } else {
                    filter = CharacterListFragment.FILTER_ALL;
                }
                intent.putExtra(CharacterListFragment.FILTER_ARG, filter);
            } else if (menuOption == MainMenuFragment.MenuOption.ATTACK) {
                if (mEmptyPlayers) {
                    Crouton.makeText(this, R.string.no_characters, Style.ALERT).show();
                } else {
                    intent = new Intent(this, AttackActivity.class);
                }
            } else if (menuOption == MainMenuFragment.MenuOption.CRITICAL) {
                intent = new Intent(this, AttackActivity.class);
                intent.putExtra(AttackActivity.ARG_CRITICAL, true);
            } else if (menuOption == MainMenuFragment.MenuOption.MM) {
                intent = new Intent(this, MovingActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
            }
        }
    }

}