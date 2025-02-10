/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package GameOfLife;

/**
 *
 * @author Alexis
 */
import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.net.*;

public class JuegoCliente extends JPanel implements Runnable {
    private static final int CELL_SIZE = 5; // Tamaño de cada celda
    private boolean[][] generation;
    private Socket socket;
    private ObjectOutputStream out;
    private ObjectInputStream in;

    public JuegoCliente(ObjectOutputStream out, ObjectInputStream in) {
            this.out = out;
            this.in = in;
    }

    @Override
    public void run() {
        try {
            while (true) {
                // Solicita la siguiente generación
                out.writeObject("GET_NEXT_GENERATION");
                out.flush();

                // Recibe la siguiente generación del servidor
                generation = (boolean[][]) in.readObject();

                // Redibuja la interfaz
                repaint();
                Thread.sleep(500); // Controla la velocidad de actualización
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (generation == null) return;

        for (int row = 0; row < generation.length; row++) {
            for (int col = 0; col < generation[row].length; col++) {
                if (generation[row][col]) {
                    g.setColor(Color.BLACK);
                } else {
                    g.setColor(Color.WHITE);
                }
                g.fillRect(col * CELL_SIZE, row * CELL_SIZE, CELL_SIZE, CELL_SIZE);
            }
        }
    }
}
