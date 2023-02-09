package com.company;

import javax.swing.*;
import java.awt.*;

public class Window extends JFrame {

    public Window(){
        MyPanel myPanel = new MyPanel();
        Container container = getContentPane();
        setTitle("Монтаж");
        setBounds(0, 0, 610, 500);
        setLocationRelativeTo(null); //Размещаем окно по центру
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
        container.add(myPanel);
    }
}
