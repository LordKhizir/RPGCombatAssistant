package com.altekis.rpg.combatassistant.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockListFragment;
import com.altekis.rpg.combatassistant.R;
import com.altekis.rpg.combatassistant.RPGPreferences;
import com.altekis.rpg.combatassistant.character.ArmorType;
import com.altekis.rpg.combatassistant.character.CharacterAttackAdapter;
import com.altekis.rpg.combatassistant.character.RPGCharacter;
import com.altekis.rpg.combatassistant.character.RPGCharacterAttack;
import com.altekis.rpg.combatassistant.db.RuleSystem;

import java.util.List;

public class CharacterEditFragment extends SherlockListFragment implements View.OnClickListener {

    public static interface CallBack extends DBFragmentActivity {
        void characterAttackClick(int position);
        void cancelCharacter();
        void doneCharacter();
        void addAttack();
        RPGCharacter getCharacter();
        List<RPGCharacterAttack> getCharacterAttacks();
    }

    private CallBack mCallBack;
	private ArmorType[] mArmorTypes;

    /** View references */
    private EditText vTextName;
    private EditText vTextPlayerName;
    private CheckBox vCheckPnj;
    private EditText vTextHitPoints;
    private EditText vTextMaxHitPoints;
	private Spinner vSpinnerArmor;

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
        View view = inflater.inflate(R.layout.fragment_edit_character, container, false);
        vTextName = (EditText) view.findViewById(R.id.characterEdit_name);
        vTextPlayerName = (EditText) view.findViewById(R.id.characterEdit_playerName);
        vCheckPnj = (CheckBox) view.findViewById(R.id.characterEdit_playerPnj);
        vTextHitPoints = (EditText) view.findViewById(R.id.characterEdit_hitPoints);
        vTextMaxHitPoints = (EditText) view.findViewById(R.id.characterEdit_maxHitPoints);
        vSpinnerArmor = (Spinner) view.findViewById(R.id.characterEdit_armorType);
        view.findViewById(R.id.characterEdit_saveButton).setOnClickListener(this);
        view.findViewById(R.id.characterEdit_cancelButton).setOnClickListener(this);
        view.findViewById(R.id.characterEdit_addAttackButton).setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        populateData(savedInstanceState);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("armor-selection-value", mArmorTypes[vSpinnerArmor.getSelectedItemPosition()].getArmor());
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mCallBack != null) {
            mCallBack.characterAttackClick(position);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.characterEdit_saveButton) {
            if (!doSave()) {
                // Everything is correct... go create/update the mCharacter
                mCallBack.doneCharacter();
            }
        } else if (v.getId() == R.id.characterEdit_cancelButton) {
            mCallBack.cancelCharacter();
        } else if (v.getId() == R.id.characterEdit_addAttackButton) {
            if (!doSave()) {
                mCallBack.addAttack();
            }
        }
    }

    private void populateData(Bundle savedInstanceState) {
        RuleSystem system = RPGPreferences.getSystem(getSherlockActivity(), mCallBack.getHelper());
        mArmorTypes = ArmorType.getArmorTypes(system.getArmorType() == RuleSystem.ARMOR_COMPLETE);

        RPGCharacter character = mCallBack.getCharacter();
        if (character != null) {
            int armorSelectionValue = 0;
            // If there are state EditText and CheckBox save his values
            if (savedInstanceState == null) {
                vTextName.setText(character.getName());

                if (character.isPnj()) {
                    vTextPlayerName.setText(R.string.characterEdit_playerPnj);
                    vTextPlayerName.setEnabled(false);
                } else {
                    vTextPlayerName.setText(character.getPlayerName());
                    vTextPlayerName.setEnabled(true);
                }

                vCheckPnj.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if (isChecked) {
                            vTextPlayerName.setText(R.string.characterEdit_playerPnj);
                            vTextPlayerName.setEnabled(false);
                        } else {
                            vTextPlayerName.setText(null);
                            vTextPlayerName.setEnabled(true);
                        }
                    }
                });

                if (character.getHitPoints() == 0) {
                    vTextHitPoints.setText(null);
                } else {
                    vTextHitPoints.setText(Integer.toString(character.getHitPoints()));
                }

                if (character.getMaxHitPoints() == 0) {
                    vTextMaxHitPoints.setText(null);
                } else {
                    vTextMaxHitPoints.setText(Integer.toString(character.getMaxHitPoints()));
                }
            } else {
                armorSelectionValue = savedInstanceState.getInt("armor-selection-value", 0);
            }

            final String[] titles = new String[mArmorTypes.length];
            for (int i = 0 ; i < mArmorTypes.length ; i++) {
                if (system.getArmorType() == RuleSystem.ARMOR_SIMPLE) {
                    titles[i] = getString(mArmorTypes[i].getMerpString());
                } else {
                    titles[i] = getString(mArmorTypes[i].getRmString());
                }
            }
            // Create an ArrayAdapter using the string array and a default spinner layout
            ArrayAdapter<String> armorTypeAdapter = new ArrayAdapter<String>(getSherlockActivity(),
                    android.R.layout.simple_spinner_item,
                    titles);
            // Specify the layout to use when the list of choices appears
            armorTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            // Apply the adapter to the spinner
            vSpinnerArmor.setAdapter(armorTypeAdapter);
            // Select spinner position - Armor type
            if (armorSelectionValue == 0) {
                // Get the character saved armor
                armorSelectionValue = character.getArmorType();
                if (system.getArmorType() == RuleSystem.ARMOR_SIMPLE) {
                    armorSelectionValue = ArmorType.fromInteger(armorSelectionValue).getMerpArmor();
                }
            }
            int position = 0;
            for (ArmorType type : mArmorTypes) {
                if (type.getArmor() == armorSelectionValue) {
                    break;
                }
                position++;
            }
            vSpinnerArmor.setSelection(position);
        }

        CharacterAttackAdapter mAdapter = new CharacterAttackAdapter(getSherlockActivity(), system, mCallBack.getCharacterAttacks());
        setListAdapter(mAdapter);
    }

    /**
     * Apply changes to character
     * @return true if there was an error
     */
    private boolean doSave() {
        RPGCharacter character = mCallBack.getCharacter();
        boolean errorFound = false;
        if (character != null) {
            // Update character with the info provided by the user
            character.setName(vTextName.getText().toString().trim());
            if (vCheckPnj.isChecked()) {
                character.setPnj(true);
                character.setPlayerName(null);
            } else {
                character.setPnj(false);
                character.setPlayerName(vTextPlayerName.getText().toString().trim());
            }

            // Before saving, check for errors
            String maxHitPointsRaw = vTextMaxHitPoints.getText().toString().trim();
            String hitPointsRaw = vTextHitPoints.getText().toString().trim();

            if (TextUtils.isEmpty(character.getName())) {
                vTextName.setError(getResources().getText(R.string.errorMandatory));
                errorFound = true;
            }
            if (TextUtils.isEmpty(character.getPlayerName()) && !character.isPnj()) {
                vTextPlayerName.setError(getResources().getText(R.string.errorMandatory));
                errorFound = true;
            }
            if (maxHitPointsRaw.length() == 0) {
                errorFound = true; // 0 is allowed... but we'll require it to be explicitly typed, to avoid usual errors
                vTextMaxHitPoints.setError(getResources().getText(R.string.errorMandatory));
            } else {
                try {
                    character.setMaxHitPoints(Integer.parseInt(maxHitPointsRaw));
                } catch (NumberFormatException e) {
                    errorFound = true;
                    vTextMaxHitPoints.setError(getResources().getText(R.string.errorMustBeANumber));
                }
            }
            if (hitPointsRaw.length()==0) {
                errorFound = true; // 0 is allowed... but we'll require it to be explicitly typed, to avoid usual errors
                vTextHitPoints.setError(getResources().getText(R.string.errorMandatory));
            } else {
                try {
                    character.setHitPoints(Integer.parseInt(hitPointsRaw));
                } catch (NumberFormatException e) {
                    errorFound = true;
                    vTextHitPoints.setError(getResources().getText(R.string.errorMustBeANumber));
                }
            }

            // armor type is a spinner, it's always correct
            character.setArmorType(mArmorTypes[vSpinnerArmor.getSelectedItemPosition()].getArmor());
        }
        return errorFound;
    }
}
