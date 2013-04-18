package com.altekis.rpg.combatassistant.db;

import android.util.Log;
import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.attack.AttackResult;
import com.altekis.rpg.combatassistant.attack.AttackTable;
import com.altekis.rpg.combatassistant.character.ArmorType;
import com.altekis.rpg.combatassistant.critical.Critical;
import com.altekis.rpg.combatassistant.critical.CriticalLevel;
import com.altekis.rpg.combatassistant.critical.CriticalTable;
import com.altekis.rpg.combatassistant.maneuver.DifficultyType;
import com.altekis.rpg.combatassistant.maneuver.MovingFumble;
import com.altekis.rpg.combatassistant.maneuver.MovingResult;
import com.altekis.rpg.combatassistant.maneuver.MovingTable;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;

public final class DBUtil {

    private DBUtil() {

    }

    public static AttackResult getValue(DatabaseHelper dbHelper, Attack attack, int roll, int total, ArmorType armorType) {
        AttackResult attackResult = new AttackResult();
        // Check if fumble
        if (roll <= attack.getFumble()) {
            // If fumble, just stop
			attackResult.setFumbled(true);
        } else {
			// Not fumbled... check for results
            // Get the first attack with id = attack.getId and total >= minimum
            try {
                Dao<Critical, Long> daoC = dbHelper.getDaoCritical();
                Dao<AttackTable, Long> dao = dbHelper.getDaoAttackTable();
                QueryBuilder<AttackTable, Long> qb = dao.queryBuilder();
                qb.setWhere(qb.where()
                        .eq(AttackTable.FIELD_ATTACK_ID, attack.getId())
                        .and()
                        .le(AttackTable.FIELD_MINIMUM, total));
                qb.orderBy(AttackTable.FIELD_MINIMUM, false);
                PreparedQuery<AttackTable> pq = qb.prepare();
                AttackTable attackTable = dao.queryForFirst(pq);
                if (attackTable != null) {
                    String wholeResult = chooseAttack(attackTable, armorType);
                    String[] parts = wholeResult.split("-");
                    attackResult.setHitPoints(Integer.parseInt(parts[0]));
                    if (parts.length > 1) {
                        attackResult.setCriticalLevel(CriticalLevel.valueOf(parts[1]));
                        Critical c = null;
                        if (parts.length == 3) {
                            // We have the critical id
                            c = daoC.queryForId(Long.parseLong(parts[2]));
                        }

                        if (c == null) {
                            // Get the default attack critical
                            attackResult.setCritical(attack.getCritical());
                            daoC.refresh(attackResult.getCritical());
                        } else {
                            attackResult.setCritical(c);
                        }
                    }
                }
            } catch (SQLException e) {
                Log.e("RPGCombatAssistant", "Can't read database", e);
            }
		}
		return attackResult;
    }

    public static String getCritical(DatabaseHelper dbHelper, Critical critical, CriticalLevel criticalLevel, int result) {
        String criticalResult = "";
        int total = result;
        // Get the first critical with total >= minumum and id_critical = critical.getId
        try {
            dbHelper.getDaoSystem().refresh(critical.getRuleSystem());
            if (critical.getRuleSystem().getCriticalType() == RuleSystem.CRITICAL_SIMPLE) {
                total = applySimpleCritical(total, criticalLevel);
            }
            Dao<CriticalTable, Long> dao = dbHelper.getDaoCriticalTable();
            QueryBuilder<CriticalTable, Long> qb = dao.queryBuilder();
            qb.setWhere(qb.where()
                    .eq(CriticalTable.FIELD_CRITICAL_ID, critical.getId())
                    .and()
                    .le(CriticalTable.FIELD_MINIMUM, total));
            qb.orderBy(CriticalTable.FIELD_MINIMUM, false);
            CriticalTable criticalTable = dao.queryForFirst(qb.prepare());
            if (criticalTable != null) {
                criticalResult = readCritical(critical.getRuleSystem().getCriticalType(), criticalLevel, criticalTable);
            }
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Can't read database", e);
        }
        return criticalResult;
    }

    public static MovingResult getMoving(DatabaseHelper dbHelper, RuleSystem system, DifficultyType difficultyType, int total) {
        MovingResult movingResult = null;
        try {
            Dao<MovingTable, Long> dao = dbHelper.getDaoMovingTable();
            QueryBuilder<MovingTable, Long> qb = dao.queryBuilder();
            qb.setWhere(qb.where()
                    .eq(MovingTable.FIELD_SYSTEM_ID, system.getId())
                    .and()
                    .le(MovingTable.FIELD_MINIMUM, total));
            qb.orderBy(MovingTable.FIELD_MINIMUM, false);
            MovingTable movingTable = dao.queryForFirst(qb.prepare());
            if (movingTable == null) {
                // TODO - check if total is lower than minimun or higher than maximun
            } else {
                movingResult = new MovingResult(readMoving(difficultyType, movingTable));
            }
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Can't read database", e);
        }
        if (movingResult == null) {
            movingResult = new MovingResult(MovingResult.FUMBLE);
        }
        return movingResult;
    }

    public static String getMovingFumble(DatabaseHelper dbHelper, RuleSystem system, DifficultyType difficultyType, int total) {
        String fumbleResult = null;
        int result = applyMovingFumble(total, difficultyType);
        try {
            Dao<MovingFumble, Long> dao = dbHelper.getDaoMovingFumble();
            QueryBuilder<MovingFumble, Long> qb = dao.queryBuilder();
            qb.setWhere(qb.where()
                    .eq(MovingFumble.FIELD_SYSTEM_ID, system.getId())
                    .and()
                    .le(MovingFumble.FIELD_MINIMUM, result));
            qb.orderBy(MovingFumble.FIELD_MINIMUM, false);
            MovingFumble movingFumble = dao.queryForFirst(qb.prepare());
            if (movingFumble != null) {
                fumbleResult = movingFumble.getResult();
            }
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Can't read database", e);
        }
        return fumbleResult;
    }

    private static String readMoving(DifficultyType difficultyType, MovingTable movingTable) {
        String result = "";
        if (difficultyType == DifficultyType.ROUTINE) {
            result = movingTable.getValueRoutine();
        } else if (difficultyType == DifficultyType.EASY) {
            result = movingTable.getValueEasy();
        } else if (difficultyType == DifficultyType.LIGHT) {
            result = movingTable.getValueLight();
        } else if (difficultyType == DifficultyType.MEDIUM) {
            result = movingTable.getValueMedium();
        } else if (difficultyType == DifficultyType.VERY_HARD) {
            result = movingTable.getValueVeryHard();
        } else if (difficultyType == DifficultyType.EXTREMELY_HARD) {
            result = movingTable.getValueExtremelyHard();
        } else if (difficultyType == DifficultyType.SHEER_HARD) {
            result = movingTable.getValueSheerHard();
        } else if (difficultyType == DifficultyType.FOLLY) {
            result = movingTable.getValueFolly();
        } else if (difficultyType == DifficultyType.ABSURD) {
            result = movingTable.getValueAbsurd();
        }
        return result;
    }

    private static int applySimpleCritical(int total, CriticalLevel criticalLevel) {
        int result = total;
        if (criticalLevel == CriticalLevel.T) {
            result -= 50;
        } else if (criticalLevel == CriticalLevel.A) {
            result -= 20;
        } else if (criticalLevel == CriticalLevel.B) {
            result -= 10;
        } else if (criticalLevel == CriticalLevel.C) {
            // it's ok
        } else if (criticalLevel == CriticalLevel.D) {
            result += 10;
        } else if (criticalLevel == CriticalLevel.E) {
            result += 20;
        }
        return result;
    }

    private static int applyMovingFumble(int total, DifficultyType difficultyType) {
        int result = total;
        if (difficultyType == DifficultyType.ROUTINE) {
            result += -50;
        } else if (difficultyType == DifficultyType.EASY) {
            result += -35;
        } else if (difficultyType == DifficultyType.LIGHT) {
            result += -20;
        } else if (difficultyType == DifficultyType.MEDIUM) {
            result += -10;
        } else if (difficultyType == DifficultyType.VERY_HARD) {
            // Igual
        } else if (difficultyType == DifficultyType.EXTREMELY_HARD) {
            result += 5;
        } else if (difficultyType == DifficultyType.SHEER_HARD) {
            result += 10;
        } else if (difficultyType == DifficultyType.FOLLY) {
            result += 15;
        } else if (difficultyType == DifficultyType.ABSURD) {
            result += 20;
        }
        return result;
    }

    private static String readCritical(int criticalType, CriticalLevel criticalLevel, CriticalTable criticalTable) {
        String result;
        if (criticalType == RuleSystem.CRITICAL_SIMPLE) {
            result = criticalTable.getTypeA();
        } else {
            if (criticalLevel == CriticalLevel.A) {
                result = criticalTable.getTypeA();
            } else if (criticalLevel == CriticalLevel.B) {
                result = criticalTable.getTypeB();
            } else if (criticalLevel == CriticalLevel.C) {
                result = criticalTable.getTypeC();
            } else if (criticalLevel == CriticalLevel.D) {
                result = criticalTable.getTypeD();
            } else {
                result = criticalTable.getTypeE();
            }
        }
        return result;
    }

    private static String chooseAttack(AttackTable attackTable, ArmorType armorType) {
        if (armorType == ArmorType.TP1) {
            return attackTable.getArmorType1();
        } else if (armorType == ArmorType.TP2) {
            return attackTable.getArmorType2();
        } else if (armorType == ArmorType.TP3) {
            return attackTable.getArmorType3();
        } else if (armorType == ArmorType.TP4) {
            return attackTable.getArmorType4();
        } else if (armorType == ArmorType.TP5) {
            return attackTable.getArmorType5();
        } else if (armorType == ArmorType.TP6) {
            return attackTable.getArmorType6();
        } else if (armorType == ArmorType.TP7) {
            return attackTable.getArmorType7();
        } else if (armorType == ArmorType.TP8) {
            return attackTable.getArmorType8();
        } else if (armorType == ArmorType.TP9) {
            return attackTable.getArmorType9();
        } else if (armorType == ArmorType.TP10) {
            return attackTable.getArmorType10();
        } else if (armorType == ArmorType.TP11) {
            return attackTable.getArmorType11();
        } else if (armorType == ArmorType.TP12) {
            return attackTable.getArmorType12();
        } else if (armorType == ArmorType.TP13) {
            return attackTable.getArmorType13();
        } else if (armorType == ArmorType.TP14) {
            return attackTable.getArmorType14();
        } else if (armorType == ArmorType.TP15) {
            return attackTable.getArmorType15();
        } else if (armorType == ArmorType.TP16) {
            return attackTable.getArmorType16();
        } else if (armorType == ArmorType.TP17) {
            return attackTable.getArmorType17();
        } else if (armorType == ArmorType.TP18) {
            return attackTable.getArmorType18();
        } else if (armorType == ArmorType.TP19) {
            return attackTable.getArmorType19();
        } else {
            return attackTable.getArmorType20();
        }
    }

}
