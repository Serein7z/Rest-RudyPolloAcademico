package pe.edu.utp.rudypollo.dao.impl;

import pe.edu.utp.rudypollo.dao.ClienteDAO;
import pe.edu.utp.rudypollo.model.Cliente;
import pe.edu.utp.rudypollo.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ClienteDAOImpl implements ClienteDAO {

    @Override
    public Cliente findById(int id) throws Exception {
        Connection con = DBConnection.getConnection();
        String sql = "SELECT * FROM clientes WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
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
public Cliente findByNombre(String nombre) throws Exception {
    Connection con = DBConnection.getConnection();
    String sql = "SELECT * FROM clientes WHERE LOWER(nombre)=LOWER(?) LIMIT 1";
    try (PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setString(1, nombre);
        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return mapRow(rs);
            }
        }
    }
    return null;
}

    

    @Override
    public Cliente findByTelefono(String telefono) throws Exception {
        Connection con = DBConnection.getConnection();
        String sql = "SELECT * FROM clientes WHERE telefono=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, telefono);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRow(rs);
                }
            }
        }
        return null;
    }

    @Override
    public int save(Cliente cliente, Connection con) throws Exception {
        String sql = "INSERT INTO clientes(nombre, direccion, telefono) VALUES (?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDireccion());
            ps.setString(3, cliente.getTelefono());
            ps.executeUpdate();
            try (ResultSet rs = ps.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        return -1;
    }

    @Override
    public void update(Cliente cliente, Connection con) throws Exception {
        String sql = "UPDATE clientes SET nombre=?, direccion=?, telefono=? WHERE id=?";
        try (PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, cliente.getNombre());
            ps.setString(2, cliente.getDireccion());
            ps.setString(3, cliente.getTelefono());
            ps.setInt(4, cliente.getId());
            ps.executeUpdate();
        }
    }

    // Método auxiliar para mapear resultados
    private Cliente mapRow(ResultSet rs) throws SQLException {
        return new Cliente(
            rs.getInt("id"),
            rs.getString("nombre"),
            rs.getString("direccion"),
            rs.getString("telefono")
        );
    }
    
    
    // Total de clientes registrados
    @Override
    public int countClientes() throws Exception {
    String sql = "SELECT COUNT(*) FROM clientes";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        if (rs.next()) return rs.getInt(1);
    }
    return 0;
}

// Top clientes con más pedidos
    @Override
    public List<Object[]> topClientes(int limit) throws Exception {
    List<Object[]> lista = new ArrayList<>();
    String sql = """
        SELECT c.nombre, COUNT(p.id) as total
        FROM clientes c
        LEFT JOIN pedidos p ON c.id = p.cliente_id
        GROUP BY c.id, c.nombre
        ORDER BY total DESC
        LIMIT ?
    """;
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql)) {
        ps.setInt(1, limit);
        try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(new Object[]{rs.getString("nombre"), rs.getInt("total")});
            }
        }
    }
    return lista;
}

    @Override
public List<Cliente> findAll() throws Exception {
    List<Cliente> lista = new ArrayList<>();
    String sql = "SELECT * FROM clientes";
    try (Connection con = DBConnection.getConnection();
         PreparedStatement ps = con.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
            lista.add(mapRow(rs));
        }
    }
    return lista;
}
}
