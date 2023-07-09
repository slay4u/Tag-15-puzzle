package org.example;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Random;

public class Main extends JPanel {
    private static final Color FOREGROUND_COLOR = new Color(230, 80, 10);
    private final int size;
    private final int numberOfTiles;
    private final int[] tiles;
    private final int tileSize;
    private final int margin;
    private int blankPos;
    private boolean gameOver;

    public Main(int size) {
        this.size = size;
        int margin = 20;
        this.margin = margin;
        numberOfTiles = size * size - 1;
        tiles = new int[size * size];
        int dim = 450;
        int gridSize = (dim - 2 * margin);
        tileSize = gridSize / size;
        setPreferredSize(new Dimension(dim, dim + margin));
        setForeground(FOREGROUND_COLOR);
        setFont(new Font(Font.SANS_SERIF, Font.BOLD, 55));
        gameOver = true;
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                int ex = e.getX() - margin;
                int ey = e.getY() - margin;
                if (ex < 0 || ex > gridSize || ey < 0 || ey > gridSize) return;
                int c1 = ex / tileSize;
                int r1 = ey / tileSize;
                int c2 = blankPos % size;
                int r2 = blankPos / size;
                int clickPos = r1 * size + c1;
                int dir = 0;
                if (c1 == c2 && Math.abs(r1 - r2) > 0) dir = (r1 - r2) > 0 ? size : -size;
                else if (r1 == r2 && Math.abs(c1 - c2) > 0) dir = (c1 - c2) > 0 ? 1 : -1;
                if (dir != 0) {
                    do {
                        int newBlankPos = blankPos + dir;
                        tiles[blankPos] = tiles[newBlankPos];
                        blankPos = newBlankPos;
                    } while (blankPos != clickPos);
                    tiles[blankPos] = 0;
                }
                gameOver = isSolved();
                repaint();
            }
        });
        newGame();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            frame.setTitle("tag / 15 puzzle");
            frame.setResizable(false);
            frame.add(new Main(4), BorderLayout.CENTER);
            frame.pack();
            frame.setLocationRelativeTo(null);
            frame.setVisible(true);
        });
    }

    private void newGame() {
        do {
            reset();
            shuffle();
        } while (isNotSolvable());
        gameOver = false;
    }

    private void reset() {
        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = (i + 1) % tiles.length;
        }
        blankPos = tiles.length - 1;
    }

    private void shuffle() {
        int n = numberOfTiles;
        while (n > 1) {
            int r = new Random().nextInt(n--);
            int tmp = tiles[r];
            tiles[r] = tiles[n];
            tiles[n] = tmp;
        }
    }

    private boolean isNotSolvable() {
        int countInversions = 0;
        for (int i = 0; i < numberOfTiles; i++) {
            for (int j = 0; j < i; j++) {
                if (tiles[j] > tiles[i]) countInversions++;
            }
        }
        return countInversions % 2 != 0;
    }

    private boolean isSolved() {
        if (tiles[tiles.length - 1] != 0) return false;
        for (int i = numberOfTiles - 1; i >= 0; i--) {
            if (tiles[i] != i + 1) return false;
        }
        return true;
    }

    private void drawGrid(Graphics2D g) {
        for (int i = 0; i < tiles.length; i++) {
            int r = i / size;
            int c = i % size;
            int x = margin + c * tileSize;
            int y = margin + r * tileSize;
            if (tiles[i] == 0) {
                if (gameOver) {
                    g.setColor(FOREGROUND_COLOR);
                    drawCenteredString(g, "\u2713", x, y);
                }
                continue;
            }
            g.setColor(getForeground());
            g.fillRoundRect(x, y, tileSize, tileSize, 25, 25);
            g.setColor(Color.BLACK);
            g.drawRoundRect(x, y, tileSize, tileSize, 25, 25);
            g.setColor(Color.WHITE);
            drawCenteredString(g, String.valueOf(tiles[i]), x, y);
        }
    }

    private void drawEndMessage(Graphics2D g) {
        if (gameOver) {
            g.setFont(getFont().deriveFont(Font.BOLD, 20));
            g.setColor(FOREGROUND_COLOR);
            String s = "Puzzle solved!";
            g.drawString(s, (getWidth() - g.getFontMetrics().stringWidth(s)) / 2, getHeight() - margin);
        }
    }

    private void drawCenteredString(Graphics2D g, String s, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        int asc = fm.getAscent();
        int desc = fm.getDescent();
        g.drawString(s, x + (tileSize - fm.stringWidth(s)) / 2, y + (asc + (tileSize - (asc + desc)) / 2));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2D = (Graphics2D) g;
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawGrid(g2D);
        drawEndMessage(g2D);
    }
}