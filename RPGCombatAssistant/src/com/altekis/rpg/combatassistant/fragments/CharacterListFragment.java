package com.altekis.rpg.combatassistant.fragments;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import com.actionbarsherlock.app.SherlockListFragment;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.altekis.rpg.combatassistant.R;
import com.altekis.rpg.combatassistant.character.CharacterAdapter;
import com.altekis.rpg.combatassistant.character.RPGCharacter;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class CharacterListFragment extends SherlockListFragment {

    public static interface CallBack extends DBFragmentActivity {
        void addCharacter();
        void characterClick(long id);
    }

    private CallBack mCallBack;
    private CharacterAdapter mAdapter;

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
        // This fragment has it's own menu
        setHasOptionsMenu(true);
        mAdapter = new CharacterAdapter(getSherlockActivity(), null, false);
        setListAdapter(mAdapter);
        loadCharacters();
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        if (mCallBack != null) {
            mCallBack.characterClick(id);
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.fragment_character_list, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_add) {
            mCallBack.addCharacter();
        }
        return true;
    }

    public void loadCharacters() {
        List<RPGCharacter> characters = null;
        try {
            Dao<RPGCharacter, Long> dao = mCallBack.getHelper().getDaoRPGCharacter();
            QueryBuilder<RPGCharacter, Long> qb = dao.queryBuilder();
            qb.setWhere(qb.where().eq(RPGCharacter.FIELD_PNJ, false));
            qb.orderBy(RPGCharacter.FIELD_PNJ, true);
            qb.orderBy(RPGCharacter.FIELD_NAME, true);
            characters = dao.query(qb.prepare());
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Error loading characters", e);
        }
        mAdapter.setCharacters(characters);
        mAdapter.notifyDataSetChanged();
    }
}
