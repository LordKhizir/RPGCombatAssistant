package com.altekis.rpg.combatassistant.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.altekis.rpg.combatassistant.R;
import com.altekis.rpg.combatassistant.attack.AttackResult;
import com.altekis.rpg.combatassistant.character.RPGCharacterAttack;

public class AttackDialogFragment extends SherlockDialogFragment {

    public static interface CallBack extends DBFragmentActivity {
        AttackResult getResultAttack();
        void applyResultAttack(boolean apply, boolean critical);
    }

    private CallBack mCallBack;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (activity instanceof CallBack) {
            mCallBack = (CallBack) activity;
        } else {
            throw new IllegalStateException("Activity must implement CallBack's Fragment");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mCallBack = null;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getSherlockActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        AttackResult attackResult = mCallBack.getResultAttack();

        if (attackResult != null) {
            View v = inflater.inflate(R.layout.fragment_dialog_attack, null);
            final TextView messageText = (TextView) v.findViewById(android.R.id.message);
            final CheckBox applyCheck = (CheckBox) v.findViewById(R.id.check_apply);
            builder.setView(v);
            RPGCharacterAttack attack = attackResult.getCharacterAttack();

            final StringBuilder message = new StringBuilder();
            message.append(attack.getRPGCharacter().getStringName(getSherlockActivity()));
            message.append("\n");
            if (attackResult.isFumbled()) {
                message.append("Fumble!");
            } else {
                message.append(attackResult.getHitPoints());
                message.append(" points");
            }

            final int positiveButton;
            final boolean critical;
            if (attackResult.getCriticalLevel() == null) {
                positiveButton = android.R.string.ok;
                critical = false;
            } else {
                message.append("\n");
                message.append(getString(R.string.attack_critical,
                        attackResult.getCritical().getName(),
                        attackResult.getCriticalLevel().toString()));
                positiveButton = R.string.attack_goToCriticalButton;
                critical = true;
            }

            messageText.setText(message);

            if (attackResult.getCharacterDefender() == null) {
                applyCheck.setVisibility(View.GONE);
            }

            builder.setTitle("Result attack");

            builder.setPositiveButton(positiveButton, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mCallBack.applyResultAttack(applyCheck.isChecked(), critical);
                }
            });

            builder.setNegativeButton(android.R.string.cancel, null);
        }
        return builder.create();
    }
}
