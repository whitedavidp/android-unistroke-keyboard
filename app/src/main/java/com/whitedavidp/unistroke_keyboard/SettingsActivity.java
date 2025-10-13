package com.whitedavidp.unistroke_keyboard;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;

public class SettingsActivity extends Activity implements OnClickListener
{
  private TextView txtAppInfo = null;
  private CheckBox chkFiles = null;
  private CheckBox chkLogcat = null;
  private CheckBox chkResults = null;
  private CheckBox chkBitmaps = null;
  private Button btnInputSettings = null;
  private Button btnReloadGestures = null;
  private Button btnShowHelp = null;
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_settings);
    setAppInfo();
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
    chkResults = (CheckBox) findViewById(R.id.checkResults);
    chkResults.setOnClickListener(this);
    chkResults.setChecked(App.isShowResultsEnabled());
    chkBitmaps = (CheckBox) findViewById(R.id.checkShowBitmaps);
    chkBitmaps.setOnClickListener(this);
    chkBitmaps.setChecked(App.isBitmapsEnabled());
    btnShowHelp = (Button) findViewById(R.id.buttonShowHelp);
    btnShowHelp.setOnClickListener(this);
  }
  
  private String assembleAppInfo()
  {
    PackageManager manager = getPackageManager();
    StringBuilder sb = new StringBuilder();
    
    try
    {
      PackageInfo info = manager.getPackageInfo(getPackageName(), 0);
      sb.append(String.format(getString(R.string.about_version_name), info.versionName, "\n"));
      sb.append(String.format(getString(R.string.about_version_number), info.versionCode, "\n"));
    }
    catch(NameNotFoundException e)
    {
      App.Log("Settings", e.getLocalizedMessage());
    }

    return sb.toString();
  }
  
  private void setAppInfo()
  {
    txtAppInfo = (TextView) findViewById(R.id.textAppInfo); 
    StringBuilder sb = new StringBuilder(getString(R.string.app_name)+ " " + getString(R.string.app_url) + "\n");
    sb.append(assembleAppInfo());
    txtAppInfo.setText(sb.toString());
  }

  @Override
  public void onBackPressed()
  {
    super.onBackPressed();
    finish();
  }

  @Override
  public void onClick(View v)
  {
    if(v == chkFiles)
    {
      App.setLoadFromFiles(chkFiles.isChecked());
      App.showToast(getString(R.string.checking_files) + chkFiles.isChecked()); 
    }
    
    if(v == chkLogcat)
    {
      App.setLoggingEnabled(chkLogcat.isChecked());
      App.showToast(getString(R.string.logging) + chkLogcat.isChecked());
    } 
    
    if(v == btnInputSettings)
    {
      this.startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
    }
    
    if(v == btnReloadGestures)
    {
      App.reloadGestures();
      App.showToast(getString(R.string.gestures_reloaded));
    }
  
    if(v == btnShowHelp)
    {
      App.showHelp();
    }
  
    if(v == chkResults)
    {
      App.setShowResults(chkResults.isChecked());
      App.showToast(getString(R.string.showing_results) + chkResults.isChecked());
    }
    
    if(v == chkBitmaps)
    {
      App.setBitmapsEnabled(chkBitmaps.isChecked());
      App.showToast(getString(R.string.showing_bitmaps) + chkBitmaps.isChecked());
    }
  }
}
