package com.altekis.rpg.combatassistant.critical;


public enum CriticalLevel {
    T(1),
    A(2),
    B(3),
    C(4),
    D(5),
    E(6);

    private CriticalLevel(int criticalLevel) {
        this.type = criticalLevel;
    }

    private int type;

    public int toInteger() {
        return type;
    }

    public static CriticalLevel fromInteger(int type) {
        CriticalLevel[] array = CriticalLevel.values();
        for (int i = 0 ; i < array.length ; i++) {
            if (array[i].toInteger() == type) {
                return array[i];
            }
        }
        return A;
    }
}
