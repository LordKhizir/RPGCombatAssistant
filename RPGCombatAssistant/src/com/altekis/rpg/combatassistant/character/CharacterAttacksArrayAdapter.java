package com.altekis.rpg.combatassistant.character;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.altekis.rpg.combatassistant.R;
import com.altekis.rpg.combatassistant.RPGCombatAssistant;
import com.altekis.rpg.combatassistant.attack.Attack;
import com.altekis.rpg.combatassistant.attack.AttackType;

public class CharacterAttacksArrayAdapter extends ArrayAdapter<Attack> {
	private final Context context;
	private final List<Attack> attacks;

	public CharacterAttacksArrayAdapter(Context context, List<Attack> attacks) {
		super(context, R.layout.character_attack_rowlayout, attacks);
		this.context = context;
		this.attacks = attacks;
	}
	
	 @Override
	  public View getView(int position, View convertView, ViewGroup parent) {
		Attack attack = getItem(position);
		if (convertView==null) {
		    LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		    convertView = inflater.inflate(R.layout.character_attack_rowlayout, parent, false);
		}
	
		// Fill UI elements with current attack values
		((TextView)convertView.findViewById(R.id.character_attack_name)).setText(attack.getName());
		AttackType attackType = RPGCombatAssistant.attackTypes.get(attack.getAttackType());
		((TextView)convertView.findViewById(R.id.character_attack_info)).setText(attackType.getName());

		// Change the icon for that of the weapon
		// TODO link to real images
		ImageView imageView = (ImageView) convertView.findViewById(R.id.character_attack_icon);
		imageView.setImageResource(R.drawable.ic_launcher);

		return convertView;
	  }

	@Override
	public long getItemId(int position) {
		return attacks.get(position).getId();
	}	 
} 