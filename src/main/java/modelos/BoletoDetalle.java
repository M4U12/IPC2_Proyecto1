package modelos;

import java.time.LocalDateTime;

public class BoletoDetalle {

    private int idBoleto;
    private int numeroAsiento;
    private double precioPagado;
    private String origen;
    private String destino;
    private LocalDateTime fechaHoraSalida;
    private Enums.EstadoViaje estadoViaje;

    public BoletoDetalle(int idBoleto, int numeroAsiento, double precioPagado, String origen, String destino, LocalDateTime fechaHoraSalida, Enums.EstadoViaje estadoViaje) {
        this.idBoleto = idBoleto;
        this.numeroAsiento = numeroAsiento;
        this.precioPagado = precioPagado;
        this.origen = origen;
        this.destino = destino;
        this.fechaHoraSalida = fechaHoraSalida;
        this.estadoViaje = estadoViaje;
    }

    public int getIdBoleto() {
        return idBoleto;
    }

    public int getNumeroAsiento() {
        return numeroAsiento;
    }

    public double getPrecioPagado() {
        return precioPagado;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public LocalDateTime getFechaHoraSalida() {
        return fechaHoraSalida;
    }

    public Enums.EstadoViaje getEstadoViaje() {
        return estadoViaje;
    }
}
