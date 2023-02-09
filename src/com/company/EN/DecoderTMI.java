package com.company.EN;

import java.util.ArrayList;

public class DecoderTMI {
    int word;
    int time = 0; //time хранит текущее значение счетчика времени от [0 ; 4096)
    int prev_time = 0; //prev_time хранит предыдущие значение счетчика времени
    int difference; //разница между текущем и предыдущем значением счетчика
    int counterForSeconds = 20; //код секунды формируется 20 разрядами, этой переменной считаем кол-во уже полученных разрядов
    boolean start_digitization = false; //тригер маркера оццифровки секунды
    int seconds_digitization = 0; //20разрядное значение секунды в 2СС полученное после оцифровки
    int second = 0; //Текущее значение секунды в обычном представление
    int pass = 2; //В случаем если встречаем разницу <= 4, то следующее после этого значение записывать не нужно. С помощью этой переменной пропускаем.
    boolean firstFrame = true; //Первый кадр пропускаем.
    boolean firstTime = true; //Первая оццифровка
    int millisecond = 0; //Добавляем каждому слову время.
    ArrayList<Frame> allFrames = new ArrayList<>();
    private ArrayList<Integer> var_frame = new ArrayList<>();
    float startTimeFrame = 0;
    int crushFrame = 0;

    int gaugeLevel_47_LK3;
    int gaugeLevel_62_LK3;

    public void start(byte[] bytes) {
        for (int i = 0; i < bytes.length; i = i + 2) {
            //Формируем слово. Учитывая структуру передачи, сначала идет младший байт, затем старший.
            //При формировании слова, разряды 15-8 заполняются элеметном i+1, разряды 7-0 элеметном i.
            try {
                word = (bytes[i + 1] & 0xFF) << Byte.SIZE | (bytes[i] & 0xFF);
            } catch (ArrayIndexOutOfBoundsException e) {
                continue;
            }
            //Вычисляем текущее время для слова в милисекундах
            long timeForWord = second * 1000 + millisecond;
            //Вычисляем номер источника
            int sourceID = (word & 0x3000) >> 12;
            //Берем слова только с 0 источника
            if (sourceID == 0) {
                //Если значения разрядов 15 и 14 равны 0, значит это слово данных, иначе слово времени.
                if ((word & 0xC000) == 0) {
                    var_frame.add(word);
                    //Конец кадра
                    if ((word & 0xFFF) == 0xFFF | var_frame.size() == 512) {
                        if((word & 0xFFF) != 0xFFF){
                            crushFrame++;
                        }
                        if(var_frame.size() != 512){
                            crushFrame++;
                        }
                        if (firstFrame) {
                            var_frame.clear();
                            firstFrame = false;
                            //Время начала следующего слова (соотвественно нового кадра) в мс
                            startTimeFrame = timeForWord + 1000 / 51200f;
                        } else {
                            if (var_frame.size() == 512) {
                                allFrames.add(new Frame(startTimeFrame, var_frame));
                            }
//                            allFrames.add(new Frame(startTimeFrame, var_frame));
                            startTimeFrame = timeForWord + 1000 / 51200f;
                            var_frame.clear();
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
            if (firstTime) {
                for (int i = 0; i < allFrames.size(); i++) {
                    int count_word = 0;
                    for (int j = i; j < allFrames.size(); j++) {
                        count_word = count_word + allFrames.get(j).wordsList.size() + var_frame.size();
                    }
                    float new_time = second * 1000 - count_word * (1000 / 51200f);
                    allFrames.get(i).timeStartFrame = new_time;
                }
                startTimeFrame = second * 1000 - var_frame.size() * (1000 / 51200f);
                firstTime = false;
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

    public void calculateGaugeLevels() {
        this.gaugeLevel_47_LK3 = calculateGaugeLevel(371 - 1);
        this.gaugeLevel_62_LK3 = calculateGaugeLevel(491 - 1);
//        System.out.println("Значения 47 канала = " + this.gaugeLevel_47_LK3);
//        System.out.println("Значения 62 канала = " + this.gaugeLevel_62_LK3);
    }


    //Считаем значения 47 и 62 канала 3 комутатора.
    //В кадре из 512 слов это всегда 371 и 491 слово
    public int calculateGaugeLevel(int levels) {
        //После того, как встретится подряд 30 одинаковых значений, считаем это значением канала
        int count = 50;
        int index = 1;
        int result = allFrames.get(0).wordsList.get(levels) & 0x1FF;
        while (count > 0) {
            int value = allFrames.get(index).wordsList.get(levels) & 0x1FF;
            if (value == result) {
                count--;
            } else {
                count = 50;
                result = value;
            }
            index++;
        }
        return result;
    }
}