package com.codingspezis.android.metalonly.player.views;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.codingspezis.android.metalonly.player.R;
import com.codingspezis.android.metalonly.player.stream.metadata.Metadata;
import com.codingspezis.android.metalonly.player.utils.jsonapi.Stats;

import org.androidannotations.annotations.EViewGroup;
import org.androidannotations.annotations.UiThread;
import org.androidannotations.annotations.ViewById;

@EViewGroup(R.layout.view_showinformation)
public class ShowInformation extends LinearLayout {


    @ViewById(R.id.marqueeMod)
    Marquee marqueeMod;
    @ViewById(R.id.marqueeGenree)
    Marquee marqueeGenre;
    @ViewById(android.R.id.progress)
    View progress;
    private Stats stats;

    public ShowInformation(Context context, AttributeSet attrs) {
        super(context, attrs);
        setOrientation(VERTICAL);
    }

    public void setMetadata(Metadata metadata) {
        if (metadata.toSong().isValid()) {
            updateViews(metadata.getGenre(), metadata.getModerator());
        }

    }

    @UiThread
    void updateViews(String genre, String moderator) {
        if (marqueeGenre != null) marqueeGenre.setText(genre);
        if (marqueeGenre != null) marqueeGenre.setVisibility(VISIBLE);
        if (marqueeMod != null) marqueeMod.setText(moderator);
        if (marqueeMod != null) marqueeMod.setVisibility(VISIBLE);
        if (progress != null) progress.setVisibility(GONE);
    }

    public void setStats(Stats stats) {
        updateViews(stats.getGenre(), stats.getModerator());
    }
}
