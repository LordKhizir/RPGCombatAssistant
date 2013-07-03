package com.altekis.rpg.combatassistant.attack;

import com.altekis.rpg.combatassistant.character.RPGCharacterAttack;

import java.util.Comparator;

public class AttackComparator implements Comparator<RPGCharacterAttack> {

    private long idSystem;

    public AttackComparator(long idSystem) {
        this.idSystem = idSystem;
    }

    @Override
    public int compare(RPGCharacterAttack lhs, RPGCharacterAttack rhs) {
        int result = 0;
        if (lhs != null && rhs != null) {
            if (lhs.getAttack().getRuleSystem().getId() == idSystem) {
                if (rhs.getAttack().getRuleSystem().getId() != idSystem) {
                    result = -1;
                }
            } else if (rhs.getAttack().getRuleSystem().getId() == idSystem) {
                result = 1;
            }
            if (result == 0) {
                result = lhs.getName().compareTo(rhs.getName());
            }
        }
        return result;
    }
}