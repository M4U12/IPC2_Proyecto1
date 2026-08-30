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
        String query = "INSERT INTO viajes (tipo_viaje, estado_viaje, id_bus, id_chofer, id_ruta, id_cliente, "
                + "origen_privado, destino_privado, cantidad_pasajeros_privado, precio_total_privado, fecha_retorno_privado, "
                + "fecha_hora_salida_estimada, fecha_hora_llegada_estimada, fecha_hora_salida_real, fecha_hora_llegada_real, "
                + "kilometraje_salida, kilometraje_llegada, gasto_combustible) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, viaje.getTipoViaje().name());
            ps.setString(2, viaje.getEstadoViaje().name());
            ps.setInt(3, viaje.getIdBus());
            ps.setInt(4, viaje.getIdChofer());

            if (viaje.getIdRuta() != null) {
                ps.setInt(5, viaje.getIdRuta());
            } else {
                ps.setNull(5, Types.INTEGER);
            }

            if (viaje.getIdCliente() != null) {
                ps.setInt(6, viaje.getIdCliente());
            } else {
                ps.setNull(6, Types.INTEGER);
            }

            ps.setString(7, viaje.getOrigenPrivado());
            ps.setString(8, viaje.getDestinoPrivado());

            if (viaje.getCantidadPasajerosPrivado() != null) {
                ps.setInt(9, viaje.getCantidadPasajerosPrivado());
            } else {
                ps.setNull(9, Types.INTEGER);
            }

            if (viaje.getPrecioTotalPrivado() != null) {
                ps.setDouble(10, viaje.getPrecioTotalPrivado());
            } else {
                ps.setNull(10, Types.DOUBLE);
            }

            if (viaje.getFechaRetornoPrivado() != null) {
                ps.setTimestamp(11, Timestamp.valueOf(viaje.getFechaRetornoPrivado()));
            } else {
                ps.setNull(11, Types.TIMESTAMP);
            }

            if (viaje.getFechaHoraSalidaEstimada() != null) {
                ps.setTimestamp(12, Timestamp.valueOf(viaje.getFechaHoraSalidaEstimada()));
            } else {
                ps.setNull(12, Types.TIMESTAMP);
            }

            if (viaje.getFechaHoraLlegadaEstimada() != null) {
                ps.setTimestamp(13, Timestamp.valueOf(viaje.getFechaHoraLlegadaEstimada()));
            } else {
                ps.setNull(13, Types.TIMESTAMP);
            }

            if (viaje.getFechaHoraSalidaReal() != null) {
                ps.setTimestamp(14, Timestamp.valueOf(viaje.getFechaHoraSalidaReal()));
            } else {
                ps.setNull(14, Types.TIMESTAMP);
            }

            if (viaje.getFechaHoraLlegadaReal() != null) {
                ps.setTimestamp(15, Timestamp.valueOf(viaje.getFechaHoraLlegadaReal()));
            } else {
                ps.setNull(15, Types.TIMESTAMP);
            }

            if (viaje.getKilometrajeSalida() != null) {
                ps.setDouble(16, viaje.getKilometrajeSalida());
            } else {
                ps.setNull(16, Types.DOUBLE);
            }

            if (viaje.getKilometrajeLlegada() != null) {
                ps.setDouble(17, viaje.getKilometrajeLlegada());
            } else {
                ps.setNull(17, Types.DOUBLE);
            }

            if (viaje.getGastoCombustible() != null) {
                ps.setDouble(18, viaje.getGastoCombustible());
            } else {
                ps.setNull(18, Types.DOUBLE);
            }

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al registrar el viaje: " + e.getMessage(), e);
        }
    }

    public List<Viaje> listarViajes(String estadoFiltro) throws BDException {
        List<Viaje> listaViajes = new ArrayList<>();
        String query = "SELECT * FROM viajes";

        if (estadoFiltro != null && !estadoFiltro.isEmpty()) {
            query += " WHERE estado_viaje = ?";
        }

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            if (estadoFiltro != null && !estadoFiltro.isEmpty()) {
                ps.setString(1, estadoFiltro);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Viaje viaje = extraerViajeDeResultSet(rs);
                    listaViajes.add(viaje);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar los viajes: " + e.getMessage(), e);
        }
        return listaViajes;
    }

    public boolean iniciarViaje(int idViaje, double kilometrajeSalida, LocalDateTime fechaHoraSalidaReal) throws BDException {
        String query = "UPDATE viajes SET estado_viaje = 'En Curso', kilometraje_salida = ?, fecha_hora_salida_real = ? WHERE id_viaje = ?";

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
        String query = "UPDATE viajes SET estado_viaje = 'Finalizado', kilometraje_llegada = ?, gasto_combustible = ?, fecha_hora_llegada_real = ? WHERE id_viaje = ?";

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
        viaje.setTipoViaje(Enums.TipoViaje.valueOf(rs.getString("tipo_viaje")));
        viaje.setEstadoViaje(Enums.EstadoViaje.valueOf(rs.getString("estado_viaje")));
        viaje.setIdBus(rs.getInt("id_bus"));
        viaje.setIdChofer(rs.getInt("id_chofer"));

        int idRuta = rs.getInt("id_ruta");
        viaje.setIdRuta(rs.wasNull() ? null : idRuta);

        int idCliente = rs.getInt("id_cliente");
        viaje.setIdCliente(rs.wasNull() ? null : idCliente);

        viaje.setOrigenPrivado(rs.getString("origen_privado"));
        viaje.setDestinoPrivado(rs.getString("destino_privado"));

        int cantidad = rs.getInt("cantidad_pasajeros_privado");
        viaje.setCantidadPasajerosPrivado(rs.wasNull() ? null : cantidad);

        double precio = rs.getDouble("precio_total_privado");
        viaje.setPrecioTotalPrivado(rs.wasNull() ? null : precio);

        Timestamp retorno = rs.getTimestamp("fecha_retorno_privado");
        viaje.setFechaRetornoPrivado(retorno != null ? retorno.toLocalDateTime() : null);

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
}
