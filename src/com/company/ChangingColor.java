package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

public class ChangingColor {
    public Color color = Color.WHITE;

    private class MPanel extends JPanel {
        public void paintComponent(Graphics g1) {
            Graphics2D g = (Graphics2D) g1;
            super.paintComponent(g1);
            g.setColor(color);
            g.fillRect(0, 0, 1000, 1000);
        }
    }

    public void getColor(ArrayList<Color> c, int index, JPanel q, JButton b) {
        JFrame frame = new JFrame("Параметры цвета");
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        frame.setResizable(false);
        frame.setVisible(true);
        frame.setLayout(null);
        frame.setPreferredSize(new Dimension(445, 330));

        Scroll red = new Scroll(0, 255, Scroll.VERTICAL);
        red.setBounds(100, 20, 15, 200);
        red.setValue(color.getRed());
        frame.add(red);

        Scroll green = new Scroll(0, 255, Scroll.VERTICAL);
        green.setBounds(215, 20, 15, 200);
        green.setValue(color.getGreen());
        frame.add(green);

        Scroll blue = new Scroll(0, 255, Scroll.VERTICAL);
        blue.setBounds(330, 20, 15, 200);
        blue.setValue(color.getBlue());
        frame.add(blue);

        MPanel panel = new MPanel();
        panel.setBounds(0, 0, 445, 15);
        frame.add(panel);

        red.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                color = new Color(red.getValue(), green.getValue(), blue.getValue());
                panel.repaint();
            }
        });

        green.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                color = new Color(red.getValue(), green.getValue(), blue.getValue());
                panel.repaint();
            }
        });

        blue.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                color = new Color(red.getValue(), green.getValue(), blue.getValue());
                panel.repaint();
            }
        });

        JButton ok = new JButton("OK");
        ok.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                c.set(index, color);
                try {
                    Path path = Paths.get("colorsChannel.properties");
                    Properties properties = new Properties();
                    properties.load(new FileReader("colorsChannel.properties"));
                    if (color.equals(Color.WHITE)) {
                        properties.remove("" + index);
                    } else {
                        properties.setProperty("" + index, "" + color.getRed() + " " + color.getGreen() + " " + color.getBlue());
                    }
                    properties.store(Files.newOutputStream(path), null);
                } catch (FileNotFoundException ex_1) {
                    System.out.println("Не удалось прочитать файл colorsChannel.properties");
                } catch (IOException ex_2) {
                    System.out.println("Не удалось записать в файл colorsChannel.properties");
                }
                frame.setVisible(false);
                q.repaint();
                if (color.equals(Color.WHITE)) {
                    b.setBackground(null);
                } else {
                    b.setBackground(color);
                }
            }
        });
        ok.setBounds(180, 250, 85, 20);
        frame.add(ok);

        frame.pack();
        frame.setLocation((screen.width - frame.getWidth()) / 2, (screen.height - frame.getHeight()) / 2);
    }


    public void getColor(ArrayList<Color> c, int index, JPanel q, JButton b, Color newColor) {
        c.set(index, color);
        System.out.println("color.toString() = " + color.toString());
        System.out.println("index = " + index);
        q.repaint();
        b.setBackground(color);
    }
}

