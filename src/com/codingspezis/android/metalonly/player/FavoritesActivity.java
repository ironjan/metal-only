package com.codingspezis.android.metalonly.player;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;

import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;
import com.actionbarsherlock.view.SubMenu;
import com.codingspezis.android.metalonly.player.WishChecker.AllowedActions;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

/**
 * FavoritesActivity
 * @version 09.01.2013
 *
 * TODO: option for adding songs manually
 * TODO: saving via xml
 *
 * this activity displays favorites and allows to handle them
 *
 */
public class FavoritesActivity extends SubActivity implements OnItemClickListener{
	
	// GUI
	private ListView listView;
	private Menu menu;
	
	// data
	private SongSaver favoritesSaver;
	
	// me
	private FavoritesActivity me = this;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.list);
		favoritesSaver = new SongSaver(this, MainActivity.KEY_SP_FAVORITE, -1);
		listView = (ListView)findViewById(R.id.listView1);
		listView.setOnItemClickListener(this);
		displayFavorites();
	}
	
	@Override
	public void onPause(){
		favoritesSaver.saveSongsToStorage();
		super.onPause();
	}
	
	/**
	 * displays favorites on screen
	 */
	private void displayFavorites(){
		listView.removeAllViewsInLayout();
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		for(int i=favoritesSaver.size()-1; i>=0; i--){
		    HashMap<String, String> dataEntry = new HashMap<String, String>(2);		    
		    dataEntry.put(SongAdapterFavorites.KEY_TITLE, favoritesSaver.get(i).title);
		    dataEntry.put(SongAdapterFavorites.KEY_INTERPRET, favoritesSaver.get(i).interpret);
		    data.add(dataEntry);
		}
		SongAdapterFavorites adapter = new SongAdapterFavorites(this, data);
		listView.setAdapter(adapter); 
	}
	
	/**
	 * handles an action on an index
	 * @param index item to handle
	 * @param action action to handle
	 */
	private void handleAction(final int index, int action){
		switch(action){
		case 0: // wish
			if(!HTTPGrabber.displayNetworkSettingsIfNeeded(this)){
				WishChecker wishChecker = new WishChecker(this, WishActivity.URL_WISHES);
				wishChecker.setOnWishesCheckedListener(new OnWishesCheckedListener() {
					@Override
					public void onWishesChecked(AllowedActions allowedActions) {
						if(WishActivity.canWishOrDisplayNot(me, allowedActions)){
							Bundle bundle = new Bundle();
							bundle.putBoolean(WishActivity.KEY_WISHES_ALLOWED,  allowedActions.wishes);
							bundle.putBoolean(WishActivity.KEY_REGARDS_ALLOWED, allowedActions.regards);
							bundle.putString(WishActivity.KEY_NUMBER_OF_WISHES, allowedActions.limit);
							bundle.putString(WishActivity.KEY_DEFAULT_INTERPRET, favoritesSaver.get(index).interpret);
							bundle.putString(WishActivity.KEY_DEFAULT_TITLE, favoritesSaver.get(index).title);
							Intent wishIntent = new Intent(me, WishActivity.class);
							wishIntent.putExtras(bundle);
							me.startActivity(wishIntent);
						}
					}
				});
				wishChecker.start();
			}
			break;
		case 1: // YouTube
			String searchStr = favoritesSaver.get(index).interpret+" - "+favoritesSaver.get(index).title;
			try {
				searchStr = URLEncoder.encode(searchStr, "UTF-8");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Uri url = Uri.parse("http://www.youtube.com/results?search_query="+searchStr);
			Intent youtube = new Intent(Intent.ACTION_VIEW, url);
			startActivity(youtube);
			break;
		case 2: // share
			String message = favoritesSaver.get(index).interpret+" - "+favoritesSaver.get(index).title;
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("text/plain");
			share.putExtra(Intent.EXTRA_TEXT, message);
			startActivity(Intent.createChooser(share, getResources().getStringArray(R.array.favorite_options_array)[2]));
			break;
		case 3: // delete
			favoritesSaver.removeAt(index);
			displayFavorites();
		}
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		final int index = arg2;
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setItems(R.array.favorite_options_array, new DialogInterface.OnClickListener() {
	               @Override
				public void onClick(DialogInterface dialog, int which) {
	            	   handleAction(favoritesSaver.size() - index - 1, which);
	           }
	    });
	    builder.show();
	}

	/**
	 * generates options menu
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		this.menu=menu;
		SubMenu sub = menu.addSubMenu(0, R.id.menu_sub, 0, R.string.menu);
		sub.setIcon(R.drawable.ic_core_unstyled_action_overflow);
		sub.add(0, R.id.shareall, 0, R.string.menu_shareall);
		sub.add(0, R.id.deleteall, 0, R.string.menu_deleteall);
        sub.getItem().setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS | MenuItem.SHOW_AS_ACTION_WITH_TEXT);
        return true;
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
	    if(keyCode == KeyEvent.KEYCODE_MENU){
	        if (event.getAction() == KeyEvent.ACTION_UP && menu != null && menu.findItem(R.id.menu_sub) != null)
	        {
	            menu.performIdentifierAction(R.id.menu_sub, 0);
	            return true;
	        }
	    }
	    return super.onKeyUp(keyCode, event);
	}
	
	/**
	 * handles menu button actions
	 */
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		// share all
		if(item.getItemId() == 			R.id.shareall){	
			// generate share string
			String message = "";
			for(int i=favoritesSaver.size()-1; i>=0; i--){
				message += favoritesSaver.get(i).interpret+" - "+favoritesSaver.get(i).title+"\n";
			}
			// open share dialog
			Intent share = new Intent(Intent.ACTION_SEND);
			share.setType("text/plain");
			share.putExtra(Intent.EXTRA_TEXT, message);
			startActivity(Intent.createChooser(share, getResources().getStringArray(R.array.favorite_options_array)[2]));
		}
		// delete all
		else if(item.getItemId() == 	R.id.deleteall){
			askSureDelete(this, new OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					favoritesSaver.clear();
					displayFavorites();
				}
			}, null);
		} else return false;
		return true;
	}
	
	/**
	 * asks if user is sure to delete something
	 * @param yes what is to do if user clicks yes
	 * @param no what is to do if user clicks no
	 */
	public static void askSureDelete(Context context, OnClickListener yes, OnClickListener no){
		AlertDialog.Builder alert = new AlertDialog.Builder(context);
		alert.setMessage(R.string.delete_sure);
		alert.setNegativeButton(R.string.no, no);
		alert.setPositiveButton(R.string.yes, yes);
		alert.show();
	}
}
