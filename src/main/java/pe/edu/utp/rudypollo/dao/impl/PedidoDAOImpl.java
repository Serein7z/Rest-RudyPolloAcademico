package pe.edu.utp.rudypollo.dao.impl;

import pe.edu.utp.rudypollo.dao.PedidoDAO;
import pe.edu.utp.rudypollo.model.Pedido;
import pe.edu.utp.rudypollo.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoDAOImpl implements PedidoDAO {

    private Connection getConnection() throws SQLException {
        return DBConnection.getConnection();
    }

    @Override
    public void save(Pedido pedido) throws Exception {
        String sql = "INSERT INTO pedidos (cliente_id, total, estado, assigned_repartidor_id, eta) VALUES (?,?,?,?,?)";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, pedido.getClienteId());
            ps.setDouble(2, pedido.getTotal());
            ps.setString(3, pedido.getEstado());
            if (pedido.getAssignedRepartidorId() != null) {
                ps.setInt(4, pedido.getAssignedRepartidorId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }
            if (pedido.getEta() != null) {
                ps.setInt(5, pedido.getEta());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    pedido.setId(rs.getInt(1));
                }
            }
        }
    }

    @Override
    public void update(Pedido pedido) throws Exception {
        String sql = "UPDATE pedidos SET cliente_id=?, total=?, estado=?, assigned_repartidor_id=?, eta=? WHERE id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, pedido.getClienteId());
            ps.setDouble(2, pedido.getTotal());
            ps.setString(3, pedido.getEstado());

            if (pedido.getAssignedRepartidorId() != null) {
                ps.setInt(4, pedido.getAssignedRepartidorId());
            } else {
                ps.setNull(4, Types.INTEGER);
            }

            if (pedido.getEta() != null) {
                ps.setInt(5, pedido.getEta());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            ps.setInt(6, pedido.getId());
            ps.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws Exception {
        String sqlSelectItems = "SELECT plato_id, cantidad FROM pedido_items WHERE pedido_id=?";
        String sqlUpdateStock = "UPDATE platos SET stock = stock + ? WHERE id=?";
        String sqlDeleteItems = "DELETE FROM pedido_items WHERE pedido_id=?";
        String sqlDeletePedido = "DELETE FROM pedidos WHERE id=?";

        try ( Connection con = getConnection()) {
            boolean prevAuto = con.getAutoCommit();
            con.setAutoCommit(false);

            try {
                //Devolver stock
                try ( PreparedStatement psSel = con.prepareStatement(sqlSelectItems)) {
                    psSel.setInt(1, id);
                    try ( ResultSet rs = psSel.executeQuery()) {
                        while (rs.next()) {
                            int platoId = rs.getInt("plato_id");
                            int cantidad = rs.getInt("cantidad");
                            try ( PreparedStatement psUpd = con.prepareStatement(sqlUpdateStock)) {
                                psUpd.setInt(1, cantidad);
                                psUpd.setInt(2, platoId);
                                psUpd.executeUpdate();
                            }
                        }
                    }
                }

                //Eliminar items
                try ( PreparedStatement psDelItems = con.prepareStatement(sqlDeleteItems)) {
                    psDelItems.setInt(1, id);
                    psDelItems.executeUpdate();
                }

                //Eliminar pedido
                try ( PreparedStatement psDelPedido = con.prepareStatement(sqlDeletePedido)) {
                    psDelPedido.setInt(1, id);
                    psDelPedido.executeUpdate();
                }

                con.commit();
            } catch (Exception ex) {
                con.rollback();
                throw ex;
            } finally {
                con.setAutoCommit(prevAuto);
            }
        }
    }


    @Override
    public Pedido findById(int id) throws Exception {
        String sql = "SELECT * FROM pedidos WHERE id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Pedido> findAll() throws Exception {
        String sql = "SELECT * FROM pedidos ORDER BY creado_at DESC";
        List<Pedido> lista = new ArrayList<>();
        try (Connection con = getConnection();
             Statement st = con.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapRow(rs));
            }
        }
        return lista;
    }

    @Override
    public List<Pedido> findByEstado(String estado) throws Exception {
        String sql = "SELECT * FROM pedidos WHERE estado=? ORDER BY creado_at DESC";
        List<Pedido> lista = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, estado);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public List<Pedido> findByClienteOrEstado(String filtro) throws Exception {
        String sql = "SELECT p.* FROM pedidos p " +
                     "JOIN clientes c ON p.cliente_id = c.id " +
                     "WHERE LOWER(c.nombre) LIKE ? OR LOWER(p.estado) LIKE ? " +
                     "ORDER BY p.creado_at DESC";
        List<Pedido> lista = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            String like = "%" + filtro.toLowerCase() + "%";
            ps.setString(1, like);
            ps.setString(2, like);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(mapRow(rs));
                }
            }
        }
        return lista;
    }

    @Override
    public void asignarRepartidor(int pedidoId, int repartidorId, int eta) throws Exception {
        String sql = "UPDATE pedidos SET assigned_repartidor_id=?, estado='ASIGNADO', eta=? WHERE id=?";
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, repartidorId);
            ps.setInt(2, eta);
            ps.setInt(3, pedidoId);
            ps.executeUpdate();
        }
    }

  
    public List<Object[]> findItemsByPedidoId(int pedidoId) throws Exception {
        String sql = "SELECT pi.plato_id, pl.nombre, pi.cantidad, pi.precio_unit " +
                     "FROM pedido_items pi " +
                     "JOIN platos pl ON pi.plato_id = pl.id " +
                     "WHERE pi.pedido_id=?";
        List<Object[]> items = new ArrayList<>();
        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new Object[]{
                            rs.getInt("plato_id"),
                            rs.getString("nombre"),
                            rs.getInt("cantidad"),
                            rs.getDouble("precio_unit")
                    });
                }
            }
        }
        return items;
    }

    
    public void deleteItemsByPedidoId(int pedidoId, Connection con) throws Exception {
        String sql = "DELETE FROM pedido_items WHERE pedido_id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            ps.executeUpdate();
        }
    }


    public void insertItem(int pedidoId, int platoId, int cantidad, double precioUnit, Connection con) throws Exception {
        String sql = "INSERT INTO pedido_items (pedido_id, plato_id, cantidad, precio_unit) VALUES (?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, pedidoId);
            ps.setInt(2, platoId);
            ps.setInt(3, cantidad);
            ps.setDouble(4, precioUnit);
            ps.executeUpdate();
        }
    }


    private Pedido mapRow(ResultSet rs) throws SQLException {
        Pedido p = new Pedido();
        p.setId(rs.getInt("id"));
        p.setClienteId(rs.getInt("cliente_id"));
        p.setTotal(rs.getDouble("total"));
        p.setEstado(rs.getString("estado"));
        p.setAssignedRepartidorId((Integer) rs.getObject("assigned_repartidor_id"));
        p.setEta((Integer) rs.getObject("eta"));
        p.setCreadoAt(rs.getTimestamp("creado_at"));
        return p;
    }
}


