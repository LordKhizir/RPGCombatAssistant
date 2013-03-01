package com.altekis.rpg.combatassistant.character;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.altekis.rpg.combatassistant.R;

import java.util.List;

public class CharacterAttackAdapter extends BaseAdapter {

    class AttackView {
        ImageView icon;
        TextView name;
        TextView info;
    }

    private final LayoutInflater inflater;
    private final List<RPGCharacterAttack> attacks;

    public CharacterAttackAdapter(Context context, List<RPGCharacterAttack> attacks) {
        this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.attacks = attacks;
    }

    @Override
    public int getCount() {
        int count = 0;
        if (attacks != null) {
            count = attacks.size();
        }
        return count;
    }

    @Override
    public Object getItem(int position) {
        return attacks.get(position);
    }

    @Override
    public long getItemId(int position) {
        return attacks.get(position).getId();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;
        AttackView attackView;
        if (view == null) {
            view = inflater.inflate(R.layout.character_attack_rowlayout, parent, false);
            attackView = new AttackView();
            attackView.icon = (ImageView) view.findViewById(R.id.character_attack_icon);
            attackView.name = (TextView) view.findViewById(R.id.character_attack_name);
            attackView.info = (TextView) view.findViewById(R.id.character_attack_info);
            view.setTag(attackView);
        } else {
            attackView = (AttackView) view.getTag();
        }

        RPGCharacterAttack attack = (RPGCharacterAttack) getItem(position);
        // Fill UI elements with current attack values
        attackView.name.setText(attack.getName());
        attackView.info.setText(attack.getAttack().getName());

        // Change the icon for that of the weapon
        // TODO link to real images
        attackView.icon.setImageResource(R.drawable.ic_launcher);

        return view;
    }
} 