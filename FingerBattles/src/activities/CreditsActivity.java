package activities;

import finger_battles.gen.R;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


public class CreditsActivity extends Activity {
    
	public static final int RESULT_CLOSE_ALL = 0;
	public static final int RESULT_BACK = 1;
	public static final int DUMMY = 10;
	
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.credits_screen);

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
    
    
    
    public void backToMenu(View v){
    	setResult(DUMMY);
    	finish();
    }
    
}