package com.konovalov.vad;

import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;

import java.util.LinkedHashMap;
import java.util.LinkedList;

/**
 * Created by George Konovalov on 11/16/2019.
 */

public class Vad {

    private VadConfig config;

    private boolean needResetDetectedSamples = true;
    private long detectedVoiceSamplesMillis = 0;
    private long detectedSilenceSamplesMillis = 0;
    private long previousTimeMillis = System.currentTimeMillis();
    private HandlerThread handlerThread = new HandlerThread("vad-thread");
    private Handler detectHandler = null;
    private VadListener listener = null;
    private Handler.Callback detectCallback = new Handler.Callback() {

        @Override
        public boolean handleMessage(Message msg) {
            short[] data = (short[]) msg.obj;
            extracted(data);
            return true;
        }
    };

    /**
     * Valid Sample Rates and corresponding Frame Sizes
     */
    public static final LinkedHashMap<VadConfig.SampleRate, LinkedList<VadConfig.FrameSize>> SAMPLE_RATE_VALID_FRAMES = new LinkedHashMap<VadConfig.SampleRate, LinkedList<VadConfig.FrameSize>>() {{
        put(VadConfig.SampleRate.SAMPLE_RATE_8K, new LinkedList<VadConfig.FrameSize>() {{
            add(VadConfig.FrameSize.FRAME_SIZE_80);
            add(VadConfig.FrameSize.FRAME_SIZE_160);
            add(VadConfig.FrameSize.FRAME_SIZE_240);
        }});
        put(VadConfig.SampleRate.SAMPLE_RATE_16K, new LinkedList<VadConfig.FrameSize>() {{
            add(VadConfig.FrameSize.FRAME_SIZE_160);
            add(VadConfig.FrameSize.FRAME_SIZE_320);
            add(VadConfig.FrameSize.FRAME_SIZE_480);
        }});
        put(VadConfig.SampleRate.SAMPLE_RATE_32K, new LinkedList<VadConfig.FrameSize>() {{
            add(VadConfig.FrameSize.FRAME_SIZE_320);
            add(VadConfig.FrameSize.FRAME_SIZE_640);
            add(VadConfig.FrameSize.FRAME_SIZE_960);
        }});
        put(VadConfig.SampleRate.SAMPLE_RATE_48K, new LinkedList<VadConfig.FrameSize>() {{
            add(VadConfig.FrameSize.FRAME_SIZE_480);
            add(VadConfig.FrameSize.FRAME_SIZE_960);
            add(VadConfig.FrameSize.FRAME_SIZE_1440);
        }});
    }};

    public Vad() {
        this(null);
    }

    /**
     * VAD constructor
     *
     * @param config contains such parameters as Sample Rate {@link VadConfig.SampleRate}, Frame Size {@link VadConfig.FrameSize}, Mode {@link VadConfig.Mode}, etc.
     */
    public Vad(VadConfig config) {
        this.config = config;
    }

    /**
     * Start VAD should be called before {@link #isSpeech(short[] audio)}
     */
    public void start() {
        if (config == null) {
            throw new NullPointerException("VadConfig is NULL!");
        }

        if (!isSampleRateAndFrameSizeValid()) {
            throw new UnsupportedOperationException("VAD doesn't support this SampleRate and FrameSize!");
        }
        handlerThread.start();
        if (detectHandler == null) {
            detectHandler = new Handler(handlerThread.getLooper(), detectCallback);
        }
        try {
            int result = nativeStart(config.getSampleRate().getValue(), config.getFrameSize().getValue(), config.getMode().getValue());

            if (result < 0) {
                throw new RuntimeException("Error can't set parameters for VAD!");
            }
        } catch (Exception e) {
            throw new RuntimeException("Error can't start VAD!", e);
        }
    }

    public void setVadListener(VadListener listener) {
        this.listener = listener;
    }

    /**
     * Stop VAD - should be called after {@link #start()}
     */
    public void stop() {
        try {
            nativeStop();
            handlerThread.quit();
        } catch (Exception e) {
            throw new RuntimeException("Error can't stop VAD!", e);
        }
    }

    /**
     * Speech detector was designed to detect speech/noise in small audio
     * frames and return result for every frame. This method will not work for
     * long utterances.
     *
     * @param audio input audio frame
     * @return boolean containing result of speech detection
     */
    public boolean isSpeech(short[] audio) {
        if (audio == null) {
            throw new NullPointerException("Audio data is NULL!");
        }

        try {
            return nativeIsSpeech(audio);
        } catch (Exception e) {
            throw new RuntimeException("Error during VAD speech detection!", e);
        }
    }

    /**
     * Continuous Speech listener was designed to detect long utterances
     * without returning false positive results when user makes pauses between
     * sentences.
     *
     * @param audio input audio frame
     *
     */
    @Deprecated
    public void isContinuousSpeech(short[] audio) {
        startSpeachDetect(audio);
    }

    /**
     * Continuous Speech listener was designed to detect long utterances
     * without returning false positive results when user makes pauses between
     * sentences.
     *
     * @param audio input audio frame
     */
    public void startSpeachDetect(short[] audio) {
        if (config == null) {
            throw new NullPointerException("VadConfig is NULL!");
        }

        if (audio == null) {
            throw new NullPointerException("Audio data is NULL!");
        }

        if (listener == null) {
            throw new NullPointerException("VadListener is NULL!");
        }

        Message obtain = Message.obtain();
        obtain.obj = audio;
        detectHandler.sendMessage(obtain);
    }

    private void extracted(short[] audio) {
        long currentTimeMillis = System.currentTimeMillis();

        if (isSpeech(audio)) {
            detectedVoiceSamplesMillis += currentTimeMillis - previousTimeMillis;
            needResetDetectedSamples = true;
            if (detectedVoiceSamplesMillis > config.getVoiceDurationMillis()) {
                previousTimeMillis = currentTimeMillis;
                if (listener != null)
                    listener.onSpeechDetected();
            }
        } else {
            if (needResetDetectedSamples) {
                needResetDetectedSamples = false;
                detectedSilenceSamplesMillis = 0;
                detectedVoiceSamplesMillis = 0;
            }
            detectedSilenceSamplesMillis += currentTimeMillis - previousTimeMillis;
            if (detectedSilenceSamplesMillis > config.getSilenceDurationMillis()) {
                previousTimeMillis = currentTimeMillis;
                if (listener != null)
                    listener.onNoiseDetected();
            }
        }
        previousTimeMillis = currentTimeMillis;
    }

    /**
     * Get current VAD config
     *
     * @return config {@link VadConfig} of VAD
     */
    public VadConfig getConfig() {
        return config;
    }

    /**
     * Set {@link VadConfig} for VAD
     *
     * @param config VAD config
     */
    public void setConfig(VadConfig config) {
        this.config = config;
    }

    /**
     * Check Sample Rate and corresponding Frame Size inside of config
     *
     * @return boolean - contains true if Sample Rate and Frame Size inside of config is valid
     */
    private boolean isSampleRateAndFrameSizeValid() {
        if (config == null) {
            throw new NullPointerException("VadConfig is NULL!");
        }

        LinkedList<VadConfig.FrameSize> supportingFrameSizes = getValidFrameSize(config.getSampleRate());

        if (supportingFrameSizes != null) {
            return supportingFrameSizes.contains(config.getFrameSize());
        } else {
            return false;
        }
    }

    /**
     * Method return valid Frame sizes for specific Sample Rate
     *
     * @param sampleRate contains sample rate
     * @return LinkedList with valid Frame sizes
     */
    public static LinkedList<VadConfig.FrameSize> getValidFrameSize(VadConfig.SampleRate sampleRate) {
        if (sampleRate == null) {
            throw new NullPointerException("SampleRate is NULL!");
        }

        return SAMPLE_RATE_VALID_FRAMES.get(sampleRate);
    }

    private native int nativeStart(int sampleRate, int frameSize, int mode);

    private native boolean nativeIsSpeech(short[] audio);

    private native void nativeStop();

    static {
        System.loadLibrary("vad_jni");
    }
}
