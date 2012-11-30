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
	 * Get the list of ACTIVE characters
	 * @return list of characters
	 */
	public List<RPGCharacter> getActiveCharacters() {
		return new DAOCharacter().getActiveCharacters();
	}

	/**
	 * Get requested character from database
	 */
	public RPGCharacter getCharacter(long characterId) {
		return new DAOCharacter().getCharacter(characterId);
	}

	/**
	 * Add a new character to database
	 * @param character
	 */
	public long addCharacter(RPGCharacter character) {
		return new DAOCharacter().addCharacter(character);
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
	public void deleteCharacter(long characterId) {
		new DAOCharacter().deleteCharacter(characterId);
	}
	
	/**
	 * Add to the character's current hit points
	 * @param characterId
	 */
	public void modifyHitPoints(long characterId, int hitPointsToAdd, int actualHitPoints, int maxHitPoints) {
		int hitPoints = actualHitPoints + hitPointsToAdd;
		if (hitPoints>maxHitPoints) {
			hitPoints=maxHitPoints;
		}
		if (hitPoints<0) {
			hitPoints = 0;
		}
		new DAOCharacter().updateHitPoints(characterId, hitPoints);
	}

}
