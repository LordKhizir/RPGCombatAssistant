package com.altekis.rpg.combatassistant.maneuver;

import com.altekis.rpg.combatassistant.db.DatabaseHelper;
import com.altekis.rpg.combatassistant.db.RuleSystem;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = DatabaseHelper.TABLE_MOVING_FUMBLE)
public class MovingFumble {

    public static final String FIELD_SYSTEM_ID = "rule_system_id";
    public static final String FIELD_MINIMUM = "minimum_value";
    public static final String FIELD_RESULT = "result";

    @DatabaseField(generatedId = true, columnName = DatabaseHelper.FIELD_ID)
    private long id;
    @DatabaseField(foreign = true, columnName = FIELD_SYSTEM_ID)
    private RuleSystem ruleSystem;
    @DatabaseField(columnName = FIELD_MINIMUM)
    private int minimumValue;
    @DatabaseField(columnName = FIELD_RESULT)
    private String result;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public RuleSystem getRuleSystem() {
        return ruleSystem;
    }

    public void setRuleSystem(RuleSystem ruleSystem) {
        this.ruleSystem = ruleSystem;
    }

    public int getMinimumValue() {
        return minimumValue;
    }

    public void setMinimumValue(int minimumValue) {
        this.minimumValue = minimumValue;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
