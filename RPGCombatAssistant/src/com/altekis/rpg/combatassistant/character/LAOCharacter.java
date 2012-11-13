package com.altekis.rpg.combatassistant.character;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import android.content.Context;

public class LAOCharacter {
	Context context;
	//DAOGame daoGame;
	
	private String[] names = {
			"Aphrodite",
    		"Artemis",
    		"Athena",
    		"Demeter",
    		"Hera",
    		"Hestia",
    		"Persephone",
    		"Selene"};

	private String[] playerNames = {
			"Marc",
    		"Fran",
    		"Julia",
    		"Toni",
    		"Victor",
    		"Marta",
    		"Luis",
    		"Jose"};

	public LAOCharacter(Context context) {
	    this.context = context;
	    //daoGame = new DAOGame(context);
	}
	
	/**
	 * Get the list of characters, both player and non-player types // TODO coming from local database
	 * @return list of characters
	 */
	public List<List<RPGCharacter>> getCharacters() {
		// TODO return daoCharacter.getCharacters();

		List<RPGCharacter> pcList = new ArrayList<RPGCharacter>();
		List<RPGCharacter> npcList = new ArrayList<RPGCharacter>();

		Random random = new Random();
		for (int i=0;i<names.length;i++) {
			RPGCharacter character = new RPGCharacter();
			character.setId(random.nextInt(10000));
			character.setName(names[i]);
			if (character.getId()%2==0) {
				character.setPlayerName(playerNames[i]);
				pcList.add(character);
			} else {
				character.setPlayerName("Master");
				npcList.add(character);
			}
		}

		// Encapsulate the lists and return
		List<List<RPGCharacter>> allCharacters = new ArrayList<List<RPGCharacter>>();
		allCharacters.add(pcList);
		allCharacters.add(npcList);
		return allCharacters;
	}
}
