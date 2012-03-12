package activities;

import finger_battles.gen.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;


public class TimeActivity extends Activity {
    
	public static final int RESULT_CLOSE_ALL = 0;
	public static final int RESULT_BACK = 1;
	public static final int DUMMY = 10;
	
	public static boolean duration;	/* false:  60 seconds			true:  120 seconds */
	public static boolean touched;
	
	
	
	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.time_screen);
        
        touched = false;

    }
    
    
    
    
    /* Called when the "Start !" button is touched. */
    public void launchGame(View v){
    	
    	if(touched){
    		Intent intent = new Intent();
    		Bundle bun = new Bundle();

    		//bun.putString("param_string", "the actual string"); // add two parameters: a string and a boolean
    		bun.putBoolean("duration", duration);

    		intent.setClass(this, GameActivity.class);
    		intent.putExtras(bun);
    		//startActivity(intent);
    		startActivityForResult(intent, DUMMY);
    	}
    	else{/*alert*/}
    	
    }
    
    
    
    
    /* Called when the "60" radio button is touched. */
    public void seconds60(View v){
    	touched = true;
    	duration = false;
    }
    
    /* Called when the "120" radio button is touched. */
    public void seconds120(View v){
    	touched = true;
    	duration = true;
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
    
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	setResult(RESULT_BACK);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    
}