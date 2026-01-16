package fr.asdepack.common.radio;

public class HighPassAudioEffect {
    private float lastInput = 0;
    private float lastOutput = 0;
    private final float alpha;

    /**
     * @param cutoffFrequency Hz frequency
     * @param sampleRate      Sample rate
     */
    public HighPassAudioEffect(float cutoffFrequency, float sampleRate) {
        float dt = 1.0f / sampleRate;
        float rc = 1.0f / (2.0f * (float) Math.PI * cutoffFrequency);
        this.alpha = rc / (rc + dt);
    }

    public short[] apply(short[] input) {
        short[] output = new short[input.length];

        for (int i = 0; i < input.length; i++) {
            float in = input[i];

            float out = alpha * (lastOutput + in - lastInput);

            lastInput = in;
            lastOutput = out;

            if (out > 32767) out = 32767;
            else if (out < -32768) out = -32768;

            output[i] = (short) out;
        }

        return output;
    }
}