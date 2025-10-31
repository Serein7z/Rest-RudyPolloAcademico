/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.edu.utp.rudypollo.dao;


import java.util.List;
import pe.edu.utp.rudypollo.model.Plato;

/**
 *
 * @author Vinz
 */
public interface PlatoDAO {
  // Guardar un nuevo plato
    void save(Plato plato) throws Exception;

    // Actualizar un plato existente
    void update(Plato plato) throws Exception;

    // Eliminar plato por ID
    void delete(int id) throws Exception;

    // Buscar un plato por ID
    Plato findById(int id) throws Exception;

    // Listar todos los platos
    List<Plato> findAll() throws Exception;

    //Nuevo Buscar platos cuyo nombre contenga un texto
    List<Plato> findByNameLike(String name) throws Exception;
}
