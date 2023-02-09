package com.company;

import java.util.ArrayList;

public class Analyzer {
    int word;
    int time = 0; //time хранит текущее значение счетчика времени от [0 ; 4096)
    int prev_time = 0; //prev_time хранит предыдущие значение счетчика времени
    int difference; //разница между текущем и предыдущем значением счетчика
    int counterForSeconds = 20; //код секунды формируется 20 разрядами, этой переменной считаем кол-во уже полученных разрядов
    boolean start_digitization = false; //тригер маркера оццифровки секунды
    int seconds_digitization = 0; //20разрядное значение секунды в 2СС полученное после оцифровки
    int second = 0; //Текущее значение секунды в обычном представление
    int pass = 2; //В случаем если встречаем разницу <= 4, то следующее после этого значение записывать не нужно. С помощью этой переменной пропускаем.
    boolean firstTime = false; //Значение для секунды еще не получено.
    boolean firstFrame = true; //Первый кадр пропускаем, т.к. скорее всего он не полный
    int crushFrame = 0;

    ArrayList<LK> LKList = new ArrayList(); //Массив с 8 локальниками
    ArrayList<FloatPoint> var_WordsOfFrame = new ArrayList(); //Массив слов формируем пока не встретится конец кадра. Затем все слова из массива рассфасовываются по локальникам.

    //Добавляем каждому слову время.
    int millisecond = 0;

    {
        for (int i = 1; i <= 8; i++) {
            LKList.add(new LK(i));
        }
    }


    public void start(byte[] bytes) {
        for (int i = 0; i < bytes.length; i = i + 2) {
            //Формируем слово. Учитывая структуру передачи, сначала идет младший байт, затем старший.
            //При формировании слова, разряды 15-8 заполняются элеметном i+1, разряды 7-0 элеметном i.
            try {
                word = (bytes[i + 1] & 0xFF) << Byte.SIZE | (bytes[i] & 0xFF);
            } catch (ArrayIndexOutOfBoundsException e) {

            }
            //Вычисляем текущее время для слова !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            float timeForWord = (second * 1000 + millisecond);
            //Вычисляем номер источника
            int sourceID = (word & 0x3000) >> 12;
            //Берем слова только с 0 источника
            if (sourceID == 0) {
                //Если значения разрядов 15 и 14 равны 0, значит это слово данных, иначе слово времени.
                if ((word & 0xC000) == 0) {
                    float addWord = word & 0x1FF;
                    float addTime = (float) timeForWord / 1000f;
                    var_WordsOfFrame.add(new FloatPoint(addTime, addWord));
                    //if ((word & 0xFFF) == 0xFFF) { //Конец кадра
                    if ((word & 0xFFF) == 0xFFF | var_WordsOfFrame.size() == 512) { //Конец кадра
                        if((word & 0xFFF) != 0xFFF){
                            crushFrame++;
                        }
                        if(var_WordsOfFrame.size() != 512){
                            crushFrame++;
                        }
                        //Если в кадре оказалось 512 слов, то рассфасовываем их по локальникам
                        //делаем так, потому что в начале файл может быть зашумлен и в кадре может быть больше 512 слов, такие данные нам не нужны.
                        //if (var_WordsOfFrame.size() == 512) {
                        if (!firstFrame) {
                            int var = 0;
                            for (int j = 0; j < 512; j++) {
                                if(j > var_WordsOfFrame.size()-1){
                                    var_WordsOfFrame.add(null);
                                }
                                LKList.get(var).wordOfLK.add(var_WordsOfFrame.get(j));
                                var++;
                                if (var > 7) {
                                    var = 0;
                                }
                            }
                            var_WordsOfFrame.clear();
                        } else {
                            firstFrame = false;
                            var_WordsOfFrame.clear();
                        }
                    }
                }
                //Слово второго типа. Слово может быть чисто Слово времени, может быть чисто СЕВ, может одновременно быть и тем и тем
                else {
                    //Если слово является словом времени (15 разряд == 1).
                    // Сюда поподают также слова и с "1" в 14 разряде (маркер СЕВ)
                    if (word >> 15 == 1) {
                        time = word & 0xFFF;
                        timeWordAnalyze();
                    }
                }
            }
        }
    }

    //Метод для работы со словом времени (15 разряд == 1)
    public void timeWordAnalyze() {
        difference = time - prev_time;
        prev_time = time;
        //Счетчик меняется от 0 до 4095.
        // Чтобы посчитать разницу по модулю (т.к. может идти счетчик 4095, а потом 0. 0 - 4095 = -4095), если разница меньше 0, мы прибавляем к разницу 4096.
        if (difference < 0) {
            difference = difference + 4096;
        }

        //Время увеличилось на 1 миллисекунду
        if (difference > 4) {
            millisecond++;
        }
        //Если разница меньше 4, начилась оццифровка и кол-во значений для формирования секунды больше 0 (т.е. еще не получено 20 разрядов)
        //значит добавляем к формируемому 20 разрядному значению секунды '1'
        // и устанавливаем pass = 2, для того, чтобы "Надеюсь ты помнишь для чего" (записать это в одну-две строчки не представляется возможным)
        if (difference <= 4 & start_digitization == true & counterForSeconds > 0) {
            seconds_digitization = seconds_digitization << 1;
            seconds_digitization++;
            counterForSeconds--;
            pass = 2;
        }

        if (difference > 4 & start_digitization == true & counterForSeconds > 0 & pass <= 0) {
            seconds_digitization = seconds_digitization << 1;
            counterForSeconds--;
        }

        if (start_digitization == false) {
            if (difference <= 4) {
                start_digitization = true;
                pass = 2;
            }
        }

        //Оццифровка закончена, 20 разрядное число для секунды сформировано.
        //Осталось считать его, учитывая, что младшие байты впереди.
        //В методе secondBinToDec переворачиваем и переводим в 10 СС.
        if (counterForSeconds <= 0) {
            second = secondBinToDec(seconds_digitization);
            //Выполнится один раз
            if (firstTime == false) {
                int k = 1;
                for (int i = var_WordsOfFrame.size() - 1; i >= 0; i--) {
                    k = var_WordsOfFrame.size() - i;
                    float new_time = (second * 1000) - (k * (1000f / 51200f)); //Здесь время высчиталось в миллисекундах
                    new_time = new_time / 1000f;
                    //new_time = Math.round(new_time * 1000) / 1000f;
                    if(var_WordsOfFrame.get(i) != null){
                        var_WordsOfFrame.get(i).x = new_time;
                    }
                    k++;
                    firstTime = true;
                }
                int allWorldOnLK = 0;
                for (LK lk : LKList) {
                    allWorldOnLK = allWorldOnLK + lk.wordOfLK.size();
                }
                k = allWorldOnLK + k;
                int var_lk = 0;
                int var_index_word = 0;
                for (int i = allWorldOnLK; i > 0; i--) {
                    float new_time = (second * 1000) - (k * (1000 / 51200f));
                    new_time = new_time / 1000f;
                    if(LKList.get(var_lk).wordOfLK.get(var_index_word) != null){
                        LKList.get(var_lk).wordOfLK.get(var_index_word).x = new_time;
                    }
                    var_lk++;
                    if (var_lk > 7) {
                        var_lk = 0;
                        var_index_word++;
                    }
                    k--;
                }
            }
            millisecond = 0;
            seconds_digitization = 0;
            counterForSeconds = 20;
            start_digitization = false;
        }
        pass--;
    }

    //После формирования 20 разрядного двоичного числа секунды, необходимо число перевернуть, т.к. первыми приходили младдшии биты.
    public int secondBinToDec(int second) {
        int result = 0;
        for (int i = 0; i < 20; i++) {
            if ((second & 1) != 1) {
                result = result << 1;
            } else {
                result = result << 1;
                result++;
            }
            second = second >> 1;
        }
        //System.out.println("result = " + result);
        return result;
    }
}