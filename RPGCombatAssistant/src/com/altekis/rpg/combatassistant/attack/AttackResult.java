package com.altekis.rpg.combatassistant.attack;

import com.altekis.rpg.combatassistant.critical.CriticalLevel;
import com.altekis.rpg.combatassistant.critical.Critical;

/**
 * Wrapper for the results of an attack
 *
 */
public class AttackResult {
	int hitPoints = 0;
	CriticalLevel critLevel = null;
	Critical critical = null;
	boolean fumbled = false;
	
	// Getters
	public int getHitPoints() {
		return hitPoints;
	}
	public CriticalLevel getCritLevel() {
		return critLevel;
	}
	public Critical getCritical() {
		return critical;
	}
	public boolean isFumbled() {
		return fumbled;
	}
	public boolean isNoEffects() {
		return (hitPoints==0 && critical == null && fumbled==false);
	}
	// Setters
	public void setHitPoints(int hitPoints) {
		this.hitPoints = hitPoints;
	}
	public void setCritLevel(CriticalLevel critLevel) {
		this.critLevel = critLevel;
	}
	public void setCritical(Critical critical) {
		this.critical = critical;
	}
	public void setFumbled(boolean fumbled) {
		this.fumbled = fumbled;
	}
}
