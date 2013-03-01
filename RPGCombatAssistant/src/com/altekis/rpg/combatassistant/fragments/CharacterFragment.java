package com.altekis.rpg.combatassistant.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.altekis.rpg.combatassistant.R;
import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.character.ArmorType;
import com.altekis.rpg.combatassistant.character.CharacterAttackAdapter;
import com.altekis.rpg.combatassistant.character.RPGCharacter;
import com.altekis.rpg.combatassistant.character.RPGCharacterAttack;
import com.j256.ormlite.dao.Dao;

import java.sql.SQLException;
import java.util.List;

public class CharacterFragment extends SherlockListFragment {

    public static interface CallBack extends DBFragmentActivity {
        void characterAttackClick(long characterId, long characterAttackId);
        void addCharacter();
        void editCharacter(long characterId);
        void deleteCharacter(long characterId);
        void addCharacterAttack(long characterId);
    }

    private static final String ARG_CHARACTER_ID = "characterId";

    public static CharacterFragment newInstance(long characterId) {
        Bundle args = new Bundle();
        args.putLong(ARG_CHARACTER_ID, characterId);
        CharacterFragment frg = new CharacterFragment();
        frg.setArguments(args);
        return frg;
    }

    private CallBack mCallBack;
	private RPGCharacter mCharacter;
	private List<RPGCharacterAttack> mAttackList;

    /** View references */
    private TextView textViewName;
    private TextView textViewHitPoints;
    private TextView textViewArmor;

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
        View view = inflater.inflate(R.layout.fragment_character, container, false);
        textViewName = (TextView) view.findViewById(R.id.character_nameLabel);
        textViewHitPoints = (TextView) view.findViewById(R.id.character_hitPoints);
        textViewArmor = (TextView) view.findViewById(R.id.character_armor);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        setHasOptionsMenu(true);

        long characterId = 0;
        if (savedInstanceState != null) {
            characterId = savedInstanceState.getLong(ARG_CHARACTER_ID, 0);
        }
        if (characterId == 0) {
            characterId = getArguments().getLong(ARG_CHARACTER_ID);
        }
        loadData(characterId, true, true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_character, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add_character) {
            mCallBack.addCharacter();
        } else if (item.getItemId() == R.id.menu_add_attack) {
            mCallBack.addCharacterAttack(mCharacter.getId());
        } else if (item.getItemId() == R.id.menu_edit) {
            mCallBack.editCharacter(mCharacter.getId());
        } else if (item.getItemId() == R.id.menu_delete) {
            // Confirm delete, then go and do it
            // Ready a confirmation dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
            builder.setMessage(R.string.character_deleteDialog_message);
            builder.setTitle(R.string.character_deleteDialog_title);
            // Add buttons
            builder.setPositiveButton(R.string.character_deleteDialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    mCallBack.deleteCharacter(mCharacter.getId());
                }
            });
            builder.setNegativeButton(R.string.character_deleteDialog_cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User cancelled the dialog
                    // Nothing else to do here
                }
            });
            AlertDialog deleteDialog = builder.create();
            deleteDialog.show();
        }
        return true;
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mCallBack != null) {
            mCallBack.characterAttackClick(mCharacter.getId(), id);
        }
    }

    public void loadData(long characterId, boolean character, boolean attacks) {
        try {
            if (character) {
                Dao<RPGCharacter, Long> dao = mCallBack.getHelper().getDaoRPGCharacter();
                mCharacter = dao.queryForId(characterId);
            }
            if (attacks) {
                Dao<RPGCharacterAttack, Long> daoA = mCallBack.getHelper().getDaoRPGCharacterAttack();
                Dao<Attack, Long> daoAttack = mCallBack.getHelper().getDaoAttack();
                mAttackList = daoA.queryForEq(RPGCharacterAttack.FIELD_CHARACTER_ID, characterId);
                if (mAttackList != null) {
                    for (RPGCharacterAttack a : mAttackList) {
                        daoAttack.refresh(a.getAttack());
                    }
                }
            }
            pupulateData(character, attacks);
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Can't read database", e);
        }
    }

    private void pupulateData(boolean character, boolean attacks) {
        if (character && mCharacter != null) {
            if (mCharacter.isPnj()) {
                textViewName.setText(getString(R.string.character_name_pnj, mCharacter.getName()));
            } else {
                textViewName.setText(getString(R.string.character_name, mCharacter.getName(), mCharacter.getPlayerName()));
            }

            textViewHitPoints.setText(getString(R.string.character_name, mCharacter.getHitPoints(), mCharacter.getMaxHitPoints()));

            // TODO Rolemaster system
            boolean rolemasterSystem = false;
            ArmorType armorType = ArmorType.fromInteger(mCharacter.getArmorType());
            if (rolemasterSystem) {
                textViewArmor.setText(getString(armorType.getRmString()));
            } else {
                textViewArmor.setText(getString(armorType.getMerpString()));
            }
        }

        if (attacks) {
            CharacterAttackAdapter adapter = new CharacterAttackAdapter(getSherlockActivity(), mAttackList);
            setListAdapter(adapter);
        }
    }
}
