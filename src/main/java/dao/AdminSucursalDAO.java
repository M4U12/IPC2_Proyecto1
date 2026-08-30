package dao;

import dbconection.DBConection;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelos.Sucursal;
import modelos.Usuario;

public class AdminSucursalDAO {

    private DBConection conexionDB;

    public AdminSucursalDAO() {
        this.conexionDB = new DBConection();
    }

    public boolean agregarAdminASucursal(Usuario usuario, Sucursal sucursal) throws BDException {
        String query = "INSERT INTO admin_sucursal (id_usuario, id_sucursal) VALUES (?, ?)";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, usuario.getIdUsuario());
            ps.setInt(2, sucursal.getIdSucursal());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al asignar un administrador a la sucursal" + e.getMessage(), e);
        }
    }

    public boolean actualizarSucursalDeAdmin(int idUsuario, int idNuevaSucursal) throws BDException {
        String query = "UPDATE admin_sucursal SET id_sucursal = ? WHERE id_usuario = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idNuevaSucursal);
            ps.setInt(2, idUsuario);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al reasignar la sucursal del administrador: " + e.getMessage(), e);
        }
    }

    public List<Usuario> listarAdminPorSucursalActivos(int idSucursal) throws BDException {
        List<Usuario> listaAdmins = new ArrayList<>();

        String query = "SELECT u.id_usuario, u.dpi, u.nombre, u.telefono, u.estado FROM usuarios u "
                + "INNER JOIN admin_sucursal asignacion ON u.id_usuario = asignacion.id_usuario WHERE asignacion.id_sucursal = ? AND u.estado = TRUE";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Usuario admin = new Usuario();
                    admin.setIdUsuario(rs.getInt("id_usuario"));
                    admin.setDpi(rs.getString("dpi"));
                    admin.setNombre(rs.getString("nombre"));
                    admin.setTelefono(rs.getString("telefono"));
                    admin.setEstado(rs.getBoolean("estado"));

                    listaAdmins.add(admin);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al solicitar los administradores de la sucursal" + e.getMessage(), e);
        }
        return listaAdmins;
    }

    public List<Usuario> listarTodosAdmins(String filtro) throws BDException {
        List<Usuario> listaAdmins = new ArrayList<>();

        String query = "SELECT u.id_usuario, u.dpi, u.nombre, u.telefono, u.estado, asignacion.id_sucursal "
                + "FROM usuarios u "
                + "INNER JOIN admin_sucursal asignacion ON u.id_usuario = asignacion.id_usuario";

        if (filtro.equals("Activos")) {
            query += " WHERE u.estado = TRUE";
        } else if (filtro.equals("Inactivos")) {
            query += " WHERE u.estado = FALSE";
        }

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario admin = new Usuario();
                admin.setIdUsuario(rs.getInt("id_usuario"));
                admin.setDpi(rs.getString("dpi"));
                admin.setNombre(rs.getString("nombre"));
                admin.setTelefono(rs.getString("telefono"));
                admin.setEstado(rs.getBoolean("estado"));
                admin.setIdSucursalAsignada(rs.getInt("id_sucursal"));

                listaAdmins.add(admin);
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar los administradores: " + e.getMessage(), e);
        }

        return listaAdmins;
    }
}
