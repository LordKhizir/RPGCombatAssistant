package com.altekis.rpg.combatassistant.critical;

import java.io.InputStream;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import android.content.Context;
import android.util.Log;

public class LAOCritical {
	/**
	 * Load and parse a Critical from a XML file
	 * @param context
	 * @param name
	 * @return
	 */
	public static Critical loadCritical(Context context, String name) {
		// Inflate static XML file
		Critical crit = new Critical();
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
						crit.setName(parser.nextText());
					} else if ("key".equals(tagName)) {
						crit.setKey(parser.nextText());
					} else if ("range".equals(tagName)) {
						int min = Integer.parseInt(parser.getAttributeValue(null, "min"));
						String text = parser.nextText();
						crit.addResult(min, text);
					}
				}
				eventType = parser.next();
			}			
		} catch (Exception e) {
			Log.i("RPGCombatAssistant", "Unable to load " + name + ". " + e.toString());
			return null;
		}
		return crit;
	}

}
