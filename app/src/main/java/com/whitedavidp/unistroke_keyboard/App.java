package com.whitedavidp.unistroke_keyboard;

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
  private static final String ENABLE_FILE_LOAD_PREF = "LOAD_FROM_FILES";
  private static final String ENABLE_LOGGING_PREF = "PERFORM_LOGGING";
  private static final String ENABLE_SHOW_RESULTS_PREF = "SHOW_RESULTS";
  private static final String ENABLE_SHOW_BITMAPS_PREF = "SHOW_BITMAPS";
  private static final String ENABLE_VIBRATE_ON_SPECIAL_PREF = "VIBRATE_ON_SPECIAL";
  private static final String ENABLE_LONG_VIBRATE_ON_ERROR_PREF = "LONG_VIBRATE_ON_ERROR";
  private static final String MINIMUM_RECOGNITION_SCORE_PREF = "MINIMUM_RECOGNITION_SCORE";
  private static final String SHORTCUT_APP_PREF = "SHORTCUT_APP";
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
  
  static SharedPreferences getPrefs()
  {
    if(null == preferences)
    {
      preferences = PreferenceManager.getDefaultSharedPreferences(App.getAppContext());
    }
    
    return preferences;
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
  
  public static void Log(String tag, String msg)
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
  
  static void showToast(String msg)
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