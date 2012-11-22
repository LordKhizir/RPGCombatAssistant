package com.altekis.rpg.combatassistant.attack;

import android.util.SparseArray;

import com.altekis.rpg.combatassistant.character.ArmorType;
import com.altekis.rpg.combatassistant.critical.CriticalLevel;

public class AttackType {
	/** Name of the attack type */
	private String name;
	/** Key (like S for Slash, K for Krush...) - It acts as the link from attack tables */
	private String key;
	/** Max number for fumble */
	private int maxFumble;
	/**
	 * @return the maxFumble
	 */
	public int getMaxFumble() {
		return maxFumble;
	}


	/**
	 * @param maxFumble the maxFumble to set
	 */
	public void setMaxFumble(int maxFumble) {
		this.maxFumble = maxFumble;
	}


	/** Ranges -we have an element on "results" for each Armor type
	 * Stored using "min value for the range" as key, and String[] as value
	 * String[] has one element for each armor type
	 * Each individual String is conformed of (hitpoints)(criticalLevel) - crit level can be blank
	 * */
	private SparseArray<String[]> results = new SparseArray<String[]>();

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}


	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * Add a result to the attack type
	 * Used by XML inflaters
	 * @param minRange
	 * @param String[] values
	 */
	public void addResult(int minRange, String[] values) {
		results.append(minRange, values);
	}

	/** Check if attack roll is a fumble */
	public boolean isFumble(int roll) {
		return roll<=maxFumble;
	}

	/**
	 *  Return the result corresponding to passed total and armour type. Roll is checked for fumble.
	 * @param roll to check for
	 * @return Descriptive result
	 */
	public AttackResult getValue(int roll, int total, ArmorType armorType) {
		AttackResult attackResult = new AttackResult();
		if (roll<=maxFumble) {
			// If fumble, just stop
			attackResult.setFumbled(true);
		} else {
			// Not fumbled... check for results
			int selectedIndex = -1; // If it remains as -1, no value found
			for(int actualIndex = 0; actualIndex < results.size(); actualIndex++) {
				int actualKey = results.keyAt(actualIndex);
				// Array is set as key=min range value... so we use it to navigate
				if (total>=actualKey) {
					// Roll is equal or greater than this range entry value... so it's valid, unless surpassed by next one
					selectedIndex = actualIndex;
				} else {
					// Roll is not enough to enter this range... just exit.
					break;
				}
			}

			if (selectedIndex!=-1) {
				// We have found a match
				String wholeResult = results.valueAt(selectedIndex)[armorType.toInteger()];
				// wholeResult is formatted as [number|critLevel], we have to split it
				String[] parts = wholeResult.split("-");
				attackResult.setHitPoints(Integer.parseInt(parts[0]));
				if (parts.length>1) {
					attackResult.setCritLevel(CriticalLevel.valueOf(parts[1]));
					attackResult.setCritType(key);
				}
			}
		}
		return attackResult;
	}

	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}
}
