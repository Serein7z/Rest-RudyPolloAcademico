package pe.edu.utp.rudypollo.dao;

import java.util.List;
import pe.edu.utp.rudypollo.model.Pedido;

public interface PedidoDAO {
    // Crear un pedido
    void save(Pedido pedido) throws Exception;

    // Actualizar pedido (cliente, total, estado, repartidor, eta)
    void update(Pedido pedido) throws Exception;

    // Eliminar pedido
    void delete(int id) throws Exception;

    // Buscar por ID
    Pedido findById(int id) throws Exception;

    // Listar todos los pedidos
    List<Pedido> findAll() throws Exception;

    // Buscar por estado exacto
    List<Pedido> findByEstado(String estado) throws Exception;

    // Buscar por cliente o estado (LIKE)
    List<Pedido> findByClienteOrEstado(String filtro) throws Exception;

    // Asignar repartidor y ETA
    void asignarRepartidor(int pedidoId, int repartidorId, int eta) throws Exception;
    
    
}