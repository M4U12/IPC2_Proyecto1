package modelos;

public class Sucursal {

    private int idSucursal;
    private String nombre;
    private String direccion;
    private String telefono;
    private double latitud;
    private double longitud;
    private double tarifaBaseHora;
    private double tarifaPasajero;

    public Sucursal() {
    }

    public Sucursal(int idSucursal, String nombre, String direccion, String telefono, double latitud, double longitud) {
        this.idSucursal = idSucursal;
        this.nombre = nombre;
        this.direccion = direccion;
        this.telefono = telefono;
        this.latitud = latitud;
        this.longitud = longitud;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public String getNombre() {
        return nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public double getLatitud() {
        return latitud;
    }

    public double getLongitud() {
        return longitud;
    }

    public void setLatitud(double latitud) {
        this.latitud = latitud;
    }

    public void setLongitud(double longitud) {
        this.longitud = longitud;
    }

    public double getTarifaBaseHora() {
        return tarifaBaseHora;
    }

    public void setTarifaBaseHora(double tarifaBaseHora) {
        this.tarifaBaseHora = tarifaBaseHora;
    }

    public double getTarifaPasajero() {
        return tarifaPasajero;
    }

    public void setTarifaPasajero(double tarifaPasajero) {
        this.tarifaPasajero = tarifaPasajero;
    }
}
