package com.altekis.rpg.combatassistant.character;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.altekis.rpg.combatassistant.R;

public class CharacterArrayAdapter extends BaseExpandableListAdapter {
	private final Context context;
	private final List<List<RPGCharacter>> rpgCharacters;

	public CharacterArrayAdapter(Context context, List<List<RPGCharacter>> rpgCharacters) {
		super();
		this.context = context;
		this.rpgCharacters = rpgCharacters;
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return rpgCharacters.get(groupPosition).get(childPosition);
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		return ((RPGCharacter)getChild(groupPosition,childPosition)).getId();
	}

	@Override
	public View getChildView(int groupPosition, int childPosition,
			boolean isLastChild, View convertView, ViewGroup parent) {
		RPGCharacter rpgCharacter = (RPGCharacter)getChild(groupPosition, childPosition);
		if (convertView==null) {
			LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			convertView = inflater.inflate(R.layout.character_rowlayout, parent, false);
		}

		// Fill UI elements with current character values
		((TextView)convertView.findViewById(R.id.characterList_name)).setText(rpgCharacter.getName());
		((TextView)convertView.findViewById(R.id.characterList_playerName)).setText(rpgCharacter.getPlayerName());

		// Change the icon for that of the user...
		ImageView imageView = (ImageView) convertView.findViewById(R.id.gameRow_icon);
		String s = rpgCharacter.getName();
		if (s.length()%2==1) {
			imageView.setImageResource(R.drawable.ic_action_search);
		} else {
			imageView.setImageResource(R.drawable.ic_launcher);
		}

		return convertView;
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		return rpgCharacters.get(groupPosition).size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return rpgCharacters.get(groupPosition);
	}

	@Override
	public int getGroupCount() {
		return rpgCharacters.size();
	}

	@Override
	public long getGroupId(int groupPosition) {
		return groupPosition;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded,
			View convertView, ViewGroup parent) {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View headerView = inflater.inflate(R.layout.character_header_layout, parent, false);

		// Fill UI elements with current header
		String text = "Wrong turn status (" + groupPosition + ")";

		switch (groupPosition) {
		case 0: //TODO Avoid magic number!
			text = "Player characters"; // TODO To be i18n
			break;
		case 1:
			text = "Non-player characters";
			break;
		}
		((TextView)headerView.findViewById(R.id.characterList_sectionHeader_title)).setText(text);

		return headerView;
	}

	@Override
	public boolean hasStableIds() {
		return true;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		return true;
	}
} 