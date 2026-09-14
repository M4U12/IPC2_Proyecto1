package dao;

import dbconection.DBConection;
import excepciones.BDException;
import modelos.Cartera;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

public class CarteraDAO {

    private DBConection conexionDB;

    public CarteraDAO() {
        this.conexionDB = new DBConection();
    }

    public Optional<Cartera> obtenerCarteraPorUsuario(int idUsuario) throws BDException {
        Cartera cartera = null;
        String query = "SELECT id_cartera, id_usuario, cantidad_dinero FROM cartera WHERE id_usuario = ?";

        try (Connection connection = conexionDB.getConection(); PreparedStatement ps = connection.prepareStatement(query)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    cartera = new Cartera(
                            rs.getInt("id_cartera"),
                            rs.getInt("id_usuario"),
                            rs.getDouble("cantidad_dinero")
                    );
                }
            }
        } catch (SQLException e) {
            throw new BDException("Error al consultar la cartera: " + e.getMessage(), e);
        }

        return Optional.ofNullable(cartera);
    }

    public boolean agregarFondosYRegistrar(int idUsuario, int idCartera, double monto, String descripcion) throws BDException {
        String updateCartera = "UPDATE cartera SET cantidad_dinero = cantidad_dinero + ? WHERE id_usuario = ?";
        String insertTransaccion = "INSERT INTO transacciones (id_cartera, monto, tipo, fecha_hora, descripcion) VALUES (?, ?, ?, ?, ?)";

        Connection connection = null;
        try {
            connection = conexionDB.getConection();
            connection.setAutoCommit(false);

            try (PreparedStatement psUpdate = connection.prepareStatement(updateCartera); PreparedStatement psInsert = connection.prepareStatement(insertTransaccion)) {

                psUpdate.setDouble(1, monto);
                psUpdate.setInt(2, idUsuario);
                psUpdate.executeUpdate();

                psInsert.setInt(1, idCartera);
                psInsert.setDouble(2, monto);
                psInsert.setString(3, modelos.Enums.TipoTransacciones.RECARGA.name());
                psInsert.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                psInsert.setString(5, descripcion);
                psInsert.executeUpdate();

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
            throw new BDException("Error crítico al procesar la recarga: " + e.getMessage(), e);
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

    public boolean descontarFondosYRegistrar(int idUsuario, int idCartera, double montoCobrar, modelos.Enums.TipoTransacciones tipoEnum, String descripcion) throws BDException {
        String queryCheck = "SELECT cantidad_dinero FROM cartera WHERE id_usuario = ?";
        String queryUpdate = "UPDATE cartera SET cantidad_dinero = cantidad_dinero - ? WHERE id_usuario = ?";
        String queryInsert = "INSERT INTO transacciones (id_cartera, monto, tipo, fecha_hora, descripcion) VALUES (?, ?, ?, ?, ?)";

        Connection connection = null;
        try {
            connection = conexionDB.getConection();
            connection.setAutoCommit(false);

            try (PreparedStatement psCheck = connection.prepareStatement(queryCheck); PreparedStatement psUpdate = connection.prepareStatement(queryUpdate); PreparedStatement psInsert = connection.prepareStatement(queryInsert)) {

                psCheck.setInt(1, idUsuario);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        if (rs.getDouble("cantidad_dinero") < montoCobrar) {
                            throw new BDException("Saldo insuficiente para realizar el pago.");
                        }
                    } else {
                        throw new BDException("No se encontró la cartera.");
                    }
                }

                psUpdate.setDouble(1, montoCobrar);
                psUpdate.setInt(2, idUsuario);
                psUpdate.executeUpdate();

                psInsert.setInt(1, idCartera);
                psInsert.setDouble(2, montoCobrar);
                psInsert.setString(3, tipoEnum.name()); 
                psInsert.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));
                psInsert.setString(5, descripcion);
                psInsert.executeUpdate();

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
            throw new BDException("Error en el proceso de pago: " + e.getMessage(), e);
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
}
