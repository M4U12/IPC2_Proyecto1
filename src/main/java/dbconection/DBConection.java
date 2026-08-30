package dbconection;

import excepciones.BDException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import modelos.Usuario;

public class DBConection {
    private static final String IP = "localhost";
    private static final int PUERTO = 3306;
    private static final String SCHEMA = "code_n_bugs";
    public static final String USER_NAME = "mau";
    public static final String PASSWORD = "IPC2026";
    public static final String URL = "jdbc:mysql://" + IP + ":" + PUERTO + "/" + SCHEMA;
    
    public Connection getConection() throws BDException{
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
        } catch (SQLException e) {
            throw new BDException("Error al intentar generar la conexión a la base de datos", e);
        }
        return connection;
    }
}
