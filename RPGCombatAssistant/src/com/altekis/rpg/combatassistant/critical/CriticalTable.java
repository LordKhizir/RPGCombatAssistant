package com.altekis.rpg.combatassistant.critical;

import com.altekis.rpg.combatassistant.db.DatabaseHelper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = DatabaseHelper.TABLE_CRITICAL_TABLE)
public class CriticalTable {

    public static final String FIELD_CRITICAL_ID = "critical_id";
    public static final String FIELD_MINIMUM = "minimum_value";
    public static final String FIELD_TYPE_A = "typeA";
    public static final String FIELD_TYPE_B = "typeB";
    public static final String FIELD_TYPE_C = "typeC";
    public static final String FIELD_TYPE_D = "typeD";
    public static final String FIELD_TYPE_E = "typeE";

    @DatabaseField(id = true, columnName = DatabaseHelper.FIELD_ID)
    private long id;
    @DatabaseField(foreign = true, columnName = FIELD_CRITICAL_ID)
    private Critical critical;
    @DatabaseField(columnName = FIELD_MINIMUM)
    private int minimumValue;
    @DatabaseField(columnName = FIELD_TYPE_A)
    private String typeA;
    @DatabaseField(columnName = FIELD_TYPE_B)
    private String typeB;
    @DatabaseField(columnName = FIELD_TYPE_C)
    private String typeC;
    @DatabaseField(columnName = FIELD_TYPE_D)
    private String typeD;
    @DatabaseField(columnName = FIELD_TYPE_E)
    private String typeE;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public Critical getCritical() {
        return critical;
    }

    public void setCritical(Critical critical) {
        this.critical = critical;
    }

    public int getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(int minimumValue) {
        this.minimumValue = minimumValue;
    }

    public String getTypeA() {
        return typeA;
    }

    public void setTypeA(String typeA) {
        this.typeA = typeA;
    }

    public String getTypeB() {
        return typeB;
    }

    public void setTypeB(String typeB) {
        this.typeB = typeB;
    }

    public String getTypeC() {
        return typeC;
    }

    public void setTypeC(String typeC) {
        this.typeC = typeC;
    }

    public String getTypeD() {
        return typeD;
    }

    public void setTypeD(String typeD) {
        this.typeD = typeD;
    }

    public String getTypeE() {
        return typeE;
    }

    public void setTypeE(String typeE) {
        this.typeE = typeE;
    }
}
