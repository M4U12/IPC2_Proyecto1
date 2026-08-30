package dao;

import dbconection.DBConection;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import modelos.Chofer;

public class ChoferDAO {

    private DBConection conexionDB;

    public ChoferDAO() {
        this.conexionDB = new DBConection();
    }

    public boolean agregarChofer(Chofer chofer) throws BDException {
        String query = "INSERT INTO choferes (id_sucursal, nombre, foto, num_licencia, tipo_licencia, fecha_vencimiento_licencia, telefono, salario_base_por_viaje, estado) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, chofer.getIdSucursal());
            ps.setString(2, chofer.getNombre());
            ps.setString(3, chofer.getFoto());
            ps.setString(4, chofer.getNumLicencia());
            ps.setString(5, chofer.getTipoLicencia());
            ps.setDate(6, Date.valueOf(chofer.getFechaVencimientoLicencia()));
            ps.setString(7, chofer.getTelefono());
            ps.setDouble(8, chofer.getSalarioBasePorViaje());
            ps.setBoolean(9, chofer.isEstado());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al registrar el chofer: " + e.getMessage(), e);
        }
    }

    public List<Chofer> listarChoferesPorSucursal(int idSucursal, boolean soloActivos) throws BDException {
        List<Chofer> listaChoferes = new ArrayList<>();
        String query = "SELECT id_chofer, id_sucursal, nombre, foto, num_licencia, tipo_licencia, fecha_vencimiento_licencia, telefono, salario_base_por_viaje, estado FROM choferes WHERE id_sucursal = ?";

        if (soloActivos) {
            query += " AND estado = TRUE";
        }

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Chofer chofer = new Chofer(
                            rs.getInt("id_chofer"),
                            rs.getInt("id_sucursal"),
                            rs.getString("nombre"),
                            rs.getString("foto"),
                            rs.getString("num_licencia"),
                            rs.getString("tipo_licencia"),
                            rs.getDate("fecha_vencimiento_licencia").toLocalDate(),
                            rs.getString("telefono"),
                            rs.getDouble("salario_base_por_viaje"),
                            rs.getBoolean("estado")
                    );
                    listaChoferes.add(chofer);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar los choferes: " + e.getMessage(), e);
        }
        return listaChoferes;
    }

    public boolean actualizarChofer(Chofer chofer) throws BDException {
        String query = "UPDATE choferes SET id_sucursal = ?, nombre = ?, foto = ?, num_licencia = ?, tipo_licencia = ?, fecha_vencimiento_licencia = ?, telefono = ?, salario_base_por_viaje = ? WHERE id_chofer = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, chofer.getIdSucursal());
            ps.setString(2, chofer.getNombre());
            ps.setString(3, chofer.getFoto());
            ps.setString(4, chofer.getNumLicencia());
            ps.setString(5, chofer.getTipoLicencia());
            ps.setDate(6, java.sql.Date.valueOf(chofer.getFechaVencimientoLicencia()));
            ps.setString(7, chofer.getTelefono());
            ps.setDouble(8, chofer.getSalarioBasePorViaje());
            ps.setInt(9, chofer.getIdChofer());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al actualizar el chofer: " + e.getMessage(), e);
        }
    }

    public boolean cambiarEstadoChofer(int idChofer, boolean nuevoEstado) throws BDException {
        String query = "UPDATE choferes SET estado = ? WHERE id_chofer = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setBoolean(1, nuevoEstado);
            ps.setInt(2, idChofer);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al cambiar el estado del chofer: " + e.getMessage(), e);
        }
    }
}
