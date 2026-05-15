package сlient;

import javax.swing.*;
import java.awt.*;
import java.util.function.BiConsumer;

public class BoardPanel extends JPanel {
    private final JButton[][] buttons;

    public BoardPanel(BiConsumer<Integer, Integer> moveHandler) {
        setLayout(new GridLayout(3, 3));
        buttons = new JButton[3][3];
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 3; j++) {
                buttons[i][j] = new JButton(" ");
                buttons[i][j].setFont(new Font("Arial", Font.BOLD, 60));
                final int row = i, col = j;
                buttons[i][j].addActionListener(e -> moveHandler.accept(row, col));
                buttons[i][j].setEnabled(false);
                add(buttons[i][j]);
            }
        }
    }

    public void updateBoard(String boardState, boolean myTurn, boolean gameActive) {
        SwingUtilities.invokeLater(() -> {
            for (int i = 0; i < 3; i++) {
                for (int j = 0; j < 3; j++) {
                    char c = boardState.charAt(i * 3 + j);
                    buttons[i][j].setText(String.valueOf(c));
                    boolean enabled = gameActive && myTurn && c == ' ';
                    buttons[i][j].setEnabled(enabled);
                }
            }
        });
    }

    public void enableAll(boolean enable) {
        SwingUtilities.invokeLater(() -> {
            for (JButton[] row : buttons) {
                for (JButton btn : row) {
                    btn.setEnabled(enable);
                }
            }
        });
    }
}
