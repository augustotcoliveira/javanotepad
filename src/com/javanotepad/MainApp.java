package com.javanotepad;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class MainApp {

    public static void main(String[] args) {
        // Bloco try-catch para definir o Look and Feel Nimbus
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Se o Nimbus não estiver disponível, pode usar o padrão do sistema.
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        // Garante que a GUI seja criada na Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            MainFrame frame = new MainFrame();
            frame.setVisible(true);
        });
    }
}