/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.whitedavidp.unistroke_keyboard.gesturebuilder;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.MotionEvent;
import android.view.View.OnClickListener;

import com.whitedavidp.unistroke_keyboard.gesture.GestureOverlayView;
import com.whitedavidp.unistroke_keyboard.gesture.Gesture;
import com.whitedavidp.unistroke_keyboard.gesture.GestureLibrary;

import android.widget.Button;
import android.widget.TextView;
import com.whitedavidp.unistroke_keyboard.R;

public class CreateGestureActivity extends Activity implements OnClickListener {
    public static final float LENGTH_THRESHOLD = 120.0f;

    private Gesture mGesture = null;
    private Button mDoneButton = null;
    private Button mCancelButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.create_gesture);

        mDoneButton = (Button) findViewById(R.id.done);
        mDoneButton.setOnClickListener(this);
        mCancelButton = (Button) findViewById(R.id.cancel);
        mCancelButton.setOnClickListener(this);
        
        GestureOverlayView overlay = (GestureOverlayView) findViewById(R.id.gestures_overlay);
        overlay.setFadeEnabled(false);
        overlay.addOnGestureListener(new GesturesProcessor());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        
        if (mGesture != null) {
            outState.putParcelable("gesture", mGesture);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        
        mGesture = savedInstanceState.getParcelable("gesture");
        if (mGesture != null) {
            final GestureOverlayView overlay =
                    (GestureOverlayView) findViewById(R.id.gestures_overlay);
            overlay.post(new Runnable() {
                public void run() {
                    overlay.setGesture(mGesture);
                }
            });

            mDoneButton.setEnabled(true);
        }
    }

    public void addGesture(View v) {
        if (mGesture != null) {
            final TextView input = (TextView) findViewById(R.id.gesture_name);
            final CharSequence name = input.getText();
            if (name.length() == 0) {
                input.setError(getString(R.string.error_missing_name));
                return;
            }

            final GestureLibrary store = GestureBuilderActivity.getStore();
            store.addGesture(name.toString(), mGesture);
            store.save();

            setResult(RESULT_OK);

/*            final String path = new File(Environment.getExternalStorageDirectory(),
                    "gestures").getAbsolutePath();
            Toast.makeText(this, getString(R.string.save_success, path), Toast.LENGTH_LONG).show(); */
        } else {
            setResult(RESULT_CANCELED);
        }

        finish();      
    }
    
    public void cancelGesture(View v) {
        setResult(RESULT_CANCELED);
        finish();
    }
    
    private class GesturesProcessor implements GestureOverlayView.OnGestureListener {
        public void onGestureStarted(GestureOverlayView overlay, MotionEvent event)
        {
          mDoneButton.setEnabled(false);
          mGesture = null;
        }

        public void onGesture(GestureOverlayView overlay, MotionEvent event) {
        }

        public void onGestureEnded(GestureOverlayView overlay, MotionEvent event)
        {
          mGesture = overlay.getGesture();
          if (mGesture.getLength() < LENGTH_THRESHOLD)
          {
          	mGesture = null;
            overlay.clear(false);
          }
          else 
          {
          	mDoneButton.setEnabled(true);
          }
        }

        public void onGestureCancelled(GestureOverlayView overlay, MotionEvent event) {
        }
    }

    @Override
    public void onClick(View v)
    {
      if(v == mCancelButton)
      {
        cancelGesture(v);
      }

      if(v == mDoneButton)
      {
        addGesture(v);
      }      
    }
}
