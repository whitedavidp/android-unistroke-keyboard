package com.whitedavidp.unistroke_keyboard;

import android.app.Application;
import android.content.Context;

// this class is implemented only because in build version >= 24, sending a log file via intent causes a crash
// due to "security" changes
public class App extends Application
{
  protected static String TAG = "_App";
  private static Context context = null;
  
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
  
  public static Context getAppContext()
  {
    return context;
  }
}
