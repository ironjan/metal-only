package de.ironjan.metalonly.streaming

/** AIDL based binder */
class StreamingServiceBinder(private val service: MoStreamingService) : IStreamingService.Stub() {
    override fun play(cb: IStreamChangeCallback) = service.play(wrap(cb))
    override fun stop() = service.stop()
    override fun stopWithCallback(cb: IStreamChangeCallback) = service.stopWithCallback(wrap(cb))


    override fun getState(): State = service.state
    override fun getIsPlayingOrPreparing(): Boolean = service.isPlayingOrPreparing
    override fun getCanPlay(): Boolean = service.canPlay
    override fun getLastError(): String? = service.lastError

    override fun addCallback(cb: IStreamChangeCallback?) {
        service.addStateChangeCallback(wrap(cb))
    }

    private fun wrap(cb: IStreamChangeCallback?): StateChangeCallback {
        return object : StateChangeCallback {
            override fun onStateChange(newState: State) {
                cb?.onNewState(newState)
            }
        }
    }
}