package com.codingspezis.android.metalonly.player.favorites

import android.content.Context

import com.codingspezis.android.metalonly.player.core.HistoricTrack

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject

import java.io.FileNotFoundException
import java.io.IOException
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.util.Collections
import java.util.LinkedList

/**
 * This class persists songs in the internal storage via JSON files.
 * @param context context of calling object
 * @param fileName name of file where to save songs
 * @param limit maximum of songs (FIFO) - if value <= 0 songs are unlimited

 */
class SongSaver(
    private val context: Context,
    private val fileName: String,
    limit: Int
) {
    private var limit: Int = 0
    private val trackList: LinkedList<HistoricTrack> // main data
    private var changes: Boolean = false // is there share to save?

    init {
        if (limit <= 0)
            this.limit = Integer.MAX_VALUE
        else
            this.limit = limit
        trackList = LinkedList<HistoricTrack>()
        readSongsFromStorage()
        changes = false
    }

    /**
     * TODO refactor, we should use jackson directly (i.e. parse file directly into pojo)
     * reads songs from file
     */
    @Synchronized private fun readSongsFromStorage() {

        val isr: InputStreamReader
        try {
            isr = InputStreamReader(context.openFileInput(fileName))
            var s = ""
            var read: Int
            val BUFF_SIZE = 256
            val buffer = CharArray(BUFF_SIZE)
            do {
                read = isr.read(buffer, 0, BUFF_SIZE)
                if (read > 0) s += String(buffer, 0, read)
            } while (read == BUFF_SIZE)
            isr.close()
            val jObj = JSONObject(s)
            val jSongs = jObj.getJSONArray(JSON_ARRAY_SONGS)
            trackList.clear()
            for (i in 0..jSongs.length() - 1) {
                val jSong = jSongs.getJSONObject(i)
                val interpret = jSong.getString(JSON_STRING_INTERPRET)
                val title = jSong.getString(JSON_STRING_TITLE)
                val thumb = jSong.getString(JSON_STRING_THUMB)
                val date = jSong.getLong(JSON_LONG_DATE)
                val track = HistoricTrack(interpret, title, thumb, date)
                queueIn(track)
            }
        } catch (e: FileNotFoundException) {
            // everything is fine - just nothing saved
        } catch (e: IOException) {
            // TODO: error handling (but this should be dead code)
            e.printStackTrace()
        } catch (e: JSONException) {
            e.printStackTrace()
        }
    }

    /**
     * saves songs to file
     */
    @Synchronized fun saveSongsToStorage() {
        if (somethingChanged()) {
            try {
                val jSongs = JSONArray()
                for (i in trackList.indices) {
                    val track = trackList[i]
                    val jSong = JSONObject()
                    jSong.put(JSON_STRING_INTERPRET, track.artist)
                    jSong.put(JSON_STRING_TITLE, track.title)
                    jSong.put(JSON_STRING_THUMB, track.moderator)
                    jSong.put(JSON_LONG_DATE, track.playedAtAsLong)
                    jSongs.put(jSong)
                }
                val jObj = JSONObject()
                jObj.put(JSON_ARRAY_SONGS, jSongs)

                context.deleteFile(fileName)
                val osr = OutputStreamWriter(context.openFileOutput(fileName, Context.MODE_PRIVATE))
                osr.write(jObj.toString())
                osr.close()
            } catch (e: JSONException) {
                // TODO: error handling (but this should be dead code)
                e.printStackTrace()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    /**
     * adds track to track list

     * @param track track to add
     * *
     * @return true if adding was successful - false otherwise
     */
    fun addSong(track: HistoricTrack): Boolean {
        if (track.isValid &&
                isAlreadyIn(track) == -1 &&
                limit > trackList.size) {
            trackList.add(track)
            changes = true
            return true
        }
        return false
    }

    /**
     * adds track to track list - if limit is reached first in will be deleted

     * @param track track to add
     */
    fun queueIn(track: HistoricTrack): Boolean {
        while (limit <= trackList.size) {
            trackList.remove()
        }
        return addSong(track)
    }

    /**
     * checks if track is already in list of songs

     * @param track track to check
     * *
     * @return index of last entry of track if it is in the list - -1 otherwise
     * *
     */
    @Deprecated("Move to {@link SongSaver#contains(HistoricTrack)}")
    fun isAlreadyIn(track: HistoricTrack): Int {
        for (i in trackList.indices.reversed()) {
            if (trackList[i].artist == track.artist && trackList[i].title == track.title)
                return i
        }
        return -1
    }

    /**
     * Checks if the given track is already in the list of songs
     * @param track track to check
     * *
     * @return `true`, if the song is known. `false` otherwise
     */
    operator fun contains(track: HistoricTrack): Boolean {
        return isAlreadyIn(track) != -1
    }

    /**
     * removes song at position i

     * @param i position of song to delete
     */
    fun removeAt(i: Int) {
        changes = true
        trackList.removeAt(i)
    }

    /**
     * removes every song of the list
     */
    fun clear() {
        while (!trackList.isEmpty()) {
            changes = true
            trackList.remove()
        }
    }

    /**
     * getter for i-th song of list of songs

     * @param i index of requested song
     * *
     * @return i-th song of list of songs
     */
    operator fun get(i: Int): HistoricTrack {
        return trackList[i]
    }

    /**
     * Returns a copy of the historic song list.
     */
    val all: List<HistoricTrack>
        get() {
            val historicTracks = LinkedList(trackList)
            Collections.sort(historicTracks) { (_, _, _, lhs), (_, _, _, rhs) -> (-(lhs - rhs)).toInt() }
            return historicTracks
        }

    /**
     * @return true if there were changes on list of songs since they have been read - false otherwise
     */
    fun somethingChanged(): Boolean {
        return changes
    }

    /**
     * @return returns the size of list of songs
     */
    fun size(): Int {
        return trackList.size
    }

    /**
     * reloads song list
     */
    fun reload() {
        readSongsFromStorage()
    }

    companion object {

        val JSON_ARRAY_SONGS = "songs"
        val JSON_STRING_INTERPRET = "interpret"
        val JSON_STRING_TITLE = "title"
        val JSON_STRING_THUMB = "thumb"
        val JSON_LONG_DATE = "date"
    }
}