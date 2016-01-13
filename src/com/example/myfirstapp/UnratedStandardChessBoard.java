package com.example.myfirstapp;

import java.io.Serializable;

import com.example.myfirstapp.UnratedGamePage.GameTypeConstraints;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

public class UnratedStandardChessBoard extends ActionBarActivity 
									   implements NoticeDialogFragment.NoticeDialogListener{
	
	private chessCore core;
	private boardView board;
	private int callingActivity;
	
	private String user;

	public void createUsername(View v)
	{
		setContentView(board);
		board.requestFocus();
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_unrated_standard_chess_board);
		
	    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
	    // If your minSdkVersion is 11 or higher, instead use:
	    // getActionBar().setDisplayHomeAsUpEnabled(true);

	    // Check whether we're recreating a previously destroyed instance
	    if (savedInstanceState != null) {
			//setContentView(savedboard);
			//savedboard.requestFocus();
	    }
	    else{
	    	callingActivity = getIntent().getIntExtra("calling-activity", 0);
        	switch (callingActivity) {
        	case GameTypeConstraints.Standard:
        	
        		board = new boardView(this);
            	core = new chessCore();
            	core.setGameType(callingActivity);
            	board.setCore(core);
            	setContentView(board);
            	break;
            
        	case GameTypeConstraints.Fischer:
                    	
        		board = new boardView(this);
        		core = new chessCore();
        		core.setGameType(callingActivity);
        		board.setCore(core);
        		setContentView(board);    
            	break;
            
        	case GameTypeConstraints.Maharaja:
        	
        		board = new boardView(this);
            	core = new chessCore();
        		core.setGameType(callingActivity);
            	board.setCore(core);
            	setContentView(board);
            	break;
        	}
        }
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.unrated_standard_chess_board, menu);
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

/*
    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
  
    	super.onSaveInstanceState(savedInstanceState);
    	
    	// Save UI state changes to the savedInstanceState.
      // This bundle will be passed to onCreate if the process is
      // killed and restarted.
    	savedInstanceState.putBoolean("MyBoolean", true);
    	savedInstanceState.putDouble("myDouble", 1.9);
    	savedInstanceState.putInt("MyInt", 1);
    	savedInstanceState.putString("MyString", "Welcome back to Android");
    	// etc.
    	savedInstanceState.putSerializable("myBoard", (Serializable) this.board);

    			savedInstanceState.putParcelable("chessBoard", (Parcelable) this.board);
    	
//    	super.onSaveInstanceState(savedInstanceState);
    	//java.lang.ClassCastException: com.example.myfirstapp.boardView cannot be cast to android.os.Parcelable
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
      super.onRestoreInstanceState(savedInstanceState);
      // Restore UI state from the savedInstanceState.
      // This bundle has also been passed to onCreate.
      boolean myBoolean = savedInstanceState.getBoolean("MyBoolean");
      double myDouble = savedInstanceState.getDouble("myDouble");
      int myInt = savedInstanceState.getInt("MyInt");
      String myString = savedInstanceState.getString("MyString");
      boardView savedboard = savedInstanceState.getParcelable("chessBoard");
      
      //drawBoard(savedboard);
 //     setContentView(savedboard);
 //     savedboard.requestFocus();
      
    }
*/
	
/*    
    public Object onRetainNonConfigurationInstance() {
    	   return this;
    	}
    	//Then in YourActivity's onCreate()

    	public void onCreate(Bundle savedState)
    	{
    	   YourActivity prevActivity = (YourActivity)getLastNonConfigurationInstance();
    	   if(prevActivity!= null) { 
    	       // So the orientation did change
    	       // Restore some field for example
    	       this.myValue = prevActivity.myValue;
    	   }
    	}
  */  	

	@Override
	protected void onPause() 
	{
	  super.onPause();

	  // Store values between instances here
	  SharedPreferences preferences = getPreferences(MODE_PRIVATE);
	  SharedPreferences.Editor editor = preferences.edit();  // Put the values from the UI
	  
	  // Commit to storage
	  editor.commit();
	}
	
	@Override
	protected void onResume() 
	{
	  super.onResume();

	  // Pull data back between instances
		setContentView(board);
		board.requestFocus();

	}
	
	public void showNoticeDialog() {
		// Create an instance of the dialog fragment and show it
		DialogFragment dialog = new NoticeDialogFragment();
		dialog.show(getSupportFragmentManager(), "NoticeDialogFragment");
	}
    
	@Override
    public void onDialogPromotionClick(DialogFragment dialog) {
        // User touched the promotion piece
 
    }
	
}
    