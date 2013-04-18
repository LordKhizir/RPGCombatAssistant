package com.altekis.rpg.combatassistant;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.actionbarsherlock.app.SherlockListActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.altekis.rpg.combatassistant.db.DatabaseHelper;
import com.altekis.rpg.combatassistant.fragments.CharacterListFragment;

public class MainActivity extends SherlockListActivity {

    private static final int REQUEST_INIT_DB = 1;

    private boolean mDbInitialized;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setListAdapter(new MenuAdapter(this));

        // Check if database is initialised
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
        mDbInitialized = sp.getBoolean(DatabaseHelper.DB_INITIALISED, false);
        if (!mDbInitialized) {
            // Not initialised, launch splash
            startActivityForResult(new Intent(this, SplashScreenActivity.class), REQUEST_INIT_DB);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_INIT_DB) {
            if (resultCode == RESULT_OK) {
                SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(this);
                sp.edit().putBoolean(DatabaseHelper.DB_INITIALISED, true).commit();
                mDbInitialized = true;
            } else {
                // TODO Advice user
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getSupportMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    @Override
    public boolean onMenuItemSelected(int featureId, MenuItem item) {
        if (item.getItemId() == R.id.menu_settings) {
            startActivity(new Intent(this, RPGPreferences.class));
            return true;
        } else {
            return super.onMenuItemSelected(featureId, item);
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        super.onListItemClick(l, v, position, id);
        if (mDbInitialized) {
            Intent intent = null;
            if (id == R.string.menu_players_pc || id == R.string.menu_players_npc || id == R.string.menu_players_all) {
                intent = new Intent(this, CharacterActivity.class);
                int filter;
                if (id == R.string.menu_players_pc) {
                    filter = CharacterListFragment.FILTER_PC;
                } else if (id == R.string.menu_players_npc) {
                    filter = CharacterListFragment.FILTER_NPC;
                } else {
                    filter = CharacterListFragment.FILTER_ALL;
                }
                intent.putExtra(CharacterListFragment.FILTER_ARG, filter);
            } else if (id == R.string.menu_attack_start) {
                intent = new Intent(this, AttackActivity.class);
            } else if (id == R.string.menu_attack_critical) {
                intent = new Intent(this, AttackActivity.class);
                intent.putExtra(AttackActivity.ARG_CRITICAL, true);
            } else if (id == R.string.menu_other_mm) {
                intent = new Intent(this, MovingActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
            }
        }
    }

    static final int[] TITLES = {
            R.string.menu_players_title,
            R.string.menu_players_pc,
            R.string.menu_players_npc,
            R.string.menu_players_all,
            R.string.menu_attack_title,
            R.string.menu_attack_start,
            R.string.menu_attack_critical,
            R.string.menu_other_title,
            R.string.menu_other_mm
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