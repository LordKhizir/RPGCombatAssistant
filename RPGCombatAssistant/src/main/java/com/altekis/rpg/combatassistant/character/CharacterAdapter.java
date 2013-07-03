package com.altekis.rpg.combatassistant.character;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.altekis.rpg.combatassistant.R;
import com.altekis.rpg.combatassistant.db.RuleSystem;

import java.util.List;

public class CharacterAdapter extends BaseAdapter {

    class CharacterView {
        TextView name;
        TextView hitPoints;
        TextView armor;
    }

    private final Context context;
    private final LayoutInflater inflater;
    private final int armorTypeSystem;
    private List<RPGCharacter> characters;

    public CharacterAdapter(Context context, List<RPGCharacter> characters, int armorTypeSystem) {
        this.context = context;
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.characters = characters;
        this.armorTypeSystem = armorTypeSystem;
    }

    public void setCharacters(List<RPGCharacter> characters) {
        this.characters = characters;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (characters != null) {
            count = characters.size();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        return characters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return characters.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        CharacterView characterView;
        if (view == null) {
            view = inflater.inflate(R.layout.fragment_character_list_element, parent, false);
            characterView = new CharacterView();
            characterView.name = (TextView) view.findViewById(R.id.character_nameLabel);
            characterView.hitPoints = (TextView) view.findViewById(R.id.character_hitPoints);
            characterView.armor = (TextView) view.findViewById(R.id.character_armor);
            view.setTag(characterView);
        } else {
            characterView = (CharacterView) view.getTag();
        }

        RPGCharacter c = (RPGCharacter) getItem(position);

        characterView.name.setText(c.getStringName(context));
        characterView.hitPoints.setText(context.getString(R.string.character_name, c.getHitPoints(), c.getMaxHitPoints()));

        ArmorType armorType = ArmorType.fromInteger(c.getArmorType());
        if (armorTypeSystem == RuleSystem.ARMOR_SIMPLE) {
            characterView.armor.setText(context.getString(armorType.getMerpString()));
        } else {
            characterView.armor.setText(context.getString(armorType.getRmString()));
        }


        return view;
    }
} 