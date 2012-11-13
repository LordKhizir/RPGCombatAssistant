package com.altekis.rpg.combatassistant;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.altekis.rpg.combatassistant.character.CharacterArrayAdapter;
import com.altekis.rpg.combatassistant.character.LAOCharacter;
/**
 * Main application activity
 * Shows list of current PC+NPC, from where the user can decide the next step
 *
 */
public class CharacterListActivity extends Activity {
	ExpandableListView characterListView;
	CharacterArrayAdapter characterAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Remove title bar      
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_character_list);
        characterListView = (ExpandableListView) findViewById(R.id.characterListView);
 
        // FAKE - Get list of characters
        // FORCE reading of questions

        // Assign listener to list
        characterListView.setOnChildClickListener(characterClickListener);
    }
    
    @Override
    public void onResume() {
    	super.onResume();
    	// Fill games' lists
    	// Here, on onResume, to force reload each time we get back from another activity
        populatePlayerList();
    }

//    // Navigate to turn - to which exact activity, depends on the current state
//    private void gotoGameTurn(Game game) {
//    	@SuppressWarnings("rawtypes")
//		Class nextStep = new SmuacsApplication().getNextStep(game);
//    	if (nextStep!=null) {
//    		// Has the user 
//        	Intent nextStepIntent = new Intent(this, nextStep);
//        	nextStepIntent.putExtra("GameId", game.getId());
//            startActivityForResult(nextStepIntent, 0);
//    	}
//    }
	/**
	 * Listener for turn list
	 */
	ExpandableListView.OnChildClickListener characterClickListener = new ExpandableListView.OnChildClickListener() {
		@Override
		public boolean onChildClick(ExpandableListView parent, View v,
				int groupPosition, int childPosition, long id) {
			Toast.makeText(getApplicationContext(), "Selected", Toast.LENGTH_SHORT).show();
//			Game game = ((Game)gameAdapter.getChild(groupPosition,childPosition));
//			gotoGameTurn(game);
			return true;
		}
	};
	
	private void populatePlayerList() {
		// Feed lists of games to the adapter 
        characterAdapter = new CharacterArrayAdapter(this, new LAOCharacter(this).getCharacters());

	    // Assign adapter to populate list
        characterListView.setAdapter(characterAdapter);
        characterListView.expandGroup(0);
        characterListView.expandGroup(1);
	}

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.activity_main, menu);
//        return true;
//    }    
}
