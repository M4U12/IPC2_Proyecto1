package dao;

import dbconection.DBConection;
import excepciones.BDException;
import modelos.Ruta;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RutaDAO {

    private DBConection conexionDB;

    public RutaDAO() {
        this.conexionDB = new DBConection();
    }

    public boolean agregarRuta(Ruta ruta) throws BDException {
        String query = "INSERT INTO rutas (id_origen, id_destino, distancia_km) VALUES (?, ?, ?)";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, ruta.getIdOrigen());
            ps.setInt(2, ruta.getIdDestino());
            ps.setDouble(3, ruta.getDistanciaKm());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al registrar la ruta: " + e.getMessage(), e);
        }
    }

    public List<Ruta> listarRutasPorSucursal(int idSucursal) throws BDException {
        List<Ruta> listaRutas = new ArrayList<>();
        String query = "SELECT id_ruta, id_origen, id_destino, distancia_km, precio FROM rutas WHERE id_origen = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idSucursal);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Ruta ruta = new Ruta(
                            rs.getInt("id_ruta"),
                            rs.getInt("id_origen"),
                            rs.getInt("id_destino"),
                            rs.getDouble("distancia_km"),
                            rs.getDouble("precio")
                    );
                    listaRutas.add(ruta);
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al listar las rutas de la sucursal: " + e.getMessage(), e);
        }

        return listaRutas;
    }

    public boolean actualizarRuta(Ruta ruta) throws BDException {
        String query = "UPDATE rutas SET id_origen = ?, id_destino = ?, distancia_km = ?, precio = ? WHERE id_ruta = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, ruta.getIdOrigen());
            ps.setInt(2, ruta.getIdDestino());
            ps.setDouble(3, ruta.getDistanciaKm());
            ps.setDouble(4, ruta.getPrecio());
            ps.setInt(5, ruta.getIdRuta());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al actualizar la ruta: " + e.getMessage(), e);
        }
    }

    public boolean eliminarRuta(int idRuta) throws BDException {
        try (Connection connection = conexionDB.getConection()) {

            String queryCheck = "SELECT COUNT(*) AS total FROM viajes WHERE id_ruta = ?";//para ver si tiene asociado algun viaje
            try (PreparedStatement psCheck = connection.prepareStatement(queryCheck)) {
                psCheck.setInt(1, idRuta);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next() && rs.getInt("total") > 0) {
                        throw new BDException("No se puede eliminar la ruta porque ya tiene viajes asociados.");
                    }
                }
            }

            String queryDelete = "DELETE FROM rutas WHERE id_ruta = ?";
            try (PreparedStatement psDelete = connection.prepareStatement(queryDelete)) {
                psDelete.setInt(1, idRuta);
                return psDelete.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            throw new BDException("Error en el proceso de eliminación de la ruta: " + e.getMessage(), e);
        }
    }
}
