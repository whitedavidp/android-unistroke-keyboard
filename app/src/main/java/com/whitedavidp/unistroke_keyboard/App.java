package com.whitedavidp.unistroke_keyboard;

import android.app.Application;
import android.content.Context;
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
    
    // according to the docs, this should be done as early in the app processing as possible
    // createNotificationChannel();
  }
  
  /* private void createNotificationChannel()
  {
    // Create the NotificationChannel, but only on API 26+ because
    // the NotificationChannel class is new and not in the support library
    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
    {
      CharSequence name = getString(R.string.notificationChannelName);
      String description = getString(R.string.notificationChannelDesc);
      int importance = NotificationManager.IMPORTANCE_DEFAULT;
      NotificationChannel channel = new NotificationChannel(Util.NOTIFICATION_CHANNEL_ID, name, importance);
      channel.setDescription(description);
      // Register the channel with the system; you can't change the importance
      // or other notification behaviors after this
      NotificationManager notificationManager = getSystemService(NotificationManager.class);
      notificationManager.createNotificationChannel(channel);
    }
  }
  */
  
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
  }
  
  static boolean isShowResultsEnabled()
  {
   return getPrefs().getBoolean(ENABLE_SHOW_RESULTS_PREF, false);
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
    SingleToast.show(context, msg, Toast.LENGTH_SHORT);
  }
  
  static void reloadGestures()
  {
    App.resources = new ApplicationResources(getAppContext());
  }
}

