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

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_character_edit);

		// Get Extras
		int characterId = getIntent().getIntExtra("CharacterId",0);
		character = new LAOCharacter().getCharacter(characterId);

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
		character.setName(nameText.getText().toString());
		EditText playerNameText = (EditText) findViewById(R.id.characterEdit_playerName);
		character.setPlayerName(playerNameText.getText().toString());
    	new LAOCharacter().updateCharacter(character);
    	
    	// Set result as OK==updated
    	setResult(RESULT_OK);
    	finish();
	}
}
