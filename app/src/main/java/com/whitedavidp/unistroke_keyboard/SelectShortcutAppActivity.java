package com.whitedavidp.unistroke_keyboard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import android.os.Bundle;
import android.app.Activity;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

public class SelectShortcutAppActivity extends Activity implements OnClickListener
{
  private Spinner spinChooseApp = null;
  private Button btnOk = null;
  private Button btnCancel = null;
  private CheckBox chkIncludeSystem = null;
  private HashMap<String, String> installedApps = new HashMap<String, String>();
  private PackageManager pm = null;
  private boolean changesMade = false;

  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_shortcut_app);

    pm = getPackageManager();

    spinChooseApp = (Spinner) findViewById(R.id.spinChooseApp);

    btnOk = (Button) findViewById(R.id.btnOk);
    btnOk.setOnClickListener(this);
    btnCancel = (Button) findViewById(R.id.btnCancel);
    btnCancel.setOnClickListener(this);
    chkIncludeSystem = (CheckBox) findViewById(R.id.chkIncludeSystem);
    chkIncludeSystem.setOnClickListener(this);

    String currentApp = populateApplications();

    if(null != currentApp)
    {
      TextView txtCurrentApp = (TextView) findViewById(R.id.txtCurrentApp);
      txtCurrentApp.setText(getString(R.string.current_app) + " " + currentApp);
    }

    // by default the title bar is black which looks crappy. so change it
    View title = getWindow().findViewById(android.R.id.title);
    title.setBackgroundColor(Color.GRAY);
/*    View dialog = (View) title.getParent();
    dialog.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark)); */
  }

  protected void onStart()
  {
    super.onStart();
  }

  String populateApplications()
  {
    String currentAppPackage = App.getShortcutApp();
    String currentAppName = null;
    List<ApplicationInfo> list = pm.getInstalledApplications(0);
    int size = list.size();
    ArrayList<String> appList = new ArrayList<String>();
    boolean includeSystem = chkIncludeSystem.isChecked();

    for(int i = 0; i < size; i++)
    {
      ApplicationInfo info = list.get(i);
      String label = info.loadLabel(pm).toString();
      String pkg = info.packageName;
      
      if(null != currentAppPackage && pkg.equals(currentAppPackage))
      {
        currentAppName = label;
      }

      // skip system packages if we are so instructed
      if(!includeSystem)
      {
        if((info.flags & ApplicationInfo.FLAG_SYSTEM)!= 0)
        {
          continue;
        }
      }

      // we combine the app name and package to avoid multiple apps sharing the same
      // name
      String appEntry = label + " (" + pkg +")";
      
      installedApps.put(appEntry, pkg);
      appList.add(appEntry);
    }

    Collections.sort(appList);
    appList.add(0, getString(R.string.clear_app));
    size = appList.size();
    String[] apps = new String[size];
    appList.toArray(apps);

    ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, apps);
    dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
    spinChooseApp.setAdapter(dataAdapter);
    
    return currentAppName;
  }

  @Override
  public void onDestroy()
  {
    if(!changesMade)
    {
      App.showToast(getString(R.string.no_changes));
    }
    
    super.onDestroy();
  }
  
  @Override
  public void onClick(View v)
  {
    if(v == btnCancel)
    {
      finish();
      return;
    }
    
    if(v == btnOk)
    {     
      String packageId = null;
      
      // remember that NONE is in the first position
      if((AdapterView.INVALID_POSITION != spinChooseApp.getSelectedItemPosition()) && (0 != spinChooseApp.getSelectedItemPosition()))
      {
        packageId = installedApps.get((String) (spinChooseApp.getSelectedItem()));
      }
      
      App.setShortcutApp(packageId);
      changesMade = true;
      finish();
      return;
    }

    if(v == chkIncludeSystem)
    {
      populateApplications();
      return;
    }
  }
}
