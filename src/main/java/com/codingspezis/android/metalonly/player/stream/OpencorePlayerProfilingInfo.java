package com.codingspezis.android.metalonly.player.stream;

class OpencorePlayerProfilingInfo {
    protected long profMs = 0;
    protected long profSamples = 0;
    protected long profSampleRate = 0;
    protected int profCount = 0;

    public double getAverageDecodingTime() {
        if (profCount == 0)
            return Double.POSITIVE_INFINITY;

        return (double) profMs / (double) profCount;
    }

    public double getDecodingPerformance() {
        if (profMs == 0)
            return Double.POSITIVE_INFINITY;

        return 1000 * (double) profSamples / (double) profMs;
    }

    public double getOverallPerformance() {
        final double performanceMeasure = getDecodingPerformance() - profSampleRate;
        if (profSampleRate == 0 && performanceMeasure >= 0) {
            return Double.POSITIVE_INFINITY;
        }
        if (profSampleRate == 0 && performanceMeasure < 0) {
            return Double.NEGATIVE_INFINITY;
        }
        return performanceMeasure * 100 / profSampleRate;
    }
}
