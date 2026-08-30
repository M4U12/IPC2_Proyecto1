package modelos;

public class Usuario {
    private int idUsuario;
    private String dpi;
    private String password;
    private String nombre;
    private String nit;
    private String telefono;
    private String direccion;
    private boolean estado;
    private Enums.RolUsuario rol;
    private int idSucursalAsignada; //solo sirve para las consultas cruzadas
    
    public Usuario() {
    }

    public Usuario(int idUsuario, String dpi, String password, String nombre, String nit, String telefono, String direccion, boolean estado, Enums.RolUsuario rol) {
        this.idUsuario = idUsuario;
        this.dpi = dpi;
        this.password = password;
        this.nombre = nombre;
        this.nit = nit;
        this.telefono = telefono;
        this.direccion = direccion;
        this.estado = estado;
        this.rol = rol;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public String getDpi() {
        return dpi;
    }

    public String getPassword() {
        return password;
    }

    public String getNombre() {
        return nombre;
    }

    public String getNit() {
        return nit;
    }

    public String getTelefono() {
        return telefono;
    }

    public String getDireccion() {
        return direccion;
    }

    public boolean isEstado() {
        return estado;
    }

    public Enums.RolUsuario getRol() {
        return rol;
    }
    
    public int getIdSucursalAsignada(){
        return idSucursalAsignada;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setDpi(String dpi) {
        this.dpi = dpi;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }

    public void setRol(Enums.RolUsuario rol) {
        this.rol = rol;
    }

    public void setIdSucursalAsignada(int idSucursalAsignada) {
        this.idSucursalAsignada = idSucursalAsignada;
    }
}
