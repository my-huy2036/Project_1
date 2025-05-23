package com.housemanagement;

import javax.swing.SwingUtilities;
import com.housemanagement.ui.LoginFrame;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new LoginFrame().setVisible(true);
        });
    }
}
