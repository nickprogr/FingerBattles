package activities;

import finger_battles.gen.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


public class MenuActivity extends Activity {
    
	public static final int RESULT_CLOSE_ALL = 0;
	public static final int RESULT_BACK = 1;
	public static final int DUMMY = 10;
	
	
	
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_screen);

    }
    
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode){
        	case RESULT_CLOSE_ALL:
        		setResult(RESULT_CLOSE_ALL);
        		finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu1, menu);
        return true;
    }

    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        	case R.id.exitOption:
        		setResult(RESULT_CLOSE_ALL);
        		finish();
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
    	}
    }
    
    
    public void callCreditsScreen(View v){
    	Intent intent = new Intent();
    	intent.setClass(this, CreditsActivity.class);
    	startActivityForResult(intent, DUMMY);
    }
    
    
    public void callTimeScreen(View v){
    	Intent intent = new Intent();
    	intent.setClass(this, TimeActivity.class);
    	startActivityForResult(intent, DUMMY);
    }
}