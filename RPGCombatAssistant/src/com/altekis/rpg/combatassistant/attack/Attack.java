package com.altekis.rpg.combatassistant.attack;
/**
 * All info related to a character's attack
 *
 */
public class Attack {
	/** Id. for internal use - Primary key */
	long id;
	/** Character that "owns" that attack */
	long characterId;
	/** Key for the attack type */
	String attackType;
	/** Attack name - default as the type, but user can rename it to differentiate between similar weapons */
	String name;
	/** Total bonus for attack (statistic + skill + weapon) */
	int bonus;
	
	// Just plain, auto-generated setters and getters

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
	 * @return the characterId
	 */
	public long getCharacterId() {
		return characterId;
	}
	/**
	 * @param characterId the characterId to set
	 */
	public void setCharacterId(long characterId) {
		this.characterId = characterId;
	}
	/**
	 * @return the attackType
	 */
	public String getAttackType() {
		return attackType;
	}
	/**
	 * @param attackType the attackType to set
	 */
	public void setAttackType(String attackType) {
		this.attackType = attackType;
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
	 * @return the bonus
	 */
	public int getBonus() {
		return bonus;
	}
	/**
	 * @param bonus the bonus to set
	 */
	public void setBonus(int bonus) {
		this.bonus = bonus;
	}
}
