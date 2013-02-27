package com.altekis.rpg.combatassistant;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.altekis.rpg.combatassistant.character.CharacterArrayAdapter;
import com.altekis.rpg.combatassistant.db.*;
import com.altekis.rpg.combatassistant.character.RPGCharacter;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Main application activity
 * Shows list of current PC+NPC, from where the user can decide the next step
 *
 */
public class CharacterListActivity extends BaseActivity {

    private static final int REQUEST_INIT_DB = 1;

	ExpandableListView characterListView;
	CharacterArrayAdapter characterAdapter;
	String[] groupHeaders;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

        setContentView(R.layout.activity_character_list);
        characterListView = (ExpandableListView) findViewById(R.id.characterListView);

        // Prepare localized strings
        groupHeaders = new String[]{
                getResources().getString(R.string.characterList_playerCharactersGroupHeader),
                getResources().getString(R.string.characterList_nonPlayerCharactersGroupHeader)};

        // Assign listener to list
        characterListView.setOnChildClickListener(characterClickListener);
        // Add a +character button
        Button btnAddCharacter = new Button(this);
        btnAddCharacter.setText("New RPGCharacter");
        btnAddCharacter.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                doAddCharacter();
            }
        });
        characterListView.addFooterView(btnAddCharacter);

        // Check if database is initialised
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        if (!sp.getBoolean(DatabaseHelper.DB_INITIALISED, false)) {
            // Not initialised, launch splash
            startActivityForResult(new Intent(this, SplashScreen.class), REQUEST_INIT_DB);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INIT_DB) {
            if (resultCode == RESULT_OK) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                sp.edit().putBoolean(DatabaseHelper.DB_INITIALISED, true).commit();
            } else {
                // TODO Advice user
                finish();
            }
        }
    }

    @Override
    public void onResume() {
    	super.onResume();
    	// Fill characters' lists
    	// Here, on onResume instead of onCreate, to force reload each time we get back from another activity
        populatePlayerList();
    }
    
    private void doAddCharacter() {
    	// Jump start to the edition of a new RPGCharacter
    	Intent intent = new Intent(this, CharacterEditActivity.class);
    	// We pass no CharacterId as extra, to claim for a new character 
        startActivity(intent);
    }

	/**
	 * Listener for turn list
	 */
	ExpandableListView.OnChildClickListener characterClickListener = new ExpandableListView.OnChildClickListener() {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			long selectedCharacterId = characterAdapter.getChildId(groupPosition, childPosition);
			Intent intent = new Intent(getApplicationContext(), CharacterActivity.class);
	    	intent.putExtra(CharacterActivity.ARG_CHARACTER_ID, selectedCharacterId);
	        startActivity(intent);			
			return true;
		}
	};
	
	private void populatePlayerList() {
		// Feed lists of games to the adapter
        List<RPGCharacter> characters;
        List<RPGCharacter> pnjs;
        try {
            Dao<RPGCharacter, Long> dao = getHelper().getDaoRPGCharacter();
            QueryBuilder<RPGCharacter, Long> qb = dao.queryBuilder();
            qb.setWhere(qb.where().eq(RPGCharacter.FIELD_PNJ, false));
            qb.orderBy(RPGCharacter.FIELD_NAME, true);
            characters = dao.query(qb.prepare());
            qb.clear();
            qb.setWhere(qb.where().eq(RPGCharacter.FIELD_PNJ, true));
            qb.orderBy(RPGCharacter.FIELD_NAME, true);
            pnjs = dao.query(qb.prepare());
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Error loading characters", e);
            characters = new ArrayList<RPGCharacter>();
            pnjs = new ArrayList<RPGCharacter>();
        }

        characterAdapter = new CharacterArrayAdapter(this, characters, pnjs, groupHeaders);

	    // Assign adapter to populate list
        characterListView.setAdapter(characterAdapter);
        characterListView.expandGroup(0);
        characterListView.expandGroup(1);
	}

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }    
}
