/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.edu.utp.rudypollo.util;

/**
 *
 * @author Vinz
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

//SINGLETON INSTANCIA GLOBAL 
public class DBConnection {
     private static DBConnection instance;
    private Connection connection;

    private final String url = "jdbc:mysql://localhost:3306/rudypollo2?useSSL=false&serverTimezone=UTC";
    private final String user = "root";     
    private final String password = "123456789"; 

    private DBConnection() throws SQLException {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión inicial establecida con éxito.");
        } catch (ClassNotFoundException e) {
            throw new SQLException("Driver JDBC no encontrado.", e);
        }
    }

    public static synchronized Connection getConnection() throws SQLException {
        if (instance == null) {
            instance = new DBConnection();
        } else if (instance.connection == null || instance.connection.isClosed()) {
            // Si la conexión se cerró, la reabrimos
            instance.connection = DriverManager.getConnection(
                    instance.url, instance.user, instance.password);
            System.out.println("Conexión reabierta automáticamente.");
        }
        return instance.connection;
    }
}
