package com.altekis.rpg.combatassistant.character;

import java.util.List;

public class LAOCharacter {
	/**
	 * Get the list of characters, both player and non-player types
	 * @return list of characters
	 */
	public List<List<RPGCharacter>> getCharacters() {
		return new DAOCharacter().getCharacters();
	}
	
	/**
	 * Get requested character from database
	 */
	public RPGCharacter getCharacter(int characterId) {
		return new DAOCharacter().getCharacter(characterId);
	}

	/**
	 * Add a new character to database
	 * @param character
	 */
	public void addCharacter(RPGCharacter character) {
		new DAOCharacter().addCharacter(character);
	}
	
	/**
	 * Update an existing character in database
	 * @param character
	 */
	public void updateCharacter(RPGCharacter character) {
		new DAOCharacter().updateCharacter(character);
	}
	
	/**
	 * Delete an existing character in database
	 * @param characterId
	 */
	public void deleteCharacter(int characterId) {
		new DAOCharacter().deleteCharacter(characterId);
	}
}
