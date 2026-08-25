package modelos;

public class Ruta {
    private int idRuta;
    private int idOrigen;
    private int idDestino;
    private double distanciaKm;
    private double precio;

    public Ruta() {
    }

    public Ruta(int idRuta, int idOrigen, int idDestino, double distanciaKm, double precio) {
        this.idRuta = idRuta;
        this.idOrigen = idOrigen;
        this.idDestino = idDestino;
        this.distanciaKm = distanciaKm;
        this.precio = precio;
    }

    public int getIdRuta() {
        return idRuta;
    }

    public int getIdOrigen() {
        return idOrigen;
    }

    public int getIdDestino() {
        return idDestino;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public double getPrecio() {
        return precio;
    }

    public void setIdRuta(int idRuta) {
        this.idRuta = idRuta;
    }

    public void setIdOrigen(int idOrigen) {
        this.idOrigen = idOrigen;
    }

    public void setIdDestino(int idDestino) {
        this.idDestino = idDestino;
    }

    public void setDistanciaKm(double distanciaKm) {
        this.distanciaKm = distanciaKm;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }
}
