package com.altekis.rpg.combatassistant.fragments;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockListFragment;
import com.altekis.rpg.combatassistant.R;

public class MainMenuFragment extends SherlockListFragment {

    public static enum MenuOption {
        PLAYERS_PC,
        PLAYERS_NPC,
        PLAYERS_ALL,
        ATTACK,
        CRITICAL,
        MM
    }

    public static interface CallBack {
        void optionMenuClicked(MenuOption menuOption);
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_menu_main, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setListAdapter(new MenuAdapter(getSherlockActivity()));
    }

    @Override
    public void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (mCallBack != null) {
            MenuOption menuOption = null;
            if (id == R.string.main_menu_players_pc) {
                menuOption = MenuOption.PLAYERS_PC;
            } else if (id == R.string.main_menu_players_npc) {
                menuOption = MenuOption.PLAYERS_NPC;
            } else if (id == R.string.main_menu_players_all) {
                menuOption = MenuOption.PLAYERS_ALL;
            } else if (id == R.string.main_menu_attack_start) {
                menuOption = MenuOption.ATTACK;
            } else if (id == R.string.main_menu_attack_critical) {
                menuOption = MenuOption.CRITICAL;
            } else if (id == R.string.main_menu_other_mm) {
                menuOption = MenuOption.MM;
            }

            if (menuOption != null) {
                mCallBack.optionMenuClicked(menuOption);
            }
        }
    }

    static final int[] TITLES = {
            R.string.main_menu_players_title,
            R.string.main_menu_players_pc,
            R.string.main_menu_players_npc,
            R.string.main_menu_players_all,
            R.string.main_menu_attack_title,
            R.string.main_menu_attack_start,
            R.string.main_menu_attack_critical,
            R.string.main_menu_other_title,
            R.string.main_menu_other_mm
    };

    static final int[] TYPE_TITLES = {
            0,
            1,
            1,
            1,
            0,
            1,
            1,
            0,
            1
    };

    static class MenuAdapter extends BaseAdapter {

        private LayoutInflater inflater;

        MenuAdapter(Context context) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return TITLES.length;
        }

        @Override
        public Object getItem(int position) {
            return TITLES[position];
        }

        @Override
        public long getItemId(int position) {
            return TITLES[position];
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = (TextView) convertView;
            if (textView == null) {
                if (getItemViewType(position) == 0) {
                    textView = (TextView) inflater.inflate(R.layout.activity_main_item_title, parent, false);
                } else {
                    textView = (TextView) inflater.inflate(R.layout.activity_main_item, parent, false);
                }
            }
            textView.setText(TITLES[position]);
            return textView;
        }

        @Override
        public boolean areAllItemsEnabled() {
            return false;
        }

        @Override
        public int getViewTypeCount() {
            return 2;
        }

        @Override
        public int getItemViewType(int position) {
            return TYPE_TITLES[position];
        }
    }

}
