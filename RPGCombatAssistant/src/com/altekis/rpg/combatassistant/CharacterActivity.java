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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.altekis.rpg.combatassistant.R;
import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.attack.LAOAttack;
import com.altekis.rpg.combatassistant.character.CharacterAttacksArrayAdapter;
import com.altekis.rpg.combatassistant.character.LAOCharacter;
import com.altekis.rpg.combatassistant.character.RPGCharacter;

public class CharacterActivity extends Activity {
	static private RPGCharacter character;
	static private List<Attack> attacks;
	ListView attacksListView;
	CharacterAttacksArrayAdapter characterAttacksAdapter;

	static final int REQUEST_CHARACTER_EDIT = 1;
	static final int REQUEST_ADD_ATTACK = 2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_character);

		// Get Extras
		int characterId = getIntent().getIntExtra("CharacterId",0);
		LAOCharacter laoCharacter = new LAOCharacter(this);
		character = laoCharacter.getCharacter(characterId);

		attacksListView = (ListView) findViewById(R.id.character_attacks);

		// Assign listener to list
		attacksListView.setOnItemClickListener(attacksClickListener);

		// Add a +attack button
		Button btnAddAttack = new Button(this);
		btnAddAttack.setText("New Attack");
		btnAddAttack.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doAddAttack();
			}
		});
		attacksListView.addFooterView(btnAddAttack);

		// Show character data
		populateCharacterUI();
		// attacks list will be shown on 
		populateAttackList();
	}

	/**
	 * Display data
	 * Used on activity create, and each time we return from Edit with info updated
	 */
	private void populateCharacterUI() {
		// Set UI
		TextView nameText = (TextView) findViewById(R.id.character_name);
		nameText.setText(character.getName());
		TextView playerNameText = (TextView) findViewById(R.id.character_playerName);
		playerNameText.setText(character.getPlayerName());
	}

	private void populateAttackList() {
		// Feed lists of attacks to the adapter
		// FIXME Fake list
//		Attack attack = new Attack();
//		attack.setId(1);
//		attack.setName("Espada");
//		attack.setAttackType("SW");
		attacks = new LAOAttack(this).getAttacks(character.getId());
		characterAttacksAdapter = new CharacterAttacksArrayAdapter(this, attacks);

		// Assign adapter to populate list
		attacksListView.setAdapter(characterAttacksAdapter);
	}


	private void doAddAttack() {
    	// Generate a BLANK, NEW character, and jump start to its edition
    	Attack attack = new Attack();
    	// id is not set, database will auto-increment it
    	attack.setCharacterId(character.getId());
    	attack.setName("NEW ATTACK");
    	attack.setAttackType("");
    	long newAttackId = new LAOAttack(this).addAttack(attack);
    	
    	Intent intent = new Intent(this, AttackEditActivity.class);
    	intent.putExtra("AttackId", newAttackId);
		startActivityForResult(intent, REQUEST_ADD_ATTACK);
    }

	/**
	 * Listener for attack list
	 */ 
	ListView.OnItemClickListener attacksClickListener = new ListView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> arg0, View v, int position, long id) {
			//			Intent intent = new Intent(getApplicationContext(), CharacterActivity.class);
			//	    	intent.putExtra("CharacterId", selectedCharacterId);
			//	        startActivity(intent);			
			//			return true;
		}
	};

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.activity_character, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.menu_character_edit:
			doEdit();
			return true;
		case R.id.menu_character_delete:
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
		// Jump to the edition activity for this character
		Intent intent = new Intent(this, CharacterEditActivity.class);
		intent.putExtra("CharacterId", character.getId());
		startActivityForResult(intent, REQUEST_CHARACTER_EDIT);
	}

	/**
	 * Action for "Delete" command option
	 */
	public void doConfirmDelete() {
		// Confirm delete, then go and do it
		// Ready a confirmation dialog
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.character_deleteDialog_message)
		.setTitle(R.string.character_deleteDialog_title);
		// Add buttons
		builder.setPositiveButton(R.string.character_deleteDialog_ok, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				// User clicked OK button
				doDelete();
			}
		});
		builder.setNegativeButton(R.string.character_deleteDialog_cancel, new DialogInterface.OnClickListener() {
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
		int characterId = character.getId();
		LAOCharacter laoCharacter = new LAOCharacter(this);
		laoCharacter.deleteCharacter(characterId);
		finish(); // Close this activity
	}

	/**
	 * 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CHARACTER_EDIT) {
			switch (resultCode) {
			case RESULT_OK:
				// Character was edited. So we have to RELOAD it.
				// Only characterId is guaranteed to remain, we'll use it to access storage
				int characterId = character.getId();
				LAOCharacter laoCharacter = new LAOCharacter(this);
				character = laoCharacter.getCharacter(characterId);
				populateCharacterUI();
				break;
			case RESULT_CANCELED:
				// Character edition was cancelled. So, no need to reload it.
				break;
			}
		} else if (requestCode == REQUEST_ADD_ATTACK) {
			switch (resultCode) {
			case RESULT_OK:
				// A new attack has been added to the character. So we have to RELOAD its attack list.
				populateAttackList();
				break;
			case RESULT_CANCELED:
				// Attack creation edition was cancelled. So, no need to reload it.
				break;
			}
		}
	}
}
