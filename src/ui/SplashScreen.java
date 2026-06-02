package ui;

import util.SoundPlayer;

import javax.swing.*;
import java.awt.*;

public class SplashScreen extends JWindow {

    public SplashScreen() {

        // Full screen center dialog
        setSize(500, 300);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel();
        panel.setBackground(new Color(0x36, 0x65, 0x00));
        panel.setLayout(new BorderLayout());

        // LOGO
        JLabel logo = new JLabel();
        logo.setHorizontalAlignment(SwingConstants.CENTER);

        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/assets/ufone_logo.png"));
            Image img = icon.getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH);
            logo.setIcon(new ImageIcon(img));
        } catch (Exception e) {
            logo.setText("UFONE");
            logo.setFont(new Font("Segoe UI", Font.BOLD, 30));
            logo.setForeground(Color.WHITE);
        }

        // TEXT
        JLabel text = new JLabel("UFONE NETWORK CONTROL SYSTEM", SwingConstants.CENTER);
        text.setFont(new Font("Segoe UI", Font.BOLD, 16));
        text.setForeground(Color.WHITE);

        JLabel sub = new JLabel("Initializing Telecom Dashboard...", SwingConstants.CENTER);
        sub.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        sub.setForeground(new Color(0xAB, 0xE3, 0x00));

        JPanel bottom = new JPanel(new GridLayout(2,1));
        bottom.setBackground(new Color(0x36, 0x65, 0x00));
        bottom.add(text);
        bottom.add(sub);

        panel.add(logo, BorderLayout.CENTER);
        panel.add(bottom, BorderLayout.SOUTH);

        setContentPane(panel);
    }

    public void showSplash() {

    setVisible(true);

    // play sound
    SoundPlayer.playIntro();

    // delay without freezing UI
    new javax.swing.Timer(5000, e -> {
        ((javax.swing.Timer) e.getSource()).stop();

        setVisible(false);
        dispose();

        new MainFrame(); // launch main app

    }).start();
}
}