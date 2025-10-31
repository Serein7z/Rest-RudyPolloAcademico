package pe.edu.utp.rudypollo.dao;

import pe.edu.utp.rudypollo.model.Ingrediente;

import java.sql.SQLException;
import java.util.List;

public interface IngredienteDAO {

    // Guarda un nuevo ingrediente
    void save(Ingrediente ingrediente) throws SQLException;

    // Actualiza un ingrediente existente
    void update(Ingrediente ingrediente) throws SQLException;

    // Elimina un ingrediente por su ID
    void delete(int id) throws SQLException;

    // Busca un ingrediente por ID
    Ingrediente findById(int id) throws SQLException;

    // Lista todos los ingredientes
    List<Ingrediente> findAll() throws SQLException;

    // Busca ingredientes por nombre (búsqueda parcial)
    List<Ingrediente> findByNameLike(String nombre) throws SQLException;
}
