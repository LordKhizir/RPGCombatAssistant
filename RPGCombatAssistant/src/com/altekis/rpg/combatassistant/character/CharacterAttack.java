package com.altekis.rpg.combatassistant.character;
/**
 * All info related to the current attacks of a character in play
 *
 */
public class CharacterAttack {
	/** Id, for internal use */
	int id;
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	/**
	 * @param id the id to set
	 */
	public void setId(int id) {
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
	 * @return the base
	 */
	public int getBase() {
		return base;
	}
	/**
	 * @param base the base to set
	 */
	public void setBase(int base) {
		this.base = base;
	}
	/** Attack name */
	String name;
	/** Weapon code */
	String weaponCode;
	/**
	 * @return the weaponCode
	 */
	public String getWeaponCode() {
		return weaponCode;
	}
	/**
	 * @param weaponCode the weaponCode to set
	 */
	public void setWeaponCode(String weaponCode) {
		this.weaponCode = weaponCode;
	}
	/** Base score for roll */
	int base;
}
