package com.altekis.rpg.combatassistant.attack;

import android.os.Bundle;
import com.altekis.rpg.combatassistant.character.RPGCharacter;
import com.altekis.rpg.combatassistant.character.RPGCharacterAttack;
import com.altekis.rpg.combatassistant.critical.Critical;
import com.altekis.rpg.combatassistant.critical.CriticalLevel;

/**
 * Wrapper for the results of an attack
 *
 */
public class AttackResult {

    private static final String CHARACTER_ATTACK_ID = "attackResultCharacterAttackId";
    private static final String CHARACTER_DEFENDER_ID = "attackResultCharacterDefenderId";
    private static final String CRITICAL_ID = "attackResultCriticalId";
    private static final String CRITICAL_LEVEL = "attackResultCriticalLevel";
    private static final String HIT_POINTS = "attackResultHitPoints";
    private static final String FUMBLED = "attackResultFumbled";

    private RPGCharacterAttack characterAttack;
    private RPGCharacter characterDefender;
    private Critical critical;
	private CriticalLevel criticalLevel;
    private int hitPoints;
	private boolean fumbled;
//	private boolean applied;

    public AttackResult() {
    }

    public AttackResult(Bundle bundle) {
        characterAttack = new RPGCharacterAttack();
        characterAttack.setId(bundle.getLong(CHARACTER_ATTACK_ID));
        long idDefender = bundle.getLong(CHARACTER_DEFENDER_ID, 0);
        if (idDefender > 0) {
            characterDefender = new RPGCharacter();
            characterDefender.setId(idDefender);
        }
        critical = new Critical();
        critical.setId(bundle.getLong(CRITICAL_ID));
        criticalLevel = CriticalLevel.fromInteger(bundle.getInt(CRITICAL_LEVEL));
        hitPoints = bundle.getInt(HIT_POINTS);
        fumbled = bundle.getBoolean(FUMBLED);
    }

//    public boolean isApplied() {
//        return applied;
//    }
//
//    public void setApplied(boolean applied) {
//        this.applied = applied;
//    }

    public RPGCharacterAttack getCharacterAttack() {
        return characterAttack;
    }

    public void setCharacterAttack(RPGCharacterAttack characterAttack) {
        this.characterAttack = characterAttack;
    }

    public RPGCharacter getCharacterDefender() {
        return characterDefender;
    }

    public void setCharacterDefender(RPGCharacter characterDefender) {
        this.characterDefender = characterDefender;
    }

    public Critical getCritical() {
        return critical;
    }

    public void setCritical(Critical critical) {
        this.critical = critical;
    }

    public CriticalLevel getCriticalLevel() {
        return criticalLevel;
    }

    public void setCriticalLevel(CriticalLevel criticalLevel) {
        this.criticalLevel = criticalLevel;
    }

    public boolean isFumbled() {
        return fumbled;
    }

    public void setFumbled(boolean fumbled) {
        this.fumbled = fumbled;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public void saveData(Bundle bundle) {
        bundle.putLong(CHARACTER_ATTACK_ID, characterAttack.getId());
        if (characterDefender != null) {
            bundle.putLong(CHARACTER_DEFENDER_ID, characterDefender.getId());
        }
        bundle.putLong(CRITICAL_ID, critical.getId());
        bundle.putInt(CRITICAL_LEVEL, criticalLevel.toInteger());
        bundle.putInt(HIT_POINTS, hitPoints);
        bundle.putBoolean(FUMBLED, fumbled);
    }
}
