package com.altekis.rpg.combatassistant.attack;

import com.altekis.rpg.combatassistant.critical.CriticalLevel;

/**
 * Wrapper for the results of an attack
 *
 */
public class AttackResult {
	int hitPoints = 0;
	CriticalLevel critLevel = null;
	String critType = null;
	boolean fumbled = false;
	
	// Getters
	public int getHitPoints() {
		return hitPoints;
	}
	public CriticalLevel getCritLevel() {
		return critLevel;
	}
	public String getCritType() {
		return critType;
	}
	public boolean isFumbled() {
		return fumbled;
	}
	public boolean isNoEffects() {
		return (hitPoints==0 && critType == null && fumbled==false);
	}
	// Setters
	public void setHitPoints(int hitPoints) {
		this.hitPoints = hitPoints;
	}
	public void setCritLevel(CriticalLevel critLevel) {
		this.critLevel = critLevel;
	}
	public void setCritType(String critType) {
		this.critType = critType;
	}
	public void setFumbled(boolean fumbled) {
		this.fumbled = fumbled;
	}
}
