package com.company.EN;

import java.util.ArrayList;
import java.util.List;

public class AbstractTimeEnricher{
    static final int TIME_MASK = 32768;
    static final int TIME_COUNTER_MASK = 4095;
    static final int TIME_COUNTER_INC_PER_MS = 10;
    static final int TIME_SEQUENCE_OPEN_CLOSE_MASK = 131073;
    static final int TIME_SEQUENCE_LENGTH = 17;
    static final int SIGNIFICANT_MARKER_SHIFT = 4;
    private int timeCounter = 43;
    private int counterDelta;
    private boolean digitization = false;
    private int k = 0;
    private double t;
    final List<Integer> ints = new ArrayList();

    protected boolean[] getTimeSequence(int time) {
        time = time << 1 | 131073;
        boolean[] result = new boolean[17];
        int bitMask = 1;

        for(int i = 0; i < result.length; ++i) {
            result[i] = (time & bitMask) != 0;
            bitMask <<= 1;
        }

        return result;
    }

    public int[] enrich(Frame frame) {
        int[] data = frame.getWordsList();
        double firstTime = frame.timeStartFrame;
        this.ints.clear();

        for(int i = 0; i < data.length; ++i) {
            ++this.k;
            double currentTime = this.digitization ? this.t + (double)this.k : firstTime + (double)i;
            double predTime = this.digitization ? this.t + (double)(this.k - 1) : firstTime + (double)(i - 1);
            boolean msCross = Math.floor(predTime * 1000.0D) != Math.floor(currentTime * 1000.0D);
            boolean halfMsCross = Math.floor(predTime * 10000.0D) != Math.floor(currentTime * 10000.0D) && Math.round(Math.floor(currentTime * 10000.0D)) % 10L >= 5L && Math.round(Math.floor(predTime * 10000.0D)) % 10L < 5L;
            if (!msCross && !halfMsCross) {
                this.ints.add(data[i]);
            } else {
                int ms;
                if (msCross) {
                    this.timeCounter += this.counterDelta;
                    this.counterDelta = 10;
                    ms = '耀' | this.timeCounter & 4095;
                    this.ints.add(ms);
                    this.ints.add(data[i]);
                } else if (halfMsCross) {
                    ms = (int)((currentTime - Math.floor(currentTime)) * 1000.0D);
                    if (ms < 17) {
                        if (ms == 0 && !this.digitization) {
                            this.t = firstTime;
                            this.k = i;
                        }

                        this.digitization = true;
                        int second = (int)(firstTime + (double)i);
                        boolean[] timeSequence = this.getTimeSequence(second);
                        if (timeSequence[ms]) {
                            this.counterDelta = 1;
                            this.timeCounter += this.counterDelta;
                            this.counterDelta = 9;
                            int timeMessage = '耀' | this.timeCounter & 4095;
                            this.ints.add(timeMessage);
                        }
                    } else {
                        this.digitization = false;
                        this.k = 0;
                    }

                    this.ints.add(data[i]);
                }
            }
        }

        int[] result = new int[this.ints.size()];

        for(int i = 0; i < this.ints.size(); ++i) {
            result[i] = (Integer)this.ints.get(i);
        }

        return result;
    }
}
