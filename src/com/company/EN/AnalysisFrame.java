package com.company.EN;

import java.util.ArrayList;

public class AnalysisFrame {
    static Frame getBetterFrame(ArrayList<Frame> frames) {
//        System.out.println("Получили кадры:");
//        for (Frame varFrame : frames) {
//            System.out.println("КС = " + varFrame.getCrc32Code() + " : Время = " + varFrame.timeStartFrame + " Size = " + varFrame.wordsList.size());
//        }

        //Вначале подсчитаем кол-во исскуственных кадров (которы всавили для синхронизации времени, их КС = -1)
        int count_negativeOne = 0;
        for (Frame varFrame : frames) {
            if (varFrame.getCrc32Code() == -1) {
                count_negativeOne++;
            }
        }
        //Если все кадры исскуственные, то заканчиваем выборку и возвращаем null
        if (count_negativeOne == frames.size()) {
            return null;
            //return frames.get(0);
        }
        //Если все кадры кроме одного исскуственные, то возвращаем единственный не исскуственный кадр
        if (count_negativeOne == frames.size() - 1) {
            for (Frame varFrame : frames) {
                if (varFrame.getCrc32Code() != -1) {
                    varFrame.setRating(90.0f);
                    return varFrame;
                }
            }
        }
        //Если есть два и больше одинаковых кадров, то возвращаем один из них
        int maxEqual = 0;
        Frame valueEqual = frames.get(0);
        for (int i = 0; i < frames.size(); i++) {
            Frame mainFrame = frames.get(i);
            int var_maxEqual = 0;
            for (Frame varFrame : frames) {
                if (mainFrame.equals(varFrame) & (mainFrame.getCrc32Code() != -1)) {
                    var_maxEqual++;
                }
            }
            if (var_maxEqual > maxEqual) {
                maxEqual = var_maxEqual;
                valueEqual = mainFrame;
            }
        }
        //Если есть хотябы две одинаковые КС, то возвращаем такой кадр
        if (maxEqual >= 2) {
            for (Frame varFrame : frames) {
                if (varFrame.equals(valueEqual)) {
                    varFrame.setRating(100.0f);
                }
            }
            return valueEqual;
        }
        //Начинаем анализ кадра и его слов
        else {
//            for (Frame frame : frames) {
//                int k = 0;
//                for (int i = 0; i < frame.wordsList.size(); i++) {
//                    String s = Integer.toBinaryString(frame.wordsList.get(i));
//                    s = "0000000000000000".substring(s.length()) + s;
//                    System.out.printf("%18s", s);
//                    k++;
//                    if (k % 8 == 0) {
//                        System.out.println();
//                    }
//
//                }
//                System.out.println("");
//            }
            //Оцениваем каждый кадр (кроме исскуственного)
            for (int i = 0; i < frames.size(); i++) {
                if (frames.get(i).getCrc32Code() == -1) {
//                    System.out.println("Пропуск исскуственного кадра");
                    continue;
                }
//                System.out.println("Файл № " + i);
//                System.out.println("КС кадра = " + frames.get(i).getCrc32Code() + " ; Время = " + frames.get(i).timeStartFrame);
//                System.out.println("Значения 47 канала у файла (3 коммутатор) = " + AutoEN.enArrayList.get(i).gaugeLevel_47_LK3);
//                System.out.println("Значения 62 канала у файла (3 коммутатор) = " + AutoEN.enArrayList.get(i).gaugeLevel_62_LK3);
//                System.out.println("Значения 47 канала у кадра (3 коммутатор) = " + (frames.get(i).wordsList.get(371 - 1) & 0x1FF));
//                System.out.println("Значения 62 канала у кадра (3 коммутатор) = " + (frames.get(i).wordsList.get(491 - 1) & 0x1FF));
//                System.out.println("Кол-во маркеров ЛК " + calculateLKMark(frames.get(i)) + " из 8");
//                System.out.println("Кол-во маркеров ОК " + calculateOKMark(frames.get(i)) + " из 64");
//                System.out.println("Кол-во слов с правильным битом четности = " + calculateBit(frames.get(i)));
//                System.out.println("Самая длинная подряд последовательность битов четоности = " + calculateBitEven(frames.get(i)));
//                System.out.println("Имеется признак конца кадра = " + endOfFrame(frames.get(i)));
//                System.out.println("Средняя оценка предыдущих 100 кадров = " + calculateMiddleRating(i, frames.get(i)));
//                System.out.println("-----------------------------");
                float rating = 0;
                int const_47 = AutoEN.enArrayList.get(i).gaugeLevel_47_LK3;
                int const_62 = AutoEN.enArrayList.get(i).gaugeLevel_62_LK3;
                int value_47 = frames.get(i).wordsList.get(371 - 1) & 0x1FF;
                int value_62 = frames.get(i).wordsList.get(491 - 1) & 0x1FF;
                if (value_47 >= const_47 - 4 & const_47 + 4 >= value_47) {
                    rating = rating + 20;
                }
                if (value_62 >= const_62 - 4 & const_62 + 4 >= value_62) {
                    rating = rating + 20;
                }
                if (calculateLKMark(frames.get(i)) == 8) {
                    rating = rating + 20;
                }
                if (calculateOKMark(frames.get(i)) == 64) {
                    rating = rating + 20;
                }
                if (endOfFrame(frames.get(i))) {
                    rating = rating + 20;
                }
                float ratingBit = 5 - ((512 - calculateBit(frames.get(i)))/100f);
                rating = rating + ratingBit;
//                System.out.println("Устанавили рейтинг = " + rating);
                frames.get(i).setRating(rating);
            }
//            System.out.println("=================================================================");
            float maxRating = 0;
            //Вычисляем максимльный рейтинг
            for(Frame frame : frames){
                if(frame.getRating() > maxRating){
                    maxRating = frame.getRating();
                }
            }
            float maxMiddleRating = 0;
            Frame resultFrame = frames.get(0);
            for(int i = 0; i < frames.size(); i++){
                if(frames.get(i).getRating() == maxRating){
                    float currentMiddleRating = calculateMiddleRating(i, frames.get(i));
                    if(currentMiddleRating > maxMiddleRating){
                        maxMiddleRating = currentMiddleRating;
                        resultFrame = frames.get(i);
                    }
                }
            }
//            System.out.println("Выбран кадр с КС - " + resultFrame.getCrc32Code() + " ; Оценка = " + resultFrame.getRating());
            return resultFrame;
        }
    }


    //Считаем маркеры ЛК, они выставляются для последних слов для каждого коммутатора.
    static int calculateLKMark(Frame frame) {
        int count = 0;
        for (int i = 504; i < frame.wordsList.size(); i++) {
            int value = (frame.wordsList.get(i) & 0x400) >> 10;
            if (value == 1) {
                count++;
            }
        }
        return count;
    }

    //Считаем маркеры ОК, они выставляютяс для каждого 8 слова (т.к. для 8 коммутатора)
    static int calculateOKMark(Frame frame) {
        int count = 0;
        for (int i = 7; i < frame.wordsList.size(); i = i + 8) {
            int value = (frame.wordsList.get(i) & 0x800) >> 11;
            if (value == 1) {
                count++;
            }
        }
        return count;
    }

    //Проверяем имеется ли признак конца кадра
    static boolean endOfFrame(Frame frame) {
        int endOfFrame = frame.wordsList.get(frame.wordsList.size() - 1) & 0x3FF;
        if (endOfFrame == 0x3FF) {
            return true;
        } else {
            return false;
        }
    }

    //Подсчитываем кол-во слов с правильным битом четности
    static int calculateBit(Frame frame) {
        int result = 0;
        for (int i = 0; i < frame.wordsList.size(); i++) {
            int value = frame.wordsList.get(i) & 0x3FF;
            int countBit = Integer.bitCount(value);
            if (countBit % 2 == 0) {
                result++;
            }
        }
        return result;
    }

    //Подсчитываем самую длинную не нарушающюся последовательность битов четности
    static int calculateBitEven(Frame frame) {
        int countBitEven = 0;
        int var_countBitEven = 0;
        for (int i = 0; i < frame.wordsList.size(); i++) {
            int value = frame.wordsList.get(i) & 0x3FF;
            int countBit = Integer.bitCount(value);
            if (countBit % 2 == 0) {
                var_countBitEven++;
            } else {
                if (var_countBitEven > countBitEven) {
                    countBitEven = var_countBitEven;
                }
                var_countBitEven = 0;
            }
        }
        if (var_countBitEven > countBitEven) {
            countBitEven = var_countBitEven;
        }
        return countBitEven;
    }

    //Подсчитываме среднюю оценку предыдущих 100 кадров
    //Параметры: Номер файла в enArrayList indexFile и кадр
    static float calculateMiddleRating(int indexFile, Frame frame) {
        ArrayList<Frame> preAllFrames = AutoEN.enArrayList.get(indexFile).allFrames;
        int currentIndexFrame = preAllFrames.indexOf(frame);
        int i = 0;
        if (currentIndexFrame > 100) {
            i = currentIndexFrame - 100;
        }
        int count = 0;
        float summa = 0;
        for (; i < currentIndexFrame; i++) {
            count++;
            summa = summa + preAllFrames.get(i).getRating();
        }
        return summa / count;
    }
}
