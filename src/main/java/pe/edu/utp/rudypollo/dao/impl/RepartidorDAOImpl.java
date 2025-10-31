package pe.edu.utp.rudypollo.dao.impl;

import pe.edu.utp.rudypollo.dao.RepartidorDAO;
import pe.edu.utp.rudypollo.model.Repartidor;
import pe.edu.utp.rudypollo.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepartidorDAOImpl implements RepartidorDAO {

    @Override
    public void save(Repartidor r) throws Exception {
        String sql = "INSERT INTO repartidores(nombre, telefono, estado) VALUES(?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, r.getNombre());
            stmt.setString(2, r.getTelefono());
            stmt.setString(3, r.getEstado());
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(Repartidor r) throws Exception {
        String sql = "UPDATE repartidores SET nombre=?, telefono=?, estado=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, r.getNombre());
            stmt.setString(2, r.getTelefono());
            stmt.setString(3, r.getEstado());
            stmt.setInt(4, r.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sql = "DELETE FROM repartidores WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public Repartidor findById(int id) throws Exception {
        String sql = "SELECT * FROM repartidores WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return new Repartidor(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("telefono"),
                            rs.getString("estado")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public List<Repartidor> findAll() throws Exception {
        List<Repartidor> lista = new ArrayList<>();
        String sql = "SELECT * FROM repartidores";
        try (Connection conn = DBConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(new Repartidor(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("estado")
                ));
            }
        }
        return lista;
    }

    @Override
    public List<Repartidor> findByNameLike(String nombre) throws Exception {
        List<Repartidor> lista = new ArrayList<>();
        String sql = "SELECT * FROM repartidores WHERE LOWER(nombre) LIKE ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, "%" + nombre.toLowerCase() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    lista.add(new Repartidor(
                            rs.getInt("id"),
                            rs.getString("nombre"),
                            rs.getString("telefono"),
                            rs.getString("estado")
                    ));
                }
            }
        }
        return lista;
    }


    public List<Repartidor> findDisponibles() throws Exception {
        List<Repartidor> lista = new ArrayList<>();
        String sql = "SELECT * FROM repartidores WHERE estado='DISPONIBLE'";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                lista.add(new Repartidor(
                        rs.getInt("id"),
                        rs.getString("nombre"),
                        rs.getString("telefono"),
                        rs.getString("estado")
                ));
            }
        }
        return lista;
    }

   
    public void updateEstado(int id, String estado) throws Exception {
        String sql = "UPDATE repartidores SET estado=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, estado);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        }
    }
}
