package modelosReportesSucursal;
import java.time.LocalDateTime;

public class ReporteIngresoBoleto {
    private int idViaje;
    private String ruta;
    private LocalDateTime fechaViaje;
    private int cantidadBoletos;
    private double ingresoTotal;

    public ReporteIngresoBoleto() {}

    public int getIdViaje() {
        return idViaje;
    }

    public String getRuta() {
        return ruta;
    }

    public LocalDateTime getFechaViaje() {
        return fechaViaje;
    }

    public int getCantidadBoletos() {
        return cantidadBoletos;
    }

    public double getIngresoTotal() {
        return ingresoTotal;
    }

    public void setIdViaje(int idViaje) {
        this.idViaje = idViaje;
    }

    public void setRuta(String ruta) {
        this.ruta = ruta;
    }

    public void setFechaViaje(LocalDateTime fechaViaje) {
        this.fechaViaje = fechaViaje;
    }

    public void setCantidadBoletos(int cantidadBoletos) {
        this.cantidadBoletos = cantidadBoletos;
    }

    public void setIngresoTotal(double ingresoTotal) {
        this.ingresoTotal = ingresoTotal;
    }

    
}