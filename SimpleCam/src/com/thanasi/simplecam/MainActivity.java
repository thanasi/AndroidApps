package com.thanasi.simplecam;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;

public class MainActivity extends Activity {

	public static final String DATE_FORMAT_NOW = "yyyyMMddHHmmss";
	public static final String TAG = "SimpleCam";

	private SurfaceView preview;
	private SurfaceHolder pHolder;
	private Camera cam;
	private Button butt;
	private boolean hasCamera = false;
	private boolean inPreview = false;
	private boolean camConfigured = false;
	private String lastTime;
	private File outputdir = Environment
			.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		PackageManager pm = this.getPackageManager();
		hasCamera = pm.hasSystemFeature(PackageManager.FEATURE_CAMERA);

		// if somehow the app installed and launched in a device with no camera,
		// exit
		if (!hasCamera) {
			Log.e(TAG, "Ain't got no camera foo!");
			return;
		}

	}

	@Override
	protected void onResume() {
		super.onResume();
		
		setContentView(R.layout.activity_main);
		butt = (Button) findViewById(R.id.capBut);
		butt.setBackgroundColor(Color.RED);
		preview = (SurfaceView) findViewById(R.id.previewWin);

		Log.i(TAG, "Continuing with SimpleCam.");

		// setup preview surface
		pHolder = preview.getHolder();
		pHolder.addCallback(surfaceCallback);
		Log.i(TAG, "Preview surface setup.");

		cam = Camera.open();
		startPreview();

		butt.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				if (cam != null) {
					Log.i(TAG, "Woah! Picture requested.");
					cam.takePicture(shutCallback, null, jpegCallback);
					cam.startPreview();
				}

				else {
					Log.e(TAG,
							"Picture requested but I couldn't grab the camera. Try again.");
					onResume();
				}

			}
		});
	} // end onResume() method

	@Override
	protected void onPause() {
		if (inPreview) {
			cam.stopPreview();
		}

		if (cam != null) {
			cam.release();
			pHolder.removeCallback(surfaceCallback);
			cam = null;
			camConfigured = false;
			inPreview = false;
		}

		Log.i(TAG, "Suspending SimpleCam.");

		super.onPause();
	} // end onPause() method

	// find best preview size for our given window
	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;

		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}

		return result;
	} // end getBestPreviewSize() method

	// initialize the preview window
	private void initPreview(int width, int height) {
		if (cam != null && pHolder.getSurface() != null) {
			try {
				cam.setPreviewDisplay(pHolder);
			} catch (Throwable t) {
				Log.e("SimpleCam-surfaceCallback",
						"Exception in setPreviewDisplay()", t);
				Toast.makeText(MainActivity.this, t.getMessage(),
						Toast.LENGTH_LONG).show();
			}

			if (!camConfigured) {
				Camera.Parameters parameters = cam.getParameters();
				Camera.Size size = getBestPreviewSize(width, height, parameters);

				if (size != null) {
					parameters.setPreviewSize(size.width, size.height);
					parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
					parameters.setRotation(0);
					cam.setParameters(parameters);
					camConfigured = true;
				}
			}
		}
	} // end initPreview() method

	private void startPreview() {
		if (camConfigured && cam != null) {
			cam.setDisplayOrientation(90);
			cam.startPreview();
			inPreview = true;
		}
	} // end StartPreview() method

	// define our class's SurfaceHolder callback function

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			// no-op -- wait until surfaceChanged()
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {

			initPreview(width, height);
			startPreview();

		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			Log.i(TAG, "Go figure. The surface was destroyed.");
		}
	}; // end SurfaceHolder Callback definition

	Camera.ShutterCallback shutCallback = new Camera.ShutterCallback() {

		@Override
		public void onShutter() {
			// when the picture is taken, set the date and change the
			// inPreview flag

			Calendar cal = Calendar.getInstance();
			SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
			lastTime = sdf.format(cal.getTime());
			inPreview = false;
		}
	};

	Camera.PictureCallback jpegCallback = new Camera.PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {

			File outfile = new File(outputdir, lastTime + ".jpg");
			FileOutputStream outStream = null;

			try {
				outStream = new FileOutputStream(outfile);
				outStream.write(data);
				outStream.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				Log.i(TAG, "Wrote jpg output to " + outfile);
				cam.startPreview();
			}
		}
	};

} // end MainActivity class