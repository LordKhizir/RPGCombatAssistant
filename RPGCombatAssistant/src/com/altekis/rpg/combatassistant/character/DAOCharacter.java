package com.altekis.rpg.combatassistant.character;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.altekis.rpg.combatassistant.LocalDatabaseHelper;


/**
 * Data layer for Character
 *
 */
public class DAOCharacter {

	private final String CHARACTERS_TABLE_NAME = "characters";
	private final String[] ALL_COLUMNS = {
			"_id",
			"name",
			"playerName",
			"maxHitPoints",
			"hitPoints",
			"armorType"};	
	/**
	 * Get the list of characters, both player and non-player types
	 * @return list of characters
	 */
	protected List<List<RPGCharacter>> getCharacters() {
		List<RPGCharacter> pcList = new ArrayList<RPGCharacter>();
		List<RPGCharacter> npcList = new ArrayList<RPGCharacter>();

		SQLiteDatabase db = LocalDatabaseHelper.getInstance().getDB();

		Cursor cursor = db.query(CHARACTERS_TABLE_NAME, // Table
				ALL_COLUMNS, // returned columns
				null, // WHERE - We want all columns, so no filters here
				null, // selection arguments - no filters
				null, // GROUP BY
				null, // HAVING
				null // ORDER BY
				);

		// Iterate through rows
		boolean iterate = cursor.moveToFirst();
		while (iterate) {
			RPGCharacter character = cursorToRPGCharacter(cursor);
			// Add to corresponding list
			//			if (SmuacsApplication.isItYourTurn(game)) {
			//				yourTurnList.add(game);
			//			} else {
			//				theirTurnList.add(game);
			//			}
			pcList.add(character);
			iterate = cursor.moveToNext();
		}
		cursor.close();

		// Encapsulate the lists and return
		List<List<RPGCharacter>> allCharacters = new ArrayList<List<RPGCharacter>>();
		allCharacters.add(pcList);
		allCharacters.add(npcList);
		return allCharacters;
	}

	/**
	 * Get the list of ACTIVE characters
	 * @return list of characters
	 */
	protected List<RPGCharacter> getActiveCharacters() {
		List<RPGCharacter> list = new ArrayList<RPGCharacter>();

		SQLiteDatabase db = LocalDatabaseHelper.getInstance().getDB();

		// TODO! Filtrar, solo queremos activos
		Cursor cursor = db.query(CHARACTERS_TABLE_NAME, // Table
				ALL_COLUMNS, // returned columns
				null, // WHERE - We want all columns, so no filters here
				null, // selection arguments - no filters
				null, // GROUP BY
				null, // HAVING
				null // ORDER BY
				);

		// Iterate through rows
		boolean iterate = cursor.moveToFirst();
		while (iterate) {
			RPGCharacter character = cursorToRPGCharacter(cursor);
			list.add(character);
			iterate = cursor.moveToNext();
		}
		cursor.close();

		// Encapsulate the lists and return
		return list;
	}
	/**
	 * Get requested character from local database
	 * @return character
	 */
	public RPGCharacter getCharacter(long id) {
		SQLiteDatabase db = LocalDatabaseHelper.getInstance().getDB();

		Cursor cursor = db.query(CHARACTERS_TABLE_NAME, // Table
				ALL_COLUMNS, // returned columns
				"_id=?", // WHERE - We want just one row
				new String[] {String.valueOf(id)},// selection arguments - id
				null, // GROUP BY
				null, // HAVING
				null // ORDER BY
				);
		if (cursor!=null) {
			cursor.moveToFirst();
		}
		RPGCharacter character = cursorToRPGCharacter(cursor);
		cursor.close();
		return character;
	}

	/**
	 * Add a new character to database
	 * @param character
	 */
	protected long addCharacter(RPGCharacter character) {
		SQLiteDatabase db = LocalDatabaseHelper.getInstance().getDB();

		ContentValues cv = rpgCharacterToContentValues(character);
		cv.remove("_id"); // _id is auto-increment, so we have to nullify it prior to calling
		return db.insert(CHARACTERS_TABLE_NAME, null, cv);
	}

	/**
	 * Update an existing character in database
	 * @param character
	 */
	public void updateCharacter(RPGCharacter character) {
		SQLiteDatabase db = LocalDatabaseHelper.getInstance().getDB();

		ContentValues cv = rpgCharacterToContentValues(character);
		db.update(CHARACTERS_TABLE_NAME, cv, "_id=?",  new String[] {String.valueOf(character.getId())});
	}
	
	/**
	 * Add the character's current hit points
	 * @param characterId
	 * @param hitPoints
	 */
	public void updateHitPoints(long characterId, int hitPoints) {
		SQLiteDatabase db = LocalDatabaseHelper.getInstance().getDB();

		ContentValues cv = new ContentValues();
		cv.put("hitPoints", hitPoints);
		
		db.update(CHARACTERS_TABLE_NAME, cv, "_id=?",  new String[] {String.valueOf(characterId)});
	}

	/**
	 * Delete an existing character in database
	 * @param character
	 */
	public void deleteCharacter(long characterId) {
		SQLiteDatabase db = LocalDatabaseHelper.getInstance().getDB();

		db.delete(CHARACTERS_TABLE_NAME, "_id=?",  new String[] {String.valueOf(characterId)});
	}

	/* Transformers */
	private ContentValues rpgCharacterToContentValues(RPGCharacter character) {
		ContentValues cv = new ContentValues();
		cv.put("_id", character.getId());
		cv.put("name", character.getName());
		cv.put("playerName", character.getPlayerName());
		cv.put("maxHitPoints", character.getMaxHitPoints());
		cv.put("hitPoints", character.getHitPoints());
		cv.put("armorType", character.getArmorType().toInteger());
		return cv;
	}

	private RPGCharacter cursorToRPGCharacter(Cursor cursor) {
		RPGCharacter character = new RPGCharacter();
		int i=0;
		character.setId(cursor.getLong(i++));
		character.setName(cursor.getString(i++));
		character.setPlayerName(cursor.getString(i++));
		character.setMaxHitPoints(cursor.getInt(i++));
		character.setHitPoints(cursor.getInt(i++));
		character.setArmorType(ArmorType.fromInteger(cursor.getInt(i++)));
		return character;
	}
}
