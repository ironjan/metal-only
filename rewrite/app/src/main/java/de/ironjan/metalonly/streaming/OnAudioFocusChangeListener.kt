package de.ironjan.metalonly.streaming

import android.media.AudioManager
import de.ironjan.metalonly.log.LW

class OnAudioFocusChangeListener(
    private val service: MoStreamingService,
    private val moStreamingService: MoStreamingService
) : AudioManager.OnAudioFocusChangeListener {

    private var continueOnAudioFocusReceived: Boolean = false

    override fun onAudioFocusChange(focusChange: Int) {
        LW.d(TAG, "Received audio focus change to $focusChange")

        when (focusChange) {
            AudioManager.AUDIOFOCUS_LOSS -> {
                // Permanent loss of audio focus
                // Stop playback immediately
                continueOnAudioFocusReceived = false
                service.stop()
                LW.d(TAG, "Stopped playback, no continue on gain")
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                // Pause playback
                // we rely on https://developer.android.com/guide/topics/media-apps/audio-focus#automatic-ducking
                // until required otherwise
                if (moStreamingService.state == State.Started) {
                    service.pause()
                    continueOnAudioFocusReceived = true
                    LW.d(
                        TAG,
                        "transient loss. Paused playback, continue on gain"
                    )
                }
                if (moStreamingService.state == State.Preparing) {
                    // FIXME how to handle preparing?
                }
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                // Lower the volume, keep playing
                // we rely on https://developer.android.com/guide/topics/media-apps/audio-focus#automatic-ducking
                // until required otherwise

                // todo: implement Lower the volume, keep playing?
                LW.d(TAG, "transient loss can duck. did nothing")
            }
            AudioManager.AUDIOFOCUS_GAIN -> {
                // Your app has been granted audio focus again
                // Raise volume to normal, restart playback if necessary
                LW.d(
                    TAG,
                    "gained focus, continueOnAudioFocusReceived: $continueOnAudioFocusReceived"
                )

                if (moStreamingService.state == State.Started && continueOnAudioFocusReceived) {
                    moStreamingService.continuePlayback()
                    LW.d(TAG, "... started playback again")
                } else {
                    LW.d(
                        TAG,
                        "... state was ${moStreamingService.state} and continueOnAudioFocusReceived was $continueOnAudioFocusReceived. Playback not resumed."
                    )
                }
            }
        }
    }

    companion object {
        private const val TAG = "OnAudioFocusChangeListener"

    }
}