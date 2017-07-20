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

    private val imageLoader: ImageLoader = ImageLoader(context.applicationContext)

    override fun bind(t: HistoricTrack) {
        txtArtist?.text = t.artist
        txtTitle?.text = t.title

        // FIXME convert t.playedAtAsLong to readable things and set txtDate, txtTime

        if (modImage != null) {
            imageLoader.loadImage(t.moderator, modImage!!)
        }
    }
}
