package dao;

import dbconection.DBConection;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
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
            ps.setString(8, usuario.getRol());
            ps.setInt(9, usuario.getIdUsuario());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al actualizar los datos del usuario: " + e.getMessage(), e);
        }
    }

    public Usuario buscarPorDpi(String dpi) throws BDException {
        Usuario usuario = null;
        String query = "SELECT id_usuario, dpi, nombre, nit, telefono, direccion, estado, rol FROM usuarios WHERE dpi = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, dpi);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setIdUsuario(rs.getInt("id_usuario"));
                    usuario.setDpi(rs.getString("dpi"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setNit(rs.getString("nit"));
                    usuario.setTelefono(rs.getString("telefono"));
                    usuario.setDireccion(rs.getString("direccion"));
                    usuario.setEstado(rs.getBoolean("estado"));
                    usuario.setRol(rs.getString("rol"));
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al buscar el usuario por DPI: " + e.getMessage(), e);
        }

        return usuario;
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
        String query = "INSERT INTO usuarios (dpi, password, nombre, nit, telefono, direccion, estado, rol) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, usuario.getDpi());
            ps.setString(2, usuario.getPassword());
            ps.setString(3, usuario.getNombre());
            ps.setString(4, usuario.getNit());
            ps.setString(5, usuario.getTelefono());
            ps.setString(6, usuario.getDireccion());
            ps.setBoolean(7, usuario.isEstado());
            ps.setString(8, usuario.getRol());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al crear el usuario: " + e.getMessage(), e);
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
}
