package com.company;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Toolkit;

import javax.swing.JTextField;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPasswordField;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.Color;
import java.awt.Dimension;

public class Login extends JFrame {

    private JPanel contentPane;
    private JTextField textFieldLogin;
    private JPasswordField passwordField;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    Login frame = new Login();
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
    public Login() {
        setTitle("Вход");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setBounds(300, 300, 713, 434);
        setLocationRelativeTo(null);
        contentPane = new JPanel();
        contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
        setContentPane(contentPane);
        contentPane.setLayout(null);

        JLabel labelProgName = new JLabel("");
        labelProgName.setForeground(Color.BLACK);
        labelProgName.setHorizontalAlignment(SwingConstants.CENTER);
        labelProgName.setText("<html> <p> Программное обеспечение для формирования единого носителя </p> <p>и отображения телеметрической информации для РН «Союз-2» </p>" + "</html>");
        labelProgName.setFont(new Font("Tahoma", Font.PLAIN, 18));
        labelProgName.setBounds(46, 11, 586, 72);
        contentPane.add(labelProgName);

        JLabel labelLogin = new JLabel("Введите логин");
        labelLogin.setHorizontalAlignment(SwingConstants.CENTER);
        labelLogin.setFont(new Font("Tahoma", Font.BOLD, 16));
        labelLogin.setBounds(278, 109, 133, 30);
        contentPane.add(labelLogin);

        JLabel labelPass = new JLabel("Введите пароль");
        labelPass.setHorizontalAlignment(SwingConstants.CENTER);
        labelPass.setFont(new Font("Tahoma", Font.BOLD, 16));
        labelPass.setBounds(278, 191, 143, 30);
        contentPane.add(labelPass);

        textFieldLogin = new JTextField();
        textFieldLogin.setFont(new Font("Tahoma", Font.PLAIN, 16));
        textFieldLogin.setBounds(246, 150, 200, 30);
        contentPane.add(textFieldLogin);
        textFieldLogin.setColumns(10);

        JButton btnNewButton = new JButton("Вход");
        btnNewButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String login = textFieldLogin.getText().trim();
                String password = passwordField.getText().trim();
                if(login.equals("Operator") & password.equals("Operator")) {
                    JOptionPane.showMessageDialog(null, "Добро пожаловать!");
                    setVisible(false);
                    WorkWindow frame = new WorkWindow();
                    frame.setVisible(true);
                }
                else {
                    JOptionPane.showMessageDialog(null, "Неправильный логин и/или пароль");
                }

            }
        });
        btnNewButton.setFont(new Font("Tahoma", Font.PLAIN, 16));
        btnNewButton.setBounds(298, 272, 99, 30);
        contentPane.add(btnNewButton);

        passwordField = new JPasswordField();
        passwordField.setFont(new Font("Tahoma", Font.PLAIN, 16));
        passwordField.setBounds(245, 224, 201, 30);
        contentPane.add(passwordField);

        JLabel imageLabel = new JLabel(new ImageIcon("fon.jpg"));
        imageLabel.setBounds(0, 0, 700, 400);
        contentPane.add(imageLabel);
        setResizable(false);


    }
}


