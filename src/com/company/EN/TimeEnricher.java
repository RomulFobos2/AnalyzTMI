package com.company.EN;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.floor;

public class TimeEnricher {
    static final int TIME_MASK = 0x8000;
    static final int TIME_COUNTER_MASK = 0x0FFF;
    static final int TIME_COUNTER_INC_PER_MS = 10;
    static final int TIME_SEQUENCE_OPEN_CLOSE_MASK = 0x00020001;
    static final int TIME_SEQUENCE_LENGTH = 17;
    static final int SIGNIFICANT_MARKER_SHIFT = 3; // 2: 3,7; 3: 2,8; 4:1,9
    //-------------------Fields---------------------------------------------------
    /**
     * Счетчик для значения слов времени.
     */
    private int timeCounter = 43;
    private int counterDelta;
    private boolean digitization = false;
    private int k = 0;
    private double t;
    // Буфер накопления сообщений. Может быть вынесен в экземпляр класса, т.к. многопоточность при
    // использовании класса не предусмотрена.
    final List<Integer> ints = new ArrayList<>();


    /**
     * Метод, возвращающий битовую последовательность для оцифровки времени.
     *
     * @param time времы, которое нужно оцифровать.
     * @return битовая последовательность, представленная в виде массива
     * логических значений, где true - 1, а false - 0.
     */
    protected boolean[] getTimeSequence(int time){
        time = (time << 1) | TIME_SEQUENCE_OPEN_CLOSE_MASK;
        boolean[] result = new boolean[TIME_SEQUENCE_LENGTH];
        int bitMask = 1;
        for (int i = 0; i < result.length; i++) {
            result[i] = (time & bitMask) != 0;
            bitMask <<= 1;
        }
        return result;
    }


    /**
     * Выполняет оцифровку блока данных - вставляет слова данных и возвращает
     * массив слов, где между словами данных вставлены слова времени.
     *
   //  * @param block блок ТМИ для оцифровки.
     * @return массив слов данных со словами времени на позициях,
     * соответствующих оцифровке времени блока.
     */
//    public int[] enrich(Frame block) {
//        int[] data = block.getWordsList();
//        ints.clear();
//        int wf = 51200;
//        for (int i = 0; i < data.length; i++) {
//            // оцифровка времени проводится только на row frame
//            k++;
//            currentTime = digitization ? t + k * TEN_THOUSANDS / wf : (int) (block.timeStartFrame/100) + i * TEN_THOUSANDS / wf;
//            // Признак перехода через границу миллисекунды.
//            boolean msCross = predTime / TEN != currentTime / TEN;
//            // Признак перехода через границу в половину миллисекунды.
//            boolean halfMsCross = (predTime != currentTime)
//                    && ((currentTime % 10 >= TIME_COUNTER_INC_PER_MS / 2 - SIGNIFICANT_MARKER_SHIFT)
//                    && (predTime % 10 < TIME_COUNTER_INC_PER_MS / 2 - SIGNIFICANT_MARKER_SHIFT));
//            predTime = currentTime;
//// Если не было перехода через мс или полМс границу, то записать слово данных.
//            if (!msCross && !halfMsCross) {
//                ints.add(data[i]);
//                continue;
//            }
//            if (msCross) {
//                timeCounter += counterDelta;
//                counterDelta = TIME_COUNTER_INC_PER_MS;
//                int timeMessage = TIME_MASK | (timeCounter & TIME_COUNTER_MASK);
//                ints.add(timeMessage);
//                ints.add(data[i]);
//                continue;
//            }
//            if (halfMsCross) {
//                int ms = currentTime % TEN_THOUSANDS / TEN;
//                if (ms < TIME_SEQUENCE_LENGTH) {
//                    if (ms == 0 && !digitization) {
//                        t = (int) (block.timeStartFrame/10);
//                        k = i;
//                    }
//                    digitization = true;
//                    int second = ((int) (block.timeStartFrame/10) + i * TEN_THOUSANDS / wf) / TEN_THOUSANDS;
//                    boolean[] timeSequence = getTimeSequence(second);
//                    if (timeSequence[ms]) {
//                        counterDelta = TIME_COUNTER_INC_PER_MS / 2 - 2;
//                        timeCounter += counterDelta;
//                        counterDelta = TIME_COUNTER_INC_PER_MS / 2 + 2;
//                        int timeMessage = TIME_MASK | (timeCounter & TIME_COUNTER_MASK);
//                        ints.add(timeMessage);
//                    }
//                } else {
//                    digitization = false;
//                    k = 0;
//                }
//                ints.add(data[i]);
//            }
//        }
//        int result[] = new int[ints.size()];
//        for (int i = 0; i < ints.size(); i++) {
//            result[i] = ints.get(i);
//        }
//        return result;
//    }


    public int[] enrich2(Frame frame) {
        int[] data = frame.getWordsList();
        float dt = 1/51200f;
        float firstTime = frame.timeStartFrame/1000f;
        ints.clear();
        for (int i = 0; i < data.length; i++) {
            // оцифровка времени проводится только на row frame
            k++;
            double currentTime = digitization ? t + k * dt : firstTime + i * dt;
            double predTime = digitization ? t + (k - 1) * dt : firstTime + (i - 1) * dt;
            // Признак перехода через границу миллисекунды.
            boolean msCross = floor(predTime * 1000) != floor(currentTime * 1000);
            // Признак перехода через границу в половину миллисекунды.
            boolean halfMsCross = (floor(predTime * 10000) != floor(currentTime * 10000))
                    && (
                    (Math.round(floor(currentTime * 10000)) % 10 >= TIME_COUNTER_INC_PER_MS / 2 - SIGNIFICANT_MARKER_SHIFT)
                            && (Math.round(floor(predTime * 10000)) % 10 < TIME_COUNTER_INC_PER_MS / 2 - SIGNIFICANT_MARKER_SHIFT)
            );
            // Если не было перехода через мс или полМс границу, то записать слово данных.
            if (!msCross && !halfMsCross) {
                ints.add(data[i]);
                continue;
            }
            if (msCross) {
                timeCounter += counterDelta;
                counterDelta = TIME_COUNTER_INC_PER_MS;
                int timeMessage = TIME_MASK | (timeCounter & TIME_COUNTER_MASK);
                ints.add(timeMessage);
                ints.add(data[i]);
                continue;
            }
            if (halfMsCross) {
                int ms = (int) ((currentTime - floor(currentTime)) * 1000);
                if (ms < TIME_SEQUENCE_LENGTH) {
                    if (ms == 0 && !digitization) {
                        t = firstTime;
                        k = i;
                    }
                    digitization = true;
                    int second = (int) (firstTime + i * dt);
                    //int second = (int) currentTime;
                    boolean[] timeSequence = getTimeSequence(second);
                    if (timeSequence[ms]) {
                        counterDelta = TIME_COUNTER_INC_PER_MS / 2 - SIGNIFICANT_MARKER_SHIFT;
                        timeCounter += counterDelta;
                        counterDelta = TIME_COUNTER_INC_PER_MS / 2 + SIGNIFICANT_MARKER_SHIFT;
                        int timeMessage = TIME_MASK | (timeCounter & TIME_COUNTER_MASK);
                        ints.add(timeMessage);
                    }
                } else {
                    digitization = false;
                    k = 0;
                }
                ints.add(data[i]);
            }
        }
        int result[] = new int[ints.size()];
        for (int i = 0; i < ints.size(); i++) {
            result[i] = ints.get(i);
        }
        return result;
    }
}
