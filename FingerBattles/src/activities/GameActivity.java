package activities;

import java.util.Random;

import finger_battles.gen.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Vibrator;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.TextView;


public class GameActivity extends Activity implements OnTouchListener{
    
	public static final int RESULT_CLOSE_ALL = 0;
	public static final int RESULT_BACK = 1;
	public static final int DUMMY = 10;
	
	public static int seconds, score, card_id, back_card_id;
	public static Random randGen, randGenBack;
	public static CountDownTimer cdt, three_sec;
	public static boolean multiTouch, lessThanThreeSec, duration, flipped, finish;
	public static boolean multiBackFlag = false;
	public static double initXMulti1, initXMulti2, initYMulti1, initYMulti2;
	public static Vibrator vib;
	public static long milliSecondsLeft;
	
	/* SHAPE VARS */
	
	/* common */
	public static double initX, initY, lastX, lastY; 			/* circle + square    		   */
	public static double changeX, changeY;			 			/* circle + square     		   */
	public static int  directX;						 			/* circle + horizontal 		   */
	public static int  directY;						 			/* circle + vertical + cross   */
    public static int drawingLine = 0; 				 			/* circle + vertical + cross   */
    public static boolean completeLine1, completeLine2 = false;	/* circle + vertical + cross   */
	
	/* circle */
	public static boolean minX, maxX, minY, maxY;
	public static int counterX, counterY, originalDirectX, originalDirectY;
	
	/* square */
	public static boolean upLeft, upRight, downLeft, downRight, flag1, flag2, flag3, changeDirection;
	public static int startSide, currentSide; /* 0: up , 1: right , 2: down , 3: left , 4: upleft , 5: upright , 6: downright , 7: downleft*/
	public static int clockWise; /* 0: not clockwise , 1: clockwise */
	
	/* horizontal lines */
	public static boolean completeLine3 = false;
	
	 


	
	/** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
    	
    	/* passed parameters */
    	Bundle bun = getIntent().getExtras();
    	duration = bun.getBoolean("duration");
    	
    	
    	score = 0;
    	multiTouch = false;
    	finish = false;
    	
    	if(!duration)
    		seconds = 60000;
    	else
    		seconds = 120000;
    	
    	milliSecondsLeft = seconds;
    	
    	/* initialize UI */
    	super.onCreate(savedInstanceState);
        setContentView(R.layout.game_screen);
        
        
        
        /* chronometer */
        final TextView clock = (TextView) findViewById(R.id.clock);
        
        cdt = new CountDownTimer(seconds, 50) {

            public void onTick(long millisUntilFinished) {
            	Long l = millisUntilFinished/1000+1;
            	String s = "";
            	
            	if(l >= 100)
            		s = l.toString();
            	else if(l >= 10 && l < 100){
            		if(duration)
            			s = "0"+l.toString();
            		else
            			s = l.toString();
            	}
            	else{
            		if(duration)
            			s = "00"+l.toString();
            		else
            			s = "0"+l.toString();
            	}
            	
            	clock.setText(s);
            	
            	milliSecondsLeft = millisUntilFinished;
            }

            public void onFinish() {
            	if(duration)
            		clock.setText("000");
            	else
            		clock.setText("00");
            	
            	card_id = -1;
            	
            	CountDownTimer wait_before_finish = new CountDownTimer(1400, 1000) {

                    public void onTick(long millisUntilFinished) {}

                    public void onFinish() {
                    	callEndScreen();
                    }
                }.start();
            }
         }.start();
         
         
         
         /* first card */
         randGen = new Random();
         newCard(-1);
         ImageView im = (ImageView) findViewById(R.id.card);
         im.setOnTouchListener(this);
         randGenBack = new Random();

         vib = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
 
    }
    
    
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu2, menu);
        return true;
    }

    
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
        	case R.id.exitOption:
        		setResult(RESULT_CLOSE_ALL);
        		cdt.cancel();
        		finish();
        		return true;
        	case R.id.restartOption:
        		Intent in = getIntent();
        		cdt.cancel();
        		finish();
        		startActivity(in);
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
    	}
    }

      
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
        	setResult(RESULT_BACK);
        	cdt.cancel();
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    
    
    public boolean onTouch(View v, MotionEvent event){
    	
    	if(v instanceof ImageView){
    		
    		ImageView iv = (ImageView) v;
    		
    		if(iv.getId() == R.id.card){
    			
    			/* circle actions*/
    			if(card_id == 0){
    				if(event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN){
    					multiTouch = true;
    					
    					initXMulti1 = event.getX(0);
    					initXMulti2 = event.getX(1);
    					initYMulti1 = event.getY(0);
    					initYMulti2 = event.getY(1);
    				}
    				else if(event.getActionMasked() == MotionEvent.ACTION_POINTER_UP){
    					if(event.getPointerCount() <= 2){
    						multiTouch = multiBackFlag = false;
    						initXMulti1 = initXMulti2 = initYMulti1 = initYMulti2 = -1;
    					}
    				}
    				else if(event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_MOVE){
    					if(multiTouch){
    						int height = iv.getHeight();
    						
    						if(Math.abs(event.getY(0) - initYMulti1) > height/6 || Math.abs(event.getY(1) - initYMulti2) > height/6){
    							multiBackFlag = true;
    							boolean b = checkBackMulti(iv, event);
    							
    							if(b){
    								if(!flipped)
    									newBackCard();
    								
    								multiTouch = multiBackFlag = false;
            						initXMulti1 = initXMulti2 = initYMulti1 = initYMulti2 = -1;
    							}
    						}
    						else if(Math.abs(event.getX(0) - initXMulti1) > height/4 && Math.abs(event.getX(1) - initXMulti2) > height/4 && !multiBackFlag){
    							calculateScore(-10);
    					    	newCard(-1);
    					    	multiTouch = false;
        						initXMulti1 = initXMulti2 = initYMulti1 = initYMulti2 = -1;
    						}
    						
    						initCircleVars();
    					}
    					else if(event.getX(0) >= 0 && event.getX(0) <= iv.getHeight() && event.getY(0) >= 0 && event.getY(0) <= iv.getHeight()){
    						boolean b = checkCircle(iv, event);
    						
    						if(!b){
    							if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
    								setFalse();
    								vib.vibrate(50);
    							}
    							else if(event.getActionMasked() == MotionEvent.ACTION_MOVE){
    								if(initX != -1){
    									setFalse();
    									vib.vibrate(50);
    								}
    							}
    							
    									
    							initCircleVars();
    						}
    						else{
    							if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
    								initCircleVars();
    								initX = event.getX(0);
    								initY = event.getY(0);
    								lastX = initX;
    								lastY = initY;
    								directX = directY = 0;
    							}
    							else{
    								if(directX == 0){
    									if(event.getX(0) > lastX)
    										directX = originalDirectX = 1;
    									else if(event.getX(0) < lastX)
    										directX = originalDirectX = -1;
    								}
    								else if(directX == 1){
    									if(event.getX(0) < lastX){
    										changeX = event.getX(0);
    										directX = -1;
    									}
    									else{
    										if(changeX != -1 && event.getX(0) - changeX > iv.getHeight()/30){
    											counterX++;
    											changeX=-1;
    										}
    									}
    								}
    								else{
    									if(event.getX(0) > lastX){
    										changeX = event.getX(0);
    										directX = 1;
    									}
    									else{
    										if(changeX != -1 && changeX - event.getX(0) > iv.getHeight()/30){
    											counterX++;
    											changeX=-1;
    										}
    									}
    								}
    								
    								
    								
    								
    								if(directY == 0){
    									if(event.getY(0) > lastY)
    										directY = originalDirectY = 1;
    									else if(event.getY(0) < lastY)
    										directY = originalDirectY = -1;
    								}
    								else if(directY == 1){
    									if(event.getY(0) < lastY){
    										changeY = event.getY(0);
    										directY = -1;
    									}
    									else{
    										if(changeY != -1 && event.getY(0) - changeY > iv.getHeight()/30){
    											counterY++;
    											changeY=-1;
    										}
    									}
    								}
    								else{
    									if(event.getY(0) > lastY){
    										changeY = event.getY(0);
    										directY = 1;
    									}
    									else{
    										if(changeY != -1 && changeY - event.getY(0) > iv.getHeight()/30){
    											counterY++;
    											changeY=-1;
    										}
    									}
    								}
    								
    								lastX = event.getX(0);
    								lastY = event.getY(0);
    								
    							}
    						
    							if(counterX == 2 && counterY == 2){
    								if((originalDirectX == 1 && lastX > initX && initX != -1) || (originalDirectX == -1 && lastX < initX && initX != -1)){
    									if((originalDirectY == 1 && lastY > initY && initY != -1) || (originalDirectY == -1 && lastY < initY  && initY != -1)){
    										
    										if(maxX && minX && maxY && minY){
    											
    											if(lessThanThreeSec){
    												calculateScore(10);
    												three_sec.cancel();
    											}
    											else
    												calculateScore(5);
    											
    											setTrue();
    											newCard(-1);
    											initCircleVars();
    										}
    									}
    								}
    							}
    							else if(counterX > 2 || counterY > 2){
    								initCircleVars();
    								setFalse();
        							vib.vibrate(50);
    							}
    						}
    						
    					}
    					else{
    						initCircleVars();
    						if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
    							setFalse();
    							vib.vibrate(50);
    						}
    					}
    				}
    				else if(event.getActionMasked() == MotionEvent.ACTION_UP){
    					initCircleVars();
    				}
    				
    			}//circle case
    			
    			
    			
    			
    			
    			/* cross actions */
    			else if(card_id == 1){
    				if(event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN){
    					multiTouch = true;
    					
    					initXMulti1 = event.getX(0);
    					initXMulti2 = event.getX(1);
    					initYMulti1 = event.getY(0);
    					initYMulti2 = event.getY(1);
    				}
    				else if(event.getActionMasked() == MotionEvent.ACTION_POINTER_UP){
    					if(event.getPointerCount() <= 2){
    						multiTouch = multiBackFlag = false;
    						initXMulti1 = initXMulti2 = initYMulti1 = initYMulti2 = -1;
    					}
    				}
    				else if(event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_MOVE){
    					if(multiTouch){
    						int height = iv.getHeight();
    						
    						if(Math.abs(event.getY(0) - initYMulti1) > height/6 || Math.abs(event.getY(1) - initYMulti2) > height/6){
    							multiBackFlag = true;
    							boolean b = checkBackMulti(iv, event);
    							
    							if(b){
    								if(!flipped)
    									newBackCard();
    								
    								multiTouch = multiBackFlag = false;
            						initXMulti1 = initXMulti2 = initYMulti1 = initYMulti2 = -1;
    							}
    						}
    						else if(Math.abs(event.getX(0) - initXMulti1) > height/4 && Math.abs(event.getX(1) - initXMulti2) > height/4 && !multiBackFlag){
    							calculateScore(-10);
    					    	newCard(-1);
    					    	multiTouch = false;
        						initXMulti1 = initXMulti2 = initYMulti1 = initYMulti2 = -1;
    						}
    						
    						
    					}
    					else if(event.getX(0) >= 0 && event.getX(0) <= iv.getHeight() && event.getY(0) >= 0 && event.getY(0) <= iv.getHeight()){
    						if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
    							//check if any line started
    							checkCross(-1, true, iv, event);
    						}
    						else if(event.getActionMasked() == MotionEvent.ACTION_MOVE){
    							if(drawingLine != 0){
    								checkCross(drawingLine, false, iv, event);
    								if(completeLine1 && completeLine2){
    									//done
    									completeLine1 = completeLine2 = false; 
    									drawingLine = 0;
    									
    									if(lessThanThreeSec){
											calculateScore(10);
											three_sec.cancel();
										}
    									else
    										calculateScore(5);
    									
    									setTrue();
    									newCard(-1);
    								}
    							}	
    						}
    					}
    				}
    				else if(event.getActionMasked() == MotionEvent.ACTION_UP){
    					drawingLine = 0;
    					directX = 0;
    				}
    			}
    			
    			

    			
    			
    			/* horizontal lines actions */
    			else if(card_id == 2){
    				if(event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN){
    					multiTouch = true;
    					
    					initXMulti1 = event.getX(0);
    					initXMulti2 = event.getX(1);
    					initYMulti1 = event.getY(0);
    					initYMulti2 = event.getY(1);
    				}
    				else if(event.getActionMasked() == MotionEvent.ACTION_POINTER_UP){
    					if(event.getPointerCount() <= 2){
    						multiTouch = multiBackFlag = false;
    						initXMulti1 = initXMulti2 = initYMulti1 = initYMulti2 = -1;
    					}
    				}
    				else if(event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_MOVE){
    					if(multiTouch){
    						int height = iv.getHeight();
    						
    						if(Math.abs(event.getY(0) - initYMulti1) > height/6 || Math.abs(event.getY(1) - initYMulti2) > height/6){
    							multiBackFlag = true;
    							boolean b = checkBackMulti(iv, event);
    							
    							if(b){
    								if(!flipped)
    									newBackCard();
    								
    								multiTouch = multiBackFlag = false;
            						initXMulti1 = initXMulti2 = initYMulti1 = initYMulti2 = -1;
    							}
    						}
    						else if(Math.abs(event.getX(0) - initXMulti1) > height/4 && Math.abs(event.getX(1) - initXMulti2) > height/4 && !multiBackFlag){
    							calculateScore(-10);
    					    	newCard(-1);
    					    	multiTouch = false;
        						initXMulti1 = initXMulti2 = initYMulti1 = initYMulti2 = -1;
    						}
    						
    						
    					}
    					else if(event.getX(0) >= 0 && event.getX(0) <= iv.getHeight() && event.getY(0) >= 0 && event.getY(0) <= iv.getHeight()){
    						if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
    							//check if any line started
    							checkHorizontalLine(-1, true, iv, event);
    						}
    						else if(event.getActionMasked() == MotionEvent.ACTION_MOVE){
    							if(drawingLine != 0){
    								checkHorizontalLine(drawingLine, false, iv, event);
    								if(completeLine1 && completeLine2 && completeLine3){
    									//done
    									completeLine1 = completeLine2 = completeLine3 = false;
    									drawingLine = 0;
    									
    									if(lessThanThreeSec){
											calculateScore(10);
											three_sec.cancel();
										}
    									else
    										calculateScore(5);
    									
    									setTrue();
    									newCard(-1);
    								}
    							}      
    						}
    					}
    				}
    				else if(event.getActionMasked() == MotionEvent.ACTION_UP){
                        drawingLine = 0;
                        directX = 0;
    				}
    			}//horizontal case
    			
    			
    			
    			
    			
    			
    			/* square actions */
    			else if(card_id == 3){
    				if(event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN){
    					multiTouch = true;
    					
    					initXMulti1 = event.getX(0);
    					initXMulti2 = event.getX(1);
    					initYMulti1 = event.getY(0);
    					initYMulti2 = event.getY(1);
    					
    					//System.err.println("X1: "+initXMulti1+"   X2: "+initXMulti2+"   Y1: "+initYMulti1+"   Y2: "+initYMulti2);
    				}
    				else if(event.getActionMasked() == MotionEvent.ACTION_POINTER_UP){
    					if(event.getPointerCount() <= 2){
    						multiTouch = multiBackFlag = false;
    						initXMulti1 = initXMulti2 = initYMulti1 = initYMulti2 = -1;
    					}
    					//System.err.println("X1: "+initXMulti1+"   X2: "+initXMulti2+"   Y1: "+initYMulti1+"   Y2: "+initYMulti2);
    				}
    				else if(event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_MOVE){
    					
    					if(multiTouch){
    						int height = iv.getHeight();
    						
    						if(Math.abs(event.getY(0) - initYMulti1) > height/6 || Math.abs(event.getY(1) - initYMulti2) > height/6){
    							multiBackFlag = true;
    							boolean b = checkBackMulti(iv, event);
    							
    							if(b){
    								if(!flipped)
    									newBackCard();
    								
    								multiTouch = multiBackFlag = false;
            						initXMulti1 = initXMulti2 = initYMulti1 = initYMulti2 = -1;
    							}
    						}
    						else if(Math.abs(event.getX(0) - initXMulti1) > height/4 && Math.abs(event.getX(1) - initXMulti2) > height/4 && !multiBackFlag){
    							calculateScore(-10);
    					    	newCard(-1);
    					    	multiTouch = false;
        						initXMulti1 = initXMulti2 = initYMulti1 = initYMulti2 = -1;
    						}
    						
    						initSquareVars();
    					}
    					else if(event.getX(0) >= 0 && event.getX(0) <= iv.getHeight() && event.getY(0) >= 0 && event.getY(0) <= iv.getHeight()){
    						boolean b = checkSquare(iv, event);//System.err.println("REG");
    						
    						if(!b || changeDirection){
    							if(changeDirection){
    								setFalse();
    								vib.vibrate(50);
    							}
    							
    							if(!b){
    								if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
    									setFalse();
    									vib.vibrate(50);
    								}
    								else if(event.getActionMasked() == MotionEvent.ACTION_MOVE){
    									if(initX != -1){
    										setFalse();
    										vib.vibrate(50);
    									}
    								}
    							}
    									
    							initSquareVars();
    						}
    						else{
    							if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
    								initSquareVars();
    								initX = event.getX(0);
    								initY = event.getY(0);
    								lastX = initX;
    								lastY = initY;
    								
    								int height = iv.getHeight();
    								double side = (height*13)/19;
    								int x_c = height/2; int y_c = height/2;
    								squareHelper(event, side, height, x_c, y_c);
    							}
    							else{
    								int height = iv.getHeight();
    								double side = (height*13)/19;
    								int x_c = height/2; int y_c = height/2;
    								squareHelper(event, side, height, x_c, y_c);
    								
    								lastX = event.getX(0);
    								lastY = event.getY(0);
    							}
    							
    							
    							if(upLeft && upRight && downLeft && downRight){
    								if(currentSide == startSide){
    									if(startSide >= 4 && startSide <= 7){
    										
    										if(lessThanThreeSec){
												calculateScore(10);
												three_sec.cancel();
											}
        									else
        										calculateScore(5);
											
    										setTrue();
											newCard(-1);
											initSquareVars();
    									}
    									else{
    										if(startSide == 0){
    											if(clockWise == 1){
    												if(event.getX(0) > initX){
    													
    													if(lessThanThreeSec){
    	    												calculateScore(10);
    	    												three_sec.cancel();
    	    											}
    			    									else
    			    										calculateScore(5);
    													
    													setTrue();
    													newCard(-1);
    													initSquareVars();
    												}
    											}
    											else{
    												if(event.getX(0) < initX){
    													
    													if(lessThanThreeSec){
    	    												calculateScore(10);
    	    												three_sec.cancel();
    	    											}
    			    									else
    			    										calculateScore(5);
    													
    													setTrue();
    													newCard(-1);
    													initSquareVars();
    												}
    											}
    										}
    										else if(startSide == 1){
    											if(clockWise == 1){
    												if(event.getY(0) > initY){
    													
    													if(lessThanThreeSec){
    	    												calculateScore(10);
    	    												three_sec.cancel();
    	    											}
    			    									else
    			    										calculateScore(5);
    													
    													setTrue();
    													newCard(-1);
    													initSquareVars();
    												}
    											}
    											else{
    												if(event.getY(0) < initY){
    													
    													if(lessThanThreeSec){
    	    												calculateScore(10);
    	    												three_sec.cancel();
    	    											}
    			    									else
    			    										calculateScore(5);
    													
    													setTrue();
    													newCard(-1);
    													initSquareVars();
    												}
    											}
    										}
    										else if(startSide == 2){
    											if(clockWise == 1){
    												if(event.getX(0) < initX){
    													
    													if(lessThanThreeSec){
    	    												calculateScore(10);
    	    												three_sec.cancel();
    	    											}
    			    									else
    			    										calculateScore(5);
    													
    													setTrue();
    													newCard(-1);
    													initSquareVars();
    												}
    											}
    											else{
    												if(event.getX(0) > initX){
    													
    													if(lessThanThreeSec){
    	    												calculateScore(10);
    	    												three_sec.cancel();
    	    											}
    			    									else
    			    										calculateScore(5);
    													
    													setTrue();
    													newCard(-1);
    													initSquareVars();
    												}
    											}
    										}
    										else if(startSide == 3){
    											if(clockWise == 1){
    												if(event.getY(0) < initY){
    													
    													if(lessThanThreeSec){
    	    												calculateScore(10);
    	    												three_sec.cancel();
    	    											}
    			    									else
    			    										calculateScore(5);
    													
    													setTrue();
    													newCard(-1);
    													initSquareVars();
    												}
    											}
    											else{
    												if(event.getY(0) > initY){
    													
    													if(lessThanThreeSec){
    	    												calculateScore(10);
    	    												three_sec.cancel();
    	    											}
    			    									else
    			    										calculateScore(5);
    													
    													setTrue();
    													newCard(-1);
    													initSquareVars();
    												}
    											}
    										}
    										
    									}
    								}
								}
    							
    							
    						}
    						
    						
    					}
    					else{
    						initSquareVars();
    						if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
    							setFalse();
    							vib.vibrate(50);
    						}
    					}
    				}
    				else if(event.getActionMasked() == MotionEvent.ACTION_UP){
    					initSquareVars();
    				}
    			}//square case
    			
    			
    			
    			
    			
    			
    			/* vertical lines actions */
    			else if(card_id == 4){
    				if(event.getActionMasked() == MotionEvent.ACTION_POINTER_DOWN){
    					multiTouch = true;
    					
    					initXMulti1 = event.getX(0);
    					initXMulti2 = event.getX(1);
    					initYMulti1 = event.getY(0);
    					initYMulti2 = event.getY(1);
    				}
    				else if(event.getActionMasked() == MotionEvent.ACTION_POINTER_UP){
    					if(event.getPointerCount() <= 2){
    						multiTouch = multiBackFlag = false;
    						initXMulti1 = initXMulti2 = initYMulti1 = initYMulti2 = -1;
    					}
    				}
    				else if(event.getActionMasked() == MotionEvent.ACTION_DOWN || event.getActionMasked() == MotionEvent.ACTION_MOVE){
    					if(multiTouch){
    						int height = iv.getHeight();
    						
    						if(Math.abs(event.getY(0) - initYMulti1) > height/6 || Math.abs(event.getY(1) - initYMulti2) > height/6){
    							multiBackFlag = true;
    							boolean b = checkBackMulti(iv, event);
    							
    							if(b){
    								if(!flipped)
    									newBackCard();
    								
    								multiTouch = multiBackFlag = false;
            						initXMulti1 = initXMulti2 = initYMulti1 = initYMulti2 = -1;
    							}
    						}
    						else if(Math.abs(event.getX(0) - initXMulti1) > height/4 && Math.abs(event.getX(1) - initXMulti2) > height/4 && !multiBackFlag){
    							calculateScore(-10);
    					    	newCard(-1);
    					    	multiTouch = false;
        						initXMulti1 = initXMulti2 = initYMulti1 = initYMulti2 = -1;
    						}
    						
    						
    					}
    					else if(event.getX(0) >= 0 && event.getX(0) <= iv.getHeight() && event.getY(0) >= 0 && event.getY(0) <= iv.getHeight()){
    						if(event.getActionMasked() == MotionEvent.ACTION_DOWN){
    							//check if any line started
    							checkVerticalLine(-1, true, iv, event);
    						}
    						else if(event.getActionMasked() == MotionEvent.ACTION_MOVE){
    							if(drawingLine != 0){
    								checkVerticalLine(drawingLine, false, iv, event);
    								if(completeLine1 && completeLine2){
    									//done
    									completeLine1 = completeLine2 = false;
    									drawingLine = 0;
    									
    									if(lessThanThreeSec){
											calculateScore(10);
											three_sec.cancel();
										}
    									else
    										calculateScore(5);
    									
    									setTrue();
    									newCard(-1);
    								}
    							}      
    						}
    					}
    				}
                    else if(event.getActionMasked() == MotionEvent.ACTION_UP){
                        drawingLine = 0;
                        directY = 0;
                    }
    			}//vertical case
    			
    			
    			
    			
    			
    			
    			
    			return true;
    		}
    			
    	}
    	
    	
    	return false;
    }
    
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(resultCode){
        	case RESULT_CLOSE_ALL:
        		setResult(RESULT_CLOSE_ALL);
        		cdt.cancel();
        		finish();
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    
    
    
    public void newCard(int previousCard){
    	
    	if(previousCard == -1){
    		card_id = randGen.nextInt(5);
    		flipped = false;
    	}
    	else{
    		card_id = previousCard;
    		flipped = true;
    	}
        
        ImageView im = (ImageView) findViewById(R.id.card);
        
        if(card_id == 0)
        	im.setImageResource(R.drawable.card_circle);
        else if(card_id == 1){
        	im.setImageResource(R.drawable.card_cross);
        }
        else if(card_id == 2)
        	im.setImageResource(R.drawable.card_horizontallines);
        else if(card_id == 3)
        	im.setImageResource(R.drawable.card_square);
        else
        	im.setImageResource(R.drawable.card_verticallines);
        
        
        lessThanThreeSec = true;
        
        /* 3 seconds timer. if the user completes the card in less than 3 seconds, he wins 10 points. */
        three_sec = new CountDownTimer(3400, 1000) {

            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
            	lessThanThreeSec = false;
            }
         }.start();
         
         
    }
    
    public void newBackCard(){
    	
    	flipped = true;
    	
    	cdt.cancel();
    	
    	back_card_id = randGenBack.nextInt(4);
    	final int flippedCardId = card_id;
    	card_id = -1;
    	
    	
    	
    	ImageView im = (ImageView) findViewById(R.id.card);
        
        if(back_card_id == 0)
        	im.setImageResource(R.drawable.card_backside);
        else if(back_card_id == 1){
        	im.setImageResource(R.drawable.card_killself);
        }
        else if(back_card_id == 2)
        	im.setImageResource(R.drawable.card_timeextend);
        else
        	im.setImageResource(R.drawable.card_timeshorten);
        
        
        /* change the background and the fonts for the interval */
        final ImageView topBg = (ImageView) findViewById(R.id.topBg);
        topBg.setImageResource(R.drawable.bg_interval);
        
        final ImageView surface = (ImageView) findViewById(R.id.surface);
        surface.setImageResource(R.drawable.bg_interval);
        
        final TextView clockLabel = (TextView) findViewById(R.id.clockLabel);
        clockLabel.setTextColor(getResources().getColor(R.color.intervalGrey));
        
        final TextView clock = (TextView) findViewById(R.id.clock);
        clock.setTextColor(getResources().getColor(R.color.intervalGrey));
        
        final TextView scoreLabel = (TextView) findViewById(R.id.scoreLabel);
        scoreLabel.setTextColor(getResources().getColor(R.color.intervalGrey));
        
        final TextView score = (TextView) findViewById(R.id.score);
        score.setTextColor(getResources().getColor(R.color.intervalGrey));
        
        
        CountDownTimer backTimer = new CountDownTimer(2400, 1000) {

            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
            	
            	if(back_card_id == 1)
            		calculateScore(-10);
            	else if(back_card_id == 2)
            		milliSecondsLeft += 20000;
            	else if(back_card_id == 3)
            		milliSecondsLeft -= 5000;
            	
            	
            	 /* chronometer */
                final TextView clock = (TextView) findViewById(R.id.clock);
                
                cdt = new CountDownTimer(milliSecondsLeft, 50) {

                    public void onTick(long millisUntilFinished) {
                    	Long l = millisUntilFinished/1000+1;
                    	String s = "";
                    	
                    	if(l >= 100)
                    		s = l.toString();
                    	else if(l >= 10 && l < 100){
                    		if(duration)
                    			s = "0"+l.toString();
                    		else
                    			s = l.toString();
                    	}
                    	else{
                    		if(duration)
                    			s = "00"+l.toString();
                    		else
                    			s = "0"+l.toString();
                    	}
                    	
                    	clock.setText(s);
                    	
                    	milliSecondsLeft = millisUntilFinished;
                    }

                    public void onFinish() {
                    	if(duration)
                    		clock.setText("000");
                    	else
                    		clock.setText("00");
                    	
                    	card_id = -1;
                    	
                    	CountDownTimer wait_before_finish = new CountDownTimer(1400, 1000) {

                            public void onTick(long millisUntilFinished) {}

                            public void onFinish() {
                            	callEndScreen();
                            }
                        }.start();
                    }
                 }.start();
            	
                
                 /* restore background and fonts */
                 topBg.setImageResource(R.drawable.menu_bg_wood);
                 surface.setImageResource(R.drawable.surface);
                 clockLabel.setTextColor(getResources().getColor(R.color.white));
                 clock.setTextColor(getResources().getColor(R.color.white));
                 scoreLabel.setTextColor(getResources().getColor(R.color.white));
                 score.setTextColor(getResources().getColor(R.color.white));
            	
                
            	newCard(flippedCardId);
            }
         }.start();
         
         
    }
    
    public void callEndScreen(){
    	
    	if(!finish){
    		Intent intent = new Intent();
    		Bundle bun = new Bundle();

    		bun.putInt("score", score);

    		intent.setClass(this, EndActivity.class);
    		intent.putExtras(bun);
    		startActivityForResult(intent, DUMMY);
    	}
    }
    
    public void calculateScore(int scr){
    	TextView scoreV = (TextView) findViewById(R.id.score);
		score += scr;
		String s = "";
		
		if(score < 0)
			s = "-";
	
		if(Math.abs(score) >= 100)
			s += ((Integer)Math.abs(score)).toString();
		else if(Math.abs(score) >= 10 && Math.abs(score) < 100)
			s += "0"+((Integer)Math.abs(score)).toString();
		else if(Math.abs(score) >= 0 && Math.abs(score) < 10)
			s += "00"+((Integer)Math.abs(score)).toString();
	
		scoreV.setText(s);
    }
    
    public void setTrue(){
    	final ImageView im = (ImageView) findViewById(R.id.trueFalse);
        im.setImageResource(R.drawable.tick1);
        im.setVisibility(ImageView.VISIBLE);
        
        CountDownTimer one_sec = new CountDownTimer(500, 100) {

            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
            	im.setVisibility(ImageView.INVISIBLE);
            }
         }.start();
    }
    
    public void setFalse(){
    	final ImageView im = (ImageView) findViewById(R.id.trueFalse);
        im.setImageResource(R.drawable.x1);
        im.setVisibility(ImageView.VISIBLE);
        
        CountDownTimer one_sec = new CountDownTimer(300, 100) {

            public void onTick(long millisUntilFinished) {}

            public void onFinish() {
            	im.setVisibility(ImageView.INVISIBLE);
            }
         }.start();
    }
    
    public void initCircleVars(){
    	initX = initY = lastX = lastY = changeX = changeY = -1;
    	counterX = counterY = directX = directY = originalDirectX = originalDirectY = 0;
    	minX = maxX = minY = maxY = false;
    }
    
    public void initSquareVars(){
    	upLeft = upRight = downLeft = downRight = flag1 = flag2 = flag3 = changeDirection = false;
    	initX = initY = lastX = lastY = startSide = currentSide = clockWise = -1;
    	changeX = changeY = -1;
    	//flag1 = true;
    }
    
    public boolean checkCircle(ImageView iv, MotionEvent event){
    	int height = iv.getHeight();
    	
    	double radius = (height*6.5)/19;
    	int x = (int) Math.floor(event.getX(0));
    	int y = (int) Math.floor(event.getY(0));
    	int x_c = height/2; int y_c = height/2;
    	
    	double dist = Math.sqrt(Math.pow((x-x_c),2) + Math.pow((y-y_c),2));
    	
    	double dif = Math.abs(dist - radius);
    	
    	if(dif < height*0.11){
    		if(event.getX(0) > x_c + radius - height*0.05 && event.getX(0) < x_c + radius + height*0.05)
    			maxX = true;
    		if(event.getX(0) > x_c - radius - height*0.05 && event.getX(0) < x_c - radius + height*0.05)
    			minX = true;
    		if(event.getY(0) > y_c + radius - height*0.05 && event.getY(0) < y_c + radius + height*0.05)
    			maxY = true;
    		if(event.getY(0) > y_c - radius - height*0.05 && event.getY(0) < y_c - radius + height*0.05)
    			minY = true;
    		
    		return true;
    	}
    	else
    		return false;
    	
    }
       
    public boolean checkSquare(ImageView iv, MotionEvent event){
    	int height = iv.getHeight();
    	
    	double side = (height*13)/19;
    	int x = (int) Math.floor(event.getX(0));
    	int y = (int) Math.floor(event.getY(0));
    	int x_c = height/2; int y_c = height/2;
    	
    	
    	if(x > x_c - side/2 - height*0.08 && x < x_c - side/2 + height*0.08){
    		if(y > y_c - side/2 - height*0.08 && y < y_c - side/2 + height*0.08){
    			upLeft = true;
    		}
    		if(y > y_c + side/2 - height*0.08 && y < y_c + side/2 + height*0.08){
    			downLeft = true;
    		}
    	}
    	
    	if(x > x_c + side/2 - height*0.08 && x < x_c + side/2 + height*0.08){
    		if(y > y_c - side/2 - height*0.08 && y < y_c - side/2 + height*0.08){
    			upRight = true;
    		}
    		if(y > y_c + side/2 - height*0.08 && y < y_c + side/2 + height*0.08){
    			downRight = true;
    		}
    	}
    	
    	
    	if((x > x_c - side/2 - height*0.08 && x < x_c - side/2 + height*0.08) || (x > x_c + side/2 - height*0.08 && x < x_c + side/2 + height*0.08)){
    		if(y > y_c - side/2 - height*0.08 && y < y_c + side/2 + height*0.08){
    			return true;
    		}
    	}
    	
    	if((y > y_c - side/2 - height*0.08 && y < y_c - side/2 + height*0.08) || (y > y_c + side/2 - height*0.08 && y < y_c + side/2 + height*0.08)){
    		if(x > x_c - side/2 - height*0.08 && x < x_c + side/2 + height*0.08){
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    private void squareHelper(MotionEvent event, double side, int height, int x_c, int y_c){
    	
    	if(event.getActionMasked() == MotionEvent.ACTION_DOWN && !flag1){
    		/* find side of first touch */
    		if(event.getX(0) > x_c - side/2 - height*0.08 && event.getX(0) < x_c + side/2 + height*0.08){
    			if(event.getY(0) > y_c - side/2 - height*0.08 && event.getY(0) < y_c - side/2 + height*0.08)
    				startSide = 0;
    			if(event.getY(0) > y_c + side/2 - height*0.08 && event.getY(0) < y_c + side/2 + height*0.08)
    				startSide = 2;
    		}
    		
    		if(event.getY(0) > y_c - side/2 - height*0.08 && event.getY(0) < y_c + side/2 + height*0.08){
    			if(event.getX(0) > x_c - side/2 - height*0.08 && event.getX(0) < x_c - side/2 + height*0.08)
    				startSide = 3;
    			if(event.getX(0) > x_c + side/2 - height*0.08 && event.getX(0) < x_c + side/2 + height*0.08)
    				startSide = 1;
    		}
    		
    		
    		
    		if(event.getX(0) > x_c - side/2 - height*0.08 && event.getX(0) < x_c - side/2 + height*0.08){
        		if(event.getY(0) > y_c - side/2 - height*0.08 && event.getY(0) < y_c - side/2 + height*0.08){
        			startSide = 4;
        		}
        		if(event.getY(0) > y_c + side/2 - height*0.08 && event.getY(0) < y_c + side/2 + height*0.08){
        			startSide = 7;
        		}
        	}
        	
        	if(event.getX(0) > x_c + side/2 - height*0.08 && event.getX(0) < x_c + side/2 + height*0.08){
        		if(event.getY(0) > y_c - side/2 - height*0.08 && event.getY(0) < y_c - side/2 + height*0.08){
        			startSide = 5;
        		}
        		if(event.getY(0) > y_c + side/2 - height*0.08 && event.getY(0) < y_c + side/2 + height*0.08){
        			startSide = 6;
        		}
        	}
    	}
    	else if(event.getActionMasked() == MotionEvent.ACTION_MOVE && !flag1 && !flag2){
    		/* find direction of movement */ 
    		if(startSide == 0){
    			if(event.getX(0) > initX){
    				if((event.getX(0) - initX) > height/30){
    					clockWise = 1;
    					flag2 = true;
    				}
    			}
    			else{
    				if((initX - event.getX(0)) > height/30){
    					clockWise = 0;
    					flag2 = true;
    				}
    			}
    		}
    		else if(startSide == 1){
    			if(event.getY(0) > initY){
    				if((event.getY(0) - initY) > height/30){
    					clockWise = 1;
    					flag2 = true;
    				}
    			}
    			else{
    				if((initY - event.getY(0)) > height/30){
    					clockWise = 0;
    					flag2 = true;
    				}
    			}
    		}
    		else if(startSide == 2){
    			if(event.getX(0) < initX){
    				if((initX - event.getX(0)) > height/30){
    					clockWise = 1;
    					flag2 = true;
    				}
    			}
    			else{
    				if((event.getX(0) - initX) > height/30){
    					clockWise = 0;
    					flag2 = true;
    				}
    			}
    		}
    		else if(startSide == 3){
    			if(event.getY(0) < initY){
    				if((initY - event.getY(0)) > height/30){
    					clockWise = 1;
    					flag2 = true;
    				}
    			}
    			else{
    				if((event.getY(0) - initY) > height/30){
    					clockWise = 0;
    					flag2 = true;
    				}
    			}
    		}
    		else if(startSide >= 4 && startSide <= 7){
    			flag1 = true;
    		}
    		
    		
    	}
    	
    	else if(flag1){
    		/* find direction of movement when first touch is a corner */
    		flag1 = false;
    		int secondSide = -1;
    		
    		if(event.getX(0) > x_c - side/2 - height*0.08 && event.getX(0) < x_c + side/2 + height*0.08){
    			if(event.getY(0) > y_c - side/2 - height*0.08 && event.getY(0) < y_c - side/2 + height*0.08)
    				secondSide = 0;
    			if(event.getY(0) > y_c + side/2 - height*0.08 && event.getY(0) < y_c + side/2 + height*0.08)
    				secondSide = 2;
    		}
    		
    		if(event.getY(0) > y_c - side/2 - height*0.08 && event.getY(0) < y_c + side/2 + height*0.08){
    			if(event.getX(0) > x_c - side/2 - height*0.08 && event.getX(0) < x_c - side/2 + height*0.08)
    				secondSide = 3;
    			if(event.getX(0) > x_c + side/2 - height*0.08 && event.getX(0) < x_c + side/2 + height*0.08)
    				secondSide = 1;
    		}
    		
    		
    		
    		if(event.getX(0) > x_c - side/2 - height*0.08 && event.getX(0) < x_c - side/2 + height*0.08){
        		if(event.getY(0) > y_c - side/2 - height*0.08 && event.getY(0) < y_c - side/2 + height*0.08){
        			secondSide = 4;
        		}
        		if(event.getY(0) > y_c + side/2 - height*0.08 && event.getY(0) < y_c + side/2 + height*0.08){
        			secondSide = 7;
        		}
        	}
        	
        	if(event.getX(0) > x_c + side/2 - height*0.08 && event.getX(0) < x_c + side/2 + height*0.08){
        		if(event.getY(0) > y_c - side/2 - height*0.08 && event.getY(0) < y_c - side/2 + height*0.08){
        			secondSide = 5;
        		}
        		if(event.getY(0) > y_c + side/2 - height*0.08 && event.getY(0) < y_c + side/2 + height*0.08){
        			secondSide = 6;
        		}
        	}
    		
    		
    		
    		if(startSide == 4){
    			if(secondSide == 0){
    				flag2 = true;
    				clockWise = 1;
    			}
    			else if(secondSide == 3){
    				flag2 = true;
    				clockWise = 0;
    			}
    			else
    				flag1 = true;
    		}
    		else if(startSide == 5){
    			if(secondSide == 1){
    				flag2 = true;
    				clockWise = 1;
    			}
    			else if(secondSide == 0){
    				flag2 = true;
    				clockWise = 0;
    			}
    			else
    				flag1 = true;
    		}
    		else if(startSide == 6){
    			if(secondSide == 2){
    				flag2 = true;
    				clockWise = 1;
    			}
    			else if(secondSide == 1){
    				flag2 = true;
    				clockWise = 0;
    			}
    			else
    				flag1 = true;
    		}
    		else if(startSide == 7){
    			if(secondSide == 3){
    				flag2 = true;
    				clockWise = 1;
    			}
    			else if(secondSide == 2){
    				flag2 = true;
    				clockWise = 0;
    			}
    			else
    				flag1 = true;
    		}
    		
    		
    	}
    	else if(flag2){
    		/* check for change of direction */
    		int secondSide = -1, tempClock = -1;
    		
    		if(event.getX(0) > x_c - side/2 - height*0.08 && event.getX(0) < x_c + side/2 + height*0.08){
    			if(event.getY(0) > y_c - side/2 - height*0.08 && event.getY(0) < y_c - side/2 + height*0.08)
    				secondSide = 0;
    			if(event.getY(0) > y_c + side/2 - height*0.08 && event.getY(0) < y_c + side/2 + height*0.08)
    				secondSide = 2;
    		}
    		
    		if(event.getY(0) > y_c - side/2 - height*0.08 && event.getY(0) < y_c + side/2 + height*0.08){
    			if(event.getX(0) > x_c - side/2 - height*0.08 && event.getX(0) < x_c - side/2 + height*0.08)
    				secondSide = 3;
    			if(event.getX(0) > x_c + side/2 - height*0.08 && event.getX(0) < x_c + side/2 + height*0.08)
    				secondSide = 1;
    		}
    		
    		
    		
    		if(event.getX(0) > x_c - side/2 - height*0.08 && event.getX(0) < x_c - side/2 + height*0.08){
        		if(event.getY(0) > y_c - side/2 - height*0.08 && event.getY(0) < y_c - side/2 + height*0.08){
        			secondSide = 4;
        		}
        		if(event.getY(0) > y_c + side/2 - height*0.08 && event.getY(0) < y_c + side/2 + height*0.08){
        			secondSide = 7;
        		}
        	}
        	
        	if(event.getX(0) > x_c + side/2 - height*0.08 && event.getX(0) < x_c + side/2 + height*0.08){
        		if(event.getY(0) > y_c - side/2 - height*0.08 && event.getY(0) < y_c - side/2 + height*0.08){
        			secondSide = 5;
        		}
        		if(event.getY(0) > y_c + side/2 - height*0.08 && event.getY(0) < y_c + side/2 + height*0.08){
        			secondSide = 6;
        		}
        	}
        	
        	currentSide = secondSide;
        	
        	if(secondSide == 0){
    			if(event.getX(0) > lastX)
    				tempClock = 1;
    			else
    				tempClock = 0;
    		}
    		else if(secondSide == 1){
    			if(event.getY(0) > lastY)
    				tempClock = 1;
    			else
    				tempClock = 0;
    		}
    		else if(secondSide == 2){
    			if(event.getX(0) < lastX)
    				tempClock = 1;
    			else
    				tempClock = 0;
    		}
    		else if(secondSide == 3){
    			if(event.getY(0) < lastY)
    				tempClock = 1;
    			else
    				tempClock = 0;
    		}
        	
        	
        	if((clockWise == 0 && tempClock == 1) || (clockWise == 1 && tempClock == 0)){
        		if(!flag3){
        			changeX = event.getX(0);
        			changeY = event.getY(0);
        			flag3 = true;
        		}
        		else if(flag3 && (Math.abs(event.getX(0) - changeX) > height/30 || Math.abs(event.getY(0) - changeY) > height/30)){
        			changeDirection = true;
        		}
        	}
        	else{
        		flag3 = false;
        		changeX = changeY = -1;
        	}
        	
    		
    	}
    	
    }
    
    public void checkHorizontalLine(int line, boolean start, ImageView iv, MotionEvent event){
        double currentX = event.getX(0);
        double currentY = event.getY(0);
        int height = iv.getHeight();
       
        if(start){
            //top left
            if(currentX > 0.08*height && currentX < 0.21*height && currentY > 0.08*height && currentY < 0.25*height){
                directX = 1;
                drawingLine = 1;
            }
            //middle left
            else if(currentX > 0.08*height && currentX < 0.21*height && currentY > 0.4*height && currentY < 0.58*height){
                directX = 1;
                drawingLine = 2;
            }
            //bottom left
            else if(currentX > 0.08*height && currentX < 0.21*height && currentY > 0.74*height && currentY < 0.91*height){
                directX = 1;
                drawingLine = 3;
            }
            //top right
            else if(currentX > 0.79*height && currentX < 0.92*height && currentY > 0.08*height && currentY < 0.25*height){
                directX = -1;
                drawingLine = 1;
            }
            //middle right
            else if(currentX > 0.79*height && currentX < 0.92*height && currentY > 0.4*height && currentY < 0.58*height){
                directX = -1;
                drawingLine = 2;
            }
            //bottom right
            else if(currentX > 0.79*height && currentX < 0.92*height && currentY > 0.74*height && currentY < 0.91*height){
                directX = -1;
                drawingLine = 3;
            }
            else{
                directX = 0;
                drawingLine = 0;
                setFalse();
				vib.vibrate(50);
            }
        }
        else{   //if line already started
            if(directX == 1){
                if(drawingLine == 1){
                    if(currentY > 0.08*height && currentY < 0.25*height){
                        if(currentX > 0.79*height && currentX < 0.92*height){
                            completeLine1 = true;
                            drawingLine = 0;
                        }
                    }
                    else{
                        drawingLine = 0;
                        setFalse();
        				vib.vibrate(50);
                    }
                }
                else if(drawingLine == 2){
                    if(currentY > 0.4*height && currentY < 0.58*height){
                        if(currentX > 0.79*height && currentX < 0.92*height){
                            completeLine2 = true;
                            drawingLine = 0;
                        }
                    }
                    else{
                        drawingLine = 0;
                        setFalse();
        				vib.vibrate(50);
                    }
                }
                else if(drawingLine == 3){
                    if(currentY > 0.74*height && currentY < 0.91*height){
                        if(currentX > 0.79*height && currentX < 0.92*height){
                        	completeLine3 = true;
                            drawingLine = 0;
                        }
                    }
                    else{
                        drawingLine = 0;
                        setFalse();
        				vib.vibrate(50);
                    }
                }
                       
            }
            else if(directX == -1){
                if(drawingLine == 1){
                    if(currentY > 0.08*height && currentY < 0.25*height){
                        if(currentX > 0.08*height && currentX < 0.21*height){
                            completeLine1 = true;
                            drawingLine = 0;
                        }
                    }
                    else{
                    	drawingLine = 0;
                    	setFalse();
        				vib.vibrate(50);
                    }
                }
                else if(drawingLine == 2){
                    if(currentY > 0.4*height && currentY < 0.58*height){
                        if(currentX > 0.08*height && currentX < 0.21*height){
                            completeLine2 = true;
                            drawingLine = 0;
                        }
                    }
                    else{
                        drawingLine = 0;
                        setFalse();
        				vib.vibrate(50);
                    }
                }
                else if(drawingLine == 3){
                    if(currentY > 0.74*height && currentY < 0.91*height){
                    	if(currentX > 0.08*height && currentX < 0.21*height){
                            completeLine3 = true;
                            drawingLine = 0;
                        }
                    }
                    else{
                        drawingLine = 0;
                        setFalse();
        				vib.vibrate(50);
                    }
                }
            }
        }
    }//checkHorizontalLine
    
    public void checkVerticalLine(int line, boolean start, ImageView iv, MotionEvent event){
        double currentX = event.getX(0);
        double currentY = event.getY(0);
        int height = iv.getHeight();
       
        if(start){
            //top left
            if(currentY > 0.08*height && currentY < 0.21*height && currentX > 0.17*height && currentX < 0.34*height){
                directY = 1;
                drawingLine = 1;
            }
            //top right
            else if(currentY > 0.08*height && currentY < 0.21*height && currentX > 0.65*height && currentX < 0.82*height){
                directY = 1;
                drawingLine = 2;
            }
            //bottom left
            else if(currentY > 0.79*height && currentY < 0.92*height && currentX > 0.17*height && currentX < 0.34*height){
                directY = -1;
                drawingLine = 1;
            }
            //bottom right
            else if(currentY > 0.79*height && currentY < 0.92*height && currentX > 0.65*height && currentX < 0.82*height){
                directY = -1;
                drawingLine = 2;
            }
            else{
                directY = 0;
                drawingLine = 0;
                setFalse();
				vib.vibrate(50);
            }
        }
        else{   //if line already started
            if(directY == 1){
                if(drawingLine == 1){
                    if(currentX > 0.17*height && currentX < 0.34*height){
                         if(currentY > 0.79*height && currentY < 0.92*height){
                        	 completeLine1 = true;
                             drawingLine = 0;
                         }
                    }
                    else{
                    	drawingLine = 0;
                    	setFalse();
        				vib.vibrate(50);
                    }
                }
                else if(drawingLine == 2){
                    if(currentX > 0.65*height && currentX < 0.82*height){
                        if(currentY > 0.79*height && currentY < 0.92*height){
                        	completeLine2 = true;
                            drawingLine = 0;
                        }
                    }
                    else{
                    	drawingLine = 0;
                    	setFalse();
        				vib.vibrate(50);
                    }
                }
            }
            else if(directY == -1){
                if(drawingLine == 1){
                    if(currentX > 0.17*height && currentX < 0.34*height){
                        if(currentY > 0.08*height && currentY < 0.21*height){
                            completeLine1 = true;
                            drawingLine = 0;
                        }
                    }
                    else{
                    	drawingLine = 0;
                    	setFalse();
        				vib.vibrate(50);
                    }
                }
                else if(drawingLine == 2){
                    if(currentX > 0.65*height && currentX < 0.82*height){
                        if(currentY > 0.08*height && currentY < 0.21*height){
                        	completeLine2 = true;
                            drawingLine = 0;
                        }
                    }
                    else{
                    	drawingLine = 0;
                    	setFalse();
        				vib.vibrate(50);
                    }
                }
            }
        }
    }//checkVerticalLine
    
    public void checkCross(int line, boolean start, ImageView iv, MotionEvent event){
    	double currentX = event.getX(0);
    	double currentY = event.getY(0);
    	int height = iv.getHeight();
    	
    	if(start){
    		//top left
    		if(currentY > 0.08*height && currentY < 0.21*height && currentX>(currentY - 0.1*height) && currentX<(currentY + 0.1*height)){
    			directY = 1;
    			drawingLine = 1;
    		}
    		//top right
    		else if(currentY > 0.08*height && currentY < 0.21*height && currentX>(height - currentY - 0.1*height) && currentX<(height - currentY + 0.1*height)){
    			directY = 1;
    			drawingLine = 2;
    		}
    		//bottom left
    		else if(currentY > 0.79*height && currentY < 0.92*height && currentX>(height - currentY - 0.1*height) && currentX<(height - currentY + 0.1*height)){
    			directY = -1;
    			drawingLine = 2;
    		}
    		//bottom right
    		else if(currentY > 0.79*height && currentY < 0.92*height &&currentX>(currentY - 0.1*height) && currentX<(currentY + 0.1*height)){
    			directY = -1;
    			drawingLine = 1;
    		}
    		else{
	    		directY = 0;
				drawingLine = 0;
				setFalse();
				vib.vibrate(50);
    		}
    	}
    	else{   //if line already started
    		if(directY == 1){
    			if(drawingLine == 1){
    				if(currentX>(currentY - 0.1*height) && currentX<(currentY + 0.1*height)){
    					if(currentY > 0.79*height && currentY < 0.92*height){
    						completeLine1 = true;
    						drawingLine = 0;
    					}
    				}
    				else{
    					drawingLine = 0;
    					setFalse();
    					vib.vibrate(50);
    				}
    			}
    			else if(drawingLine == 2){
    				if(currentX>(height - currentY - 0.1*height) && currentX<(height - currentY + 0.1*height)){
    					if(currentY > 0.79*height && currentY < 0.92*height){
    						completeLine2 = true;
    						drawingLine = 0;
    					}
    				}
    				else{
    					drawingLine = 0;
    					setFalse();
    					vib.vibrate(50);
    				}
    			}
    		}
    		else if(directY == -1){
    			if(drawingLine == 1){
    				if(currentX>(currentY - 0.1*height) && currentX<(currentY + 0.1*height)){
    					if(currentY > 0.08*height && currentY < 0.21*height){
    						completeLine1 = true;
    						drawingLine = 0;
    					}
    				}
    				else{
    					drawingLine = 0;
    					setFalse();
    					vib.vibrate(50);
    				}
    			}
    			else if(drawingLine == 2){
    				if(currentX>(height - currentY - 0.1*height) && currentX<(height - currentY + 0.1*height)){
    					if(currentY > 0.08*height && currentY < 0.21*height){
    						completeLine2 = true;
    						drawingLine = 0;
    					}
    				}
    				else{
    					drawingLine = 0;
    					setFalse();
    					vib.vibrate(50);
    				}
    			}
    		}
    	}
    }//checkCross
    
	public boolean checkBackMulti(ImageView iv, MotionEvent event){
    	
    	int height = iv.getHeight();
    	int x_c = height/2; int y_c = height/2;
    	
    	/*double newInitXMulti1 = initXMulti1 - x_c;
    	double newInitXMulti2 = initXMulti2 - x_c; 
    	double newInitYMulti1 = y_c - initYMulti1; 
    	double newInitYMulti2 = y_c - initYMulti2;
    	
    	double normInitVector1 = Math.sqrt(Math.pow(newInitXMulti1, 2) + Math.pow(newInitYMulti1, 2));
    	double normInitVector2 = Math.sqrt(Math.pow(newInitXMulti2, 2) + Math.pow(newInitYMulti2, 2));
    	
    	double newCurrentXMulti1 = event.getX(0) - x_c;
    	double newCurrentXMulti2 = event.getX(1) - x_c; 
    	double newCurrentYMulti1 = y_c - event.getY(0); 
    	double newCurrentYMulti2 = y_c - event.getY(1);
    	
    	double normCurrentVector1 = Math.sqrt(Math.pow(newCurrentXMulti1, 2) + Math.pow(newCurrentYMulti1, 2));
    	double normCurrentVector2 = Math.sqrt(Math.pow(newCurrentXMulti2, 2) + Math.pow(newCurrentYMulti2, 2));
    	
    	double innerProduct1 = newInitXMulti1*newCurrentXMulti1 + newInitYMulti1*newCurrentYMulti1;
    	double innerProduct2 = newInitXMulti2*newCurrentXMulti2 + newInitYMulti2*newCurrentYMulti2;
    	
    	double cos1 = innerProduct1 / (normInitVector1*normCurrentVector1);
    	double cos2 = innerProduct2 / (normInitVector2*normCurrentVector2);
    	
    	System.err.println(normInitVector1+" "+normCurrentVector1);
    	
    	if(Math.abs(normInitVector1 - normCurrentVector1) <= height/10 && Math.abs(normInitVector2 - normCurrentVector2) <= height/10){
    		if(cos1 <= 0.0 && cos2 <= 0.0){
    			return true;
    		}
    	}*/
    	
    	double newInitXMulti = -1, newInitYMulti = -1; 
    	int finger = -1;
    	
    	if(event.getX(0) > x_c - height*0.08 && event.getX(0) < x_c + height*0.08 && event.getY(0) > y_c - height*0.08 && event.getY(0) < y_c + height*0.08){
    		finger = 0;
    		newInitXMulti = initXMulti2 - x_c;
    		newInitYMulti = y_c - initYMulti2;
    	}
    	else if(event.getX(1) > x_c - height*0.08 && event.getX(1) < x_c + height*0.08 && event.getY(1) > y_c - height*0.08 && event.getY(1) < y_c + height*0.08){
    		finger = 1;
    		newInitXMulti = initXMulti1 - x_c; 
    	    newInitYMulti = y_c - initYMulti1;
    	}
    	else
    		return false;
    	
    	
    	double normInitVector = Math.sqrt(Math.pow(newInitXMulti, 2) + Math.pow(newInitYMulti, 2));
    	
    	double newCurrentXMulti = -1, newCurrentYMulti = -1;
    	
    	
    	if(finger == 0){
    		newCurrentXMulti = event.getX(1) - x_c;
    		newCurrentYMulti = y_c - event.getY(1);
    	}
    	else{
    		newCurrentXMulti = event.getX(0) - x_c;
    	    newCurrentYMulti = y_c - event.getY(0);
    	}
    	
    	
    	double normCurrentVector = Math.sqrt(Math.pow(newCurrentXMulti, 2) + Math.pow(newCurrentYMulti, 2));
    	
    	double innerProduct = newInitXMulti*newCurrentXMulti + newInitYMulti*newCurrentYMulti;
    	
    	double cos = innerProduct / (normInitVector*normCurrentVector);
    	
    	
    	if(Math.abs(normInitVector - normCurrentVector) <= height/10){
    		if(cos <= 0.0 ){
    			return true;
    		}
    	}
    	
    	
    	
    	return false;
    	
    	
    }
}