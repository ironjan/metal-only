package com.codingspezis.android.metalonly.player.stream;

import android.os.Process;
import android.util.*;

import com.codingspezis.android.metalonly.player.stream.exceptions.*;
import com.spoledge.aacdecoder.*;

import java.io.*;

/**
 * adds isPlaying functionality to MultiPlayer sets priority higher
 * 
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
	 * @param cb
	 *            the callback, can be null
	 * @param streamPlayerOpencore
	 *            TODO
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
		new Thread(new Runnable() {
            @Override
			@SuppressWarnings("synthetic-access")
			public void run() {
            	Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO);
				OpencorePlayer.this.streamPlayerOpencore.wakeLock.acquire();
				OpencorePlayer.this.streamPlayerOpencore.wifiLock.acquire();
				int samplerateRestarts = 0;
				while (OpencorePlayer.this.streamPlayerOpencore.shouldPlay) {
	                try {
	                    play( url, expectedKBitSecRate );
	                }
	                catch (WrongSampleRateException wsbe){
	                	Log.e(LOG, "playAsync(): "+wsbe.getMessage());
	                	if(samplerateRestarts >= 10){ // ^= 5 sec 
	                		if (playerCallback != null) playerCallback.playerException(wsbe);
	                		break;
	                	}
	                	Log.e(LOG, "playAsync(): restarting playback #"+(++samplerateRestarts));
	                	try {
							Thread.sleep(500);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
	                }catch (Exception e) {
	                	Log.e(LOG, "playAsync():", e);
	                    if (playerCallback != null) playerCallback.playerException( e );
	                    break;
	                }
				}
                OpencorePlayer.this.streamPlayerOpencore.wakeLock.release();
				OpencorePlayer.this.streamPlayerOpencore.wifiLock.release();
            }
        }).start();
	}
	
	// needed in the following method
	public static final int NECESSARY_SAMPLE_RATE = 44100;
	public static final int NECESSARY_CHANNELS = 2;
	
	/**
	 * this override is needed to ensure that sample rate & number of channels are right
	 * 
     * defective:
     * 07-30 11:57:14.148: D/PCMFeed(6410): run(): sampleRate=48000, channels=1, bufferSizeInBytes=144000 (1500 ms)
     * 
     * correctly:
     * 07-30 11:57:10.847: D/PCMFeed(6410): run(): sampleRate=44100, channels=2, bufferSizeInBytes=264600 (1500 ms)
     * 
	 */
	@Override
    protected void playImpl(InputStream is, int expectedKBitSecRate ) throws Exception {
        BufferReader reader = new BufferReader(
                                        computeInputBufferSize( expectedKBitSecRate, decodeBufferCapacityMs ),
                                        is );
        new Thread( reader ).start();

        PCMFeed pcmfeed = null;
        Thread pcmfeedThread = null;

        // profiling info
        long profMs = 0;
        long profSamples = 0;
        long profSampleRate = 0;
        int profCount = 0;

        try {
            Decoder.Info info = decoder.start( reader );

            // *** start of modification ***
            int sr = info.getSampleRate();
            int nc = info.getChannels();
            
            Log.d( LOG, "play(): samplerate=" + sr + ", channels=" + nc);
            
            if(sr != NECESSARY_SAMPLE_RATE || nc != NECESSARY_CHANNELS){
            	throw new WrongSampleRateException(sr);
            }
            // *** end of modification ***

            Log.d( LOG, "play(): samplerate=" + info.getSampleRate() + ", channels=" + info.getChannels());

            profSampleRate = info.getSampleRate() * info.getChannels();

            if (info.getChannels() > 2) {
                throw new RuntimeException("Too many channels detected: " + info.getChannels());
            }

            // 3 buffers for result samples:
            //   - one is used by decoder
            //   - one is used by the PCMFeeder
            //   - one is enqueued / passed to PCMFeeder - non-blocking op
            short[][] decodeBuffers = createDecodeBuffers( 3, info );
            short[] decodeBuffer = decodeBuffers[0];
            int decodeBufferIndex = 0;

            pcmfeed = createPCMFeed( info );
            pcmfeedThread = new Thread( pcmfeed );
            pcmfeedThread.start();

            if (info.getFirstSamples() != null) {
                short[] firstSamples = info.getFirstSamples();
                Log.d( LOG, "First samples length: " + firstSamples.length );

                pcmfeed.feed( firstSamples, firstSamples.length );
                info.setFirstSamples( null );
            }

            do {
                long tsStart = System.currentTimeMillis();

                info = decoder.decode( decodeBuffer, decodeBuffer.length );
                int nsamp = info.getRoundSamples();

                profMs += System.currentTimeMillis() - tsStart;
                profSamples += nsamp;
                profCount++;

                Log.d( LOG, "play(): decoded " + nsamp + " samples" );

                if (nsamp == 0 || stopped) break;
                if (!pcmfeed.feed( decodeBuffer, nsamp ) || stopped) break;

                int kBitSecRate = computeAvgKBitSecRate( info );
                if (Math.abs(expectedKBitSecRate - kBitSecRate) > 1) {
                    Log.i( LOG, "play(): changing kBitSecRate: " + expectedKBitSecRate + " -> " + kBitSecRate );
                    reader.setCapacity( computeInputBufferSize( kBitSecRate, decodeBufferCapacityMs ));
                    expectedKBitSecRate = kBitSecRate;
                }

                decodeBuffer = decodeBuffers[ ++decodeBufferIndex % 3 ];
            } while (!stopped);
        }
        finally {
            boolean stopImmediatelly = stopped;
            stopped = true;

            if (pcmfeed != null) pcmfeed.stop( !stopImmediatelly );
            decoder.stop();
            reader.stop();

            int perf = 0;

            if (profCount > 0) Log.i( LOG, "play(): average decoding time: " + profMs / profCount + " ms");

            if (profMs > 0) {
                perf = (int)((1000*profSamples / profMs - profSampleRate) * 100 / profSampleRate);

                Log.i( LOG, "play(): average rate (samples/sec): audio=" + profSampleRate
                    + ", decoding=" + (1000*profSamples / profMs)
                    + ", audio/decoding= " + perf
                    + " %  (the higher, the better; negative means that decoding is slower than needed by audio)");
            }

            if (pcmfeedThread != null) pcmfeedThread.join();

            if (playerCallback != null) playerCallback.playerStopped( perf );
        }

    }
}
