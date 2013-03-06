package com.altekis.rpg.combatassistant;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.altekis.rpg.combatassistant.attack.AttackResult;
import com.altekis.rpg.combatassistant.character.ArmorType;
import com.altekis.rpg.combatassistant.character.RPGCharacter;
import com.altekis.rpg.combatassistant.character.RPGCharacterAttack;
import com.altekis.rpg.combatassistant.critical.Critical;
import com.altekis.rpg.combatassistant.db.DBUtil;
import com.altekis.rpg.combatassistant.fragments.AttackFragment;
import com.altekis.rpg.combatassistant.fragments.CriticalFragment;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public class AttackActivity extends BaseActivity implements AttackFragment.CallBack, CriticalFragment.CallBack, View.OnClickListener {

    public static final String ARG_ID_ATTACKER = "idAttacker";
    public static final String ARG_ID_ATTACKER_ATTACK = "idAttackerAttack";

    private List<RPGCharacter> mRPGCharacters;
    private AttackResult mAttackResult;

    private View vLayoutAttack;
    private TextView vTextCharacter;
    private TextView vTextCritical;
    private TextView vTextHitPoints;
    private View vButtonApply;
    private View vButtonCritical;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attack);
        vLayoutAttack = findViewById(R.id.attack_result);
        vLayoutAttack.setOnClickListener(this);
        vTextCharacter = (TextView) findViewById(R.id.attack_resultCharacter);
        vTextCritical = (TextView) findViewById(R.id.attack_resultCritical);
        vTextHitPoints = (TextView) findViewById(R.id.attack_resultHitPoints);
        vButtonApply = findViewById(R.id.attack_applyHitPoints);
        vButtonApply.setOnClickListener(this);
        vButtonCritical = findViewById(R.id.attack_critical);
        vButtonCritical.setOnClickListener(this);

        if (savedInstanceState != null) {
            mAttackResult = new AttackResult(savedInstanceState);
        }
        populateAttackResult();

        FragmentManager fm = getSupportFragmentManager();
        Fragment frg = fm.findFragmentById(R.id.main_content);
        if (frg == null) {
            long idAttacker = getIntent().getLongExtra(ARG_ID_ATTACKER, 0);
            long idAttackerAttack = getIntent().getLongExtra(ARG_ID_ATTACKER_ATTACK, 0);
            frg = AttackFragment.newInstance(idAttacker, idAttackerAttack);
            fm.beginTransaction().add(R.id.main_content, frg).commit();
        }
	}

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mAttackResult != null) {
            mAttackResult.saveData(outState);
        }
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        Fragment frg = fm.findFragmentById(R.id.main_content);
        if (frg instanceof CriticalFragment) {
            vLayoutAttack.setVisibility(View.VISIBLE);
        }
        super.onBackPressed();
    }

    @Override
    public void cancelAttack() {
        finish();
    }

    @Override
    public void saveAttack(RPGCharacterAttack attack, int roll, int total, RPGCharacter defender, ArmorType armorType) {
        mAttackResult = DBUtil.getValue(getHelper(), attack.getAttack(), roll, total, armorType);
        mAttackResult.setCharacterAttack(attack);
        mAttackResult.setCharacterDefender(defender);
        populateAttackResult();
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

    private void populateAttackResult() {
        if (mAttackResult == null) {
            vLayoutAttack.setVisibility(View.GONE);
        } else {
            RPGCharacterAttack attack = mAttackResult.getCharacterAttack();
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

                vLayoutAttack.setVisibility(View.VISIBLE);
                vTextCharacter.setText(attack.getRPGCharacter().getStringName(this));

                if (mAttackResult.isFumbled()) {
                    vTextHitPoints.setText("Pifia");
                } else {
                    vTextHitPoints.setText(mAttackResult.getHitPoints() + " puntos");
                }

                if (mAttackResult.getCharacterDefender() == null || mAttackResult.isApplied()) {
                    vButtonApply.setEnabled(false);
                } else {
                    vButtonApply.setEnabled(true);
                }

                if (mAttackResult.getCriticalLevel() == null) {
                    vTextCritical.setText(null);
                    vButtonCritical.setEnabled(false);
                } else {
                    vTextCritical.setText(getString(R.string.attack_critical,
                            mAttackResult.getCritical().getName(), mAttackResult.getCriticalLevel().toString()));
                    vButtonCritical.setEnabled(true);
                }
            } catch (SQLException e) {
                Log.e("RPGCombatAssistant", "Can't read database", e);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.attack_applyHitPoints) {
            if (mAttackResult.getCharacterDefender() != null && !mAttackResult.isApplied()) {
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
                    vButtonCritical.setEnabled(false);
                    mAttackResult.setApplied(true);
                } catch (SQLException e) {
                    Log.e("RPGCombatAssistant", "Can't read database", e);
                }
            }
        } else if (v.getId() == R.id.attack_critical) {
            if (mAttackResult.getCritical() != null && mAttackResult.getCriticalLevel() != null && !mAttackResult.isApplied()) {
                FragmentManager fm = getSupportFragmentManager();
                CriticalFragment frg = CriticalFragment.newInstance(mAttackResult.getCritical().getId(), mAttackResult.getCriticalLevel());
                fm.beginTransaction().replace(R.id.main_content, frg).addToBackStack("critical").commit();
                vLayoutAttack.setVisibility(View.GONE);
            }
        }
    }

    @Override
    public void cancelCritical() {
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack();
        vLayoutAttack.setVisibility(View.VISIBLE);
    }
}
