package modelosReportesSucursal;
import java.time.LocalDate;

public class ReporteChofer {
    private String licencia;
    private String nombre;
    private String tipoLicencia;
    private LocalDate fechaVencimiento;
    private String estado;
    private int totalViajes;

    public ReporteChofer() {}

    public String getLicencia() {
        return licencia;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipoLicencia() {
        return tipoLicencia;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public String getEstado() {
        return estado;
    }

    public int getTotalViajes() {
        return totalViajes;
    }

    public void setLicencia(String licencia) {
        this.licencia = licencia;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setTipoLicencia(String tipoLicencia) {
        this.tipoLicencia = tipoLicencia;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public void setTotalViajes(int totalViajes) {
        this.totalViajes = totalViajes;
    }

    
}