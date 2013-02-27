package com.altekis.rpg.combatassistant;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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

import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.character.RPGCharacter;
import com.altekis.rpg.combatassistant.character.RPGCharacterAttack;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

public class AttackEditActivity extends BaseActivity {

    private static final int CREATE_NEW_ATTACK = 0;

	private RPGCharacterAttack characterAttack;
	private long attackId;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attack_edit);

		// Get Extras
		long attackId = getIntent().getLongExtra("AttackId", CREATE_NEW_ATTACK);

		if (attackId == CREATE_NEW_ATTACK) {
			// If no AttackId, we'll create a new one instead of updating
			characterAttack = new RPGCharacterAttack();
			// ... but the parent character should be assigned!
			long characterId = getIntent().getLongExtra(CharacterActivity.ARG_CHARACTER_ID, 0);
            RPGCharacter rpgCharacter = new RPGCharacter();
            rpgCharacter.setId(characterId);
			characterAttack.setRPGCharacter(rpgCharacter);
		} else {
			// Retrieve the desired characterAttack
            try {
                Dao<RPGCharacterAttack, Long> dao = getHelper().getDaoRPGCharacterAttack();
                characterAttack = dao.queryForId(attackId);
            } catch (SQLException e) {
                Log.e("RPGCombatAssistant", "Can't read database", e);
            }
        }

        if (characterAttack == null) {
            // Database not accesible, we need to finish
            finish();
        } else {
            // Set UI
            EditText nameText = (EditText) findViewById(R.id.attackEdit_name);
            nameText.setText(characterAttack.getName());

            Spinner attackTypeSpinner = (Spinner) findViewById(R.id.attackEdit_attackType);
            // Create an ArrayAdapter using the string array and a default spinner layout
            List<Attack> attackList;
            try {
                Dao<Attack, Long> dao = getHelper().getDaoAttack();
                QueryBuilder<Attack, Long> qb = dao.queryBuilder();
                // TODO - Change when rolemaster will available
                qb.setWhere(qb.where().eq(Attack.FIELD_RM, false));
                qb.orderBy(Attack.FIELD_NAME, true);
                attackList = dao.query(qb.prepare());
            } catch (SQLException e) {
                Log.e("RPGCombatAssistant", "Can't read database", e);
                attackList = new ArrayList<Attack>();
            }
            ArrayAdapter<Attack> attackTypeAdapter = new ArrayAdapter<Attack>(this,
                    android.R.layout.simple_spinner_item, attackList);
            // Specify the layout to use when the list of choices appears
            attackTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            attackTypeSpinner.setAdapter(attackTypeAdapter);
            if (characterAttack.getAttack() != null) {
                // Editting, we need to set de spinner on right position
                int position = 0;
                for (Attack attack : attackList) {
                    if (attack.getId() == characterAttack.getId()) {
                        attackTypeSpinner.setSelection(position);
                        break;
                    }
                    position++;
                }
            }
            attackTypeSpinner.setOnItemSelectedListener(new AttackTypeSelectedListener());

            EditText bonusText = (EditText) findViewById(R.id.attackEdit_bonus);
            bonusText.setText(Integer.toString(characterAttack.getBonus()));

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
	}

	/** Nested class for spinner value recovery */
	public class AttackTypeSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			attackId = ((Attack) parent.getItemAtPosition(pos)).getId();
		}

		public void onNothingSelected(AdapterView<?> parent) {
			attackId = 0;
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
		// Update characterAttack with the info provided by the user
		EditText nameText = (EditText) findViewById(R.id.attackEdit_name);
		EditText bonusText = (EditText) findViewById(R.id.attackEdit_bonus);

		// For each UI field: get input value, check for errors, update characterAttack field
		boolean errorFound = false;
		
		// Name - mandatory
		String name = nameText.getText().toString().trim();
		if (name.length()==0) {
			nameText.setError(getResources().getText(R.string.errorMandatory));
			errorFound = true;
		} else {
			characterAttack.setName(name);
		}
		
		// Attack type - it's a spinner, no error possible
        Attack attack = new Attack();
        attack.setId(attackId);
		characterAttack.setAttack(attack);
		
		// Bonus - numeric, mandatory
		int bonus = 0;
		try {
			bonus = Integer.parseInt(bonusText.getText().toString());
			characterAttack.setBonus(bonus);
		} catch (NumberFormatException e) {
			errorFound = true;
			bonusText.setError(getResources().getText(R.string.errorMustBeANumber));
		}
 
		if (!errorFound) {
            try {
                Dao<RPGCharacterAttack, Long> dao = getHelper().getDaoRPGCharacterAttack();
                dao.createOrUpdate(characterAttack);
                setResult(RESULT_OK); // Set result as OK == created/updated
            } catch (SQLException e) {
                Log.e("RPGCombatAssistant", "Can't write database", e);
            }

			finish();
		}
	}
}
