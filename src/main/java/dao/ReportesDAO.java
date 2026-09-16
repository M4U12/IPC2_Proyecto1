package dao;

import dbconection.DBConection;
import excepciones.BDException;
import modelos.ReporteGanancias;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelos.ReporteRutasDemanda;
import modelosReportesSucursal.*;

public class ReportesDAO {

    private DBConection conexionDB;

    public ReportesDAO() {
        this.conexionDB = new DBConection();
    }

    //true para el de reporte de ganancias y false para el de costos
    public List<ReporteGanancias> generarReporteFinanciero(String fechaInicio, String fechaFin, Integer idSucursalFiltro, boolean incluirIngresos) throws BDException {
        List<ReporteGanancias> reporte = new ArrayList<>();
        double depreciacionPorKm = new ConfiguracionSistemaDAO().obtenerDepreciacionActual();

        String querySucursales = "SELECT id_sucursal, nombre FROM sucursales";
        if (idSucursalFiltro != null && idSucursalFiltro > 0) {
            querySucursales += " WHERE id_sucursal = " + idSucursalFiltro;
        }
        querySucursales += " ORDER BY nombre ASC";

        try (Connection connection = conexionDB.getConection(); PreparedStatement psSucursales = connection.prepareStatement(querySucursales); ResultSet rsSucursales = psSucursales.executeQuery()) {

            while (rsSucursales.next()) {
                int idSucursal = rsSucursales.getInt("id_sucursal");
                ReporteGanancias rep = new ReporteGanancias(rsSucursales.getString("nombre"));

                if (incluirIngresos) {
                    String queryBoletos = "SELECT SUM(b.precio_pagado) AS total FROM boletos b "
                            + "INNER JOIN viajes v ON b.id_viaje = v.id_viaje "
                            + "INNER JOIN rutas r ON v.id_ruta = r.id_ruta "
                            + "WHERE r.id_origen = ? AND b.fecha_pago BETWEEN ? AND ?";
                    rep.setIngresosBoletos(obtenerSuma(connection, queryBoletos, idSucursal, fechaInicio, fechaFin));

                    String queryPrivados = "SELECT SUM(precio) AS total FROM viajes_privados "
                            + "WHERE id_sucursal = ? AND estado = 'FINALIZADO' AND fecha_hora_salida_real BETWEEN ? AND ?";
                    rep.setIngresosPrivados(obtenerSuma(connection, queryPrivados, idSucursal, fechaInicio, fechaFin));
                }

                String queryCombRegulares = "SELECT SUM(v.gasto_combustible) AS total FROM viajes v "
                        + "INNER JOIN buses bus ON v.id_bus = bus.id_bus "
                        + "WHERE bus.id_sucursal = ? AND v.fecha_hora_salida_real BETWEEN ? AND ?";
                double combRegulares = obtenerSuma(connection, queryCombRegulares, idSucursal, fechaInicio, fechaFin);

                String qCombPrivados = "SELECT SUM(gasto_combustible) AS total FROM viajes_privados "
                        + "WHERE id_sucursal = ? AND fecha_hora_salida_real BETWEEN ? AND ?";
                double combPrivados = obtenerSuma(connection, qCombPrivados, idSucursal, fechaInicio, fechaFin);

                rep.setCostoCombustible(combRegulares + combPrivados);

                String queryTaller = "SELECT SUM(m.monto_mano_obra) AS total_obra, SUM(m.monto_repuestos) AS total_repuestos "
                        + "FROM mantenimiento m INNER JOIN buses b ON m.id_bus = b.id_bus "
                        + "WHERE b.id_sucursal = ? AND m.fecha_mantenimiento BETWEEN ? AND ?";
                try (PreparedStatement psTaller = connection.prepareStatement(queryTaller)) {
                    psTaller.setInt(1, idSucursal);
                    psTaller.setString(2, fechaInicio + " 00:00:00");
                    psTaller.setString(3, fechaFin + " 23:59:59");
                    try (ResultSet rsTaller = psTaller.executeQuery()) {
                        if (rsTaller.next()) {
                            rep.setCostoManoObraTaller(rsTaller.getDouble("total_obra"));
                            rep.setCostoRepuestosTaller(rsTaller.getDouble("total_repuestos"));
                        }
                    }
                }

                String queryKmRegulares = "SELECT SUM(v.kilometraje_llegada - v.kilometraje_salida) AS total FROM viajes v "
                        + "INNER JOIN buses bus ON v.id_bus = bus.id_bus "
                        + "WHERE bus.id_sucursal = ? AND v.fecha_hora_salida_real BETWEEN ? AND ?";
                double kmReg = obtenerSuma(connection, queryKmRegulares, idSucursal, fechaInicio, fechaFin);

                String queryKmPrivados = "SELECT SUM(kilometraje_llegada - kilometraje_salida) AS total FROM viajes_privados "
                        + "WHERE id_sucursal = ? AND fecha_hora_salida_real BETWEEN ? AND ?";
                double kmPriv = obtenerSuma(connection, queryKmPrivados, idSucursal, fechaInicio, fechaFin);

                rep.setCostoDepreciacion((kmReg + kmPriv) * depreciacionPorKm);

                reporte.add(rep);
            }
        } catch (SQLException e) {
            throw new BDException("Error al generar el reporte: " + e.getMessage(), e);
        }

        return reporte;
    }

    private double obtenerSuma(Connection conn, String query, int idSucursal, String fechaInicio, String fechaFin) throws SQLException {
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idSucursal);
            ps.setString(2, fechaInicio + " 00:00:00");
            ps.setString(3, fechaFin + " 23:59:59");
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        return 0.0;
    }

    public List<ReporteRutasDemanda> generarReporteRutasDemandadas(String fechaInicio, String fechaFin) throws BDException {
        List<ReporteRutasDemanda> lista = new ArrayList<>();

        String query = "SELECT so.nombre AS origen, sd.nombre AS destino, r.precio, COUNT(b.id_boleto) AS total_boletos "
                + "FROM boletos b "
                + "INNER JOIN viajes v ON b.id_viaje = v.id_viaje "
                + "INNER JOIN rutas r ON v.id_ruta = r.id_ruta "
                + "INNER JOIN sucursales so ON r.id_origen = so.id_sucursal "
                + "INNER JOIN sucursales sd ON r.id_destino = sd.id_sucursal "
                + "WHERE b.fecha_pago BETWEEN ? AND ? "
                + "GROUP BY r.id_ruta, so.nombre, sd.nombre, r.precio "
                + "ORDER BY total_boletos DESC";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, fechaInicio + " 00:00:00");
            ps.setString(2, fechaFin + " 23:59:59");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReporteRutasDemanda rep = new ReporteRutasDemanda();
                    rep.setOrigen(rs.getString("origen"));
                    rep.setDestino(rs.getString("destino"));
                    rep.setPrecio(rs.getDouble("precio"));
                    rep.setTotalBoletosVendidos(rs.getInt("total_boletos"));
                    lista.add(rep);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al generar reporte de rutas: " + e.getMessage(), e);
        }

        return lista;
    }

    //reportes para el administrador de sucursal
    public List<ReporteBus> reporteBuses(int idSucursal, Boolean estadoFiltro) throws BDException {
        List<ReporteBus> lista = new ArrayList<>();

        String query = "SELECT b.placa, b.marca, b.modelo, b.capacidad, b.estado_operativo, "
                + "c.nombre AS chofer, b.kilometraje_actual, "
                + "(SELECT COUNT(*) FROM viajes v WHERE v.id_bus = b.id_bus) + "
                + "(SELECT COUNT(*) FROM viajes_privados vp WHERE vp.id_bus = b.id_bus) AS total_viajes "
                + "FROM buses b LEFT JOIN choferes c ON b.id_chofer = c.id_chofer "
                + "WHERE b.id_sucursal = ?";

        if (estadoFiltro != null) {
            query += " AND b.estado = ?";
        }

        try (Connection conn = conexionDB.getConection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idSucursal);
            if (estadoFiltro != null) {
                ps.setBoolean(2, estadoFiltro);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReporteBus rb = new ReporteBus();
                    rb.setPlaca(rs.getString("placa"));
                    rb.setMarca(rs.getString("marca"));
                    rb.setModelo(rs.getString("modelo"));
                    rb.setCapacidad(rs.getInt("capacidad"));
                    rb.setEstadoOperativo(rs.getString("estado_operativo"));
                    String nombreChofer = rs.getString("chofer");
                    rb.setNombreChofer(nombreChofer != null ? nombreChofer : "Sin asignar");

                    rb.setKilometrajeActual(rs.getDouble("kilometraje_actual"));
                    rb.setTotalViajes(rs.getInt("total_viajes"));
                    lista.add(rb);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error reporte buses: " + e.getMessage());
        }
        return lista;
    }

    public List<ReporteChofer> reporteChoferes(int idSucursal) throws BDException {
        List<ReporteChofer> lista = new ArrayList<>();

        String query = "SELECT c.num_licencia, c.nombre, c.tipo_licencia, c.fecha_vencimiento_licencia, c.estado, "
                + "(SELECT COUNT(*) FROM viajes v WHERE v.id_chofer = c.id_chofer) + "
                + "(SELECT COUNT(*) FROM viajes_privados vp WHERE vp.id_chofer = c.id_chofer) AS total_viajes "
                + "FROM choferes c WHERE c.id_sucursal = ?";

        try (Connection conn = conexionDB.getConection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idSucursal);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReporteChofer rc = new ReporteChofer();
                    rc.setLicencia(rs.getString("num_licencia"));
                    rc.setNombre(rs.getString("nombre"));
                    rc.setTipoLicencia(rs.getString("tipo_licencia"));
                    rc.setFechaVencimiento(rs.getDate("fecha_vencimiento_licencia").toLocalDate());
                    rc.setEstado(rs.getBoolean("estado") ? "Activo" : "De Baja");
                    rc.setTotalViajes(rs.getInt("total_viajes"));
                    lista.add(rc);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error reporte choferes: " + e.getMessage());
        }
        return lista;
    }

    public List<ReporteIngresoBoleto> reporteIngresosBoletos(int idSucursal, String fechaInicio, String fechaFin, Integer idRuta, Integer idBus) throws BDException {
        List<ReporteIngresoBoleto> lista = new ArrayList<>();

        String query = "SELECT v.id_viaje, so.nombre AS origen, sd.nombre AS destino, v.fecha_hora_salida_estimada, "
                + "COUNT(b.id_boleto) AS cantidad, SUM(b.precio_pagado) AS total "
                + "FROM viajes v INNER JOIN boletos b ON v.id_viaje = b.id_viaje "
                + "INNER JOIN rutas r ON v.id_ruta = r.id_ruta "
                + "INNER JOIN sucursales so ON r.id_origen = so.id_sucursal "
                + "INNER JOIN sucursales sd ON r.id_destino = sd.id_sucursal "
                + "WHERE r.id_origen = ?";

        if (fechaInicio != null && !fechaInicio.isEmpty()) {
            query += " AND b.fecha_pago >= '" + fechaInicio + " 00:00:00'";
        }
        if (fechaFin != null && !fechaFin.isEmpty()) {
            query += " AND b.fecha_pago <= '" + fechaFin + " 23:59:59'";
        }
        if (idRuta != null && idRuta > 0) {
            query += " AND r.id_ruta = " + idRuta;
        }
        if (idBus != null && idBus > 0) {
            query += " AND v.id_bus = " + idBus;
        }
        query += " GROUP BY v.id_viaje, origen, destino, v.fecha_hora_salida_estimada ORDER BY v.fecha_hora_salida_estimada DESC";

        try (Connection conn = conexionDB.getConection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idSucursal);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReporteIngresoBoleto rep = new ReporteIngresoBoleto();
                    rep.setIdViaje(rs.getInt("id_viaje"));
                    rep.setRuta(rs.getString("origen") + " a " + rs.getString("destino"));
                    rep.setFechaViaje(rs.getTimestamp("fecha_hora_salida_estimada").toLocalDateTime());
                    rep.setCantidadBoletos(rs.getInt("cantidad"));
                    rep.setIngresoTotal(rs.getDouble("total"));
                    lista.add(rep);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error reporte boletos: " + e.getMessage());
        }
        return lista;
    }

    public List<ReporteIngresoAlquiler> reporteIngresosAlquiler(int idSucursal, String fechaInicio, String fechaFin) throws BDException {
        List<ReporteIngresoAlquiler> lista = new ArrayList<>();

        String query = "SELECT u.nombre AS cliente, vp.origen, vp.destino, vp.fecha_hora_salida_estimada, "
                + "b.placa, vp.precio "
                + "FROM viajes_privados vp INNER JOIN usuarios u ON vp.id_cliente = u.id_usuario "
                + "LEFT JOIN buses b ON vp.id_bus = b.id_bus "
                + "WHERE vp.id_sucursal = ? AND vp.estado IN ('PAGADA', 'EN_CURSO', 'FINALIZADO')";

        if (fechaInicio != null && !fechaInicio.isEmpty()) {
            query += " AND vp.fecha_hora_salida_estimada >= '" + fechaInicio + " 00:00:00'";
        }
        if (fechaFin != null && !fechaFin.isEmpty()) {
            query += " AND vp.fecha_hora_salida_estimada <= '" + fechaFin + " 23:59:59'";
        }

        query += " ORDER BY vp.fecha_hora_salida_estimada DESC";

        try (Connection conn = conexionDB.getConection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idSucursal);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReporteIngresoAlquiler rep = new ReporteIngresoAlquiler();
                    rep.setCliente(rs.getString("cliente"));
                    rep.setOrigen(rs.getString("origen"));
                    rep.setDestino(rs.getString("destino"));
                    String placa = rs.getString("placa");
                    rep.setPlacaBus(placa != null ? placa : "Sin asignar");

                    rep.setFechaSalida(rs.getTimestamp("fecha_hora_salida_estimada").toLocalDateTime());
                    rep.setPrecioTotal(rs.getDouble("precio"));
                    lista.add(rep);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error reporte alquileres: " + e.getMessage());
        }
        return lista;
    }

    public List<ReporteDepreciacion> reporteDepreciacion(int idSucursal) throws BDException {
        List<ReporteDepreciacion> lista = new ArrayList<>();
        double depPorKm = new ConfiguracionSistemaDAO().obtenerDepreciacionActual();
        String query = "SELECT b.placa, "
                + "(SELECT SUM(kilometraje_llegada - kilometraje_salida) FROM viajes WHERE id_bus = b.id_bus AND estado_viaje = 'FINALIZADO') AS km_regulares, "
                + "(SELECT SUM(kilometraje_llegada - kilometraje_salida) FROM viajes_privados WHERE id_bus = b.id_bus AND estado = 'FINALIZADO') AS km_privados "
                + "FROM buses b WHERE b.id_sucursal = ?";

        try (Connection conn = conexionDB.getConection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idSucursal);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ReporteDepreciacion rep = new ReporteDepreciacion();
                    rep.setPlaca(rs.getString("placa"));
                    double kmReg = rs.getDouble("km_regulares");
                    double kmPriv = rs.getDouble("km_privados");

                    rep.setKilometrosRecorridos(kmReg + kmPriv);
                    rep.setDepreciacionPorKm(depPorKm);
                    rep.setDepreciacionTotal(rep.getKilometrosRecorridos() * depPorKm);
                    lista.add(rep);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error reporte depreciación: " + e.getMessage());
        }
        return lista;
    }

    public String obtenerNombreSucursal(int idSucursal) throws BDException {
        String query = "SELECT nombre FROM sucursales WHERE id_sucursal = ?";
        try (Connection conn = conexionDB.getConection(); PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, idSucursal);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nombre");
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al obtener el nombre de la sucursal: " + e.getMessage());
        }
        return "Sucursal Local";
    }
}
