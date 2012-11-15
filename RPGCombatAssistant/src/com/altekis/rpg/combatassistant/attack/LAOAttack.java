package com.altekis.rpg.combatassistant.attack;

import java.util.List;

import android.content.Context;

public class LAOAttack {
	Context context;
	DAOAttack daoAttack;

	public LAOAttack(Context context) {
	    this.context = context;
	    daoAttack = new DAOAttack(context);
	}
	
	/**
	 * Get the list of attacks for the character
	 * @return list of attacks
	 */
	public List<Attack> getAttacks(int characterId) {
		return daoAttack.getAttacks(characterId);
	}
	
	/**
	 * Get requested attack from database
	 */
	public Attack getAttack(long attackId) {
		return daoAttack.getAttack(attackId);
	}

	/**
	 * Add a new attack to database
	 * @param attack
	 */
	public long addAttack(Attack attack) {
		return daoAttack.addAttack(attack);
	}
	
	/**
	 * Update an existing attack in database
	 * @param attack
	 */
	public void updateAttack(Attack attack) {
		daoAttack.updateAttack(attack);
	}
	
	/**
	 * Delete an existing attack in database
	 * @param attackId
	 */
	public void deleteAttack(long attackId) {
		daoAttack.deleteAttack(attackId);
	}
}
