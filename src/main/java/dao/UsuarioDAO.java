package dao;

import dbconection.DBConection;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;
import modelos.Enums;
import modelos.Usuario;

public class UsuarioDAO {

    private DBConection conexionDB;

    public UsuarioDAO() {
        this.conexionDB = new DBConection();
    }

    public boolean actualizarUsuario(Usuario usuario) throws BDException {
        String query = "UPDATE usuarios SET dpi = ?, password = ?, nombre = ?, nit = ?, telefono = ?, direccion = ?, estado = ?, rol = ? WHERE id_usuario = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, usuario.getDpi());
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getNit());
            ps.setString(5, usuario.getTelefono());
            ps.setString(6, usuario.getDireccion());
            ps.setBoolean(7, usuario.isEstado());
            ps.setString(8, usuario.getRol().name());
            ps.setInt(9, usuario.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al actualizar los datos del usuario: " + e.getMessage(), e);
        }
    }

    public Optional <Usuario> buscarPorDpi(String dpi) throws BDException {
        Usuario usuario = null;
        String query = "SELECT id_usuario, dpi, password, nombre, nit, telefono, direccion, estado, rol FROM usuarios WHERE dpi = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, dpi);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setDpi(rs.getString("dpi"));
                    usuario.setPassword(rs.getString("password"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setNit(rs.getString("nit"));
                    usuario.setTelefono(rs.getString("telefono"));
                    usuario.setDireccion(rs.getString("direccion"));
                    usuario.setEstado(rs.getBoolean("estado"));
                    usuario.setRol(Enums.RolUsuario.valueOf(rs.getString("rol")));
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al buscar el usuario por DPI: " + e.getMessage(), e);
        }

        return Optional.ofNullable(usuario);
    }

    public boolean actualizarPassword(String dpi, String nuevaPassword) throws BDException {
        String query = "UPDATE usuarios SET password = ? WHERE dpi = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, nuevaPassword);
            ps.setString(2, dpi);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al actualizar la contraseña: " + e.getMessage(), e);
        }
    }

    public boolean crearUsuario(Usuario usuario) throws BDException {
        String queryUsuario = "INSERT INTO usuarios (dpi, password, nombre, nit, telefono, direccion, estado, rol) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        String queryBusqueda = "SELECT id_usuario FROM usuarios WHERE dpi = ?";
        String queryCartera = "INSERT INTO Cartera (id_usuario, cantidad_dinero) VALUES (?, 0.00)";
        
        Connection connection = null;

        try {
            connection = conexionDB.getConection();
            connection.setAutoCommit(false); 

            try (PreparedStatement psUsuario = connection.prepareStatement(queryUsuario);
                 PreparedStatement psBusqueda = connection.prepareStatement(queryBusqueda);
                 PreparedStatement psCartera = connection.prepareStatement(queryCartera)) {
                
                psUsuario.setString(1, usuario.getDpi());
                psUsuario.setString(2, usuario.getPassword());
                psUsuario.setString(3, usuario.getNombre());
                psUsuario.setString(4, usuario.getNit());
                psUsuario.setString(5, usuario.getTelefono());
                psUsuario.setString(6, usuario.getDireccion());
                psUsuario.setBoolean(7, usuario.isEstado());
                psUsuario.setString(8, usuario.getRol().name());
                psUsuario.executeUpdate();

                int idGenerado = 0;
                psBusqueda.setString(1, usuario.getDpi());
                
                try (ResultSet rs = psBusqueda.executeQuery()) {
                    if (rs.next()) {
                        idGenerado = rs.getInt("id_usuario");
                    } else {
                        throw new SQLException("Fallo al localizar el ID del usuario recién insertado.");
                    }
                }

                // id recuperado y crea la cartera
                psCartera.setInt(1, idGenerado);
                psCartera.executeUpdate();
                connection.commit();
                return true;
            }
        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback(); 
                } catch (SQLException exRollback) {
                    throw new BDException("Error crítico al revertir la transacción: " + exRollback.getMessage(), exRollback);
                }
            }
            throw new BDException("Error en el registro. Se canceló la creación del usuario y su cartera: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException eClose) {
                }
            }
        }
    }

    public boolean cambiarEstadoUsuario(String dpi, boolean nuevoEstado) throws BDException {
        String query = "UPDATE usuarios SET estado = ? WHERE dpi = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setBoolean(1, nuevoEstado);
            ps.setString(2, dpi);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al cambiar el estado del usuario: " + e.getMessage(), e);
        }
    }
    
    public boolean existeDpi(String dpi) throws BDException {
        String query = "SELECT 1 FROM usuarios WHERE dpi = ?";
        try (Connection connection = conexionDB.getConection(); 
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, dpi);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); 
            }
        } catch (SQLException e) {
            throw new BDException("Error al verificar el DPI: " + e.getMessage(), e);
        }
    }

    public boolean existeNit(String nit) throws BDException {
        String query = "SELECT 1 FROM usuarios WHERE nit = ?";
        try (Connection connection = conexionDB.getConection(); 
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, nit);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); 
            }
        } catch (SQLException e) {
            throw new BDException("Error al verificar el NIT: " + e.getMessage(), e);
        }
    }

    public boolean existeTelefono(String telefono) throws BDException {
        String query = "SELECT 1 FROM usuarios WHERE telefono = ?";
        try (Connection connection = conexionDB.getConection(); 
             PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, telefono);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next(); 
            }
        } catch (SQLException e) {
            throw new BDException("Error al verificar el teléfono: " + e.getMessage(), e);
        }
    }
}
