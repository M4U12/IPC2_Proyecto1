package dao;

import dbconection.DBConection;
import excepciones.BDException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ConfiguracionSistemaDAO {

    private DBConection conexionDB;

    public ConfiguracionSistemaDAO() {
        this.conexionDB = new DBConection();
    }

    public boolean guardarConfiguracion(double depreciacion) throws BDException {
        boolean registroExiste = false;
        String queryCheck = "SELECT COUNT(*) AS total FROM configuracion_sistema";

        try (Connection connection = conexionDB.getConection()) {

            // se verifica si existe
            try (PreparedStatement psCheck = connection.prepareStatement(queryCheck); ResultSet rs = psCheck.executeQuery()) {
                if (rs.next() && rs.getInt("total") > 0) {
                    registroExiste = true;
                }
            }

            
            String queryFinal = registroExiste
                    ? "UPDATE configuracion_sistema SET depreciacion_por_km = ?"
                    : "INSERT INTO configuracion_sistema (depreciacion_por_km) VALUES (?)";

            try (PreparedStatement psFinal = connection.prepareStatement(queryFinal)) {
                psFinal.setDouble(1, depreciacion);
                return psFinal.executeUpdate() > 0;
            }

        } catch (SQLException e) {
            throw new BDException("Error al procesar la configuración: " + e.getMessage(), e);
        }
    }
}
