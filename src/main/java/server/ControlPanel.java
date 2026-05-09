package server;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

public class ControlPanel extends JPanel {
    private final JTextField portField;
    private final JButton startButton;
    private final JButton stopButton;

    public ControlPanel(Consumer<Integer> onStart, Runnable onStop) {
        setLayout(new FlowLayout());

        add(new JLabel("Порт:"));
        portField = new JTextField("12345", 8);
        add(portField);

        startButton = new JButton("Запустити сервер");
        stopButton = new JButton("Зупинити сервер");
        stopButton.setEnabled(false);

        startButton.addActionListener(e -> {
            try {
                int port = Integer.parseInt(portField.getText());
                onStart.accept(port);
                startButton.setEnabled(false);
                stopButton.setEnabled(true);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Невірний порт!");
            }
        });

        stopButton.addActionListener(e -> {
            onStop.run();
            startButton.setEnabled(true);
            stopButton.setEnabled(false);
        });

        add(startButton);
        add(stopButton);
    }

    public void setServerRunning(boolean running) {
        startButton.setEnabled(!running);
        stopButton.setEnabled(running);
    }
}