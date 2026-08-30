package dao;

import dbconection.DBConection;
import excepciones.BDException;
import modelos.Transaccion;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TransaccionDAO {

    private DBConection conexionDB;

    public TransaccionDAO() {
        this.conexionDB = new DBConection();
    }

    public boolean registrarTransaccion(Transaccion transaccion) throws BDException {
        String query = "INSERT INTO transacciones (id_cartera, monto, tipo, fecha, descripcion) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, transaccion.getIdCartera());
            ps.setDouble(2, transaccion.getMonto());
            ps.setString(3, transaccion.getTipo()); 
            ps.setTimestamp(4, Timestamp.valueOf(transaccion.getFechaHora()));
            ps.setString(5, transaccion.getDescripcion());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al registrar la transacción en la bitácora: " + e.getMessage(), e);
        }
    }

    public List<Transaccion> listarTransacciones(LocalDate fechaInicio, LocalDate fechaFin) throws BDException {
        List<Transaccion> listaTransacciones = new ArrayList<>();
        
        String query = "SELECT id_transaccion, id_cartera, monto, tipo, fecha, descripcion FROM transacciones ";
        boolean aplicarFiltro = (fechaInicio != null && fechaFin != null);
        
        //si hay fechas se añade el WHERE antes del ORDER BY
        if (aplicarFiltro) {
            query += "WHERE fecha >= ? AND fecha <= ? ";
        }
        query += "ORDER BY fecha DESC";
        
        try (Connection connection = conexionDB.getConection(); 
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            if (aplicarFiltro) {
                ps.setTimestamp(1, Timestamp.valueOf(fechaInicio.atStartOfDay()));
                ps.setTimestamp(2, Timestamp.valueOf(fechaFin.atTime(23, 59, 59)));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Transaccion transaccion = new Transaccion(
                        rs.getInt("id_transaccion"),
                        rs.getInt("id_cartera"),
                        rs.getDouble("monto"),
                        rs.getString("tipo"),
                        rs.getTimestamp("fecha").toLocalDateTime(),
                        rs.getString("descripcion")
                    );
                    listaTransacciones.add(transaccion);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al consultar el historial de transacciones: " + e.getMessage(), e);
        }
        
        return listaTransacciones;
    }
}
