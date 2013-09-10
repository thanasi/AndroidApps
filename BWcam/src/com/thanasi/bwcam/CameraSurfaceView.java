package com.thanasi.bwcam;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class CameraSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

	private SurfaceHolder holder;
	private Camera camera;
	
	public CameraSurfaceView(Context context) {
		super(context);
		
		//Initiate the Surface Holder properly
        this.holder = this.getHolder();
        this.holder.addCallback(this);

	} // end CameraSurfaceView() constructor

	@Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // Now that the size is known, set up the camera parameters and begin
            // the preview.
            Camera.Parameters parameters = camera.getParameters();
            parameters.setPreviewSize(width, height);
            camera.setDisplayOrientation(90);
            camera.setParameters(parameters);
            camera.startPreview();
    } // end surfaceChanged() method

	 @Override
     public void surfaceCreated(SurfaceHolder holder) {
             try
             {
                     //Open the Camera in preview mode
                     this.camera = Camera.open();
                     this.camera.setPreviewDisplay(this.holder);
             }
             catch(IOException ioe)
             {
                     ioe.printStackTrace(System.out);
             }
     } // end 

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
            // Surface will be destroyed when replaced with a new screen
            //Always make sure to release the Camera instance
            camera.stopPreview();
            camera.release();
            camera = null;
    } // end surfaceDestroyed() method
    
    public Camera getCamera() {
            return this.camera;
    }
	
} // end CameraSurfaceView class