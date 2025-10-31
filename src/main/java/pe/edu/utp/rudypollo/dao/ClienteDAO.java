package pe.edu.utp.rudypollo.dao;

import pe.edu.utp.rudypollo.model.Cliente;
import java.sql.Connection;
import java.util.List;

public interface ClienteDAO {
    Cliente findById(int id) throws Exception;
    Cliente findByTelefono(String telefono) throws Exception;
    int save(Cliente cliente, Connection con) throws Exception; // insertar y devolver id
    void update(Cliente cliente, Connection con) throws Exception; // actualizar cliente
    Cliente findByNombre(String nombre) throws Exception;
    public int countClientes() throws Exception;
    public List<Object[]> topClientes(int limit) throws Exception;
    public List<Cliente> findAll() throws Exception;
}
