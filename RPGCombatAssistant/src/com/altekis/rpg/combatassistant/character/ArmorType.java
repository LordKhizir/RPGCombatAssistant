package com.altekis.rpg.combatassistant.character;

public enum ArmorType {
	NoArmor			(0),
	SoftLeather 	(1),
	RigidLeather	(2),
	Chainmail		(3),
	Plate			(4);
	
	ArmorType (int armorType)
    {
        this.type = armorType;
    }

    private int type;

    public int toInteger()
    {
        return type;
    }
    
    public static ArmorType fromInteger(int armorTypeId) {
    	for (ArmorType armorType:ArmorType.values()) {
    		if (armorType.toInteger()==armorTypeId) {
    			return armorType;
    		}
    	}
		return null;
    }
}
