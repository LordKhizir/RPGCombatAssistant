package com.altekis.rpg.combatassistant.attack;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.altekis.rpg.combatassistant.LocalDatabaseHelper;


/**
 * Data layer for Attack
 *
 */
public class DAOAttack {
	
	private final String ATTACKS_TABLE_NAME = "attacks";
	private final String[] ALL_COLUMNS = {	"_id",
											"characterId",
											"name",
											"attackType",
											"bonus"};	
	/**
	 * Get the list of attacks for the character
	 * @return list of attacks
	 */
	protected List<Attack> getAttacks(long characterId) {
		SQLiteDatabase db = LocalDatabaseHelper.getInstance().getDB();

		List<Attack> attacks = new ArrayList<Attack>();
		
		Cursor cursor = db.query(ATTACKS_TABLE_NAME, // Table
				ALL_COLUMNS, // returned columns
				"characterId=?", // WHERE
				new String[] {String.valueOf(characterId)},// selection arguments - characterId
				null, // GROUP BY
				null, // HAVING
				null // ORDER BY
				);
		
		// Iterate through rows
		boolean iterate = cursor.moveToFirst();
		while (iterate) {
			Attack attack = cursorToAttack(cursor);
			attacks.add(attack);
			iterate = cursor.moveToNext();
		}
		cursor.close();
		return attacks;
	}
	
	/**
	 * Get requested attack from local database
	 * @return attack
	 */
	public Attack getAttack(long id) {
		SQLiteDatabase db = LocalDatabaseHelper.getInstance().getDB();

		Cursor cursor = db.query(ATTACKS_TABLE_NAME, // Table
				ALL_COLUMNS, // returned columns
				"_id=?", // WHERE - We want just one row
				new String[] {String.valueOf(id)},// selection args - id
				null, // GROUP BY
				null, // HAVING
				null // ORDER BY
				);
		if (cursor!=null) {
			cursor.moveToFirst();
		}
		Attack attack = cursorToAttack(cursor);
		cursor.close();
		return attack;
	}
	
	/**
	 * Add a new attack to database
	 * @param attack
	 */
	protected long addAttack(Attack attack) {
		SQLiteDatabase db = LocalDatabaseHelper.getInstance().getDB();

		ContentValues cv = attackToContentValues(attack);
		cv.remove("_id"); // _id is auto-increment, so we have to nullify it prior to calling
		return db.insert(ATTACKS_TABLE_NAME, null, cv);
	}
	
	/**
	 * Update an existing attack in database
	 * @param attack
	 */
	public void updateAttack(Attack attack) {
		SQLiteDatabase db = LocalDatabaseHelper.getInstance().getDB();

		ContentValues cv = attackToContentValues(attack);
		db.update(ATTACKS_TABLE_NAME, cv, "_id=?",  new String[] {String.valueOf(attack.getId())});
	}

	/**
	 * Delete an existing attack in database
	 * @param attack
	 */
	public void deleteAttack(long attackId) {
		SQLiteDatabase db = LocalDatabaseHelper.getInstance().getDB();

		db.delete(ATTACKS_TABLE_NAME, "_id=?",  new String[] {String.valueOf(attackId)});
	}

	/* Transformers */
	private ContentValues attackToContentValues(Attack attack) {
		ContentValues cv = new ContentValues();
		cv.put("_id", attack.getId());
		cv.put("characterId", attack.getCharacterId());
		cv.put("name", attack.getName());
		cv.put("attackType", attack.getAttackType());
		cv.put("bonus", attack.getBonus());
		return cv;
	}
	
	private Attack cursorToAttack(Cursor cursor) {
		Attack attack = new Attack();
		int i=0;
		attack.setId(cursor.getLong(i++));
		attack.setCharacterId(cursor.getLong(i++));
		attack.setName(cursor.getString(i++));
		attack.setAttackType(cursor.getString(i++));		
		attack.setBonus(cursor.getInt(i++));
		return attack;
	}
}
