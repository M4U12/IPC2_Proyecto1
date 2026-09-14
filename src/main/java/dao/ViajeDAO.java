package dao;

import dbconection.DBConection;
import excepciones.BDException;
import modelos.Viaje;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import modelos.Enums;

public class ViajeDAO {

    private DBConection conexionDB;

    public ViajeDAO() {
        this.conexionDB = new DBConection();
    }

    public boolean registrarViaje(Viaje viaje) throws BDException {
        String query = "INSERT INTO viajes (estado_viaje, id_bus, id_chofer, id_ruta, "
                + "fecha_hora_salida_estimada, fecha_hora_llegada_estimada, fecha_hora_salida_real, fecha_hora_llegada_real, "
                + "kilometraje_salida, kilometraje_llegada, gasto_combustible) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, viaje.getEstadoViaje().name());
            ps.setInt(2, viaje.getIdBus());
            ps.setInt(3, viaje.getIdChofer());
            ps.setInt(4, viaje.getIdRuta());

            if (viaje.getFechaHoraSalidaEstimada() != null) {
                ps.setTimestamp(5, Timestamp.valueOf(viaje.getFechaHoraSalidaEstimada()));
            } else {
                ps.setNull(5, Types.TIMESTAMP);
            }

            if (viaje.getFechaHoraLlegadaEstimada() != null) {
                ps.setTimestamp(6, Timestamp.valueOf(viaje.getFechaHoraLlegadaEstimada()));
            } else {
                ps.setNull(6, Types.TIMESTAMP);
            }

            if (viaje.getFechaHoraSalidaReal() != null) {
                ps.setTimestamp(7, Timestamp.valueOf(viaje.getFechaHoraSalidaReal()));
            } else {
                ps.setNull(7, Types.TIMESTAMP);
            }

            if (viaje.getFechaHoraLlegadaReal() != null) {
                ps.setTimestamp(8, Timestamp.valueOf(viaje.getFechaHoraLlegadaReal()));
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }

            if (viaje.getKilometrajeSalida() != null) {
                ps.setDouble(9, viaje.getKilometrajeSalida());
            } else {
                ps.setNull(9, Types.DOUBLE);
            }

            if (viaje.getKilometrajeLlegada() != null) {
                ps.setDouble(10, viaje.getKilometrajeLlegada());
            } else {
                ps.setNull(10, Types.DOUBLE);
            }

            if (viaje.getGastoCombustible() != null) {
                ps.setDouble(11, viaje.getGastoCombustible());
            } else {
                ps.setNull(11, Types.DOUBLE);
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al registrar el viaje: " + e.getMessage(), e);
        }
    }

    public List<Viaje> listarViajesRegularesPorSucursal(int idSucursal) throws BDException {
        List<Viaje> listaViajes = new ArrayList<>();
        String query = "SELECT v.* FROM viajes v "
                + "INNER JOIN rutas r ON v.id_ruta = r.id_ruta "
                + "WHERE r.id_origen = ? "
                + "ORDER BY v.fecha_hora_salida_estimada ASC";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idSucursal);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listaViajes.add(extraerViajeDeResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar los viajes de la sucursal: " + e.getMessage(), e);
        }
        return listaViajes;
    }

    public List<Viaje> listarViajesDisponibles() throws BDException {
        List<Viaje> lista = new ArrayList<>();
        String query = "SELECT * FROM viajes WHERE estado_viaje = 'PROGRAMADO' ORDER BY fecha_hora_salida_estimada ASC";

        try (java.sql.Connection connection = conexionDB.getConection(); java.sql.PreparedStatement ps = connection.prepareStatement(query); java.sql.ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                lista.add(extraerViajeDeResultSet(rs));
            }

        } catch (SQLException e) {
            throw new BDException("Error al cargar la cartelera de viajes: " + e.getMessage(), e);
        }
        return lista;
    }

    public boolean iniciarViaje(int idViaje, double kilometrajeSalida, LocalDateTime fechaHoraSalidaReal) throws BDException {
        String query = "UPDATE viajes SET estado_viaje = 'EN_CURSO', kilometraje_salida = ?, fecha_hora_salida_real = ? WHERE id_viaje = ?";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDouble(1, kilometrajeSalida);
            ps.setTimestamp(2, Timestamp.valueOf(fechaHoraSalidaReal));
            ps.setInt(3, idViaje);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BDException("Error al iniciar el viaje: " + e.getMessage(), e);
        }
    }

    public boolean finalizarViaje(int idViaje, double kilometrajeLlegada, double gastoCombustible, LocalDateTime fechaHoraLlegadaReal) throws BDException {
        String query = "UPDATE viajes SET estado_viaje = 'FINALIZADO', kilometraje_llegada = ?, gasto_combustible = ?, fecha_hora_llegada_real = ? WHERE id_viaje = ?";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDouble(1, kilometrajeLlegada);
            ps.setDouble(2, gastoCombustible);
            ps.setTimestamp(3, Timestamp.valueOf(fechaHoraLlegadaReal));
            ps.setInt(4, idViaje);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BDException("Error al finalizar el viaje: " + e.getMessage(), e);
        }
    }

    private Viaje extraerViajeDeResultSet(ResultSet rs) throws SQLException {
        Viaje viaje = new Viaje();
        viaje.setIdViaje(rs.getInt("id_viaje"));
        viaje.setEstadoViaje(Enums.EstadoViaje.valueOf(rs.getString("estado_viaje")));
        viaje.setIdBus(rs.getInt("id_bus"));
        viaje.setIdChofer(rs.getInt("id_chofer"));
        viaje.setIdRuta(rs.getInt("id_ruta"));

        Timestamp salidaEst = rs.getTimestamp("fecha_hora_salida_estimada");
        viaje.setFechaHoraSalidaEstimada(salidaEst != null ? salidaEst.toLocalDateTime() : null);

        Timestamp llegadaEst = rs.getTimestamp("fecha_hora_llegada_estimada");
        viaje.setFechaHoraLlegadaEstimada(llegadaEst != null ? llegadaEst.toLocalDateTime() : null);

        Timestamp salidaReal = rs.getTimestamp("fecha_hora_salida_real");
        viaje.setFechaHoraSalidaReal(salidaReal != null ? salidaReal.toLocalDateTime() : null);

        Timestamp llegadaReal = rs.getTimestamp("fecha_hora_llegada_real");
        viaje.setFechaHoraLlegadaReal(llegadaReal != null ? llegadaReal.toLocalDateTime() : null);

        double kmSalida = rs.getDouble("kilometraje_salida");
        viaje.setKilometrajeSalida(rs.wasNull() ? null : kmSalida);

        double kmLlegada = rs.getDouble("kilometraje_llegada");
        viaje.setKilometrajeLlegada(rs.wasNull() ? null : kmLlegada);

        double gasto = rs.getDouble("gasto_combustible");
        viaje.setGastoCombustible(rs.wasNull() ? null : gasto);

        return viaje;
    }

    public boolean eliminarViaje(int idViaje) throws BDException {
        try (Connection connection = conexionDB.getConection()) {

            // verificar estado del viaje y existencia de pagos
            String queryCheck = "SELECT v.estado_viaje, COUNT(b.id_boleto) AS boletos_vendidos "
                    + "FROM viajes v LEFT JOIN boletos b ON v.id_viaje = b.id_viaje "
                    + "WHERE v.id_viaje = ? GROUP BY v.estado_viaje";

            try (PreparedStatement psCheck = connection.prepareStatement(queryCheck)) {
                psCheck.setInt(1, idViaje);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        String estado = rs.getString("estado_viaje");
                        int boletosVendidos = rs.getInt("boletos_vendidos");

                        if (!estado.equals(Enums.EstadoViaje.PROGRAMADO.name())) {
                            throw new BDException("No se puede eliminar el viaje: ya fue iniciado, finalizado o cancelado.");
                        }
                        if (boletosVendidos > 0) {
                            throw new BDException("No se puede eliminar el viaje: ya tiene boletos pagados por clientes.");
                        }
                    } else {
                        throw new BDException("El viaje seleccionado no existe.");
                    }
                }
            }

            // se elimina
            String queryDelete = "DELETE FROM viajes WHERE id_viaje = ?";
            try (PreparedStatement psDelete = connection.prepareStatement(queryDelete)) {
                psDelete.setInt(1, idViaje);
                return psDelete.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            throw new BDException("Error en el proceso de eliminación del viaje: " + e.getMessage(), e);
        }
    }

    public boolean tieneViajesActivosPorBus(int idBus) throws BDException {
        String query = "SELECT COUNT(*) FROM viajes WHERE id_bus = ? AND estado_viaje IN ('PROGRAMADO', 'EN_CURSO')";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idBus);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (java.sql.SQLException e) {
            throw new BDException("Error al verificar viajes del bus: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean tieneViajesActivosPorChofer(int idChofer) throws BDException {
        String query = "SELECT COUNT(*) FROM viajes WHERE id_chofer = ? AND estado_viaje IN ('PROGRAMADO', 'EN_CURSO')";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idChofer);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al verificar viajes privados del chofer: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean actualizarViajeRegular(Viaje viaje) throws BDException {
        String query = "UPDATE viajes SET id_ruta = ?, id_bus = ?, id_chofer = ?, fecha_hora_salida_estimada = ?, fecha_hora_llegada_estimada = ? "
                + "WHERE id_viaje = ? AND estado_viaje = 'PROGRAMADO'";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, viaje.getIdRuta());
            ps.setInt(2, viaje.getIdBus());
            ps.setInt(3, viaje.getIdChofer());
            ps.setTimestamp(4, Timestamp.valueOf(viaje.getFechaHoraSalidaEstimada()));
            ps.setTimestamp(5, Timestamp.valueOf(viaje.getFechaHoraLlegadaEstimada()));
            ps.setInt(6, viaje.getIdViaje());
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BDException("Error al actualizar el viaje: " + e.getMessage(), e);
        }
    }

    public boolean cancelarViaje(int idViaje) throws BDException {
        String query = "UPDATE viajes SET estado_viaje = 'CANCELADO' WHERE id_viaje = ? AND estado_viaje = 'PROGRAMADO'";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idViaje);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BDException("Error al cancelar el viaje: " + e.getMessage(), e);
        }
    }

    public List<Viaje> listarViajesPorUsuario(int idUsuario) throws BDException {
        List<Viaje> lista = new ArrayList<>();
        String query = "SELECT * FROM viajes v INNER JOIN boletos b ON v.id_viaje = b.id_viaje WHERE b.id_usuario = ? ORDER BY v.fecha_hora_salida_estimada DESC";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    lista.add(extraerViajeDeResultSet(rs));
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar los viajes del usuario " + e, e);
        }
        return lista;
    }
}
