package modelos;

import java.time.LocalDateTime;

public class Transaccion {
    private int idTransaccion;
    private int idCartera;
    private double monto;
    private String tipo;
    private LocalDateTime fechaHora;
    private String descripcion;

    public Transaccion() {
    }

    public Transaccion(int idTransaccion, int idCartera, double monto, String tipo, LocalDateTime fechaHora, String descripcion) {
        this.idTransaccion = idTransaccion;
        this.idCartera = idCartera;
        this.monto = monto;
        this.tipo = tipo;
        this.fechaHora = fechaHora;
        this.descripcion = descripcion;
    }

    public int getIdTransaccion() {
        return idTransaccion;
    }

    public int getIdCartera() {
        return idCartera;
    }

    public double getMonto() {
        return monto;
    }

    public String getTipo() {
        return tipo;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setIdTransaccion(int idTransaccion) {
        this.idTransaccion = idTransaccion;
    }

    public void setIdCartera(int idCartera) {
        this.idCartera = idCartera;
    }

    public void setMonto(double monto) {
        this.monto = monto;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
