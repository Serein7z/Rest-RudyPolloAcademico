/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.edu.utp.rudypollo.dao;

/**
 *
 * @author Vinz
 */
import pe.edu.utp.rudypollo.model.Repartidor;
import java.util.List;


public interface RepartidorDAO {
    void save(Repartidor r) throws Exception;
    void update(Repartidor r) throws Exception;
    void delete(int id) throws Exception;
    Repartidor findById(int id) throws Exception;
    List<Repartidor> findAll() throws Exception;
    List<Repartidor> findByNameLike(String nombre) throws Exception;
}
