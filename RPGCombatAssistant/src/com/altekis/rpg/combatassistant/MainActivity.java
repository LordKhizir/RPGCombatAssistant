package com.altekis.rpg.combatassistant;

import android.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import com.altekis.rpg.combatassistant.character.RPGCharacter;
import com.altekis.rpg.combatassistant.db.DatabaseHelper;
import com.altekis.rpg.combatassistant.fragments.CharacterFragment;
import com.altekis.rpg.combatassistant.fragments.CharacterListFragment;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public class MainActivity extends BaseActivity implements CharacterListFragment.CallBack, CharacterFragment.CallBack {

    private static final int REQUEST_INIT_DB = 1;
    private static final int REQUEST_EDIT_CHARACTER = 2;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Check if database is initialised
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (sp.getBoolean(DatabaseHelper.DB_INITIALISED, false)) {
            if (savedInstanceState == null) {
                loadCharacterListFragment();
            }
        } else {
            // Not initialised, launch splash
            startActivityForResult(new Intent(this, SplashScreen.class), REQUEST_INIT_DB);
        }
    }

    private void loadCharacterListFragment() {
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(android.R.id.content, new CharacterListFragment()).commit();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INIT_DB) {
            if (resultCode == RESULT_OK) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                sp.edit().putBoolean(DatabaseHelper.DB_INITIALISED, true).commit();
                loadCharacterListFragment();
            } else {
                // TODO Advice user
                finish();
            }
        } else if (requestCode == REQUEST_EDIT_CHARACTER) {
            if (resultCode == RESULT_OK) {
                long idCharacter = data.getLongExtra(CharacterEditActivity.ARG_CHARACTER_ID, 0);
                FragmentManager fm = getSupportFragmentManager();
                CharacterFragment frg = (CharacterFragment) fm.findFragmentById(R.id.content);
                frg.loadData(idCharacter, true, true);
            }
        }
    }

    @Override
    public void addCharacter() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frg = fm.findFragmentById(R.id.content);
        if (frg instanceof CharacterFragment) {
            // When activity return we want the list, not another character than created
            fm.popBackStack();
        }
        // Jump start to the edition of a new RPGCharacter
        Intent intent = new Intent(this, CharacterEditActivity.class);
        // We pass no CharacterId as extra, to claim for a new character
        startActivityForResult(intent, REQUEST_EDIT_CHARACTER);
    }

    @Override
    public void characterClick(long id) {
        FragmentManager fm = getSupportFragmentManager();
        CharacterFragment frg = CharacterFragment.newInstance(id);
        fm.beginTransaction().replace(android.R.id.content, frg).addToBackStack("character").commit();
    }

    @Override
    public void characterAttackClick(long characterId, long characterAttackId) {
        Intent intent = new Intent(this, AttackActivity.class);
        intent.putExtra("AttackId", characterAttackId);
        startActivity(intent);
    }

    @Override
    public void editCharacter(long characterId) {
        // Jump start to the edition of characterId
        Intent intent = new Intent(this, CharacterEditActivity.class);
        intent.putExtra(CharacterEditActivity.ARG_CHARACTER_ID, characterId);
        startActivityForResult(intent, REQUEST_EDIT_CHARACTER);
    }

    @Override
    public void deleteCharacter(long characterId) {
        try {
            Dao<RPGCharacter, Long> dao = getHelper().getDaoRPGCharacter();
            dao.deleteById(characterId);
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Can't read database", e);
        }
        FragmentManager fm = getSupportFragmentManager();
        Fragment frg = fm.findFragmentById(R.id.content);
        if (frg instanceof CharacterListFragment) {
            // Called from list, simple refresh
            ((CharacterListFragment) frg).loadCharacters();
        } else if (frg instanceof CharacterFragment) {
            // DeleteCharacter is called from Character
            fm.popBackStack();
        }
    }

    @Override
    public void addCharacterAttack(long characterId) {

    }
}