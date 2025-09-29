package com.javanotepad;

import javax.swing.JPanel;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.util.Random;

public class AnimatedBackgroundPanel extends JPanel implements Runnable {

    // Enum para os padrões de animação
    public enum AnimationPattern {
        STARFIELD,
        MATRIX_RAIN
    }

    private Thread animator;
    private volatile boolean running = false;
    private final Random random = new Random();

    // Configurações da animação
    private AnimationPattern currentPattern = AnimationPattern.STARFIELD;
    private boolean userChangedColor = false; // flag para saber se o usuário alterou
    private Color animationColor = Color.WHITE; // branco para contrastar no fundo preto
    private int animationSpeed = 50; // delay em ms

    // Atributos para Starfield
    private int starCount = 800;
    private int[] starX, starY, starZ;

    // Atributos para Matrix Rain
    private int matrixFontSize = 16;
    private int[] drops;
    private final String matrixChars
            = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890@#$%^&*()";

    public AnimatedBackgroundPanel() {
        // Re-inicializa animações quando o painel tiver tamanho
        addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentResized(java.awt.event.ComponentEvent e) {
                initializeAnimations();
            }
        });
    }

    private void initializeAnimations() {
        int w = Math.max(1, getWidth());
        int h = Math.max(1, getHeight());

        // Inicializa Starfield
        starX = new int[starCount];
        starY = new int[starCount];
        starZ = new int[starCount];

        for (int i = 0; i < starCount; i++) {
            starX[i] = random.nextInt(w * 2) - w;
            starY[i] = random.nextInt(h * 2) - h;
            starZ[i] = random.nextInt(w) + 1; // evita divisão por zero
        }

        // Inicializa Matrix Rain
        int columns = Math.max(1, w / matrixFontSize);
        drops = new int[columns];
        for (int i = 0; i < drops.length; i++) {
            drops[i] = random.nextInt(h / matrixFontSize);
        }
    }

    // Inicia a thread de animação
    public void startAnimation() {
        if (animator == null || !running) {
            animator = new Thread(this);
            running = true;
            animator.start();
        }
    }

    // Para a thread de animação
    public void stopAnimation() {
        running = false;
    }

    @Override
    public void run() {
        while (running) {
            updateAnimation();
            repaint();
            try {
                Thread.sleep(animationSpeed);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                running = false;
            }
        }
    }

    private void updateAnimation() {
        if (currentPattern == AnimationPattern.STARFIELD) {
            updateStarfield();
        } else if (currentPattern == AnimationPattern.MATRIX_RAIN) {
            updateMatrixRain();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Se ainda não inicializou, inicia agora
        if (starX == null || starY == null || starZ == null || drops == null) {
            initializeAnimations();
        }

        // Fundo preto (reaproveitado no Matrix Rain com transparência)
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());

        switch (currentPattern) {
            case STARFIELD:
                drawStarfield(g2d);
                break;
            case MATRIX_RAIN:
                // fundo translucido
                g2d.setColor(new Color(0, 0, 0, 20));
                g2d.fillRect(0, 0, getWidth(), getHeight());
                drawMatrixRain(g2d);
                break;
        }
    }

    // --- STARFIELD ---
    private void updateStarfield() {
        if (starZ == null) {
            return;
        }

        for (int i = 0; i < starCount; i++) {
            starZ[i] -= 5;
            if (starZ[i] <= 0) {
                starZ[i] = Math.max(1, getWidth());
                starX[i] = random.nextInt(getWidth() * 2) - getWidth();
                starY[i] = random.nextInt(getHeight() * 2) - getHeight();
            }
        }
    }

    private void drawStarfield(Graphics2D g2d) {
        if (starX == null || starY == null || starZ == null) {
            return;
        }

        g2d.setColor(animationColor);
        g2d.translate(getWidth() / 2, getHeight() / 2);

        for (int i = 0; i < starCount; i++) {
            float sx = (float) starX[i] / starZ[i] * getWidth();
            float sy = (float) starY[i] / starZ[i] * getHeight();
            float r = Math.max(1, (float) (getWidth() - starZ[i]) / getWidth() * 6);
            g2d.fillOval((int) sx, (int) sy, (int) r, (int) r);
        }

        g2d.translate(-getWidth() / 2, -getHeight() / 2);
    }

    // --- MATRIX RAIN ---
    private void updateMatrixRain() {
        if (drops == null) {
            return;
        }

        for (int i = 0; i < drops.length; i++) {
            if (drops[i] * matrixFontSize > getHeight() && random.nextFloat() > 0.975) {
                drops[i] = 0;
            }
            drops[i]++;
        }
    }

    private void drawMatrixRain(Graphics2D g2d) {
        if (drops == null) {
            return;
        }

        g2d.setColor(animationColor);
        g2d.setFont(new Font("Monospaced", Font.PLAIN, matrixFontSize));

        for (int i = 0; i < drops.length; i++) {
            char randomChar = matrixChars.charAt(random.nextInt(matrixChars.length()));
            int x = i * matrixFontSize;
            int y = drops[i] * matrixFontSize;
            g2d.drawString(String.valueOf(randomChar), x, y);
        }
    }

    // --- CONTROLES ---
    public void setPattern(AnimationPattern pattern) {
        this.currentPattern = pattern;

        if (pattern == AnimationPattern.MATRIX_RAIN && !userChangedColor) {
            this.animationColor = Color.GREEN; // padrão matrix
        }
    }

    public void setAnimationColor(Color color) {
        this.animationColor = color;
        userChangedColor = true;
    }

    public void setAnimationSpeed(int speed) {
        if (speed == 0) {
            this.animationSpeed = 100; // Lenta
        }
        if (speed == 1) {
            this.animationSpeed = 50;  // Normal
        }
        if (speed == 2) {
            this.animationSpeed = 20;  // Rápida
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(800, 600);
    }
}
