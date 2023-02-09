package com.company;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

//Локальный коммутатор
public class LK {
    String name;
    ArrayList<FloatPoint> wordOfLK = new ArrayList<>();
    int gaugeLevel_47;
    int gaugeLevel_62;

    public LK(int i) {
        name = "Локальный коммутатор № " + i;
    }

    //Выводим значения всех 64 каналов локального коммутатора
    //line_number кол-во выводимых строк (кадров, т.к. можно сказать, что у локальника кадр равен 64)
    public void printInfo() {
        System.out.println("Локальный коммутатор = " + name);
        System.out.println("Кол-во слов = " + wordOfLK.size());
        //Печатаем номера каналов от 1 до 64
        for (int i = 1; i <= 64; i++) {
            System.out.printf("%15d", i);
        }
        //Выводим содержимое. Учитываем, что 9 разряд это бит четности, он не относится к информации.
//        for (int i = 0; i < line_number * 64; i++) {
        for (int i = 0; i < wordOfLK.size(); i++) {
            if (i % 64 == 0) {
                System.out.println();
            }
            System.out.printf("%15s", wordOfLK.get(i).x + " ; " + wordOfLK.get(i).y);
        }
        System.out.println();
    }

    //
    //Метод для получения всех значений у выбранного канала
    public ArrayList<Integer> getChannelAllVar(int channel_number) {
        System.out.println(name + ", значения канала № " + channel_number + ":");
        ArrayList<Integer> result = new ArrayList<>();
        int maxCount = wordOfLK.size() / 64;
        System.out.println("Кол-во строк = " + maxCount);
        for (int i = 0; i < maxCount; i++) {
            //result.add(wordOfLK.get((channel_number - 1 + 64 * i)).x);
            if (wordOfLK.get(channel_number - 1 + 64 * i) != null) {
                System.out.println(wordOfLK.get((channel_number - 1 + 64 * i)).y + " ; " + wordOfLK.get((channel_number - 1 + 64 * i)).x);
            }

        }
        return result;
    }

    //
//    public void getChannelAllVarWithTime(int channel_number) {
//        System.out.println(name + ", значения канала № " + channel_number + ":");
//        int maxCount = wordOfLK.size() / 64;
//        for (int i = 0; i < maxCount; i++) {
//            System.out.println(wordOfLK.get((channel_number - 1 + 64 * i)).time/1000 + " ; " + (wordOfLK.get((channel_number - 1 + 64 * i)).word & 0x1FF));
//        }
//    }
//
//
    public void makeDataFile() throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(new File("result.txt"));
        String s = "Локальный коммутатор = " + name;
        fileOutputStream.write(s.getBytes());
        for (int i = 0; i < wordOfLK.size(); i++) {
            s = wordOfLK.get(i).x + " ; " + (wordOfLK.get(i).y) + "\n";
            fileOutputStream.write(s.getBytes());
        }
        fileOutputStream.close();
    }

    public void calculateGaugeLevels() {
        this.gaugeLevel_47 = calculateGaugeLevel(47);
        this.gaugeLevel_62 = calculateGaugeLevel(62);
        WorkWindow.textArea_1.append("Значения 47 канала = " + this.gaugeLevel_47 + "\n");
        WorkWindow.textArea_1.append("Значения 62 канала = " + this.gaugeLevel_62 + "\n");
//        System.out.println("Значения 47 канала = " + this.gaugeLevel_47);
//        System.out.println("Значения 62 канала = " + this.gaugeLevel_62);
    }

    public int calculateGaugeLevel(int levels) {
        int maxCount = wordOfLK.size() / 64;
        int index = levels - 1;
        int i = 1;
        while (wordOfLK.get(index) == null) {
            index = levels - 1 + 64 * i;
            i++;
        }
        int value = (int) wordOfLK.get(index).y;
        int count = 30;
        for (; i < maxCount; i++) {
            index = levels - 1 + 64 * i;
            if (wordOfLK.get(index) == null) {
                count = 30;
                continue;
            }
            int var_value = (int) wordOfLK.get(index).y;
            if (value == var_value) {
                count--;
            } else {
                value = var_value;
                count = 30;
            }
            if (count <= 0) {
                return value;
            }
        }
        return 0;
    }

    //int time - с какой переодичностью пропускаю слова для вывода на график.
    //Если time = 50, то возьмется сначало 1 слово каналов, потом 51 и т.д.
    ArrayList<FloatPoint> dataForChart(int time, float timeKP) {
        ArrayList<FloatPoint> result = new ArrayList<>();
        int count = 0;
        for (int i = 0; i < wordOfLK.size() - 1; i++) {
            if(wordOfLK.get(i) != null){
                wordOfLK.get(i).x = wordOfLK.get(i).x - timeKP;
            }
            result.add(wordOfLK.get(i));
            count++;
            if (count == 64) {
                i = i + (64 * time);
                count = 0;
            }
        }
        //wordOfLK = result;
        return result;
    }

    float calculateTimeKP(){
        float result = -1;
        int maxCount = wordOfLK.size() / 64;
        for (int i = 0; i < maxCount; i++) {
            if (wordOfLK.get(55 - 1 + 64 * i) != null) {
                if(wordOfLK.get((55 - 1 + 64 * i)).y == 62.0){
                    //System.out.println("Время КП = " + wordOfLK.get((55 - 1 + 64 * i)).x);
                    result = (int) wordOfLK.get((55 - 1 + 64 * i)).x;
                    return result;
                }
            }
        }
        return result;
    }


}
