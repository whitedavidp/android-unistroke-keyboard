package com.whitedavidp.unistroke_keyboard;

import com.whitedavidp.unistroke_keyboard.gesture.Gesture;
import com.whitedavidp.unistroke_keyboard.gesture.GestureOverlayView;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.inputmethodservice.InputMethodService;
import android.os.Build;
import android.os.SystemClock;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;
import android.view.inputmethod.*;

public class GestureInputMethod
extends InputMethodService
implements IKeyboardService
{
    private static final String SHORTCUT_GESTURE = "shortcut";
    private static ViewController mViewController = null;
    private final KeyboardViewModel mViewModel = new KeyboardViewModel(this);

    @Override
    public void onCreate()
    {
        super.onCreate();
        mViewController = new ViewController();
    }

    static ViewController getViewController()
    {
      return mViewController;
    }
    
    @Override
    public void onDestroy()
    {
        super.onDestroy();
    }

    @Override
    public void onBindInput() {
        super.onBindInput();
    }

    @Override
    public void onUnbindInput() {
        super.onUnbindInput();
    }

    @Override
    public View onCreateInputView()
    {
        View view = mViewController.onCreateInputView();
        return view;
    }

    @Override
    public void onStartInput(EditorInfo attribute, boolean restarting)
    {
        super.onStartInput(attribute, restarting);
        
        // try to clear any past recognition results when starting input again
        if(null != GestureInputMethod.mViewController)
        {
          GestureInputMethod.mViewController.displayResults("");
        }
    }

    @Override
    public void onFinishInput() {
        super.onFinishInput();
     }

    @Override
    public void onStartInputView(EditorInfo info, boolean restarting)
    {
        super.onStartInputView(info, restarting);
    }

    @Override
    public void onFinishInputView(boolean finishingInput)
    {
        super.onFinishInputView(finishingInput);
    }

    @Override
    public void onWindowHidden() {
        super.onWindowHidden();
    }

    @Override
    public void onWindowShown() {
        super.onWindowShown();
        
        // this is to make sure that changes made to the prefs xml file outside this app get incorporated.
        // in the long run, this should not be needed as all non-debugging prefs should have get/set methods
        App.refreshPrefs();
    }

    public void updateView()
    {
        mViewController.update();
    }

    private int getEditorAction()
    {
        return getCurrentInputEditorInfo().imeOptions & (EditorInfo.IME_MASK_ACTION | EditorInfo.IME_FLAG_NO_ENTER_ACTION);
    }

    public boolean isEditorActionRequested()
    {
        int action = getEditorAction();

        if ((action & EditorInfo.IME_FLAG_NO_ENTER_ACTION) != 0)
        {
            return false;
        }

        switch (action)
        {
            case EditorInfo.IME_ACTION_NONE:
            case EditorInfo.IME_ACTION_UNSPECIFIED:
                return false;

            default:
                return true;
        }
    }

    public void performEditorAction()
    {
        getCurrentInputConnection().performEditorAction(getEditorAction());
    }

    public void showInputMethodPicker()
    {
        InputMethodManager manager = (InputMethodManager)getSystemService(INPUT_METHOD_SERVICE);
        if (manager != null)
        {
            manager.showInputMethodPicker();
        }
    }

    public void sendText(String str)
    {
        getCurrentInputConnection().commitText(str, str.length());
        mViewController.update();
    }

    public void sendKey(int action, int keyCode, int metaState)
    {
        long time = SystemClock.uptimeMillis();
        KeyEvent event = new KeyEvent(time, time, action, keyCode, 0, metaState);
        getCurrentInputConnection().sendKeyEvent(event);
    }

    public void sendKeyRepeat(int keyCode, int metaState)
    {
        long time = SystemClock.uptimeMillis();
        KeyEvent event = new KeyEvent(time, time, KeyEvent.ACTION_DOWN, keyCode, 1, metaState);
        getCurrentInputConnection().sendKeyEvent(event);
    }

    private static class KeyEventUtils
    {
        public static int keyCodeFromTag(String tag)
        {
            return KeyEvent.keyCodeFromString("KEYCODE_" + tag.toUpperCase());
        }
    }

    class ViewController
    {
        private ViewGroup mCenterPanel;
        private Button mButtonShift;
        private Button mButtonCtrl;
        private Button mButtonAlt;
        private final InfoView mInfoView = new InfoView();
        private View mainView = null;

        public View onCreateInputView()
        {
            int layoutToUse = R.layout.input_method;
            
            if(Resources.getSystem().getDisplayMetrics().heightPixels <= 1920)
            {
              layoutToUse = R.layout.input_method_small;
            }
            
            mainView = getLayoutInflater().inflate(layoutToUse, null);
            showResults();

            setupMainView(mainView);
            setupKeyboardView(mainView);
            setupExtendKey(mainView);
            mInfoView.setup(mainView);

            update();

            return mainView;
        }
        
        public void displayResults(String msg)
        {
          if(null != mainView)
          {
            TextView txtResults = (TextView) mainView.findViewById(R.id.text_results);
            if((null != txtResults) && (txtResults.getVisibility() == View.VISIBLE))
            {
              txtResults.setText(msg);
            }
          }
        }
        
        public void showResults()
        {
          if(null != mainView)
          {
            TextView txtResults = (TextView) mainView.findViewById(R.id.text_results);
            txtResults.setVisibility((App.isShowResultsEnabled()) ? View.VISIBLE : View.GONE);
          }
        }

        private void setupMainView(View view)
        {
            mCenterPanel = (ViewGroup) view.findViewById(R.id.center_panel);
            mButtonShift = (Button) view.findViewById(R.id.button_shift);
            mButtonCtrl = (Button) view.findViewById(R.id.button_ctrl);
            mButtonAlt = (Button) view.findViewById(R.id.button_alt);

            setupGestureOverlays(view);
            setupButtonKey(view, R.id.button_shift);
            setupButtonKey(view, R.id.button_ctrl);
            setupButtonKey(view, R.id.button_alt);
            setupButtonKey(view, R.id.button_del);
            setupButtonKey(view, R.id.button_enter);
        }

        private void setupKeyboardView(final View view)
        {
            setupButtonKey(view, R.id.keyboard_button_h);
            setupButtonKey(view, R.id.keyboard_button_j);
            setupButtonKey(view, R.id.keyboard_button_k);
            setupButtonKey(view, R.id.keyboard_button_l);
            setupButtonKey(view, R.id.keyboard_button_z);
            setupButtonKey(view, R.id.keyboard_button_x);
            setupButtonKey(view, R.id.keyboard_button_c);
            setupButtonKey(view, R.id.keyboard_button_v);
            setupButtonKey(view, R.id.keyboard_button_home);
            setupButtonKey(view, R.id.keyboard_button_move_end);
            setupButtonKey(view, R.id.keyboard_button_dpad_left);
            setupButtonKey(view, R.id.keyboard_button_dpad_right);
            setupButtonKey(view, R.id.keyboard_button_dpad_up);
            setupButtonKey(view, R.id.keyboard_button_dpad_down);
            setupButtonKey(view, R.id.keyboard_button_forward_del);
        }
        
        private void setGestureOverlayBackground(GestureOverlayView overlay, int drawable)
        {
          Bitmap bitmap = BitmapFactory.decodeResource(overlay.getResources(), drawable);
          overlay.setBackground(new BitmapDrawable(overlay.getResources(), bitmap));
        }
        
        private void clearGestureOverlayBackground(GestureOverlayView overlay)
        {
          overlay.setBackground(null);
          overlay.setBackgroundColor(Color.parseColor("#222222"));
        }
        
        public void showBackground()
        {
          if(null != mainView)
          {
            GestureOverlayView overlay = (GestureOverlayView) mainView.findViewById(R.id.gestures_overlay);
            GestureOverlayView overlay_num = (GestureOverlayView) mainView.findViewById(R.id.gestures_overlay_num);
            if(App.isBitmapsEnabled())
            {
              if(!mViewModel.isSpecialOn())
              {
                setGestureOverlayBackground(overlay, R.drawable.alphabet);
              }
              else
              {
                setGestureOverlayBackground(overlay, R.drawable.special);
              }
              
              setGestureOverlayBackground(overlay_num, R.drawable.number);
            }
            else
            {
              clearGestureOverlayBackground(overlay);
              clearGestureOverlayBackground(overlay_num);
            }
          }
        }

        private void setupGestureOverlays(View view)
        {
            final com.whitedavidp.unistroke_keyboard.gesture.GestureOverlayView overlay = (com.whitedavidp.unistroke_keyboard.gesture.GestureOverlayView) view.findViewById(R.id.gestures_overlay);
            final com.whitedavidp.unistroke_keyboard.gesture.GestureOverlayView overlayNum = (com.whitedavidp.unistroke_keyboard.gesture.GestureOverlayView) view.findViewById(R.id.gestures_overlay_num);
            showBackground();
 
            overlay.addOnGestureListener(
                new OnGestureUnistrokeListener(GestureStore.FLAG_CATEGORY_ALPHABET)
                {
                    @Override
                    public void onGestureEnded(GestureOverlayView overlay, MotionEvent e)
                    {
                        mViewController.setAlphabetActive();
                        super.onGestureEnded(overlay, e);
                    }
                });

            overlayNum.addOnGestureListener(
                new OnGestureUnistrokeListener(GestureStore.FLAG_CATEGORY_NUMBER)
                {
                    @Override
                    public void onGestureEnded(GestureOverlayView overlay, MotionEvent e)
                    {
                        mViewController.setNumberActive();
                        super.onGestureEnded(overlay, e);
                    }
                });
        }

        private void setupExtendKey(final View view)
        {
            final View keyboardArea = view.findViewById(R.id.keyboard_area);
            final View gestureArea = view.findViewById(R.id.gesture_area);
            final Button extendKey = (Button) view.findViewById(R.id.button_key);

            keyboardArea.setVisibility(View.INVISIBLE);

            extendKey.setOnTouchListener(
                new OnTouchGestureListener(view.getContext())
                {
                  @Override
                  public boolean onDoubleTap (MotionEvent e)
                  {
                    App.showHelp();
                    return true; 
                  }
                  
                    @Override
                    public boolean onSingleTapConfirmed(MotionEvent e)
                    {
                        toggleKeyboadOn();
                        return true;
                    }

                    @Override
                    public void onLongPress(MotionEvent e)
                    {
                        showInputMethodPicker();
                    }

                    private void toggleKeyboadOn()
                    {
                        if (keyboardArea.getVisibility() == View.VISIBLE)
                        {
                            keyboardArea.setVisibility(View.INVISIBLE);
                            gestureArea.setVisibility(View.VISIBLE);
                        }
                        else
                        {
                            keyboardArea.setVisibility(View.VISIBLE);
                            gestureArea.setVisibility(View.INVISIBLE);
                        }
                    }
                });
        }

        private void setupButtonKey(View rootView, int id)
        {
            final Button button = (Button) rootView.findViewById(id);
            final String tag = (String) button.getTag();
            final int keyCode = KeyEventUtils.keyCodeFromTag(tag);
            if (keyCode != KeyEvent.KEYCODE_UNKNOWN)
            {
                button.setOnTouchListener(
                    new OnTouchKeyListener(GestureInputMethod.this, keyCode)
                    {
                        @Override
                        protected void onKeyDown(int keyCode)
                        {
                            mViewModel.keyDown(keyCode);
                        }

                        @Override
                        protected void onKeyUp(int keyCode)
                        {
                            mViewModel.keyUp(keyCode);
                        }

                        @Override
                        protected void onKeyRepeat(int keyCode)
                        {
                            mViewModel.keyRepeat(keyCode);
                        }

                        @SuppressLint("InlinedApi")
                        @Override
                        protected void onFlick(int keyCode, FlickDirection direction)
                        {
                            if (Build.VERSION.SDK_INT > 10 && keyCode == KeyEvent.KEYCODE_DEL && direction == FlickDirection.FLICK_LEFT)
                            {
                                mViewModel.key(KeyEvent.KEYCODE_FORWARD_DEL);
                            }
                            else if (keyCode == KeyEvent.KEYCODE_SHIFT_LEFT && direction == FlickDirection.FLICK_RIGHT)
                            {
                                mViewModel.setShiftOn(false);
                            }
                            else
                            {
                                mViewModel.key(keyCode);
                            }
                        }
                    });
            }
        }

        public void update()
        {
            if (mViewModel.isCapsLockOn())
            {
                mButtonShift.setBackgroundResource(R.drawable.button_locked);
            }
            else
            {
                mButtonShift.setBackgroundResource(mViewModel.isShiftOn() ? R.drawable.button_active : R.drawable.button);
            }

            mButtonCtrl.setBackgroundResource(mViewModel.isCtrlOn() ? R.drawable.button_active : R.drawable.button);
            mButtonAlt.setBackgroundResource(mViewModel.isAltOn() ? R.drawable.button_active : R.drawable.button);
            mInfoView.setText(mViewModel.isSpecialOn() ? "special" : "");           
            getViewController().showBackground();
        }

        public void setAlphabetActive()
        {
            mInfoView.setAlphabetActive();
        }

        public void setNumberActive()
        {
            mInfoView.setNumberActive();
        }

        public RectF getCenterRect()
        {
            return ViewUtils.getViewRect(mCenterPanel);
        }

        private class InfoView
        {
            private TextView mInfo;
            private TextView mInfoNum;
            private TextView mInfoCurrent;

            public void setup(View view)
            {
                mInfo = (TextView) view.findViewById(R.id.info);
                mInfoNum = (TextView) view.findViewById(R.id.info_num);
                mInfoCurrent = mInfo;
            }

            public void setText(String text)
            {
              // sometimes, I don't see the "special" indicator if it is on only 1 side. so I am putting it on both
              //mInfoView.mInfoCurrent.setText(text);
              mInfoNum.setText(text);
              mInfo.setText(text);
            }

            public void setAlphabetActive()
            {
                setActiveInfo(mInfo);
            }

            public void setNumberActive()
            {
                setActiveInfo(mInfoNum);
            }

            private void setActiveInfo(TextView info)
            {
                if (!mInfoView.mInfoCurrent.equals(info))
                {
                    info.setText(mInfoView.mInfoCurrent.getText());
                    mInfoView.mInfoCurrent.setText("");
                    mInfoView.mInfoCurrent = info;
                }
            }
        }

        private class OnGestureUnistrokeListener
        extends GestureOverlayViewOnGestureListener
        {
            private final int category;
            private long gestureStartTime = -1;

            public OnGestureUnistrokeListener(int category)
            {
                this.category = category;
            }
            
            @Override
            public void onGestureStarted (GestureOverlayView overlay, MotionEvent e)
            {
              gestureStartTime = e.getDownTime();
            }

            @Override
            public void onGestureEnded(GestureOverlayView overlay, MotionEvent e)
            {
                long gestureDuration = e.getEventTime() - gestureStartTime;
                gestureStartTime = -1;
                Gesture gesture = overlay.getGesture();
                PredictionResult prediction = App.getApplicationResources().gestures.recognize(gesture, makeFlags());
                
                // if a poor match vibrate and ignore the gesture
                if (prediction.score < App.getMinimumRecognitionScore())
                {
                  App.vibrate(true);
                  return;
                }
                
                // if the user entered the shortcut gesture, start the app (if any defined in the prefs)
                if(prediction.name.toLowerCase().equals(GestureInputMethod.SHORTCUT_GESTURE))
                {
                  App.startShortcutApp();
                  
                  // do the following to turn special mode off
                  mViewModel.sendText("");
                  return;
                }
                
                int keyCode = KeyEventUtils.keyCodeFromTag(prediction.name);
                
                if (keyCode == KeyEvent.KEYCODE_UNKNOWN)
                {
                  mViewModel.sendText(prediction.name);
                  return;
                }
                
                // if this is a single-tap but not long enough, do NOT go into special mode
                if (keyCode == KeyEvent.KEYCODE_PERIOD)
                {
                  if(!mViewModel.isSpecialOn())
                  {
                    // enforce long tap, if enabled, on gesture to enter special mode
                    long setting = App.getSpecialModeTapDelay();
                    App.log("Gesture end", "setting: " + setting + " delay: " + gestureDuration);
                    
                    if((setting != App.SPECIAL_MODE_TAP_DELAY_DEFAULT) && (gestureDuration < setting))
                    {
                      return;
                    }
                  }
                }

                // otherwise, send the key along
                mViewModel.key(keyCode);
            }

            private int makeFlags()
            {
                int flags;

                if (mViewModel.isSpecialOn())
                {
                    flags = GestureStore.FLAG_CATEGORY_SPECIAL;
                }
                else
                {
                    flags = this.category | GestureStore.FLAG_CATEGORY_CONTROL;
                }

                if (mViewModel.isCtrlOn() || mViewModel.isAltOn())
                {
                    flags |= GestureStore.FLAG_STRICT;
                }

                return flags;
            }
        }
    }
}

