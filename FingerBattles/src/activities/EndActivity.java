package activities;

import finger_battles.gen.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;


public class EndActivity extends Activity {
	
	public static final int RESULT_CLOSE_ALL = 0;
	
	public static int score;
	
	
    
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	/* passed parameters */
    	Bundle bun = getIntent().getExtras();
    	score = bun.getInt("score");
    	
        super.onCreate(savedInstanceState);
        setContentView(R.layout.end_screen);
        
        TextView scoreEnd = (TextView) findViewById(R.id.scoreEnd);
    	scoreEnd.setText(((Integer)score).toString());
    	
    	CountDownTimer cdt = new CountDownTimer(1400, 1000) {

            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
            	Button playAg = (Button) findViewById(R.id.playAgainButton);
            	playAg.setVisibility(Button.VISIBLE);
            	
            	Button toMain = (Button) findViewById(R.id.toMainMenuButton);
            	toMain.setVisibility(TextView.VISIBLE);
            }
         }.start();

    }
    
    
    
    public void playAgain(View v){
    	Intent intent = new Intent();
    	intent.setClass(this, TimeActivity.class);
    	startActivity(intent);
    	setResult(RESULT_CLOSE_ALL);
		finish();
    }
    

    public void toMainMenu(View v){
    	Intent intent = new Intent();
    	intent.setClass(this, MenuActivity.class);
    	startActivity(intent);
    	setResult(RESULT_CLOSE_ALL);
		finish();
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
    
}