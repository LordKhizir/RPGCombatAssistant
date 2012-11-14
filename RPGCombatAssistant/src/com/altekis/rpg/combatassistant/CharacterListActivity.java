package com.altekis.rpg.combatassistant;

import java.util.Random;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ExpandableListView;

import com.altekis.rpg.combatassistant.character.CharacterArrayAdapter;
import com.altekis.rpg.combatassistant.character.LAOCharacter;
import com.altekis.rpg.combatassistant.character.RPGCharacter;
/**
 * Main application activity
 * Shows list of current PC+NPC, from where the user can decide the next step
 *
 */
public class CharacterListActivity extends Activity {
	ExpandableListView characterListView;
	CharacterArrayAdapter characterAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar      
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_character_list);
        characterListView = (ExpandableListView) findViewById(R.id.characterListView);
 
        // Assign listener to list
        characterListView.setOnChildClickListener(characterClickListener);
        // Add a +character button
        Button btnAddCharacter = new Button(this);
        btnAddCharacter.setText("New Character");
        btnAddCharacter.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doAddCharacter();
			}
		});
        characterListView.addFooterView(btnAddCharacter);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	// Fill characters' lists
    	// Here, on onResume instead of onCreate, to force reload each time we get back from another activity
        populatePlayerList();
    }
    
    private void doAddCharacter() {
    	// Generate a BLANK, NEW character, and jump start to its edition
    	RPGCharacter character = new RPGCharacter();
    	character.setId(new Random().nextInt()); // TODO Fix
    	character.setName("");
    	character.setPlayerName("");
    	new LAOCharacter(this).addCharacter(character);
    	
    	Intent intent = new Intent(this, CharacterEditActivity.class);
    	intent.putExtra("CharacterId", character.getId());
        startActivity(intent);
    }

	/**
	 * Listener for turn list
	 */
	ExpandableListView.OnChildClickListener characterClickListener = new ExpandableListView.OnChildClickListener() {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			int selectedCharacterId = (int)characterAdapter.getChildId(groupPosition, childPosition); 
			Intent intent = new Intent(getApplicationContext(), CharacterActivity.class);
	    	intent.putExtra("CharacterId", selectedCharacterId);
	        startActivity(intent);			
			return true;
		}
	};
	
	private void populatePlayerList() {
		// Feed lists of games to the adapter 
        characterAdapter = new CharacterArrayAdapter(this, new LAOCharacter(this).getCharacters());

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
