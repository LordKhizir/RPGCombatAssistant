package com.altekis.rpg.combatassistant;

import java.util.ArrayList;

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

public class AttackEditActivity extends Activity {
	static private Attack attack;
	static String selectedAttackType = null;
	static final int CREATE_NEW_ATTACK = -1;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attack_edit);

		// Get Extras
		long attackId = getIntent().getLongExtra("AttackId",CREATE_NEW_ATTACK);

		if (attackId==CREATE_NEW_ATTACK) {
			// If no AttackId, we'll create a new one instead of updating
			attack = new Attack();
			attack.setId(CREATE_NEW_ATTACK);
			// ... but the parent character should be assigned!
			long characterId = getIntent().getLongExtra("CharacterId",0);
			attack.setCharacterId(characterId);
		} else {
			// Retrieve the desired attack
			attack = new LAOAttack().getAttack(attackId);
		}

		// Set UI
		EditText nameText = (EditText) findViewById(R.id.attackEdit_name);
		nameText.setText(attack.getName());

		Spinner attackTypeSpinner = (Spinner) findViewById(R.id.attackEdit_attackType);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<AttackType> attackTypeAdapter = new ArrayAdapter<AttackType>(this,
				android.R.layout.simple_spinner_item,
				new ArrayList<AttackType>(RPGCombatAssistant.attackTypes.values()));
		// Specify the layout to use when the list of choices appears
		attackTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		attackTypeSpinner.setAdapter(attackTypeAdapter);
		attackTypeSpinner.setOnItemSelectedListener(new AttackTypeSelectedListener());
		
		EditText bonusText = (EditText) findViewById(R.id.attackEdit_bonus);
		bonusText.setText(Integer.toString(attack.getBonus()));

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
			selectedAttackType = ((AttackType)parent.getItemAtPosition(pos)).getKey();
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
		EditText bonusText = (EditText) findViewById(R.id.attackEdit_bonus);

		// For each UI field: get input value, check for errors, update attack field
		boolean errorFound = false;
		
		// Name - mandatory
		String name = nameText.getText().toString().trim();
		if (name.length()==0) {
			nameText.setError(getResources().getText(R.string.errorMandatory));
			errorFound = true;
		} else {
			attack.setName(name);
		}
		
		// Attack type - it's a spinner, no error possible
		attack.setAttackType(selectedAttackType);
		
		// Bonus - numeric, mandatory
		int bonus = 0;
		try {
			bonus = Integer.parseInt(bonusText.getText().toString());
			attack.setBonus(bonus);
		} catch (NumberFormatException e) {
			errorFound = true;
			bonusText.setError(getResources().getText(R.string.errorMustBeANumber));
		}
 
		if (!errorFound) {
			// Everything is correct... go create/update the attack
			if (attack.getId()==CREATE_NEW_ATTACK) {
				// Create a new attack with the entered info
				new LAOAttack().addAttack(attack);
			} else {
				// Update an existing attack
				new LAOAttack().updateAttack(attack);
			}

			setResult(RESULT_OK); // Set result as OK == created/updated
			finish();
		}
	}
}
