/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package pe.edu.utp.rudypollo.dao.impl;

/**
 *
 * @author Vinz
 */
import pe.edu.utp.rudypollo.dao.PlatoDAO;
import pe.edu.utp.rudypollo.model.Plato;
import pe.edu.utp.rudypollo.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PlatoDAOImpl implements PlatoDAO {

    @Override
    public void save(Plato plato) throws SQLException {
        String sql = "INSERT INTO platos (nombre, precio, stock, descripcion) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, plato.getNombre());
            stmt.setDouble(2, plato.getPrecio());
            stmt.setInt(3, plato.getStock());
            stmt.setString(4, plato.getDescripcion());
            stmt.executeUpdate();
        }
    }

    @Override
    public void update(Plato plato) throws SQLException {
        String sql = "UPDATE platos SET nombre=?, precio=?, stock=?, descripcion=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, plato.getNombre());
            stmt.setDouble(2, plato.getPrecio());
            stmt.setInt(3, plato.getStock());
            stmt.setString(4, plato.getDescripcion());
            stmt.setInt(5, plato.getId());
            stmt.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM platos WHERE id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    @Override
    public Plato findById(int id) throws SQLException {
        String sql = "SELECT * FROM platos WHERE id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Plato p = new Plato();
                    p.setId(rs.getInt("id"));
                    p.setNombre(rs.getString("nombre"));
                    p.setPrecio(rs.getDouble("precio"));
                    p.setStock(rs.getInt("stock"));
                    p.setDescripcion(rs.getString("descripcion"));
                    return p;
                }
            }
        }
        return null; // si no encuentra nada
    }

    @Override
    public List<Plato> findAll() throws SQLException {
        List<Plato> lista = new ArrayList<>();
        String sql = "SELECT * FROM platos";

        try (Connection conn = DBConnection.getConnection(); Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Plato p = new Plato();
                p.setId(rs.getInt("id"));
                p.setNombre(rs.getString("nombre"));
                p.setPrecio(rs.getDouble("precio"));
                p.setStock(rs.getInt("stock"));
                p.setDescripcion(rs.getString("descripcion"));
                lista.add(p);
            }
        }
        return lista;
    }

    @Override
    public List<Plato> findByNameLike(String nombre) throws SQLException {
        List<Plato> lista = new ArrayList<>();
        String sql = "SELECT * FROM platos WHERE LOWER(nombre) LIKE ?";

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, "%" + nombre.toLowerCase() + "%");
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Plato p = new Plato();
                    p.setId(rs.getInt("id"));
                    p.setNombre(rs.getString("nombre"));
                    p.setPrecio(rs.getDouble("precio"));
                    p.setStock(rs.getInt("stock"));
                    p.setDescripcion(rs.getString("descripcion"));
                    lista.add(p);
                }
            }
        }
        return lista;
    }

    public int calcularStockDisponible(int idPlato) throws SQLException {
        String sql = """
        SELECT FLOOR(MIN(i.cantidad / r.cantidad_necesaria)) AS stock_disponible
        FROM receta r
        JOIN ingredientes i ON r.id_ingrediente = i.id
        WHERE r.id_plato = ?
    """;

        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, idPlato);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stock_disponible");
                }
            }
        }
        return 0;
    }

    public void actualizarStockPlato(int idPlato) throws SQLException {
        int nuevoStock = calcularStockDisponible(idPlato);
        String sql = "UPDATE platos SET stock=? WHERE id=?";
        try (Connection conn = DBConnection.getConnection(); PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, nuevoStock);
            stmt.setInt(2, idPlato);
            stmt.executeUpdate();
        }
    }

    public void descontarIngredientesPorVenta(int idPlato, int cantidadVendida, Connection conn) throws SQLException {
        String sql = """
        SELECT i.id AS id_ingrediente, i.cantidad AS cantidad_actual, 
               r.cantidad_necesaria * ? AS cantidad_a_descontar
        FROM receta r
        JOIN ingredientes i ON r.id_ingrediente = i.id
        WHERE r.id_plato = ?
    """;

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setInt(1, cantidadVendida);
            stmt.setInt(2, idPlato);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int idIngrediente = rs.getInt("id_ingrediente");
                    double cantidadActual = rs.getDouble("cantidad_actual");
                    double cantidadADescontar = rs.getDouble("cantidad_a_descontar");

                    double nuevaCantidad = cantidadActual - cantidadADescontar;
                    if (nuevaCantidad < 0) {
                        nuevaCantidad = 0;
                    }

                    try (PreparedStatement updateStmt = conn.prepareStatement(
                            "UPDATE ingredientes SET cantidad=? WHERE id=?")) {
                        updateStmt.setDouble(1, nuevaCantidad);
                        updateStmt.setInt(2, idIngrediente);
                        updateStmt.executeUpdate();
                    }
                }
            }
        }
    }

}
