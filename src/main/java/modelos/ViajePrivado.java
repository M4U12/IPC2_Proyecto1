package modelos;

import java.time.LocalDateTime;

public class ViajePrivado {

    private int idViajePrivado;
    private int idCliente;
    private int idSucursal;
    private String origen;
    private String destino;
    private int cantidadPasajeros;
    private Double precio;
    private Enums.EstadoViaje estado;

    private Integer idBus;
    private Integer idChofer;

    private LocalDateTime fechaHoraSalidaEstimada;
    private LocalDateTime fechaHoraLlegadaEstimada;
    private LocalDateTime fechaHoraSalidaReal;
    private LocalDateTime fechaHoraLlegadaReal;

    private Double kilometrajeSalida;
    private Double kilometrajeLlegada;
    private Double gastoCombustible;

    //auxiliar para la vista del administrador
    private String nombreCliente;

    public ViajePrivado() {
    }

    public int getIdViajePrivado() {
        return idViajePrivado;
    }

    public int getIdCliente() {
        return idCliente;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public String getOrigen() {
        return origen;
    }

    public String getDestino() {
        return destino;
    }

    public int getCantidadPasajeros() {
        return cantidadPasajeros;
    }

    public Double getPrecio() {
        return precio;
    }

    public Enums.EstadoViaje getEstado() {
        return estado;
    }

    public Integer getIdBus() {
        return idBus;
    }

    public Integer getIdChofer() {
        return idChofer;
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

    public String getNombreCliente() {
        return nombreCliente;
    }

    public void setIdViajePrivado(int idViajePrivado) {
        this.idViajePrivado = idViajePrivado;
    }

    public void setIdCliente(int idCliente) {
        this.idCliente = idCliente;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public void setOrigen(String origen) {
        this.origen = origen;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public void setCantidadPasajeros(int cantidadPasajeros) {
        this.cantidadPasajeros = cantidadPasajeros;
    }

    public void setPrecio(Double precio) {
        this.precio = precio;
    }

    public void setEstado(Enums.EstadoViaje estado) {
        this.estado = estado;
    }

    public void setIdBus(Integer idBus) {
        this.idBus = idBus;
    }

    public void setIdChofer(Integer idChofer) {
        this.idChofer = idChofer;
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

    public void setNombreCliente(String nombreCliente) {
        this.nombreCliente = nombreCliente;
    }
}
