package com.company;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Properties;

public class CoreClass {
    public boolean firstShow = true;
    public static JButton inc = new JButton("+");
    public static JButton dec = new JButton("–");
    private ArrayList<FloatPoint> data = new ArrayList<FloatPoint>();
    private ArrayList<Color> colors = new ArrayList<Color>();
    public ArrayList<JButton> buttonsAllChanel = new ArrayList<>();
    private float centerX = 300, centerY = 250, scale = 1, W = 1100, H = 600, width = 960, height = 500;
    public static Scroll scrollVert = new Scroll(0, 110, JScrollBar.VERTICAL),
            scrollHor = new Scroll(0, 110, JScrollBar.HORIZONTAL);

    private FloatPoint toC(FloatPoint a) {
        Float x = a.x, y = a.y, w = W * scale, h = H * scale, sx = (float)scrollHor.getValue() / 100 * (W - w),
                sy = (float)scrollVert.getMValue() / 100 * (H - h);
        return new FloatPoint((x - sx + W / 2 - centerX) * width / w, -(y - sy + H / 2 - centerY - h) * height / h);
    }

    private FloatPoint fromC(FloatPoint a) {
        Float x = a.x, y = -a.y, w = W * scale, h = H * scale, sx = (float)scrollHor.getValue() / 100 * (W - w),
                sy = (float)scrollVert.getMValue() / 100 * (H - h);
        return new FloatPoint(w * x / width + sx + W / 2 + centerX, h * y / height + sy + H / 2 + centerY + h);
    }

    private class MPanel extends JPanel {
        public void paintComponent(Graphics g1) {
            Graphics2D g = (Graphics2D) g1;
            super.paintComponent(g1);

            g.setStroke(new BasicStroke(1));
            g.setColor(Color.GRAY);
            float px = 100;
            if (scale < 0.3)
                px = 10;
            if (scale < 0.05)
                px = 1;
            if (scale < 0.005)
                px = 0.1f;
            for (float i = -10000; i <= 10000; i += px) {
                g.drawLine(0, Math.round(toC(new FloatPoint(0f, i)).y), 1000, Math.round(toC(new FloatPoint(0f, i)).y));
                //g.drawString(String.valueOf(i), 0, Math.round(toC(new FloatPoint(0f, i)).y) + 15);
                g.drawLine(Math.round(toC(new FloatPoint(i, 0f)).x), 0, Math.round(toC(new FloatPoint(i, 0f)).x), 1000);
                //g.drawString(String.valueOf(i), Math.round(toC(new FloatPoint(i, 0f)).x), 15);
            }

            g.setColor(Color.BLACK);

            g.drawLine(30, 5, 30, 465);
            g.drawLine(30, 5, 27, 15);
            g.drawLine(30, 5, 33, 15);

            g.drawLine(30, 465, 955, 465);
            g.drawLine(955, 465, 945, 462);
            g.drawLine(955, 465, 945, 468);

            for (float i = -10000; i <= 10000; i += px) {
                int x = Math.round(toC(new FloatPoint(i, 0f)).x), y = Math.round(toC(new FloatPoint(0f, i)).y);

                if (y >= 15 && y <= 485) {
                    g.drawLine(25, y, 35, y);
                    if (scale < 0.005)
                        g.drawString(String.valueOf((float)Math.round(i * 10) / 10), 0, y + 5);
                    else
                        g.drawString(String.valueOf((int)i), 5, y + 5);
                }

                if (x >= 10 && x <= 945) {
                    g.drawLine(x, 460, x, 470);
                    if (scale < 0.005)
                        g.drawString(String.valueOf((float)Math.round(i * 10) / 10), x - 10, 490);
                    else
                        g.drawString(String.valueOf((int)i), x - 10, 490);
                }
            }
            int count = 0;
            g.setStroke(new BasicStroke(3));
            for (int i = 0; i < data.size(); i++) {
                if(data.get(i) == null){
                    continue;
                }
                int x = Math.round(toC(data.get(i)).x), y = Math.round(toC(data.get(i)).y);
                if (colors.get(i % 64).equals(Color.WHITE))
                    continue;
                g.setColor(colors.get(i % 64));
                g.drawLine(x, y, x, y);
            }
        }
    }

    public void drawChart(ArrayList<FloatPoint> data_2, String fileName){
        data = data_2;

        Properties properties = new Properties();
        try {
            properties.load(new FileReader("colorsChannel.properties"));
        } catch (IOException e) {
            System.out.println("Не удалось прочитать файл colorsChannel.properties");
        }
        for (int i = 0; i < 64; i++){
            String str_colorRGB = properties.getProperty(String.valueOf(i));
            if(str_colorRGB != null){
                String red = str_colorRGB.split(" ")[0];
                String green = str_colorRGB.split(" ")[1];
                String blue = str_colorRGB.split(" ")[2];
                Color color = new Color(Integer.parseInt(red), Integer.parseInt(green), Integer.parseInt(blue));
                colors.add(color);
            }
            else {
                colors.add(Color.WHITE);
            }
        }

        //JFrame frame = new JFrame("График");
        JFrame frame = new JFrame(fileName);
        frame.setVisible(true);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setPreferredSize(new Dimension(1020, 680));
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        //frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        JLabel coords = new JLabel();
        coords.setBounds(300, 570, 250, 100);
        frame.add(coords);

        MPanel panel = new MPanel();
        panel.setBounds(10, 10, 960, 500);
        panel.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                coords.setText(fromC(new FloatPoint((float)e.getX(), (float)e.getY())).toString(W, H));
            }
        });
        frame.add(panel);

        Scroll new_scrollHor = new Scroll(0, 110, JScrollBar.HORIZONTAL);
        new_scrollHor.setBounds(10, 520, 960, 15);
        new_scrollHor.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                BoundedRangeModel model = new_scrollHor.getModel();
                WorkWindow.scrollBarALLHor.setModel(model);
            }
        });
        frame.add(new_scrollHor);

        scrollHor.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                BoundedRangeModel model = scrollHor.getModel();
                new_scrollHor.setModel(model);
                panel.repaint();
            }
        });

        Scroll new_scrollVert = new Scroll(0, 110, JScrollBar.VERTICAL);
        new_scrollVert.setBounds(980, 10, 15, 500);
        new_scrollVert.addMouseMotionListener(new MouseAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                BoundedRangeModel model = new_scrollVert.getModel();
                WorkWindow.scrollBarALLVert.setModel(model);
            }
        });
        frame.add(new_scrollVert);

        scrollVert.addAdjustmentListener(new AdjustmentListener() {
            @Override
            public void adjustmentValueChanged(AdjustmentEvent e) {
                BoundedRangeModel model = scrollVert.getModel();
                new_scrollVert.setModel(model);
                panel.repaint();
            }
        });

        for (int i = 0; i < 64; i++) {
            JButton button = new JButton(String.valueOf(i + 1));
            buttonsAllChanel.add(button);
            button.setBounds((i % 32) * 30 + 10, 540 + (i / 32) * 30, 30, 30);
            button.setFont(new Font("Arial", Font.PLAIN, 10));
            button.setMargin(new Insets(0, 0, 0, 0));
            button.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    ChangingColor c = new ChangingColor();
                    c.getColor(colors, Integer.parseInt(button.getText()) - 1, panel, button);
                }
            });
            frame.add(button);
        }

        inc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                centerY -= 11 * scale;
                scale *= 0.8;
                panel.repaint();
            }
        });
        //frame.add(inc);

        JButton newInc = new JButton("+");
        newInc.setBounds(10, 610, 50, 20);
        newInc.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WorkWindow.btnAllInc.doClick();
            }
        });
        frame.add(newInc);

        dec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                scale /= 0.8;
                centerY += 11 * scale;
                if (scale > 1) {
                    centerY -= 11 * scale;
                    scale *= 0.8;
                }
                panel.repaint();
            }
        });

        JButton newDec = new JButton("-");
        newDec.setBounds(70, 610, 50, 20);
        newDec.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                WorkWindow.btnAllDec.doClick();
            }
        });
        frame.add(newDec);

        JButton breakAllColor = new JButton("Сбросить все каналы");
        breakAllColor.setBounds(790, 610, 180, 20);
        breakAllColor.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                for(int i = 0; i < colors.size(); i++){
                    colors.set(i, Color.WHITE);
                    buttonsAllChanel.get(i).setBackground(null);
                    panel.repaint();
                }
                Path path = Paths.get("colorsChannel.properties");
                properties.clear();
                try {
                    properties.store(Files.newOutputStream(path), null);
                } catch (IOException ioException) {
                    System.out.println("Не удалось очистить файл colorsChannel.properties");
                }
            }
        });
        frame.add(breakAllColor);

        if(firstShow){
            for(int i = 0; i < buttonsAllChanel.size(); i++){
                if(!colors.get(i).equals(Color.WHITE)){
                    buttonsAllChanel.get(i).setBackground(colors.get(i));
                }
            }
            firstShow = false;
        }

        frame.pack();
        frame.setLocation((screen.width - frame.getWidth()) / 2, (screen.height - frame.getHeight()) / 2);
        panel.repaint();
    }
}
