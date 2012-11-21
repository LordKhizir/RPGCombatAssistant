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
import android.widget.TextView;
import android.widget.Toast;

import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.attack.AttackType;
import com.altekis.rpg.combatassistant.attack.LAOAttack;
import com.altekis.rpg.combatassistant.character.ArmorType;
import com.altekis.rpg.combatassistant.character.RPGCharacter;

public class AttackActivity extends Activity {
	static private RPGCharacter character; // TODO Check if we really need it
	static private Attack attack;
	static String selectedAttackType = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attack);

		// Get Extras
		long attackId = getIntent().getLongExtra("AttackId",0);
		attack = new LAOAttack().getAttack(attackId);

//		LAOCharacter laoCharacter = new LAOCharacter(this);
//		character = laoCharacter.getCharacter(characterId);

		// Set UI
		TextView nameText = (TextView) findViewById(R.id.attack_name);
		nameText.setText(attack.getName());
		
		TextView attackType = (TextView) findViewById(R.id.attack_attackType);
		attackType.setText(attack.getAttackType());

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
		int bonus = Integer.parseInt(bonusText.getText().toString());
		int roll = Integer.parseInt(rollText.getText().toString());
		// TODO Falta parry, bonuses/minuses por oportunidad, status, etc...
		// TODO Falta Armor type... asumimos 1.
		int total = bonus + roll;
		
		/* TODO Debería ser algo como esto
		AttackType attackType = RPGCombatAssistant.attackTypes.get(attack.getAttackType());
		
		pero ahora mismo en attack.getAttackType tenemos el NOMBRE del ataque, no su Key.
		Hay que cambiar el spinner de edición de ataque para que muestre "Filo" pero guarde "S"
		
		De momento, acceso a S a piñón
		*/
		AttackType attackType = RPGCombatAssistant.attackTypes.get("S");
		String result = attackType.getValue(total, ArmorType.SoftLeather);

    	Toast.makeText(getApplicationContext(), "Resultado:" + result, Toast.LENGTH_SHORT).show();
    	// Set result as OK==updated
//    	setResult(RESULT_OK);
//    	finish();
	}
}
