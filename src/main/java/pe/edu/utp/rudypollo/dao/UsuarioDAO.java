/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package pe.edu.utp.rudypollo.dao;

import java.sql.SQLException;
import pe.edu.utp.rudypollo.model.Usuario;

/**
 *
 * @author Vinz
 */
public interface UsuarioDAO {
    Usuario findByUsername(String username) throws Exception;
     void save(Usuario usuario) throws SQLException;
}
