package com.whitedavidp.unistroke_keyboard.gesturebuilder;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.content.Intent;

import com.whitedavidp.unistroke_keyboard.gesture.Gesture;
import com.whitedavidp.unistroke_keyboard.gesture.GestureStroke;
import com.whitedavidp.unistroke_keyboard.gesture.GestureLibraries;
import com.whitedavidp.unistroke_keyboard.gesture.GestureOverlayView;
import com.whitedavidp.unistroke_keyboard.gesture.GestureOverlayView.OnGesturePerformedListener;
import com.whitedavidp.unistroke_keyboard.gesture.GestureOverlayView.OnGestureListener;
import com.whitedavidp.unistroke_keyboard.gesture.Prediction;

import android.widget.Toast;
import android.view.MotionEvent;

import java.io.File;
import java.util.ArrayList;

import com.whitedavidp.unistroke_keyboard.App;
import com.whitedavidp.unistroke_keyboard.R;

public class TestActivity extends Activity implements OnGestureListener {
	private com.whitedavidp.unistroke_keyboard.gesture.GestureLibrary gestureLib = null;
  private String mStoreFileName = "gestures";
  private File mStoreFile = new File(Environment.getExternalStorageDirectory(), mStoreFileName);
	
  //PointCloudAndroidGestureLibraryAdapter pointCloudGestureLib = null;

  @Override
  protected void onStart()
  {
    super.onStart();
    Intent startingIntent = getIntent();
    String fileName = startingIntent.getStringExtra(App.GESTURE_BUILDER_FILE_NAME);
    App.log("TestGesture", "file from intent: " + fileName);
    if(null != fileName)
    {
      mStoreFileName = fileName;
      mStoreFile = new File(Environment.getExternalStorageDirectory(), mStoreFileName);
    }

    gestureLib = GestureLibraries.fromFile(mStoreFile);
    if(null != gestureLib) {
      App.preGestureLibraryLoad(gestureLib);

      if (!gestureLib.load()) {
          Toast.makeText(this, "Could not load /sdcard/" + mStoreFileName, Toast.LENGTH_SHORT).show();
          finish();
      }
    }      
  }
  
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        
        Intent startingIntent = getIntent();
        String fileName = startingIntent.getStringExtra(App.GESTURE_BUILDER_FILE_NAME);
        if(null != fileName)
        {
          mStoreFileName = fileName;
          mStoreFile = new File(Environment.getExternalStorageDirectory(), mStoreFileName);
        }
        
        
        //pointCloudGestureLib = new PointCloudAndroidGestureLibraryAdapter(gestureLib);
        GestureOverlayView gestureView = (GestureOverlayView)
        findViewById(R.id.testGestureOverlayView);
        gestureView.addOnGestureListener(this);
    }
    
    public void onGesture(GestureOverlayView overlay, MotionEvent event) {
    	App.log("TestGesture", "In onGesture");
    }

    public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
    	App.log("TestGesture", "In onGestureCancelled");
    }
    
    public void onGestureEnded(GestureOverlayView overlay, MotionEvent event) {
    	App.log("TestGesture", "In onGestureEnded");
    	Gesture g = overlay.getGesture();
    	if (g.getLength() < CreateGestureActivity.LENGTH_THRESHOLD) {
    	    App.showToast("got nothing");
          overlay.clear(false);
    	}
    	else {
    		processGesture(g);
    	}
    }
    
    public void onGestureStarted(GestureOverlayView overlay, MotionEvent event) {
    	App.log("TestGesture", "In onGestureStarted");
    }
    
    private void processGesture(Gesture gesture) {
    	App.log("TestGesture", "Num Strokes: " + gesture.getStrokes().size());
      // if the user manages to enter a multi-stroke, inform and ignore it
      
    	if(gesture.getStrokesCount() > 1)
      {
        String s = this.getString(R.string.multi_stroke_entered);
        App.showToast(s);
        return;
      }
    	
    	GestureStroke stroke = gesture.getStrokes().get(0);
    	int l = stroke.points.length / 2;
    	App.log("TestGesture", "Num Points: " + l);
    	
    	for(int i = 0; i < l; i++)
    	{
    		float x = stroke.points[(i * 2)];
    		float y = stroke.points[((i * 2) + 1)];
        App.log("TestGesture", "Point " + i + "- " + x + ":" + y);
    	}
    	
    	ArrayList<Prediction> predictions = gestureLib.recognize(gesture);
    	
    	App.log("TestGesture", "Num predictions: " + predictions.size());
    	for(int i = 0; i < predictions.size(); i++)
    	{
    	  App.log("TestGesture", "Prediction " + i + ": " + predictions.get(i).name + " / " + predictions.get(i).score);
    	}
    	
    	if (predictions.size() > 0)
    	{
          Prediction prediction = (Prediction) predictions.get(0);
          logPrediction(prediction);
          if (prediction.score > App.getMinimumRecognitionScore())
          {
            Toast.makeText(this, "Prediction: " + prediction.name + " / " + prediction.score, Toast.LENGTH_SHORT).show();
            
            ArrayList<Gesture> predicted = gestureLib.getGestures(prediction.name);
            Gesture g = predicted.get(0);
            App.log("TestGesture", "Predicted Num Strokes: " + g.getStrokes().size());
          	
            GestureStroke s = g.getStrokes().get(0);
          	int pl = s.points.length / 2;
          	App.log("TestGesture", "Predicted Num Points: " + pl);
          	for(int pi = 0; pi < pl; pi++) {
          		float px = s.points[(pi * 2)];
          		float py = s.points[((pi * 2) + 1)];
                  App.log("TestGesture", "Point " + pi + "- " + px + ":" + py);
          	}
          }
          else
          {
            App.showToast(getString(R.string.unrecognized));
          }
          
          for(int i = 1; i < predictions.size(); i++) {
          	prediction = (Prediction) predictions.get(i);
              logPrediction(prediction);
          }
        }
        else 
        {
          App.showToast(getString(R.string.unrecognized));
        	App.log("TestGesture", "No predictions found");
        } 
/*    	
    	GestureOverlayView gestureView = (GestureOverlayView)
    	
    	ArrayList<PointCloudMatchResult> results = null;
    	
    	if((gestureView.getGestureStrokeType() == GestureOverlayView.GESTURE_STROKE_TYPE_SINGLE) &&
    	    (pointCloudGestureLib.containsOnlyUnistrokes()))
    	{
    	  results = pointCloudGestureLib.recognize(gesture, true);
    	}
    	else
    	{
    	  results = pointCloudGestureLib.recognize(gesture, false);
    	}

  		Toast.makeText(this, results.get(0).getName() + ": " + results.get(0).getScore(), Toast.LENGTH_SHORT).show();
  		*/
    }
    
    private void logPrediction(Prediction prediction) {
    	App.log("TestGesture", "Prediction: " + prediction.name + " - score = " + prediction.score);
    }
}
