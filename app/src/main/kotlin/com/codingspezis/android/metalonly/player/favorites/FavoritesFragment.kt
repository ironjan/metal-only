package com.codingspezis.android.metalonly.player.favorites

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface.OnClickListener
import android.content.Intent
import android.net.Uri
import android.support.v4.app.Fragment
import android.view.View
import android.widget.EditText
import android.widget.ListView
import com.codingspezis.android.metalonly.player.BuildConfig
import com.codingspezis.android.metalonly.player.R
import com.codingspezis.android.metalonly.player.WishActivity_
import com.codingspezis.android.metalonly.player.core.HistoricTrack
import com.codingspezis.android.metalonly.player.core.Track
import org.androidannotations.annotations.*
import org.slf4j.LoggerFactory
import java.net.URLEncoder
import java.util.*

/**
 * Displays favorites and allows to handle them
 */
@EFragment(R.layout.fragment_favorites)
@OptionsMenu(R.menu.favoritesmenu)
@SuppressLint("Registered")
open class FavoritesFragment : Fragment() {
    @JvmField
    @ViewById
    internal var list: ListView? = null

    @JvmField
    @ViewById
    internal var empty: View? = null

    private var adapter: SongAdapterFavorites? = null

    private var favoritesSaver: SongSaver? = null

    @AfterViews
    internal fun bindContent() {
        favoritesSaver = SongSaver(activity, JSON_FILE_FAV, -1)
        adapter = SongAdapterFavorites(activity, ArrayList<Track>(0))
        list!!.adapter = adapter
        list!!.emptyView = empty
        displayFavorites()
    }

    override fun onPause() {
        favoritesSaver!!.saveSongsToStorage()
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        favoritesSaver!!.reload()
        displayFavorites()
    }

    @OptionsItem(R.id.mnu_add_manually)
    internal fun showAddSongDialog() {
        // TODO We should use a custom class for this dialog
        val alert = AlertDialog.Builder(activity)
        alert.setTitle(R.string.menu_add_mannually)

        // FIXME fix this...
        @SuppressLint("RestrictedApi")
        val v = getLayoutInflater(null).inflate(R.layout.dialog_add_song, null)

        alert.setView(v)
        alert.setNegativeButton(R.string.abort, null)
        alert.setPositiveButton(R.string.ok) { dialog, action ->
            val artist = v.findViewById<EditText>(R.id.edit_artist)
            val txtTitle = v.findViewById<EditText>(R.id.edit_title)

            val interpret = artist.text.toString()
            val title = txtTitle.text.toString()

            val track = HistoricTrack(interpret, title, "", Calendar.getInstance().timeInMillis)

            if (track.isValid && favoritesSaver!!.isAlreadyIn(track) < 0) {
                favoritesSaver!!.addSong(track)
                displayFavorites()
            }
        }
        alert.show()
    }

    /**
     * displays favorites on screen
     */
    private fun displayFavorites() {
        list!!.removeAllViewsInLayout()
        val trackList = ArrayList<Track>()
        for (i in favoritesSaver!!.size() - 1 downTo 0) {
            trackList.add(favoritesSaver!!.get(i))
        }
        adapter!!.replaceData(trackList)
    }

    /**
     * handles an action on an index
     * @param index item to handle
     * @param action action to handle
     */
    private fun handleAction(index: Int, action: Int) {
        when (action) {
            ITEM_CLICK_ACTION_WISH -> wishSong(index)
            ITEM_CLICK_ACTION_YOUTUBE -> searchSongOnYoutube(index)
            ITEM_CLICK_ACTION_SHARE -> shareSong(index)
            ITEM_CLICK_ACTION_DELETE -> deleteSong(index)
        }
    }

    @Background
    internal open fun wishSong(index: Int) {
        WishActivity_.intent(activity)
                .interpret(favoritesSaver!!.get(index).artist)
                .title(favoritesSaver!!.get(index).title)
                .start()
    }

    @UiThread
    internal open fun alertMessage(context: Context, msg: String) {
        if (BuildConfig.DEBUG) LOGGER.debug("alertMessage({},{})", context, msg)

        AlertDialog.Builder(context)
                .setMessage(msg)
                .setPositiveButton(context.getString(R.string.ok), null)
                .show()

        if (BuildConfig.DEBUG) LOGGER.debug("alertMessage({},{}) done", context, msg)
    }

    private fun searchSongOnYoutube(index: Int) {
        var searchStr = favoritesSaver!!.get(index).artist + " - "
        favoritesSaver!!.get(index).title
        try {
            searchStr = URLEncoder.encode(searchStr, "UTF-8")
        } catch (e: Exception) {
            e.printStackTrace()
        }

        val url = Uri.parse("http://www.youtube.com/results?search_query=" + searchStr)
        val youtube = Intent(Intent.ACTION_VIEW, url)
        startActivity(youtube)
    }

    private fun shareSong(index: Int) {
        val message = favoritesSaver!!.get(index).artist + " - "
        favoritesSaver!!.get(index).title
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(Intent.createChooser(share,
                resources.getStringArray(R.array.favorite_options_array)[2]))
    }

    private fun deleteSong(index: Int) {
        favoritesSaver!!.removeAt(index)
        displayFavorites()
    }

    @ItemClick(android.R.id.list)
    fun listItemClicked(position: Int) {
        val builder = AlertDialog.Builder(activity)
        builder.setItems(R.array.favorite_options_array) { dialog, action -> handleAction(favoritesSaver!!.size() - position - 1, action) }
        builder.show()
    }

    @OptionsItem(R.id.mnu_deleteall)
    internal fun deleteAllClicked() {
        askSureDelete(activity, OnClickListener { dialog, action ->
            favoritesSaver!!.clear()
            displayFavorites()
        }, null)
    }

    @OptionsItem(R.id.mnu_shareall)
    internal fun shareAllClicked() {
        var message = ""
        for (i in favoritesSaver!!.size() - 1 downTo 0) {
            message += favoritesSaver!!.get(i).artist + " - " + favoritesSaver!!.get(i).title + "\n"
        }
        // open share dialog
        val share = Intent(Intent.ACTION_SEND)
        share.type = "text/plain"
        share.putExtra(Intent.EXTRA_TEXT, message)
        startActivity(Intent.createChooser(share,
                resources.getStringArray(R.array.favorite_options_array)[2]))
    }

    companion object {

        val JSON_FILE_FAV = "mo_fav.json"
        private val ITEM_CLICK_ACTION_DELETE = 3
        private val ITEM_CLICK_ACTION_SHARE = 2
        private val ITEM_CLICK_ACTION_YOUTUBE = 1
        private val ITEM_CLICK_ACTION_WISH = 0
        private val LOGGER = LoggerFactory.getLogger(FavoritesFragment::class.java)

        /**
         * asks if user is sure to delete share
         * @param yes what is to do if user clicks yes
         * @param no what is to do if user clicks no
         */
        fun askSureDelete(context: Context, yes: OnClickListener, no: OnClickListener?) {
            val alert = AlertDialog.Builder(context)
            alert.setMessage(R.string.delete_sure)
            alert.setNegativeButton(R.string.no, no)
            alert.setPositiveButton(R.string.yes, yes)
            alert.show()
        }
    }

}
