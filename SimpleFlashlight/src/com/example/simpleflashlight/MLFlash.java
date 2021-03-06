package com.example.simpleflashlight;

import android.os.Bundle;
import android.graphics.Color;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.util.Log;
// import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

public class MLFlash extends Activity {

	//***Define your Instance Variables***
	//I've left mine here but feel free to do it your own way

	private boolean isLightOn = false;
	private Camera camera;
	private Button button;
	private View v;

	private boolean hasFlash;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mlflash);

	   /* Retrieve the layout elements (buttonFlashlight and backgroundView) and cast them
		*  into objects we can use here.
		*/

		button = (Button) findViewById(R.id.flashButton);
		v = (View) findViewById(R.id.theBack);

		button.setBackgroundColor(Color.WHITE);
		v.setBackgroundColor(Color.BLACK);

		
		/*
		 * Retrieve the application's context (basically the state in which it's in) and ask it for
		 * its PackageManager. This PackageManager will then allow us to figure out if the 
		 * phone has a Camera.
		 */

		Context context = this;		
		PackageManager pm = context.getPackageManager();
		
		// check to see if there is a camera with flash onboard
		hasFlash = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA_FLASH);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		if(!hasFlash){ 
			// if there's no LED flash, log a warning to logcat
			Log.w("MLFlash", "Your device has no flash built in! This app will only illuminate screen.");   
			button.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View arg0) {
					if (isLightOn) {
						
						// 1. Prints to the LogCat the state you are about to change the light to ("Flashlight is on/off!")
						Log.i("MLFlash", "Flashlight was on >> turning OFF");
						
						// 2. Changes the background color of our backgroundView (which we'ved named 'v' above)
						v.setBackgroundColor(Color.BLACK);
						button.setBackgroundColor(Color.WHITE);
						button.setText("ON");
						
						// 3. Adjusts the boolean value accordingly (so we can toggle it again next time)
						isLightOn = false;	
					
					} else {
						
						// 1. Prints to the LogCat the state you are about to change the light to ("Flashlight is on/off!")
						Log.i("MLFlash", "Flashlight was off >> turning ON");
						
						// 2. Changes the background color of our backgroundView (which we'ved named 'v' above)
						v.setBackgroundColor(Color.WHITE);
						button.setBackgroundColor(Color.GRAY);
						button.setText("OFF");
						
						// 3. Adjusts the boolean value accordingly (so we can toggle it again next time)
						isLightOn = true;	
					}
				}

			});		
			
		} else { // if there is a camera
			
			//start the camera
			camera = Camera.open();
			final Parameters p = camera.getParameters();
	
	
			///set the clicklistener on our button
			button.setOnClickListener(new OnClickListener() {
	
				@Override
				public void onClick(View arg0) {
	
					if (isLightOn) {					
						// 1. Prints to the LogCat the state you are about to change the light to ("Flashlight is on/off!")
						Log.i("MLFlash", "Flashlight was on >> turning OFF");
						
						// 2. Turns on or off the flash (google "setFlashMode" on Parameters of the camera ('p')
						p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
						camera.setParameters(p);
						
						// 3. Starts or stops the preview (required for camera access) [e.g. camera.stopPreview(); is run when you want to switch it off]
						camera.stopPreview();
						
						// 4. Changes the background color of our backgroundView (which we'ved named 'v' above)
						v.setBackgroundColor(Color.BLACK);
						button.setBackgroundColor(Color.WHITE);
						button.setText("ON");
						
						// 5. Adjusts the boolean value accordingly (so we can toggle it again next time)
						isLightOn = false;

					} else {
	
						// 1. Prints to the LogCat the state you are about to change the light to ("Flashlight is on/off!")
						Log.i("MLFlash", "Flashlight was off >> turning ON");
						
						// 2. Turns on or off the flash (google "setFlashMode" on Parameters of the camera ('p')
						p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
						camera.setParameters(p);
						
						// 3. Starts or stops the preview (required for camera access) [e.g. camera.stopPreview(); is run when you want to switch it off]
						camera.startPreview();
						
						// 4. Changes the background color of our backgroundView (which we'ved named 'v' above)
						v.setBackgroundColor(Color.WHITE);
						button.setBackgroundColor(Color.GRAY);
						button.setText("OFF");
						
						// 5. Adjusts the boolean value accordingly (so we can toggle it again next time)
						isLightOn = true;
						
					}
				}
			});
		}

		
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
		if (camera != null) {
			camera.release();
		}
	}
		

	@Override
	protected void onStop() {
		super.onStop();

		/*
		 * Here we handle the case when the app is Stopped (via exit or by the OS)
		 * [no need for code here, just understand why we've put this here in the onStop Method of the application lifecycle]
		 */
		if (camera != null) {
			camera.release();
		}
	}

}
