package com.altekis.rpg.combatassistant.attack;

import java.util.List;

public class LAOAttack {
	/**
	 * Get the list of attacks for the character
	 * @return list of attacks
	 */
	public List<Attack> getAttacks(int characterId) {
		return new DAOAttack().getAttacks(characterId);
	}
	
	/**
	 * Get requested attack from database
	 */
	public Attack getAttack(long attackId) {
		return new DAOAttack().getAttack(attackId);
	}

	/**
	 * Add a new attack to database
	 * @param attack
	 */
	public long addAttack(Attack attack) {
		return new DAOAttack().addAttack(attack);
	}
	
	/**
	 * Update an existing attack in database
	 * @param attack
	 */
	public void updateAttack(Attack attack) {
		new DAOAttack().updateAttack(attack);
	}
	
	/**
	 * Delete an existing attack in database
	 * @param attackId
	 */
	public void deleteAttack(long attackId) {
		new DAOAttack().deleteAttack(attackId);
	}
}
