package modelos;

import java.time.LocalDateTime;

public class Viaje {
    private int idViaje;
    private String tipoViaje;
    private String estadoViaje;
    private int idBus;
    private int idChofer;
    private Integer idRuta; 
    private Integer idCliente; 
    
    private String origenPrivado;
    private String destinoPrivado;
    private Integer cantidadPasajerosPrivado;
    private Double precioTotalPrivado;
    private LocalDateTime fechaRetornoPrivado;
    
    private LocalDateTime fechaHoraSalidaEstimada;
    private LocalDateTime fechaHoraLlegadaEstimada;
    private LocalDateTime fechaHoraSalidaReal;
    private LocalDateTime fechaHoraLlegadaReal;
    
    private Double kilometrajeSalida;
    private Double kilometrajeLlegada;
    private Double gastoCombustible;

    public Viaje() {
    }

    public Viaje(int idViaje, String tipoViaje, String estadoViaje, int idBus, int idChofer, Integer idRuta, Integer idCliente, String origenPrivado, String destinoPrivado, Integer cantidadPasajerosPrivado, Double precioTotalPrivado, LocalDateTime fechaRetornoPrivado, LocalDateTime fechaHoraSalidaEstimada, LocalDateTime fechaHoraLlegadaEstimada, LocalDateTime fechaHoraSalidaReal, LocalDateTime fechaHoraLlegadaReal, Double kilometrajeSalida, Double kilometrajeLlegada, Double gastoCombustible) {
        this.idViaje = idViaje;
        this.tipoViaje = tipoViaje;
        this.estadoViaje = estadoViaje;
        this.idBus = idBus;
        this.idChofer = idChofer;
        this.idRuta = idRuta;
        this.idCliente = idCliente;
        this.origenPrivado = origenPrivado;
        this.destinoPrivado = destinoPrivado;
        this.cantidadPasajerosPrivado = cantidadPasajerosPrivado;
        this.precioTotalPrivado = precioTotalPrivado;
        this.fechaRetornoPrivado = fechaRetornoPrivado;
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

    public String getTipoViaje() {
        return tipoViaje;
    }

    public String getEstadoViaje() {
        return estadoViaje;
    }

    public int getIdBus() {
        return idBus;
    }

    public int getIdChofer() {
        return idChofer;
    }

    public Integer getIdRuta() {
        return idRuta;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public String getOrigenPrivado() {
        return origenPrivado;
    }

    public String getDestinoPrivado() {
        return destinoPrivado;
    }

    public Integer getCantidadPasajerosPrivado() {
        return cantidadPasajerosPrivado;
    }

    public Double getPrecioTotalPrivado() {
        return precioTotalPrivado;
    }

    public LocalDateTime getFechaRetornoPrivado() {
        return fechaRetornoPrivado;
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

    public void setTipoViaje(String tipoViaje) {
        this.tipoViaje = tipoViaje;
    }

    public void setEstadoViaje(String estadoViaje) {
        this.estadoViaje = estadoViaje;
    }

    public void setIdBus(int idBus) {
        this.idBus = idBus;
    }

    public void setIdChofer(int idChofer) {
        this.idChofer = idChofer;
    }

    public void setIdRuta(Integer idRuta) {
        this.idRuta = idRuta;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public void setOrigenPrivado(String origenPrivado) {
        this.origenPrivado = origenPrivado;
    }

    public void setDestinoPrivado(String destinoPrivado) {
        this.destinoPrivado = destinoPrivado;
    }

    public void setCantidadPasajerosPrivado(Integer cantidadPasajerosPrivado) {
        this.cantidadPasajerosPrivado = cantidadPasajerosPrivado;
    }

    public void setPrecioTotalPrivado(Double precioTotalPrivado) {
        this.precioTotalPrivado = precioTotalPrivado;
    }

    public void setFechaRetornoPrivado(LocalDateTime fechaRetornoPrivado) {
        this.fechaRetornoPrivado = fechaRetornoPrivado;
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