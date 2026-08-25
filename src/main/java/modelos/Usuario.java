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
    private String rol;
    
    public Usuario() {
    }

    public Usuario(int idUsuario, String dpi, String password, String nombre, String nit, String telefono, String direccion, boolean estado, String rol) {
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

    public String getRol() {
        return rol;
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

    public void setRol(String rol) {
        this.rol = rol;
    }
}
