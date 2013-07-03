package com.altekis.rpg.combatassistant.attack;

import com.altekis.rpg.combatassistant.db.DatabaseHelper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = DatabaseHelper.TABLE_ATTACK_TABLE)
public class AttackTable {

    public static final String FIELD_ATTACK_ID = "attack_id";
    public static final String FIELD_MINIMUM = "minimum_value";
    public static final String FIELD_ARMOR_TYPE = "armor_type_";

    @DatabaseField(generatedId = true, columnName = DatabaseHelper.FIELD_ID)
    private long id;
    @DatabaseField(foreign = true, columnName = FIELD_ATTACK_ID)
    private Attack attack;
    @DatabaseField(columnName = FIELD_MINIMUM)
    private int minimumValue;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "1")
    private String armorType1;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "2")
    private String armorType2;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "3")
    private String armorType3;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "4")
    private String armorType4;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "5")
    private String armorType5;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "6")
    private String armorType6;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "7")
    private String armorType7;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "8")
    private String armorType8;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "9")
    private String armorType9;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "10")
    private String armorType10;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "11")
    private String armorType11;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "12")
    private String armorType12;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "13")
    private String armorType13;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "14")
    private String armorType14;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "15")
    private String armorType15;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "16")
    private String armorType16;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "17")
    private String armorType17;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "18")
    private String armorType18;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "19")
    private String armorType19;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE + "20")
    private String armorType20;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Attack getAttack() {
        return attack;
    }

    public void setAttack(Attack attack) {
        this.attack = attack;
    }

    public int getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(int minimumValue) {
        this.minimumValue = minimumValue;
    }

    public String getArmorType1() {
        return armorType1;
    }

    public void setArmorType1(String armorType1) {
        this.armorType1 = armorType1;
    }

    public String getArmorType2() {
        return armorType2;
    }

    public void setArmorType2(String armorType2) {
        this.armorType2 = armorType2;
    }

    public String getArmorType3() {
        return armorType3;
    }

    public void setArmorType3(String armorType3) {
        this.armorType3 = armorType3;
    }

    public String getArmorType4() {
        return armorType4;
    }

    public void setArmorType4(String armorType4) {
        this.armorType4 = armorType4;
    }

    public String getArmorType5() {
        return armorType5;
    }

    public void setArmorType5(String armorType5) {
        this.armorType5 = armorType5;
    }

    public String getArmorType6() {
        return armorType6;
    }

    public void setArmorType6(String armorType6) {
        this.armorType6 = armorType6;
    }

    public String getArmorType7() {
        return armorType7;
    }

    public void setArmorType7(String armorType7) {
        this.armorType7 = armorType7;
    }

    public String getArmorType8() {
        return armorType8;
    }

    public void setArmorType8(String armorType8) {
        this.armorType8 = armorType8;
    }

    public String getArmorType9() {
        return armorType9;
    }

    public void setArmorType9(String armorType9) {
        this.armorType9 = armorType9;
    }

    public String getArmorType10() {
        return armorType10;
    }

    public void setArmorType10(String armorType10) {
        this.armorType10 = armorType10;
    }

    public String getArmorType11() {
        return armorType11;
    }

    public void setArmorType11(String armorType11) {
        this.armorType11 = armorType11;
    }

    public String getArmorType12() {
        return armorType12;
    }

    public void setArmorType12(String armorType12) {
        this.armorType12 = armorType12;
    }

    public String getArmorType13() {
        return armorType13;
    }

    public void setArmorType13(String armorType13) {
        this.armorType13 = armorType13;
    }

    public String getArmorType14() {
        return armorType14;
    }

    public void setArmorType14(String armorType14) {
        this.armorType14 = armorType14;
    }

    public String getArmorType15() {
        return armorType15;
    }

    public void setArmorType15(String armorType15) {
        this.armorType15 = armorType15;
    }

    public String getArmorType16() {
        return armorType16;
    }

    public void setArmorType16(String armorType16) {
        this.armorType16 = armorType16;
    }

    public String getArmorType17() {
        return armorType17;
    }

    public void setArmorType17(String armorType17) {
        this.armorType17 = armorType17;
    }

    public String getArmorType18() {
        return armorType18;
    }

    public void setArmorType18(String armorType18) {
        this.armorType18 = armorType18;
    }

    public String getArmorType19() {
        return armorType19;
    }

    public void setArmorType19(String armorType19) {
        this.armorType19 = armorType19;
    }

    public String getArmorType20() {
        return armorType20;
    }

    public void setArmorType20(String armorType20) {
        this.armorType20 = armorType20;
    }
}
