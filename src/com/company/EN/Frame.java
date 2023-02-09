package com.company.EN;

import java.util.ArrayList;
import java.util.zip.CRC32;

public class Frame {
    float timeStartFrame;
    ArrayList<Integer> wordsList = new ArrayList<>();
    private CRC32 crc32 = new CRC32();
    private static final int BYTE_MASK = 0xFF;
    public int countEqual;
    private float rating;

    public Frame(float timeStartFrame, ArrayList<Integer> wordsList) {
        this.timeStartFrame = timeStartFrame;
        this.wordsList = new ArrayList<>(wordsList);
        calc_CRC32();
    }

    public Frame(float timeStartFrame) {
        this.timeStartFrame = timeStartFrame;
        this.wordsList = new ArrayList<>(wordsList);
        this.rating = -100;
    }

    public void calc_CRC32(){
        for(int value : wordsList){
            for (int j = 0; j < Integer.BYTES; j++) {
                crc32.update((value >> j * Byte.SIZE) & BYTE_MASK);
            }
        }
    }

    public long getCrc32Code() {
        return crc32.getValue() == 0 ? -1 : crc32.getValue();
    }

    public void setCrc32(CRC32 crc32) {
        this.crc32 = crc32;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Frame other = (Frame) obj;
        return getCrc32Code() == other.getCrc32Code();
    }

    @Override
    public int hashCode() {
        return (int) (getCrc32Code() % Integer.MAX_VALUE);
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public int[] getWordsList() {
        int[] result = new int[wordsList.size()];
        for(int i = 0; i < result.length; i++){
            result[i] = wordsList.get(i);
        }
        return result;
    }
}
