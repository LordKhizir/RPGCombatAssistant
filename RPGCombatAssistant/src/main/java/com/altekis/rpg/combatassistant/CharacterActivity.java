package com.altekis.rpg.combatassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.altekis.rpg.combatassistant.character.RPGCharacter;
import com.altekis.rpg.combatassistant.fragments.CharacterFragment;
import com.altekis.rpg.combatassistant.fragments.CharacterListFragment;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

/**
 * CharacterActivity manage the character list and character details
 */
public class CharacterActivity extends BaseActivity implements CharacterListFragment.CallBack, CharacterFragment.CallBack {

    private static final int REQUEST_EDIT_CHARACTER = 2;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            loadCharacterListFragment(false);
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

    /**
     * Add the CharacterListFragment
     * @param replace force replace. If false we check if it's already added
     */
    private void loadCharacterListFragment(boolean replace) {
        FragmentManager fm = getSupportFragmentManager();
        if (replace || fm.findFragmentById(R.id.main_content) == null) {
            int filter = CharacterListFragment.FILTER_PC;
            if (getIntent() != null) {
                filter = getIntent().getIntExtra(CharacterListFragment.FILTER_ARG, filter);
            }
            fm.beginTransaction().replace(R.id.main_content, CharacterListFragment.newInstance(filter)).commit();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_EDIT_CHARACTER) {
            if (resultCode == RESULT_OK) {
                long idCharacter = data.getLongExtra(CharacterEditActivity.ARG_CHARACTER_ID, 0);
                FragmentManager fm = getSupportFragmentManager();
                Fragment frg = fm.findFragmentById(R.id.main_content);
                if (frg instanceof CharacterFragment) {
                    ((CharacterFragment) frg).loadData(idCharacter);
                } else if (frg instanceof CharacterListFragment) {
                    ((CharacterListFragment) frg).loadCharacters();
                }
            }
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frg = fm.findFragmentById(R.id.main_content);
        if (frg instanceof CharacterFragment) {
            // We want the list
            loadCharacterListFragment(true);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public void addCharacter() {
        /*
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
        */
        editCharacter(0);
    }

    @Override
    public void characterClick(long id) {
        FragmentManager fm = getSupportFragmentManager();
        CharacterFragment frg = CharacterFragment.newInstance(id);
        fm.beginTransaction().replace(R.id.main_content, frg).commit();
    }

    @Override
    public void characterAttackClick(long characterId, long characterAttackId) {
        Intent intent = new Intent(this, AttackActivity.class);
        intent.putExtra(AttackActivity.ARG_ID_ATTACKER, characterId);
        intent.putExtra(AttackActivity.ARG_ID_ATTACKER_ATTACK, characterAttackId);
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
        Fragment frg = fm.findFragmentById(R.id.main_content);
        if (frg instanceof CharacterListFragment) {
            // Called from list, simple refresh
            ((CharacterListFragment) frg).loadCharacters();
        } else if (frg instanceof CharacterFragment) {
            // DeleteCharacter is called from Character
            loadCharacterListFragment(true);
        }
    }
}