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

import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.attack.AttackType;
import com.altekis.rpg.combatassistant.attack.LAOAttack;
import com.altekis.rpg.combatassistant.character.RPGCharacter;

public class AttackEditActivity extends Activity {
	static private RPGCharacter character; // TODO Check if we really need it
	static private Attack attack;
	static String selectedAttackType = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attack_edit);

		// Get Extras
		long attackId = getIntent().getLongExtra("AttackId",0);
		attack = new LAOAttack().getAttack(attackId);

//		LAOCharacter laoCharacter = new LAOCharacter(this);
//		character = laoCharacter.getCharacter(characterId);

		// Set UI
		EditText nameText = (EditText) findViewById(R.id.attackEdit_name);
		nameText.setText(attack.getName());
		
		Spinner attackTypeSpinner = (Spinner) findViewById(R.id.attackEdit_attackType);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> attackTypeAdapter = new ArrayAdapter<CharSequence>(this,android.R.layout.simple_spinner_item);
		for(AttackType attackType:RPGCombatAssistant.attackTypes.values()) {
			attackTypeAdapter.add(attackType.getName());
		}
		// Specify the layout to use when the list of choices appears
		attackTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		attackTypeSpinner.setAdapter(attackTypeAdapter);
		attackTypeSpinner.setOnItemSelectedListener(new AttackTypeSelectedListener());

		
		// Add listeners for buttons
		Button btnCancel = (Button) findViewById(R.id.attackEdit_cancelButton);
        btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doCancel();
			}
		});

		Button btnSave = (Button) findViewById(R.id.attackEdit_saveButton);
        btnSave.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doSave();
			}
		});
	}
	
	/** Nested class for spinner value recovery */
	public class AttackTypeSelectedListener implements OnItemSelectedListener {

	    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
	        selectedAttackType = parent.getItemAtPosition(pos).toString();
	    }

	    public void onNothingSelected(AdapterView<?> parent) {
	        selectedAttackType = null;
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
		// Update attack with the info provided by the user
		EditText nameText = (EditText) findViewById(R.id.attackEdit_name);
		attack.setName(nameText.getText().toString());
		attack.setAttackType(selectedAttackType);
    	new LAOAttack().updateAttack(attack);
    	
    	// Set result as OK==updated
    	setResult(RESULT_OK);
    	finish();
	}
}
