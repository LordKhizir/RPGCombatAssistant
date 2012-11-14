package com.altekis.rpg.combatassistant.character;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.altekis.rpg.combatassistant.LocalDatabaseHelper;


/**
 * Data layer for Character
 *
 */
public class DAOCharacter {
	
	private LocalDatabaseHelper dbHelper;
	private SQLiteDatabase db;
	
	public DAOCharacter(Context context) {
	    dbHelper = new LocalDatabaseHelper(context);
	    db = dbHelper.getWritableDatabase();
	}
	private final String CHARACTERS_TABLE_NAME = "characters";
	private final String[] ALL_COLUMNS = {	"_id",
											"name", "playerName"};

	@Override
	protected void finalize() throws Throwable {
		try {
			db.close();
			dbHelper.close();
		} finally {
			super.finalize();
		}
	}
	
//	/**
//	 * Get the list of your games, in every state
//	 * TODO Now it's just a fake method -> make a real call to AppEngine.
//	 * @return list of games
//	 */
//	protected void refreshGames() {
//		//if (SmuacsApplication.allGames==null) {
//			fillFakeGameList();
//		//}
//	}
	
	/**
	 * Get the list of characters, both player and non-player types
	 * @return list of characters
	 */
	protected List<List<RPGCharacter>> getCharacters() {
		List<RPGCharacter> pcList = new ArrayList<RPGCharacter>();
		List<RPGCharacter> npcList = new ArrayList<RPGCharacter>();
		
		Cursor cursor = db.query(CHARACTERS_TABLE_NAME, // Table
				ALL_COLUMNS, // returned columns
				null, // WHERE - We want all columns, so no filters here
				null, // selection args - no filters
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
	 * Get requested character from local database
	 * @return character
	 */
	public RPGCharacter getCharacter(int id) {
		Cursor cursor = db.query(CHARACTERS_TABLE_NAME, // Table
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
		RPGCharacter character = cursorToRPGCharacter(cursor);
		cursor.close();
		return character;
	}
	
//	private void fillFakeGameList() {
//		// First, delete current contents
//        db.delete(GAMES_TABLE_NAME, null, null);
//        
//		Random random = new Random();
//		for(String name:names) {
//			long gameId = random.nextInt(10000);
//			long partnerId = random.nextInt(10000);
//			long senderId = ((int)(gameId%2)==0)?partnerId:SmuacsApplication.getUserId(); //TODO current user id
//			Game game = new Game(gameId,partnerId,name,senderId,random.nextInt(20),random.nextInt(10));
//			ContentValues cv = gameToContentValues(game);
//			db.insert(GAMES_TABLE_NAME, null, cv);
//		}
//	}
	/**
	 * Add a new character to database
	 * @param character
	 */
	protected void addCharacter(RPGCharacter character) {
		ContentValues cv = rpgCharacterToContentValues(character);
		db.insert(CHARACTERS_TABLE_NAME, null, cv);
	}
	
	/**
	 * Update an existing character in database
	 * @param character
	 */
	public void updateCharacter(RPGCharacter character) {
		ContentValues cv = rpgCharacterToContentValues(character);
		db.update(CHARACTERS_TABLE_NAME, cv, "_id=?",  new String[] {String.valueOf(character.getId())});
	}

	/**
	 * Delete an existing character in database
	 * @param character
	 */
	public void deleteCharacter(int characterId) {
		db.delete(CHARACTERS_TABLE_NAME, "_id=?",  new String[] {String.valueOf(characterId)});
	}

	/* Transformers */
	private ContentValues rpgCharacterToContentValues(RPGCharacter character) {
		ContentValues cv = new ContentValues();
		cv.put("_id", character.getId());
		cv.put("name", character.getName());
		cv.put("playerName", character.getPlayerName());
		return cv;
	}
	
	private RPGCharacter cursorToRPGCharacter(Cursor cursor) {
		RPGCharacter character = new RPGCharacter();
		int i=0;
		character.setId(cursor.getInt(i++));
		character.setName(cursor.getString(i++));
		character.setPlayerName(cursor.getString(i++));
		return character;
	}
}
