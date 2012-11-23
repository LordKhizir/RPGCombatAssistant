package com.altekis.rpg.combatassistant;

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
/**
 * Main application activity
 * Shows list of current PC+NPC, from where the user can decide the next step
 *
 */
public class CharacterListActivity extends Activity {
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
    	// Jump start to the edition of a new Character
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
			int selectedCharacterId = (int)characterAdapter.getChildId(groupPosition, childPosition); 
			Intent intent = new Intent(getApplicationContext(), CharacterActivity.class);
	    	intent.putExtra("CharacterId", selectedCharacterId);
	        startActivity(intent);			
			return true;
		}
	};
	
	private void populatePlayerList() {
		// Feed lists of games to the adapter 
        characterAdapter = new CharacterArrayAdapter(this, new LAOCharacter().getCharacters(), groupHeaders);

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
