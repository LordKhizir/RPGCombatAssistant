package com.altekis.rpg.combatassistant.attack;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

public class LAOAttackType {
	/**
	 * Load and parse an Attack Type from a XML file
	 * @param context
	 * @param name
	 * @return
	 */
	public static AttackType loadAttackType(Context context, String name) {
		// Inflate static XML file
		AttackType attackType = new AttackType();
		try {
			InputStream in = context.getAssets().open("tables/" + name);
			XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
			factory.setNamespaceAware(true);
			XmlPullParser parser;
			parser = factory.newPullParser();
			parser.setInput(in, "UTF-8");

			int eventType = parser.getEventType();
			while (eventType != XmlPullParser.END_DOCUMENT) {
				if(eventType == XmlPullParser.START_TAG) {
					String tagName = parser.getName();
					if ("name".equals(tagName)) {
						attackType.setName(parser.nextText());
					} else if ("key".equals(tagName)) {
						attackType.setKey(parser.nextText());
					} else if ("fumble".equals(tagName)) {
						int max = Integer.parseInt(parser.getAttributeValue(null, "max"));
						attackType.setMaxFumble(max);
					} else if ("range".equals(tagName)) {
						int min = Integer.parseInt(parser.getAttributeValue(null, "min"));
						String plate = parser.getAttributeValue(null, "p");
						String chainmail = parser.getAttributeValue(null, "c");
						String hardleather = parser.getAttributeValue(null, "hl");
						String softleather = parser.getAttributeValue(null, "sl");
						String unarmored = parser.getAttributeValue(null, "u");
						String[] values = {unarmored,softleather,hardleather,chainmail,plate};
						attackType.addResult(min, values);
					}
				}
				eventType = parser.next();
			}			
		} catch (Exception e) {
			Log.i("RPGCombatAssistant", "Unable to load " + name + ". " + e.toString());
			return null;
		}
		return attackType;
	}
}
