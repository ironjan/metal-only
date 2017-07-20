package com.codingspezis.android.metalonly.player.views

import android.content.Context
import android.util.AttributeSet
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.codingspezis.android.metalonly.player.R
import com.codingspezis.android.metalonly.player.core.HistoricTrack
import com.codingspezis.android.metalonly.player.plan.views.CustomDataView
import com.codingspezis.android.metalonly.player.utils.ImageLoader
import org.androidannotations.annotations.EViewGroup
import org.androidannotations.annotations.ViewById
import java.text.DateFormat
import java.util.Date
import java.util.Locale

/**
 * Custom view to display [HistoricTrack]s in the song history
 */
@EViewGroup(R.layout.view_list_item_song_hist)
open class SongHistoryView(context: Context, attrs: AttributeSet?) : RelativeLayout(context, attrs), CustomDataView<HistoricTrack> {

    @JvmField
    @ViewById
    protected var modImage: ImageView? = null

    @JvmField
    @ViewById
    protected var txtDate: TextView? = null

    @JvmField
    @ViewById
    protected var txtTime: TextView? = null

    @JvmField
    @ViewById
    protected var txtArtist: TextView? = null

    @JvmField
    @ViewById
    protected var txtTitle: TextView? = null

    var moderator: String = ""

    private val imageLoader: ImageLoader = ImageLoader(context.applicationContext)

    override fun bind(t: HistoricTrack) {
        txtArtist?.text = t.artist
        txtTitle?.text = t.title

        setDateAndTime(t)

        if(moderator != t.moderator){
            moderator = t.moderator
            if(modImage != null){
                imageLoader.loadImage(moderator, modImage!!)
            }
        }
    }

    private fun setDateAndTime(t: HistoricTrack) {
        try {
            val dateAsDate = Date(t.playedAtAsLong)
            val day = DateFormat.getDateInstance(DateFormat.SHORT, Locale.GERMAN).format(dateAsDate)
            val time = DateFormat.getTimeInstance(DateFormat.SHORT, Locale.GERMAN).format(dateAsDate)

            txtDate?.text = day
            txtTime?.text = time
        } catch (e: Exception) {
            txtDate?.text = ""
            txtTime?.text = ""
        }
    }
}
