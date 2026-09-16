package dao;

import dbconection.DBConection;
import excepciones.BDException;
import modelos.ViajePrivado;
import modelos.Enums;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ViajePrivadoDAO {

    private DBConection conexionDB;

    public ViajePrivadoDAO() {
        this.conexionDB = new DBConection();
    }

    public boolean registrarSolicitud(ViajePrivado viaje) throws BDException {
        String query = "INSERT INTO viajes_privados (id_cliente, id_sucursal, origen, destino, cantidad_pasajeros, fecha_hora_salida_estimada, estado) "
                + "VALUES (?, ?, ?, ?, ?, ?, 'PENDIENTE')";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, viaje.getIdCliente());
            ps.setInt(2, viaje.getIdSucursal());
            ps.setString(3, viaje.getOrigen());
            ps.setString(4, viaje.getDestino());
            ps.setInt(5, viaje.getCantidadPasajeros());
            ps.setTimestamp(6, Timestamp.valueOf(viaje.getFechaHoraSalidaEstimada()));
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BDException("Error al registrar solicitud privada: " + e.getMessage(), e);
        }
    }

    public List<ViajePrivado> listarPorSucursal(int idSucursal) throws BDException {
        List<ViajePrivado> lista = new ArrayList<>();
        String query = "SELECT vp.*, u.nombre as nombre_cliente FROM viajes_privados vp "
                + "INNER JOIN usuarios u ON vp.id_cliente = u.id_usuario "
                + "WHERE vp.id_sucursal = ? ORDER BY vp.fecha_hora_salida_estimada DESC";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idSucursal);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ViajePrivado vp = new ViajePrivado();
                    vp.setIdViajePrivado(rs.getInt("id_viaje_privado"));
                    vp.setIdCliente(rs.getInt("id_cliente"));
                    vp.setIdSucursal(rs.getInt("id_sucursal"));
                    vp.setOrigen(rs.getString("origen"));
                    vp.setDestino(rs.getString("destino"));
                    vp.setCantidadPasajeros(rs.getInt("cantidad_pasajeros"));
                    vp.setEstado(Enums.EstadoViaje.valueOf(rs.getString("estado")));
                    vp.setNombreCliente(rs.getString("nombre_cliente"));

                    double precio = rs.getDouble("precio");
                    vp.setPrecio(rs.wasNull() ? null : precio);

                    int idBus = rs.getInt("id_bus");
                    vp.setIdBus(rs.wasNull() ? null : idBus);

                    int idChofer = rs.getInt("id_chofer");
                    vp.setIdChofer(rs.wasNull() ? null : idChofer);

                    Timestamp salidaEst = rs.getTimestamp("fecha_hora_salida_estimada");
                    vp.setFechaHoraSalidaEstimada(salidaEst != null ? salidaEst.toLocalDateTime() : null);

                    Timestamp llegadaEst = rs.getTimestamp("fecha_hora_llegada_estimada");
                    vp.setFechaHoraLlegadaEstimada(llegadaEst != null ? llegadaEst.toLocalDateTime() : null);

                    Timestamp salidaReal = rs.getTimestamp("fecha_hora_salida_real");
                    vp.setFechaHoraSalidaReal(salidaReal != null ? salidaReal.toLocalDateTime() : null);

                    Timestamp llegadaReal = rs.getTimestamp("fecha_hora_llegada_real");
                    vp.setFechaHoraLlegadaReal(llegadaReal != null ? llegadaReal.toLocalDateTime() : null);

                    double kmSalida = rs.getDouble("kilometraje_salida");
                    vp.setKilometrajeSalida(rs.wasNull() ? null : kmSalida);

                    double kmLlegada = rs.getDouble("kilometraje_llegada");
                    vp.setKilometrajeLlegada(rs.wasNull() ? null : kmLlegada);

                    lista.add(vp);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar viajes privados: " + e.getMessage(), e);
        }
        return lista;
    }

    public boolean cotizarViaje(int idViajePrivado, double precio, LocalDateTime llegadaEstimada) throws BDException {
        String query = "UPDATE viajes_privados SET precio = ?, fecha_hora_llegada_estimada = ?, estado = 'COTIZADA' WHERE id_viaje_privado = ? AND estado = 'PENDIENTE'";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDouble(1, precio);
            ps.setTimestamp(2, Timestamp.valueOf(llegadaEstimada));
            ps.setInt(3, idViajePrivado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BDException("Error al cotizar el viaje: " + e.getMessage(), e);
        }
    }

    public boolean asignarRecursos(int idViajePrivado, int idBus, int idChofer, Connection conn) throws BDException {
        String query = "UPDATE viajes_privados SET id_bus = ?, id_chofer = ? WHERE id_viaje_privado = ? AND estado = 'PAGADA'";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idBus);
            ps.setInt(2, idChofer);
            ps.setInt(3, idViajePrivado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BDException("Error al asignar recursos al viaje privado: " + e.getMessage(), e);
        }
    }

    public boolean iniciarViaje(int idViajePrivado, double kilometrajeSalida, LocalDateTime fechaHoraSalidaReal) throws BDException {
        String query = "UPDATE viajes_privados SET estado = 'EN_CURSO', kilometraje_salida = ?, fecha_hora_salida_real = ? WHERE id_viaje_privado = ?";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDouble(1, kilometrajeSalida);
            ps.setTimestamp(2, Timestamp.valueOf(fechaHoraSalidaReal));
            ps.setInt(3, idViajePrivado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BDException("Error al iniciar el viaje privado: " + e.getMessage(), e);
        }
    }

    public boolean finalizarViaje(int idViajePrivado, double kilometrajeLlegada, double gastoCombustible, LocalDateTime fechaHoraLlegadaReal, Connection conn) throws BDException {
        String query = "UPDATE viajes_privados SET estado = 'FINALIZADO', kilometraje_llegada = ?, gasto_combustible = ?, fecha_hora_llegada_real = ? WHERE id_viaje_privado = ?";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setDouble(1, kilometrajeLlegada);
            ps.setDouble(2, gastoCombustible);
            ps.setTimestamp(3, Timestamp.valueOf(fechaHoraLlegadaReal));
            ps.setInt(4, idViajePrivado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BDException("Error al finalizar el viaje privado: " + e.getMessage(), e);
        }
    }

    public List<ViajePrivado> listarPorCliente(int idCliente) throws BDException {
        List<ViajePrivado> lista = new ArrayList<>();
        String query = "SELECT * FROM viajes_privados WHERE id_cliente = ? ORDER BY fecha_hora_salida_estimada DESC";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idCliente);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ViajePrivado vp = new ViajePrivado();
                    vp.setIdViajePrivado(rs.getInt("id_viaje_privado"));
                    vp.setOrigen(rs.getString("origen"));
                    vp.setDestino(rs.getString("destino"));
                    vp.setCantidadPasajeros(rs.getInt("cantidad_pasajeros"));
                    vp.setEstado(Enums.EstadoViaje.valueOf(rs.getString("estado")));

                    double precio = rs.getDouble("precio");
                    vp.setPrecio(rs.wasNull() ? null : precio);
                    int idBus = rs.getInt("id_bus");
                    vp.setIdBus(rs.wasNull() ? null : idBus);
                    int idChofer = rs.getInt("id_chofer");
                    vp.setIdChofer(rs.wasNull() ? null : idChofer);

                    Timestamp salidaEst = rs.getTimestamp("fecha_hora_salida_estimada");
                    vp.setFechaHoraSalidaEstimada(salidaEst != null ? salidaEst.toLocalDateTime() : null);

                    lista.add(vp);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar los viajes del cliente: " + e.getMessage(), e);
        }
        return lista;
    }

    public boolean pagarCotizacion(int idViajePrivado) throws BDException {
        String query = "UPDATE viajes_privados SET estado = 'PAGADA' WHERE id_viaje_privado = ? AND estado = 'COTIZADA'";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idViajePrivado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BDException("Error al registrar pago de cotización: " + e.getMessage(), e);
        }
    }

    public boolean tieneViajesPrivadosActivosPorBus(int idBus) throws BDException {
        String query = "SELECT COUNT(*) FROM viajes_privados WHERE id_bus = ? AND estado IN ('PAGADA', 'EN_CURSO')";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idBus);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al verificar viajes privados del bus: " + e.getMessage(), e);
        }
        return false;
    }

    public boolean tieneViajesPrivadosActivosPorChofer(int idChofer) throws BDException {
        String query = "SELECT COUNT(*) FROM viajes_privados WHERE id_chofer = ? AND estado IN ('PAGADA', 'EN_CURSO')";
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

    public boolean cancelarViajePrivado(int idViajePrivado) throws BDException {
        String query = "UPDATE viajes_privados SET estado = 'CANCELADO' WHERE id_viaje_privado = ? AND estado IN ('PENDIENTE', 'COTIZADA')";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idViajePrivado);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            throw new BDException("Error al cancelar la solicitud: " + e.getMessage(), e);
        }
    }

    public boolean eliminarViajePrivado(int idViajePrivado) throws BDException {
        String queryCheck = "SELECT estado FROM viajes_privados WHERE id_viaje_privado = ?";
        try (Connection connection = conexionDB.getConection()) {
            try (PreparedStatement psCheck = connection.prepareStatement(queryCheck)) {
                psCheck.setInt(1, idViajePrivado);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        String estado = rs.getString("estado");
                        //bloqueo de seguridad
                        if (estado.equals("PAGADA") || estado.equals("EN_CURSO") || estado.equals("FINALIZADO")) {
                            throw new BDException("No se puede eliminar un viaje que ya ha sido pagado o procesado.");
                        }
                    } else {
                        throw new BDException("El viaje no existe.");
                    }
                }
            }
            String queryDelete = "DELETE FROM viajes_privados WHERE id_viaje_privado = ?";
            try (PreparedStatement psDelete = connection.prepareStatement(queryDelete)) {
                psDelete.setInt(1, idViajePrivado);
                return psDelete.executeUpdate() > 0;
            }
        } catch (SQLException e) {
            throw new BDException("Error al eliminar el viaje privado: " + e.getMessage(), e);
        }
    }

    public void procesarAsignacionCompleta(int idViaje, int idBus, int idChofer) throws BDException {
        Connection conn = null;
        try {
            conn = conexionDB.getConection();
            conn.setAutoCommit(false);

            asignarRecursos(idViaje, idBus, idChofer, conn);
            new BusDAO().modificacionChoferTrans(idChofer, idBus, conn);
            new BusDAO().actualizarEstadoOperativoTrans(idBus, Enums.EstadoOperativo.EN_RUTA, conn);
            new ChoferDAO().actualizarEstadoOperativoTrans(idChofer, Enums.EstadoOperativo.EN_RUTA, conn);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new BDException("Error en Rollback: " + ex.getMessage());
            }
            throw new BDException("La operación falló y se cancelaron los cambios: " + e.getMessage());
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public void procesarFinalizacionCompleta(int idViaje, int idBus, int idChofer, double kmLlegada, double gasto, LocalDateTime llegadaReal) throws BDException {
        Connection conn = null;
        try {
            conn = conexionDB.getConection();
            conn.setAutoCommit(false);

            finalizarViaje(idViaje, kmLlegada, gasto, llegadaReal, conn);
            new BusDAO().actualizarEstadoOperativoYKilometraje(idBus, Enums.EstadoOperativo.DISPONIBLE, kmLlegada, conn);
            new ChoferDAO().actualizarEstadoOperativoTrans(idChofer, Enums.EstadoOperativo.DISPONIBLE, conn);
            new BusDAO().modificacionChoferTrans(null, idBus, conn);

            conn.commit();
        } catch (SQLException e) {
            if (conn != null) try {
                conn.rollback();
            } catch (SQLException ex) {
                throw new BDException("Error en Rollback: " + ex.getMessage());
            }
            throw new BDException("Fallo al finalizar el viaje: " + e.getMessage());
        } finally {
            if (conn != null) try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Optional<ViajePrivado> obtenerViajePrivadoActivoPorChofer(int idChofer) throws BDException {
        String query = "SELECT * FROM viajes_privados WHERE id_chofer = ? AND estado IN ('PAGADA', 'EN_CURSO') LIMIT 1";
        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idChofer);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ViajePrivado vp = new ViajePrivado();
                    vp.setIdViajePrivado(rs.getInt("id_viaje_privado"));
                    vp.setIdBus(rs.getInt("id_bus"));
                    vp.setIdChofer(rs.getInt("id_chofer"));
                    vp.setOrigen(rs.getString("origen"));
                    vp.setDestino(rs.getString("destino"));
                    vp.setEstado(Enums.EstadoViaje.valueOf(rs.getString("estado")));

                    Timestamp salidaEst = rs.getTimestamp("fecha_hora_salida_estimada");
                    if (salidaEst != null) {
                        vp.setFechaHoraSalidaEstimada(salidaEst.toLocalDateTime());
                    }

                    double kmSalida = rs.getDouble("kilometraje_salida");
                    if (!rs.wasNull()) {
                        vp.setKilometrajeSalida(kmSalida);
                    }

                    return Optional.of(vp);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al buscar el viaje privado activo: " + e.getMessage(), e);
        }
        return Optional.empty(); 
    }
}
