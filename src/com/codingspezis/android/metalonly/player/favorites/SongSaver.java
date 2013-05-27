package com.codingspezis.android.metalonly.player.favorites;

import java.util.*;

import android.content.*;

import com.codingspezis.android.metalonly.player.*;

/**
 * 
 * this saves a list of songs to shared preferences
 * 
 * TODO: get rid of the quick and dirty solution of saving songs via shared
 * preferences -> do this with XML files
 * 
 */
public class SongSaver {

	// part two of shared preferences key
	public static final String KEY_INTERPRET = "_INTERPRET_";
	public static final String KEY_TITLE = "_TITLE_";
	public static final String KEY_THUMB = "_THUMB_";
	public static final String KEY_DATE = "_DATE_";
	public static final String KEY_COUNT = "_COUNT";

	// given by constructor
	private final String sharedPreferencesPrefix;
	private int limit;

	// holding
	private LinkedList<Song> songList;
	private boolean changes;
	private final SharedPreferences sharedPreferences;

	/**
	 * constructor
	 * 
	 * @param context
	 *            context
	 * @param sharedPreferencesPrefix
	 *            prefix to use for saving songs in shared preferences
	 */
	public SongSaver(Context context, String sharedPreferencesPrefix, int limit) {
		this.sharedPreferencesPrefix = sharedPreferencesPrefix;
		if (limit < 0) {
			this.limit = Integer.MAX_VALUE;
		} else {
			this.limit = limit;
		}
		sharedPreferences = context.getSharedPreferences(
				context.getString(R.string.app_name),
				Context.MODE_MULTI_PROCESS);
		readSongsFromStorage();
		changes = false;
	}

	/**
	 * reads songs from storage
	 * 
	 * @return list of songs from storage
	 */
	private void readSongsFromStorage() {
		int numberOfSongs = sharedPreferences.getInt(sharedPreferencesPrefix
				+ KEY_COUNT, 0);
		songList = new LinkedList<Song>();
		for (int i = 0; i < numberOfSongs; i++) {

			String interpret = sharedPreferences.getString(
					sharedPreferencesPrefix + KEY_INTERPRET + i, "");
			String title = sharedPreferences.getString(sharedPreferencesPrefix
					+ KEY_TITLE + i, "");
			String thumb = sharedPreferences.getString(sharedPreferencesPrefix
					+ KEY_THUMB + i, "");
			long date = sharedPreferences.getLong(sharedPreferencesPrefix
					+ KEY_DATE + i, 0);

			Song song = new Song(interpret, title, thumb, date);

			if (!addSong(song)) {
				break;
			}
		}
	}

	/**
	 * saves songs to storage
	 */
	public void saveSongsToStorage() {
		if (somethingChanged()) {
			SharedPreferences.Editor editor = sharedPreferences.edit();
			for (int i = 0; i < songList.size(); i++) {
				final Song song = songList.get(i);
				editor.putString(sharedPreferencesPrefix + KEY_INTERPRET + i,
						song.interpret);
				editor.putString(sharedPreferencesPrefix + KEY_TITLE + i,
						song.title);
				editor.putString(sharedPreferencesPrefix + KEY_THUMB + i,
						song.getThumb());
				editor.putLong(sharedPreferencesPrefix + KEY_DATE + i,
						song.date);
			}
			editor.putInt(sharedPreferencesPrefix + KEY_COUNT, songList.size());
			editor.commit();
		}
	}

	/**
	 * adds song to list of songs
	 * 
	 * @param song
	 *            song to add
	 * @return true if adding was successful - false otherwise
	 */
	public boolean addSong(Song song) {
		if (song.isValid() && isAlreadyIn(song) == -1
				&& limit > songList.size()) {
			songList.add(song);
			changes = true;
			return true;
		}
		return false;
	}

	/**
	 * adds song to song list - if limit is reached first in will be deleted
	 * 
	 * @param song
	 *            song to add
	 */
	public boolean queeIn(Song song) {
		while (limit <= songList.size()) {
			songList.remove();
		}
		return addSong(song);
	}

	/**
	 * checks if song is already in list of songs
	 * 
	 * @param song
	 *            song to check
	 * @return index of last entry of song if it is in the list - -1 otherwise
	 */
	public int isAlreadyIn(Song song) {
		for (int i = songList.size() - 1; i >= 0; i--) {
			if (songList.get(i).interpret.equals(song.interpret)
					&& songList.get(i).title.equals(song.title)) {
				return i;
			}
		}
		return -1;
	}

	/**
	 * removes song at position i
	 * 
	 * @param i
	 *            position of song to delete
	 */
	public void removeAt(int i) {
		changes = true;
		songList.remove(i);
	}

	/**
	 * removes every song of the list
	 */
	public void clear() {
		while (!songList.isEmpty()) {
			changes = true;
			songList.remove();
		}
	}

	/**
	 * getter for ith song of list of songs
	 * 
	 * @param i
	 *            index of requested song
	 * @return ith song of list of songs
	 */
	public Song get(int i) {
		return songList.get(i);
	}

	/**
	 * @return true if there were changes on list of songs since they have been
	 *         read - false otherwise
	 */
	public boolean somethingChanged() {
		return changes;
	}

	/**
	 * @return returns the size of list of songs
	 */
	public int size() {
		return songList.size();
	}

	/**
	 * reloads song list
	 */
	public void reload() {
		readSongsFromStorage();
	}
}
