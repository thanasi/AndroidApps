package com.example.opencvcamera;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.WindowManager;

public class MainActivity extends Activity implements CvCameraViewListener {
	
	private CameraBridgeViewBase mOpenCvCameraView;
	
	private Mat mRgba;

    private BaseLoaderCallback  mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                {
                    Log.i("OpenCVCam", "OpenCV loaded successfully");
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
    	 Log.i("OpenCVCam", "Instantiated new " + this.getClass());
    }

    public void onCreate(Bundle savedInstanceState) {
        Log.i("OpenCVCam", "called onCreate");
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        setContentView(R.layout.activity_main);

        mOpenCvCameraView = (CameraBridgeViewBase) findViewById(R.id.cam_surface_view);
        mOpenCvCameraView.setCvCameraViewListener(this);
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
        mRgba = new Mat(height, width, CvType.CV_8UC4);
    }

    public void onCameraViewStopped() {
        mRgba.release();
    }
    
    
    public Mat onCameraFrame(Mat inputFrame) {
    	//Imgproc.cvtColor(inputFrame, mRgba, Imgproc.COLOR_RGBA2GRAY);
    	Imgproc.Canny(inputFrame, mRgba, 80, 100);
		return mRgba;
    }	
	

}