package com.altekis.rpg.combatassistant.character;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class CharacterSpinnerAdapter extends ArrayAdapter<RPGCharacter> {

    private final Context mContext;
    private final LayoutInflater mInflater;
    private final int mResource;
    private int mDropDownResource;
    private final boolean mEmpty;

    public CharacterSpinnerAdapter(Context context, int textViewResourceId, List<RPGCharacter> objects, boolean empty) {
        super(context, textViewResourceId, objects);
        mContext = context;
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mResource = textViewResourceId;
        mEmpty = empty;
    }

    public CharacterSpinnerAdapter(Context context, int textViewResourceId, List<RPGCharacter> objects) {
        this(context, textViewResourceId, objects, false);
    }

    @Override
    public void setDropDownViewResource(int resource) {
        this.mDropDownResource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mResource);
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return createViewFromResource(position, convertView, parent, mDropDownResource);
    }

    @Override
    public long getItemId(int position) {
        return getItem(position).getId();
    }

    private View createViewFromResource(int position, View convertView, ViewGroup parent, int resource) {
        View view;
        TextView text;

        if (convertView == null) {
            view = mInflater.inflate(resource, parent, false);
        } else {
            view = convertView;
        }

        try {
            text = (TextView) view;
        } catch (ClassCastException e) {
            throw new IllegalStateException("ArrayAdapter requires the resource ID to be a TextView", e);
        }

        RPGCharacter item = getItem(position);
        if (mEmpty && item.getId() == 0) {
            text.setText(item.getName());
        } else {
            text.setText(item.getStringName(mContext));
        }

        return view;
    }
}
