package com.example.myfirstapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class MainActivity extends ActionBarActivity {
    

    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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
    
    /** Called when the user clicks the Unrated Game button */
    public void unratedGamePage(View view) {
    	Intent intent = new Intent(this, UnratedGamePage.class);
    	startActivity(intent);
    }
    
    /** Called when the user clicks the Rated Game button */
    public void ratedGamePage(View view) {
    	Intent intent = new Intent(this, RatedGamePage.class);
    	startActivity(intent);
    }
    
    /** Called when the user clicks the Account button */
    public void accountPage(View view) {
    	Intent intent = new Intent(this, AccountPage.class);
    	startActivity(intent);
    }
    
    /** Called when the user clicks the Friends button */
    public void friendsPage(View view) {
    	Intent intent = new Intent(this, FriendsPage.class);
    	startActivity(intent);
    }
    
    /** Called when the user clicks the About button */
    public void aboutPage(View view) {
    	Intent intent = new Intent(this, AboutPage.class);
    	startActivity(intent);
    }
    
    /** Called when the user clicks the Help button */
    public void helpPage(View view) {
    	Intent intent = new Intent(this, HelpPage.class);
    	startActivity(intent);
    }
    
    /** Called when the user clicks the Game Types button */
    public void gameTypesPage(View view) {
    	Intent intent = new Intent(this, GameTypesPage.class);
    	startActivity(intent);
    }
}

