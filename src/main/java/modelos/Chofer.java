package modelos;

import java.time.LocalDate;

public class Chofer {
    private int idChofer;
    private int idSucursal;
    private String nombre;
    private String foto; 
    private String numLicencia;
    private Enums.TipoLicencia tipoLicencia;
    private LocalDate fechaVencimientoLicencia;
    private String telefono;
    private double salarioBasePorViaje;
    private boolean estado;

    public Chofer() {
    }

    public Chofer(int idChofer, int idSucursal, String nombre, String foto, String numLicencia, Enums.TipoLicencia tipoLicencia, LocalDate fechaVencimientoLicencia, String telefono, double salarioBasePorViaje, boolean estado) {
        this.idChofer = idChofer;
        this.idSucursal = idSucursal;
        this.nombre = nombre;
        this.foto = foto;
        this.numLicencia = numLicencia;
        this.tipoLicencia = tipoLicencia;
        this.fechaVencimientoLicencia = fechaVencimientoLicencia;
        this.telefono = telefono;
        this.salarioBasePorViaje = salarioBasePorViaje;
        this.estado = estado;
    }

    public int getIdChofer() {
        return idChofer;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public String getNombre() {
        return nombre;
    }

    public String getFoto() {
        return foto;
    }

    public String getNumLicencia() {
        return numLicencia;
    }

    public Enums.TipoLicencia getTipoLicencia() {
        return tipoLicencia;
    }

    public LocalDate getFechaVencimientoLicencia() {
        return fechaVencimientoLicencia;
    }

    public String getTelefono() {
        return telefono;
    }

    public double getSalarioBasePorViaje() {
        return salarioBasePorViaje;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setIdChofer(int idChofer) {
        this.idChofer = idChofer;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void setNumLicencia(String numLicencia) {
        this.numLicencia = numLicencia;
    }

    public void setTipoLicencia(Enums.TipoLicencia tipoLicencia) {
        this.tipoLicencia = tipoLicencia;
    }

    public void setFechaVencimientoLicencia(LocalDate fechaVencimientoLicencia) {
        this.fechaVencimientoLicencia = fechaVencimientoLicencia;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public void setSalarioBasePorViaje(double salarioBasePorViaje) {
        this.salarioBasePorViaje = salarioBasePorViaje;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    }
}
