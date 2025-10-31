package pe.edu.utp.rudypollo.dao.impl;

import pe.edu.utp.rudypollo.dao.RecetaDAO;
import pe.edu.utp.rudypollo.model.Receta;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import pe.edu.utp.rudypollo.util.DBConnection;

public class RecetaDAOImpl implements RecetaDAO {

    @Override
    public List<Receta> findByIngredienteId(int idIngrediente) {
        List<Receta> lista = new ArrayList<>();
        String sql = "SELECT * FROM receta WHERE id_ingrediente = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idIngrediente);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Receta r = new Receta();
                r.setId(rs.getInt("id"));
                r.setIdPlato(rs.getInt("id_plato"));
                r.setIdIngrediente(rs.getInt("id_ingrediente"));
                r.setCantidadNecesaria(rs.getDouble("cantidad_necesaria"));
                lista.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    @Override
    public List<Receta> findByPlatoId(int idPlato) {
        List<Receta> lista = new ArrayList<>();
        String sql = "SELECT * FROM receta WHERE id_plato = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, idPlato);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Receta r = new Receta();
                r.setId(rs.getInt("id"));
                r.setIdPlato(rs.getInt("id_plato"));
                r.setIdIngrediente(rs.getInt("id_ingrediente"));
                r.setCantidadNecesaria(rs.getDouble("cantidad_necesaria"));
                lista.add(r);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }
}
