package com.altekis.rpg.combatassistant.db;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;
import android.util.Log;
import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.attack.AttackTable;
import com.altekis.rpg.combatassistant.character.RPGCharacter;
import com.altekis.rpg.combatassistant.character.RPGCharacterAttack;
import com.altekis.rpg.combatassistant.critical.Critical;
import com.altekis.rpg.combatassistant.critical.CriticalTable;
import com.altekis.rpg.combatassistant.maneuver.MovingFumble;
import com.altekis.rpg.combatassistant.maneuver.MovingTable;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    public static final String DB_INITIALISED = "DB_INITIALISED";

    private static final String DATABASE_NAME = "rpg-combat-assistant-v11.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_SYSTEM = "db_system";
    public static final String TABLE_ATTACK = "db_attack";
    public static final String TABLE_ATTACK_TABLE = "db_attack_table";
    public static final String TABLE_CHARACTER = "db_character";
    public static final String TABLE_CHARACTER_ATTACKS = "db_character_attacks";
    public static final String TABLE_CRITICAL = "db_critical";
    public static final String TABLE_CRITICAL_TABLE = "db_critical_table";
    public static final String TABLE_MOVING_TABLE = "db_moving_table";
    public static final String TABLE_MOVING_FUMBLE = "db_moving_fumble";
    public static final String FIELD_ID = "_id";

    private Context context;
    private Dao<RuleSystem, Long> daoSystem;
    private Dao<Attack, Long> daoAttack;
    private Dao<AttackTable, Long> daoAttackTable;
    private Dao<RPGCharacter, Long> daoCharacter;
    private Dao<RPGCharacterAttack, Long> daoCharacterAttack;
    private Dao<Critical, Long> daoCritical;
    private Dao<CriticalTable, Long> daoCriticalTable;
    private Dao<MovingTable, Long> daoMovingManeuverTable;
    private Dao<MovingFumble, Long> daoMovingManeuverFumble;

	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
	}

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, RuleSystem.class);
            TableUtils.createTable(connectionSource, Critical.class);
            TableUtils.createTable(connectionSource, CriticalTable.class);
            TableUtils.createTable(connectionSource, Attack.class);
            TableUtils.createTable(connectionSource, AttackTable.class);
            TableUtils.createTable(connectionSource, RPGCharacter.class);
            TableUtils.createTable(connectionSource, RPGCharacterAttack.class);
            TableUtils.createTable(connectionSource, MovingTable.class);
            TableUtils.createTable(connectionSource, MovingFumble.class);
            // Set initialised to false
            SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
            sp.edit().putBoolean(DB_INITIALISED, false).commit();
        } catch (SQLException e) {
            Log.e(DatabaseHelper.class.getName(), "Can't create database", e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        if (oldVersion == 1) {
            // legacy Database
            onCreate(sqLiteDatabase, connectionSource);
            Cursor cursor = sqLiteDatabase.query("characters",
                    new String[] {"_id", "name", "playerName", "hitPoints", "maxHitPoints", "armorType" },
                    null, null, null, null, null);
            ContentValues values = new ContentValues();
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    values.put(DatabaseHelper.FIELD_ID, cursor.getLong(0));
                    values.put(RPGCharacter.FIELD_NAME, cursor.getString(1));
                    values.put(RPGCharacter.FIELD_PLAYER_NAME, cursor.getString(2));
                    values.put(RPGCharacter.FIELD_HIT_POINTS, cursor.getInt(3));
                    values.put(RPGCharacter.FIELD_MAX_HIT_POINTS, cursor.getInt(4));
                    values.put(RPGCharacter.FIELD_ARMOR_TYPE, legacyArmorType(cursor.getInt(5)));
                    sqLiteDatabase.insert(DatabaseHelper.TABLE_CHARACTER, null, values);
                }
                cursor.close();
            }

            cursor = sqLiteDatabase.query("attacks",
                    new String[] {"_id", "characterId", "name", "attackType", "bonus" },
                    null, null, null, null, null);
            if (cursor != null) {
                values.clear();
                while (cursor.moveToNext()) {
                    values.put(DatabaseHelper.FIELD_ID, cursor.getLong(0));
                    values.put(RPGCharacterAttack.FIELD_CHARACTER_ID, cursor.getLong(1));
                    values.put(RPGCharacterAttack.FIELD_NAME, cursor.getString(2));
                    values.put(RPGCharacterAttack.FIELD_ATTACK_ID, legacyAttackType(cursor.getString(3)));
                    values.put(RPGCharacterAttack.FIELD_BONUS, cursor.getInt(4));
                    sqLiteDatabase.insert(DatabaseHelper.TABLE_CHARACTER_ATTACKS, null, values);
                }
                cursor.close();
            }
        }
    }

    private long legacyAttackType(String previousAttackType) {
        long id;
        if ("S".equals(previousAttackType)) {
            // Filo
            id = 1;
        } else if ("K".equals(previousAttackType)) {
            // Contundentes
            id = 2;
        } else if ("2H".equals(previousAttackType)) {
            // A dos manos
            id = 3;
        } else if ("".equals(previousAttackType)) {
            //  Proyectiles
            id = 4;
        } else if ("G".equals(previousAttackType)) {
            // Agarrar y desequilibrar
            id = 5;
        } else {
            // Garras y dientes
            id = 6;
        }
        return id;
    }

    private int legacyArmorType(int previousArmorType) {
        int newValue;
        switch (previousArmorType) {
            case 0:
                newValue = 1;
                break;
            case 1:
                newValue = 5;
                break;
            case 2:
                newValue = 10;
                break;
            case 3:
                newValue = 15;
                break;
            default:
                newValue = 20;
        }
        return newValue;
    }

    public Dao<RuleSystem, Long> getDaoSystem() throws SQLException {
        if (daoSystem == null) {
            daoSystem = getDao(RuleSystem.class);
        }
        return daoSystem;
    }

    public Dao<Attack, Long> getDaoAttack() throws SQLException {
        if (daoAttack == null) {
            daoAttack = getDao(Attack.class);
        }
        return daoAttack;
    }

    public Dao<AttackTable, Long> getDaoAttackTable() throws SQLException {
        if (daoAttackTable == null) {
            daoAttackTable = getDao(AttackTable.class);
        }
        return daoAttackTable;
    }

    public Dao<RPGCharacter, Long> getDaoRPGCharacter() throws SQLException {
        if (daoCharacter == null) {
            daoCharacter = getDao(RPGCharacter.class);
        }
        return daoCharacter;
    }

    public Dao<RPGCharacterAttack, Long> getDaoRPGCharacterAttack() throws SQLException {
        if (daoCharacterAttack == null) {
            daoCharacterAttack = getDao(RPGCharacterAttack.class);
        }
        return daoCharacterAttack;
    }

    public Dao<Critical, Long> getDaoCritical() throws SQLException {
        if (daoCritical == null) {
            daoCritical = getDao(Critical.class);
        }
        return daoCritical;
    }

    public Dao<CriticalTable, Long> getDaoCriticalTable() throws SQLException {
        if (daoCriticalTable == null) {
            daoCriticalTable = getDao(CriticalTable.class);
        }
        return daoCriticalTable;
    }

    public Dao<MovingTable, Long> getDaoMovingTable() throws SQLException {
        if (daoMovingManeuverTable == null) {
            daoMovingManeuverTable = getDao(MovingTable.class);
        }
        return daoMovingManeuverTable;
    }

    public Dao<MovingFumble, Long> getDaoMovingFumble() throws SQLException {
        if (daoMovingManeuverFumble == null) {
            daoMovingManeuverFumble = getDao(MovingFumble.class);
        }
        return daoMovingManeuverFumble;
    }
}
