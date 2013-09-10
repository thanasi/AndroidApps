package com.thanasi.bwcam;

import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;

public class PicStoreHandler implements PictureCallback {

    @Override
    public void onPictureTaken(byte[] picture, Camera camera) {
            // The picture can be stored or do something else with the data
//            
//            //This is invoked when picture is taken and the data needs to be processed
//            System.out.println("Picture successfully taken: "+picture);
//            
//            String fileName = "shareme.jpg";
//            String mime = "image/jpeg";
//            
//            MainActivity.this.shareme.setFullName(fileName);
//            MainActivity.this.shareme.setMimeType(mime);
//            MainActivity.this.shareme.setPhoto(picture);
    	
    	
    }
}