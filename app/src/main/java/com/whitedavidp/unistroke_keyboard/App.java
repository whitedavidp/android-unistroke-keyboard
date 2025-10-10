package com.whitedavidp.unistroke_keyboard;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.preference.PreferenceManager;
import android.widget.Toast;

// this class is implemented only because in build version >= 24, sending a log file via intent causes a crash
// due to "security" changes
public class App extends Application
{
  private static final String ENABLE_FILE_LOAD_PREF = "LOAD_FROM_FILES";
  private static final String ENABLE_LOGGING_PREF = "PERFORM_LOGGING";
  private static final String ENABLE_SHOW_RESULTS_PREF = "SHOW_RESULTS";
  private static final String ENABLE_SHOW_BITMAPS_PREF = "SHOW_BITMAPS";
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
}