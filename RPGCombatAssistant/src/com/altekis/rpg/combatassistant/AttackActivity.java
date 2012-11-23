package com.altekis.rpg.combatassistant;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.attack.AttackResult;
import com.altekis.rpg.combatassistant.attack.AttackType;
import com.altekis.rpg.combatassistant.attack.LAOAttack;
import com.altekis.rpg.combatassistant.character.ArmorType;

public class AttackActivity extends Activity {
	static private Attack attack;
	static private AttackType attackType;
	static String selectedAttackType = null;

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
		// Get all UI values
		EditText bonusText = (EditText) findViewById(R.id.attack_bonus);
		EditText rollText = (EditText) findViewById(R.id.attack_roll);
		int bonus = 0;
		int roll = 0;
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
			
			AttackResult attackResult = attackType.getValue(roll, total, ArmorType.SoftLeather);
			
			if (attackResult.isNoEffects()) {
				Toast.makeText(getApplicationContext(), "Sin efecto", Toast.LENGTH_SHORT).show();	
			} else if (attackResult.isFumbled()){
				// TODO implementar pifia
				Toast.makeText(getApplicationContext(), "¡Pifia!", Toast.LENGTH_SHORT).show();
			} else {
				// TODO Mejorar mensaje
				// TODO implementar pérdida de PV
				// TODO implementar critico
				String resultMessage = "";
				if (attackResult.getHitPoints()>0) {
					resultMessage = attackResult.getHitPoints() + " puntos de vida.\n";
				}
				if (attackResult.getCritLevel()!=null) {
					resultMessage+= "Crítico " + attackResult.getCritLevel().toString() + " (" + attackResult.getCritType() + ")";
				}
				Toast.makeText(getApplicationContext(), resultMessage, Toast.LENGTH_SHORT).show();
			}
	    	
	    	// Set result as OK==updated
	//    	setResult(RESULT_OK);
	//    	finish();
		}
	}
}
