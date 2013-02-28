package com.altekis.rpg.combatassistant;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.*;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.SeekBar.OnSeekBarChangeListener;
import com.altekis.rpg.combatassistant.attack.AttackResult;
import com.altekis.rpg.combatassistant.character.ArmorType;
import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.db.DBUtil;
import com.altekis.rpg.combatassistant.character.RPGCharacter;
import com.altekis.rpg.combatassistant.character.RPGCharacterAttack;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public class AttackActivity extends BaseActivity {

	private RPGCharacterAttack characterAttack;
    private List<RPGCharacter> characters;
    private ArmorType[] armorTypes;
    private AttackResult attackResult;
	private RPGCharacter selectedDefender;


	// Attack bonus - total = characterAttack + parry
	private int totalBonus = 0;
	private int attackBonus = 0;

	// View references used everywhere
	private TextView nameText;
	private TextView bonusSeekText;
	private SeekBar bonusSeek;
    private Spinner defenderArmorSpinner;
	private EditText extraText;
	private EditText rollText;
	private TextView resultText;
	private Button applyResultButton;
	private Button goToCriticalButton;

	static final int VOID_CHARACTER_IDENTIFIER = -1;
	static final int REQUEST_ROLL_CRITICAL = 1;
	static final int REQUEST_ATTACK_EDIT = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attack);

		// Get Extras
		long attackId = getIntent().getLongExtra("AttackId",0);
        try {
            Dao<RPGCharacterAttack, Long> daoRPGCharacterAttack = getHelper().getDaoRPGCharacterAttack();
            Dao<RPGCharacter, Long> daoRPGCharacter = getHelper().getDaoRPGCharacter();
            Dao<Attack, Long> daoAttack = getHelper().getDaoAttack();
            characterAttack = daoRPGCharacterAttack.queryForId(attackId);
            daoAttack.refresh(characterAttack.getAttack());
            // Get actual characters
		    characters = daoRPGCharacter.query(daoRPGCharacter.queryBuilder().orderBy(RPGCharacter.FIELD_NAME, true).prepare());
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Can't read database", e);
        }

		// Set static view references to UI elements used everywhere...
		nameText = (TextView) findViewById(R.id.attack_nameLabel);
		bonusSeekText = (TextView) findViewById(R.id.attack_bonusSeekLabel);
		bonusSeek = (SeekBar) findViewById(R.id.attack_bonusSeek);
        Spinner defenderSpinner = (Spinner) findViewById(R.id.attack_defender);
		defenderArmorSpinner = (Spinner) findViewById(R.id.attack_defenderArmorType);
		extraText = (EditText) findViewById(R.id.attack_extra);
		rollText = (EditText) findViewById(R.id.attack_roll);
		resultText = (TextView) findViewById(R.id.attack_result);
		applyResultButton = (Button) findViewById(R.id.attack_applyResultButton);
		goToCriticalButton = (Button) findViewById(R.id.attack_goToCriticalButton);

		// Defender Spinner - fed with list of "current" characters
        int size = characters == null ? 1 : characters.size() + 1;
        String[] titles = new String[size];
        int pos = 0;
        titles[pos++] = "(Nadie)";
        if (size > 1) {
            for (RPGCharacter c : characters) {
                if (c.isPnj()) {
                    titles[pos++] = getString(R.string.character_name_pnj, c.getName());
                } else {
                    titles[pos++] = getString(R.string.character_name, c.getName(), c.getPlayerName());
                }
            }
        }
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<String> defenderAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,
				titles);
		// Specify the layout to use when the list of choices appears
		defenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		defenderSpinner.setAdapter(defenderAdapter);
		defenderSpinner.setOnItemSelectedListener(new DefenderSelectedListener());


		// DefenderArmorType Spinner
		// Create an ArrayAdapter using the string array and a default spinner layout
        // TODO - Change when rolemaster setting will available
        armorTypes = ArmorType.getArmorTypes(false);
        titles = new String[armorTypes.length];
        for (int i = 0 ; i < armorTypes.length ; i++) {
            titles[i] = getString(armorTypes[i].getMerpString());
        }
		ArrayAdapter<String> defenderArmorTypeAdapter = new ArrayAdapter<String>(this,
				android.R.layout.simple_spinner_item,
				titles);
		// Specify the layout to use when the list of choices appears
		defenderArmorTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		defenderArmorSpinner.setAdapter(defenderArmorTypeAdapter);
		//defenderArmorSpinner.setOnItemSelectedListener(new DefenderArmorTypeSelectedListener());

		// Add support for seek bar
		bonusSeek.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				attackBonus = progress;
				if (fromUser){
					updateBonusText();
				}
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
		});

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

		applyResultButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doApplyResult();
			}
		});

		goToCriticalButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doGoToCritical();
			}
		});

		// Complete UI
		populateAttackUI();
	}

	/**
	 * Display data
	 * Used on activity creation, and each time we return from Edit with info updated
	 */
	private void populateAttackUI() {
		// Set UI
        nameText.setText(characterAttack.getName() + " (" + characterAttack.getAttack().getName() + ")");

		totalBonus = characterAttack.getBonus();

		bonusSeek.setMax(totalBonus);
		bonusSeek.setProgress(totalBonus);

		// By default, EVERYTHING to characterAttack
		attackBonus = totalBonus;
		updateBonusText();
	}

	/**
	 * Display data
	 * Used each time the Defender Spinner is changed
	 */
	private void populateDefenderArmorType() {
        if (selectedDefender == null) {
            defenderArmorSpinner.setSelection(0);
        } else {
            // TODO - Change when rolemaster setting will available
            ArmorType type = ArmorType.fromInteger(selectedDefender.getArmorType()).getMerp();
            // Select spinner position - Defender armor type
            for (int i = 0 ; i < armorTypes.length ; i++) {
                if (armorTypes[i] == type) {
                    defenderArmorSpinner.setSelection(i);
                    break;
                }
            }
        }
	}

	private void updateBonusText() {
		int attackParry = totalBonus - attackBonus;
		bonusSeekText.setText(Integer.toString(attackBonus) + " ataque · defensa " + Integer.toString(attackParry));	
	}

	/** Nested class for spinner value recovery */
	public class DefenderSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
            if (pos == 0) {
                selectedDefender = null;
            } else {
                selectedDefender = characters.get(pos - 1);
            }
			populateDefenderArmorType();
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
	 * Get all info and calculate characterAttack result
	 */
	private void doGo() {
		// Reset visibility
		resultText.setText("");
		goToCriticalButton.setVisibility(View.GONE);

		// Initialize variables
		int roll = 0;
		int extra = 0;
		String resultMessage = "";
		boolean errorFound = false;

		// Check for errors on the UI as a whole, while loading variables with their values
		String extraRaw = extraText.getText().toString().trim();
		if (extraRaw.length()==0) {
			extra = 0; // 0 allowed
		} else {
			try {
				extra = Integer.parseInt(extraRaw);
			} catch (NumberFormatException e) {
				errorFound = true;
				extraText.setError(getResources().getText(R.string.errorMustBeANumber));
			}
		}

		String rollRaw = rollText.getText().toString().trim();
		if (rollRaw.length()==0) {
			errorFound = true; // 0 is allowed, just in case of fumble (example, 02 - (02))... but we'll require it to be explicitly typed, to avoid usual errors
			rollText.setError(getResources().getText(R.string.errorMandatory));
		} else {
			try {
				roll = Integer.parseInt(rollRaw);
			} catch (NumberFormatException e) {
				errorFound = true;
				rollText.setError(getResources().getText(R.string.errorMustBeANumber));
			}
		}

		if (!errorFound) {
			// TODO Falta parry, bonuses/minuses por oportunidad, status, etc...
			int total = attackBonus + extra + roll;

            // Get user selected armor
            ArmorType armorType = armorTypes[defenderArmorSpinner.getSelectedItemPosition()];
            attackResult = DBUtil.getValue(getHelper(), characterAttack.getAttack(), roll, total, armorType);

			if (attackResult.isNoEffects()) {
				resultMessage = getResources().getString(R.string.noEffect);	
			} else if (attackResult.isFumbled()){
				// TODO implementar pifia
				resultMessage = getResources().getString(R.string.fumble);
			} else {
				// TODO Mejorar mensaje
				if (attackResult.getHitPoints()>0) {
					resultMessage = attackResult.getHitPoints() + " puntos de vida.\n";
					
					// Some HP to substract... show UI elements for it... but only if we know the defender
					if (selectedDefender.getId()!=VOID_CHARACTER_IDENTIFIER) {
						applyResultButton.setEnabled(true);
						applyResultButton.setVisibility(View.VISIBLE);	
						applyResultButton.requestFocus();
					}
				}
				if (attackResult.getCritLevel()!=null) {
					resultMessage+= "Crítico " + attackResult.getCritLevel().toString() + " (" + attackResult.getCritical() + ")";

					// There's a critical result... show UI elements for it
					goToCriticalButton.setVisibility(View.VISIBLE);					
					goToCriticalButton.requestFocus();
					
				}
				findViewById(R.id.attack_scrollView).scrollTo(0, findViewById(R.id.attack_lastElement).getBottom());
			}
		}
		// Show result of characterAttack
		resultText.setText(resultMessage);
	}

	/**
	 * Apply result to defender
	 */
	private void doApplyResult() {
		int hitPoints = selectedDefender.getHitPoints() + (attackResult.getHitPoints() * -1);
        if (hitPoints > selectedDefender.getMaxHitPoints()) {
            hitPoints = selectedDefender.getMaxHitPoints();
        }
        if (hitPoints < 0) {
            hitPoints = 0;
        }
        selectedDefender.setHitPoints(hitPoints);

        try {
            Dao<RPGCharacter, Long> daoRPGCharacter = getHelper().getDaoRPGCharacter();
            daoRPGCharacter.update(selectedDefender);
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Can't read database", e);
        }

        applyResultButton.setText("Aplicado");
		applyResultButton.setEnabled(false);

	}
	/**
	 * Go to critical rolling activity
	 */
	private void doGoToCritical() {
		// Get needed info from static variables
		Intent intent = new Intent(this, CriticalActivity.class);
		// We pass no AttackId as extra, to claim for a new characterAttack
		// But the CharacterId will be needed, to assign the new characterAttack to its correct parent character
		intent.putExtra(CriticalActivity.ARG_CRITICAL_ID, attackResult.getCritical().getId());
		intent.putExtra(CriticalActivity.ARG_CRITICAL_LEVEL, attackResult.getCritLevel().name());
		//intent.putExtra("CharacterId", character.getId());
		startActivityForResult(intent, REQUEST_ROLL_CRITICAL);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_attack, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_attack_edit:
			doEdit();
			return true;
		case R.id.menu_attack_delete:
			doConfirmDelete();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Action for "Edit" command option
	 */
	public void doEdit() {
		// Jump to the edition activity for this characterAttack
		Intent intent = new Intent(this, AttackEditActivity.class);
		intent.putExtra("AttackId", characterAttack.getId());
		startActivityForResult(intent, REQUEST_ATTACK_EDIT);
	}

	/**
	 * Action for "Delete" command option
	 */
	public void doConfirmDelete() {
		// Confirm delete, then go and do it
		// Ready a confirmation dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.attack_deleteDialog_message)
		.setTitle(R.string.attack_deleteDialog_title);
		// Add buttons
		builder.setPositiveButton(R.string.attack_deleteDialog_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User clicked OK button
				doDelete();
			}
		});
		builder.setNegativeButton(R.string.attack_deleteDialog_cancel, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User cancelled the dialog
				// Nothing else to do here 
			}
		});
		AlertDialog deleteDialog = builder.create();
		deleteDialog.show();
	}

	/**
	 * Delete character
	 */
	public void doDelete() {
		long attackId = characterAttack.getId();
        try {
            Dao<Attack, Long> daoAttack = getHelper().getDaoAttack();
            daoAttack.deleteById(attackId);
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Can't read database", e);
        }
		finish(); // Close this activity
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_ATTACK_EDIT) {
			switch (resultCode) {
			case RESULT_OK:
				// Attack was edited. So we have to RELOAD it.
				// Only attackId is guaranteed to remain, we'll use it to access storage
				long attackId = characterAttack.getId();
                try {
                    Dao<RPGCharacterAttack, Long> daoRPGCharacterAttack = getHelper().getDaoRPGCharacterAttack();
                    Dao<Attack, Long> daoAttack = getHelper().getDaoAttack();
                    characterAttack = daoRPGCharacterAttack.queryForId(attackId);
                    daoAttack.refresh(characterAttack.getAttack());
                } catch (SQLException e) {
                    Log.e("RPGCombatAssistant", "Can't read database", e);
                }
				populateAttackUI();
				break;
			case RESULT_CANCELED:
				// RPGCharacter edition was cancelled. So, no need to reload it.
				break;
			}
		}
	}
}
