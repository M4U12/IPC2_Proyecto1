package dao;

import dbconection.DBConection;
import excepciones.BDException;
import modelos.Boleto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

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
}
