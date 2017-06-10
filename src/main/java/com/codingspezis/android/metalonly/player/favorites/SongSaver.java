package com.codingspezis.android.metalonly.player.favorites;

import android.content.Context;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.LinkedList;

/**
 * This class persists songs in the internal storage via JSON files.
 */
public class SongSaver {

    public static final String JSON_ARRAY_SONGS = "songs";
    public static final String JSON_STRING_INTERPRET = "interpret";
    public static final String JSON_STRING_TITLE = "title";
    public static final String JSON_STRING_THUMB = "thumb";
    public static final String JSON_LONG_DATE = "date";


    /**
     * variables and objects for saving songs
     */
    private Context context;
    private String fileName;
    private int limit;
    private LinkedList<Song> songList; // main data
    private boolean changes; // is there share to save?


    /**
     * constructor
     *
     * @param context  context of calling object
     * @param fileName name of file where to save songs
     * @param limit    maximum of songs (FIFO) - if value <= 0 songs are unlimited
     */
    public SongSaver(Context context, String fileName, int limit) {
        this.context = context;
        this.fileName = fileName;
        if (limit <= 0)
            this.limit = Integer.MAX_VALUE;
        else
            this.limit = limit;
        songList = new LinkedList<>();
        readSongsFromStorage();
        changes = false;
    }


    /**
     * TODO refactor, we should use jackson directly (i.e. parse file directly into pojo)
     * reads songs from file
     */
    private synchronized void readSongsFromStorage() {

        InputStreamReader isr;
        try {
            isr = new InputStreamReader(context.openFileInput(fileName));
            String s = "";
            int read;
            final int BUFF_SIZE = 256;
            char buffer[] = new char[BUFF_SIZE];
            do {
                read = isr.read(buffer, 0, BUFF_SIZE);
                if (read > 0) s += String.valueOf(buffer, 0, read);
            } while (read == BUFF_SIZE);
            isr.close();
            JSONObject jObj = new JSONObject(s);
            JSONArray jSongs = jObj.getJSONArray(JSON_ARRAY_SONGS);
            songList.clear();
            for (int i = 0; i < jSongs.length(); i++) {
                JSONObject jSong = jSongs.getJSONObject(i);
                String interpret = jSong.getString(JSON_STRING_INTERPRET);
                String title = jSong.getString(JSON_STRING_TITLE);
                String thumb = jSong.getString(JSON_STRING_THUMB);
                long date = jSong.getLong(JSON_LONG_DATE);
                Song song = new Song(interpret, title, thumb, date);
                queeIn(song);
            }
        } catch (FileNotFoundException e) {
            // everything is fine - just nothing saved
        } catch (IOException | JSONException e) {
            // TODO: error handling (but this should be dead code)
            e.printStackTrace();
        }

    }


    /**
     * saves songs to file
     */
    public synchronized void saveSongsToStorage() {
        if (somethingChanged()) {
            try {
                JSONArray jSongs = new JSONArray();
                for (int i = 0; i < songList.size(); i++) {
                    Song song = songList.get(i);
                    JSONObject jSong = new JSONObject();
                    jSong.put(JSON_STRING_INTERPRET, song.getInterpret());
                    jSong.put(JSON_STRING_TITLE, song.getTitle());
                    jSong.put(JSON_STRING_THUMB, song.getThumb());
                    jSong.put(JSON_LONG_DATE, song.getDate());
                    jSongs.put(jSong);
                }
                JSONObject jObj = new JSONObject();
                jObj.put(JSON_ARRAY_SONGS, jSongs);

                context.deleteFile(fileName);
                OutputStreamWriter osr = new OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE));
                osr.write(jObj.toString());
                osr.close();
            } catch (JSONException | IOException e) {
                // TODO: error handling (but this should be dead code)
                e.printStackTrace();
            }
        }
    }


    /**
     * adds song to song list
     *
     * @param song song to add
     * @return true if adding was successful - false otherwise
     */
    public boolean addSong(Song song) {
        if (song.isValid() &&
                isAlreadyIn(song) == -1 &&
                limit > songList.size()) {
            songList.add(song);
            changes = true;
            return true;
        }
        return false;
    }


    /**
     * adds song to song list - if limit is reached first in will be deleted
     *
     * @param song song to add
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
     * @param song song to check
     * @return index of last entry of song if it is in the list - -1 otherwise
     */
    public int isAlreadyIn(Song song) {
        for (int i = songList.size() - 1; i >= 0; i--) {
            if (songList.get(i).getInterpret().equals(song.getInterpret()) &&
                    songList.get(i).getTitle().equals(song.getTitle()))
                return i;
        }
        return -1;
    }


    /**
     * removes song at position i
     *
     * @param i position of song to delete
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
     * getter for i-th song of list of songs
     *
     * @param i index of requested song
     * @return i-th song of list of songs
     */
    public Song get(int i) {
        return songList.get(i);
    }


    /**
     * @return true if there were changes on list of songs since they have been read - false otherwise
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