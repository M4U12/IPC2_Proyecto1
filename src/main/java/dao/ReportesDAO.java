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
}
