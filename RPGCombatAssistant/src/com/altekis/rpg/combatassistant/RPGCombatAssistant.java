package com.altekis.rpg.combatassistant;

import java.util.HashMap;

import android.app.Application;

import com.altekis.rpg.combatassistant.critical.Critical;

public class RPGCombatAssistant extends Application {

	public static boolean initialized = false;

	private static RPGCombatAssistant singleton = null;

	public static RPGCombatAssistant get() {
		if (singleton==null) {
			singleton = new RPGCombatAssistant();
		}
		return singleton;
	}
	// Application cached data - AKA Global variables
	/** Critical tables */
	public static HashMap<String,Critical> criticals = new HashMap<String,Critical>();
}
