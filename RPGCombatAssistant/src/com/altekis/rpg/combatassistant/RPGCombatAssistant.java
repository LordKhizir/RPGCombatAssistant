package com.altekis.rpg.combatassistant;

import java.util.HashMap;

import android.app.Application;

import com.altekis.rpg.combatassistant.attack.AttackType;
import com.altekis.rpg.combatassistant.critical.Critical;

public class RPGCombatAssistant extends Application {

	public static boolean initialized = false;

	private static RPGCombatAssistant singletonInstance;
	
	
	private RPGCombatAssistant() {
		super();
	}

	public static synchronized RPGCombatAssistant get() {
		if (singletonInstance == null) {
			singletonInstance = new RPGCombatAssistant();
		}
		return singletonInstance;
	}

	// Application cached data - AKA Global variables
	/** Critical tables */
	public static HashMap<String,Critical> criticals = new HashMap<String,Critical>();
	
	/** Attack type tables */
	public static HashMap<String,AttackType> attackTypes = new HashMap<String,AttackType>();

}
