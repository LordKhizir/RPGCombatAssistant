package com.altekis.rpg.combatassistant;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.attack.AttackResult;
import com.altekis.rpg.combatassistant.attack.AttackType;
import com.altekis.rpg.combatassistant.attack.LAOAttack;
import com.altekis.rpg.combatassistant.character.ArmorType;
import com.altekis.rpg.combatassistant.character.LAOCharacter;
import com.altekis.rpg.combatassistant.character.RPGCharacter;

public class AttackActivity extends Activity {
	static private Attack attack;
	static private AttackType attackType;
	static private AttackResult attackResult;
	static private RPGCharacter selectedDefender;
	static private ArmorType selectedDefenderArmorType;
	static private List<RPGCharacter> characters;

	// Attack bonus - total = attack + parry
	static private int totalBonus = 0;
	static private int attackBonus = 0;

	// Static view references used everywhere
	static private TextView nameText;
	static private TextView bonusSeekText;
	static private SeekBar bonusSeek;
	static private Spinner defenderSpinner;
	static private Spinner defenderArmorTypeSpinner;
	static private EditText extraText;
	static private EditText rollText;
	static private TextView resultText;
	static private Button applyResultButton;
	static private Button goToCriticalButton;

	static final int VOID_CHARACTER_IDENTIFIER = -1;
	static final int REQUEST_ROLL_CRITICAL = 1;
	static final int REQUEST_ATTACK_EDIT = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attack);

		// Get Extras
		long attackId = getIntent().getLongExtra("AttackId",0);
		attack = new LAOAttack().getAttack(attackId);

		// Set static view references to UI elements used everywhere...
		nameText = (TextView) findViewById(R.id.attack_nameLabel);
		bonusSeekText = (TextView) findViewById(R.id.attack_bonusSeekLabel);
		bonusSeek = (SeekBar) findViewById(R.id.attack_bonusSeek);
		defenderSpinner = (Spinner) findViewById(R.id.attack_defender);
		defenderArmorTypeSpinner = (Spinner) findViewById(R.id.attack_defenderArmorType);
		extraText = (EditText) findViewById(R.id.attack_extra);
		rollText = (EditText) findViewById(R.id.attack_roll);
		resultText = (TextView) findViewById(R.id.attack_result);
		applyResultButton = (Button) findViewById(R.id.attack_applyResultButton);
		goToCriticalButton = (Button) findViewById(R.id.attack_goToCriticalButton);

		// Get actual characters
		LAOCharacter laoCharacter = new LAOCharacter();
		RPGCharacter voidCharacter = new RPGCharacter();
		voidCharacter.setId(VOID_CHARACTER_IDENTIFIER);
		voidCharacter.setName("(Nadie)");
		characters = laoCharacter.getActiveCharacters();
		characters.add(0,voidCharacter);

		// Defender Spinner - fed with list of "current" characters
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<RPGCharacter> defenderAdapter = new ArrayAdapter<RPGCharacter>(this,
				android.R.layout.simple_spinner_item,
				characters);
		// Specify the layout to use when the list of choices appears
		defenderAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		defenderSpinner.setAdapter(defenderAdapter);
		defenderSpinner.setOnItemSelectedListener(new DefenderSelectedListener());


		// DefenderArmorType Spinner
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<ArmorType> defenderArmorTypeAdapter = new ArrayAdapter<ArmorType>(this,
				android.R.layout.simple_spinner_item,
				ArmorType.values());
		// Specify the layout to use when the list of choices appears
		defenderArmorTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		defenderArmorTypeSpinner.setAdapter(defenderArmorTypeAdapter);
		defenderArmorTypeSpinner.setOnItemSelectedListener(new DefenderArmorTypeSelectedListener());

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

		attackType = RPGCombatAssistant.attackTypes.get(attack.getAttackType());
		nameText.setText(attack.getName() + " (" + attackType.getName() + ")");

		totalBonus = attack.getBonus();

		bonusSeek.setMax(totalBonus);
		bonusSeek.setProgress(totalBonus);

		// By default, EVERYTHING to attack
		attackBonus = totalBonus;
		updateBonusText();
	}

	/**
	 * Display data
	 * Used each time the Defender Spinner is changed
	 */
	private void populateDefenderArmorType() {
		selectedDefenderArmorType = selectedDefender.getArmorType();

		// Select spinner position - Defender armor type
		int position = 0;
		for (ArmorType type:ArmorType.values()) {
			if (type.equals(selectedDefenderArmorType)) {
				defenderArmorTypeSpinner.setSelection(position);
				break;
			}
			position++;
		}
	}

	private void updateBonusText() {
		int attackParry = totalBonus - attackBonus;
		bonusSeekText.setText(Integer.toString(attackBonus) + " ataque · defensa " + Integer.toString(attackParry));	
	}

	/** Nested class for spinner value recovery */
	public class DefenderArmorTypeSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			selectedDefenderArmorType = ((ArmorType)parent.getItemAtPosition(pos));
		}

		public void onNothingSelected(AdapterView<?> parent) {
			// 
		}
	}

	/** Nested class for spinner value recovery */
	public class DefenderSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			selectedDefender = ((RPGCharacter)parent.getItemAtPosition(pos));
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
	 * Get all info and calculate attack result
	 */
	private void doGo() {
		// Reset visibility
		resultText.setText("");
		goToCriticalButton.setVisibility(View.GONE);

		// Initialize variables
		int bonus = 0;
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

		// Bonus is always calculated
		// TODO - check from text/seekbar
		bonus = attackBonus;

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
			int total = bonus + extra + roll;

			attackResult = attackType.getValue(roll, total, selectedDefenderArmorType);

			if (attackResult.isNoEffects()) {
				resultMessage = getResources().getString(R.string.noEffect);	
			} else if (attackResult.isFumbled()){
				// TODO implementar pifia
				resultMessage = getResources().getString(R.string.fumble);
			} else {
				// TODO Mejorar mensaje
				// TODO implementar pérdida de PV
				// TODO implementar critico
				// TODO localizar
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
					resultMessage+= "Crítico " + attackResult.getCritLevel().toString() + " (" + attackResult.getCritType() + ")";

					// There's a critical result... show UI elements for it
					goToCriticalButton.setVisibility(View.VISIBLE);					
					goToCriticalButton.requestFocus();
					
				}
				((ScrollView) findViewById(R.id.attack_scrollView)).scrollTo(0,((TextView)findViewById(R.id.attack_lastElement)).getBottom());
			}
		}
		// Show result of attack
		resultText.setText(resultMessage);
	}

	/**
	 * Apply result to defender
	 */
	private void doApplyResult() {
		LAOCharacter laoCharacter = new LAOCharacter();
		laoCharacter.modifyHitPoints(selectedDefender.getId(), attackResult.getHitPoints()*-1, selectedDefender.getHitPoints(), selectedDefender.getMaxHitPoints());
		applyResultButton.setText("Aplicado");
		applyResultButton.setEnabled(false);

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
		// Jump to the edition activity for this attack
		Intent intent = new Intent(this, AttackEditActivity.class);
		intent.putExtra("AttackId", attack.getId());
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
		long attackId = attack.getId();
		new LAOAttack().deleteAttack(attackId);
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
				long attackId = attack.getId();
				attack = new LAOAttack().getAttack(attackId);
				populateAttackUI();
				break;
			case RESULT_CANCELED:
				// Character edition was cancelled. So, no need to reload it.
				break;
			}
		}
	}
}
