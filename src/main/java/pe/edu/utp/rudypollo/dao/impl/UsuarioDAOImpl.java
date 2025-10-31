/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.edu.utp.rudypollo.dao.impl;

import pe.edu.utp.rudypollo.dao.UsuarioDAO;
import pe.edu.utp.rudypollo.model.Usuario;
import pe.edu.utp.rudypollo.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
/**
 *
 * @author Vinz
 */
public class UsuarioDAOImpl implements UsuarioDAO {

    @Override
    public Usuario findByUsername(String username) throws Exception {
        String sql = "SELECT u.id, u.username, u.password_hash, r.nombre AS role_name "
                + "FROM usuarios u LEFT JOIN roles r ON u.role_id = r.id "
                + "WHERE u.username = ?";
        Connection conn = DBConnection.getConnection();
        try ( PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, username);
            try ( ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Usuario u = new Usuario();
                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setPasswordHash(rs.getString("password_hash"));
                    u.setRoleName(rs.getString("role_name"));
                    return u;
                } else {
                    return null;
                }
            }
        }
    }

    @Override
    public void save(Usuario usuario) throws SQLException {
        String sql = "INSERT INTO usuarios (username, password_hash, role_id) "
                + "VALUES (?, ?, (SELECT id FROM roles WHERE nombre=?))";
        try ( Connection conn = DBConnection.getConnection();  PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, usuario.getUsername());
            ps.setString(2, usuario.getPasswordHash());
            ps.setString(3, usuario.getRoleName());
            ps.executeUpdate();
        }
    }
}
