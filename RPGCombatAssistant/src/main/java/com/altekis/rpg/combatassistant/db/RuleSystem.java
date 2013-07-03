package com.altekis.rpg.combatassistant.db;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = DatabaseHelper.TABLE_SYSTEM)
public class RuleSystem {

    public static final int ARMOR_SIMPLE = 0;
    public static final int ARMOR_COMPLETE = 1;
    public static final int CRITICAL_SIMPLE = 0;
    public static final int CRITICAL_COMPLETE = 1;
    public static final int MOVING_FUMBLE = 0;
    public static final int MOVING_NO_FUMBLE = 1;

    public static final String FIELD_NAME = "name";
    public static final String FIELD_ARMOR_TYPE = "armor";
    public static final String FIELD_CRITICAL_TYPE = "critical";
    public static final String FIELD_MOVING_TYPE = "moving";

    @DatabaseField(generatedId = true, columnName = DatabaseHelper.FIELD_ID)
    private long id;
    @DatabaseField(columnName = FIELD_NAME)
    private String name;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE)
    private int armorType;
    @DatabaseField(columnName = FIELD_CRITICAL_TYPE)
    private int criticalType;
    @DatabaseField(columnName = FIELD_MOVING_TYPE)
    private int movingType;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getArmorType() {
        return armorType;
    }

    public void setArmorType(int armorType) {
        this.armorType = armorType;
    }

    public int getCriticalType() {
        return criticalType;
    }

    public void setCriticalType(int criticalType) {
        this.criticalType = criticalType;
    }

    public int getMovingType() {
        return movingType;
    }

    public void setMovingType(int movingType) {
        this.movingType = movingType;
    }

    @Override
    public String toString() {
        return name;
    }
}
