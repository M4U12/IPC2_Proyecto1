package modelosReportesSucursal;

public class ReporteDepreciacion {
    private String placa;
    private double kilometrosRecorridos;
    private double depreciacionPorKm;
    private double depreciacionTotal;

    public ReporteDepreciacion() {}

    public String getPlaca() {
        return placa;
    }

    public double getKilometrosRecorridos() {
        return kilometrosRecorridos;
    }

    public double getDepreciacionPorKm() {
        return depreciacionPorKm;
    }

    public double getDepreciacionTotal() {
        return depreciacionTotal;
    }

    public void setPlaca(String placa) {
        this.placa = placa;
    }

    public void setKilometrosRecorridos(double kilometrosRecorridos) {
        this.kilometrosRecorridos = kilometrosRecorridos;
    }

    public void setDepreciacionPorKm(double depreciacionPorKm) {
        this.depreciacionPorKm = depreciacionPorKm;
    }

    public void setDepreciacionTotal(double depreciacionTotal) {
        this.depreciacionTotal = depreciacionTotal;
    }

    
}