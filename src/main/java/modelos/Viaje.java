package modelos;

import java.time.LocalDateTime;

public class Viaje {

    private int idViaje;
    private Enums.EstadoViaje estadoViaje;
    private int idBus;
    private int idChofer;
    private int idRuta;
    private LocalDateTime fechaHoraSalidaEstimada;
    private LocalDateTime fechaHoraLlegadaEstimada;
    private LocalDateTime fechaHoraSalidaReal;
    private LocalDateTime fechaHoraLlegadaReal;
    private Double kilometrajeSalida;
    private Double kilometrajeLlegada;
    private Double gastoCombustible;

    public Viaje() {
    }

    public Viaje(int idViaje, Enums.EstadoViaje estadoViaje, int idBus, int idChofer, int idRuta, LocalDateTime fechaHoraSalidaEstimada, LocalDateTime fechaHoraLlegadaEstimada, LocalDateTime fechaHoraSalidaReal, LocalDateTime fechaHoraLlegadaReal, Double kilometrajeSalida, Double kilometrajeLlegada, Double gastoCombustible) {
        this.idViaje = idViaje;
        this.estadoViaje = estadoViaje;
        this.idBus = idBus;
        this.idChofer = idChofer;
        this.idRuta = idRuta;
        this.fechaHoraSalidaEstimada = fechaHoraSalidaEstimada;
        this.fechaHoraLlegadaEstimada = fechaHoraLlegadaEstimada;
        this.fechaHoraSalidaReal = fechaHoraSalidaReal;
        this.fechaHoraLlegadaReal = fechaHoraLlegadaReal;
        this.kilometrajeSalida = kilometrajeSalida;
        this.kilometrajeLlegada = kilometrajeLlegada;
        this.gastoCombustible = gastoCombustible;
    }

    public int getIdViaje() {
        return idViaje;
    }

    public Enums.EstadoViaje getEstadoViaje() {
        return estadoViaje;
    }

    public int getIdBus() {
        return idBus;
    }

    public int getIdChofer() {
        return idChofer;
    }

    public int getIdRuta() {
        return idRuta;
    }

    public LocalDateTime getFechaHoraSalidaEstimada() {
        return fechaHoraSalidaEstimada;
    }

    public LocalDateTime getFechaHoraLlegadaEstimada() {
        return fechaHoraLlegadaEstimada;
    }

    public LocalDateTime getFechaHoraSalidaReal() {
        return fechaHoraSalidaReal;
    }

    public LocalDateTime getFechaHoraLlegadaReal() {
        return fechaHoraLlegadaReal;
    }

    public Double getKilometrajeSalida() {
        return kilometrajeSalida;
    }

    public Double getKilometrajeLlegada() {
        return kilometrajeLlegada;
    }

    public Double getGastoCombustible() {
        return gastoCombustible;
    }

    public void setIdViaje(int idViaje) {
        this.idViaje = idViaje;
    }

    public void setEstadoViaje(Enums.EstadoViaje estadoViaje) {
        this.estadoViaje = estadoViaje;
    }

    public void setIdBus(int idBus) {
        this.idBus = idBus;
    }

    public void setIdChofer(int idChofer) {
        this.idChofer = idChofer;
    }

    public void setIdRuta(int idRuta) {
        this.idRuta = idRuta;
    }

    public void setFechaHoraSalidaEstimada(LocalDateTime fechaHoraSalidaEstimada) {
        this.fechaHoraSalidaEstimada = fechaHoraSalidaEstimada;
    }

    public void setFechaHoraLlegadaEstimada(LocalDateTime fechaHoraLlegadaEstimada) {
        this.fechaHoraLlegadaEstimada = fechaHoraLlegadaEstimada;
    }

    public void setFechaHoraSalidaReal(LocalDateTime fechaHoraSalidaReal) {
        this.fechaHoraSalidaReal = fechaHoraSalidaReal;
    }

    public void setFechaHoraLlegadaReal(LocalDateTime fechaHoraLlegadaReal) {
        this.fechaHoraLlegadaReal = fechaHoraLlegadaReal;
    }

    public void setKilometrajeSalida(Double kilometrajeSalida) {
        this.kilometrajeSalida = kilometrajeSalida;
    }

    public void setKilometrajeLlegada(Double kilometrajeLlegada) {
        this.kilometrajeLlegada = kilometrajeLlegada;
    }

    public void setGastoCombustible(Double gastoCombustible) {
        this.gastoCombustible = gastoCombustible;
    }
}
