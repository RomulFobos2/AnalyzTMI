package com.company;

import com.company.EN.AutoEN;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class MyPanel extends JPanel {
    static String pathToFile;
    public static String fileForEN_1;
    public static String fileForEN_2;
    public static String fileForEN_3;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        for(int i = 0; i < 500; i+=50){
            g.drawLine(0, i, 600,i);
        }

        for(int i = 0; i < 600; i+=200){
            g.drawLine(i, 0, i,500);
        }

        Font font = new Font("arial", 1, 16);
        setLayout(null);

        JLabel jLabel_1_1  = new JLabel("Файл ТМИ:");
        jLabel_1_1.setBounds(10, 10, 180, 30);
        jLabel_1_1.setBorder(BorderFactory.createLineBorder(Color.black));
        jLabel_1_1.setFont(font);
        add(jLabel_1_1);

        JLabel jLabel_1_2 = new JLabel();
        jLabel_1_2.setBounds(210, 10, 180, 30);
        jLabel_1_2.setBorder(BorderFactory.createLineBorder(Color.black));
        jLabel_1_2.setFont(font);
        add(jLabel_1_2);

        JButton jButton_1_3 = new JButton();
        jButton_1_3.setText("Выберите файл");
        jButton_1_3.setBounds(410, 10, 180, 30);
        jButton_1_3.setBorder(BorderFactory.createLineBorder(Color.black));
        jButton_1_3.setFont(font);
        jButton_1_3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser();
                int ret = fileopen.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileopen.getSelectedFile();
                    jLabel_1_2.setText(file.getName());
                    pathToFile = file.getAbsolutePath();
                }
            }
        });
        add(jButton_1_3);

        JLabel jLabel_2_1  = new JLabel("Выберите ЛК:");
        jLabel_2_1.setBounds(10, 60, 180, 30);
        jLabel_2_1.setBorder(BorderFactory.createLineBorder(Color.black));
        jLabel_2_1.setFont(font);
        add(jLabel_2_1);

        String[] valueLK = {"1", "2", "3", "4", "5", "6", "7", "8"};
        JComboBox select_2_2 = new JComboBox(valueLK);
        select_2_2.setBounds(210, 60, 180, 30);
        select_2_2.setBorder(BorderFactory.createLineBorder(Color.black));
        select_2_2.setSelectedIndex(2);
        add(select_2_2);

        JButton jButton_2_2 = new JButton();
        jButton_2_2.setText("Обработать файл");
        jButton_2_2.setBounds(410, 60, 180, 30);
        jButton_2_2.setBorder(BorderFactory.createLineBorder(Color.black));
        jButton_2_2.setFont(font);
        jButton_2_2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if(pathToFile == null || pathToFile.equals("")){
                    JOptionPane.showMessageDialog(null, "Выберите файл!");
                }
                else {
                    int timeKP = 0;
                    FileWithTMI fileWithTMI = new FileWithTMI(pathToFile);
                    LK lk = fileWithTMI.analyzerFile.LKList.get(select_2_2.getSelectedIndex());
                    lk.calculateGaugeLevels();
                    JOptionPane.showMessageDialog(null, "Выбран " + lk.name);
                    CoreClass coreClass = new CoreClass();
                    //coreClass.drawChart(lk.wordOfLK);
                    coreClass.drawChart(lk.dataForChart(0, 0), pathToFile.split("\\\\")[pathToFile.split("\\\\").length-1]);
                }
            }
        });
        add(jButton_2_2);

        JLabel jLabel_4_1  = new JLabel("Авто ЕН:");
        jLabel_4_1.setBounds(10, 160, 180, 30);
        jLabel_4_1.setBorder(BorderFactory.createLineBorder(Color.black));
        jLabel_4_1.setFont(font);
        add(jLabel_4_1);

        JLabel jLabel_5_2 = new JLabel();
        jLabel_5_2.setBounds(210, 210, 180, 30);
        jLabel_5_2.setBorder(BorderFactory.createLineBorder(Color.black));
        jLabel_5_2.setFont(font);
        add(jLabel_5_2);

        JButton jButton_5_1 = new JButton();
        jButton_5_1.setText("Выберите файл ЕН");
        jButton_5_1.setBounds(10, 210, 180, 30);
        jButton_5_1.setBorder(BorderFactory.createLineBorder(Color.black));
        jButton_5_1.setFont(font);
        jButton_5_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser();
                int ret = fileopen.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileopen.getSelectedFile();
                    jLabel_5_2.setText(file.getName());
                    fileForEN_1 = file.getAbsolutePath();
                }
            }
        });
        add(jButton_5_1);

        JLabel jLabel_6_2 = new JLabel();
        jLabel_6_2.setBounds(210, 260, 180, 30);
        jLabel_6_2.setBorder(BorderFactory.createLineBorder(Color.black));
        jLabel_6_2.setFont(font);
        add(jLabel_6_2);

        JButton jButton_6_1 = new JButton();
        jButton_6_1.setText("Выберите файл");
        jButton_6_1.setBounds(10, 260, 180, 30);
        jButton_6_1.setBorder(BorderFactory.createLineBorder(Color.black));
        jButton_6_1.setFont(font);
        jButton_6_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser();
                int ret = fileopen.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileopen.getSelectedFile();
                    jLabel_6_2.setText(file.getName());
                    fileForEN_2 = file.getAbsolutePath();
                }
            }
        });
        add(jButton_6_1);

        JLabel jLabel_7_2 = new JLabel();
        jLabel_7_2.setBounds(210, 310, 180, 30);
        jLabel_7_2.setBorder(BorderFactory.createLineBorder(Color.black));
        jLabel_7_2.setFont(font);
        add(jLabel_7_2);

        JButton jButton_7_1 = new JButton();
        jButton_7_1.setText("Выберите файл");
        jButton_7_1.setBounds(10, 310, 180, 30);
        jButton_7_1.setBorder(BorderFactory.createLineBorder(Color.black));
        jButton_7_1.setFont(font);
        jButton_7_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileopen = new JFileChooser();
                int ret = fileopen.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileopen.getSelectedFile();
                    jLabel_7_2.setText(file.getName());
                    fileForEN_3 = file.getAbsolutePath();
                }
            }
        });
        add(jButton_7_1);

        JButton jButton_8_2 = new JButton();
        jButton_8_2.setText("Сформировать ЕН");
        jButton_8_2.setBounds(210, 360, 180, 30);
        jButton_8_2.setBorder(BorderFactory.createLineBorder(Color.black));
        jButton_8_2.setFont(font);
        jButton_8_2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
//                try {
//                    AutoEN.start();
//                } catch (IOException ioException) {
//                    ioException.printStackTrace();
//                }
            }
        });
        add(jButton_8_2);
    }
}
