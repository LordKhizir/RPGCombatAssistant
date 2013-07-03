package com.altekis.rpg.combatassistant.attack;

import com.altekis.rpg.combatassistant.R;
import com.altekis.rpg.combatassistant.critical.Critical;
import com.altekis.rpg.combatassistant.db.DatabaseHelper;
import com.altekis.rpg.combatassistant.db.RuleSystem;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = DatabaseHelper.TABLE_ATTACK)
public class Attack {

    private static final String[] WEAPON_NAMES = {
            "weapon_arrowhead",
            "weapon_axe",
            "weapon_bite",
            "weapon_bow",
            "weapon_claw",
            "weapon_fist",
            "weapon_mace",
            "weapon_sword",
            "weapon_whip"
    };

    private static final int[] WEAPON_IDS = {
            R.drawable.weapon_arrowhead,
            R.drawable.weapon_axe,
            R.drawable.weapon_bite,
            R.drawable.weapon_bow,
            R.drawable.weapon_claws,
            R.drawable.weapon_fist,
            R.drawable.weapon_mace,
            R.drawable.weapon_sword,
            R.drawable.weapon_whip
    };

    public static final String FIELD_NAME = "name";
    public static final String FIELD_SYSTEM_ID = "system_id";
    public static final String FIELD_FUMBLE = "fumble";
    public static final String FIELD_CRITICAL_ID = "critical_id";
    public static final String FIELD_ICON = "icon";

    @DatabaseField(generatedId = true, columnName = DatabaseHelper.FIELD_ID)
    private long id;
    @DatabaseField(columnName = FIELD_NAME)
    private String name;
    @DatabaseField(foreign = true, columnName = FIELD_SYSTEM_ID)
    private RuleSystem ruleSystem;
    @DatabaseField(columnName = FIELD_FUMBLE)
    private int fumble;
    @DatabaseField(foreign = true, columnName = FIELD_CRITICAL_ID)
    private Critical critical;
    @DatabaseField(columnName = FIELD_ICON)
    private String icon;

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

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getWeaponIcon() {
        int resource = R.drawable.weapon_sword;
        if (icon != null) {
            for (int i = 0 ; i < WEAPON_NAMES.length ; i++) {
                if (WEAPON_NAMES[i].equals(icon)) {
                    resource = WEAPON_IDS[i];
                    break;
                }
            }
        }
        return resource;
    }
}
