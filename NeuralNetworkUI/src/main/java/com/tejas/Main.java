package com.tejas;

import com.formdev.flatlaf.FlatDarkLaf;
import com.tejas.ui.MainForm;

import javax.swing.*;

public class Main {
    public static void startApplication() {
        FlatDarkLaf.setup();

        MainForm mainForm = new MainForm();

        JFrame frame = new JFrame("Scratch Neural Networks");
        frame.setSize(835, 600);
        frame.setResizable(false);
        frame.setLayout(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setContentPane(mainForm.getPanelMain());
        frame.setVisible(true);
    }

    public static void main(String[] args) throws Exception {
        startApplication();
    }
}