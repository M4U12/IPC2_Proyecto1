package dao;

import dbconection.DBConection;
import excepciones.BDException;
import modelos.Cartera;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CarteraDAO {

    private DBConection conexionDB;

    public CarteraDAO() {
        this.conexionDB = new DBConection();
    }

    public boolean crearCartera(int idUsuario) throws BDException {
        String query = "INSERT INTO carteras (id_usuario, saldo) VALUES (?, 0.00)";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idUsuario);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al crear la cartera del usuario: " + e.getMessage(), e);
        }
    }

    public Cartera obtenerCarteraPorUsuario(int idUsuario) throws BDException {
        Cartera cartera = null;
        String query = "SELECT id_cartera, id_usuario, saldo FROM carteras WHERE id_usuario = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cartera = new Cartera(
                            rs.getInt("id_cartera"),
                            rs.getInt("id_usuario"),
                            rs.getDouble("saldo")
                    );
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al consultar la cartera: " + e.getMessage(), e);
        }

        return cartera;
    }

    public boolean agregarFondos(int idUsuario, double monto) throws BDException {
        String query = "UPDATE carteras SET saldo = saldo + ? WHERE id_usuario = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setDouble(1, monto);
            ps.setInt(2, idUsuario);

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            throw new BDException("Error al agregar fondos a la cartera: " + e.getMessage(), e);
        }
    }

    public boolean descontarFondos(int idUsuario, double montoCobrar) throws BDException {
        try (Connection connection = conexionDB.getConection()) {
            String queryCheck = "SELECT saldo FROM carteras WHERE id_usuario = ?"; //ver dinero actual
            
            try (PreparedStatement psCheck = connection.prepareStatement(queryCheck)) {
                psCheck.setInt(1, idUsuario);
                
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        double saldoActual = rs.getDouble("saldo");
                        if (saldoActual < montoCobrar) {
                            throw new BDException("Saldo insuficiente para realizar la compra.");
                        }
                    } else {
                        throw new BDException("No se encontró una cartera para este usuario.");
                    }
                }
            }

            String queryUpdate = "UPDATE carteras SET saldo = saldo - ? WHERE id_usuario = ?";//tiene dinero suficiente
            try (PreparedStatement psUpdate = connection.prepareStatement(queryUpdate)) {
                psUpdate.setDouble(1, montoCobrar);
                psUpdate.setInt(2, idUsuario);

                return psUpdate.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            throw new BDException("Error en el proceso de pago: " + e.getMessage(), e);
        }
    }
}
