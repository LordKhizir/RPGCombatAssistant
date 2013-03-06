package com.altekis.rpg.combatassistant.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragment;
import com.altekis.rpg.combatassistant.AttackActivity;
import com.altekis.rpg.combatassistant.R;
import com.altekis.rpg.combatassistant.RPGPreferences;
import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.character.*;
import com.altekis.rpg.combatassistant.db.RuleSystem;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttackFragment extends SherlockFragment implements AdapterView.OnItemSelectedListener, View.OnClickListener {

    private static final String ARG_ID_DEFENDER = "idDefender";

    public static AttackFragment newInstance(long idAttacker, long idAttackerAttack) {
        Bundle args = new Bundle();
        args.putLong(AttackActivity.ARG_ID_ATTACKER, idAttacker);
        args.putLong(AttackActivity.ARG_ID_ATTACKER_ATTACK, idAttackerAttack);
        AttackFragment frg = new AttackFragment();
        frg.setArguments(args);
        return frg;
    }

    public static interface CallBack extends DBFragmentActivity {
        List<RPGCharacter> getCharacters();
        void cancelAttack();
        void saveAttack(RPGCharacterAttack attack, int roll, int total, RPGCharacter defender, ArmorType armorType);
    }

    private CallBack mCallBack;
    private ArmorType[] mArmorTypes;

    private Spinner vSpinnerAttacker;
    private Spinner vSpinnerAttackerAttack;
    private TextView vTextBonus;
    private SeekBar vSeekBonus;
    private EditText vEditRoll;
    private EditText vEditExtra;
    private Spinner vSpinnerDefender;
    private Spinner vSpinnerDefenderArmor;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof CallBack) {
            mCallBack = (CallBack) activity;
        } else {
            throw new IllegalStateException(activity.getClass().getName() + " must implement " + CallBack.class.getName());
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack = null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_attack, container, false);
        vSpinnerAttacker = (Spinner) v.findViewById(R.id.attack_attacker);
        vSpinnerAttackerAttack = (Spinner) v.findViewById(R.id.attack_attackerAttack);
        vTextBonus = (TextView) v.findViewById(R.id.attack_bonusSeekLabel);
        vSeekBonus = (SeekBar) v.findViewById(R.id.attack_bonusSeek);
        vEditRoll = (EditText) v.findViewById(R.id.attack_roll);
        vEditExtra = (EditText) v.findViewById(R.id.attack_extra);
        vSpinnerDefender = (Spinner) v.findViewById(R.id.attack_defender);
        vSpinnerDefenderArmor = (Spinner) v.findViewById(R.id.attack_defenderArmor);
        v.findViewById(R.id.attack_cancelButton).setOnClickListener(this);
        v.findViewById(R.id.attack_goButton).setOnClickListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        RuleSystem system = RPGPreferences.getSystem(getSherlockActivity(), mCallBack.getHelper());
        mArmorTypes = ArmorType.getArmorTypes(system.getArmorType() == RuleSystem.ARMOR_COMPLETE);

        long idAttacker;
        long idDefender;
        // First we will search in instance
        if (savedInstanceState != null) {
            idAttacker = savedInstanceState.getLong(AttackActivity.ARG_ID_ATTACKER);
            idDefender = savedInstanceState.getLong(ARG_ID_DEFENDER);
        } else {
            idAttacker = getArguments().getLong(AttackActivity.ARG_ID_ATTACKER, 0);
            idDefender = getArguments().getLong(ARG_ID_DEFENDER, 0);
        }

        List<RPGCharacter> lst = mCallBack.getCharacters();
        if (lst != null && !lst.isEmpty()) {
            // Attacker
            int selected = 0;
            int pos = 0;
            for (RPGCharacter c : lst) {
                if (c.getId() == idAttacker) {
                    selected = pos;
                    break;
                }
                pos++;
            }
            CharacterSpinnerAdapter adapter = new CharacterSpinnerAdapter(getSherlockActivity(),
                    android.R.layout.simple_spinner_item,
                    lst);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            vSpinnerAttacker.setAdapter(adapter);
            vSpinnerAttacker.setSelection(selected);
            vSpinnerAttacker.setOnItemSelectedListener(this);
            updateSeekBar();

            // Defender
            selected = 0;
            pos = 0;
            for (RPGCharacter c : lst) {
                if (c.getId() == idDefender) {
                    selected = pos;
                    break;
                }
                pos++;
            }
            RPGCharacter mock = new RPGCharacter();
            mock.setName("(Nadie)");
            List<RPGCharacter> lstDef = new ArrayList<RPGCharacter>(lst);
            lstDef.add(0, mock);
            adapter = new CharacterSpinnerAdapter(getSherlockActivity(),
                    android.R.layout.simple_spinner_item,
                    lstDef, true);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            vSpinnerDefender.setAdapter(adapter);
            vSpinnerDefender.setSelection(selected);
            vSpinnerDefender.setOnItemSelectedListener(this);

            String[] titles = new String[mArmorTypes.length];
            for (int i = 0 ; i < mArmorTypes.length ; i++) {
                if (system.getArmorType() == RuleSystem.ARMOR_SIMPLE) {
                    titles[i] = getString(mArmorTypes[i].getMerpString());
                } else {
                    titles[i] = getString(mArmorTypes[i].getRmString());
                }
            }
            ArrayAdapter<String> armorAdapter = new ArrayAdapter<String>(getSherlockActivity(),
                    android.R.layout.simple_spinner_item,
                    titles);
            armorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            vSpinnerDefenderArmor.setAdapter(armorAdapter);
            setDefenderArmor(lstDef.get(selected));
        }

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        RPGCharacter character = (RPGCharacter) vSpinnerAttacker.getSelectedItem();
        outState.putLong(AttackActivity.ARG_ID_ATTACKER, character.getId());
        if (vSpinnerDefender.getSelectedItemId() > 0) {
            character = (RPGCharacter) vSpinnerDefender.getSelectedItem();
            outState.putLong(ARG_ID_DEFENDER, character.getId());
        }

    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent.getId() == R.id.attack_attacker) {
            // Reset the spinnerAttackerAttack
            loadAttackerAttack(id, 0);
        } else if (parent.getId() == R.id.attack_attackerAttack) {
            updateSeekBar();
        } else if (parent.getId() == R.id.attack_defender) {
            setDefenderArmor((RPGCharacter) parent.getSelectedItem());
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.attack_cancelButton) {
            mCallBack.cancelAttack();
        } else if (v.getId() == R.id.attack_goButton) {
            boolean errorFound = false;
            int extra = 0;
            int roll = 0;
            String extraRaw = vEditExtra.getText().toString().trim();
            if (extraRaw.length() == 0) {
                extra = 0; // 0 allowed
            } else {
                try {
                    extra = Integer.parseInt(extraRaw);
                } catch (NumberFormatException e) {
                    errorFound = true;
                    vEditExtra.setError(getResources().getText(R.string.errorMustBeANumber));
                }
            }

            String rollRaw = vEditRoll.getText().toString().trim();
            if (rollRaw.length() == 0) {
                // 0 is allowed, just in case of fumble (example, 02 - (02))... but we'll require it to be explicitly typed, to avoid usual errors
                errorFound = true;
                vEditRoll.setError(getResources().getText(R.string.errorMandatory));
            } else {
                try {
                    roll = Integer.parseInt(rollRaw);
                } catch (NumberFormatException e) {
                    errorFound = true;
                    vEditRoll.setError(getResources().getText(R.string.errorMustBeANumber));
                }
            }

            if (!errorFound) {
                int total = vSeekBonus.getProgress() + extra + roll;
                // Get character attack
                RPGCharacterAttack attack = (RPGCharacterAttack) vSpinnerAttackerAttack.getSelectedItem();
                // Get character defender
                RPGCharacter defender;
                if (vSpinnerDefender.getSelectedItemId() == 0) {
                    defender = null;
                } else {
                    defender = (RPGCharacter) vSpinnerDefender.getSelectedItem();
                }
                // Get user selected armor
                int selected = vSpinnerDefenderArmor.getSelectedItemPosition();
                ArmorType armorType;
                if (selected < 0 || selected >= mArmorTypes.length) {
                    armorType = ArmorType.TP1;
                } else {
                    armorType = mArmorTypes[selected];
                }

                mCallBack.saveAttack(attack, roll, total, defender, armorType);
            }
        }
    }

    private void updateBonusText() {
        int actual = vSeekBonus.getProgress();
        int attackParry = vSeekBonus.getMax() - actual;
        vTextBonus.setText(Integer.toString(actual) + " ataque · defensa " + Integer.toString(attackParry));
    }

    private void setDefenderArmor(RPGCharacter character) {
        if (character.getArmorType() == 0) {
            vSpinnerDefenderArmor.setSelection(0);
        } else {
            RuleSystem system = RPGPreferences.getSystem(getSherlockActivity(), mCallBack.getHelper());
            ArmorType type = ArmorType.fromInteger(character.getArmorType());
            if (system.getArmorType() == RuleSystem.ARMOR_SIMPLE) {
                type = type.getMerp();
            }
            // Select spinner position - Defender armor type
            for (int i = 0 ; i < mArmorTypes.length ; i++) {
                if (mArmorTypes[i] == type) {
                    vSpinnerDefenderArmor.setSelection(i);
                    break;
                }
            }
        }
    }

    private void updateSeekBar() {
        RPGCharacterAttack attack = (RPGCharacterAttack) vSpinnerAttackerAttack.getSelectedItem();
        if (attack != null) {
            vSeekBonus.setMax(attack.getBonus());
            vSeekBonus.setProgress(attack.getBonus());
            vSeekBonus.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                    if (fromUser) {
                        updateBonusText();
                    }
                }

                @Override
                public void onStartTrackingTouch(SeekBar seekBar) {

                }

                @Override
                public void onStopTrackingTouch(SeekBar seekBar) {

                }
            });
            updateBonusText();
        }
    }

    private void loadAttackerAttack(long idAttacker, long idAttack) {
        List<RPGCharacterAttack> lst = loadAttacks(idAttacker);
        int selected = 0;
        int pos = 0;
        for (RPGCharacterAttack attack : lst) {
            if (attack.getId() == idAttack) {
                selected = pos;
                break;
            }
            pos++;
        }
        CharacterAttackSpinnerAdapter adapter = new CharacterAttackSpinnerAdapter(getSherlockActivity(),
                android.R.layout.simple_spinner_item,
                lst);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vSpinnerAttackerAttack.setAdapter(adapter);
        vSpinnerAttackerAttack.setSelection(selected);
        vSpinnerAttackerAttack.setOnItemSelectedListener(this);
    }

    private List<RPGCharacterAttack> loadAttacks(long characterId) {
        RuleSystem system = RPGPreferences.getSystem(getSherlockActivity(), mCallBack.getHelper());
        List<RPGCharacterAttack> lst;
        try {
            Dao<Attack, Long> daoAttack = mCallBack.getHelper().getDaoAttack();
            QueryBuilder<Attack, Long> qbAttack = daoAttack.queryBuilder();
            qbAttack.setWhere(qbAttack.where().eq(Attack.FIELD_SYSTEM_ID, system.getId()));

            Dao<RPGCharacterAttack, Long> daoCharacterAttack = mCallBack.getHelper().getDaoRPGCharacterAttack();
            QueryBuilder<RPGCharacterAttack, Long> qbCharacterAttack = daoCharacterAttack.queryBuilder();
            qbCharacterAttack.setWhere(qbCharacterAttack.where().eq(RPGCharacterAttack.FIELD_CHARACTER_ID, characterId));
            qbCharacterAttack.orderBy(RPGCharacterAttack.FIELD_NAME, true);

            lst = qbCharacterAttack.join(qbAttack).query();
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Error", e);
            lst = new ArrayList<RPGCharacterAttack>();
        }
        return lst;
    }
}
