package com.codingspezis.android.metalonly.player.favorites;

import android.content.Context;

import com.codingspezis.android.metalonly.player.core.HistoricTrack;

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
    private LinkedList<HistoricTrack> trackList; // main data
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
        trackList = new LinkedList<>();
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
            trackList.clear();
            for (int i = 0; i < jSongs.length(); i++) {
                JSONObject jSong = jSongs.getJSONObject(i);
                String interpret = jSong.getString(JSON_STRING_INTERPRET);
                String title = jSong.getString(JSON_STRING_TITLE);
                String thumb = jSong.getString(JSON_STRING_THUMB);
                long date = jSong.getLong(JSON_LONG_DATE);
                HistoricTrack track = new HistoricTrack(interpret, title, thumb, date);
                queeIn(track);
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
                for (int i = 0; i < trackList.size(); i++) {
                    HistoricTrack track = trackList.get(i);
                    JSONObject jSong = new JSONObject();
                    jSong.put(JSON_STRING_INTERPRET, track.getArtist());
                    jSong.put(JSON_STRING_TITLE, track.getTitle());
                    jSong.put(JSON_STRING_THUMB, track.getModerator());
                    jSong.put(JSON_LONG_DATE, track.getPlayedAtAsLong());
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
     * adds track to track list
     *
     * @param track track to add
     * @return true if adding was successful - false otherwise
     */
    public boolean addSong(HistoricTrack track) {
        if (track.isValid() &&
                isAlreadyIn(track) == -1 &&
                limit > trackList.size()) {
            trackList.add(track);
            changes = true;
            return true;
        }
        return false;
    }


    /**
     * adds track to track list - if limit is reached first in will be deleted
     *
     * @param track track to add
     */
    public boolean queeIn(HistoricTrack track) {
        while (limit <= trackList.size()) {
            trackList.remove();
        }
        return addSong(track);
    }


    /**
     * checks if track is already in list of songs
     *
     * @param track track to check
     * @return index of last entry of track if it is in the list - -1 otherwise
     * @deprecated Move to {@link SongSaver#contains(HistoricTrack)}
     */
    public int isAlreadyIn(HistoricTrack track) {
        for (int i = trackList.size() - 1; i >= 0; i--) {
            if (trackList.get(i).getArtist().equals(track.getArtist()) &&
                    trackList.get(i).getTitle().equals(track.getTitle()))
                return i;
        }
        return -1;
    }

    /**
     * Checks if the given track is already in the list of songs
     * @param track track to check
     * @return <code>true</code>, if the song is known. <code>false</code> otherwise
     */
    public boolean contains(HistoricTrack track){
        return isAlreadyIn(track) != -1;
    }

    /**
     * removes song at position i
     *
     * @param i position of song to delete
     */
    public void removeAt(int i) {
        changes = true;
        trackList.remove(i);
    }


    /**
     * removes every song of the list
     */
    public void clear() {
        while (!trackList.isEmpty()) {
            changes = true;
            trackList.remove();
        }
    }


    /**
     * getter for i-th song of list of songs
     *
     * @param i index of requested song
     * @return i-th song of list of songs
     */
    public HistoricTrack get(int i) {
        return trackList.get(i);
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
        return trackList.size();
    }


    /**
     * reloads song list
     */
    public void reload() {
        readSongsFromStorage();
    }


}