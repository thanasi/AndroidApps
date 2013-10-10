package com.thanasi.ocv_lfcam0;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.WindowManager;

public class MainActivity extends Activity implements CvCameraViewListener {
    
	private int NPOINTS = 10;
	private String TAG = "LightFieldCam";
	private double Q = 0.1d; 
	
	private CameraBridgeViewBase mOpenCvCameraView;
	
	private Mat mLastIm, mInIm, mOutIm;
	private boolean firstFrame;
	private Scalar red = new Scalar(255,0,0);
	private MatOfPoint mFeats;
	private Point [] mFeatArr = new Point[NPOINTS];
	
	// to thread the image processing
    private static final long TIMEOUT = 1000L;
    private BlockingQueue<Mat> frames = new LinkedBlockingQueue<Mat>();
    private boolean running = true;
	
    Thread worker = new Thread() {
        @Override
        public void run() {
            while (running) {
                Mat inputFrame = frames.poll(TIMEOUT, TimeUnit.MILLISECONDS);
                if (inputFrame == null) {
                    // timeout. Also, with a try {} catch block poll can be interrupted via Thread.interrupt() so not to wait for the timeout.
                    continue;
                }
                Mat result = doImgProc(inputFrame);
            }
        }
    };
	

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };
    
    public MainActivity(){
    	 Log.i(TAG, "Instantiated new " + this.getClass());
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);
        
        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.cam_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
        worker.start();
    }
    
    public void onPause()
    {
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
        super.onPause();
    }

    @Override
    public void onResume()
    {
        super.onResume();
        OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_2_4_3, this, mLoaderCallback);
    }

    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
    }

    public void onCameraViewStarted(int width, int height) {
    	mLastIm = new Mat(height, width, CvType.CV_8UC4);
    	mInIm = new Mat(height, width, CvType.CV_8UC4);
        mOutIm = new Mat(height, width, CvType.CV_8UC4);
        firstFrame = true;
    }

    public void onCameraViewStopped() {
        mInIm.release();
    	mLastIm.release();
        mOutIm.release();
    }
    
    
    public Mat onCameraFrame(Mat inputFrame) {
    	
    	inputFrame.copyTo(mInIm);
        try {
			frames.put(inputFrame);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	Log.i(TAG,"Got frame >> " + mInIm);

		return mInIm;
    }	
    
    
//	inputFrame.copyTo(mOutIm);
//	Imgproc.cvtColor(inputFrame, mOutIm, Imgproc.COLOR_RGBA2GRAY);
//	Log.i(TAG,"copied as gray to mOutIm");
//	Imgproc.cornerHarris(mInIm, mOutIm, 5, 3, 1);
//	Imgproc.goodFeaturesToTrack(mOutIm, mFeats, NPOINTS, Q, 2d);
//	Log.i(TAG,"Got features");
//	mFeatArr = mFeats.toArray();

//	for (int i=0;i<NPOINTS;i++){ 
//		Core.circle(mOutIm, mFeatArr[i], 2, red);
//	}
		
//	Log.i(TAG,"Drew circles");
//	}
		    	
    
    public boolean doImgProc(Mat inputFrame, Mat outputFrame) {
    	
    	return true;
    }
    

}
