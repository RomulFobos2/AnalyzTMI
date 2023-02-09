package com.company.EN;

import com.company.MyPanel;
import com.company.WorkWindow;

import javax.swing.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

public class AutoEN {
    public static ArrayList<DecoderTMI> enArrayList = new ArrayList<>();
    public static ArrayList<Frame> frameForEN = new ArrayList<>();

    //public static void start(ArrayList<String> fileNameForEN) throws IOException {
    public static void start(HashMap<String, Boolean> fileNameForEN) throws IOException {
        enArrayList.clear();
        frameForEN.clear();
//        for (String s : fileNameForEN) {
//            FileWithTMI fileWithTMI = new FileWithTMI(s);
//            enArrayList.add(fileWithTMI.decoderTMI);
//        }
        for (Map.Entry<String, Boolean> entry : fileNameForEN.entrySet()) {
            FileWithTMI fileWithTMI = new FileWithTMI(entry.getKey(), entry.getValue());
            enArrayList.add(fileWithTMI.decoderTMI);
        }
        synchroFirstFrame();
        //print();
        infoSync();
        infoCR();
        equalizer();
        createEN();
        TimeEnricher timeEnricher = new TimeEnricher();
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM-dd-yyyy(HH-mm)", Locale.ENGLISH);
        String s = simpleDateFormat.format(date).toLowerCase();
        String strFileName = "EN" + s + ".tmi";
        File file = new File(strFileName);
        FileOutputStream fileOutputStream = new FileOutputStream(file);
        for (Frame frame : frameForEN) {
            int[] ints = timeEnricher.enrich2(frame);
            int index = 0;
            byte[] bytes = new byte[ints.length * 2];
            for (int word : ints) {
                byte oldByte = (byte) (word >> 8);
                byte youngByte = (byte) (word & 0xFF);
                bytes[index] = youngByte;
                index++;
                bytes[index] = oldByte;
                index++;
            }
            fileOutputStream.write(bytes);
        }
        fileOutputStream.close();
        JOptionPane.showMessageDialog(null, "Единный носитель сформирован.");

        WorkWindow.fileName.setText(file.getName());
        WorkWindow.pathToFile = file.getAbsolutePath();
        WorkWindow.btnNewButton_1.doClick();
    }

    public static void main(String[] args) throws IOException {
        //start();
        synchroFirstFrame();
        infoSync();
        infoCR();
        createEN();
        TimeEnricher timeEnricher = new TimeEnricher();
        FileOutputStream fileOutputStream = new FileOutputStream(new File("EN.tmi"));
        for (Frame frame : frameForEN) {
            int[] ints = timeEnricher.enrich2(frame);
            int index = 0;
            byte[] bytes = new byte[ints.length * 2];
            for (int word : ints) {
                byte oldByte = (byte) (word >> 8);
                byte youngByte = (byte) (word & 0xFF);
                bytes[index] = youngByte;
                index++;
                bytes[index] = oldByte;
                index++;
            }
            fileOutputStream.write(bytes);
        }
        fileOutputStream.close();
        JOptionPane.showMessageDialog(null, "Единный носитель сформирован.");
    }

    //Определяем самый маленький файл с кадрами
    static int determinateMinSize() {
        int minSize = enArrayList.get(0).allFrames.size();
        for (DecoderTMI decoderTMI : enArrayList) {
            minSize = Math.min(minSize, decoderTMI.allFrames.size());
        }
        return minSize;
    }

    //Определяем самый большой файл с кадрами
    static int determinateMaxSize() {
        int maxSize = enArrayList.get(0).allFrames.size();
        for (DecoderTMI decoderTMI : enArrayList) {
            maxSize = Math.max(maxSize, decoderTMI.allFrames.size());
        }
        return maxSize;
    }

    //Синхронизируем все списки кадров, по первому кадру. (т.е. делаем, чтобы первый кадр был у всех с одинаковой КС.
    public static void synchroFirstFrame() {
        DecoderTMI enFile_1 = enArrayList.get(0);
        int i = 0;
        boolean check = false;
        ArrayList<Integer> index = new ArrayList<>();
        for (; i < enFile_1.allFrames.size(); i++) {
            if (!check) {
                index.clear();
                int count_check = enArrayList.size() - 1; //Кол-во необходимых совпадений. Если три файла считали, то для кадра из первого файла должно найтись два совпадения
                Frame mainFrame = enFile_1.allFrames.get(i);
                for (int j = 1; j < enArrayList.size(); j++) {
                    DecoderTMI var_enFile = enArrayList.get(j);
                    for (int k = 0; k < var_enFile.allFrames.size(); k++) {
                        Frame var_Frame = var_enFile.allFrames.get(k);
                        if (mainFrame.equals(var_Frame)) {
                            index.add(k);
                            count_check--;
                            if (count_check < 1) {
                                check = true;
                            }
                            break;
                        }
                    }
                }
            } else {
                i--;
                index.add(0, i);
                break;
            }
        }
        //Удаляем начальные кадры, чтобы синхронизируемые кадры оказались первыми элементами
        for (int j = 0; j < index.size(); j++) {
            int repeat = index.get(j);
            while (repeat > 0) {
                enArrayList.get(j).allFrames.remove(0);
                repeat--;
            }
        }
        //Необходима два раза вызвать метод для восстановления времени
        rebornTime();
        rebornTime();
    }

    static void rebornTime() {
        for (DecoderTMI en : enArrayList) {
            ArrayList<Frame> frames = en.allFrames;
            int balance = 0;
            int start_index = 0;
            for (int i = 1; i < frames.size(); i++) {
                int var_index = i;
                float difference = frames.get(i).timeStartFrame - frames.get(i - 1).timeStartFrame;
                int var_balance = (int) (difference - 10);
                balance = balance + var_balance;

                if (var_balance != 0) {
                    start_index = i;
                }

                //while (balance >= 10) {
                while (balance >= 9) {
                    Frame frameAdd = new Frame(frames.get(var_index - 1).timeStartFrame + 10);
                    frames.add(var_index, frameAdd);
                    var_index++;
                    balance = balance - 10;
                }

                if (i - start_index >= 100 | balance == 0) {
                    balance = 0;
                    start_index = 0;
                }
            }
        }
    }

    static void createEN() {
        int count_1 = 0;
        int count_2 = 0;
        for (int i = 0; i < determinateMinSize(); i++) {
            ArrayList<Frame> currentFrame = new ArrayList<>();
            for (DecoderTMI decoderTMI : enArrayList) {
                currentFrame.add(decoderTMI.allFrames.get(i));
            }
            boolean check = true;
            Frame mainFrame = currentFrame.get(0);
            for (Frame varFrame : currentFrame) {
                if (mainFrame.getCrc32Code() != -1 & varFrame.getCrc32Code() != -1) {
                    check = check & mainFrame.equals(varFrame);
                } else {
                    check = false;
                }

            }
            if (check) {
                for (Frame varFrame : currentFrame) {
                    varFrame.setRating(100.0f);
                }
                count_1++;
                frameForEN.add(currentFrame.get(0));
            } else {
                Frame betterFrame = AnalysisFrame.getBetterFrame(currentFrame);
                if (betterFrame != null) {
                    count_2++;
                    frameForEN.add(betterFrame);
                }
            }
        }
        WorkWindow.textArea.append("Вставлено синхронных кадров = " + count_1 + "\n");
        WorkWindow.textArea.append("Выбирался кадр = " + count_2 + " раз" + "\n");
        WorkWindow.textArea.append("Кол-во кадров в файле ЕН = " + frameForEN.size() + "\n");
//        System.out.println("Вставлено синхронных кадров = " + count_1);
//        System.out.println("Выбирался кадр = " + count_2 + " раз");
//        System.out.println("Кол-во кадров в файле ЕН = " + frameForEN.size());
    }

    static void print() {
        ArrayList<Frame> _1 = enArrayList.get(0).allFrames;
        ArrayList<Frame> _2 = enArrayList.get(1).allFrames;
        //ArrayList<Frame> _3 = enArrayList.get(2).allFrames;
        //ArrayList<Frame> _4 = enArrayList.get(3).allFrames;
        for (int i = 200; i < 210; i++) {
            System.out.println("PRINT:");
            System.out.println("Индекс = " + i);
            System.out.println("1: КС = " + _1.get(i).getCrc32Code() + " : Время = " + _1.get(i).timeStartFrame + " Size = " + _1.get(i).wordsList.size());
            System.out.println("2: КС = " + _2.get(i).getCrc32Code() + " : Время = " + _2.get(i).timeStartFrame + " Size = " + _2.get(i).wordsList.size());
            //System.out.println("3: КС = " + _3.get(i).getCrc32Code() + " : Время = " + _3.get(i).timeStartFrame + " Size = " + _3.get(i).wordsList.size());
            //System.out.println("4: КС = " + _4.get(i).getCrc32Code() + " : Время = " + _4.get(i).timeStartFrame + " Size = " + _4.get(i).wordsList.size());
            System.out.println("=======================================");
        }
    }


    static void infoSync() {
        for (DecoderTMI decoderTMI : enArrayList) {
            //System.out.println("Кол-во кадров = " + decoderTMI.allFrames.size());
        }
        int count_1 = 0;
        int count_2 = 0;
        for (int i = 0; i < determinateMinSize(); i++) {
            ArrayList<Frame> currentFrame = new ArrayList<>();
            for (DecoderTMI decoderTMI : enArrayList) {
                currentFrame.add(decoderTMI.allFrames.get(i));
            }
            boolean check = true;
            Frame mainFrame = currentFrame.get(0);
            for (Frame varFrame : currentFrame) {
                if (mainFrame.getCrc32Code() != -1 & varFrame.getCrc32Code() != -1) {
                    check = check & mainFrame.equals(varFrame);
                } else {
                    check = false;
                }

            }
            if (check) {
                for (Frame varFrame : currentFrame) {
                    varFrame.setRating(100.0f);
                }
                count_1++;
            } else {
//                System.out.println("INFO:");
//                System.out.println("Индекс = " + i);
//                for (Frame varFrame : currentFrame) {
//                    System.out.println("КС = " + varFrame.getCrc32Code() + " : Время = " + varFrame.timeStartFrame + " Size = " + varFrame.wordsList.size() + " : Оценка " + varFrame.getRating());
//                }
//                System.out.println("=======================================");
                count_2++;
            }
        }
        WorkWindow.textArea.append("Синхронных кадров = " + count_1 + "\n");
        WorkWindow.textArea.append("Не синхронных кадров = " + count_2 + "\n");
//        System.out.println("Синхронных кадров = " + count_1);
//        System.out.println("Не синхронных кадров = " + count_2);
    }

    //После первой синхронизации, проводим вторую. Для всех остальных кадров.
    //Суть в том, что если есть кадоы с одинаковой КС, но они стоят на разных позициях относительно друг друга, то такие кадры сводим к одной позиции.
    static void infoCR() {
        //Определяем минимальный размер файлов из имеющихся/ После удаления в синхронизации, также пересчитываем размер
        int count = 0;
        ArrayList<Integer> index = new ArrayList<>();
        DecoderTMI enFile_1 = enArrayList.get(0);
        for (int i = 0; i < enFile_1.allFrames.size(); i++) {
            Frame mainFrame = enFile_1.allFrames.get(i);
            for (int j = 1; j < enArrayList.size(); j++) {
                boolean check = false;
                DecoderTMI var_enFile = enArrayList.get(j);
                int k = 0;
                if (i > 1000) {
                    k = i - 1000;
                }
                for (; k < var_enFile.allFrames.size(); k++) {
                    Frame varFrame = var_enFile.allFrames.get(k);
                    if (mainFrame.equals(varFrame) & mainFrame.getCrc32Code() != -1) {
                        index.add(k);
                        check = true;
                        break;
                    }
                }
                if (!check) {
                    index.add(-1);
                }
            }
            //Получаем список с индексами элементов с одинаковыми КС
            index.add(0, i);
            int count_negativeOne = 0;
            for (int var : index) {
                if (var == -1) {
                    count_negativeOne++;
                }
            }
            //Передаем индексы на анализ
            //Если в списке, все элементы кроме одного, равны -1, значит синхронизация для такого кадра не нужна. Его необходимо анализировать глубоким анализом
            if (count_negativeOne == 0) {
//                System.out.println("Индекс = " + i);
//                int var_j = 0;
//                for (AnalyzerEN en : enArrayList) {
//                    System.out.println("КС = " + en.allFrames.get(index.get(var_j)).getCrc32Code() + " ; Время = " + en.allFrames.get(index.get(var_j)).timeStartFrame);
//                    var_j++;
//                }
//                System.out.println("==========================");
                count++;
            }
            index.clear();
        }
        WorkWindow.textArea.append("Одинаковых контрольных сумм в файлах = " + count + "\n");
        //System.out.println("Одинаковых контрольных сумм в файле = " + count);
    }

    //Делаем все массивы с кадрами одной длины (длина = самого большого массива с кадрами)
    static void equalizer() {
        //System.out.println("Самый большой размер файла с кадрами - " + determinateMaxSize());
        for (DecoderTMI decoderTMI : enArrayList) {
            while (decoderTMI.allFrames.size() < determinateMaxSize()) {
                float newTime = decoderTMI.allFrames.get(decoderTMI.allFrames.size() - 1).timeStartFrame + 10;
                decoderTMI.allFrames.add(new Frame(newTime));
            }
        }
    }

    protected static List<byte[]> process(Frame tmiFrame) throws Exception {
        final int id = (1 - 1) << 12;
        final int[] messages = tmiFrame.getWordsList();

        final byte[] b = new byte[Short.BYTES * messages.length];
        for (int i = 0; i < messages.length; i++) {
            int message = messages[i];
            message &= 0xCFFF;
            message |= id;
            b[Short.BYTES * i] = (byte) (message & 0xFF);
            b[Short.BYTES * i + 1] = (byte) ((message >> Byte.SIZE) & 0xFF);
        }
        final List<byte[]> list = new ArrayList<>();
        list.add(b);
        return list;
    }
}




