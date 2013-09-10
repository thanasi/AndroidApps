package com.thanasi.bwcam;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
// import android.graphics.Color;
import android.content.Context;
import android.content.pm.PackageManager;
// import android.graphics.SurfaceTexture;
import android.hardware.Camera;
//import android.hardware.Camera.Parameters;
//import android.hardware.Camera.Size;
import android.os.Bundle;
import android.util.Log;
// import android.view.Gravity;
// import android.view.Menu;
//import android.view.SurfaceHolder;
//import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
//import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Switch;

import com.thanasi.bwcam.CameraSurfaceView;
import com.thanasi.bwcam.PicStoreHandler;


public class MakePhotoActivity extends Activity {

	private Button mButton;
	private Camera mCamera;
	private Switch mSwitch;
	private View mView;
	private FrameLayout mPreview;
		private CameraSurfaceView cameraSurfaceView;
    // private boolean isFlashOn, wantsFlashOn;
    
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_photo);
        
        PackageManager pm = this.getPackageManager();

		// check to see if there is a camera

        if (!(pm.hasSystemFeature(PackageManager.FEATURE_CAMERA))) {
        	
        	Log.e("bWphoto","Ain't got no camera foo!");
        	return;
        }
        
        mView = (View) findViewById(R.id.theback);
        mButton = (Button) findViewById(R.id.captureFront);
        mSwitch = (Switch) findViewById(R.id.switchFlash);
        
        Log.i("bWphoto","Warming up the preview");
        //Setup the FrameLayout with the Camera Preview Screen
        cameraSurfaceView = new CameraSurfaceView(this);
        mPreview = (FrameLayout) findViewById(R.id.previewFrame); 
        mPreview.addView(cameraSurfaceView);        
        
    } // end onCreate
    
    @Override
    protected void onResume() {
    	super.onResume();
        
		this.mButton.setOnClickListener(new OnClickListener() 
	    {
	        public void onClick(View v) 
	        {
	        	Camera camera = cameraSurfaceView.getCamera();
	            camera.takePicture(null, null, new PicStoreHandler());
	        }
	    });

    } // end onResume()
    
    @Override
    protected void onPause() {
    	super.onPause();
    	
    	// release the camera if we have control
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }

    } // end onPause()    

    @Override
    protected void onStop() {
    	super.onStop();
    	
    	// release the camera if we have control
    	if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
       	
    } // end onStop()

    
} // end MakePhotoActivity class


	
