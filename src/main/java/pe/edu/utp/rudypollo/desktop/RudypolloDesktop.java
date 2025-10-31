/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Project/Maven2/JavaApp/src/main/java/${packagePath}/${mainClassName}.java to edit this template
 */

package pe.edu.utp.rudypollo.desktop;

/**
 *
 * @author Vinz
 */

import pe.edu.utp.rudypollo.view.MainFrame;
import pe.edu.utp.rudypollo.util.DBConnection;

import javax.swing.*;
import java.sql.Connection;
import java.sql.SQLException;
import pe.edu.utp.rudypollo.view.LoginFrame;

public class RudypolloDesktop {
       public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel("com.formdev.flatlaf.FlatLightLaf");
        } catch (Exception ex) {

        }

        try {
            Connection conn = DBConnection.getConnection();
            System.out.println("Conexión verificada (desde main).");
            SwingUtilities.invokeLater(() -> {
                LoginFrame login = new LoginFrame();
                login.setVisible(true);
            });
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(null,
                    "No se pudo conectar a la base de datos: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
}