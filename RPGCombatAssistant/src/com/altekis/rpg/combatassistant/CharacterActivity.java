package com.altekis.rpg.combatassistant;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.altekis.rpg.combatassistant.character.CharacterAttack;
import com.altekis.rpg.combatassistant.character.CharacterAttacksArrayAdapter;
import com.altekis.rpg.combatassistant.character.LAOCharacter;
import com.altekis.rpg.combatassistant.character.RPGCharacter;

public class CharacterActivity extends Activity {
	static private RPGCharacter character;
	ListView attacksListView;
	CharacterAttacksArrayAdapter characterAttacksAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_character);
        
        // Get Extras
        int characterId = getIntent().getIntExtra("CharacterId",0);
        LAOCharacter laoCharacter = new LAOCharacter(this);
        character = laoCharacter.getCharacter(characterId);
        
        // Set UI
        TextView nameText = (TextView) findViewById(R.id.character_name);
    	nameText.setText(character.getName());
        TextView playerNameText = (TextView) findViewById(R.id.character_playerName);
    	playerNameText.setText(character.getPlayerName());
    	
    	attacksListView = (ListView) findViewById(R.id.character_attacks);
    	 
        // Assign listener to list
        attacksListView.setOnItemClickListener(attacksClickListener);
    	
        // Add a +attack button
        Button btnAddAttack = new Button(this);
        btnAddAttack.setText("New Attack");
        btnAddAttack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doAddAttack();
			}
		});
        attacksListView.addFooterView(btnAddAttack);

        populateAttackList();
    }
    
	private void populateAttackList() {
		// Feed lists of games to the adapter
		// FIXME Fake list
		CharacterAttack attack = new CharacterAttack();
		attack.setId(1);
		attack.setName("Espada");
		attack.setWeaponCode("SW");
		List<CharacterAttack> atts = new ArrayList<CharacterAttack>();
		atts.add(attack);
        characterAttacksAdapter = new CharacterAttacksArrayAdapter(this, atts);

	    // Assign adapter to populate list
        attacksListView.setAdapter(characterAttacksAdapter);
	}


    private void doAddAttack() {
    	// Generate a BLANK, NEW attack, and jump start to its edition
//    	RPGCharacter character = new RPGCharacter();
//    	character.setId(new Random().nextInt()); // TODO Fix
//    	character.setName("NOT DEFINED");
//    	character.setPlayerName("NOT DEFINED");
//    	new LAOCharacter(this).addCharacter(character);
//    	
//    	Intent intent = new Intent(this, CharacterActivity.class);
//    	intent.putExtra("CharacterId", character.getId());
//        startActivity(intent);
    }
    
    
    /**
	 * Listener for attack list
	 */ 
	ListView.OnItemClickListener attacksClickListener = new ListView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
//			Intent intent = new Intent(getApplicationContext(), CharacterActivity.class);
//	    	intent.putExtra("CharacterId", selectedCharacterId);
//	        startActivity(intent);			
//			return true;
		}
	};

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_character, menu);
        return true;
    }

    
}
