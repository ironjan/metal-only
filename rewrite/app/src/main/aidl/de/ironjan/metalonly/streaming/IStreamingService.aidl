package de.ironjan.metalonly.streaming;

import de.ironjan.metalonly.streaming.IStreamChangeCallback;
import de.ironjan.metalonly.streaming.State;

interface IStreamingService {
    void play(in IStreamChangeCallback cb);
    void stop();

    State getState();
    boolean getIsPlayingOrPreparing();
    boolean getCanPlay();
    String getLastError();

    void addCallback(in IStreamChangeCallback cb);
}
