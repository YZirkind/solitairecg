/*
  Copyright 2008 Google Inc.
  
  Licensed under the Apache License, Version 2.0 (the "License");
  you may not use this file except in compliance with the License.
  You may obtain a copy of the License at
  
       http://www.apache.org/licenses/LICENSE-2.0
  
  Unless required by applicable law or agreed to in writing, software
  distributed under the License is distributed on an "AS IS" BASIS,
  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  See the License for the specific language governing permissions and
  limitations under the License.

  Modified by Curtis Gedak 2015
*/
package net.sourceforge.solitaire_cg;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.SubMenu;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

// Base activity class.
public class SolitaireCG extends Activity {

  public static String VERSION_NAME = "";

  private static final int MENU_NEW_GAME     = 1;
  private static final int MENU_RESTART      = 2;
  private static final int MENU_OPTIONS      = 4;
  private static final int MENU_STATS        = 5;
  private static final int MENU_HELP         = 6;
  private static final int MENU_FORTYTHIEVES = 7;
  private static final int MENU_FREECELL     = 8;
  private static final int MENU_GOLF         = 9;
  private static final int MENU_KLONDIKE     = 10;
  private static final int MENU_SPIDER       = 11;

  // View extracted from main.xml.
  private View mMainView;
  private SolitaireView mSolitaireView;
  private SharedPreferences mSettings;

  // Shared preferences are where the various user settings are stored.
  public SharedPreferences GetSettings() { return mSettings; }

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    // Force landscape and no title for extra room
    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    requestWindowFeature(Window.FEATURE_NO_TITLE);

    // If the user has never accepted the EULA show it again.
    mSettings = getSharedPreferences("SolitairePreferences", 0);
    setContentView(R.layout.main);
    mMainView = findViewById(R.id.main_view);
    mSolitaireView = (SolitaireView) findViewById(R.id.solitaire);
    mSolitaireView.SetTextView((TextView) findViewById(R.id.text));

    //StartSolitaire(savedInstanceState);
    registerForContextMenu(mSolitaireView);

    // Set global variable for versionName
    try {
      VERSION_NAME = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
    } catch (NameNotFoundException e) {
      Log.e("SolitaireCG.java", e.getMessage());
    }
  }

  // Entry point for starting the game.
  //public void StartSolitaire(Bundle savedInstanceState) {
  @Override
  public void onStart() {
    super.onStart();
    mSolitaireView.onStart();

    if (mSettings.getBoolean("SolitaireSaveValid", false)) {
      SharedPreferences.Editor editor = GetSettings().edit();
      editor.putBoolean("SolitaireSaveValid", false);
      editor.commit();
      // If save is corrupt, just start a new game.
      if (mSolitaireView.LoadSave()) {
        SplashScreen();
        return;
      }
    }

    mSolitaireView.InitGame(mSettings.getInt("LastType", Rules.KLONDIKE));
    SplashScreen();
  }

  // Force show splash screen if this is the first time played.
  private void SplashScreen() {
    if (!mSettings.getBoolean("PlayedBefore", false)) {
      mSolitaireView.DisplaySplash();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    super.onCreateOptionsMenu(menu);

    SubMenu subMenu = menu.addSubMenu(0, MENU_NEW_GAME, 0, R.string.menu_newgame);
    subMenu.add(0, MENU_FORTYTHIEVES, 0, R.string.menu_fortythieves);
    subMenu.add(0, MENU_FREECELL, 0, R.string.menu_freecell);
    subMenu.add(0, MENU_GOLF, 0, R.string.menu_golf);
    subMenu.add(0, MENU_KLONDIKE, 0, R.string.menu_klondike);
    subMenu.add(0, MENU_SPIDER, 0, R.string.menu_spider);

    menu.add(0, MENU_RESTART, 0, R.string.menu_restart);
    menu.add(0, MENU_OPTIONS, 0, R.string.menu_options);
    menu.add(0, MENU_STATS, 0, R.string.menu_stats);
    menu.add(0, MENU_HELP, 0, R.string.menu_help);
    return true;
  }

  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case MENU_FORTYTHIEVES:
        mSolitaireView.InitGame(Rules.FORTYTHIEVES);
        break;
      case MENU_FREECELL:
        mSolitaireView.InitGame(Rules.FREECELL);
        break;
      case MENU_GOLF:
        mSolitaireView.InitGame(Rules.GOLF);
        break;
      case MENU_KLONDIKE:
        mSolitaireView.InitGame(Rules.KLONDIKE);
        break;
      case MENU_SPIDER:
        mSolitaireView.InitGame(Rules.SPIDER);
        break;
      case MENU_RESTART:
        mSolitaireView.RestartGame();
        break;
      case MENU_OPTIONS:
        DisplayOptions();
        break;
      case MENU_STATS:
        DisplayStats();
        break;
      case MENU_HELP:
        DisplayHelp();
        break;
    }

    return false;
  }

  @Override
  public void onCreateContextMenu(ContextMenu menu, View v,
                                  ContextMenuInfo menuInfo) {
    super.onCreateContextMenu(menu, v, menuInfo);
    MenuInflater inflater = getMenuInflater();
    inflater.inflate(R.menu.context, menu);
  }

  // Alternate Menu
  // Invoked with long press and needed on some devices where Android
  // options menu is not accessible or available.
  @Override
  public boolean onContextItemSelected(MenuItem item) {
    switch (item.getItemId()) {
      case R.id.context_fortythieves:
        mSolitaireView.InitGame(Rules.FORTYTHIEVES);
        break;
      case R.id.context_freecell:
        mSolitaireView.InitGame(Rules.FREECELL);
        break;
      case R.id.context_golf:
        mSolitaireView.InitGame(Rules.GOLF);
        break;
      case R.id.context_klondike:
        mSolitaireView.InitGame(Rules.KLONDIKE);
        break;
      case R.id.context_spider:
        mSolitaireView.InitGame(Rules.SPIDER);
        break;
      case R.id.context_restart:
        mSolitaireView.RestartGame();
        break;
      case R.id.context_options:
        DisplayOptions();
        break;
      case R.id.context_stats:
        DisplayStats();
        break;
      case R.id.context_help:
        DisplayHelp();
        break;
      default:
        return super.onContextItemSelected(item);
    }

    return false;
  }

  @Override
  protected void onPause() {
    super.onPause();
    mSolitaireView.onPause();
  }

  @Override
  protected void onStop() {
    super.onStop();
    mSolitaireView.SaveGame();
  }

  @Override
  protected void onResume() {
    super.onResume();
    mSolitaireView.onResume();
  }

  @Override
  public void onSaveInstanceState(Bundle outState) {
    super.onSaveInstanceState(outState);
  }

  public void DisplayOptions() {
    mSolitaireView.SetTimePassing(false);
    new Options(this, mSolitaireView.GetDrawMaster());
  }

  public void DisplayHelp() {
    mSolitaireView.SetTimePassing(false);
    new Help(this, mSolitaireView.GetDrawMaster());
  }

  public void DisplayStats() {
    mSolitaireView.SetTimePassing(false);
    new Stats(this, mSolitaireView);
  }

  public void CancelOptions() {
    setContentView(mMainView);
    mSolitaireView.requestFocus();
    mSolitaireView.SetTimePassing(true);
  }

  public void NewOptions() {
    setContentView(mMainView);
    mSolitaireView.InitGame(mSettings.getInt("LastType", Rules.KLONDIKE));
  }

  // This is called for option changes that require a refresh, but not a new game
  public void RefreshOptions() {
    setContentView(mMainView);
    mSolitaireView.RefreshOptions();
  }
}
