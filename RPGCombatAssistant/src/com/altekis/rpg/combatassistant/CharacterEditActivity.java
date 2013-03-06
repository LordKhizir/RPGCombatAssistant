package com.altekis.rpg.combatassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.attack.AttackComparator;
import com.altekis.rpg.combatassistant.character.ArmorType;
import com.altekis.rpg.combatassistant.character.RPGCharacter;
import com.altekis.rpg.combatassistant.character.RPGCharacterAttack;
import com.altekis.rpg.combatassistant.db.RuleSystem;
import com.altekis.rpg.combatassistant.fragments.AttackEditFragment;
import com.altekis.rpg.combatassistant.fragments.CharacterEditFragment;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CharacterEditActivity extends BaseActivity implements CharacterEditFragment.CallBack, AttackEditFragment.CallBack {

    public static final String ARG_CHARACTER_ID = "CharacterId";
    public static final String ARG_ATTACK_ID = "AttackId";

    private RPGCharacter mCharacter;
	private List<RPGCharacterAttack> mCharacterAttackList;
    private RPGCharacterAttack mCharacterAttack;
    private List<Attack> mAttackList;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);

        long characterId = 0;
        long attackId = -1;
        if (savedInstanceState != null) {
            characterId = savedInstanceState.getLong(ARG_CHARACTER_ID, 0);
            attackId = savedInstanceState.getLong(ARG_ATTACK_ID, -1);
        }

		// Get Extras
        if (characterId == 0) {
		    characterId = getIntent().getLongExtra(ARG_CHARACTER_ID, 0);
        }

    	loadData(characterId, attackId);

        if (mCharacter == null) {
            // Database not accesible, we need to finish
            finish();
        } else {
            FragmentManager fm = getSupportFragmentManager();
            Fragment frg = fm.findFragmentById(R.id.main_content);
            if (frg == null) {
                frg = new CharacterEditFragment();
                fm.beginTransaction().add(R.id.main_content, frg).commit();
            }
        }
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCharacter != null) {
            outState.putLong(ARG_CHARACTER_ID, mCharacter.getId());
        }
        if (mCharacterAttack != null) {
            outState.putLong(ARG_ATTACK_ID, mCharacterAttack.getId());
        }
    }

    /**
     * Load character info for edit
     * @param characterId or 0 if is new character
     * @param attackId for edit specific character attack, 0 for new character attack or -1 for no edit attack
     */
    private void loadData(long characterId, long attackId) {
        try {
            if (characterId > 0) {
                Dao<RPGCharacter, Long> dao = getHelper().getDaoRPGCharacter();
                mCharacter = dao.queryForId(characterId);
                Dao<RPGCharacterAttack, Long> daoA = getHelper().getDaoRPGCharacterAttack();
                mCharacterAttackList = daoA.queryForEq(RPGCharacterAttack.FIELD_CHARACTER_ID, characterId);
                RuleSystem system = RPGPreferences.getSystem(this, getHelper());
                Collections.sort(mCharacterAttackList, new AttackComparator(system.getId()));
            } else {
                // If no CharacterId, we'll create a new one instead of updating
                mCharacter = new RPGCharacter();
                mCharacter.setArmorType(ArmorType.TP1.getArmor());
            }
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Can't read database", e);
        }

        if (attackId < 0) {
            // No attack to edit
        } else if (attackId == 0) {
            // Creating new character attack
            mCharacterAttack = new RPGCharacterAttack();
        } else if (mCharacterAttackList != null) {
            // Editing an existing character attack
            for (RPGCharacterAttack a : mCharacterAttackList) {
                if (attackId == a.getId()) {
                    mCharacterAttack = a;
                    break;
                }
            }
        }
    }

    @Override
    public void characterAttackClick(int position) {
        if (position < 0 || position >= mCharacterAttackList.size()) {
            mCharacterAttack = new RPGCharacterAttack();
        } else {
            mCharacterAttack = mCharacterAttackList.get(position);
        }
        FragmentManager fm = getSupportFragmentManager();
        fm.beginTransaction().replace(R.id.main_content, new AttackEditFragment()).addToBackStack("edit_attack").commit();
    }

    @Override
    public RPGCharacterAttack getCharacterAttack() {
        return mCharacterAttack;
    }

    @Override
    public List<Attack> getAttacks() {
        if (mAttackList == null) {
            Dao<Attack, Long> daoAttack;
            try {
                daoAttack = getHelper().getDaoAttack();
                QueryBuilder<Attack, Long> qb = daoAttack.queryBuilder();
                RuleSystem system = RPGPreferences.getSystem(this, getHelper());
                qb.setWhere(qb.where().eq(Attack.FIELD_SYSTEM_ID, system.getId()));
                qb.orderBy(Attack.FIELD_NAME, true);
                mAttackList = daoAttack.query(qb.prepare());
            } catch (SQLException e) {
                Log.e("RPGCombatAssistant", "Can't read database", e);
                mAttackList = new ArrayList<Attack>();
            }
        }
        return mAttackList;
    }

    @Override
    public void saveAttack() {
        if (mCharacterAttackList == null) {
            mCharacterAttackList = new ArrayList<RPGCharacterAttack>();
        }
        if (mCharacterAttack.getId() == 0) {
            mCharacterAttackList.add(mCharacterAttack);
        }
        mCharacterAttack = null;
        RuleSystem system = RPGPreferences.getSystem(this, getHelper());
        Collections.sort(mCharacterAttackList, new AttackComparator(system.getId()));
        cancelAttack();
    }

    @Override
    public void deleteAttack() {
        if (mCharacterAttack.getId() > 0) {
            mCharacterAttackList.remove(mCharacterAttack);
            try {
                Dao<RPGCharacterAttack, Long> daoAttack = getHelper().getDaoRPGCharacterAttack();
                daoAttack.delete(mCharacterAttack);
            } catch (SQLException e) {
                Log.e("RPGCombatAssistant", "Can't read database", e);
                mAttackList = new ArrayList<Attack>();
            }
        }
        cancelAttack();
    }

    @Override
    public void cancelAttack() {
        mCharacterAttack = null;
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
    }

    @Override
    public void cancelCharacter() {
        // Set result as CANCELED
        setResult(RESULT_CANCELED);
        finish();
    }

    @Override
    public void doneCharacter() {
        try {
            Dao<RPGCharacter, Long> dao = getHelper().getDaoRPGCharacter();
            Dao<RPGCharacterAttack, Long> daoA = getHelper().getDaoRPGCharacterAttack();
            dao.createOrUpdate(mCharacter);
            if (mCharacterAttackList != null) {
                for (RPGCharacterAttack attack : mCharacterAttackList) {
                    attack.setRPGCharacter(mCharacter);
                    daoA.createOrUpdate(attack);
                }
            }
            Intent data = new Intent();
            data.putExtra(ARG_CHARACTER_ID, mCharacter.getId());
            setResult(RESULT_OK, data); // Set result as OK == created/updated
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Can't read database", e);
        }
        finish();
    }

    @Override
    public void addAttack() {
        characterAttackClick(-1);
    }

    @Override
    public RPGCharacter getCharacter() {
        return mCharacter;
    }

    @Override
    public List<RPGCharacterAttack> getCharacterAttacks() {
        return mCharacterAttackList;
    }
}
