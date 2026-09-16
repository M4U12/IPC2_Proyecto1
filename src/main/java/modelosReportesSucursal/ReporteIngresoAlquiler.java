package modelosReportesSucursal;
import java.time.LocalDateTime;

public class ReporteIngresoAlquiler {
    private String cliente;
    private String origen;
    private String destino;
    private LocalDateTime fechaSalida;
    private String placaBus;
    private double precioTotal;

    public ReporteIngresoAlquiler() {}

    public String getCliente() {
        return cliente;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public LocalDateTime getFechaSalida() {
        return fechaSalida;
    }

    public String getPlacaBus() {
        return placaBus;
    }

    public double getPrecioTotal() {
        return precioTotal;
    }

    public void setCliente(String cliente) {
        this.cliente = cliente;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public void setFechaSalida(LocalDateTime fechaSalida) {
        this.fechaSalida = fechaSalida;
    }

    public void setPlacaBus(String placaBus) {
        this.placaBus = placaBus;
    }

    public void setPrecioTotal(double precioTotal) {
        this.precioTotal = precioTotal;
    }

    
}