package dbconection;

import excepciones.BDException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConection {
    private static final String IP = "localhost";
    private static final int PUERTO = 3306;
    private static final String SCHEMA = "code_n_bugs";
    public static final String USER_NAME = "mau";
    public static final String PASSWORD = "IPC2026";
    public static final String URL = "jdbc:mysql://" + IP + ":" + PUERTO + "/" + SCHEMA + "?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
    
    public Connection getConection() throws BDException {
        Connection connection = null;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            connection = DriverManager.getConnection(URL, USER_NAME, PASSWORD);
        } catch (ClassNotFoundException e) {
            throw new BDException("Falta la librería del conector JDBC de MySQL en el proyecto: " + e.getMessage(), e);
        } catch (SQLException e) {
            throw new BDException("Error al intentar generar la conexión a la base de datos: " + e.getMessage(), e);
        }
        return connection;
    }
}
