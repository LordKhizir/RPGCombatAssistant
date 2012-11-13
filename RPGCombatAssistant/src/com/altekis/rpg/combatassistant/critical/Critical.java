package com.altekis.rpg.combatassistant.critical;

import android.util.SparseArray;

public class Critical {
	/** Name of the Critical */
	private String name;
	/** Key (like S for Slash, K for Krush...) - It acts as the link from attack tables */
	private String key;
	/** Ranges
	 * Stored using "min value for the range" as key, and text as value */
	private SparseArray<String> results = new SparseArray<String>();


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
	 * Add a result to the critical
	 * Used by XML inflaters
	 * @param minRange
	 * @param text
	 */
	public void addResult(int minRange, String text) {
		results.append(minRange, text);
	}

	/**
	 *  Return the result corresponding to passed roll
	 * @param roll to check for
	 * @return Descriptive result
	 */
	public String getValue(int roll) {
		int selectedIndex = -1; // If it remains as -1, no value found
		for(int actualIndex = 0; actualIndex < results.size(); actualIndex++) {
		   int actualKey = results.keyAt(actualIndex);
		   // Array is set as key=min range value... so we use it to navigate
		   if (roll>=actualKey) {
			   // Roll is equal or greater than this range entry value... so it's valid, unless surpassed by next one
			   selectedIndex = actualIndex;
		   } else {
			   // Roll is not enough to enter this range... just exit.
			   break;
		   }
		}
		if (selectedIndex!=-1) {
			return results.valueAt(selectedIndex);
		} else {
			return null;
		}
	}
	public String getKey() {
		return key;
	}


	public void setKey(String key) {
		this.key = key;
	}
}
