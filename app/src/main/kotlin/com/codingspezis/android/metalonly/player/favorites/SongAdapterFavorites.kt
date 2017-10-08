package com.codingspezis.android.metalonly.player.favorites

import android.app.Activity
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.codingspezis.android.metalonly.player.core.Track

/**
 * adapter for displaying a 2 row list view
 */
class SongAdapterFavorites(private val activity: Activity, private val data: ArrayList<Track>) : BaseAdapter() {
    private val inflater: LayoutInflater

    init {
        inflater = activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view: View
        if (convertView == null) {
            view = inflater.inflate(R.layout.view_list_item_song_fav, null)
        } else {
            view = convertView
        }
        val track = data[position]

        val txtTitle = view.findViewById<TextView>(R.id.txtTitle)
        val txtArtist = view.findViewById<TextView>(R.id.txtArtist)

        txtTitle.text = track.title
        txtArtist.text = track.artist

        return view
    }

    override fun getCount(): Int {
        return data.size
    }

    override fun getItem(arg0: Int): Any {
        return arg0
    }

    override fun getItemId(arg0: Int): Long {
        return arg0.toLong()
    }

    fun replaceData(trackList: ArrayList<Track>) {
        data.clear()
        data.addAll(trackList)
        notifyDataSetChanged()
    }

    companion object {

        // hash keys
        val KEY_TITLE = "MO_HK_TITLE"
        val KEY_INTERPRET = "MO_HK_INTERPRET"
        val KEY_THUMB = "MO_HK_THUMB"
        val KEY_DATE = "MO_HK_DATE"
    }
}
