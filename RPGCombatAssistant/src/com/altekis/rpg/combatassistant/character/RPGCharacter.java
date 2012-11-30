package com.altekis.rpg.combatassistant.character;
/**
 * All info related to a character in play
 *
 */
public class RPGCharacter {
	/** Id, for internal use */
	long id;
	/** Character name */
	String name;
	/** Player */
	String playerName;
	/** Max hit points of character - that is, when it's full healed */ 
	int maxHitPoints;
	/** Current hit points of character - "full heal" - suffered wounds */ 
	int hitPoints;
	/** Type of the armor worn by the character */
	ArmorType armorType; 
	
	/** Needed by spinners to show the correct value */
	public String toString() {
		return ((name!=null)?name:"") + ((playerName!=null)?" (" + playerName + ")":"");
	}
	
	// From here, just plain old getters and setters
	
	/**
	 * @return the id
	 */
	public long getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(long id) {
		this.id = id;
	}
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
	 * @return the playerName
	 */
	public String getPlayerName() {
		return playerName;
	}
	/**
	 * @param playerName the playerName to set
	 */
	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}
	/**
	 * @return the maxHitPoints
	 */
	public int getMaxHitPoints() {
		return maxHitPoints;
	}
	/**
	 * @param maxHitPoints the maxHitPoints to set
	 */
	public void setMaxHitPoints(int maxHitPoints) {
		this.maxHitPoints = maxHitPoints;
	}
	/**
	 * @return the hitPoints
	 */
	public int getHitPoints() {
		return hitPoints;
	}
	/**
	 * @param hitPoints the hitPoints to set
	 */
	public void setHitPoints(int hitPoints) {
		this.hitPoints = hitPoints;
	}
	/**
	 * @return the armorType
	 */
	public ArmorType getArmorType() {
		return armorType;
	}
	/**
	 * @param armorType the armorType to set
	 */
	public void setArmorType(ArmorType armorType) {
		this.armorType = armorType;
	}
}
