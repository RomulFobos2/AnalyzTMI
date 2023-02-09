package com.company;

import com.company.EN.AutoEN;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.Font;
import java.awt.event.*;
import java.io.File;
import java.io.IOException;
import java.util.*;

public class WorkWindow extends JFrame {

    private JPanel contentPane;
    public static JTextField fileName;
    public static String pathToFile;
    public static JButton btnNewButton_1 = new JButton("Обработать");
    //private JTextField textFieldFile_1;
    //ArrayList<String> fileNameForEN = new ArrayList<>();
    HashMap<String, Boolean> fileNameForENMAP = new LinkedHashMap<>();
    static ArrayList<JTextField> inputFileEN = new ArrayList<>();
    static ArrayList<JButton> inputBtnFileEN = new ArrayList<>();
    static ArrayList<JRadioButton> inputRdnFileEN = new ArrayList<>();
    public static JTextArea textArea_1;
    public static JTextArea textArea;
    public static JProgressBar progressBar;
    public static JProgressBar progressBar_1;
    public static JButton btnAllInc = new JButton("Общий +");
    public static JButton btnAllDec = new JButton("Общий -");
    public static Scroll scrollBarALLHor = new Scroll(0, 110, JScrollBar.HORIZONTAL);
    public static Scroll scrollBarALLVert = new Scroll(0, 110, JScrollBar.VERTICAL);
    public float timeKP = 0;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    WorkWindow frame = new WorkWindow();
                    frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Create the frame.
     */
    public WorkWindow() {

        btnAllInc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CoreClass.inc.doClick();
            }
        });
        btnAllDec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                CoreClass.dec.doClick();
            }
        });
        scrollBarALLHor.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                BoundedRangeModel model = scrollBarALLHor.getModel();
                CoreClass.scrollHor.setModel(model);
            }
        });
        scrollBarALLVert.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                BoundedRangeModel model = scrollBarALLVert.getModel();
                CoreClass.scrollVert.setModel(model);
            }
        });

        setFont(new Font("Dialog", Font.PLAIN, 16));
        setTitle("Оператор");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(100, 100, 598, 550);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
        tabbedPane.setFont(new Font("Tahoma", Font.PLAIN, 16));
        tabbedPane.setBounds(0, 0, 582, 515);
        JPanel panel = new JPanel();
        tabbedPane.addTab("Отображение графиков файлов ТМИ", panel);
        panel.setLayout(null);

        JLabel lblNewLabel = new JLabel("Номер потока: 1");
        lblNewLabel.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblNewLabel.setBounds(20, 11, 143, 20);
        panel.add(lblNewLabel);

        JLabel lblNewLabel_1 = new JLabel("Основной коммутатор:");
        lblNewLabel_1.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblNewLabel_1.setBounds(20, 42, 225, 20);
        panel.add(lblNewLabel_1);

        JLabel lblNewLabel_2 = new JLabel("Коэффициент запараллеливания: 1");
        lblNewLabel_2.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblNewLabel_2.setBounds(20, 135, 313, 20);
        panel.add(lblNewLabel_2);

        JLabel lblNewLabel_3 = new JLabel("Локальных коммутаторов: 64");
        lblNewLabel_3.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblNewLabel_3.setBounds(20, 73, 264, 20);
        panel.add(lblNewLabel_3);

        JLabel lblNewLabel_4 = new JLabel("Теллеметрическая система: КИМ-Ц");
        lblNewLabel_4.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblNewLabel_4.setBounds(20, 104, 347, 20);
        panel.add(lblNewLabel_4);

        JLabel lblNewLabel_5 = new JLabel("Информативность: 51200 слов/с");
        lblNewLabel_5.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblNewLabel_5.setBounds(20, 166, 287, 20);
        panel.add(lblNewLabel_5);

        JComboBox comboBox_1 = new JComboBox();
        comboBox_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
        comboBox_1.setModel(new DefaultComboBoxModel(new String[]{"1", "2", "3", "4", "5", "6", "7", "8"}));
        comboBox_1.setSelectedIndex(2);
        comboBox_1.setBounds(221, 41, 49, 22);
        panel.add(comboBox_1);

        JLabel lblNewLabel_KP = new JLabel("Поправка КП:");
        lblNewLabel_KP.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblNewLabel_KP.setBounds(324, 11, 130, 20);
        panel.add(lblNewLabel_KP);

        JTextField textField_KP = new JTextField();
        textField_KP.setText("0.0");
        textField_KP.setFont(new Font("Tahoma", Font.PLAIN, 16));
        textField_KP.setBounds(446, 11, 88, 20);
        panel.add(textField_KP);
        textField_KP.setColumns(10);

        JLabel lblNewLabel2_KP = new JLabel("с.");
        lblNewLabel2_KP.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblNewLabel2_KP.setBounds(544, 11, 20, 20);
        panel.add(lblNewLabel2_KP);

        JLabel lblNewLabel_KP_2 = new JLabel("Поправка КП:");
        lblNewLabel_KP_2.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblNewLabel_KP_2.setBounds(324, 47, 130, 20);
        panel.add(lblNewLabel_KP_2);

        JRadioButton radioButton_KP = new JRadioButton("Автоматически");
        radioButton_KP.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (radioButton_KP.isSelected()) {
                    textField_KP.setEnabled(false);
                } else {
                    textField_KP.setEnabled(true);
                }
            }
        });
        radioButton_KP.setFont(new Font("Tahoma", Font.PLAIN, 15));
        radioButton_KP.setBounds(444, 42, 131, 30);
        panel.add(radioButton_KP);

        JButton btnNewButton = new JButton("Выберите файл");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                FileNameExtensionFilter filter = new FileNameExtensionFilter("TMI Files", "tmi");
                JFileChooser fileopen = new JFileChooser();
                fileopen.setFileFilter(filter);
                fileopen.setAcceptAllFileFilterUsed(false);
                int ret = fileopen.showDialog(null, "Открыть файл");
                if (ret == JFileChooser.APPROVE_OPTION) {
                    File file = fileopen.getSelectedFile();
                    fileName.setText(file.getName());
                    pathToFile = file.getAbsolutePath();
                }
            }
        });
        btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnNewButton.setBounds(20, 271, 158, 30);
        panel.add(btnNewButton);

        JLabel lblNewLabel_7 = new JLabel("Файл ТМИ");
        lblNewLabel_7.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_7.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblNewLabel_7.setBounds(195, 207, 158, 20);
        panel.add(lblNewLabel_7);

        fileName = new JTextField();
        fileName.setEditable(false);
        fileName.setFont(new Font("Tahoma", Font.PLAIN, 16));
        fileName.setBounds(20, 238, 547, 22);
        panel.add(fileName);
        fileName.setColumns(10);


        btnNewButton_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (pathToFile == null || pathToFile.equals("")) {
                    JOptionPane.showMessageDialog(null, "Выберите файл!");
                } else {
                    boolean checkKP = true;
                    if (!radioButton_KP.isSelected()) {
                        String strTimeKP = textField_KP.getText().trim();
                        try {
                            timeKP = Float.parseFloat(strTimeKP);
                        } catch (Exception exception) {
                            checkKP = false;
                        }
                    }
                    if (checkKP) {
                        Thread thr = new Thread() {
                            @Override
                            public void run() {
                                progressBar.setValue(0);
                                FileWithTMI fileWithTMI = new FileWithTMI(pathToFile);
                                LK lk = fileWithTMI.analyzerFile.LKList.get(comboBox_1.getSelectedIndex());
                                lk.calculateGaugeLevels();
                                WorkWindow.textArea_1.append("Вычесленное время КП " + fileWithTMI.analyzerFile.LKList.get(7).calculateTimeKP() + "\n");
                                CoreClass coreClass = new CoreClass();
                                //coreClass.drawChart(lk.wordOfLK);
                                if (radioButton_KP.isSelected()) {
                                    timeKP = fileWithTMI.analyzerFile.LKList.get(7).calculateTimeKP();
                                }
                                coreClass.drawChart(lk.dataForChart(0, timeKP), pathToFile.split("\\\\")[pathToFile.split("\\\\").length - 1]);
                            }
                        };
                        thr.start();
                    } else {
                        JOptionPane.showMessageDialog(null, "Время поправки КП указано не корректно");
                    }
                }
            }
        });
        btnNewButton_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnNewButton_1.setBounds(409, 271, 158, 30);
        panel.add(btnNewButton_1);

        JLabel lblNewLabel_6 = new JLabel("Информация");
        lblNewLabel_6.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblNewLabel_6.setBounds(69, 312, 94, 20);
        panel.add(lblNewLabel_6);

        JSeparator separator = new JSeparator();
        separator.setBounds(0, 323, 59, 2);
        panel.add(separator);

        JSeparator separator_1 = new JSeparator();
        separator_1.setBounds(173, 323, 404, 2);
        panel.add(separator_1);


        JScrollPane scrollPane_1 = new JScrollPane();
        scrollPane_1.setBounds(20, 336, 547, 91);
        panel.add(scrollPane_1);

        textArea_1 = new JTextArea();
        scrollPane_1.setViewportView(textArea_1);
        textArea_1.setEditable(false);

        JPanel panel_1 = new JPanel();
        tabbedPane.addTab("Формирование единного носителя", panel_1);
        panel_1.setLayout(null);

        JSeparator separator_2 = new JSeparator();
        separator_2.setBounds(0, 273, 59, 2);
        panel_1.add(separator_2);

        JSeparator separator_3 = new JSeparator();
        separator_3.setBounds(173, 273, 404, 2);
        panel_1.add(separator_3);
        contentPane.add(tabbedPane);

        JLabel lblNewLabel_9 = new JLabel("Информация");
        lblNewLabel_9.setFont(new Font("Tahoma", Font.PLAIN, 16));
        lblNewLabel_9.setBounds(69, 262, 94, 20);
        panel_1.add(lblNewLabel_9);

        JLabel lblNewLabel_7_1 = new JLabel("Файл ТМИ");
        lblNewLabel_7_1.setHorizontalAlignment(SwingConstants.CENTER);
        lblNewLabel_7_1.setFont(new Font("Tahoma", Font.BOLD, 16));
        lblNewLabel_7_1.setBounds(193, 11, 158, 20);
        panel_1.add(lblNewLabel_7_1);

//        textFieldFile_1 = new JTextField();
//        textFieldFile_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
//        textFieldFile_1.setEditable(false);
//        textFieldFile_1.setColumns(10);
//        textFieldFile_1.setBounds(34, 42, 365, 26);
//        panel_1.add(textFieldFile_1);

//        JButton btnNewButton_2 = new JButton("Выберите файл");
//        btnNewButton_2.addActionListener(new ActionListener() {
//            public void actionPerformed(ActionEvent e) {
//                FileNameExtensionFilter filter = new FileNameExtensionFilter("TMI Files", "tmi");
//                JFileChooser fileopen = new JFileChooser();
//                fileopen.setFileFilter(filter);
//                fileopen.setAcceptAllFileFilterUsed(false);
//                int ret = fileopen.showDialog(null, "Открыть файл");
//                if (ret == JFileChooser.APPROVE_OPTION) {
//                    File file = fileopen.getSelectedFile();
//                    textFieldFile_1.setText(file.getName());
//                    fileNameForEN.add(file.getAbsolutePath());
//                }
//            }
//        });
//        btnNewButton_2.setFont(new Font("Tahoma", Font.PLAIN, 16));
//        btnNewButton_2.setBounds(409, 40, 158, 30);
//        panel_1.add(btnNewButton_2);

        JButton btnNewButton_1_1 = new JButton("Сформировать ЕН");
        btnNewButton_1_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (fileNameForENMAP.size() < 2) {
                    JOptionPane.showMessageDialog(null, "Выберите минимум два файла ТМИ");
                } else {
                    Thread thr = new Thread() {
                        @Override
                        public void run() {
                            progressBar_1.setValue(0);
                            try {
                                btnNewButton_1_1.setEnabled(false);
                                progressBar_1.setIndeterminate(true);
                                AutoEN.start(fileNameForENMAP);
                                progressBar_1.setIndeterminate(false);
                                btnNewButton_1_1.setEnabled(true);
                            } catch (IOException ioException) {
                                ioException.printStackTrace();
                            }
                        }
                    };
                    thr.start();
                }
            }
        });
        btnNewButton_1_1.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnNewButton_1_1.setBounds(213, 232, 186, 30);
        panel_1.add(btnNewButton_1_1);

        JButton btnNewButton_3 = new JButton("+");
        btnNewButton_3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (inputFileEN.size() < 4) {
                    JTextField field = new JTextField();
                    field.setFont(new Font("Tahoma", Font.BOLD, 12));
                    field.setEditable(false);
                    field.setColumns(10);
                    field.setBounds(34, 42 + 50 * (inputFileEN.size()), 315, 26);
                    panel_1.add(field);
                    inputFileEN.add(field);

                    JRadioButton rdbtnNewRadioButton = new JRadioButton("14");
                    rdbtnNewRadioButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            String fileName = field.getText();

                            Iterator<Map.Entry<String, Boolean>> entryIterator = fileNameForENMAP.entrySet().iterator();
                            while (entryIterator.hasNext()) {
                                Map.Entry<String, Boolean> entry = entryIterator.next();
                                String[] strings = entry.getKey().split("\\\\");
                                String varFileName = strings[strings.length - 1];
                                if (varFileName.equals(fileName)) {
                                    entry.setValue(rdbtnNewRadioButton.isSelected());
                                }
                            }

                        }
                    });
                    rdbtnNewRadioButton.setBounds(355, 42 + 50 * (inputRdnFileEN.size()), 45, 23);
                    inputRdnFileEN.add(rdbtnNewRadioButton);
                    panel_1.add(rdbtnNewRadioButton);
                    //setResizable(false);

                    JButton button = new JButton("Выберите файл");
                    button.addActionListener(new ActionListener() {
                        public void actionPerformed(ActionEvent e) {
                            String oldChooseFile = field.getText();
//                            Iterator<String> iterable = fileNameForEN.iterator();
//                            while (iterable.hasNext()) {
//                                String[] strings = iterable.next().split("\\\\");
//                                String varFileName = strings[strings.length - 1];
//                                if (varFileName.equals(oldChooseFile)) {
//                                    iterable.remove();
//                                }
//                            }
                            FileNameExtensionFilter filter = new FileNameExtensionFilter("TMI Files", "tmi");
                            JFileChooser fileopen = new JFileChooser();
                            fileopen.setFileFilter(filter);
                            fileopen.setAcceptAllFileFilterUsed(false);
                            int ret = fileopen.showDialog(null, "Открыть файл");
                            if (ret == JFileChooser.APPROVE_OPTION) {
                                Iterator<Map.Entry<String, Boolean>> entryIterator = fileNameForENMAP.entrySet().iterator();
                                while (entryIterator.hasNext()) {
                                    Map.Entry<String, Boolean> entry = entryIterator.next();
                                    String[] strings = entry.getKey().split("\\\\");
                                    String varFileName = strings[strings.length - 1];
                                    if (varFileName.equals(oldChooseFile)) {
                                        entryIterator.remove();
                                    }
                                }
                                File file = fileopen.getSelectedFile();
                                field.setText(file.getName());
                                //fileNameForEN.add(file.getAbsolutePath());
                                fileNameForENMAP.put(file.getAbsolutePath(), rdbtnNewRadioButton.isSelected());
                            }
                        }
                    });
                    button.setFont(new Font("Tahoma", Font.PLAIN, 16));
                    button.setBounds(409, 40 + 50 * (inputBtnFileEN.size()), 158, 30);
                    panel_1.add(button);
                    inputBtnFileEN.add(button);
                    repaint();
                }

            }
        });
        btnNewButton_3.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btnNewButton_3.setBounds(472, 239, 45, 23);
        panel_1.add(btnNewButton_3);

        JButton btnNewButton_3_1 = new JButton("-");
        btnNewButton_3_1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (inputFileEN.size() > 0) {
                    JTextField delField = inputFileEN.get(inputFileEN.size() - 1);
                    panel_1.remove(delField);
                    inputFileEN.remove(delField);
                    JButton delBtn = inputBtnFileEN.get(inputBtnFileEN.size() - 1);
                    panel_1.remove(delBtn);
                    inputBtnFileEN.remove(delBtn);
                    JRadioButton delRdn = inputRdnFileEN.get(inputRdnFileEN.size() - 1);
                    panel_1.remove(delRdn);
                    inputRdnFileEN.remove(delRdn);
                    String oldChooseFile = delField.getText();
//                    Iterator<String> iterable = fileNameForEN.iterator();
//                    while (iterable.hasNext()) {
//                        String[] strings = iterable.next().split("\\\\");
//                        String varFileName = strings[strings.length - 1];
//                        if (varFileName.equals(oldChooseFile)) {
//                            iterable.remove();
//                        }
//                    }
                    Iterator<Map.Entry<String, Boolean>> entryIterator = fileNameForENMAP.entrySet().iterator();
                    while (entryIterator.hasNext()) {
                        Map.Entry<String, Boolean> entry = entryIterator.next();
                        String[] strings = entry.getKey().split("\\\\");
                        String varFileName = strings[strings.length - 1];
                        if (varFileName.equals(oldChooseFile)) {
                            entryIterator.remove();
                        }
                    }
                    repaint();
                }
            }
        });
        btnNewButton_3_1.setFont(new Font("Tahoma", Font.PLAIN, 15));
        btnNewButton_3_1.setBounds(522, 239, 45, 23);
        panel_1.add(btnNewButton_3_1);

        JScrollPane scrollPane = new JScrollPane();
        scrollPane.setBounds(10, 286, 557, 141);
        panel_1.add(scrollPane);


        textArea = new JTextArea();
        textArea.setEditable(false);
        scrollPane.setViewportView(textArea);

        progressBar = new JProgressBar();
        progressBar.setBounds(20, 438, 542, 22);
        progressBar.setMinimum(0);
        progressBar.setMaximum(100);
        progressBar.setStringPainted(true);
        panel.add(progressBar);

        progressBar_1 = new JProgressBar();
        progressBar_1.setBounds(20, 438, 542, 22);
        panel_1.add(progressBar_1);

        btnNewButton_3.doClick();
        setResizable(false);
    }
}

