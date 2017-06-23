package com.codingspezis.android.metalonly.player.utils.jsonapi;

import com.codingspezis.android.metalonly.player.core.Track;
import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

/**
 * Wrapper around {@link SimpleTrack} because the API also has a wrapper object.
 * {"track":{"artist":"ID 2A","title":"Rotkaeppchen"}}
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonAutoDetect
public class TrackWrapper {
    private Track track;

    public TrackWrapper() {
    }

    public Track getTrack() {
        return track;
    }

    public void setTrack(SimpleTrack track) {
        this.track = track;
    }
}
