package com.altekis.rpg.combatassistant.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.altekis.rpg.combatassistant.R;
import com.altekis.rpg.combatassistant.db.RuleSystem;
import com.j256.ormlite.dao.Dao;

import java.io.File;
import java.sql.SQLException;
import java.util.List;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class RuleSystemListFragment extends SherlockListFragment {

    public static interface CallBack extends DBFragmentActivity {
        void importRuleSystem();
        void ruleSystemClick(long id);
    }

    private CallBack mCallBack;

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
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setHasOptionsMenu(true);
        loadData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.activity_import, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_import) {
            mCallBack.importRuleSystem();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void loadData() {
         try {
             Dao<RuleSystem, Long> dao = mCallBack.getHelper().getDaoSystem();
             List<RuleSystem> lst = dao.query(dao.queryBuilder().orderBy(RuleSystem.FIELD_NAME, true).prepare());
             RuleSystemAdapter adapter = new RuleSystemAdapter(getSherlockActivity(), android.R.layout.simple_list_item_1, lst);
             setListAdapter(adapter);
         } catch (SQLException e) {
             Log.e("RPGCombatAssistant", "Error reading database", e);
         }
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        mCallBack.ruleSystemClick(id);
    }

    static class RuleSystemAdapter extends ArrayAdapter<RuleSystem> {

        public RuleSystemAdapter(Context context, int textViewResourceId, List<RuleSystem> objects) {
            super(context, textViewResourceId, objects);
        }

        @Override
        public long getItemId(int position) {
            return getItem(position).getId();
        }
    }
}
