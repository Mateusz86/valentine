package com.example.valentine;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;


import android.media.FaceDetector;
import android.os.Bundle;
import android.os.Handler;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.hardware.Camera.Face;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener {

	private static final int REQUEST_ID = 1;
	private static final int HALF = 2;
	private boolean isLoadFirstImage=false;
	private boolean isLoadSecondImage=false;
	private View viewClickedImage;
	private boolean isFirstAnimationButton=true;
	private ImageView  img1;
	private Button analysisButton;
	private LinearLayout baseLayout;
	private LinearLayout secondLayout;
	private LinearLayout scoreLayout;
	private Handler handler = new Handler();
	private ImageView image;
	private ImageView image2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().hide();
		Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/VanessasValentine.otf");
		analysisButton = (Button) findViewById(R.id.btn1);
		analysisButton.setTypeface(tf);
		
		analysisButton.setOnClickListener(this);

		image = (ImageView) findViewById(R.id.picture1);
		image.setTag(0);
		image.setOnClickListener(this);
		
		image2 = (ImageView) findViewById(R.id.picture2);
		image2.setOnClickListener(this);
		image2.setTag(1);
		
		baseLayout= (LinearLayout) findViewById(R.id.baseLayout);
		secondLayout= (LinearLayout) findViewById(R.id.secondLayout);
		scoreLayout = (LinearLayout) findViewById(R.id.scoreLayout);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		if(v instanceof ImageView) {
		this.img1 = (ImageView) v;
		this.viewClickedImage=v;
		
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);

		intent.addCategory(Intent.CATEGORY_OPENABLE);

		intent.setType("image/*");
         //tmp
		startActivityForResult(intent, REQUEST_ID);
		}
		else {
			doAnimation(baseLayout,2);
		}
 
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		InputStream stream = null;
		Bitmap original = null;
		if (requestCode == REQUEST_ID && resultCode == Activity.RESULT_OK) {
			try {
				
				if((Integer)(viewClickedImage.getTag())==0) {
					isLoadFirstImage=true;
				}
				else {
					isLoadSecondImage=true;
				}
				
				stream = getContentResolver().openInputStream(data.getData());

				BitmapFactory.Options options=new BitmapFactory.Options();
				options.inSampleSize = 8;
				options.inPreferredConfig = Bitmap.Config.RGB_565;
				options.inScaled = false;
				options.inDither = false;
				
			    original=BitmapFactory.decodeStream(stream,null,options);   
			    
			    FaceDetector fd = new FaceDetector(original.getWidth(),original.getHeight(), 5);
			   
			    android.media.FaceDetector.Face[] faces = new android.media.FaceDetector.Face[5];
			    int c = fd.findFaces(original, faces);
			    Log.d("Face", c+" -- " );
			    if(c>0){
			    	(this.img1).setImageBitmap(Bitmap.createScaledBitmap(original,
								original.getWidth(),
								original.getHeight(), true));
			    }else{
			    	Toast.makeText(this, getResources().getString(R.string.no_deceted_face), Toast.LENGTH_LONG).show();
			    }
			    		    
			    Log.d("Face ++",  " isLoadFirstImage =" +isLoadFirstImage + "isLoadSecondImage = " + isLoadSecondImage + "FirstAnimationButton =" +isFirstAnimationButton);
				if(isLoadFirstImage==true&&isLoadSecondImage==true && isFirstAnimationButton==true) { 
					isFirstAnimationButton=false;
					doAnimation(analysisButton,1);
				}
			    
			} catch (Exception e) {
				e.printStackTrace();

			}

			if (stream != null) {
				try {
					stream.close();
					this.img1 = null;

				} catch (Exception e) {
					e.printStackTrace();

				}

			}

		}

	}

	private void doAnimation(View v,int i) {
		Animation animationAlpha;
		if(i==1) {
		animationAlpha = new AlphaAnimation(0.0f, 1.0f);
		}
		else {
		animationAlpha = new AlphaAnimation(1.0f, 0.0f);

		analysisButton.setEnabled(false);
		animationAlpha.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				baseLayout.setVisibility(View.GONE);
				secondLayout.setVisibility(View.VISIBLE);
				final ImageView heart = (ImageView) secondLayout.findViewById(R.id.hearts);
				heart.setBackgroundResource(R.drawable.animation_heart);
				final AnimationDrawable frameAnimation = (AnimationDrawable) heart.getBackground();
				frameAnimation.start();
				//disable imagesListner
				image.setOnClickListener(null);
				image2.setOnClickListener(null);
				
	           	TimerTask timerTask = new TimerTask() {

                    @Override
                    public void run() {
                    	handler.post(new Runnable() {
							
							@Override
							public void run() {
			            		frameAnimation.stop();	
			            		hideAnimationHeart(heart);

							}
						});

                    }
                };

                Timer timer = new Timer("TIMER");
                timer.schedule(timerTask, 4000); 
			}
		});
		}
		animationAlpha.setDuration(2000);
		animationAlpha.setStartOffset(50);
		v.setVisibility(View.VISIBLE);
		v.startAnimation(animationAlpha);		
	}

	private void hideAnimationHeart(ImageView heart) {
		Animation animationAlpha = new AlphaAnimation(1.0f, 0.0f);
		animationAlpha.setFillAfter(true);
		animationAlpha.setFillEnabled(true);
		animationAlpha.setDuration(2000);
		animationAlpha.setStartOffset(50);
		heart.startAnimation(animationAlpha);
		animationAlpha.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {

				secondLayout.setVisibility(View.GONE);
				scoreLayout.setVisibility(View.VISIBLE);
				
				ArrayList<Bar> points = new ArrayList<Bar>();
				Bar d2 = new Bar();
				d2.setColor(Color.parseColor("#FFBB33"));
				d2.setName("L O V E");
				int number = (int) (Math.random()*100);
				d2.setValue(number);
				points.add(d2);
		        BarGraph g = (BarGraph)scoreLayout.findViewById(R.id.bargraph);
		        assert g != null;
		        g.setUnit("%");
		        g.appendUnit(true);
				g.setBars(points);
				
				Button back = (Button) scoreLayout.findViewById(R.id.back);
				back.setTag(3);
				back.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						setUpAllView();
					}

				});

			}
		});
		
	}
	
	
	private void setUpAllView() {
		scoreLayout.setVisibility(View.GONE);
		secondLayout.setVisibility(View.GONE);
		baseLayout.setVisibility(View.VISIBLE);
		
		//setUpListnersAll
		analysisButton.setEnabled(true);
		analysisButton.setOnClickListener(this);
		image.setTag(0);
		image.setOnClickListener(this);	
		image2.setTag(1);
		image2.setOnClickListener(this);
		

	}
	
	@Override
	public void onBackPressed() {
		showDialogWindow();
	}

	private void showDialogWindow() {
		AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
		alertDialog.setMessage("Kliknij TAK ¿eby wyjœæ z aplikacji").setCancelable(false).setPositiveButton("TAK", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				MainActivity.this.finish();
			}
		}).setNegativeButton("NIE", new DialogInterface.OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});
		AlertDialog alert = alertDialog.create();
		alert.show();
	}

}
