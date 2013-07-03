package com.altekis.rpg.combatassistant.maneuver;

import com.altekis.rpg.combatassistant.db.DatabaseHelper;
import com.altekis.rpg.combatassistant.db.RuleSystem;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = DatabaseHelper.TABLE_MOVING_TABLE)
public class MovingTable {

    public static final String FIELD_SYSTEM_ID = "rule_system_id";
    public static final String FIELD_MINIMUM = "minimum_value";
    public static final String FIELD_DIFF_ROUTINE = "diff_routine";
    public static final String FIELD_DIFF_EASY = "diff_easy";
    public static final String FIELD_DIFF_LIGHT = "diff_light";
    public static final String FIELD_DIFF_MEDIUM = "diff_medium";
    public static final String FIELD_DIFF_VERY_HARD = "diff_very_hard";
    public static final String FIELD_DIFF_EXTREMELY_HARD = "diff_extremely_hard";
    public static final String FIELD_DIFF_SHEER_HARD = "diff_sheer_hard";
    public static final String FIELD_DIFF_FOLLY = "diff_folly";
    public static final String FIELD_DIFF_ABSURD = "diff_absurd";

    @DatabaseField(generatedId = true, columnName = DatabaseHelper.FIELD_ID)
    private long id;
    @DatabaseField(foreign = true, columnName = FIELD_SYSTEM_ID)
    private RuleSystem ruleSystem;
    @DatabaseField(columnName = FIELD_MINIMUM)
    private int minimumValue;
    @DatabaseField(columnName = FIELD_DIFF_ROUTINE)
    private String valueRoutine;
    @DatabaseField(columnName = FIELD_DIFF_EASY)
    private String valueEasy;
    @DatabaseField(columnName = FIELD_DIFF_LIGHT)
    private String valueLight;
    @DatabaseField(columnName = FIELD_DIFF_MEDIUM)
    private String valueMedium;
    @DatabaseField(columnName = FIELD_DIFF_VERY_HARD)
    private String valueVeryHard;
    @DatabaseField(columnName = FIELD_DIFF_EXTREMELY_HARD)
    private String valueExtremelyHard;
    @DatabaseField(columnName = FIELD_DIFF_SHEER_HARD)
    private String valueSheerHard;
    @DatabaseField(columnName = FIELD_DIFF_FOLLY)
    private String valueFolly;
    @DatabaseField(columnName = FIELD_DIFF_ABSURD)
    private String valueAbsurd;

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

    public String getValueAbsurd() {
        return valueAbsurd;
    }

    public void setValueAbsurd(String valueAbsurd) {
        this.valueAbsurd = valueAbsurd;
    }

    public String getValueEasy() {
        return valueEasy;
    }

    public void setValueEasy(String valueEasy) {
        this.valueEasy = valueEasy;
    }

    public String getValueExtremelyHard() {
        return valueExtremelyHard;
    }

    public void setValueExtremelyHard(String valueExtremelyHard) {
        this.valueExtremelyHard = valueExtremelyHard;
    }

    public String getValueFolly() {
        return valueFolly;
    }

    public void setValueFolly(String valueFolly) {
        this.valueFolly = valueFolly;
    }

    public String getValueLight() {
        return valueLight;
    }

    public void setValueLight(String valueLight) {
        this.valueLight = valueLight;
    }

    public String getValueMedium() {
        return valueMedium;
    }

    public void setValueMedium(String valueMedium) {
        this.valueMedium = valueMedium;
    }

    public String getValueRoutine() {
        return valueRoutine;
    }

    public void setValueRoutine(String valueRoutine) {
        this.valueRoutine = valueRoutine;
    }

    public String getValueSheerHard() {
        return valueSheerHard;
    }

    public void setValueSheerHard(String valueSheerHard) {
        this.valueSheerHard = valueSheerHard;
    }

    public String getValueVeryHard() {
        return valueVeryHard;
    }

    public void setValueVeryHard(String valueVeryHard) {
        this.valueVeryHard = valueVeryHard;
    }
}
