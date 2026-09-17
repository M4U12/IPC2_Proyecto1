package dao;

import dbconection.DBConection;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import modelos.Sucursal;

public class SucursalDAO {

    private DBConection conexionDB;

    public SucursalDAO() {
        this.conexionDB = new DBConection();
    }

    public boolean agregarSucursal(Sucursal sucursal, int idAdmin) throws BDException {
        String querySucursal = "INSERT INTO sucursales (nombre, direccion, telefono, latitud, longitud) VALUES (?, ?, ?, ?, ?)";
        String queryAdmin = "INSERT INTO admin_sucursal (id_usuario, id_sucursal) VALUES (?, ?)";
        Connection connection = null;

        try {
            connection = conexionDB.getConection();
            connection.setAutoCommit(false);

            int idSucursalGenerado = -1;
            try (PreparedStatement psSucursal = connection.prepareStatement(querySucursal, PreparedStatement.RETURN_GENERATED_KEYS)) {
                psSucursal.setString(1, sucursal.getNombre());
                psSucursal.setString(2, sucursal.getDireccion());
                psSucursal.setString(3, sucursal.getTelefono());
                psSucursal.setDouble(4, sucursal.getLatitud());
                psSucursal.setDouble(5, sucursal.getLongitud());
                psSucursal.executeUpdate();

                try (ResultSet rs = psSucursal.getGeneratedKeys()) {
                    if (rs.next()) {
                        idSucursalGenerado = rs.getInt(1);
                    }
                }
            }

            if (idSucursalGenerado <= 0) {
                connection.rollback();
                return false;
            }

            try (PreparedStatement psAdmin = connection.prepareStatement(queryAdmin)) {
                psAdmin.setInt(1, idAdmin);
                psAdmin.setInt(2, idSucursalGenerado);
                psAdmin.executeUpdate();
            }

            connection.commit();
            return true;

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            throw new BDException("Error en la transacción al registrar sucursal: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public List<Sucursal> listarSucursales() throws BDException {
        List<Sucursal> listaSucursales = new ArrayList<>();
        String query = "SELECT * FROM sucursales";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sucursal s = new Sucursal();
                s.setIdSucursal(rs.getInt("id_sucursal"));
                s.setNombre(rs.getString("nombre"));
                s.setDireccion(rs.getString("direccion"));
                s.setTarifaBaseHora(rs.getDouble("tarifa_base_hora"));
                s.setTarifaPasajero(rs.getDouble("tarifa_pasajero"));

                listaSucursales.add(s);
            }

        } catch (SQLException e) {
            throw new BDException("Error al listar las sucursales: " + e.getMessage(), e);
        }

        return listaSucursales;
    }

    public boolean actualizarSucursal(Sucursal sucursal) throws BDException {
        String query = "UPDATE sucursales SET nombre = ?, direccion = ?, telefono = ?, latitud = ?, longitud = ? WHERE id_sucursal = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, sucursal.getNombre());
            ps.setString(2, sucursal.getDireccion());
            ps.setString(3, sucursal.getTelefono());
            ps.setDouble(4, sucursal.getLatitud());
            ps.setDouble(5, sucursal.getLongitud());
            ps.setInt(6, sucursal.getIdSucursal());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al actualizar la sucursal: " + e.getMessage(), e);
        }
    }

    public Optional<Sucursal> buscarSucursalPorId(int idSucursal) throws BDException {
        Sucursal sucursal = null;
        String query = "SELECT id_sucursal, nombre, direccion, telefono, latitud, longitud, tarifa_base_hora, tarifa_pasajero FROM sucursales WHERE id_sucursal = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sucursal = new Sucursal(
                            rs.getInt("id_sucursal"),
                            rs.getString("nombre"),
                            rs.getString("direccion"),
                            rs.getString("telefono"),
                            rs.getDouble("latitud"),
                            rs.getDouble("longitud")
                    );
                    sucursal.setTarifaBaseHora(rs.getDouble("tarifa_base_hora"));
                    sucursal.setTarifaPasajero(rs.getDouble("tarifa_pasajero"));
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al buscar la sucursal: " + e.getMessage(), e);
        }

        return Optional.ofNullable(sucursal);
    }

    public boolean existeTelefono(String telefono) throws BDException {
        String query = "SELECT 1 FROM sucursales WHERE telefono = ?";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, telefono);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new BDException("Error al verificar el teléfono de la sucursal: " + e.getMessage(), e);
        }
    }

    public boolean actualizarTarifas(int idSucursal, double tarifaBase, double tarifaPasajero) throws BDException {
        String query = "UPDATE sucursales SET tarifa_base_hora = ?, tarifa_pasajero = ? WHERE id_sucursal = ?";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDouble(1, tarifaBase);
            ps.setDouble(2, tarifaPasajero);
            ps.setInt(3, idSucursal);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BDException("Error al actualizar tarifas: " + e.getMessage(), e);
        }
    }
}
