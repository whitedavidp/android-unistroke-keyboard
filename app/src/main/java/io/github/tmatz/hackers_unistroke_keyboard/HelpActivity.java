package com.whitedavidp.unistroke_keyboard;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class HelpActivity extends Activity
{

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_help);
    WebView browser = (WebView) findViewById(R.id.helpview);
    browser.setInitialScale(100);
    browser.loadUrl("file:///android_asset/help.png");
  }
}
