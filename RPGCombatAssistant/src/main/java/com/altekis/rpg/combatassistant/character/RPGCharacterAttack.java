package com.altekis.rpg.combatassistant.character;

import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.db.DatabaseHelper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = DatabaseHelper.TABLE_CHARACTER_ATTACKS)
public class RPGCharacterAttack {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_CHARACTER_ID = "character_id";
    public static final String FIELD_ATTACK_ID = "attack_id";
    public static final String FIELD_BONUS = "bonus";

    @DatabaseField(generatedId = true, columnName = DatabaseHelper.FIELD_ID)
    private long id;
    @DatabaseField(columnName = FIELD_NAME)
    private String name;
    @DatabaseField(foreign = true, columnName = FIELD_CHARACTER_ID)
    private RPGCharacter RPGCharacter;
    @DatabaseField(foreign = true, foreignAutoRefresh = true,  columnName = FIELD_ATTACK_ID)
    private Attack attack;
    @DatabaseField(columnName = FIELD_BONUS)
    private int bonus;

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

    public RPGCharacter getRPGCharacter() {
        return RPGCharacter;
    }

    public void setRPGCharacter(RPGCharacter RPGCharacter) {
        this.RPGCharacter = RPGCharacter;
    }

    public Attack getAttack() {
        return attack;
    }

    public void setAttack(Attack attack) {
        this.attack = attack;
    }

    public int getBonus() {
        return bonus;
    }

    public void setBonus(int bonus) {
        this.bonus = bonus;
    }

    @Override
    public boolean equals(Object o) {
        boolean equals = false;
        if (o instanceof RPGCharacterAttack) {
            equals = (id == ((RPGCharacterAttack) o).getId());
        }
        return equals;
    }
}
