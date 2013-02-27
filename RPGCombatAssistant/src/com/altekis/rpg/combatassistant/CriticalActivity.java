package com.altekis.rpg.combatassistant;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.altekis.rpg.combatassistant.critical.CriticalLevel;
import com.altekis.rpg.combatassistant.critical.Critical;
import com.altekis.rpg.combatassistant.db.DBUtil;
import com.j256.ormlite.dao.Dao;

public class CriticalActivity extends BaseActivity {

	private Critical selectedCritical;
	private CriticalLevel selectedCriticalLevel;
    private List<Critical> criticalList;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_critical);

		// Get Extras
        // TODO
        long criticalId = getIntent().getLongExtra("CriticalId", 0);
        try {
            Dao<Critical, Long> dao = getHelper().getDaoCritical();
            selectedCritical = dao.queryForId(criticalId);
            criticalList = dao.queryForAll();
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Can't read database", e);
        }
        selectedCriticalLevel = CriticalLevel.valueOf(getIntent().getStringExtra("CriticalLevelName"));

		// Spinner for critical type
		Spinner criticalTypeSpinner = (Spinner) findViewById(R.id.critical_type);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<Critical> criticalTypeAdapter = new ArrayAdapter<Critical>(this,
				android.R.layout.simple_spinner_item,
				new ArrayList<Critical>(criticalList));
		// Specify the layout to use when the list of choices appears
		criticalTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		criticalTypeSpinner.setAdapter(criticalTypeAdapter);
		criticalTypeSpinner.setOnItemSelectedListener(new CriticalTypeSelectedListener());
		
		// Spinner for critical level
		Spinner criticalLevelSpinner = (Spinner) findViewById(R.id.critical_level);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CriticalLevel> criticalLevelAdapter = new ArrayAdapter<CriticalLevel>(this,
				android.R.layout.simple_spinner_item,
				CriticalLevel.values());
		// Specify the layout to use when the list of choices appears
		criticalLevelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		criticalLevelSpinner.setAdapter(criticalLevelAdapter);
		criticalLevelSpinner.setOnItemSelectedListener(new CriticalLevelSelectedListener());
		
		// Set UI
		// Select spinner position - Critical Type
		int position = 0;
		for (Critical crit : criticalList) {
			if (crit.getId() == selectedCritical.getId()) {
				criticalTypeSpinner.setSelection(position);
				break;
			}
			position++;
		}

		// Select spinner position - Critical Level
		position = 0;
		for (CriticalLevel level:CriticalLevel.values()) {
			if (level.equals(selectedCriticalLevel)) {
				criticalLevelSpinner.setSelection(position);
				break;
			}
			position++;
		}
		
		// Add listeners for buttons
		Button btnCancel = (Button) findViewById(R.id.critical_cancelButton);
        btnCancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doCancel();
			}
		});

		Button btnGo = (Button) findViewById(R.id.critical_goButton);
        btnGo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				doGo();
			}
		});
	}
	
	/** Nested class for spinner value recovery */
	public class CriticalTypeSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			selectedCritical = ((Critical)parent.getItemAtPosition(pos));
		}

		public void onNothingSelected(AdapterView<?> parent) {
			selectedCritical = null;
		}
	}
	
	/** Nested class for spinner value recovery */
	public class CriticalLevelSelectedListener implements OnItemSelectedListener {

		public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
			selectedCriticalLevel = ((CriticalLevel)parent.getItemAtPosition(pos));
		}

		public void onNothingSelected(AdapterView<?> parent) {
			selectedCriticalLevel = null;
		}
	}

	/**
	 * Ignore changes, and go back to caller
	 */
	private void doCancel() {
		// Set result as CANCELED
		setResult(RESULT_CANCELED);
		finish();
	}
	
	/**
	 * Get all info and calculate attack result
	 */
	private void doGo() {
		// Get UI widgets
		EditText rollText = (EditText) findViewById(R.id.critical_roll);
		TextView resultText = (TextView) findViewById(R.id.critical_result);

		int roll = 0;
		int total = 0;
		String resultMessage = "";

		boolean errorFound = false;
		// Check for errors on the UI as a whole
		try {
			roll = Integer.parseInt(rollText.getText().toString());
		} catch (NumberFormatException e) {
			errorFound = true;
			rollText.setError(getResources().getText(R.string.errorMustBeANumber));
		}
		
		if (!errorFound) {
			resultMessage = DBUtil.getCritical(getHelper(), selectedCritical, selectedCriticalLevel, roll);
		}
		// Show result of attack
		resultText.setText(resultMessage);
	}
}
