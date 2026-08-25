package modelos;

public class Cartera {
    private int idCartera;
    private int idUsuario;
    private double cantidadDinero;

    public Cartera() {
    }

    public Cartera(int idCartera, int idUsuario, double cantidadDinero) {
        this.idCartera = idCartera;
        this.idUsuario = idUsuario;
        this.cantidadDinero = cantidadDinero;
    }

    public int getIdCartera() {
        return idCartera;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public double getCantidadDinero() {
        return cantidadDinero;
    }

    public void setIdCartera(int idCartera) {
        this.idCartera = idCartera;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public void setCantidadDinero(double cantidadDinero) {
        this.cantidadDinero = cantidadDinero;
    } 
}
