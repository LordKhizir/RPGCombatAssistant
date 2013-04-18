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
import com.altekis.rpg.combatassistant.RPGPreferences;
import com.altekis.rpg.combatassistant.character.CharacterAdapter;
import com.altekis.rpg.combatassistant.character.RPGCharacter;
import com.altekis.rpg.combatassistant.db.RuleSystem;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.List;

public class CharacterListFragment extends SherlockListFragment {

    public static CharacterListFragment newInstance(int filter) {
        Bundle args = new Bundle();
        args.putInt(FILTER_ARG, filter);
        CharacterListFragment frg = new CharacterListFragment();
        frg.setArguments(args);
        return frg;
    }

    public static final String FILTER_ARG = "filter";
    public static final int FILTER_PC = 0;
    public static final int FILTER_NPC = 1;
    public static final int FILTER_ALL = 2;

    public static interface CallBack extends DBFragmentActivity {
        void addCharacter();
        void characterClick(long id);
    }

    private CallBack mCallBack;
    private CharacterAdapter mAdapter;
    private int mFilter = FILTER_PC;

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
        setListShown(false);
        setEmptyText(getString(R.string.no_characters));
        RuleSystem system = RPGPreferences.getSystem(getSherlockActivity(), mCallBack.getHelper());
        mAdapter = new CharacterAdapter(getSherlockActivity(), null, system.getArmorType());
        setListAdapter(mAdapter);
        if (getArguments() != null) {
            mFilter = getArguments().getInt(FILTER_ARG);
        }
        if (savedInstanceState != null) {
            mFilter = savedInstanceState.getInt(FILTER_ARG, FILTER_PC);
        }
        loadCharacters();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(FILTER_ARG, mFilter);
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
        } else if (item.getGroupId() == R.id.menu_view) {
            int newFilter;
            if (item.getItemId() == R.id.menu_view_pc) {
                newFilter = FILTER_PC;
            } else if (item.getItemId() == R.id.menu_view_npc) {
                newFilter = FILTER_NPC;
            } else {
                newFilter = FILTER_ALL;
            }
            if (newFilter != mFilter) {
                mFilter = newFilter;
                loadCharacters();
            }
        }
        return true;
    }

    public void loadCharacters() {
        List<RPGCharacter> characters = null;
        try {
            Dao<RPGCharacter, Long> dao = mCallBack.getHelper().getDaoRPGCharacter();
            QueryBuilder<RPGCharacter, Long> qb = dao.queryBuilder();
            if (mFilter != FILTER_ALL) {
                boolean showNpc = mFilter == FILTER_NPC;
                qb.setWhere(qb.where().eq(RPGCharacter.FIELD_NPC, showNpc));
            }
            qb.orderBy(RPGCharacter.FIELD_NPC, true);
            qb.orderBy(RPGCharacter.FIELD_NAME, true);
            characters = dao.query(qb.prepare());
        } catch (SQLException e) {
            Log.e("RPGCombatAssistant", "Error loading characters", e);
        }
        mAdapter.setCharacters(characters);
        mAdapter.notifyDataSetChanged();
        setListShown(true);
    }
}
