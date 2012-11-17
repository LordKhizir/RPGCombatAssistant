package com.altekis.rpg.combatassistant.critical;

public enum CriticalLevel {
	Tiny	(1),
	A		(2),
	B		(3),
	C		(4),
	D		(5),
	E		(6);
	
	CriticalLevel (int criticalLevel)
    {
        this.type = criticalLevel;
    }

    private int type;

    public int toInteger()
    {
        return type;
    }
}
