/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package BaseDeDatos;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Alexis
 */
public class AlumnoDaoJDBC {
    
    private static final String SQL_SELECT = "SELECT matricula, nombre, promedio" + " FROM alumno";
    
    public List<Alumno> listaClientes() {

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        Alumno alumno = null;
        List<Alumno> alumnos = new ArrayList<>();

        try {
            conn = Conexion.getConnection();
            stmt = conn.prepareStatement(SQL_SELECT);
            rs = stmt.executeQuery();

            while (rs.next()) {
                int matricula = rs.getInt("matricula");
                String nombre = rs.getString("nombre");
                double promedio = rs.getDouble("promedio");

                alumno = new Alumno(matricula, nombre, promedio);
                alumnos.add(alumno);//Se agrega el cliente a la lista
            }

        } catch (SQLException ex) {
            ex.printStackTrace(System.out);
        } finally {
            Conexion.close(rs);//Se cierran todas las conexiones creadas
            Conexion.close(stmt);
            Conexion.close(conn);//Se devuelve la conexion al pool
        }
        return alumnos;//Se regresa la lista
    }
    
}
