package com.altekis.rpg.combatassistant.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
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
import com.altekis.rpg.combatassistant.critical.Critical;
import com.altekis.rpg.combatassistant.critical.CriticalLevel;
import com.altekis.rpg.combatassistant.critical.CriticalSpinnerAdapter;
import com.altekis.rpg.combatassistant.db.DBUtil;
import com.altekis.rpg.combatassistant.db.RuleSystem;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class CriticalFragment extends SherlockFragment implements View.OnClickListener {

    private static final String ARG_CRITICAL_ID = "CriticalId";
    private static final String ARG_CRITICAL_LEVEL = "CriticalLevelName";

    public static CriticalFragment newInstance(long criticalId, CriticalLevel criticalLevel) {
        Bundle args = new Bundle();
        args.putLong(ARG_CRITICAL_ID, criticalId);
        args.putSerializable(ARG_CRITICAL_LEVEL, criticalLevel);
        CriticalFragment frg = new CriticalFragment();
        frg.setArguments(args);
        return frg;
    }

    public static interface CallBack extends DBFragmentActivity {
        void cancelCritical();
    }

    private CallBack mCallBack;

	private List<Critical> criticalList;

    private EditText vTextRoll;
    private Spinner vSpinnerCriticalType;
    private Spinner vSpinnerCriticalLevel;
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
        View v = inflater.inflate(R.layout.fragment_critical, container, false);
        vTextRoll = (EditText) v.findViewById(R.id.critical_roll);
        vSpinnerCriticalType = (Spinner) v.findViewById(R.id.critical_type);
        vSpinnerCriticalLevel = (Spinner) v.findViewById(R.id.critical_level);
        vTextResult = (TextView) v.findViewById(R.id.critical_result);
        v.findViewById(R.id.critical_cancelButton).setOnClickListener(this);
        v.findViewById(R.id.critical_goButton).setOnClickListener(this);
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        // Get Extras
        long criticalId = 0;
        CriticalLevel criticalLevel = null;
        if (getArguments() != null) {
            criticalId = getArguments().getLong(ARG_CRITICAL_ID, 0);
            criticalLevel = (CriticalLevel) getArguments().getSerializable(ARG_CRITICAL_LEVEL);
        }
        try {
            RuleSystem system = RPGPreferences.getSystem(getSherlockActivity(), mCallBack.getHelper());
            Dao<Critical, Long> dao = mCallBack.getHelper().getDaoCritical();
            QueryBuilder<Critical, Long> qb = dao.queryBuilder();
            qb.setWhere(qb.where().eq(Critical.FIELD_SYSTEM_ID, system.getId()));
            qb.orderBy(Critical.FIELD_NAME, true);
            criticalList = dao.query(qb.prepare());
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Can't read database", e);
        }

        // Create an ArrayAdapter using the string array and a default spinner layout
        CriticalSpinnerAdapter criticalTypeAdapter = new CriticalSpinnerAdapter(getSherlockActivity(),
                android.R.layout.simple_spinner_item,
                criticalList);
        // Specify the layout to use when the list of choices appears
        criticalTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        vSpinnerCriticalType.setAdapter(criticalTypeAdapter);
        // Select spinner position - Critical Type
        int position = 0;
        for (Critical crit : criticalList) {
            if (crit.getId() == criticalId) {
                vSpinnerCriticalType.setSelection(position);
                break;
            }
            position++;
        }

        ArrayAdapter<CriticalLevel> criticalLevelAdapter = new ArrayAdapter<CriticalLevel>(getSherlockActivity(),
                android.R.layout.simple_spinner_item,
                CriticalLevel.values());
        // Specify the layout to use when the list of choices appears
        criticalLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        vSpinnerCriticalLevel.setAdapter(criticalLevelAdapter);
        // Select spinner position - Critical Level
        position = 0;
        if (criticalLevel != null) {
            for (CriticalLevel level:CriticalLevel.values()) {
                if (level.equals(criticalLevel)) {
                    vSpinnerCriticalLevel.setSelection(position);
                    break;
                }
                position++;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.critical_cancelButton) {
            mCallBack.cancelCritical();
        } else if (v.getId() == R.id.critical_goButton) {
            int roll = 0;
            int total = 0;
            String resultMessage = "";

            boolean errorFound = false;
            // Check for errors on the UI as a whole
            try {
                roll = Integer.parseInt(vTextRoll.getText().toString());
            } catch (NumberFormatException e) {
                errorFound = true;
                vTextRoll.setError(getResources().getText(R.string.errorMustBeANumber));
            }

            if (!errorFound) {
                Critical selectedCritical = (Critical) vSpinnerCriticalType.getSelectedItem();
                CriticalLevel selectedCriticalLevel = (CriticalLevel) vSpinnerCriticalLevel.getSelectedItem();
                resultMessage = DBUtil.getCritical(mCallBack.getHelper(), selectedCritical, selectedCriticalLevel, roll);
            }

            // Show result of attack
            vTextResult.setText(resultMessage);
        }
    }
}
