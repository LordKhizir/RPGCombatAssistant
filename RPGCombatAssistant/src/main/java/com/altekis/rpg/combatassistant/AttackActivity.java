package com.altekis.rpg.combatassistant;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import com.actionbarsherlock.view.MenuItem;
import com.altekis.rpg.combatassistant.attack.AttackResult;
import com.altekis.rpg.combatassistant.character.RPGCharacter;
import com.altekis.rpg.combatassistant.character.RPGCharacterAttack;
import com.altekis.rpg.combatassistant.critical.Critical;
import com.altekis.rpg.combatassistant.critical.CriticalLevel;
import com.altekis.rpg.combatassistant.fragments.AttackDialogFragment;
import com.altekis.rpg.combatassistant.fragments.AttackFragment;
import com.altekis.rpg.combatassistant.fragments.CriticalFragment;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;

public class AttackActivity extends BaseActivity implements AttackFragment.CallBack, AttackDialogFragment.CallBack, CriticalFragment.CallBack {

    public static final String ARG_ID_ATTACKER = "idAttacker";
    public static final String ARG_ID_ATTACKER_ATTACK = "idAttackerAttack";
    public static final String ARG_CRITICAL = "critical";

    private List<RPGCharacter> mRPGCharacters;
    private AttackResult mAttackResult;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attack);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        FragmentManager fm = getSupportFragmentManager();
        Fragment frg = fm.findFragmentById(R.id.main_content);
        if (frg == null) {
            if (getIntent().getBooleanExtra(ARG_CRITICAL, false)) {
                frg = new CriticalFragment();
            } else {
                long idAttacker = getIntent().getLongExtra(ARG_ID_ATTACKER, 0);
                long idAttackerAttack = getIntent().getLongExtra(ARG_ID_ATTACKER_ATTACK, 0);
                frg = AttackFragment.newInstance(idAttacker, idAttackerAttack);
            }
            fm.beginTransaction().add(R.id.main_content, frg).commit();
        }
	}

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void cancelAttack() {
        finish();
    }

    @Override
    public void resultAttack(AttackResult attackResult) {
        RPGCharacterAttack attack = attackResult.getCharacterAttack();
        try {
            if (attack.getName() == null) {
                Dao<RPGCharacterAttack, Long> daoRPGCharacterAttack = getHelper().getDaoRPGCharacterAttack();
                daoRPGCharacterAttack.refresh(attack);
            }

            // Attack is auto-refreshed when refreshing RPGCharacterAttack

            if (attack.getAttack().getCritical().getName() == null) {
                Dao<Critical, Long> daoCritical = getHelper().getDaoCritical();
                daoCritical.refresh(attack.getAttack().getCritical());
            }

            if (attack.getRPGCharacter().getName() == null) {
                Dao<RPGCharacter, Long> daoRPGCharacter = getHelper().getDaoRPGCharacter();
                daoRPGCharacter.refresh(attack.getRPGCharacter());
            }

            mAttackResult = attackResult;
            new AttackDialogFragment().show(getSupportFragmentManager(), "result_attack");
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Can't read database", e);
        }
    }

    @Override
    public List<RPGCharacter> getCharacters() {
        if (mRPGCharacters == null) {
            try {
                Dao<RPGCharacter, Long> daoRPGCharacter = getHelper().getDaoRPGCharacter();
                mRPGCharacters = daoRPGCharacter.query(daoRPGCharacter.queryBuilder().orderBy(RPGCharacter.FIELD_NAME, true).prepare());
            } catch (SQLException e) {
                Log.e("RPGCombatAssistant", "Can't read database", e);
            }
        }
        return mRPGCharacters;
    }

    @Override
    public void cancelCritical() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frg = fm.findFragmentByTag("critical");
        if (frg != null) {
            fm.popBackStack();
        } else {
            finish();
        }
    }

    @Override
    public AttackResult getResultAttack() {
        return mAttackResult;
    }

    @Override
    public void applyResultAttack(boolean apply, boolean critical) {
        if (apply) {
            if (mAttackResult.getCharacterDefender() != null) {
                try {
                    RPGCharacter defender = mAttackResult.getCharacterDefender();
                    Dao<RPGCharacter, Long> daoRPGCharacter = getHelper().getDaoRPGCharacter();
                    daoRPGCharacter.refresh(defender);
                    int hitPoints = defender.getHitPoints() - mAttackResult.getHitPoints();
                    if (hitPoints > defender.getMaxHitPoints()) {
                        hitPoints = defender.getMaxHitPoints();
                    }
                    if (hitPoints < 0) {
                        hitPoints = 0;
                    }
                    defender.setHitPoints(hitPoints);
                    daoRPGCharacter.update(defender);
                } catch (SQLException e) {
                    Log.e("RPGCombatAssistant", "Can't read database", e);
                }
            }
        }
        if (critical) {
            if (mAttackResult.getCritical() != null && mAttackResult.getCriticalLevel() != null) {
                FragmentManager fm = getSupportFragmentManager();
                CriticalFragment frg = CriticalFragment.newInstance(mAttackResult.getCritical().getId(), mAttackResult.getCriticalLevel());
                fm.beginTransaction().replace(R.id.main_content, frg).addToBackStack("critical").commit();
            }
        }

    }
}
