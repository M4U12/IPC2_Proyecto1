package dao;

import dbconection.DBConection;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import modelos.Sucursal;

public class SucursalDAO {

    private DBConection conexionDB;

    public SucursalDAO() {
        this.conexionDB = new DBConection();
    }

    public boolean agregarSucursal(Sucursal sucursal) throws BDException {
        String query = "INSERT INTO sucursales (nombre, direccion, telefono) VALUES (?, ?, ?)";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, sucursal.getNombre());
            ps.setString(2, sucursal.getDireccion());
            ps.setString(3, sucursal.getTelefono());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al registrar la sucursal: " + e.getMessage(), e);
        }
    }

    public List<Sucursal> listarSucursales() throws BDException {
        List<Sucursal> listaSucursales = new ArrayList<>();
        String query = "SELECT id_sucursal, nombre, direccion, telefono FROM sucursales";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query); ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Sucursal sucursal = new Sucursal(
                        rs.getInt("id_sucursal"),
                        rs.getString("nombre"),
                        rs.getString("direccion"),
                        rs.getString("telefono")
                );
                listaSucursales.add(sucursal);
            }

        } catch (SQLException e) {
            throw new BDException("Error al listar las sucursales: " + e.getMessage(), e);
        }

        return listaSucursales;
    }

    public boolean actualizarSucursal(Sucursal sucursal) throws BDException {
        String query = "UPDATE sucursales SET nombre = ?, direccion = ?, telefono = ? WHERE id_sucursal = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setString(1, sucursal.getNombre());
            ps.setString(2, sucursal.getDireccion());
            ps.setString(3, sucursal.getTelefono());
            ps.setInt(4, sucursal.getIdSucursal());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al actualizar la sucursal: " + e.getMessage(), e);
        }
    }

    public Optional<Sucursal> buscarSucursalPorId(int idSucursal) throws BDException {
        Sucursal sucursal = null;
        String query = "SELECT id_sucursal, nombre, direccion, telefono FROM sucursales WHERE id_sucursal = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    sucursal = new Sucursal(
                            rs.getInt("id_sucursal"),
                            rs.getString("nombre"),
                            rs.getString("direccion"),
                            rs.getString("telefono")
                    );
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al buscar la sucursal: " + e.getMessage(), e);
        }

        return Optional.ofNullable(sucursal);
    }
}
