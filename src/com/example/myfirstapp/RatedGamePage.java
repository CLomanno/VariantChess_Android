package com.example.myfirstapp;

import android.support.v7.app.ActionBarActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class RatedGamePage extends ActionBarActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_rated_game_page);
				
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    // If your minSdkVersion is 11 or higher, instead use:
	    // getActionBar().setDisplayHomeAsUpEnabled(true);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.rated_game_page, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}
	
	/** Called when the user clicks the Standard Chess button */
    public void launchStandardChess(View view) {
    	Intent intent = new Intent(this, RatedStandardChessBoard.class);
    	startActivity(intent);
    }
	
    /** Called when the user clicks the Fischer 960 button */
    public void launchFischer960(View view) {
    	Intent intent = new Intent(this, RatedStandardChessBoard.class);
    	startActivity(intent);
    }
    
    /** Called when the user clicks the Bong Cloud button */
    public void launchBongCloud(View view) {
    	Intent intent = new Intent(this, RatedStandardChessBoard.class);
    	startActivity(intent);
    }
    
    /** Called when the user clicks the Maharaja button */
    public void launchMaharaja(View view) {
    	Intent intent = new Intent(this, RatedStandardChessBoard.class);
    	startActivity(intent);
    }
    
    /** Called when the user clicks the Flip Chess button */
    public void launchFlipChess(View view) {
    	Intent intent = new Intent(this, RatedStandardChessBoard.class);
    	startActivity(intent);
    }
    
    /** Called when the user clicks the Shatranj button */
    public void launchShatranj(View view) {
    	Intent intent = new Intent(this, RatedStandardChessBoard.class);
    	startActivity(intent);
    }
    
    /** Called when the user clicks the Game Types button */
    public void gameTypesPage(View view) {
    	Intent intent = new Intent(this, GameTypesPage.class);
    	startActivity(intent);
    }
}
