package com.altekis.rpg.combatassistant.character;

import android.content.Context;
import com.altekis.rpg.combatassistant.R;
import com.altekis.rpg.combatassistant.db.DatabaseHelper;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;

@DatabaseTable(tableName = DatabaseHelper.TABLE_CHARACTER)
public class RPGCharacter {

    public static final String FIELD_NAME = "name";
    public static final String FIELD_PLAYER_NAME = "player_name";
    public static final String FIELD_MAX_HIT_POINTS = "max_hit_points";
    public static final String FIELD_HIT_POINTS = "hit_points";
    public static final String FIELD_ARMOR_TYPE = "armor_type";
    public static final String FIELD_NPC = "npc";

    @DatabaseField(generatedId = true, columnName = DatabaseHelper.FIELD_ID)
    private long id;
    @DatabaseField(columnName = FIELD_NAME)
    private String name;
    @DatabaseField(columnName = FIELD_PLAYER_NAME)
    private String playerName;
    @DatabaseField(columnName = FIELD_MAX_HIT_POINTS)
    private int maxHitPoints;
    @DatabaseField(columnName = FIELD_HIT_POINTS)
    private int hitPoints;
    @DatabaseField(columnName = FIELD_ARMOR_TYPE)
    private int armorType;
    @DatabaseField(columnName = FIELD_NPC)
    private boolean pnj;

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

    public String getPlayerName() {
        return playerName;
    }

    public void setPlayerName(String playerName) {
        this.playerName = playerName;
    }

    public int getMaxHitPoints() {
        return maxHitPoints;
    }

    public void setMaxHitPoints(int maxHitPoints) {
        this.maxHitPoints = maxHitPoints;
    }

    public int getHitPoints() {
        return hitPoints;
    }

    public void setHitPoints(int hitPoints) {
        this.hitPoints = hitPoints;
    }

    public int getArmorType() {
        return armorType;
    }

    public void setArmorType(int armorType) {
        this.armorType = armorType;
    }

    public boolean isPnj() {
        return pnj;
    }

    public void setPnj(boolean pnj) {
        this.pnj = pnj;
    }

    public String getStringName(Context ctx) {
        if (pnj) {
            return ctx.getString(R.string.character_name_npc, name);
        } else {
            return ctx.getString(R.string.character_name, name, playerName);
        }
    }
}
