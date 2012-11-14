package com.altekis.rpg.combatassistant.character;

import java.util.List;

import android.content.ContentValues;
import android.content.Context;

public class LAOCharacter {
	Context context;
	DAOCharacter daoCharacter;

	public LAOCharacter(Context context) {
	    this.context = context;
	    daoCharacter = new DAOCharacter(context);
	}
	
	/**
	 * Get the list of characters, both player and non-player types
	 * @return list of characters
	 */
	public List<List<RPGCharacter>> getCharacters() {
		return daoCharacter.getCharacters();

//		List<RPGCharacter> pcList = new ArrayList<RPGCharacter>();
//		List<RPGCharacter> npcList = new ArrayList<RPGCharacter>();
//
//		Random random = new Random();
//		for (int i=0;i<names.length;i++) {
//			RPGCharacter character = new RPGCharacter();
//			character.setId(random.nextInt(10000));
//			character.setName(names[i]);
//			if (character.getId()%2==0) {
//				character.setPlayerName(playerNames[i]);
//				pcList.add(character);
//			} else {
//				character.setPlayerName("Master");
//				npcList.add(character);
//			}
//		}
//
//		// Encapsulate the lists and return
//		List<List<RPGCharacter>> allCharacters = new ArrayList<List<RPGCharacter>>();
//		allCharacters.add(pcList);
//		allCharacters.add(npcList);
//		return allCharacters;
	}
	
	/**
	 * Get requested character from database
	 */
	public RPGCharacter getCharacter(int characterId) {
		return daoCharacter.getCharacter(characterId);
	}

	/**
	 * Add a new character to database
	 * @param character
	 */
	public void addCharacter(RPGCharacter character) {
		daoCharacter.addCharacter(character);
	}
	
	/**
	 * Update an existing character in database
	 * @param character
	 */
	public void updateCharacter(RPGCharacter character) {
		daoCharacter.updateCharacter(character);
	}
}
