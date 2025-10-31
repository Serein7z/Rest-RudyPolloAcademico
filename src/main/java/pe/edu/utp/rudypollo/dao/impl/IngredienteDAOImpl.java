package pe.edu.utp.rudypollo.dao.impl;

import pe.edu.utp.rudypollo.dao.IngredienteDAO;
import pe.edu.utp.rudypollo.model.Ingrediente;
import pe.edu.utp.rudypollo.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class IngredienteDAOImpl implements IngredienteDAO {

    @Override
    public void save(Ingrediente ingrediente) throws SQLException {
        String sql = "INSERT INTO ingredientes (nombre, cantidad, unidad) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ingrediente.getNombre());
            ps.setDouble(2, ingrediente.getCantidad());
            ps.setString(3, ingrediente.getUnidad());

            ps.executeUpdate();
        }
    }

    @Override
    public void update(Ingrediente ingrediente) throws SQLException {
        String sql = "UPDATE ingredientes SET nombre = ?, cantidad = ?, unidad = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, ingrediente.getNombre());
            ps.setDouble(2, ingrediente.getCantidad());
            ps.setString(3, ingrediente.getUnidad());
            ps.setInt(4, ingrediente.getId());

            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM ingredientes WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    @Override
    public Ingrediente findById(int id) throws SQLException {
        String sql = "SELECT * FROM ingredientes WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapIngrediente(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Ingrediente> findAll() throws SQLException {
        List<Ingrediente> lista = new ArrayList<>();
        String sql = "SELECT * FROM ingredientes ORDER BY id DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(mapIngrediente(rs));
            }
        }

        return lista;
    }

    @Override
    public List<Ingrediente> findByNameLike(String nombre) throws SQLException {
        List<Ingrediente> lista = new ArrayList<>();
        String sql = "SELECT * FROM ingredientes WHERE LOWER(nombre) LIKE ?";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, "%" + nombre.toLowerCase() + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapIngrediente(rs));
                }
            }
        }

        return lista;
    }

    // Método privado para mapear un ResultSet a un objeto Ingrediente
    private Ingrediente mapIngrediente(ResultSet rs) throws SQLException {
        Ingrediente ing = new Ingrediente();
        ing.setId(rs.getInt("id"));
        ing.setNombre(rs.getString("nombre"));
        ing.setCantidad(rs.getDouble("cantidad"));
        ing.setUnidad(rs.getString("unidad"));
        return ing;
    }
}
