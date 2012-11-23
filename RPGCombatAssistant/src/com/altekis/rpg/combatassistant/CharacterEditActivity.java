package com.altekis.rpg.combatassistant;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.altekis.rpg.combatassistant.R;
import com.altekis.rpg.combatassistant.character.LAOCharacter;
import com.altekis.rpg.combatassistant.character.RPGCharacter;

public class CharacterEditActivity extends Activity {
	static private RPGCharacter character;
	static final int CREATE_NEW_CHARACTER = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_character_edit);

		// Get Extras
		long characterId = getIntent().getLongExtra("CharacterId",CREATE_NEW_CHARACTER);

    	if (characterId==CREATE_NEW_CHARACTER) {
    		// If no CharacterId, we'll create a new one instead of updating
    		character = new RPGCharacter();
    		character.setId(CREATE_NEW_CHARACTER);
    	} else {
    		// Retrieve the desired character
    		character = new LAOCharacter().getCharacter(characterId);
    	}
		
		// Set UI
		EditText nameText = (EditText) findViewById(R.id.characterEdit_name);
		nameText.setText(character.getName());
		EditText playerNameText = (EditText) findViewById(R.id.characterEdit_playerName);
		playerNameText.setText(character.getPlayerName());
				
		// Add listeners for buttons
		Button btnCancel = (Button) findViewById(R.id.characterEdit_cancelButton);
        btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doCancel();
			}
		});

		Button btnSave = (Button) findViewById(R.id.characterEdit_saveButton);
        btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doSave();
			}
		});
	}
	
	/**
	 * Ignore changes, and go back to caller
	 */
	private void doCancel() {
		// Set result as CANCELED
		setResult(RESULT_CANCELED);
		finish();
	}
	
	/**
	 * Apply changes and go back to caller
	 */
	private void doSave() {
		// Update character with the info provided by the user
		EditText nameText = (EditText) findViewById(R.id.characterEdit_name);
		character.setName(nameText.getText().toString().trim());
		EditText playerNameText = (EditText) findViewById(R.id.characterEdit_playerName);
		character.setPlayerName(playerNameText.getText().toString().trim());
		
		// Before saving, check for errors
		boolean errorFound = false;
		if (character.getName().length()==0) {
			nameText.setError(getResources().getText(R.string.errorMandatory));
			errorFound = true;
		}
		if (character.getPlayerName().length()==0) {
			playerNameText.setError(getResources().getText(R.string.errorMandatory));
			errorFound = true;
		}
		
		if (!errorFound) {
			// Everything is correct... go create/update the character
			if (character.getId()==CREATE_NEW_CHARACTER) {
				// Create a new character with the entered info
				new LAOCharacter().addCharacter(character);
			} else {
				// Update an existing character
		    	new LAOCharacter().updateCharacter(character);
			}

			setResult(RESULT_OK); // Set result as OK == created/updated
	    	finish();
		}
	}
}
