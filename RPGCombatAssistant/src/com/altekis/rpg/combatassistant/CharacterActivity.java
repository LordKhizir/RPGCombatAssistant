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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import com.altekis.rpg.combatassistant.character.CharacterAttacksArrayAdapter;
import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.character.RPGCharacter;
import com.altekis.rpg.combatassistant.character.RPGCharacterAttack;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public class CharacterActivity extends BaseActivity {

    public static final String ARG_CHARACTER_ID = "CharacterId";

	private RPGCharacter character;
	private List<RPGCharacterAttack> attacks;

	private ListView attacksListView;
	private CharacterAttacksArrayAdapter characterAttacksAdapter;

	// view references used everywhere
	private TextView nameText;

	static final int REQUEST_CHARACTER_EDIT = 1;
	static final int REQUEST_ADD_ATTACK = 2;
	static final int REQUEST_ATTACK = 3;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_character);

		// Get Extras
		long characterId = getIntent().getLongExtra(ARG_CHARACTER_ID, 0);
        reload(characterId, true, true);

        // Set static view references to UI elements used everywhere...
		nameText = (TextView) findViewById(R.id.character_nameLabel);
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

        if (character != null) {
            // Show character data
            populateCharacterUI();
            // attacks list will be shown on
            populateAttackList();
        }
	}

	/**
	 * Display data
	 * Used on activity create, and each time we return from Edit with info updated
	 */
	private void populateCharacterUI() {
        if (character.isPnj()) {
            nameText.setText(getString(R.string.character_name_pnj, character.getName()));
        } else {
            nameText.setText(getString(R.string.character_name_pnj, character.getName(), character.getPlayerName()));
        }
	}

	private void populateAttackList() {
		// Feed lists of attacks to the adapter
		characterAttacksAdapter = new CharacterAttacksArrayAdapter(this, attacks);

		// Assign adapter to populate list
		attacksListView.setAdapter(characterAttacksAdapter);
	}


	private void doAddAttack() {
    	// Jump start to the edition of a new Attack   	
    	Intent intent = new Intent(this, AttackEditActivity.class);
    	// We pass no AttackId as extra, to claim for a new attack
    	// But the CharacterId will be needed, to assign the new attack to its correct parent character
    	intent.putExtra(ARG_CHARACTER_ID, character.getId());
		startActivityForResult(intent, REQUEST_ADD_ATTACK);
    }
	
	private void doAttack(long attackId) {
    	Intent intent = new Intent(this, AttackActivity.class);
    	intent.putExtra("AttackId", attackId);
    	startActivityForResult(intent, REQUEST_ATTACK);
	}

	/**
	 * Listener for attack list
	 */ 
	ListView.OnItemClickListener attacksClickListener = new ListView.OnItemClickListener() {
		@Override
		public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
			long selectedAttackId = characterAttacksAdapter.getItemId(position);
			doAttack(selectedAttackId);
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
		intent.putExtra(ARG_CHARACTER_ID, character.getId());
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
        try {
            Dao<RPGCharacter, Long> dao = getHelper().getDaoRPGCharacter();
            dao.delete(character);
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Can't read database", e);
        }
		finish(); // Close this activity
	}

	/**
	 * 
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == REQUEST_CHARACTER_EDIT) {
			switch (resultCode) {
			case RESULT_OK:
				// RPGCharacter was edited. So we have to RELOAD it.
				// Only characterId is guaranteed to remain, we'll use it to access storage
                reload(character.getId(), true, false);
				populateCharacterUI();
				break;
			case RESULT_CANCELED:
				// RPGCharacter edition was cancelled. So, no need to reload it.
				break;
			}
		} else if (requestCode == REQUEST_ADD_ATTACK) {
			switch (resultCode) {
			case RESULT_OK:
				// A new attack has been added to the character. So we have to RELOAD its attack list.
                reload(character.getId(), false, true);
				populateAttackList();
				break;
			case RESULT_CANCELED:
				// Attack creation edition was cancelled. So, no need to reload it.
				break;
			}
		} else if (requestCode == REQUEST_ATTACK) {
			// Reload attacks, as the attack can have been edited or even deleted
			populateAttackList();			
		}
	}

    private void reload(long characterId, boolean reloadCharacter, boolean reloadAttacks) {
        try {
            if (reloadCharacter) {
                Dao<RPGCharacter, Long> dao = getHelper().getDaoRPGCharacter();
                character = dao.queryForId(characterId);
            }
            if (reloadAttacks) {
                Dao<RPGCharacterAttack, Long> daoA = getHelper().getDaoRPGCharacterAttack();
                Dao<Attack, Long> daoAttack = getHelper().getDaoAttack();
                attacks = daoA.queryForEq(RPGCharacterAttack.FIELD_CHARACTER_ID, characterId);
                if (attacks != null) {
                    for (RPGCharacterAttack a : attacks) {
                        daoAttack.refresh(a.getAttack());
                    }
                }
            }
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Can't read database", e);
        }
    }
}
