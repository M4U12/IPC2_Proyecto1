package dao;

import dbconection.DBConection;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import modelos.Bus;

public class BusDAO {

    private DBConection conexionDB;

    public BusDAO() {
        this.conexionDB = new DBConection();
    }

    public boolean agregarBus(Bus bus) throws BDException {
        String query = "INSERT INTO buses (id_sucursal, id_chofer, foto, placa, marca, modelo, anio_fabricacion, capacidad, estado_operativo, kilometraje_actual, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, bus.getIdSucursal());

            if (bus.getIdChofer() != null) {
                ps.setInt(2, bus.getIdChofer());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setString(3, bus.getFoto());
            ps.setString(4, bus.getPlaca());
            ps.setString(5, bus.getMarca());
            ps.setString(6, bus.getModelo());
            ps.setInt(7, bus.getAnioFabricacion());
            ps.setInt(8, bus.getCapacidad());
            ps.setString(9, bus.getEstadoOperativo());
            ps.setDouble(10, bus.getKilometrajeActual());
            ps.setBoolean(11, bus.isEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al registrar el bus: " + e.getMessage(), e);
        }
    }

    public List<Bus> listarBusesPorSucursal(int idSucursal, boolean soloActivos) throws BDException {
        List<Bus> listaBuses = new ArrayList<>();
        String query = "SELECT id_bus, id_sucursal, id_chofer, foto, placa, marca, modelo, anio_fabricacion, capacidad, estado_operativo, kilometraje_actual, estado FROM buses WHERE id_sucursal = ?";

        if (soloActivos) {
            query += " AND estado = TRUE";
        }

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int idChoferDB = rs.getInt("id_chofer");
                    Integer idChofer = rs.wasNull() ? null : idChoferDB;

                    Bus bus = new Bus(
                            rs.getInt("id_bus"),
                            rs.getInt("id_sucursal"),
                            idChofer,
                            rs.getString("foto"),
                            rs.getString("placa"),
                            rs.getString("marca"),
                            rs.getString("modelo"),
                            rs.getInt("anio_fabricacion"),
                            rs.getInt("capacidad"),
                            rs.getString("estado_operativo"),
                            rs.getDouble("kilometraje_actual"),
                            rs.getBoolean("estado")
                    );
                    listaBuses.add(bus);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar los buses: " + e.getMessage(), e);
        }

        return listaBuses;
    }

    public boolean actualizarBus(Bus bus) throws BDException {
        String query = "UPDATE buses SET id_sucursal = ?, id_chofer = ?, foto = ?, placa = ?, marca = ?, modelo = ?, anio_fabricacion = ?, capacidad = ?, estado_operativo = ?, kilometraje_actual = ? WHERE id_bus = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, bus.getIdSucursal());

            if (bus.getIdChofer() != null) {
                ps.setInt(2, bus.getIdChofer());
            } else {
                ps.setNull(2, Types.INTEGER);
            }

            ps.setString(3, bus.getFoto());
            ps.setString(4, bus.getPlaca());
            ps.setString(5, bus.getMarca());
            ps.setString(6, bus.getModelo());
            ps.setInt(7, bus.getAnioFabricacion());
            ps.setInt(8, bus.getCapacidad());
            ps.setString(9, bus.getEstadoOperativo());
            ps.setDouble(10, bus.getKilometrajeActual());
            ps.setInt(11, bus.getIdBus());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al actualizar el bus: " + e.getMessage(), e);
        }
    }

    public boolean cambiarEstadoBus(int idBus, boolean nuevoEstado) throws BDException {
        String query = "UPDATE buses SET estado = ? WHERE id_bus = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setBoolean(1, nuevoEstado);
            ps.setInt(2, idBus);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al cambiar el estado del bus: " + e.getMessage(), e);
        }
    }

    public boolean actualizarEstadoOperativoYKilometraje(int idBus, String estadoOperativo, double nuevoKilometraje) throws BDException {
        String query = "UPDATE buses SET estado_operativo = ?, kilometraje_actual = ? WHERE id_bus = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, estadoOperativo);
            ps.setDouble(2, nuevoKilometraje);
            ps.setInt(3, idBus);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al actualizar la operación del bus: " + e.getMessage(), e);
        }
    }
}
