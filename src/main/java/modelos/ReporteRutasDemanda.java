package modelos;

public class ReporteRutasDemanda {

    private String origen;
    private String destino;
    private double precio;
    private int totalBoletosVendidos;

    public ReporteRutasDemanda() {
    }

    public String getOrigen() {
        return origen;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public int getTotalBoletosVendidos() {
        return totalBoletosVendidos;
    }

    public void setTotalBoletosVendidos(int totalBoletosVendidos) {
        this.totalBoletosVendidos = totalBoletosVendidos;
    }
}
