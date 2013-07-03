package com.altekis.rpg.combatassistant.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.actionbarsherlock.app.SherlockFragment;
import com.altekis.rpg.combatassistant.R;
import com.altekis.rpg.combatassistant.RPGPreferences;
import com.altekis.rpg.combatassistant.db.DBUtil;
import com.altekis.rpg.combatassistant.db.RuleSystem;
import com.altekis.rpg.combatassistant.maneuver.DifficultyType;
import com.altekis.rpg.combatassistant.maneuver.MovingResult;

public class MovingFragment extends SherlockFragment implements View.OnClickListener {

    public static final String MOVING_RESULT_ARG = "moving-result";
    public static final String DIFFICULTY_TYPE_ARG = "difficulty-type";

    public static interface CallBack extends DBFragmentActivity {
        void cancelMovingManeuver();
        void rollFumble(DifficultyType difficultyType);
    }

    private CallBack mCallBack;
    private RuleSystem mRuleSystem;
    private DifficultyType[] mDifficultyTypes;
    private MovingResult mMovingResult;

    private Spinner vSpinnerDifficulty;
    private EditText vEditRoll;
    private EditText vEditBonus;
    private TextView vTextResult;
    private Button vButtonFumble;

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
        View v = inflater.inflate(R.layout.fragment_moving, container, false);
        vSpinnerDifficulty = (Spinner) v.findViewById(R.id.moving_difficulty);
        vEditRoll = (EditText) v.findViewById(R.id.moving_roll);
        vEditBonus = (EditText) v.findViewById(R.id.moving_bonus);
        vTextResult = (TextView) v.findViewById(R.id.moving_result);
        vButtonFumble = (Button) v.findViewById(R.id.moving_fumble);
        vButtonFumble.setVisibility(View.GONE);
        vButtonFumble.setOnClickListener(this);
        v.findViewById(R.id.moving_ok).setOnClickListener(this);
        v.findViewById(R.id.moving_cancel).setOnClickListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mRuleSystem = RPGPreferences.getSystem(getSherlockActivity(), mCallBack.getHelper());

        int position = 0;
        if (savedInstanceState != null) {
            position = savedInstanceState.getInt(DIFFICULTY_TYPE_ARG, 0);
            if (savedInstanceState.containsKey(MOVING_RESULT_ARG)) {
                mMovingResult = new MovingResult(savedInstanceState.getString(MOVING_RESULT_ARG));
            }
        }

        mDifficultyTypes = DifficultyType.values();
        final String[] titles = new String[mDifficultyTypes.length];
        for (int i = 0 ; i < titles.length ; i++) {
            titles[i] = getString(mDifficultyTypes[i].getTitle());
        }
        ArrayAdapter<String> armorAdapter = new ArrayAdapter<String>(getSherlockActivity(),
                android.R.layout.simple_spinner_item,
                titles);
        armorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vSpinnerDifficulty.setAdapter(armorAdapter);
        vSpinnerDifficulty.setSelection(position);

        if (mMovingResult != null) {
            printMovingResult(mDifficultyTypes[position]);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mMovingResult != null) {
            outState.putString(MOVING_RESULT_ARG, mMovingResult.getResult());
        }
        outState.putInt(DIFFICULTY_TYPE_ARG, vSpinnerDifficulty.getSelectedItemPosition());
    }

    private void printMovingResult(DifficultyType diff) {
        if (mMovingResult.isFumbled()) {
            vTextResult.setText("¡Pifia!");
        } else if (mMovingResult.isNumeric()) {
            vTextResult.setText("Result: " + mMovingResult.getResult());
        } else {
            vTextResult.setText(mMovingResult.getResult());
        }

        if (mRuleSystem.getMovingType() == RuleSystem.MOVING_FUMBLE && mMovingResult.isFumbled()) {
            vButtonFumble.setVisibility(View.VISIBLE);
        } else {
            vButtonFumble.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.moving_cancel) {
            mCallBack.cancelMovingManeuver();
        } else if (v.getId() == R.id.moving_ok) {
            processMoving();
        } else if (v.getId() == R.id.moving_fumble) {
            final DifficultyType type = mDifficultyTypes[vSpinnerDifficulty.getSelectedItemPosition()];
            mCallBack.rollFumble(type);
        }
    }

    private void processMoving() {
        boolean errorFound = false;
        int bonus = 0;
        int roll = 0;
        String bonusRaw = vEditBonus.getText().toString().trim();
        if (bonusRaw.length() == 0) {
            bonus = 0; // 0 allowed
        } else {
            try {
                bonus = Integer.parseInt(bonusRaw);
            } catch (NumberFormatException e) {
                errorFound = true;
                vEditBonus.setError(getResources().getText(R.string.errorMustBeANumber));
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
            final DifficultyType diff = mDifficultyTypes[vSpinnerDifficulty.getSelectedItemPosition()];
            mMovingResult = DBUtil.getMoving(mCallBack.getHelper(), mRuleSystem, diff, (bonus + roll));
            printMovingResult(diff);
        }
    }

}
