package modelos;
import java.time.LocalDate;

public class Mantenimiento {
    private int idMantenimiento;
    private int idBus;
    private LocalDate fechaMantenimiento;
    private double montoManoObra;
    private double montoRepuestos;
    private String descripcion;

    public Mantenimiento() {
    }

    public Mantenimiento(int idMantenimiento, int idBus, LocalDate fechaMantenimiento, double montoManoObra, double montoRepuestos, String descripcion) {
        this.idMantenimiento = idMantenimiento;
        this.idBus = idBus;
        this.fechaMantenimiento = fechaMantenimiento;
        this.montoManoObra = montoManoObra;
        this.montoRepuestos = montoRepuestos;
        this.descripcion = descripcion;
    }

    public int getIdMantenimiento() {
        return idMantenimiento;
    }

    public int getIdBus() {
        return idBus;
    }

    public LocalDate getFechaMantenimiento() {
        return fechaMantenimiento;
    }

    public double getMontoManoObra() {
        return montoManoObra;
    }

    public double getMontoRepuestos() {
        return montoRepuestos;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setIdMantenimiento(int idMantenimiento) {
        this.idMantenimiento = idMantenimiento;
    }

    public void setIdBus(int idBus) {
        this.idBus = idBus;
    }

    public void setFechaMantenimiento(LocalDate fechaMantenimiento) {
        this.fechaMantenimiento = fechaMantenimiento;
    }

    public void setMontoManoObra(double montoManoObra) {
        this.montoManoObra = montoManoObra;
    }

    public void setMontoRepuestos(double montoRepuestos) {
        this.montoRepuestos = montoRepuestos;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
