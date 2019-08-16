package de.ironjan.metalonly.streaming

interface StateChangeCallback {
    fun onStateChange(newState: State)
}