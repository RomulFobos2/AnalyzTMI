package com.company.EN;

import com.company.WorkWindow;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class FileWithTMI {
    String fileName;
    FileInputStream fileInputStream;
    int fileSize;
    DecoderTMI decoderTMI = new DecoderTMI();
    boolean check_14 = false;

    public FileWithTMI(String fileName, boolean check_14) {
        this.fileName = fileName;
        boolean chekFileName = false;
        while (!chekFileName) {
            try {
                fileInputStream = new FileInputStream(new File(fileName));
                //JOptionPane.showMessageDialog(null, "Прочитан файл '" + fileName + "'");
                WorkWindow.textArea.append("Прочитан файл " + fileName + "\n");
                fileSize = getAvailableByte();
                if (fileSize <= 0) {
                    JOptionPane.showMessageDialog(null, "Файл '" + fileName + "' пуст или поток для чтения был закрыт");
                    fileName = JOptionPane.showInputDialog(null, "Выберите файл", "Путь к файлу");
                } else {
                    WorkWindow.textArea.append("Кол-во байтов в файле " + fileName + " - " + fileSize + "\n");
                    chekFileName = true;
                }
            } catch (FileNotFoundException e) {
                JOptionPane.showMessageDialog(null, "Файл '" + fileName + "' не найден");
                fileName = JOptionPane.showInputDialog(null, "Выберите файл", "Путь к файлу");
            }
        }
//        if(fileName.contains("_014_")){
//            check_14 = true;
//        }
        this.check_14 = check_14;
        if(check_14){
            WorkWindow.textArea.append("Для файла " + fileName + " выбрана 14 частота" + "\n");
        }
        startAnalyze();
    }

    //Cчитываем по 1024 байта и передаем их в метод Analyzer
    public void startAnalyze() {
        //Кол-во байт для считывания за один раз
        int lengthForRead = 1024;
        byte[] bytes;
//        int progressMaxValue = getAvailableByte()/1024 / 10;
//        int progressVarValue = 0;
        while (getAvailableByte() > 0) {
//            progressVarValue++;
//            if(progressVarValue >= progressMaxValue){
//                WorkWindow.progressBar_1.setValue(WorkWindow.progressBar_1.getValue() + 1);
//                progressVarValue = 0;
//            }
            if (getAvailableByte() < lengthForRead) {
                lengthForRead = getAvailableByte();
            }
            bytes = new byte[lengthForRead];
            try {
                fileInputStream.read(bytes, 0, lengthForRead);
            } catch (IOException e) {
                e.printStackTrace();
                System.out.println("Ошибка ввода/вывода");
            }
            decoderTMI.start(bytes);
        }
        decoderTMI.calculateGaugeLevels();
        if(check_14){
            for(Frame frame : decoderTMI.allFrames){
                frame.timeStartFrame = frame.timeStartFrame - 3072;
            }
        }
        WorkWindow.textArea.append("Количество неполных кадров = " + decoderTMI.crushFrame + "\n");
    }

    //Метод available() вынесен в отдельный метод, для того, чтобы не заграмождать код проверкой на IOException
    public int getAvailableByte() {
        int result = -1;
        try {
            result = fileInputStream.available();
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println("ОШИБКА!");
        }
        return result;
    }
}
