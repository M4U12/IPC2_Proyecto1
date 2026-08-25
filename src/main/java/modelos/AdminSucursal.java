package modelos;

public class AdminSucursal {
    private int idAsignacion;
    private int idUsuario;
    private int idSucursal;

    public AdminSucursal() {
    }

    public AdminSucursal(int idAsignacion, int idUsuario, int idSucursal) {
        this.idAsignacion = idAsignacion;
        this.idUsuario = idUsuario;
        this.idSucursal = idSucursal;
    }

    public int getIdAsignacion() {
        return idAsignacion;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public void setIdAsignacion(int idAsignacion) {
        this.idAsignacion = idAsignacion;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }   
}
