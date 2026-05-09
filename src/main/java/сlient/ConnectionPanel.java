package сlient;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

public class ConnectionPanel extends JPanel {
    private final JTextField ipField;
    private final JTextField portField;
    private final JButton connectButton;

    public ConnectionPanel(BiConsumer<String, Integer> onConnect) {
        setLayout(new FlowLayout());
        add(new JLabel("IP сервера: "));
        ipField = new JTextField("localhost", 12);
        add(ipField);
        add(new JLabel("Порт: "));
        portField = new JTextField("12345", 6);
        connectButton = new JButton("Підключитися");
        connectButton.addActionListener(event -> {
            try {
                final var port = Integer.parseInt(portField.getText());
                onConnect.accept(ipField.getText(), port);
            } catch (NumberFormatException exception) {
                JOptionPane.showMessageDialog(this, "Невірний порт!");
            }
        });
        add(connectButton);
    }

    public void setConnected(boolean connected) {
        connectButton.setEnabled(!connected);
    }
}
