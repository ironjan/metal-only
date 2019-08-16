package de.ironjan.metalonly.streaming;

import de.ironjan.metalonly.streaming.State;

interface IStreamChangeCallback {
    void onNewState(in State state);
}
