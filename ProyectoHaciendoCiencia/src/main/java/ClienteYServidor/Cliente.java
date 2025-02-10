/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ClienteYServidor;

/**
 *
 * @author Alexis
 */

import BaseDeDatos.Alumno;
import BaseDeDatos.VentanaAlumnos;
import GameOfLife.GameOfLifeClient;
import java.awt.BorderLayout;
import javax.swing.*;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

import javax.imageio.ImageIO;
import javax.swing.table.DefaultTableModel;

public class Cliente{
    private static final String SERVER_ADDRESS = "localhost";
    private static final int SERVER_PORT = 12345;

    private JTable historyTable;
    private DefaultTableModel tableModel;
    private JComboBox<String> comboBox; // Atributo para el JComboBox
    private JComboBox<String> comboBox2; // Atributo para el JComboBox

    public static void main(String[] args) {
        SwingUtilities.invokeLater(Cliente::new); //cuando se ejecuta el cliente se crea la ventana que
        //permite la comunicación con el servidor
    }

    public Cliente() {//constructor de la clase, crea la ventana del cliente, con la tabla de los tiempos
        // de ejecución, y los botones que mandan peticiones al servidor
        JFrame frame = new JFrame("Cliente");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BorderLayout());

        // Crear tabla para mostrar el historial
        String[] columnNames = {"Programa", "Tiempo de Ejecución (ms)"};
        tableModel = new DefaultTableModel(columnNames, 0);
        historyTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(historyTable);
        frame.add(scrollPane, BorderLayout.CENTER);
        
        // Crear un panel principal para los botones y el JComboBox
        JPanel buttonPanelContainer = new JPanel();
        buttonPanelContainer.setLayout(new BoxLayout(buttonPanelContainer, BoxLayout.Y_AXIS)); // Usar BoxLayout para apilar verticalmente

        // Botones para ejecutar diferentes programas
        JPanel buttonPanel1 = new JPanel();
        JPanel buttonPanel2 = new JPanel();
        String[] buttonLabels = {"Mandelbrot Concurrente", "Mandelbrot Paralelo", "Mandelbrot Secuencial", "Juego de la Vida", "Matriz Concurrente"};
        for (String label : buttonLabels) {
            JButton button = new JButton(label);
            button.addActionListener(new ButtonClickListener());
            buttonPanel1.add(button);
        }
        String[] nombreBotones = {"Matriz Paralelo", "Matriz Secuencial","Lista Alumnos", "Pi Secuencial", "Pi Concurrente", "Pi Paralelo"};
        for (String label : nombreBotones) {
            JButton button = new JButton(label);
            button.addActionListener(new ButtonClickListener());
            buttonPanel2.add(button);
        }
        
        // Crear JComboBox con las opciones Matriz
        String[] opciones = {"10000", "15000", "20000"};
        this.comboBox = new JComboBox<>(opciones);
        
        // Crear JComboBox con las opciones Pi
        String[] numPi = {"1000", "5000", "10000"};
        this.comboBox2 = new JComboBox<>(numPi);
        
        // Crear JLabel para la etiqueta
        JLabel label = new JLabel("Tamaño NxN de la matriz:");

        // Añadir el JLabel y el JComboBox a un panel
        JPanel comboBoxPanel = new JPanel();
        comboBoxPanel.add(label);  // Añadir la etiqueta al panel
        comboBoxPanel.add(comboBox); // Añadir el JComboBox al mismo panel
        
        // Crear JLabel para la etiqueta
        JLabel label2 = new JLabel("Cantidad de decimales de Pi:");

        // Añadir el JLabel y el JComboBox a un panel
        JPanel comboBoxPanel2 = new JPanel();
        comboBoxPanel2.add(label2);  // Añadir la etiqueta al panel
        comboBoxPanel2.add(comboBox2); // Añadir el JComboBox al mismo panel

        // Añadir ambos paneles y el combo box al contenedor principal
        buttonPanelContainer.add(buttonPanel1);
        buttonPanelContainer.add(buttonPanel2);
        
        // Añadir el panel del combo box al contenedor principal
        buttonPanelContainer.add(comboBoxPanel);
        buttonPanelContainer.add(comboBoxPanel2);
        
        // Añadir ambos paneles al contenedor principal
        buttonPanelContainer.add(buttonPanel1);
        buttonPanelContainer.add(buttonPanel2);
        
        // Añadir el contenedor de botones al marco
        frame.add(buttonPanelContainer, BorderLayout.SOUTH);
    
        frame.setSize(800, 400);
        frame.setVisible(true);
    }

    private class ButtonClickListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            String comando = e.getActionCommand().toLowerCase().replace(" ", "_");
            if(comando.startsWith("matriz")){
                ejecutarPeticionMatriz(comando);
                return;
            }else if(comando.equals("juego_de_la_vida")){
                
                new Thread(() -> {
                        juegoVida();
                }).start();
                return;
            }else if(comando.startsWith("pi")){
                ejecutarPeticionPi(comando);
                return;
            }
            ejecutarPeticion(comando);//Se establece el formato necesario de cada peticion, ya que 
            //el servidor trabaja según la petición que reciba
        }
    }
    
    // Método para recuperar el valor seleccionado del JComboBox de las matrices
    private String getSelectedComboValue() {
        return (String) comboBox.getSelectedItem(); // Recupera el valor seleccionado como String
    }
    
    // Método para recuperar el valor seleccionado del JComboBox 2, de la cantidad de decimales de Pi
    private String getSelectedComboValue2() {
        System.out.println(comboBox2.getSelectedItem());
        return (String) comboBox2.getSelectedItem(); // Recupera el valor seleccionado como String
    }
    
    private void ejecutarPeticionMatriz(String command){//Solo para las opciones de las matrices, ya que en
        //ellas se especifica el tamaño
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT); //Se crea un socket cliente
             //Se recuperan los objetos "out" e "in", que permiten la comunicación entre el cliente y el servidor
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true); 
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            //Se le envia al servidor la petición
            out.println(command + "_" + getSelectedComboValue());
            String response;
            while ((response = in.readLine()) != null) {
                manejarRespuestaDelServidor(response, socket); //Se procesa la respuesta del servidor
            }

        } catch (IOException ex) {
            System.out.println("Error al crear el socket del cliente para comunicarse con el servidor");
        }
    }
    
    private void ejecutarPeticionPi(String command){//Solo para las opciones del calculo de PI, ya que segun
        // el valor de su checkbox es como se va a ejecutar la operacion
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT); //Se crea un socket cliente
             //Se recuperan los objetos "out" e "in", que permiten la comunicación entre el cliente y el servidor
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true); 
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            //Se le envia al servidor la petición
            out.println(command + "_" + getSelectedComboValue2());
            String response;
            while ((response = in.readLine()) != null) {
                manejarRespuestaDelServidor(response, socket); //Se procesa la respuesta del servidor
            }

        } catch (IOException ex) {
            System.out.println("Error al crear el socket del cliente para comunicarse con el servidor");
        }
    }


    private void ejecutarPeticion(String command) { //Se procesa la petición  que el cliente quiere enviar
        //al servidor
        try (Socket socket = new Socket(SERVER_ADDRESS, SERVER_PORT); //Se crea un socket cliente
             //Se recuperan los objetos "out" e "in", que permiten la comunicación entre el cliente y el servidor
             PrintWriter out = new PrintWriter(socket.getOutputStream(), true); 
             BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
            //Se le envia al servidor la petición
            out.println(command);
            String response;
            while ((response = in.readLine()) != null) { //Esta linea es bloqueante, y se va a terminar el ciclo
                //while solo cuando el servidor cierre la conexion, en teoria es para recibir mas de un mensaje
                //aunque en varios usos estoy recibiendo una sola respuesta, pero bueno, esa es la explicacion de 
                //esta parte
                manejarRespuestaDelServidor(response, socket); //Se procesa la respuesta del servidor
            }
            System.out.println("Termino soy el socket: "  + command);

        } catch (IOException ex) {
            System.out.println("Error al crear el socket del cliente para comunicarse con el servidor");
        }
    }

    private void manejarRespuestaDelServidor(String response, Socket socket) throws IOException {
        //String[] parts = response.split(","); //La respuesta del servidor se envia con un formato separado
        //por comas, según sean los elementos enviados del servidor es como se maneja la respuesta
        String[] parts = response.substring(5).split(","); // Remueve "META:"
        
        if (response.startsWith("META:")) {
            
                if (parts[0].equals("lista_alumnos")) {
                    int cantidadAlumnos = Integer.parseInt(parts[1]);
                    List<Alumno> listaAlumnos = new ArrayList<>();

                    // Recibir los datos de los alumnos
                    BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                    for (int i = 0; i < cantidadAlumnos; i++) {
                        String linea = in.readLine();
                        String[] datosAlumno = linea.split(",");
                        int matricula = Integer.parseInt(datosAlumno[0]);
                        String nombre = datosAlumno[1];
                        double promedio = Double.parseDouble(datosAlumno[2]);
                        listaAlumnos.add(new Alumno(matricula, nombre, promedio));
                    }

                    new Thread(() -> {
                        alumnos(listaAlumnos);
                    }).start();
                    
                    return;
                }
            
            if (parts.length == 2) { //Si solo son dos elementos entonces unicamente se recibe el nombre del 
                //método ejecutado por el servidor y el tiempo de ejecución para luego agregarlo a la tabla
                String requestType = parts[0];
                String executionTime = parts[1];
                agregarFilaTabla(requestType, executionTime);
            } 
        
            if (parts.length == 3) {// [tipo, tiempo, longitud], si llegan 3 mensajes del servidor entonces
           //se envió una imagen, por lo que además de recibir el nombre y el tiempo de ejecución del 
           //método del servidor, se necesita el largo de la imagen que se va a recibir 
                String requestType = parts[0];
                String executionTime = parts[1];
                int imageLength = 0;
                try {
                    imageLength = Integer.parseInt(parts[2]);
                } catch (NumberFormatException e) {
                    System.out.println("Error al interpretar el largo de la imagen");
                    return;
                }

                agregarFilaTabla(requestType, executionTime);

                // Envía confirmación al servidor antes de recibir la imagen
                PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
                out.println("ACK_META");
                
                // Uso de FutureTask para recibir la imagen en segundo plano
                FutureTask<BufferedImage> futureImage = new FutureTask<>(new ImageReceiver(socket, imageLength));
                new Thread(futureImage).start();
                try {
                    BufferedImage image = futureImage.get();
                    if (image != null) {
                        mostrarImagen(image, requestType);
                    } else {
                        System.err.println("Error al recibir la imagen");
                    }
                } catch (InterruptedException | ExecutionException e) {
                    System.out.println("Error al esperar la imagen.");
                }
            }
        } else if(response.startsWith("Ejecutando")){
            System.out.println("response = " + response);
        }else{
            //System.out.println("Mensaje recibido no reconocido: " + response);
            System.out.println("Mensaje recibido no reconocido");
        }
    }
    
    private void mostrarImagen(BufferedImage image, String nombreMetodo) { //Método necesario para mostrar la imagen recibida por el
        ImageIcon imageIcon = new ImageIcon(image);
        JLabel label = new JLabel(imageIcon);
        JFrame frame = new JFrame(nombreMetodo);
        frame.add(label);
        frame.setSize(image.getWidth(), image.getHeight());
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setVisible(true);
    }

    private void agregarFilaTabla(String requestType, String executionTime) { //Método que agrega el nombre
        //del método ejecutado, y el tiempo que se demoró la ejecución del mismo
        tableModel.addRow(new Object[]{requestType, executionTime});
    }
    
    
    private static class ImageReceiver implements Callable<BufferedImage> {
        private final Socket socket;
        private final int imageLength;

        public ImageReceiver(Socket socket, int imageLength) {
            this.socket = socket;
            this.imageLength = imageLength;
        }

        @Override
        public BufferedImage call() throws Exception {
            try {
                byte[] imageBytes = new byte[imageLength];
                InputStream inputStream = socket.getInputStream();
                int totalBytesRead = 0;

                while (totalBytesRead < imageLength) {
                    int bytesRead = inputStream.read(imageBytes, totalBytesRead, imageLength - totalBytesRead);
                    if (bytesRead == -1) break;
                    totalBytesRead += bytesRead;
                }

                return ImageIO.read(new ByteArrayInputStream(imageBytes));
            } catch (IOException e) {
                System.out.println("Error al recibir la imagen desde el servidor");
                return null;
            }
        }
    }
    
    public static void juegoVida() {  
        new Thread(() -> {
            System.out.println("Ejecutando el juego de la Vida en una ventana separada.");
            JFrame frame = new JFrame("Juego de la Vida");
            GameOfLifeClient client = new GameOfLifeClient(); // Usa la clase del cliente del Juego de la Vida.
            frame.add(client);
            frame.setSize(600, 600);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Solo cierra la ventana del juego, no toda la app.
            frame.setVisible(true);
        }).start();
        
    }
    
    public static void alumnos(List <Alumno> listaAlumnos){
        // Crear y mostrar la ventana con la lista de alumnos
//                    SwingUtilities.invokeLater(() -> {
//                        VentanaAlumnos ventana = new VentanaAlumnos(listaAlumnos);
//                        ventana.setVisible(true);
//                    });
                    
            System.out.println("Listado de alumnos en una ventana separada.");
            JFrame frame = new JFrame("Listado de Alumnos");
            VentanaAlumnos ventana = new VentanaAlumnos(listaAlumnos); // Usa la clase del cliente del Juego de la Vida.
            frame.add(ventana);
            frame.setSize(600, 600);
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE); // Solo cierra la ventana del juego, no toda la app.
            frame.setVisible(true);
    }
    
}
