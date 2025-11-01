package com.whitedavidp.unistroke_keyboard;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

import com.whitedavidp.unistroke_keyboard.gesturebuilder.GestureBuilderActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.os.Environment;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsActivity extends Activity implements OnClickListener, OnItemSelectedListener
{
  private TextView txtAppInfo = null;
  private CheckBox chkFiles = null;
  private CheckBox chkLogcat = null;
  private CheckBox chkResults = null;
  private CheckBox chkBitmaps = null;
  private CheckBox chkVibrateOnSpecial = null;
  private CheckBox chkLongVibrateOnError = null;
  private Button btnInputSettings = null;
  private Button btnReloadGestures = null;
  private Button btnShowHelp = null;
  private Button btnSetShortcutApp = null;
  private Button btnEditGestures = null;
  private Button btnMinimumRecognition = null;
  private EditText editMinimumRecognition = null;
  private Spinner spinSpecialTouchInterval = null;
  private Spinner spinGestureSet = null;
  
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
    btnEditGestures = (Button) findViewById(R.id.buttonEditGestures);
    btnEditGestures.setOnClickListener(this);
    chkVibrateOnSpecial = (CheckBox) findViewById(R.id.checkVibrateOnSpecial);
    chkVibrateOnSpecial.setOnClickListener(this);
    chkVibrateOnSpecial.setChecked(App.isVibrateOnSpecialEnabled());
    chkLongVibrateOnError = (CheckBox) findViewById(R.id.checkLongVibrateOnError);
    chkLongVibrateOnError.setOnClickListener(this);
    chkLongVibrateOnError.setChecked(App.isLongVibrateOnErrorEnabled());
    editMinimumRecognition = (EditText) findViewById(R.id.editMinimumRecognition);
    editMinimumRecognition.setText(Float.toString(App.getMinimumRecognitionScore()));
    btnMinimumRecognition = (Button) findViewById(R.id.buttonMinimumRecognition);
    btnMinimumRecognition.setOnClickListener(this);
    btnSetShortcutApp = (Button) findViewById(R.id.buttonSetShortcutApp);
    btnSetShortcutApp.setOnClickListener(this);
    spinSpecialTouchInterval = (Spinner) findViewById(R.id.spinSpecialTouchInterval);
    
    // this is used as a semiphore to prevent showing toasts when the initial position is set. what a PIA
    spinSpecialTouchInterval.setTag("init");
    
    ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, App.SPECIAL_MODE_TAP_DURATIONS);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinSpecialTouchInterval.setAdapter(adapter);
    spinSpecialTouchInterval.setSelection(getSpecialTouchIntervalPosition());
    spinSpecialTouchInterval.setOnItemSelectedListener(this);
    
    spinGestureSet = (Spinner) findViewById(R.id.spinGestureSet);
    adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, App.GESTURE_SETS);
    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinGestureSet.setAdapter(adapter);
  }
  
  private int getSpecialTouchIntervalPosition()
  {
    long delay = App.getSpecialModeTapDelay();
    for(int i=0; i < App.SPECIAL_MODE_TAP_DURATIONS.length; i++)
    {
      if(Long.parseLong(App.SPECIAL_MODE_TAP_DURATIONS[i]) == delay)
      {
        return i;
      }
    }
    
    return 0;
  }

  @Override
  protected void onDestroy()
  {
    super.onDestroy();
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
      App.log("Settings", e.getLocalizedMessage());
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
  
  public void startGestureBuilderActivity(String gestureSet)
  {
    String gestureFileName = "gestures_" + gestureSet;
    File gestureFile = new File(Environment.getExternalStorageDirectory(), gestureFileName);
    
    // if the file does not exist, create it
    if(!gestureFile.exists())
    {
      int rawResourceId = App.mapGestureSetNameToResourceId(gestureSet);
      InputStream inputStream = getResources().openRawResource(rawResourceId);
      
      try
      {
        FileOutputStream outputStream = new FileOutputStream(gestureFile);
      
        byte[] buffer = new byte[1024]; // Buffer for reading/writing
        int read;
        while ((read = inputStream.read(buffer)) != -1)
        {
            outputStream.write(buffer, 0, read);
        }
        
        if(null != inputStream)
        {
          inputStream.close();
        }
        
        if(null != outputStream)
        {
          outputStream.close();
        }
      }
      catch(Exception e)
      {
        String msg = getString(R.string.gesture_file_error) + " + s";
        App.log("startGestureBuilderActivity", msg);
        App.showToast(msg);
        return;
      }
    }
    
    if(!App.getShowOnGestureBuilderReminder())
    {
      showEditGestureInstructions(gestureFileName);
    }
    else
    {
       startGestureBuilder(gestureFileName);
    }
  }
  
  private void startGestureBuilder(String gestureFileName)
  {  
    // back the file up first
    File gestureFile = new File(Environment.getExternalStorageDirectory(), gestureFileName);
    String gestureFileBackupName = gestureFileName + ".bak";
    File gestureFileBackup = new File(Environment.getExternalStorageDirectory(), gestureFileBackupName);

    if(gestureFileBackup.exists())
    {        
      gestureFileBackup.delete();
    }
    
    String cmd = "cp " + gestureFile.getAbsolutePath() + " " + gestureFileBackup.getAbsolutePath();
    try
    {
      Runtime.getRuntime().exec(cmd);
    }
    catch(Exception e)
    {
      App.log("startGestureBuilderActivity", "failed to create backup: " + gestureFileBackup.getAbsolutePath() + " - exception " + e.getLocalizedMessage());
    }

    // finally start the gesture builder
    Intent intent = new Intent(this, GestureBuilderActivity.class);
    //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    intent.putExtra(App.GESTURE_BUILDER_FILE_NAME, gestureFileName);
    this.startActivity(intent);
  }
  
  public void showEditGestureInstructions(String gestureFileName)
  {
    final String whichFile = gestureFileName;
    String instructions = getResources().getString(R.string.edit_gesture_instructions);
    StringBuilder sb = new StringBuilder("\n");
    sb.append(instructions + "\n");
    
    String message = sb.toString();
    //message.substring(0, (message.length() - 2));
    
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(message);
    builder.setTitle(getString(R.string.edit_gestures) + whichFile);
    final CheckBox chkDontRemindAgain = new CheckBox(SettingsActivity.this);
    chkDontRemindAgain.setText(getString(R.string.dont_remind));
    chkDontRemindAgain.setChecked(false);     
    builder.setView(chkDontRemindAgain);

    builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() 
    {
      public void onClick(DialogInterface dialog, int id)
      {
        App.setShowOnGestureBuilderReminder(chkDontRemindAgain.isChecked());
        startGestureBuilder(whichFile);
      }
    });
    
    builder.setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
    {
      public void onClick(DialogInterface dialog, int id)
      {
        return;
      }
    });
    
    AlertDialog dialog = builder.create();
    dialog.show();   
  }

  @Override
  public void onClick(View v)
  {
    if(v == btnMinimumRecognition)
    {
      float newValue = validateMinimumRecognition();
      if(-1 != newValue)
      {
        App.setMinimumRecognitionScore(newValue);
        App.showToast(getString(R.string.minimum_recognition) + newValue);
      }
    }
    
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
      startActivity(new Intent(Settings.ACTION_INPUT_METHOD_SETTINGS));
    }

    if(v == btnSetShortcutApp)
    {
      Intent intent = new Intent(this, SelectShortcutAppActivity.class);
      intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
      startActivity(intent);
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

    if(v == chkVibrateOnSpecial)
    {
      App.setVibrateOnSpecialEnabled(chkVibrateOnSpecial.isChecked());
      App.showToast(getString(R.string.vibrate_on_special) + ": " + chkVibrateOnSpecial.isChecked());
    }

    if(v == chkLongVibrateOnError)
    {
      App.setLongVibrateOnErrorEnabled(chkLongVibrateOnError.isChecked());
      App.showToast(getString(R.string.long_vibrate_on_error) + ": " + chkLongVibrateOnError.isChecked());
    }
    
    if(v == btnEditGestures)
    {
      int i = spinGestureSet.getSelectedItemPosition();
      startGestureBuilderActivity(App.GESTURE_SETS[i]);
    }
  }

  private float validateMinimumRecognition()
  {
    float newValue = Float.parseFloat(getString(R.string.default_recognition_score));

    String s = editMinimumRecognition.getText().toString();
    if(null != s)
    {
      float f = Float.parseFloat(s);
      if(f < .5 || f > 3.0)
      {
        App.showToast(getString(R.string.crazy_minimum_value));
        editMinimumRecognition.setText(Float.toString(App.getMinimumRecognitionScore()));
        return -1;
      }

      newValue = f;
    }

    return newValue;
  }

  @Override
  public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3)
  {
    App.setSpecialModeTapDelay(Long.parseLong(App.SPECIAL_MODE_TAP_DURATIONS[position]));
    if(null == spinSpecialTouchInterval.getTag())
    {
      App.showToast(getString(R.string.special_mode_tap) + App.SPECIAL_MODE_TAP_DURATIONS[position]);
    }
    else
    {
      spinSpecialTouchInterval.setTag(null); 
    }
  }

  @Override
  public void onNothingSelected(AdapterView<?> arg0)
  {
    // do nothing
    
  }
}
