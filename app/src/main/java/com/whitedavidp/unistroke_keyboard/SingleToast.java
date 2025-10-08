package com.whitedavidp.unistroke_keyboard;

import android.content.Context;
import android.widget.Toast;

class SingleToast
{
  private static Toast mToast = null;

  public static void show(Context context, String text, int duration)
  {
    if(mToast != null)
    {
      mToast.cancel();
      mToast = null;
    }
    
    mToast = Toast.makeText(context, text, duration);
    mToast.show();
  }
}
