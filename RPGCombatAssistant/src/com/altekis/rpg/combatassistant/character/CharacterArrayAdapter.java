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
	private String[] groupHeaders;

	public CharacterArrayAdapter(Context context, List<List<RPGCharacter>> rpgCharacters, String[] groupHeaders) {
		super();
		this.context = context;
		this.rpgCharacters = rpgCharacters;
		this.groupHeaders = groupHeaders;
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
		String hitsInfo = Integer.toString(rpgCharacter.getHitPoints()) + "/" + Integer.toString(rpgCharacter.getMaxHitPoints());
		((TextView)convertView.findViewById(R.id.characterList_hitPoints)).setText(hitsInfo);

		// Change the icon for that of the user...
		ImageView imageView = (ImageView) convertView.findViewById(R.id.characterList_avatar);
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
		((TextView)headerView.findViewById(R.id.characterList_sectionHeader_title)).setText(groupHeaders[groupPosition]);

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