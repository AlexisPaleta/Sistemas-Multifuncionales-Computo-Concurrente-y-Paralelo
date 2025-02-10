/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BaseDeDatos;

/**
 *
 * @author Alexis
 */
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class VentanaAlumnos extends JPanel {

    private JTable tablaAlumnos;
    private DefaultTableModel modeloTabla;

    public VentanaAlumnos(List<Alumno> alumnos) {

        // Crear el modelo de la tabla
        modeloTabla = new DefaultTableModel();
        modeloTabla.addColumn("Matrícula");
        modeloTabla.addColumn("Nombre");
        modeloTabla.addColumn("Promedio");

        // Llenar el modelo con los datos de los alumnos
        for (Alumno alumno : alumnos) {
            Object[] fila = {alumno.getMatricula(), alumno.getNombre(), alumno.getPromedio()};
            modeloTabla.addRow(fila);
        }

        // Crear la tabla
        tablaAlumnos = new JTable(modeloTabla);
        tablaAlumnos.setFillsViewportHeight(true);

        // Agregar la tabla a un JScrollPane
        JScrollPane scrollPane = new JScrollPane(tablaAlumnos);
        
        // Configurar el layout y agregar el JScrollPane
        setLayout(new BorderLayout());
        add(scrollPane, BorderLayout.CENTER);
    }

    public static void main(String[] args) {
        // Recuperar los datos de los alumnos
        AlumnoDaoJDBC alumnoDao = new AlumnoDaoJDBC();
        List<Alumno> listaAlumnos = alumnoDao.listaClientes();

        // Crear y mostrar la ventana
        SwingUtilities.invokeLater(() -> {
            VentanaAlumnos ventana = new VentanaAlumnos(listaAlumnos);
            ventana.setVisible(true);
        });
    }
}
