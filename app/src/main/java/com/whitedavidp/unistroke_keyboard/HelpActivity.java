package com.whitedavidp.unistroke_keyboard;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.Button;

public class HelpActivity extends Activity implements OnClickListener
{
  private Button btnShowShortcuts = null;
  
  @Override
  public void onBackPressed()
  {
    super.onBackPressed();
    finish();
  }
  
  @Override
  protected void onCreate(Bundle savedInstanceState)
  {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_help);
    btnShowShortcuts = (Button) findViewById(R.id.buttonShowShortcuts);
    btnShowShortcuts.setOnClickListener(this);
    WebView browser = (WebView) findViewById(R.id.helpview);
    browser.getSettings().setBuiltInZoomControls(true); // If zoom is related
    browser.getSettings().setSupportZoom(true);
    //browser.setInitialScale(100);
    browser.loadUrl("file:///android_asset/help.png");
  }
  
  private void showShortcuts()
  {
    String[] shortcuts = getResources().getStringArray(R.array.keyboard_shortcuts_text);
    StringBuilder sb = new StringBuilder("\n");
    for(int i=0; i < shortcuts.length; i++)
    {
      sb.append(shortcuts[i] + "\n\n");
    }
    
    String message = sb.toString();
    message.substring(0, (message.length() - 2));
    
    AlertDialog.Builder builder = new AlertDialog.Builder(this);
    builder.setMessage(message);
    builder.setTitle(getString(R.string.keyboard_shortcuts));
    builder.setNeutralButton(getString(R.string.cancel), new DialogInterface.OnClickListener() 
    {
      public void onClick(DialogInterface dialog, int id)
      {
        // do nothing
      }
    });
    
    AlertDialog dialog = builder.create();
    dialog.show();   
  }

  @Override
  public void onClick(View v)
  {
    if(v == btnShowShortcuts)
    {
      showShortcuts();
    }  
  }
}
