package org.thanasi.camgraph2;

import java.util.ArrayList;
import java.util.List;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewFrame;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener2;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;

public class ImageManipulationsActivity extends Activity implements CvCameraViewListener2 {
    private static final String  TAG                 = "ThanasiCameraGraph::Activity";

    private CameraBridgeViewBase mOpenCvCameraView;

    private Size                 mSizeRgba;
    
    private Mat                  mRgba;
    private List<Mat>			 mlRgb;
    private Mat					 mTemp;
    private Mat                  mGray;
    private Mat					 mProfile;
    private Scalar               mColorsRGB[];
    private Point                mP1;
    private Point                mP2;
    private float                mBuff[];
    private Mat                  mRgbaInnerWindow;
    private Mat                  mGrayInnerWindow;
    private Mat                  mZoomWindow;
    private Mat                  mZoomCorner;
    
    private int					 mDividerx;
    private Mat					 mR1, mR2;
    

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    mOpenCvCameraView.enableFpsMeter();
                } break;
                default:
                {
                    super.onManagerConnected(status);
                } break;
            }
        }
    };

    public ImageManipulationsActivity() {
        Log.i(TAG, "Instantiated new " + this.getClass());
    }

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.i(TAG, "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.image_manipulations_surface_view);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.image_manipulations_activity_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
        
        
        
    }

    @Override
    public void onPause()
    {
        super.onPause();
        if (mOpenCvCameraView != null)
            mOpenCvCameraView.disableView();
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
        mGray = new Mat();
        mRgba = new Mat();
        mTemp = new Mat();
        mlRgb = new ArrayList<Mat>(3);
        mProfile = new Mat();
        mBuff = new float[width];
        mColorsRGB = new Scalar [] {new Scalar(255,0,0),
				        			new Scalar(0,255,0),
				        			new Scalar(0,0,255)};
        
        mP1 = new Point();
        mP2 = new Point();
        
        mDividerx = width/2;
//        mR1 = new Mat();
//        mR2 = new Mat();
    }

    private void CreateAuxiliaryMats() {
        if (mRgba.empty())
            return;

        mSizeRgba = mRgba.size();
        
        int rows = (int) mSizeRgba.height;
        int cols = (int) mSizeRgba.width;
        

        if (mR1 == null)
        	mR1 = mRgba.submat(0,rows, 0, cols/2);
        
        if (mR2 == null)
        	mR2 = mRgba.submat(0,rows, cols/2, cols);
        
        
    }

    public void onCameraViewStopped() {
        // Explicitly deallocate Mats
        if (mZoomWindow != null)
            mZoomWindow.release();
        if (mZoomCorner != null)
            mZoomCorner.release();
        if (mGrayInnerWindow != null)
            mGrayInnerWindow.release();
        if (mRgbaInnerWindow != null)
            mRgbaInnerWindow.release();
        if (mRgba != null)
            mRgba.release();
        if (mTemp != null)
        	mTemp.release();
        if (mGray != null)
            mGray.release();
        if (mProfile != null)
            mProfile.release();
        
        if (mR1 != null)
            mR1.release();
        if (mR2 != null)
            mR2.release();
        
        
        mRgba = null;
        mGray = null;
        mProfile = null;
        mlRgb = null;
        mTemp = null;
        mRgbaInnerWindow = null;
        mGrayInnerWindow = null;
        mZoomCorner = null;
        mZoomWindow = null;
        
        mR1 = null;
        mR2 = null;
    }

    public Mat onCameraFrame(CvCameraViewFrame inputFrame) {
        mRgba = inputFrame.rgba();
        mGray = inputFrame.gray();
        

        if ((mSizeRgba == null) || (mRgba.cols() != mSizeRgba.width) || (mRgba.height() != mSizeRgba.height)
        		|| (mR1 == null) || (mR2 == null))
            CreateAuxiliaryMats();
        
        // handle first region
        Core.split(mR1, mlRgb);
        
        // cycle through the 3 color channels
        for (int c=0; c<3; c++) {
        	mTemp = mlRgb.get(c);
        	
        	// reduce the image to one row
	    	Core.reduce(mTemp, mProfile, 1, Core.REDUCE_AVG, CvType.CV_32F);
	
	    	// debug data types and shapes
	    	Log.i(TAG, "mProfile: " + mProfile.rows() + " " + mProfile.cols() + " " + mProfile.channels() + " " + mProfile.depth() + " " + mProfile.type());
	    	Log.i(TAG, "mBuff: " + mBuff.length + " float");
	    	
	    	mProfile.t().get(0, 0, mBuff);;
    	
    	
	        for(int h=0; h<mSizeRgba.height ; h++) {
	            mP1.y = mP2.y = h;
	            mP1.x = mSizeRgba.width/2;
	            mP2.x = mSizeRgba.width/2 - (int) mBuff[h]/3;
	            Core.line(mRgba, mP1, mP2, mColorsRGB[c], 1);
	        }
    	}
        
        // handle second region
        Core.split(mR2, mlRgb);
        
        // cycle through the 3 color channels
        for (int c=0; c<3; c++) {
        	mTemp = mlRgb.get(c);
        	
        	// reduce the image to one row
	    	Core.reduce(mTemp, mProfile, 1, Core.REDUCE_AVG, CvType.CV_32F);
	
	    	// debug data types and shapes
	    	Log.i(TAG, "mProfile: " + mProfile.rows() + " " + mProfile.cols() + " " + mProfile.channels() + " " + mProfile.depth() + " " + mProfile.type());
	    	Log.i(TAG, "mBuff: " + mBuff.length + " float");
	    	
	    	mProfile.t().get(0, 0, mBuff);
    	
    	
	        for(int h=0; h<mSizeRgba.height ; h++) {
	            mP1.y = mP2.y = h;
	            mP1.x = mSizeRgba.width;
	            mP2.x = mSizeRgba.width - (int) mBuff[h]/3;
	            Core.line(mRgba, mP1, mP2, mColorsRGB[c], 1);
	        }
    	}
        Core.line(mRgba, new Point(mDividerx,0), new Point (mDividerx,mSizeRgba.height), new Scalar(0,0,0), 3);
        return mRgba;
    }
}
