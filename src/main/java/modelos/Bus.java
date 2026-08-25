package modelos;

public class Bus {
    private int idBus;
    private int idSucursal;
    private Integer idChofer; 
    private String foto; 
    private String placa;
    private String marca;
    private String modelo;
    private int anioFabricacion;
    private int capacidad;
    private String estadoOperativo;
    private double kilometrajeActual;
    private boolean estado;

    public Bus() {
    }

    public Bus(int idBus, int idSucursal, Integer idChofer, String foto, String placa, String marca, String modelo, int anioFabricacion, int capacidad, String estadoOperativo, double kilometrajeActual, boolean estado) {
        this.idBus = idBus;
        this.idSucursal = idSucursal;
        this.idChofer = idChofer;
        this.foto = foto;
        this.placa = placa;
        this.marca = marca;
        this.modelo = modelo;
        this.anioFabricacion = anioFabricacion;
        this.capacidad = capacidad;
        this.estadoOperativo = estadoOperativo;
        this.kilometrajeActual = kilometrajeActual;
        this.estado = estado;
    }

    public int getIdBus() {
        return idBus;
    }

    public int getIdSucursal() {
        return idSucursal;
    }

    public Integer getIdChofer() {
        return idChofer;
    }

    public String getFoto() {
        return foto;
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

    public int getAnioFabricacion() {
        return anioFabricacion;
    }

    public int getCapacidad() {
        return capacidad;
    }

    public String getEstadoOperativo() {
        return estadoOperativo;
    }

    public double getKilometrajeActual() {
        return kilometrajeActual;
    }

    public boolean isEstado() {
        return estado;
    }

    public void setIdBus(int idBus) {
        this.idBus = idBus;
    }

    public void setIdSucursal(int idSucursal) {
        this.idSucursal = idSucursal;
    }

    public void setIdChofer(Integer idChofer) {
        this.idChofer = idChofer;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setMarca(String marca) {
        this.marca = marca;
    }

    public void setModelo(String modelo) {
        this.modelo = modelo;
    }

    public void setAnioFabricacion(int anioFabricacion) {
        this.anioFabricacion = anioFabricacion;
    }

    public void setCapacidad(int capacidad) {
        this.capacidad = capacidad;
    }

    public void setEstadoOperativo(String estadoOperativo) {
        this.estadoOperativo = estadoOperativo;
    }

    public void setKilometrajeActual(double kilometrajeActual) {
        this.kilometrajeActual = kilometrajeActual;
    }

    public void setEstado(boolean estado) {
        this.estado = estado;
    } 
}
