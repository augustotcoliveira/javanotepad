package com.javanotepad;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

public class HelpDialog extends JDialog {

    public HelpDialog(Frame owner) {
        super(owner, "Ajuda", true); // true para modal
        initUI();
    }

    private void initUI() {
        setSize(450, 400);
        setLocationRelativeTo(getParent());
        setLayout(new BorderLayout());

        // Painel principal com margens
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));

        // 1. Imagem no topo
        try {
            ImageIcon helpIcon = new ImageIcon(getClass().getResource("/resources/images/help_image.png"));
            JLabel imageLabel = new JLabel(helpIcon);
            mainPanel.add(imageLabel, BorderLayout.NORTH);
        } catch (Exception e) {
            System.err.println("Imagem de ajuda não encontrada.");
        }
        
        // 2. Texto rolável no centro
        String helpText = "<html>"
            + "<h2>Guia Rápido da Aplicação</h2>"
            + "<p>Esta aplicação é um editor de texto simples com um fundo dinâmico e animado.</p>"
            + "<h3>Menu Arquivo</h3>"
            + "<ul>"
            + "<li><b>Abrir Arquivo:</b> Carrega um arquivo de texto (.txt) na área de edição.</li>"
            + "<li><b>Fechar Arquivo:</b> Limpa a área de edição.</li>"
            + "<li><b>Sair:</b> Encerra a aplicação.</li>"
            + "</ul>"
            + "<h3>Menu Configuração</h3>"
            + "<ul>"
            + "<li><b>Padrões:</b> Altera o tipo de animação do fundo (Starfield ou Matrix Rain).</li>"
            + "<li><b>Cores:</b> Abre um seletor para mudar a cor principal da animação.</li>"
            + "<li><b>Velocidade:</b> Ajusta a velocidade da animação (Lenta, Normal, Rápida).</li>"
            + "</ul>"
            + "<h3>Barra de Status</h3>"
            + "<p>Após abrir um arquivo, a barra de status na parte inferior da janela exibirá o nome do arquivo, o número de linhas e o número de palavras.</p>"
            + "</html>";
        
        JEditorPane editorPane = new JEditorPane("text/html", helpText);
        editorPane.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(editorPane);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // 3. Botão na parte inferior
        JButton closeButton = new JButton("Fechar");
        closeButton.addActionListener(e -> dispose()); // dispose() fecha o diálogo
        
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.add(closeButton);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        add(mainPanel);
    }
}