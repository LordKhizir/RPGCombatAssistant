package com.altekis.rpg.combatassistant.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockFragment;
import com.altekis.rpg.combatassistant.R;
import com.altekis.rpg.combatassistant.RPGPreferences;
import com.altekis.rpg.combatassistant.db.DBUtil;
import com.altekis.rpg.combatassistant.db.RuleSystem;
import com.altekis.rpg.combatassistant.maneuver.DifficultyType;

public class MovingFumbleFragment extends SherlockFragment implements View.OnClickListener {

    public static final MovingFumbleFragment newInstance(DifficultyType difficultyType) {
        Bundle args = new Bundle();
        args.putSerializable(DIFFICULTY_TYPE_ARG, difficultyType);
        MovingFumbleFragment frg = new MovingFumbleFragment();
        frg.setArguments(args);
        return frg;
    }

    private static final String DIFFICULTY_TYPE_ARG = "difficulty-type";

    public static interface CallBack extends DBFragmentActivity {
        void cancelMovingFumble();
    }

    private CallBack mCallBack;
    private DifficultyType[] mDifficultyTypes;

    private Spinner vSpinnerDifficulty;
    private EditText vEditRoll;
    private TextView vTextResult;

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
        View v = inflater.inflate(R.layout.fragment_moving_fumble, container, false);
        vSpinnerDifficulty = (Spinner) v.findViewById(R.id.moving_difficulty);
        vEditRoll = (EditText) v.findViewById(R.id.moving_roll);
        vTextResult = (TextView) v.findViewById(R.id.moving_result);
        v.findViewById(R.id.moving_ok).setOnClickListener(this);
        v.findViewById(R.id.moving_cancel).setOnClickListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        DifficultyType selected = null;
        if (savedInstanceState != null) {
            selected = (DifficultyType) savedInstanceState.getSerializable(DIFFICULTY_TYPE_ARG);
        }
        if (selected == null && getArguments() != null) {
            selected = (DifficultyType) getArguments().getSerializable(DIFFICULTY_TYPE_ARG);
        }
        if (selected == null) {
            selected = DifficultyType.EASY;
        }

        mDifficultyTypes = DifficultyType.values();
        final String[] titles = new String[mDifficultyTypes.length];
        int pos = 0;
        for (int i = 0 ; i < titles.length ; i++) {
            titles[i] = getString(mDifficultyTypes[i].getTitle());
            if (selected == mDifficultyTypes[i]) {
                pos = i;
            }
        }
        ArrayAdapter<String> armorAdapter = new ArrayAdapter<String>(getSherlockActivity(),
                android.R.layout.simple_spinner_item,
                titles);
        armorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        vSpinnerDifficulty.setAdapter(armorAdapter);
        vSpinnerDifficulty.setSelection(pos);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.moving_cancel) {
            mCallBack.cancelMovingFumble();
        } else if (v.getId() == R.id.moving_ok) {
            processFumble();
        }
    }

    private void processFumble() {
        boolean errorFound = false;
        int roll = 0;
        String rollRaw = vEditRoll.getText().toString().trim();
        if (rollRaw.length() == 0) {
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
            RuleSystem system = RPGPreferences.getSystem(getSherlockActivity(), mCallBack.getHelper());

            String result = DBUtil.getMovingFumble(mCallBack.getHelper(), system, diff, roll);
            vTextResult.setText(result);
        }
    }

}
