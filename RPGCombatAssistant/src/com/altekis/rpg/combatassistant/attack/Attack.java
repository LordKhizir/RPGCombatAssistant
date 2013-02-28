package com.altekis.rpg.combatassistant.attack;

import com.altekis.rpg.combatassistant.critical.Critical;
import com.altekis.rpg.combatassistant.db.DatabaseHelper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = DatabaseHelper.TABLE_ATTACK)
public class Attack {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_RM = "rolemaster_system";
    public static final String FIELD_FUMBLE = "fumble";
    public static final String FIELD_CRITICAL_ID = "critical_id";

    @DatabaseField(id = true, columnName = DatabaseHelper.FIELD_ID)
    private long id;
    @DatabaseField(columnName = FIELD_NAME)
    private String name;
    @DatabaseField(columnName = FIELD_RM)
    private boolean rolematerSystem;
    @DatabaseField(columnName = FIELD_FUMBLE)
    private int fumble;
    @DatabaseField(foreign = true, columnName = FIELD_CRITICAL_ID)
    private Critical critical;

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

    public boolean isRolematerSystem() {
        return rolematerSystem;
    }

    public void setRolematerSystem(boolean rolematerSystem) {
        this.rolematerSystem = rolematerSystem;
    }

    public int getFumble() {
        return fumble;
    }

    public void setFumble(int fumble) {
        this.fumble = fumble;
    }

    public Critical getCritical() {
        return critical;
    }

    public void setCritical(Critical critical) {
        this.critical = critical;
    }

    @Override
    public String toString() {
        return name;
    }
}
