package com.altekis.rpg.combatassistant.critical;

import com.altekis.rpg.combatassistant.db.DatabaseHelper;
import com.altekis.rpg.combatassistant.db.RuleSystem;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = DatabaseHelper.TABLE_CRITICAL)
public class Critical {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_SYSTEM_ID = "system_id";

    @DatabaseField(generatedId = true, columnName = DatabaseHelper.FIELD_ID)
    private long id;
    @DatabaseField(columnName = FIELD_NAME)
    private String name;
    @DatabaseField(foreign = true, columnName = FIELD_SYSTEM_ID)
    private RuleSystem ruleSystem;

    public Critical() {
    }

    public Critical(long id, String name) {
        this.id = id;
        this.name = name;
    }

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

    public RuleSystem getRuleSystem() {
        return ruleSystem;
    }

    public void setRuleSystem(RuleSystem ruleSystem) {
        this.ruleSystem = ruleSystem;
    }

    @Override
    public String toString() {
        return name;
    }
}
