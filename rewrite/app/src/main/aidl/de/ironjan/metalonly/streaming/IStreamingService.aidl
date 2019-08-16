package de.ironjan.metalonly.streaming;

import de.ironjan.metalonly.streaming.IStreamChangeCallback;
import de.ironjan.metalonly.streaming.State;

interface IStreamingService {
    State getState();
    boolean getIsPlayingOrPreparing();
    boolean getCanPlay();
    String getLastError();

    void play(in IStreamChangeCallback cb);

    void addCallback(in IStreamChangeCallback cb);

    void stop();

}
