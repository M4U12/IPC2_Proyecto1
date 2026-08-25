package modelos;

import java.time.LocalDateTime;

public class Boleto {
    private int idBoleto;
    private int idUsuario;
    private int idViaje;
    private int numeroAsiento;
    private double precioPagado;
    private LocalDateTime fechaPago;

    public Boleto() {
    }

    public Boleto(int idBoleto, int idUsuario, int idViaje, int numeroAsiento, double precioPagado, LocalDateTime fechaPago) {
        this.idBoleto = idBoleto;
        this.idUsuario = idUsuario;
        this.idViaje = idViaje;
        this.numeroAsiento = numeroAsiento;
        this.precioPagado = precioPagado;
        this.fechaPago = fechaPago;
    }

    public int getIdBoleto() {
        return idBoleto;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public int getIdViaje() {
        return idViaje;
    }

    public int getNumeroAsiento() {
        return numeroAsiento;
    }

    public double getPrecioPagado() {
        return precioPagado;
    }

    public LocalDateTime getFechaPago() {
        return fechaPago;
    }

    public void setIdBoleto(int idBoleto) {
        this.idBoleto = idBoleto;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setIdViaje(int idViaje) {
        this.idViaje = idViaje;
    }

    public void setNumeroAsiento(int numeroAsiento) {
        this.numeroAsiento = numeroAsiento;
    }

    public void setPrecioPagado(double precioPagado) {
        this.precioPagado = precioPagado;
    }

    public void setFechaPago(LocalDateTime fechaPago) {
        this.fechaPago = fechaPago;
    }
}
