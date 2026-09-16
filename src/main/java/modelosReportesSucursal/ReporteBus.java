package modelosReportesSucursal;

public class ReporteBus {
    private String placa;
    private String marca;
    private String modelo;
    private int capacidad;
    private String estadoOperativo;
    private String nombreChofer;
    private double kilometrajeActual;
    private int totalViajes;

    public ReporteBus() {}

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public void setEstadoOperativo(String estadoOperativo) {
        this.estadoOperativo = estadoOperativo;
    }

    public void setNombreChofer(String nombreChofer) {
        this.nombreChofer = nombreChofer;
    }

    public void setKilometrajeActual(double kilometrajeActual) {
        this.kilometrajeActual = kilometrajeActual;
    }

    public void setTotalViajes(int totalViajes) {
        this.totalViajes = totalViajes;
    }

    public String getPlaca() {
        return placa;
    }

    public String getMarca() {
        return marca;
    }

    public String getModelo() {
        return modelo;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public String getEstadoOperativo() {
        return estadoOperativo;
    }

    public String getNombreChofer() {
        return nombreChofer;
    }

    public double getKilometrajeActual() {
        return kilometrajeActual;
    }

    public int getTotalViajes() {
        return totalViajes;
    }

    
}