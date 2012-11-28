package com.altekis.rpg.combatassistant;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.attack.AttackResult;
import com.altekis.rpg.combatassistant.attack.AttackType;
import com.altekis.rpg.combatassistant.attack.LAOAttack;
import com.altekis.rpg.combatassistant.character.ArmorType;

public class AttackActivity extends Activity {
	static private Attack attack;
	static private AttackType attackType;
	static String selectedAttackType = null;
	static private AttackResult attackResult;

	static final int REQUEST_ROLL_CRITICAL = 1;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attack);

		// Get Extras
		long attackId = getIntent().getLongExtra("AttackId",0);
		attack = new LAOAttack().getAttack(attackId);

		// Set UI
		TextView nameText = (TextView) findViewById(R.id.attack_name);
		nameText.setText(attack.getName());
		TextView attackTypeText = (TextView) findViewById(R.id.attack_attackType);
		attackType = RPGCombatAssistant.attackTypes.get(attack.getAttackType());
		attackTypeText.setText(attackType.getName());
		TextView bonusText = (TextView) findViewById(R.id.attack_bonus);
		bonusText.setText(Integer.toString(attack.getBonus()));

		// Add listeners for buttons
		Button btnCancel = (Button) findViewById(R.id.attack_cancelButton);
        btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doCancel();
			}
		});

		Button btnGo = (Button) findViewById(R.id.attack_goButton);
        btnGo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doGo();
			}
		});
        
        Button goToCriticalButton = (Button) findViewById(R.id.attack_goToCriticalButton);
        goToCriticalButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doGoToCritical();
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
	 * Get all info and calculate attack result
	 */
	private void doGo() {
		// Get all UI widgets
		EditText bonusText = (EditText) findViewById(R.id.attack_bonus);
		EditText rollText = (EditText) findViewById(R.id.attack_roll);
		TextView resultText = (TextView) findViewById(R.id.attack_result);
		Button goToCriticalButton = (Button) findViewById(R.id.attack_goToCriticalButton);

		// Reset visibility
		resultText.setText("");
		goToCriticalButton.setVisibility(View.INVISIBLE);

		int bonus = 0;
		int roll = 0;
		String resultMessage = "";

		boolean errorFound = false;
		// Check for errors on the UI as a whole
		try {
			bonus = Integer.parseInt(bonusText.getText().toString());
		} catch (NumberFormatException e) {
			errorFound = true;
			bonusText.setError(getResources().getText(R.string.errorMustBeANumber));
		}
		try {
			roll = Integer.parseInt(rollText.getText().toString());
		} catch (NumberFormatException e) {
			errorFound = true;
			rollText.setError(getResources().getText(R.string.errorMustBeANumber));
		}
		
		if (!errorFound) {
			// TODO Falta parry, bonuses/minuses por oportunidad, status, etc...
			// TODO Falta Armor type... asumimos 1.
			int total = bonus + roll;
			
			attackResult = attackType.getValue(roll, total, ArmorType.SoftLeather);
			
			if (attackResult.isNoEffects()) {
				// TODO Localizar
				resultMessage = "Sin efecto";	
			} else if (attackResult.isFumbled()){
				// TODO implementar pifia
				// TODO localizar
				resultMessage = "¡Pifia!";
			} else {
				// TODO Mejorar mensaje
				// TODO implementar pérdida de PV
				// TODO implementar critico
				// TODO localizar
				if (attackResult.getHitPoints()>0) {
					resultMessage = attackResult.getHitPoints() + " puntos de vida.\n";
				}
				if (attackResult.getCritLevel()!=null) {
					resultMessage+= "Crítico " + attackResult.getCritLevel().toString() + " (" + attackResult.getCritType() + ")";
					
					// There's a critical result... show UI elements for it
					goToCriticalButton.setVisibility(View.VISIBLE);
				}
			}
			
	    	
	    	// Set result as OK==updated
	//    	setResult(RESULT_OK);
	//    	finish();
		}
		// Show result of attack
		resultText.setText(resultMessage);
	}
	
	/**
	 * Go to critical rolling activity
	 */
	private void doGoToCritical() {
		// Get needed info from static variables
    	Intent intent = new Intent(this, CriticalActivity.class);
    	// We pass no AttackId as extra, to claim for a new attack
    	// But the CharacterId will be needed, to assign the new attack to its correct parent character
    	intent.putExtra("CriticalType", attackResult.getCritType());
    	intent.putExtra("CriticalLevelName", attackResult.getCritLevel().name());
    	//intent.putExtra("CharacterId", character.getId());
		startActivityForResult(intent, REQUEST_ROLL_CRITICAL);
	}
}
