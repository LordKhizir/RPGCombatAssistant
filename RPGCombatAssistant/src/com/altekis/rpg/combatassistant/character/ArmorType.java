package com.altekis.rpg.combatassistant.character;

import com.altekis.rpg.combatassistant.R;

public enum ArmorType {
	TP1(  1,  1, R.string.armor_merp_1,  R.string.armor_rm_1),
	TP2(  2,  1, 0,                      R.string.armor_rm_2),
	TP3(  3,  1, 0,                      R.string.armor_rm_3),
	TP4(  4,  1, 0,                      R.string.armor_rm_4),
	TP5(  5,  5, R.string.armor_merp_5,  R.string.armor_rm_5),
	TP6(  6,  5, 0,                      R.string.armor_rm_6),
	TP7(  7,  5, 0,                      R.string.armor_rm_7),
	TP8(  8,  5, 0,                      R.string.armor_rm_8),
	TP9(  9, 10, 0,                      R.string.armor_rm_9),
	TP10(10, 10, R.string.armor_merp_10, R.string.armor_rm_10),
	TP11(11, 10, 0,                      R.string.armor_rm_11),
	TP12(12, 10, 0,                      R.string.armor_rm_12),
	TP13(13, 15, 0,                      R.string.armor_rm_13),
	TP14(14, 15, 0,                      R.string.armor_rm_14),
	TP15(15, 15, R.string.armor_merp_15, R.string.armor_rm_15),
	TP16(16, 15, 0,                      R.string.armor_rm_16),
	TP17(17, 20, 0,                      R.string.armor_rm_17),
	TP18(18, 20, 0,                      R.string.armor_rm_18),
	TP19(19, 20, 0,                      R.string.armor_rm_19),
	TP20(20, 20, R.string.armor_merp_20, R.string.armor_rm_20);

    private ArmorType(int armor, int merpArmor, int merpString, int rmString) {
        this.armor = armor;
        this.merpArmor = merpArmor;
        this.merpString = merpString;
        this.rmString = rmString;
    }

    private int armor;
    private int merpArmor;
    private int merpString;
    private int rmString;

    public int getArmor() {
        return armor;
    }

    public int getMerpArmor() {
        return merpArmor;
    }

    public int getMerpString() {
        return merpString;
    }

    public int getRmString() {
        return rmString;
    }

    public ArmorType getMerp() {
        if (armor >= 1 && armor <= 4) {
            return TP1;
        } else if (armor >= 5 && armor <= 8) {
            return TP5;
        } else if (armor >= 8 && armor <= 12) {
            return TP10;
        } else if (armor >= 13 && armor <= 16) {
            return TP15;
        } else /*if (armor >= 17 && armor <= 20)*/ {
            return TP20;
        }
    }

    public static ArmorType fromInteger(int armor) {
    	for (ArmorType armorType : ArmorType.values()) {
    		if (armorType.armor == armor) {
    			return armorType;
    		}
    	}
		return null;
    }

    public static ArmorType[] getArmorTypes(boolean rolemaster) {
        if (rolemaster) {
            return values();
        } else {
            return new ArmorType[]{ArmorType.TP1, ArmorType.TP5, ArmorType.TP10, ArmorType.TP15, ArmorType.TP20};
        }
    }
}
