package modelos;

import java.time.LocalDateTime;

public class ViajeDisponibleDetalle {

    private int idViaje;
    private LocalDateTime fechaHoraSalidaEstimada;
    private String origen;
    private String destino;
    private double precio;

    public ViajeDisponibleDetalle(int idViaje, LocalDateTime fechaHoraSalidaEstimada, String origen, String destino, double precio) {
        this.idViaje = idViaje;
        this.fechaHoraSalidaEstimada = fechaHoraSalidaEstimada;
        this.origen = origen;
        this.destino = destino;
        this.precio = precio;
    }

    public int getIdViaje() {
        return idViaje;
    }

    public LocalDateTime getFechaHoraSalidaEstimada() {
        return fechaHoraSalidaEstimada;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public double getPrecio() {
        return precio;
    }
}
