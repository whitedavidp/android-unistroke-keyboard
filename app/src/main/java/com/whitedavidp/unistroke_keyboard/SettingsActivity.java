package com.whitedavidp.unistroke_keyboard;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.CheckBox;

public class SettingsActivity extends Activity implements OnClickListener
{
  private CheckBox chkFiles = null;
  private CheckBox chkLogcat = null;
  private Button btnInputSettings = null;
  private Button btnReloadGestures = null;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    WebView browser = (WebView) findViewById(R.id.viewHelp);
    browser.setInitialScale(100);
    browser.loadUrl("file:///android_asset/help.png");
    btnInputSettings = (Button) findViewById(R.id.buttonInputSettings);
    btnInputSettings.setOnClickListener(this);
    chkFiles = (CheckBox) findViewById(R.id.checkFiles);
    chkFiles.setOnClickListener(this);
    chkFiles.setChecked(App.isLoadFromFilesEnabled());
    btnReloadGestures = (Button) findViewById(R.id.buttonReloadGestures);
    btnReloadGestures.setOnClickListener(this);
    chkLogcat = (CheckBox) findViewById(R.id.checkLogging);
    chkLogcat.setOnClickListener(this);
    chkLogcat.setChecked(App.isLoggingEnabled());
  }

  @Override
  public void onClick(View v)
  {
    if(v == chkFiles)
    {
      App.setLoadFromFiles(chkFiles.isChecked());
      App.showToast("Files to be checked: " + chkFiles.isChecked()); 
    }
    
    if(v == chkLogcat)
    {
      App.setLoggingEnabled(chkLogcat.isChecked());
      App.showToast("Logging: " + chkLogcat.isChecked());
    } 
    
    if(v == btnInputSettings)
    {
      this.startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
    }
    
    if(v == btnReloadGestures)
    {
      App.reloadGestures();
      App.showToast("Gestures have been reloaded");
    }
  }
}
