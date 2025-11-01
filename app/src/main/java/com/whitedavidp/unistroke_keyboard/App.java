package com.whitedavidp.unistroke_keyboard;

import com.whitedavidp.unistroke_keyboard.gesture.GestureLibrary;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.widget.Toast;

// this class is implemented only because in build version >= 24, sending a log file via intent causes a crash
// due to "security" changes
public class App extends Application
{
  public static final int KEYREPEAT_DELAY_FIRST_MS = 400;
  public static final int KEYREPEAT_DELAY_MS = 100;
  public static final int VIBRATION_MS = 15;
  public static final int VIBRATION_STRONG_MS = 30;
  public static final long SPECIAL_MODE_TAP_DELAY_DEFAULT = 0;
  public static final String[] SPECIAL_MODE_TAP_DURATIONS = { "0", "100", "125", "150", "175", "200", "225", "250", "275" };
  private static final String SPECIAL_MODE_TAP_DELAY_PREF = "SPECIAL_MODE_TAP_DELAY";
  private static final String ENABLE_FILE_LOAD_PREF = "LOAD_FROM_FILES";
  private static final String ENABLE_LOGGING_PREF = "PERFORM_LOGGING";
  private static final String ENABLE_SHOW_RESULTS_PREF = "SHOW_RESULTS";
  private static final String ENABLE_SHOW_BITMAPS_PREF = "SHOW_BITMAPS";
  private static final String ENABLE_VIBRATE_ON_SPECIAL_PREF = "VIBRATE_ON_SPECIAL";
  private static final String ENABLE_LONG_VIBRATE_ON_ERROR_PREF = "LONG_VIBRATE_ON_ERROR";
  private static final String GESTURE_SAMPLING_SIZE_PREF = "GESTURE_SAMPLING_SIZE";
  private static final String CHECK_CLOSE_ENOUGH_PREF = "CHECK_CLOSE_ENOUGH";
  private static final String CLOSE_ENOUGH_PREF = "CLOSE_ENOUGH";
  private static final String ORIENTATION_SENSITIVITY_PREF = "ORIENTATION_SENSITIVITY";
  private static final String MINIMUM_RECOGNITION_SCORE_PREF = "MINIMUM_RECOGNITION_SCORE";
  private static final String SHORTCUT_APP_PREF = "SHORTCUT_APP";
  private static final String SHOW_ON_TEST_REMINDER_PREF = "SHOW_ON_TEST_REMINDER";
  private static final String SHOW_ON_GESTURE_BUILDER_REMINDER_PREF = "SHOW_ON_GESTURE_BUILDER_REMINDER";
  public static final String GESTURE_BUILDER_FILE_NAME = "GESTURE_FILE_NAME";
  public static final String[] GESTURE_SETS = { "alphabet", "number", "special", "control" };

  protected static String TAG = "_App";
  private static Context context = null;
  private static ApplicationResources resources = null;
  private static SharedPreferences preferences = null;

  @Override
  public void onTerminate()
  {
    super.onTerminate();
  }

  @Override
  public void onCreate()
  {
    super.onCreate();
    context = getApplicationContext();
    resources = new ApplicationResources(getApplicationContext());
  }

  public static int mapGestureSetNameToResourceId(String gestureSet)
  {
    int rawResourceId = -1;

    // get the appropriate raw resource to copy to a file in storage based on the gesture set selected
    if(gestureSet.toLowerCase().equals("alphabet"))
    {
      rawResourceId = R.raw.gestures_alphabet;
    }

    if(gestureSet.toLowerCase().equals("number"))
    {
      rawResourceId = R.raw.gestures_number;
    }

    if(gestureSet.toLowerCase().equals("special"))
    {
      rawResourceId = R.raw.gestures_special;
    }

    if(gestureSet.toLowerCase().equals("control"))
    {
      rawResourceId = R.raw.gestures_control;
    }

    return rawResourceId;
  }

  public static String mapGestureResourceIdToFileName(int gestureResourceId)
  {
    String fileName = null;

    switch(gestureResourceId)
    {
      case R.raw.gestures_alphabet:
      {
        fileName = "gestures_alphabet";
        break;
      }
      case R.raw.gestures_number:
      {
        fileName = "gestures_number";
        break;
      }
      case R.raw.gestures_special:
      {
        fileName = "gestures_special";
        break;
      }
      case R.raw.gestures_control:
      {
        fileName = "gestures_control";
        break;
      }
      default:
      {
        fileName = null;
      }
    }

    return fileName;
  }

  // ensure that all gesture libraries as configured as follows prior to loading
  public static void preGestureLibraryLoad(GestureLibrary library)
  {
    // note: I fooled around with the settings shown here: https://stackoverflow.com/questions/7743462/does-having-variations-of-gestures-in-gesture-library-improve-recognition
    // with only worse results. so I leave this as-is
    library.setOrientationStyle(App.getOrientationSensitivity()); // there is no documented constant with this value but it works way better than com.whitedavidp.unistroke_keyboard.gesture.GestureStore.ORIENTATION_SENSITIVE and I cannot figure out why

    // not really needed actually, SEQUENCE_SENSITIVE is what is set by default but best to be specific
    library.setSequenceType(com.whitedavidp.unistroke_keyboard.gesture.GestureStore.SEQUENCE_SENSITIVE); // the libraries should be SEQUENCE_SENSITIVE by default but set it anyhow to be explicit
  }

  static SharedPreferences getPrefs()
  {
    if(null == preferences)
    {
      refreshPrefs();
    }

    return preferences;
  }

  static void refreshPrefs()
  {
    // DO NOT use the App.log() here!!!
    android.util.Log.v("App", "refreshing prefs");
    preferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());
  }

  static boolean isLoggingEnabled()
  {
   return getPrefs().getBoolean(ENABLE_LOGGING_PREF, false);
  }

  static void setLoggingEnabled(boolean isEnabled)
  {
    Editor e = getPrefs().edit();
    e.putBoolean(ENABLE_LOGGING_PREF, isEnabled);
    e.commit();
  }

  static boolean isLongVibrateOnErrorEnabled()
  {
   return getPrefs().getBoolean(ENABLE_LONG_VIBRATE_ON_ERROR_PREF, false);
  }

  static void setLongVibrateOnErrorEnabled(boolean isEnabled)
  {
    Editor e = getPrefs().edit();
    e.putBoolean(ENABLE_LONG_VIBRATE_ON_ERROR_PREF, isEnabled);
    e.commit();
  }

  static boolean isVibrateOnSpecialEnabled()
  {
   return getPrefs().getBoolean(ENABLE_VIBRATE_ON_SPECIAL_PREF, false);
  }

  static void setVibrateOnSpecialEnabled(boolean isEnabled)
  {
    Editor e = getPrefs().edit();
    e.putBoolean(ENABLE_VIBRATE_ON_SPECIAL_PREF, isEnabled);
    e.commit();
  }

  static boolean isBitmapsEnabled()
  {
   return getPrefs().getBoolean(ENABLE_SHOW_BITMAPS_PREF, false);
  }

  static void setBitmapsEnabled(boolean isEnabled)
  {
    Editor e = getPrefs().edit();
    e.putBoolean(ENABLE_SHOW_BITMAPS_PREF, isEnabled);
    e.commit();

    // the view controller may not yet have been created
    if(GestureInputMethod.getViewController() != null)
    {
      GestureInputMethod.getViewController().showBackground();
    }
  }

  static boolean isLoadFromFilesEnabled()
  {
   return getPrefs().getBoolean(ENABLE_FILE_LOAD_PREF, false);
  }

  static void setLoadFromFiles(boolean isEnabled)
  {
    Editor e = getPrefs().edit();
    e.putBoolean(ENABLE_FILE_LOAD_PREF, isEnabled);
    e.commit();
  }

  public static void log(String tag, String msg)
  {
    if(isLoggingEnabled())
    {
      android.util.Log.v(tag, msg);
    }
  }

  static void setShowResults(boolean isEnabled)
  {
    Editor e = getPrefs().edit();
    e.putBoolean(ENABLE_SHOW_RESULTS_PREF, isEnabled);
    e.commit();

    // the view controller may not yet have been created
    if(GestureInputMethod.getViewController() != null)
    {
      GestureInputMethod.getViewController().showResults();
    }
  }

  static boolean isShowResultsEnabled()
  {
   return getPrefs().getBoolean(ENABLE_SHOW_RESULTS_PREF, false);
  }

  static void displayResults(String msg)
  {
    GestureInputMethod.getViewController().displayResults(msg);
  }

  static Context getAppContext()
  {
    return context;
  }

  static ApplicationResources getApplicationResources()
  {
    return resources;
  }

  public static void showToast(String msg)
  {
    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
  }

  static void reloadGestures()
  {
    App.resources = new ApplicationResources(getAppContext());
  }

  public static void showHelp()
  {
    Intent intent = new Intent(context, HelpActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }

  public static void showSettings()
  {
    Intent intent = new Intent(context, SettingsActivity.class);
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    context.startActivity(intent);
  }

  public static boolean vibrate(boolean error)
  {
      Vibrator vibrator = (Vibrator) App.context.getSystemService(VIBRATOR_SERVICE);

      if (vibrator == null || !vibrator.hasVibrator())
      {
          return false;
      }

      // replaced the passed boolean to flag error condition (gesture not recognized) or not. previously, it was
      // always/only being passed true. I want to distinguish since errors are important and I also use the non-error
      // when switching to special mode
      //vibrator.vibrate(strong ? ApplicationResources.VIBRATION_STRONG_MS : ApplicationResources.VIBRATION_MS);

      if(error && App.isLongVibrateOnErrorEnabled())
      {
        for(int i = 0; i < 5; i++)
        {
          vibrator.vibrate(VIBRATION_STRONG_MS);
          try
          {
            Thread.sleep((VIBRATION_MS * 5));
          }
          catch(InterruptedException e)
          {
            // do nothing
          }
        }
      }
      else
      {
        vibrator.vibrate(VIBRATION_STRONG_MS);
      }

      return true;
  }

  public static float getMinimumRecognitionScore()
  {
    return getPrefs().getFloat(MINIMUM_RECOGNITION_SCORE_PREF, Float.parseFloat(context.getString(R.string.default_recognition_score)));
  }

  public static void setMinimumRecognitionScore(float score)
  {
    Editor e = getPrefs().edit();
    e.putFloat(MINIMUM_RECOGNITION_SCORE_PREF, score);
    e.commit();
  }

  // it is not clear if changing this makes any significant difference (I tried 16, 32, and 64 and, if anything, higher was worse)
  public static int getGestureSamplingSize()
  {
    int i = getPrefs().getInt(GESTURE_SAMPLING_SIZE_PREF, -1);

    // if we find nothing in the prefs, we will use what was originally in the code
    if(i == -1)
    {
      // this was originally used in the code
      i = 16;

      // create and entry in the prefs file with this value so that the user can edit it easily
      Editor e = getPrefs().edit();
      e.putInt(GESTURE_SAMPLING_SIZE_PREF, i);
      e.commit();
    }

    //App.log("Prefs", "gesture sampling size: " + i);
    return i;
  }

 //it is not clear if changing this makes any significant difference (I tried 16, 32, and 64 and, if anything, higher was worse)
 public static float getCloseEnough()
 {
   float f = getPrefs().getFloat(CLOSE_ENOUGH_PREF, .1F);

   // if we find nothing in the prefs, we will use what was originally in the code
   if(f == .1F)
   {
     // create and entry in the prefs file with this value so that the user can edit it easily
     Editor e = getPrefs().edit();
     e.putFloat(CLOSE_ENOUGH_PREF, f);
     e.commit();
   }

   //App.log("Prefs", "close enough size: " + f);
   return f;
 }

  // it is not clear if changing this makes any significant difference (I tried 16, 32, and 64 and, if anything, higher was worse)
  public static boolean getCheckCloseEnough()
  {
    boolean enabled = getPrefs().getBoolean(CHECK_CLOSE_ENOUGH_PREF, false);

    // if we find nothing in the prefs, we will use what was originally in the code
    if(enabled == false)
    {
      // create and entry in the prefs file with this value so that the user can edit it easily
      Editor e = getPrefs().edit();
      e.putBoolean(CHECK_CLOSE_ENOUGH_PREF, enabled);
      e.commit();
    }

    //App.log("Prefs", "check gesture begin/end close enough: " + enabled);
    return enabled;
  }

  // this value clearly makes a huge difference (higher is better) with 8 the real minimum for our needs.
  // i feel like 10 is even better so it is now the default value
  public static int getOrientationSensitivity()
  {
    // not used here - from the code documentation
    // int[] validValues = { com.whitedavidp.unistroke_keyboard.gesture.GestureStore.ORIENTATION_SENSITIVE, com.whitedavidp.unistroke_keyboard.gesture.GestureStore.ORIENTATION_SENSITIVE_4, com.whitedavidp.unistroke_keyboard.gesture.GestureStore.ORIENTATION_SENSITIVE_8 };
    // but note than anything lower than 8 (found in the original code for this app) works so poorly as to be worthless
    // I am experimenting with larger values - which are NOT prohibited even though not mentioned in the original android code
    int sensitivity = getPrefs().getInt(ORIENTATION_SENSITIVITY_PREF, -1);

    // if we find nothing in the prefs, we will use what was originally in the code
    if(sensitivity == -1)
    {
      sensitivity = com.whitedavidp.unistroke_keyboard.gesture.GestureStore.ORIENTATION_SENSITIVE_10;

      // create and entry in the prefs file with this value so that the user can edit it easily
      Editor e = getPrefs().edit();
      e.putInt(ORIENTATION_SENSITIVITY_PREF, sensitivity);
      e.commit();
    }

    //App.log("Prefs", "orientation sensitivity: " + sensitivity);
    return sensitivity;
  }

  public static boolean getShowOnGestureBuilderReminder()
  {
    return getPrefs().getBoolean(SHOW_ON_GESTURE_BUILDER_REMINDER_PREF, false);
  }

  public static void setShowOnGestureBuilderReminder(boolean enabled)
  {
    Editor e = getPrefs().edit();
    e.putBoolean(SHOW_ON_GESTURE_BUILDER_REMINDER_PREF, enabled);
    e.commit();
  }

  public static boolean getShowOnTestReminder()
  {
    return getPrefs().getBoolean(SHOW_ON_TEST_REMINDER_PREF, false);
  }

  public static void setShowOnTestReminder(boolean enabled)
  {
    Editor e = getPrefs().edit();
    e.putBoolean(SHOW_ON_TEST_REMINDER_PREF, enabled);
    e.commit();
  }

  public static long getSpecialModeTapDelay()
  {
    return getPrefs().getLong(SPECIAL_MODE_TAP_DELAY_PREF, SPECIAL_MODE_TAP_DELAY_DEFAULT);
  }

  public static void setSpecialModeTapDelay(long delay)
  {
    if(!validateSpecialModeTapDelay(delay))
    {
      showToast(context.getString(R.string.bad_special_tap_duration));
      return;
    }

    Editor e = getPrefs().edit();
    e.putLong(SPECIAL_MODE_TAP_DELAY_PREF, delay);
    e.commit();
  }

  private static boolean validateSpecialModeTapDelay(long delay)
  {
    for(int i=0; i < SPECIAL_MODE_TAP_DURATIONS.length; i++)
    {
      if(Long.parseLong(SPECIAL_MODE_TAP_DURATIONS[i]) == delay)
      {
        return true;
      }
    }

    return false;
  }

  public static String getShortcutApp()
  {
    return getPrefs().getString(SHORTCUT_APP_PREF, null);
  }

  public static void setShortcutApp(String appPackageName)
  {
    Editor e = getPrefs().edit();
    if(null == appPackageName)
    {
      e.remove(SHORTCUT_APP_PREF);
    }
    else
    {
      e.putString(SHORTCUT_APP_PREF, appPackageName);
    }

    e.commit();
  }

  public static void startShortcutApp()
  {
    PackageManager pm = context.getPackageManager();
    String appPackage = getShortcutApp();
    if(null != appPackage)
    {
      Intent launchIntent = pm.getLaunchIntentForPackage(appPackage);

      if (launchIntent != null)
      {
        context.startActivity(launchIntent);
      }
      else
      {
        showToast(context.getString(R.string.shortcut_app_missing));
      }
    }
  }
}
