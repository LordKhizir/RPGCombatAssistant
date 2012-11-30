package com.altekis.rpg.combatassistant;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.altekis.rpg.combatassistant.character.ArmorType;
import com.altekis.rpg.combatassistant.character.LAOCharacter;
import com.altekis.rpg.combatassistant.character.RPGCharacter;

public class CharacterEditActivity extends Activity {
	static private RPGCharacter character;
	static final int CREATE_NEW_CHARACTER = -1;
	
	static private ArmorType selectedArmorType = null;

	// Static view references used everywhere
	static private EditText nameText;
	static private EditText playerNameText;
	static private EditText maxHitPointsText;
	static private EditText hitPointsText;
	static private Spinner armorTypeSpinner;
	
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
		
    	// Set static view references to UI elements used everywhere...
		nameText = (EditText) findViewById(R.id.characterEdit_name);
		playerNameText = (EditText) findViewById(R.id.characterEdit_playerName);
		maxHitPointsText = (EditText) findViewById(R.id.characterEdit_maxHitPoints);
		hitPointsText = (EditText) findViewById(R.id.characterEdit_hitPoints);
		armorTypeSpinner = (Spinner) findViewById(R.id.characterEdit_armorType);

		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<ArmorType> armorTypeAdapter = new ArrayAdapter<ArmorType>(this,
				android.R.layout.simple_spinner_item,
				ArmorType.values());
		// Specify the layout to use when the list of choices appears
		armorTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		armorTypeSpinner.setAdapter(armorTypeAdapter);
		armorTypeSpinner.setOnItemSelectedListener(new ArmorTypeSelectedListener());

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
        
        // Complete UI
        populateUI();
	}
	
	/**
	 * Display data
	 * Used on activity creation
	 */
	private void populateUI() {
		nameText.setText(character.getName());
		playerNameText.setText(character.getPlayerName());
		maxHitPointsText.setText(Integer.toString(character.getMaxHitPoints()));
		hitPointsText.setText(Integer.toString(character.getHitPoints()));
		
		// Select spinner position - Armor type
		int position = 0;
		for (ArmorType type:ArmorType.values()) {
			if (type.equals(selectedArmorType)) {
				armorTypeSpinner.setSelection(position);
				break;
			}
			position++;
		}
	}

	/** Nested class for spinner value recovery */
	public class ArmorTypeSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			selectedArmorType = ((ArmorType)parent.getItemAtPosition(pos));
		}

		public void onNothingSelected(AdapterView<?> parent) {
			selectedArmorType = null;
		}
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
		character.setName(nameText.getText().toString().trim());
		character.setPlayerName(playerNameText.getText().toString().trim());
		
		// Before saving, check for errors
		String maxHitPointsRaw = maxHitPointsText.getText().toString().trim();
		String hitPointsRaw = hitPointsText.getText().toString().trim();
 
		boolean errorFound = false;
		if (character.getName().length()==0) {
			nameText.setError(getResources().getText(R.string.errorMandatory));
			errorFound = true;
		}
		if (character.getPlayerName().length()==0) {
			playerNameText.setError(getResources().getText(R.string.errorMandatory));
			errorFound = true;
		}
		if (maxHitPointsRaw.length()==0) {
			errorFound = true; // 0 is allowed... but we'll require it to be explicitly typed, to avoid usual errors
			maxHitPointsText.setError(getResources().getText(R.string.errorMandatory));
		} else {
			try {
				character.setMaxHitPoints(Integer.parseInt(maxHitPointsRaw));
			} catch (NumberFormatException e) {
				errorFound = true;
				maxHitPointsText.setError(getResources().getText(R.string.errorMustBeANumber));
			}
		}
		if (hitPointsRaw.length()==0) {
			errorFound = true; // 0 is allowed... but we'll require it to be explicitly typed, to avoid usual errors
			hitPointsText.setError(getResources().getText(R.string.errorMandatory));
		} else {
			try {
				character.setHitPoints(Integer.parseInt(hitPointsRaw));
			} catch (NumberFormatException e) {
				errorFound = true;
				hitPointsText.setError(getResources().getText(R.string.errorMustBeANumber));
			}
		}
		// armor type is a spinner, it's always correct
		character.setArmorType(selectedArmorType);

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
