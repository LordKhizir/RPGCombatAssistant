package com.altekis.rpg.combatassistant.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.altekis.rpg.combatassistant.R;
import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.character.RPGCharacterAttack;

import java.util.List;

public class AttackEditFragment extends SherlockFragment implements View.OnClickListener {

    private static final String ARG_ATTACK_ID = "attackId";

    public static AttackEditFragment newInstance(long characterAttackId) {
        Bundle args = new Bundle();
        args.putLong(ARG_ATTACK_ID, characterAttackId);
        AttackEditFragment frg = new AttackEditFragment();
        frg.setArguments(args);
        return frg;
    }

    public static interface CallBack extends DBFragmentActivity {
        RPGCharacterAttack getCharacterAttack();
        List<Attack> getAttacks();
        void saveAttack();
        void deleteAttack();
        void cancelAttack();
    }

    /** Edited field */
	private RPGCharacterAttack mCharacterAttack;
    private List<Attack> mAttacks;

    private CallBack mCallBack;

    /**
     * View fields
     */
    private ImageView vImageIcon;
    private EditText vTextName;
    private Spinner vSpinnerAttack;
    private EditText vTextBonus;

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
        View view = inflater.inflate(R.layout.fragment_edit_attack, container, false);
        vImageIcon = (ImageView) view.findViewById(R.id.attackEdit_icon);
        vTextName = (EditText) view.findViewById(R.id.attackEdit_name);
        vSpinnerAttack = (Spinner) view.findViewById(R.id.attackEdit_attackType);
        vTextBonus = (EditText) view.findViewById(R.id.attackEdit_bonus);
        view.findViewById(R.id.attackEdit_cancelButton).setOnClickListener(this);
        view.findViewById(R.id.attackEdit_saveButton).setOnClickListener(this);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        populateData(savedInstanceState);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_attack_edit, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_delete) {
            // Confirm delete, then go and do it
            // Ready a confirmation dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
            builder.setMessage(R.string.attack_deleteDialog_message);
            builder.setTitle(R.string.attack_deleteDialog_title);
            // Add buttons
            builder.setPositiveButton(R.string.attack_deleteDialog_ok, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    // User clicked OK button
                    mCallBack.deleteAttack();
                }
            });
            builder.setNegativeButton(R.string.attack_deleteDialog_cancel, null);
            AlertDialog deleteDialog = builder.create();
            deleteDialog.show();
        }
        return true;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong("attack-selection-id", vSpinnerAttack.getSelectedItemId());
    }

    public void populateData(Bundle savedInstanceState) {
        RPGCharacterAttack attack = mCallBack.getCharacterAttack();
        List<Attack> attackList = mCallBack.getAttacks();
        if (attack != null) {
            // Set UI
            long attackSelectionId = 0;
            if (savedInstanceState == null) {
                vTextName.setText(attack.getName());
                vTextBonus.setText(Integer.toString(attack.getBonus()));
            } else {
                attackSelectionId = savedInstanceState.getLong("attack-selection-id");
            }

            if (attackList != null) {
                // Create an ArrayAdapter using the string array and a default spinner layout
                ArrayAdapter<Attack> attackTypeAdapter = new ArrayAdapter<Attack>(getSherlockActivity(),
                        android.R.layout.simple_spinner_item, attackList);
                // Specify the layout to use when the list of choices appears
                attackTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                // Apply the adapter to the spinner
                vSpinnerAttack.setAdapter(attackTypeAdapter);
                vSpinnerAttack.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        Attack a = (Attack) parent.getItemAtPosition(position);
                        vImageIcon.setImageResource(a.getWeaponIcon());
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });
                if (attack.getAttack() != null) {
                    // Editting, we need to set the spinner on right position
                    if (attackSelectionId == 0) {
                        // Get the saved character attack
                        attackSelectionId = attack.getAttack().getId();
                    }
                    int position = 0;
                    for (Attack a : attackList) {
                        if (a.getId() == attackSelectionId) {
                            break;
                        }
                        position++;
                    }
                    vSpinnerAttack.setSelection(position);
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.attackEdit_saveButton) {
            if (!doSave()) {
                mCallBack.saveAttack();
            }
        } else if (v.getId() == R.id.attackEdit_cancelButton) {
            mCallBack.cancelAttack();
        }
    }

    private boolean doSave() {
        // For each UI field: get input value, check for errors, update mCharacterAttack field
        boolean errorFound = false;
        RPGCharacterAttack attack = mCallBack.getCharacterAttack();
        if (attack != null) {
            // Name - mandatory
            String name = vTextName.getText().toString().trim();
            if (TextUtils.isEmpty(name)) {
                vTextName.setError(getResources().getText(R.string.errorMandatory));
                errorFound = true;
            } else {
                attack.setName(name);
            }

            attack.setAttack((Attack) vSpinnerAttack.getSelectedItem());

            // Bonus - numeric, mandatory
            try {
                int bonus = Integer.parseInt(vTextBonus.getText().toString());
                attack.setBonus(bonus);
            } catch (NumberFormatException e) {
                errorFound = true;
                vTextBonus.setError(getResources().getText(R.string.errorMustBeANumber));
            }
        }
        return errorFound;
    }
}
