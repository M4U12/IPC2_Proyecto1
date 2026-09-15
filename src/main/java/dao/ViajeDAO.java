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
import modelos.ViajeDisponibleDetalle;

public class ViajeDAO {

    private DBConection conexionDB;

    public ViajeDAO() {
        this.conexionDB = new DBConection();
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
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                lista.add(extraerViajeDeResultSet(rs));
            }
        } catch (SQLException e) {
            throw new BDException("Error al cargar la cartelera de viajes: " + e.getMessage(), e);
        }
        return lista;
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

    public List<ViajeDisponibleDetalle> listarViajesDisponiblesConDetalle() throws excepciones.BDException {
        List<ViajeDisponibleDetalle> lista = new ArrayList<>();
        String query = "SELECT v.id_viaje, v.fecha_hora_salida_estimada, r.precio, "
                + "so.nombre AS origen, sd.nombre AS destino "
                + "FROM viajes v "
                + "INNER JOIN rutas r ON v.id_ruta = r.id_ruta "
                + "INNER JOIN sucursales so ON r.id_origen = so.id_sucursal "
                + "INNER JOIN sucursales sd ON r.id_destino = sd.id_sucursal "
                + "WHERE v.estado_viaje = 'PROGRAMADO' "
                + "ORDER BY v.fecha_hora_salida_estimada ASC";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                LocalDateTime fecha = null;
                if (rs.getTimestamp("fecha_hora_salida_estimada") != null) {
                    fecha = rs.getTimestamp("fecha_hora_salida_estimada").toLocalDateTime();
                }
                lista.add(new ViajeDisponibleDetalle(
                        rs.getInt("id_viaje"), fecha, rs.getString("origen"), rs.getString("destino"), rs.getDouble("precio")
                ));
            }
        } catch (SQLException e) {
            throw new excepciones.BDException("Error al listar viajes disponibles: " + e.getMessage(), e);
        }
        return lista;
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
            throw new BDException("Error al verificar viajes del chofer: " + e.getMessage(), e);
        }
        return false;
    }

    public void validarEliminacion(int idViaje) throws BDException {
        String queryCheck = "SELECT v.estado_viaje, COUNT(b.id_boleto) AS boletos_vendidos FROM viajes v LEFT JOIN boletos b ON v.id_viaje = b.id_viaje WHERE v.id_viaje = ? GROUP BY v.estado_viaje";
        try (Connection conn = conexionDB.getConection(); PreparedStatement ps = conn.prepareStatement(queryCheck)) {
            ps.setInt(1, idViaje);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    if (!rs.getString("estado_viaje").equals(Enums.EstadoViaje.PROGRAMADO.name())) {
                        throw new BDException("No se puede eliminar el viaje: ya fue iniciado o finalizado.");
                    }
                    if (rs.getInt("boletos_vendidos") > 0) {
                        throw new BDException("No se puede eliminar el viaje: ya tiene boletos pagados.");
                    }
                } else {
                    throw new BDException("El viaje seleccionado no existe.");
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error de BD: " + e.getMessage());
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


    public boolean registrarViajeTrans(Viaje viaje, Connection conn) throws SQLException {
        String query = "INSERT INTO viajes (estado_viaje, id_bus, id_chofer, id_ruta, fecha_hora_salida_estimada, fecha_hora_llegada_estimada) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, viaje.getEstadoViaje().name());
            ps.setInt(2, viaje.getIdBus());
            ps.setInt(3, viaje.getIdChofer());
            ps.setInt(4, viaje.getIdRuta());
            ps.setTimestamp(5, Timestamp.valueOf(viaje.getFechaHoraSalidaEstimada()));
            ps.setTimestamp(6, Timestamp.valueOf(viaje.getFechaHoraLlegadaEstimada()));
            return ps.executeUpdate() > 0;
        }
    }

    public boolean actualizarViajeRegularTrans(Viaje viaje, Connection conn) throws SQLException {
        String query = "UPDATE viajes SET id_ruta = ?, id_bus = ?, id_chofer = ?, fecha_hora_salida_estimada = ?, fecha_hora_llegada_estimada = ? WHERE id_viaje = ? AND estado_viaje = 'PROGRAMADO'";
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, viaje.getIdRuta());
            ps.setInt(2, viaje.getIdBus());
            ps.setInt(3, viaje.getIdChofer());
            ps.setTimestamp(4, Timestamp.valueOf(viaje.getFechaHoraSalidaEstimada()));
            ps.setTimestamp(5, Timestamp.valueOf(viaje.getFechaHoraLlegadaEstimada()));
            ps.setInt(6, viaje.getIdViaje());
            return ps.executeUpdate() > 0;
        }
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


    public void procesarProgramacionCompleta(Viaje nuevoViaje, int idBus, int idChofer) throws BDException {
        Connection conn = null;
        try {
            conn = conexionDB.getConection();
            conn.setAutoCommit(false);

            new BusDAO().modificacionChoferTrans(idChofer, idBus, conn);
            registrarViajeTrans(nuevoViaje, conn);
            new BusDAO().actualizarEstadoOperativoTrans(idBus, Enums.EstadoOperativo.EN_RUTA, conn);
            new ChoferDAO().actualizarEstadoOperativoTrans(idChofer, Enums.EstadoOperativo.EN_RUTA, conn);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            throw new BDException("Error al programar viaje: " + e.getMessage());
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
            }
        }
    }

    public void procesarEdicionCompleta(Viaje viajeEditado, int idBusAntiguo, int idChoferAntiguo) throws BDException {
        Connection conn = null;
        try {
            conn = conexionDB.getConection();
            conn.setAutoCommit(false);

            actualizarViajeRegularTrans(viajeEditado, conn);

            int idBusNuevo = viajeEditado.getIdBus();
            int idChoferNuevo = viajeEditado.getIdChofer();

            if (idBusNuevo != idBusAntiguo) {
                new BusDAO().actualizarEstadoOperativoTrans(idBusAntiguo, Enums.EstadoOperativo.DISPONIBLE, conn);
                new BusDAO().modificacionChoferTrans(null, idBusAntiguo, conn);
                new BusDAO().actualizarEstadoOperativoTrans(idBusNuevo, Enums.EstadoOperativo.EN_RUTA, conn);
            }

            if (idChoferNuevo != idChoferAntiguo) {
                new ChoferDAO().actualizarEstadoOperativoTrans(idChoferAntiguo, Enums.EstadoOperativo.DISPONIBLE, conn);
                new ChoferDAO().actualizarEstadoOperativoTrans(idChoferNuevo, Enums.EstadoOperativo.EN_RUTA, conn);
            }

            new BusDAO().modificacionChoferTrans(idChoferNuevo, idBusNuevo, conn);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            throw new BDException("Fallo al editar el viaje: " + e.getMessage());
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
            }
        }
    }

    public void procesarLiberacionDeRecursos(int idViaje, int idBus, int idChofer, String accion) throws BDException {
        if ("eliminar".equals(accion)) {
            validarEliminacion(idViaje);
        }

        Connection conn = null;
        try {
            conn = conexionDB.getConection();
            conn.setAutoCommit(false);

            if ("eliminar".equals(accion)) {
                String q = "DELETE FROM viajes WHERE id_viaje = ?";
                try (PreparedStatement ps = conn.prepareStatement(q)) {
                    ps.setInt(1, idViaje);
                    ps.executeUpdate();
                }
            } else {
                String q = "UPDATE viajes SET estado_viaje = 'CANCELADO' WHERE id_viaje = ? AND estado_viaje = 'PROGRAMADO'";
                try (PreparedStatement ps = conn.prepareStatement(q)) {
                    ps.setInt(1, idViaje);
                    ps.executeUpdate();
                }
            }

            new BusDAO().actualizarEstadoOperativoTrans(idBus, Enums.EstadoOperativo.DISPONIBLE, conn);
            new ChoferDAO().actualizarEstadoOperativoTrans(idChofer, Enums.EstadoOperativo.DISPONIBLE, conn);
            new BusDAO().modificacionChoferTrans(null, idBus, conn);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            throw new BDException("Fallo al liberar los recursos: " + e.getMessage());
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
            }
        }
    }

    public void procesarFinalizacionCompleta(int idViaje, int idBus, int idChofer, double kmLlegada, double gasto, LocalDateTime llegadaReal) throws BDException {
        Connection conn = null;
        try {
            conn = conexionDB.getConection();
            conn.setAutoCommit(false);

            String query = "UPDATE viajes SET estado_viaje = 'FINALIZADO', kilometraje_llegada = ?, gasto_combustible = ?, fecha_hora_llegada_real = ? WHERE id_viaje = ?";
            try (PreparedStatement ps = conn.prepareStatement(query)) {
                ps.setDouble(1, kmLlegada);
                ps.setDouble(2, gasto);
                ps.setTimestamp(3, Timestamp.valueOf(llegadaReal));
                ps.setInt(4, idViaje);
                ps.executeUpdate();
            }

            new BusDAO().actualizarEstadoOperativoYKilometraje(idBus, Enums.EstadoOperativo.DISPONIBLE, kmLlegada, conn);
            new ChoferDAO().actualizarEstadoOperativoTrans(idChofer, Enums.EstadoOperativo.DISPONIBLE, conn);
            new BusDAO().modificacionChoferTrans(null, idBus, conn); 

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
            }
            throw new BDException("Fallo al finalizar el viaje: " + e.getMessage());
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
            }
        }
    }
}
