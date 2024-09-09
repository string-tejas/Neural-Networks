package com.tejas.ui;
import com.tejas.model.Model;
import com.tejas.neuralnetwork.Matrix;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class SoftBrushCanvas extends JPanel {

    private static final int GRID_SIZE = 28;
    private static final int CELL_SIZE = 15;
    private static final int BRUSH_RADIUS = 1;
    public final float[][] intensityGrid = new float[GRID_SIZE][GRID_SIZE];

    public SoftBrushCanvas(Model model, DigitForm digitForm) {
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                applyBrush(e.getX(), e.getY());
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                try {
                    Matrix act = model.predict(intensityGrid);
                    for (int i = 0; i < act.values.length; i++) {
                        digitForm.renderResults(act);
                    }

                } catch (Matrix.DimensionMismatchException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });

        addMouseMotionListener(new MouseAdapter() {
            public void mouseDragged(MouseEvent e) {
                applyBrush(e.getX(), e.getY());
            }
        });

    }

    private void applyBrush(int x, int y) {
        int centerRow = y / CELL_SIZE;
        int centerCol = x / CELL_SIZE;

        for (int row = centerRow - BRUSH_RADIUS; row <= centerRow + BRUSH_RADIUS; row++) {
            for (int col = centerCol - BRUSH_RADIUS; col <= centerCol + BRUSH_RADIUS; col++) {
                if (row >= 0 && row < GRID_SIZE && col >= 0 && col < GRID_SIZE) {
                    float distance = (float) Math.sqrt(Math.pow(centerRow - row, 2) + Math.pow(centerCol - col, 2));
                    float intensity = Math.max(0, 0.8f - (distance / (BRUSH_RADIUS + 1)));
                    intensity *= 0.8f;
                    intensityGrid[row][col] = Math.min(1.0f, intensityGrid[row][col] + intensity);
                }
            }
        }

        repaint();
    }

    public void clearCanvas() {
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                intensityGrid[row][col] = 0;
            }
        }

        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int row = 0; row < GRID_SIZE; row++) {
            for (int col = 0; col < GRID_SIZE; col++) {
                if (intensityGrid[row][col] > 0) {
                    int grayScale = (int) (255 * (intensityGrid[row][col]));
                    g.setColor(new Color(grayScale, grayScale, grayScale));
                } else {
                    g.setColor(Color.BLACK); // Default background color
                }
                g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
//                g.setColor(Color.GRAY); // Grid line color
//                g.drawRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE); // Draw grid lines
            }
        }
    }
}

