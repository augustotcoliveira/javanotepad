package com.javanotepad;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class MainFrame extends JFrame {

    private AnimatedBackgroundPanel animatedPanel;
    private JTextArea textArea;
    private JLabel statusBar;
    private String currentFileName = null;

    public MainFrame() {
        initUI();
    }

    private void initUI() {
        // Configuração da janela principal
        setTitle("Editor com Fundo Dinâmico");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Define o ícone da aplicação
        try {
            ImageIcon appIcon = new ImageIcon(getClass().getResource("/resources/icons/app_icon.png"));
            setIconImage(appIcon.getImage());
        } catch (Exception e) {
            System.err.println("o icone da aplicacao nao encontrado.");
        }

        // Criação do painel de fundo animado
        animatedPanel = new AnimatedBackgroundPanel();
        animatedPanel.setLayout(new BorderLayout());
        setContentPane(animatedPanel);

        // Criação da barra de menus
        setJMenuBar(createMenuBar());

        // Criação da área de texto com barra de rolagem
        textArea = new JTextArea();
        textArea.setFont(new Font("Consolas", Font.PLAIN, 14));
        textArea.setForeground(Color.WHITE); // Cor do texto para contrastar com o fundo preto
        textArea.setBackground(new Color(0, 0, 0, 128)); // Fundo semitransparente
        textArea.setCaretColor(Color.CYAN); // Cor do cursor
        textArea.setOpaque(false); // Permite ver o fundo animado
        textArea.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) {
                updateFileStats();
            }

            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) {
                updateFileStats();
            }

            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) {
                updateFileStats();
            }
        });

        JScrollPane scrollPane = new JScrollPane(textArea);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.setBorder(new EmptyBorder(10, 10, 10, 10)); // Margem interna

        animatedPanel.add(scrollPane, BorderLayout.CENTER);

        // Criação da barra de status
        statusBar = new JLabel("Pronto");
        statusBar.setForeground(Color.WHITE);
        statusBar.setBorder(new EmptyBorder(4, 8, 4, 8));
        animatedPanel.add(statusBar, BorderLayout.SOUTH);

        // Inicia a animação DEPOIS que a janela estiver pronta
        addWindowStateListener(e -> {
            if (e.getNewState() == Frame.NORMAL) {
                animatedPanel.startAnimation();
            }
        });

        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowOpened(java.awt.event.WindowEvent e) {
                animatedPanel.startAnimation();
            }

            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                animatedPanel.stopAnimation(); // Garante que a animação pare ao fechar
            }
        });
    }

    private JMenuBar createMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        // Menu Arquivo
        JMenu fileMenu = new JMenu("Arquivo");
        fileMenu.setMnemonic(KeyEvent.VK_A);

        JMenuItem openItem = new JMenuItem("Abrir Arquivo", loadIcon("open.png", 13, 13));
        openItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        openItem.addActionListener(e -> openFile());

        JMenuItem closeItem = new JMenuItem("Fechar Arquivo", loadIcon("close.png", 13, 13));
        closeItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, Toolkit.getDefaultToolkit().getMenuShortcutKeyMaskEx()));
        closeItem.addActionListener(e -> closeFile());

        JMenuItem exitItem = new JMenuItem("Sair", loadIcon("exit.png", 15, 15));
        exitItem.setMnemonic(KeyEvent.VK_S);
        exitItem.setToolTipText("Encerrar a aplicação");
        exitItem.addActionListener((event) -> System.exit(0));

        fileMenu.add(openItem);
        fileMenu.add(closeItem);
        fileMenu.addSeparator();
        fileMenu.add(exitItem);

        // Menu Configuração
        JMenu configMenu = new JMenu("Configuração");
        configMenu.setIcon(loadIcon("settings.png", 6, 6));

        JMenu patternsMenu = new JMenu("Padrões");
        patternsMenu.setIcon(loadIcon("pattern.png", 6, 6));
        ButtonGroup patternGroup = new ButtonGroup();

        JRadioButtonMenuItem starfieldItem = new JRadioButtonMenuItem("Starfield", true);
        starfieldItem.addActionListener(e -> animatedPanel.setPattern(AnimatedBackgroundPanel.AnimationPattern.STARFIELD));

        JRadioButtonMenuItem matrixItem = new JRadioButtonMenuItem("Matrix Rain");
        matrixItem.addActionListener(e -> animatedPanel.setPattern(AnimatedBackgroundPanel.AnimationPattern.MATRIX_RAIN));

        patternGroup.add(starfieldItem);
        patternGroup.add(matrixItem);
        patternsMenu.add(starfieldItem);
        patternsMenu.add(matrixItem);

        JMenuItem colorItem = new JMenuItem("Cores", loadIcon("color.png", 6, 6));
        colorItem.addActionListener(e -> chooseColor());

        JMenu speedMenu = new JMenu("Velocidade");
        speedMenu.setIcon(loadIcon("speed.png", 6, 6));
        ButtonGroup speedGroup = new ButtonGroup();

        JRadioButtonMenuItem slowItem = new JRadioButtonMenuItem("Lenta");
        slowItem.addActionListener(e -> animatedPanel.setAnimationSpeed(0));

        JRadioButtonMenuItem normalItem = new JRadioButtonMenuItem("Normal", true);
        normalItem.addActionListener(e -> animatedPanel.setAnimationSpeed(1));

        JRadioButtonMenuItem fastItem = new JRadioButtonMenuItem("Rápida");
        fastItem.addActionListener(e -> animatedPanel.setAnimationSpeed(2));

        speedGroup.add(slowItem);
        speedGroup.add(normalItem);
        speedGroup.add(fastItem);
        speedMenu.add(slowItem);
        speedMenu.add(normalItem);
        speedMenu.add(fastItem);

        configMenu.add(patternsMenu);
        configMenu.add(colorItem);
        configMenu.add(speedMenu);

        // Menu Ajuda
        JMenu helpMenu = new JMenu("Ajuda");

        JMenuItem helpItem = new JMenuItem("Ajuda", loadIcon("help.png", 6, 6));
        helpItem.addActionListener(e -> showHelpDialog());

        JMenuItem aboutItem = new JMenuItem("Sobre", loadIcon("about.png", 6, 6));
        aboutItem.addActionListener(e -> showAboutDialog());

        helpMenu.add(helpItem);
        helpMenu.add(aboutItem);

        menuBar.add(fileMenu);
        menuBar.add(configMenu);
        menuBar.add(helpMenu);

        return menuBar;
    }

    // --- Lógica das Ações ---
    private void openFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);

        if (result == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            
            StringBuilder sb = new StringBuilder();
            try (BufferedReader reader = new BufferedReader(new FileReader(selectedFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append(System.lineSeparator()); // Adiciona a linha e a quebra de linha do sistema
                }
                
                currentFileName = selectedFile.getName(); // Guarda o nome do arquivo
                textArea.setText(sb.toString()); // Usa setText() para modificar o documento existente
                
                // Move o cursor para o início do documento
                textArea.setCaretPosition(0);

            } catch (IOException ex) {
                JOptionPane.showMessageDialog(this,
                    "Erro ao ler o arquivo: " + ex.getMessage(),
                    "Erro de Arquivo",
                    JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void closeFile() {
        this.currentFileName = null;
        textArea.setText("");
        statusBar.setText("Pronto");
    }

    private void chooseColor() {
        Color newColor = JColorChooser.showDialog(this, "Escolha uma Cor para a Animação", Color.GREEN);
        if (newColor != null) {
            animatedPanel.setAnimationColor(newColor);
        }
    }

    private void showHelpDialog() {
        HelpDialog helpDialog = new HelpDialog(this);
        helpDialog.setVisible(true);
    }

    private void showAboutDialog() {
        ImageIcon icon = loadIcon("app_icon.png", 20, 20);
        JOptionPane.showMessageDialog(this,
                "Aplicação: Editor com Fundo Dinâmico\nVersão: 1.0\nAutor: [Seu Nome Aqui]",
                "Sobre",
                JOptionPane.INFORMATION_MESSAGE,
                icon);
    }

    private void updateFileStats() {
        String fileName = (this.currentFileName == null) ? "Novo Documento" : this.currentFileName;
        String content = textArea.getText();
        if (content.isEmpty()) {
            statusBar.setText("Arquivo: " + fileName);
            return;
        }

        // Calcula as estatísticas
        int lineCount = content.isEmpty() ? 0 : content.split("\r\n|\r|\n").length;
        int wordCount = content.trim().isEmpty() ? 0 : content.trim().split("\\s+").length;

        statusBar.setText(String.format("Arquivo: %s | Linhas: %d | Palavras: %d", fileName, lineCount, wordCount));
    }

    private ImageIcon loadIcon(String fileName, int width, int height) {
        try {
            ImageIcon icon = new ImageIcon(getClass().getResource("/resources/icons/" + fileName));
            Image img = icon.getImage();
            Image resized = img.getScaledInstance(width, height, Image.SCALE_SMOOTH);
            return new ImageIcon(resized);
        } catch (Exception e) {
            System.err.println("Ícone não encontrado: " + fileName);
            return null;
        }
    }
}
