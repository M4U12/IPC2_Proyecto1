package dao;

import dbconection.DBConection;
import excepciones.BDException;
import modelos.Mantenimiento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class MantenimientoDAO {
    private DBConection conexionDB;
    
    public MantenimientoDAO() {
        this.conexionDB = new DBConection();
    }
    
    public boolean registrarMantenimiento(Mantenimiento mantenimiento) throws BDException {
        String query = "INSERT INTO mantenimientos (id_bus, fecha_mantenimiento, monto_mano_obra, monto_repuestos, descripcion) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection connection = conexionDB.getConection(); 
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setInt(1, mantenimiento.getIdBus());
            ps.setDate(2, java.sql.Date.valueOf(mantenimiento.getFechaMantenimiento()));
            ps.setDouble(3, mantenimiento.getMontoManoObra());
            ps.setDouble(4, mantenimiento.getMontoRepuestos());
            ps.setString(5, mantenimiento.getDescripcion());
            
            return ps.executeUpdate() > 0;
            
        } catch (SQLException e) {
            throw new BDException("Error al registrar el mantenimiento del bus: " + e.getMessage(), e);
        }
    }
    
    public List<Mantenimiento> listarMantenimientosPorBus(int idBus) throws BDException {
        List<Mantenimiento> listaMantenimientos = new ArrayList<>();
        String query = "SELECT id_mantenimiento, id_bus, fecha_mantenimiento, monto_mano_obra, monto_repuestos, descripcion FROM mantenimientos WHERE id_bus = ? ORDER BY fecha_mantenimiento DESC";
        
        try (Connection connection = conexionDB.getConection(); 
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setInt(1, idBus);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Mantenimiento mantenimiento = new Mantenimiento(
                        rs.getInt("id_mantenimiento"),
                        rs.getInt("id_bus"),
                        rs.getDate("fecha_mantenimiento").toLocalDate(),
                        rs.getDouble("monto_mano_obra"),
                        rs.getDouble("monto_repuestos"),
                        rs.getString("descripcion")
                    );
                    listaMantenimientos.add(mantenimiento);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al consultar el historial del bus: " + e.getMessage(), e);
        }
        
        return listaMantenimientos;
    }
    
    public List<Mantenimiento> listarMantenimientos(LocalDate fechaInicio, LocalDate fechaFin) throws BDException {
        List<Mantenimiento> listaMantenimientos = new ArrayList<>();
        String query = "SELECT id_mantenimiento, id_bus, fecha_mantenimiento, monto_mano_obra, monto_repuestos, descripcion FROM mantenimientos ";
        
        boolean aplicarFiltro = (fechaInicio != null && fechaFin != null);
        
        if (aplicarFiltro) {
            query += "WHERE fecha_mantenimiento >= ? AND fecha_mantenimiento <= ? ";
        }
        query += "ORDER BY fecha_mantenimiento DESC";
        
        try (Connection connection = conexionDB.getConection(); 
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            if (aplicarFiltro) {
                ps.setDate(1, java.sql.Date.valueOf(fechaInicio));
                ps.setDate(2, java.sql.Date.valueOf(fechaFin));
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Mantenimiento mantenimiento = new Mantenimiento(
                        rs.getInt("id_mantenimiento"),
                        rs.getInt("id_bus"),
                        rs.getDate("fecha_mantenimiento").toLocalDate(),
                        rs.getDouble("monto_mano_obra"),
                        rs.getDouble("monto_repuestos"),
                        rs.getString("descripcion")
                    );
                    listaMantenimientos.add(mantenimiento);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al consultar los mantenimientos: " + e.getMessage(), e);
        }
        
        return listaMantenimientos;
    }
}
