package com.example.valentine;

import java.io.InputStream;

import android.media.FaceDetector;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.hardware.Camera.Face;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener {

	private static final int REQUEST_ID = 1;
	private static final int HALF = 2;
	
	private ImageView  img1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		getActionBar().hide();
		Typeface tf = Typeface.createFromAsset(getAssets(),
                "fonts/VanessasValentine.otf");
		Button btn = (Button) findViewById(R.id.btn1);
		btn.setTypeface(tf);

		ImageView image = (ImageView) findViewById(R.id.picture1);
//		image.getBackground().setAlpha(128);
		image.setOnClickListener(this);
		ImageView image2 = (ImageView) findViewById(R.id.picture2);
		image2.setOnClickListener(this);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public void onClick(View v) {
		this.img1 = (ImageView) v;
		Intent intent = new Intent();
		intent.setAction(Intent.ACTION_GET_CONTENT);

		intent.addCategory(Intent.CATEGORY_OPENABLE);

		intent.setType("image/*");
         //tmp
		startActivityForResult(intent, REQUEST_ID);
 
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		InputStream stream = null;
		Bitmap original = null;
		if (requestCode == REQUEST_ID && resultCode == Activity.RESULT_OK) {
			try {
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
			    Log.d("face", c+"");
			    if(c>0){
			    	(this.img1).setImageBitmap(Bitmap.createScaledBitmap(original,
								original.getWidth(),
								original.getHeight(), true));
			    }else{
                    //TOODO -  dodac powiadomienie ¿e nie wykry³o twarzy
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

}
