package com.codingspezis.android.metalonly.player.stream;

import android.util.*;

import com.codingspezis.android.metalonly.player.stream.exceptions.*;
import com.spoledge.aacdecoder.*;

import java.io.*;

/**
 * adds isPlaying functionality to MultiPlayer sets priority higher
 */
class OpencorePlayer extends MultiPlayer {

    private static final String LOG = "OpencorePlayer";

    /**
     *
     */
    final StreamPlayerOpencore streamPlayerOpencore;

    /**
     * same as super(cb)
     *
     * @param cb                   the callback, can be null
     * @param streamPlayerOpencore TODO
     */
    public OpencorePlayer(StreamPlayerOpencore streamPlayerOpencore,
                          PlayerCallback cb) {
        super(cb);
        this.streamPlayerOpencore = streamPlayerOpencore;
    }

    /**
     * is AACPlayer playing?
     *
     * @return true if playing loop is still working - false otherwise
     */
    public boolean isPlaying() {
        return !stopped;
    }

    /**
     * this override is needed to set priority of the playback thread to THREAD_PRIORITY_AUDIO
     * and to set wakelock & wifiLock at a lower level
     */
    @Override
    public void playAsync(final String url, final int expectedKBitSecRate) {
        new Thread(new OpenCorePlayRunnable(this, url, expectedKBitSecRate)).start();
    }

    // needed in the following method
    public static final int NECESSARY_SAMPLE_RATE = 44100;
    public static final int NECESSARY_CHANNELS = 2;

    /**
     * this override is needed to ensure that sample rate & number of channels are right
     * <p/>
     * defective:
     * 07-30 11:57:14.148: D/PCMFeed(6410): run(): sampleRate=48000, channels=1, bufferSizeInBytes=144000 (1500 ms)
     * <p/>
     * correctly:
     * 07-30 11:57:10.847: D/PCMFeed(6410): run(): sampleRate=44100, channels=2, bufferSizeInBytes=264600 (1500 ms)
     */
    @Override
    protected void playImpl(InputStream is, int expectedKBitSecRate) throws Exception {
        // TODO refactor this method

        BufferReader reader = new BufferReader(
                computeInputBufferSize(expectedKBitSecRate, decodeBufferCapacityMs),
                is);
        new Thread(reader).start();

        PCMFeed pcmfeed = null;
        Thread pcmfeedThread = null;

        final OpencorePlayerProfilingInfo profilingInfo = new OpencorePlayerProfilingInfo();

        try {
            Decoder.Info info = decoder.start(reader);

            checkSampleRate(info);
            checkNumberOfChannels(info);

            final int sampleRate = info.getSampleRate();
            final int channels = info.getChannels();
            Log.d(LOG, "play(): samplerate=" + sampleRate + ", channels=" + channels);
            profilingInfo.profSampleRate = sampleRate * channels;


            // 3 buffers for result samples:
            //   - one is used by decoder
            //   - one is used by the PCMFeeder
            //   - one is enqueued / passed to PCMFeeder - non-blocking op
            short[][] decodeBuffers = createDecodeBuffers(3, info);
            short[] decodeBuffer = decodeBuffers[0];
            int decodeBufferIndex = 0;

            pcmfeed = createPCMFeed(info);
            pcmfeedThread = new Thread(pcmfeed);
            pcmfeedThread.start();

            if (info.getFirstSamples() != null) {
                short[] firstSamples = info.getFirstSamples();
                Log.d(LOG, "First samples length: " + firstSamples.length);

                pcmfeed.feed(firstSamples, firstSamples.length);
                info.setFirstSamples(null);
            }

            do {
                long tsStart = System.currentTimeMillis();

                info = decoder.decode(decodeBuffer, decodeBuffer.length);
                int nsamp = info.getRoundSamples();

                profilingInfo.profMs += System.currentTimeMillis() - tsStart;
                profilingInfo.profSamples += nsamp;
                profilingInfo.profCount++;

                Log.d(LOG, "play(): decoded " + nsamp + " samples");

                boolean shouldBreak = stopped
                        || (nsamp == 0)
                        || !pcmfeed.feed(decodeBuffer, nsamp);
                if (shouldBreak) break;

                int kBitSecRate = computeAvgKBitSecRate(info);
                if (Math.abs(expectedKBitSecRate - kBitSecRate) > 1) {
                    Log.i(LOG, "play(): changing kBitSecRate: " + expectedKBitSecRate + " -> " + kBitSecRate);
                    reader.setCapacity(computeInputBufferSize(kBitSecRate, decodeBufferCapacityMs));
                    expectedKBitSecRate = kBitSecRate;
                }

                decodeBuffer = decodeBuffers[++decodeBufferIndex % 3];
            } while (!stopped);
        } catch (PlayerException e) {
            // TODO handle exception
            e.printStackTrace();
        } finally {
            boolean stopImmediatelly = stopped;
            stopped = true;

            if (pcmfeed != null) pcmfeed.stop(!stopImmediatelly);
            decoder.stop();
            reader.stop();

            logProfilingInfo(profilingInfo);

            if (pcmfeedThread != null) pcmfeedThread.join();

            getPlayerCallback().playerStopped((int) profilingInfo.getOverallPerformance());
        }

    }

    @Override
    public PlayerCallback getPlayerCallback() {
        if (super.playerCallback != null) {
            return super.playerCallback;
        }
        return new DummyPlayerCallback();
    }

    private void logProfilingInfo(OpencorePlayerProfilingInfo profilingInfo) {
        Log.i(LOG, "play(): average decoding time: " + profilingInfo.getAverageDecodingTime() + " ms");

        Log.i(LOG, "play(): average rate (samples/sec): audio=" + profilingInfo.profSampleRate
                + ", decoding=" + profilingInfo.getDecodingPerformance()
                + ", audio/decoding= " + (int) profilingInfo.getOverallPerformance()
                + " %  (the higher, the better; negative means that decoding is slower than needed by audio)");
    }

    private void checkNumberOfChannels(Decoder.Info info) throws WrongChannelCountException {
        final int channels = info.getChannels();
        if (channels > 2) {
            throw new WrongChannelCountException("Too many channels detected: " + channels);
        }
    }

    private void checkSampleRate(Decoder.Info info) throws WrongSampleRateException {
        int sampleRate = info.getSampleRate();
        int numberOfChannels = info.getChannels();

        Log.d(LOG, "play(): samplerate=" + sampleRate + ", channels=" + numberOfChannels);

        if (sampleRate != NECESSARY_SAMPLE_RATE || numberOfChannels != NECESSARY_CHANNELS) {
            throw new WrongSampleRateException(sampleRate);
        }
    }

}
