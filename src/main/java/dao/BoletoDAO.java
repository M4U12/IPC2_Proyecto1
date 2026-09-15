package dao;

import dbconection.DBConection;
import excepciones.BDException;
import modelos.Boleto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import modelos.BoletoDetalle;
import modelos.Enums;

public class BoletoDAO {

    private DBConection conexionDB;

    public BoletoDAO() {
        this.conexionDB = new DBConection();
    }

    public boolean registrarBoleto(Boleto boleto) throws BDException {
        String query = "INSERT INTO boletos (id_usuario, id_viaje, numero_asiento, precio_pagado, fecha_pago) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, boleto.getIdUsuario());
            ps.setInt(2, boleto.getIdViaje());
            ps.setInt(3, boleto.getNumeroAsiento());
            ps.setDouble(4, boleto.getPrecioPagado());
            ps.setTimestamp(5, Timestamp.valueOf(boleto.getFechaPago()));

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al registrar la venta del boleto: " + e.getMessage(), e);
        }
    }

    public List<Boleto> listarBoletosActivosPorCliente(int idUsuario) throws BDException {
        List<Boleto> listaBoletos = new ArrayList<>();
        String query = "SELECT b.id_boleto, b.id_usuario, b.id_viaje, b.numero_asiento, b.precio_pagado, b.fecha_pago "
                + "FROM boletos b "
                + "INNER JOIN viajes v ON b.id_viaje = v.id_viaje "
                + "WHERE b.id_usuario = ? AND v.estado = 'Programado'";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Boleto boleto = new Boleto(
                            rs.getInt("id_boleto"),
                            rs.getInt("id_usuario"),
                            rs.getInt("id_viaje"),
                            rs.getInt("numero_asiento"),
                            rs.getDouble("precio_pagado"),
                            rs.getTimestamp("fecha_pago").toLocalDateTime()
                    );
                    listaBoletos.add(boleto);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar los boletos activos: " + e.getMessage(), e);
        }

        return listaBoletos;
    }

    public List<Boleto> listarHistorialBoletosPorCliente(int idUsuario) throws BDException {
        List<Boleto> listaBoletos = new ArrayList<>();
        String query = "SELECT b.id_boleto, b.id_usuario, b.id_viaje, b.numero_asiento, b.precio_pagado, b.fecha_pago "
                + "FROM boletos b "
                + "INNER JOIN viajes v ON b.id_viaje = v.id_viaje "
                + "WHERE b.id_usuario = ? AND v.estado != 'Programado'";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Boleto boleto = new Boleto(
                            rs.getInt("id_boleto"),
                            rs.getInt("id_usuario"),
                            rs.getInt("id_viaje"),
                            rs.getInt("numero_asiento"),
                            rs.getDouble("precio_pagado"),
                            rs.getTimestamp("fecha_pago").toLocalDateTime()
                    );
                    listaBoletos.add(boleto);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar el historial de boletos: " + e.getMessage(), e);
        }

        return listaBoletos;
    }

    public List<Integer> obtenerAsientosOcupados(int idViaje) throws BDException {
        List<Integer> asientosOcupados = new ArrayList<>();
        String query = "SELECT numero_asiento FROM boletos WHERE id_viaje = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idViaje);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    asientosOcupados.add(rs.getInt("numero_asiento"));
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al obtener los asientos ocupados: " + e.getMessage(), e);
        }

        return asientosOcupados;
    }

    public boolean comprarBoletoTransaccional(Boleto boleto, int idCartera, double precio, String tipoTransaccion, String descripcion) throws BDException {
        String queryCheckSaldo = "SELECT cantidad_dinero FROM cartera WHERE id_usuario = ?";
        String queryUpdateCartera = "UPDATE cartera SET cantidad_dinero = cantidad_dinero - ? WHERE id_usuario = ?";
        String queryInsertTransaccion = "INSERT INTO transacciones (id_cartera, monto, tipo, fecha_hora, descripcion) VALUES (?, ?, ?, ?, ?)";
        String queryInsertBoleto = "INSERT INTO boletos (id_usuario, id_viaje, numero_asiento, precio_pagado, fecha_pago) VALUES (?, ?, ?, ?, ?)";

        java.sql.Connection connection = null;
        try {
            connection = conexionDB.getConection();
            connection.setAutoCommit(false);

            try (PreparedStatement psCheck = connection.prepareStatement(queryCheckSaldo); PreparedStatement psUpdate = connection.prepareStatement(queryUpdateCartera); PreparedStatement psTx = connection.prepareStatement(queryInsertTransaccion); PreparedStatement psBoleto = connection.prepareStatement(queryInsertBoleto)) {

                // validar que tenga saldo suficiente
                psCheck.setInt(1, boleto.getIdUsuario());
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getDouble("cantidad_dinero") < precio) {
                            throw new BDException("Saldo insuficiente para comprar el boleto.");
                        }
                    } else {
                        throw new BDException("Cartera no encontrada.");
                    }
                }

                // descontar saldo de la cartera
                psUpdate.setDouble(1, precio);
                psUpdate.setInt(2, boleto.getIdUsuario());
                psUpdate.executeUpdate();

                // registrar transacción en el historial
                psTx.setInt(1, idCartera);
                psTx.setDouble(2, precio);
                psTx.setString(3, tipoTransaccion);
                psTx.setTimestamp(4, Timestamp.valueOf(boleto.getFechaPago()));
                psTx.setString(5, descripcion);
                psTx.executeUpdate();

                // generar el boleto y ocupar el asiento
                psBoleto.setInt(1, boleto.getIdUsuario());
                psBoleto.setInt(2, boleto.getIdViaje());
                psBoleto.setInt(3, boleto.getNumeroAsiento());
                psBoleto.setDouble(4, precio);
                psBoleto.setTimestamp(5, Timestamp.valueOf(boleto.getFechaPago()));
                psBoleto.executeUpdate();

                connection.commit();
                return true;
            }

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException ex) {
                }
            }
            throw new BDException("Error en la compra: " + e.getMessage(), e);
        } finally {
            if (connection != null) {
                try {
                    connection.setAutoCommit(true);
                    connection.close();
                } catch (SQLException ex) {
                }
            }
        }
    }

    public List<Boleto> listarTodosBoletosPorCliente(int idUsuario) throws BDException {
        List<Boleto> listaBoletos = new ArrayList<>();
        String query = "SELECT id_boleto, id_usuario, id_viaje, numero_asiento, precio_pagado, fecha_pago FROM boletos WHERE id_usuario = ? ORDER BY fecha_pago DESC";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    listaBoletos.add(new Boleto(
                            rs.getInt("id_boleto"), rs.getInt("id_usuario"), rs.getInt("id_viaje"),
                            rs.getInt("numero_asiento"), rs.getDouble("precio_pagado"), rs.getTimestamp("fecha_pago").toLocalDateTime()
                    ));
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar los boletos: " + e.getMessage(), e);
        }
        return listaBoletos;
    }
    
    public List<BoletoDetalle> listarDetallesBoletosPorCliente(int idCliente) throws BDException {
        List<BoletoDetalle> lista = new ArrayList<>();
        
        String query = "SELECT b.id_boleto, b.numero_asiento, b.precio_pagado, " +
                       "so.nombre AS origen, sd.nombre AS destino, " +
                       "v.fecha_hora_salida_estimada, v.estado_viaje " +
                       "FROM boletos b " +
                       "INNER JOIN viajes v ON b.id_viaje = v.id_viaje " +
                       "INNER JOIN rutas r ON v.id_ruta = r.id_ruta " +
                       "INNER JOIN sucursales so ON r.id_origen = so.id_sucursal " +
                       "INNER JOIN sucursales sd ON r.id_destino = sd.id_sucursal " +
                       "WHERE b.id_usuario = ? ORDER BY v.fecha_hora_salida_estimada DESC";

        try (Connection connection = conexionDB.getConection(); 
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setInt(1, idCliente);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Enums.EstadoViaje estado = Enums.EstadoViaje.valueOf(rs.getString("estado_viaje"));
                    LocalDateTime fecha = null;
                    if (rs.getTimestamp("fecha_hora_salida_estimada") != null) {
                        fecha = rs.getTimestamp("fecha_hora_salida_estimada").toLocalDateTime();
                    }
                    
                    lista.add(new BoletoDetalle(
                            rs.getInt("id_boleto"),
                            rs.getInt("numero_asiento"),
                            rs.getDouble("precio_pagado"),
                            rs.getString("origen"),
                            rs.getString("destino"),
                            fecha,
                            estado
                    ));
                }
            }
        } catch (SQLException e) {
            throw new excepciones.BDException("Error al listar historial de boletos: " + e.getMessage(), e);
        }
        return lista;
    }
}
