package com.altekis.rpg.combatassistant;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.attack.LAOAttack;
import com.altekis.rpg.combatassistant.character.LAOCharacter;
import com.altekis.rpg.combatassistant.character.RPGCharacter;

public class AttackEditActivity extends Activity {
	static private RPGCharacter character; // TODO Check if we really need it
	static private Attack attack;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attack_edit);

		// Get Extras
		long attackId = getIntent().getLongExtra("AttackId",0);
		LAOAttack laoAttack = new LAOAttack(this);
		attack = laoAttack.getAttack(attackId);

//		LAOCharacter laoCharacter = new LAOCharacter(this);
//		character = laoCharacter.getCharacter(characterId);

		// Set UI
		EditText nameText = (EditText) findViewById(R.id.attackEdit_name);
		nameText.setText(attack.getName());
		EditText attackTypeText = (EditText) findViewById(R.id.attackEdit_attackType);
		attackTypeText.setText(attack.getAttackType());
		
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
		EditText attackTypeText = (EditText) findViewById(R.id.attackEdit_attackType);
		attack.setAttackType(attackTypeText.getText().toString());
    	new LAOAttack(this).updateAttack(attack);
    	
    	// Set result as OK==updated
    	setResult(RESULT_OK);
    	finish();
	}
}
