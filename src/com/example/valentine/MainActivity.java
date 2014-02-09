package com.example.valentine;

import java.io.InputStream;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class MainActivity extends Activity implements OnClickListener {

	private static final int REQUEST_ID = 1;
	private static final int HALF = 2;
	
	private ImageView image, img1;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		 image = (ImageView) findViewById(R.id.picture1);

		 image.setOnClickListener(this);
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

		startActivityForResult(intent, REQUEST_ID);

	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		InputStream stream = null;
		if (requestCode == REQUEST_ID && resultCode == Activity.RESULT_OK) {
			try {
				stream = getContentResolver().openInputStream(data.getData());

				Bitmap original = BitmapFactory.decodeStream(stream);
				(this.img1)
						.setImageBitmap(Bitmap.createScaledBitmap(original,
								original.getWidth(),
								original.getHeight(), true));
			} catch (Exception e) {
				e.printStackTrace();

			}

			if (stream != null) {
				try {
					stream.close();

				} catch (Exception e) {
					e.printStackTrace();

				}

			}

		}

	}

}
