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

public class GameOfLifeClient extends JPanel {
    private static final int PORT = 12346;
    private static final String HOST = "localhost";
    private boolean[][] currentGeneration;

    public GameOfLifeClient() {
        currentGeneration = new boolean[100][100]; // Ajusta según el tamaño

        new Thread(() -> {
            try (Socket socket = new Socket(HOST, PORT);
                 ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
                 ObjectInputStream in = new ObjectInputStream(socket.getInputStream())) {

                while (true) {
                    out.writeObject("GET_NEXT_GENERATION");
                    currentGeneration = (boolean[][]) in.readObject();
                    repaint();
                    Thread.sleep(100); // Retardo para observar el juego
                }
            } catch (IOException | ClassNotFoundException | InterruptedException e) {
                e.printStackTrace();
            }
        }).start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int r = 0; r < currentGeneration.length; r++) {
            for (int c = 0; c < currentGeneration[r].length; c++) {
                g.setColor(currentGeneration[r][c] ? Color.BLACK : Color.WHITE);
                g.fillRect(c * 5, r * 5, 5, 5);
            }
        }
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Game of Life Client");
        GameOfLifeClient client = new GameOfLifeClient();
        frame.add(client);
        frame.setSize(600, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
