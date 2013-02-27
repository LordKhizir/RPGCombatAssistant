package com.altekis.rpg.combatassistant;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.altekis.rpg.combatassistant.character.ArmorType;
import com.altekis.rpg.combatassistant.character.RPGCharacter;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;

public class CharacterEditActivity extends BaseActivity {

	static final int CREATE_NEW_CHARACTER = 0;

    private RPGCharacter character;
	private ArmorType[] armorTypes;

	// view references used everywhere
	private EditText nameText;
	private EditText playerNameText;
	private EditText maxHitPointsText;
	private EditText hitPointsText;
	private Spinner armorTypeSpinner;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_character_edit);

		// Get Extras
		long characterId = getIntent().getLongExtra(CharacterActivity.ARG_CHARACTER_ID, CREATE_NEW_CHARACTER);

    	if (characterId==CREATE_NEW_CHARACTER) {
    		// If no CharacterId, we'll create a new one instead of updating
    		character = new RPGCharacter();
            character.setArmorType(ArmorType.TP1.getArmor());
    		character.setId(CREATE_NEW_CHARACTER);
    	} else {
    		// Retrieve the desired character
            try {
                Dao<RPGCharacter, Long> dao = getHelper().getDaoRPGCharacter();
                character = dao.queryForId(characterId);
            } catch (SQLException e) {
                Log.e("RPGCombatAssistant", "Can't read database", e);
            }
    	}

        if (character == null) {
            // Database not accesible, we need to finish
            finish();
        } else {
            // Set static view references to UI elements used everywhere...
            nameText = (EditText) findViewById(R.id.characterEdit_name);
            playerNameText = (EditText) findViewById(R.id.characterEdit_playerName);
            maxHitPointsText = (EditText) findViewById(R.id.characterEdit_maxHitPoints);
            hitPointsText = (EditText) findViewById(R.id.characterEdit_hitPoints);
            armorTypeSpinner = (Spinner) findViewById(R.id.characterEdit_armorType);

            // TODO - Change when rolemaster setting will available
            armorTypes = ArmorType.getArmorTypes(false);
            final String[] titles = new String[armorTypes.length];
            for (int i = 0 ; i < armorTypes.length ; i++) {
                titles[i] = getString(armorTypes[i].getMerpString());
            }
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<String> armorTypeAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item,
                    titles);
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
		for (ArmorType type : armorTypes) {
			if (type.getArmor() == character.getArmorType()) {
				armorTypeSpinner.setSelection(position);
				break;
			}
			position++;
		}
	}

	/** Nested class for spinner value recovery */
	public class ArmorTypeSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			character.setArmorType(armorTypes[pos].getArmor());
		}

		public void onNothingSelected(AdapterView<?> parent) {
			//
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
        // Armor is setted on spinner change
		//character.setArmorType(selectedArmorType);

		if (!errorFound) {
			// Everything is correct... go create/update the character
            try {
                Dao<RPGCharacter, Long> dao = getHelper().getDaoRPGCharacter();
                dao.createOrUpdate(character);
                setResult(RESULT_OK); // Set result as OK == created/updated
            } catch (SQLException e) {
                Log.e("RPGCombatAssistant", "Can't read database", e);
            }
	    	finish();
		}
	}
}
